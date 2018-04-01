package eidolons.game.battlecraft.rules.combat.attack.extra_attack;

import eidolons.ability.conditions.VisibilityCondition;
import eidolons.game.battlecraft.rules.action.WatchRule;
import eidolons.system.math.roll.RollMaster;
import eidolons.content.PROPS;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums;
import main.content.mode.STD_MODES;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref.KEYS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.battlecraft.rules.RuleMaster;
import eidolons.game.battlecraft.rules.RuleMaster.RULE;
import eidolons.game.battlecraft.rules.mechanics.InterruptRule;
import main.game.bf.Coordinates;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.entity.ConditionMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttackOfOpportunityRule {

    static Conditions conditions;
    static boolean sim;
    static List<DC_ActiveObj> attacks;

    public static boolean checkActionNotInterrupted(DC_ActiveObj active) {

        return checkActionNotInterrupted(active, false);
    }

    public static boolean checkActionNotInterrupted(DC_ActiveObj active, boolean simulation) {
        if (!RuleMaster.isRuleOn(RULE.ATTACK_OF_OPPORTUNITY)) {
            return false;
        }
        sim = simulation;
        if (simulation) {
            setAttacks(new ArrayList<>());
        }

        checkAttacks(active);

        if (sim) {
            return true;
        }

        if (!checkSource(active)) {
            return false;
        }

        boolean result = checkInterrupted(active);
        return result;
    }

    private static boolean checkOverride(DC_ActiveObj active) {
        // TODO opportunist // vigilant
        return false;
    }

    private static boolean checkInterrupted(DC_ActiveObj active) {
        InterruptRule.getEffect(); // TODO
        // for moves, will prevent/push
        // for spells, Interrupt
        return true;
    }

    /*
     * Default cases of Attack Opportunity are:
     * A unit will make use their Extra Attack Points to make a Free Attack.
     * If they have enough EAP or AP to make a normal AoO
     *
     *
     *
     */
    private static Set<Unit> getPotentialAttackers(DC_ActiveObj active) {
        Set<Unit> set = new HashSet<>();
        for (Unit unit : active.getGame().getUnits()) {
            if (!unit.getOwner().isHostileTo(active.getOwner())) {
                continue;
            }
            if (unit.canCounter())
            // if (!free) {
            {
                if (!WatchRule.checkWatched(unit, active.getOwnerObj())) {
                    continue;
                }
            }
            set.add(unit);
        }

        return set;
    }

    private static void checkAttacks(DC_ActiveObj active) {
        // Filter<DC_HeroObj> filter = new
        // Filter<DC_HeroObj>(active.getOwnerObj().getRef(),
        // getConditions(), C_OBJ_TYPE.UNITS_CHARS);
        // filter.getObjects();
        Set<Unit> units = getPotentialAttackers(active);
        if (units.size() <= 0) {
            return;
        }

        for (Unit unit : units) {

            try {
                checkAttack(unit, active);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            // if (unit.getMode().equals(STD_MODES.ALERT)) {
            // if (active.getActionGroup() != ACTION_TYPE_GROUPS.MOVE)
            // continue;
            // TODO On Approach Attack is *advanced*!
            //
            // Obj targetObj = active.getRef().getTargetObj();
            // if (active.getName().equals("Move"))
            // targetObj = active.getGame().getCellByCoordinate(
            // targetObj.getCoordinates()
            //
            // .getAdjacentCoordinate(
            // active.getOwnerObj().getFacing()
            // .getDirection()));
            // if (PositionMaster.getDistance(targetObj, unit) > 1)
            // continue;
            // } else
        }

    }

    public static Boolean checkAttack(Unit unit, DC_ActiveObj active) {
        return checkAttack(unit, active, false, false);
    }

    @Deprecated
    public static Boolean checkAttack(Unit unit, DC_ActiveObj active, boolean stealthAoO,
                                      boolean force) {
        // if (!getConditions().preCheck(unit)) return;
        boolean free = false;
        boolean result = false;

        if (!checkAction(active)) {
            if (!RuleMaster.isRuleTestOn(RULE.ATTACK_OF_OPPORTUNITY)) {
                return true;
            }
        }

        if (!RuleMaster.isRuleTestOn(RULE.ATTACK_OF_OPPORTUNITY)) {
            if (!stealthAoO) {
                if (!VisionManager.checkVisible(unit)) {
                    if (!unit.checkBool(GenericEnums.STD_BOOLS.STEALTHY_AOOS)) {
                        return null;
                    }
                }
            }
        }
        if (!force) {
            int distance = PositionMaster.getDistance(active.getOwnerObj().getCoordinates(), unit
             .getCoordinates());

            if (active.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MOVE) {
                distance = Math.max(distance, PositionMaster.getDistance(DC_MovementManager
                 .getMovementDestinationCoordinate(active), unit.getCoordinates()));
            }
            // TODO moving away from same-cell ?
            if (distance > getAoOMaxDistance(unit, active)) {
                return null;
            }
            if (FacingMaster.getSingleFacing(unit, active.getOwnerObj()) == UnitEnums.FACING_SINGLE.BEHIND) {
                return null; // vigilance?
            }
            if (active.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MOVE) {

                result = unit.checkPassive(UnitEnums.STANDARD_PASSIVES.VIGILANCE);
            }
        }
        // free = checkFreeAttackCase(active, unit); TODO

        if (unit.canCounter()) {
            free = true;
            result = true;
        } else if (unit.getMode().equals(STD_MODES.ALERT)) {
            free = false;
//            result = unit.checkActionCanBeActivated(DC_ActionManager.ATTACK_OF_OPPORTUNITY);
        }

        if (!result) {
            if (!RuleMaster.isRuleTestOn(RULE.ATTACK_OF_OPPORTUNITY)) {
                return null;
            }
        }

        if (sim) {
            getAttacks().add(getAoO(unit, free));
        } else {
            triggerAttack(unit, active, free);
        }
        return !active.getOwnerObj().isDead();

    }

    private static int getAoOMaxDistance(Unit unit, DC_ActiveObj active) {
        if (active.isMove()) {
            return 0;
        }
        return 1;
    }

    private static Condition getConditions() {
        if (conditions == null) {
            conditions = new Conditions(ConditionMaster.getAliveAndConsciousFilterCondition(),
             ConditionMaster.getUnit_CharTypeCondition(), ConditionMaster
             .getEnemyCondition(), new VisibilityCondition(KEYS.MATCH, KEYS.SOURCE,
             VisionEnums.UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT));
        }
        return conditions;
    }

    private static DC_ActiveObj getAoO(Unit unit, boolean free) {
        return null;
//   TODO      return unit.getAction(free ? DC_ActionManager.FREE_ATTACK_OF_OPPORTUNITY
//                : DC_ActionManager.ATTACK_OF_OPPORTUNITY);
    }

    public static List<DC_ActiveObj> getMoveAoOs(Coordinates prevCoordinates,
                                                 Coordinates coordinates, DC_ActiveObj action) {
        List<DC_ActiveObj> list = new ArrayList<>();
        // TODO
        // if (!getConditions().preCheck(unit)) return;

        return list;
    }

    public static Boolean collisionAoO(DC_ActiveObj active, Unit collidedUnit) {
        return checkAttack(collidedUnit, active, true, true);

    }

    public static void triggerAttack(Unit unit, DC_ActiveObj active, boolean free) {
        triggerAttack(unit, active.getOwnerObj(), free);
    }

    public static void triggerAttack(Unit attacker, Unit attacked, boolean free) {
        LogMaster.log(LogMaster.COMBAT_DEBUG, "Triggering "
         + ((free) ? "free " : "") + "Attack of Opportunity for " + attacker + " on "
         + attacked);

        // attacker.getGame().getActionManager().activateAttackOfOpportunity(attacked,
        // attacker, free);
    }

    public static boolean checkAction(DC_ActiveObj active) {
        if (!checkOverride(active)) {
            switch (active.getActionGroup()) {
                case MOVE:
                    return checkMove(active);
                case ADDITIONAL:
                case SPECIAL:
                    return checkSpecial(active);
                case SPELL:
                    return checkSpell(active);
                default:
                    break;
            }
        }
        return false;
    }

    private static boolean checkMove(DC_ActiveObj active) {
        // agile
        if (active.getOwnerObj().checkProperty(G_PROPS.STANDARD_PASSIVES,
         UnitEnums.STANDARD_PASSIVES.DEXTEROUS.getName())) {
            return false;
        }
        return !active.checkProperty(PROPS.STANDARD_ACTION_PASSIVES, ActionEnums.STANDARD_ACTION_PASSIVES.DEXTEROUS
         .toString());

    }

    private static boolean checkSpell(DC_ActiveObj active) {
        return !active.checkProperty(G_PROPS.SPELL_TAGS, SpellEnums.SPELL_TAGS.INSTANT.toString());
    }

    private static boolean checkSpecial(DC_ActiveObj active) {
        return active.checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.ATTACK_OF_OPPORTUNITY + "");
        // return true;
    }

    private static boolean checkSource(DC_ActiveObj active) {
        try {
            Unit source = active.getOwnerObj();
            // if (!source.canAct()) a good idea, but there is a bug in it I
            // guess
            if (source.isImmobilized()) {
                return false;
            }
            if (source.isDead()) {
                return false;
            }
            // if (!active.canBeActivated()) // TODO will not update; need
            // toBase()
            // to reset canBeActivated!
            // return false;
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static List<DC_ActiveObj> getAttacks(DC_ActiveObj active) {
        checkActionNotInterrupted(active, true);
        return attacks;
    }

    public static List<DC_ActiveObj> getAttacks() {
        return attacks;
    }

    public static void setAttacks(ArrayList<DC_ActiveObj> attacks) {
        AttackOfOpportunityRule.attacks = attacks;
    }

    public static Boolean checkAttacksOfOpportunityInterrupt(DC_ActiveObj action) {

        List<Unit> set = getPotentialAttackersOfOpportunity(action);
        for (Unit unit : set) {
            DC_ActiveObj attack = getAttackOfOpportunity(action, unit);
            if (attack == null) {
                continue;
            }
            Boolean before_after_none = RollMaster.makeReactionRoll(unit, action, attack);
            if (before_after_none == null) {
                action.addPendingAttackOpportunity(attack);
            } else if (before_after_none) {
                boolean result = triggerAttackOfOpportunityAttack(unit, action, attack);
                if (result) {
                    return true;
                }
            }

        }
        return false;
    }

    private static boolean triggerAttackOfOpportunityAttack(Unit unit, DC_ActiveObj action,
                                                            DC_ActiveObj attack) {
        // TODO Attack bonus
        if (!attack.tryOpportunityActivation(action)) {

        }
        return false; // interrupts when?
    }

    private static DC_ActiveObj getAttackOfOpportunity(DC_ActiveObj action, Unit unit) {
        // TODO AutoAttackRange?

        if (unit.getPreferredAttackOfOpportunity() != null) {
            if (canMakeAttackOfOpportunityAgainst(action, unit.getPreferredAttackOfOpportunity(),
             unit)) {
                return unit.getPreferredAttackOfOpportunity();
            }
        }

        Unit target = action.getOwnerObj();
        int distance = (PositionMaster.getDistance(target.getCoordinates(), unit.getCoordinates()));

        for (DC_ActiveObj attack : getAttacksOfOpportunity(unit)) {

            if (distance > getMaxAttackRange(attack)) {
                continue;
            }
            if (!canMakeAttackOfOpportunityAgainst(action, attack, unit)) {
                continue;
            }
            if (FacingMaster.getSingleFacing(unit, target) == UnitEnums.FACING_SINGLE.TO_THE_SIDE) {
                if (!attack.checkPassive(UnitEnums.STANDARD_PASSIVES.BROAD_REACH)) {
                    continue;
                }
            }
            if (FacingMaster.getSingleFacing(unit, target) == UnitEnums.FACING_SINGLE.BEHIND) {
                if (!attack.checkPassive(UnitEnums.STANDARD_PASSIVES.HIND_REACH)) {
                    continue;
                }
            }
            if (attack.canBeTargeted(target.getId())) {
                if (attack.canBeActivatedAsAttackOfOpportunity(false, target))
                // TODO PICK OPTIMAL? Consider roll's cost modifier...
                {
                    return attack;
                }
            }
        }

        return null;
    }

    private static int getMaxAttackRange(DC_ActiveObj attack) {

        return 1; //TODO sometimes 0?
    }

    private static List<DC_ActiveObj> getAttacksOfOpportunity(Unit unit) {
        List<DC_ActiveObj> list = new ArrayList<>();
        DequeImpl<DC_UnitAction> attacks = unit.getActionMap()
         .get(ActionEnums.ACTION_TYPE.STANDARD_ATTACK);
        if (attacks == null) {
            attacks = new DequeImpl<>();
        }
        if (unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK) != null) {
            attacks.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK));
        }
        for (DC_UnitAction attack : attacks) {
            if (attack // TODO same tag?!
             .checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.ATTACK_OF_OPPORTUNITY_ACTION
              .toString())) {
                list.add(attack);
            }
        }

        return list;
    }

    public static boolean checkPendingAttackProceeds(Unit target, DC_ActiveObj attack) {
        // TODO
        if (target.isDead()) {
            return false;
        }
        if (attack.canBeTargeted(target.getId())) {
            if (attack.canBeActivatedAsAttackOfOpportunity(true, target)) {
                return true;
            }
        }
        return false;
    }

    private static List<Unit> getPotentialAttackersOfOpportunity(DC_ActiveObj action) {
        // TODO all? anyone could have *some* action to make against certain
        // cases...
        List<Unit> list = new ArrayList<>();
        for (Unit unit : action.getGame().getUnits()) {
            if (unit.isOwnedBy(action.getGame().getPlayer(!action.getOwner().isMe())))
            // if (!VisionManager.checkVisibileForUnit(unit,
            // action.getOwnerObj())) TODO
            {
                list.add(unit);
            }
        }
        return list;
    }

    private static boolean canMakeAttackOfOpportunityAgainst(DC_ActiveObj action,
                                                             DC_ActiveObj attack, Unit attacker) {
        // TODO
        if (!action.getOwnerObj().checkVisible()) {
            return attacker.checkPassive(UnitEnums.STANDARD_PASSIVES.BLIND_FIGHTER);
        }
        if (action instanceof DC_SpellObj) {
            DC_SpellObj spellObj = (DC_SpellObj) action;
            if (spellObj.isChanneling()) {
                return true;
            }
            if (attacker.checkPassive(UnitEnums.STANDARD_PASSIVES.OPPORTUNIST)) {
                if (!spellObj.isInstant()) {
                    return true;
                }
            }
        }
        if (action.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.TURN) {
            // if (distance==0)
            // if (singleFacing == behind)
        }
        if (action.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MOVE) {

        }
        if (action.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.ATTACK) {
            // interceptor
        }
        // if (action.getActionGroup() == ACTION_TYPE_GROUPS.MODE)
        if (attacker.checkPassive(UnitEnums.STANDARD_PASSIVES.OPPORTUNIST)) {
            if (action.checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.RESTORATION.toString())) {
                return true;
            }
        }
        return action.checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.ATTACK_OF_OPPORTUNITY
         .toString());
    }

}
