package eidolons.game.battlecraft.rules.combat.misc;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.core.game.DC_Game;
import libgdx.anims.text.FloatingTextMaster;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;

public class CleaveRule {
    private static final int DEFAULT_CRITICAL_JUMPS = 2;
    private static final int DEFAULT_CRITICAL_DAMAGE_PERCENTAGE_TRANSFER = 75;
    private static final String DEFAULT_CRITICAL_DAMAGE_LOSS_PER_JUMP = "50 -{Strength}";
    Boolean clockwise;
    private DC_Obj currentTarget;
    private Unit source;
    private Integer jumpsRemaining;
    private DC_ActiveObj action;
    private Attack attack;
    private final DC_Game game;

    // can I make it work as a ChainRule as well? Perhaps extend...
    // Slashing Criticals should add Cleave params and dmg_type upon crit!
    public CleaveRule(DC_Game game) {
        this.game = game;
    }

    public static void addCriticalCleave(Unit attacker) {
        attacker.modifyParameter(PARAMS.CLEAVE_MAX_TARGETS,
                DEFAULT_CRITICAL_JUMPS);
        attacker.modifyParameter(PARAMS.CLEAVE_DAMAGE_PERCENTAGE_TRANSFER,
                DEFAULT_CRITICAL_DAMAGE_PERCENTAGE_TRANSFER);
        attacker.setParam(PARAMS.CLEAVE_DAMAGE_LOSS_PER_JUMP,
                DEFAULT_CRITICAL_DAMAGE_LOSS_PER_JUMP);

    }

    public void apply(Ref ref, Attack attack) {
        if (!RuleKeeper.isRuleOn(RuleEnums.RULE.CLEAVE)) {
            return;
        }
        this.attack = attack;

        attack.setTriggered(true);
        attack.setCanCounter(false);
        attack.setCounter(false);

        BattleFieldObject originalTarget = (BattleFieldObject) ref.getObj(KEYS.TARGET);
        currentTarget = originalTarget;
        source = (Unit) ref.getObj(KEYS.SOURCE);
        jumpsRemaining = source.getIntParam(PARAMS.CLEAVE_MAX_TARGETS);

        if (!check(ref, attack)) {
            return;
        }
        int mod = source.getIntParam(PARAMS.CLEAVE_DAMAGE_PERCENTAGE_TRANSFER);

        attack.damageDealt(attack.getRemainingDamage() * (100 - mod) / 100);

        action = (DC_ActiveObj) ref.getObj(KEYS.ACTIVE);
        // clockwise = false; // for two-handed and normal main-hand it's only
        // // anticlockwise?
        // if (attack.isOffhand())
        // clockwise = true;
        // else if (action.checkProperty(G_PROPS.ACTION_TAGS,
        // ACTION_TAGS.DUAL.toString())) // would be cool to go *BOTH
        // // WAYS*!!! Yeah, dual cleave is
        // // a lot...
        // clockwise = null;

        while (jumpsRemaining > 0 && attack.getRemainingDamage() > 0) {
            jumpsRemaining--;
            boolean result = doCleave();
            mod = source.getIntParam(PARAMS.CLEAVE_DAMAGE_LOSS_PER_JUMP);
            attack.damageDealt(attack.getRemainingDamage() * (mod) / 100);
            if (!result) {
                break;
            }
        }
    }

    private void initNextTarget() {
        boolean first = false;
        if (clockwise == null) {
            // find targets TODO
            first = true;
        }
        clockwise = true; // preCheck facing! always try the longest arc first, if
        // it fails, then short one

        DIRECTION direction = DirectionMaster.getRelativeDirection(source,
                currentTarget);
        Obj objectByCoordinate = game.getObjectByCoordinate(
                source.getCoordinates().getAdjacentCoordinate(
                        DirectionMaster.rotate45(direction, clockwise)), true);
        if (objectByCoordinate instanceof Unit) {
            currentTarget = (Unit) objectByCoordinate;

        } else if (first) {
            clockwise = false;
            objectByCoordinate = game.getObjectByCoordinate(
                    source.getCoordinates().getAdjacentCoordinate(
                            DirectionMaster.rotate45(direction, clockwise)),
                    true);

            if (objectByCoordinate != null) {
                currentTarget = (BattleFieldObject) objectByCoordinate;
            }

        }
    }

    private boolean doCleave() {

        // action.multiplyParamByPercent(PARAMS.DAMAGE_MOD, damageMod, false);
        // // ??
        initNextTarget();
        if (currentTarget == null) {
            action.getGame().getLogManager().log(action +
                    " finds no targets to cleave for its remaining damage (" +
                    Math.abs(attack.getRemainingDamage()) + ")");
            return true;
        }
        attack.getRef().setTarget(currentTarget.getId());
        attack.setDamage(attack.getRemainingDamage());
        // TODO override atk logging!

        action.getGame().getLogManager().log(action + " cleaves, remaining damage: " + attack.getRemainingDamage());
        boolean result = source.getGame().getAttackMaster().attack(attack);
//       GuiEventManager.trigger(GuiEventType. ADD_FLOATING_TEXT)
        // "dodged" or alive...
        FloatingTextMaster.getInstance().createFloatingText(FloatingTextMaster.TEXT_CASES.ATTACK_CRITICAL, "Cleave!", currentTarget);
        if (result) {
            result = !currentTarget.isDead();
        }
        return !result;

    }

    private boolean check(Ref ref, Attack attack) {
        if (jumpsRemaining <= 0) {
            return false;
        }
        if (attack.isCounter()) {
            if (!source.checkPassive(UnitEnums.STANDARD_PASSIVES.CLEAVING_COUNTERS)) {
                return false;
            }
        }
        if (attack.getDamageType() == GenericEnums.DAMAGE_TYPE.SLASHING
                || attack.getDamageType() == GenericEnums.DAMAGE_TYPE.PHYSICAL) {
            return true;
        }
        if (attack.isCritical()) {
            if (source.checkPassive(UnitEnums.STANDARD_PASSIVES.CLEAVING_CRITICALS)) {
                attack.setDamageType(GenericEnums.DAMAGE_TYPE.SLASHING);
                return true;
            }
        }
        return false;
    }

}
