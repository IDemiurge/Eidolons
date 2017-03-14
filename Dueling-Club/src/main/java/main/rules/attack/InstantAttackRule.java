package main.rules.attack;

import main.content.PARAMS;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates;
import main.game.battlefield.DC_MovementManager;
import main.game.battlefield.FacingMaster;
import main.game.core.master.EffectMaster;
import main.game.logic.combat.attack.Attack;
import main.game.logic.combat.attack.DC_AttackMaster;
import main.rules.RuleMaster;
import main.rules.RuleMaster.RULE;
import main.rules.perk.AlertRule;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG;
import main.system.math.PositionMaster;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class InstantAttackRule {
    public static boolean checkInstantAttacksInterrupt(DC_ActiveObj action) {
        if (!RuleMaster.isRuleOn(RULE.INSTANT_ATTACK)) {
            return false;
        }
        Boolean retreat_passage_none = canMakeInstantAttackAgainst(action);
        if (RuleMaster.isRuleTestOn(RULE.INSTANT_ATTACK)) {
            retreat_passage_none = true;
        }
        if (!retreat_passage_none) {
            return false;
        }
        Set<Unit> set = getPotentialInstantAttackers(action);
        for (Unit unit : set) {
            // INSTANT_ATTACK_TYPE type = getInstantAttackType(unit, action);
            DC_ActiveObj attack = getInstantAttack(action, unit);
            if (attack == null) {
                continue;
            }
            boolean result = triggerInstantAttack(unit, action, attack);
            if (result) {

                return true;
            }

        }
        return false;
    }

    public static void checkCollisionAttack(Unit moveObj, DC_ActiveObj activeObj,
                                            Unit collideObj) {
        // TODO Auto-generated method stub

    }

    private static INSTANT_ATTACK_TYPE getInstantAttackType(Unit unit, DC_ActiveObj action) {
        Coordinates c = DC_MovementManager.getMovementDestinationCoordinate(action);
        Coordinates c1 = action.getOwnerObj().getCoordinates();

        if (c.equals(c1)) {
            return INSTANT_ATTACK_TYPE.ENGAGEMENT;
        }

        FACING_SINGLE singleFacing = FacingMaster.getSingleFacing(unit.getFacing(), c, c1);
        if (singleFacing == UnitEnums.FACING_SINGLE.BEHIND) {
            return INSTANT_ATTACK_TYPE.PASSAGE;
        }

        if (singleFacing == UnitEnums.FACING_SINGLE.IN_FRONT) {
            singleFacing = FacingMaster.getSingleFacing(action.getOwnerObj().getFacing(), c1, c);

            if (singleFacing == UnitEnums.FACING_SINGLE.BEHIND) {
                return INSTANT_ATTACK_TYPE.FLIGHT; // 'turned your back on the
            }
            // enemy'
            return INSTANT_ATTACK_TYPE.DISENGAGEMENT; // controlled retreat
            // backwards, facing
            // forward
        }
        return INSTANT_ATTACK_TYPE.STUMBLE;
    }

    public static Set<Unit> getPotentialInstantAttackers(DC_ActiveObj action) {
        Set<Unit> set = new HashSet<>();
        for (Unit unit : action.getGame().getUnits()) {
            if (!unit.isHostileTo(action.getOwner())) {
                continue;
            }
            if (unit.isNeutral()) {
                continue;
            }
            if (!unit.canCounter()) {
                continue;
            }
            set.add(unit);
        }
        return set;

    }

    public static boolean triggerInstantAttack(Unit unit, DC_ActiveObj action,
                                               DC_ActiveObj attack) {
        // if (WatchedCondition) //bonus ?

        if (!attack.tryInstantActivation(action)) {
            // try other attacks? could be preferred failed somehow
            LogMaster.log(1, "*** Instant attack failed! " + attack);
        } else {
            LogMaster.log(1, "*** Instant attack successful! " + attack);
        }
        INSTANT_ATTACK_TYPE type = getInstantAttackType(unit, action);
        EffectMaster.getAttackEffect(attack).getAttack().setInstantAttackType(type);
        if (type.isWakeUpAlert()) {
            AlertRule.wakeUp(unit);
        }

        return checkInterrupt(type, attack, action);
        // TODO when does it "interrupt"?
    }

    private static boolean checkInterrupt(INSTANT_ATTACK_TYPE type, DC_ActiveObj attack,
                                          DC_ActiveObj action) {
        int n = getInterruptChance(type, attack, action.getOwnerObj());
        if (RandomWizard.chance(n)) {
            String entry = action + " has been stopped by an Instant Attack " + "(Chance: "
                    + StringMaster.wrapInParenthesis(n + "%");
            action.getGame().getLogManager().log(LOG.GAME_INFO, entry, ENTRY_TYPE.MOVE);
            return true;
        }
        return false;
    }

    private static int getInterruptChance(INSTANT_ATTACK_TYPE type, DC_ActiveObj attackAction,
                                          Unit attacked) {
        Unit attacker = attackAction.getOwnerObj();
        Attack a = DC_AttackMaster.getAttackFromAction(attackAction);
        int damage = a.getDamageDealt();
        if (damage == 0) {
            return 0;
        }
        // attackAction.getIntParam(PARAMS.DAMAGE_LAST_DEALT);
        int chance = damage * 100 / attacked.getIntParam(PARAMS.TOUGHNESS);
        // TODO force vs weight?
        // if (InterruptRule.isDamageInterrupting(damage, attacked))

        PARAMS stopChanceMod = null;
        PARAMS passChanceMod = null;
        // ContentManager.getPARAM(valueName)
        switch (type) {
            case DISENGAGEMENT:
                stopChanceMod = PARAMS.STOP_DISENGAGEMENT_CHANCE_MOD;
                passChanceMod = PARAMS.PASS_DISENGAGEMENT_CHANCE_MOD;
                break;
            case ENGAGEMENT:
                stopChanceMod = PARAMS.STOP_ENGAGEMENT_CHANCE_MOD;
                passChanceMod = PARAMS.PASS_ENGAGEMENT_CHANCE_MOD;
                break;
            case FLIGHT:
                stopChanceMod = PARAMS.STOP_FLIGHT_CHANCE_MOD;
                passChanceMod = PARAMS.PASS_FLIGHT_CHANCE_MOD;
                break;
            case PASSAGE:
                stopChanceMod = PARAMS.STOP_PASSAGE_CHANCE_MOD;
                passChanceMod = PARAMS.PASS_PASSAGE_CHANCE_MOD;
                break;
            case STUMBLE:
                return chance; // also apply Interrupt effect on sta/foc?
        }
        // String tip = "Chance: " + StringMaster.wrapInParenthesis(value)
        int stop = chance * 100 * attacker.getIntParam(stopChanceMod);
        int pass = chance * 100 * attacked.getIntParam(passChanceMod);
        return chance - pass + stop;
    }

    public static DC_ActiveObj getInstantAttack(DC_ActiveObj action, Unit unit) {
        if (!canUnitMakeInstantAttack(unit)) {
            return null;
        }
        if (!canMakeInstantAttackAgainst(action)) {
            return null;
        }
        return chooseInstantAttack(action, unit);
    }

    public static Boolean canMakeInstantAttackAgainst(DC_ActiveObj action) {
        if (action.isMove()) {
            return true; // dexterous check ?
        }
        return false;
    }

    public static boolean canUnitMakeInstantAttack(Unit unit) {
        return unit.canCounter(); // checkAlertCounter - is that right?
    }

    // 3 seconds to choose then automatic! :)
    public static DC_ActiveObj chooseInstantAttack(DC_ActiveObj action, Unit unit) {
        int distance = (PositionMaster.getDistance(DC_MovementManager
                .getMovementDestinationCoordinate(action), unit.getCoordinates()));
        // TODO moving away from same-cell ?

        // how to check if jump trajectory intersects with IA range? for (c c :
        // getMovePathCells()){
        if (unit.getPreferredInstantAttack() != null) {
            return unit.getPreferredInstantAttack();
        }

        for (DC_ActiveObj attack : getInstantAttacks(unit)) {
            if (distance > getAutoAttackRange(attack)) {
                continue;
            }
            if (!unit.checkPassive(UnitEnums.STANDARD_PASSIVES.HIND_REACH)) {
                if (!attack.checkPassive(UnitEnums.STANDARD_PASSIVES.BROAD_REACH)) {
                    if (FacingMaster.getSingleFacing(unit, action.getOwnerObj()) == UnitEnums.FACING_SINGLE.TO_THE_SIDE) {
                        continue;
                    }
                }
            }
            if (!attack.checkPassive(UnitEnums.STANDARD_PASSIVES.HIND_REACH)) {
                if (FacingMaster.getSingleFacing(unit, action.getOwnerObj()) == UnitEnums.FACING_SINGLE.BEHIND) {
                    continue;
                }
            }
            // TODO PICK OPTIMAL? Consider roll's cost modifier...
            return attack;
        }

        return null;
    }

    // could be a spell?..
    public static List<DC_ActiveObj> getInstantAttacks(Unit unit) {
        List<DC_ActiveObj> list = new LinkedList<>();
        List<DC_UnitAction> attacks = new LinkedList<>(unit.getActionMap().get(
                ActionEnums.ACTION_TYPE.STANDARD_ATTACK));

        if (unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK) != null) {
            attacks.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK));
        }
        for (DC_UnitAction attack : attacks) {

            if (checkAttackCanBeInstant(attack))
            // if (attack
            // .checkProperty(G_PROPS.ACTION_TAGS,
            // ACTION_TAGS.INSTANT_ATTACK.toString()))
            {
                list.add(attack);
            }
        }
        // for (DC_SpellObj s : unit.getSpells()) { haha
        // if (s.isInstant())
        // if (s.isMelee())
        // list.add(s);
        // }

        return list;
    }

    private static boolean checkAttackCanBeInstant(DC_UnitAction attack) {
        if (attack.isAttack()) {
            return false;
        }
        return attack.isMelee();
    }

    public static int getAutoAttackRange(DC_ActiveObj attack) {
        return attack.getIntParam(PARAMS.AUTO_ATTACK_RANGE);
    }

    public enum INSTANT_ATTACK_TYPE {
        PASSAGE, ENGAGEMENT {
            @Override
            public boolean isWakeUpAlert() {
                return true;
            }
        },
        DISENGAGEMENT, FLIGHT, STUMBLE("{1} has all but stumbled upon {2}!");

        INSTANT_ATTACK_TYPE() {

        }

        INSTANT_ATTACK_TYPE(String tip) {

        }

        public boolean isWakeUpAlert() {
            return false;
        }
    }
}
