package main.rules.combat;

import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.DirectionMaster;
import main.game.battlefield.attack.Attack;
import main.game.core.game.DC_Game;

public class CleaveRule {
    private static final int DEFAULT_CRITICAL_JUMPS = 2;
    private static final int DEFAULT_CRITICAL_DAMAGE_PERCENTAGE_TRANSFER = 75;
    private static final String DEFAULT_CRITICAL_DAMAGE_LOSS_PER_JUMP = "50 -{Strength}";
    Boolean clockwise;
    private Unit originalTarget;
    private DC_Obj currentTarget;
    private Unit source;
    private Integer jumpsRemaining;
    private DC_ActiveObj action;
    private Attack attack;
    private DC_Game game;

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
        this.attack = attack;

        attack.setTriggered(true);
        attack.setCanCounter(false);
        attack.setCounter(false);

        originalTarget = (Unit) ref.getObj(KEYS.TARGET);
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
        clockwise = true; // check facing! always try the longest arc first, if
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
                currentTarget = (Unit) objectByCoordinate;
            }

        }
    }

    private boolean doCleave() {

        // action.multiplyParamByPercent(PARAMS.DAMAGE_MOD, damageMod, false);
        // // ??
        initNextTarget();
        if (currentTarget == null) {
            return true;
        }
        attack.getRef().setTarget(currentTarget.getId());
        attack.setDamage(attack.getRemainingDamage());
        // TODO override atk logging!
        boolean result = source.getGame().getAttackMaster().attack(attack);
        // "dodged" or alive...
        if (result) {
            result = !currentTarget.isDead();
        }
        if (result) {
            return false;
        }
        return true;

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
