package main.game.battlecraft.ai.advanced.machine;

/**
 * Created by JustMe on 7/31/2017.
 */
public enum AiConst {
    DEFAULT_RESTORATION_PRIORITY_MOD(40),
    DEFAULT_PRIORITY(100),
    DEFAULT_ATTACK_PRIORITY(100),
    WAIT_PRIORITY_FACTOR(10),

    COUNTER_FACTOR(4),
    CONVERGING_FACTOR(0.5f),
    SUMMON_PRIORITY_MOD(1000),
    RETREAT_PRIORITY_FACTOR(0.33f),

    ATTRIBUTE,
    STD_ACTION,

    //RULES AND CASES
    ITEM,
    THROW,
    EXTRA_ATTACK,

    //RPG
    COST_SQUARE(5),
    COST_DIVIDER(20),

    //technical
    DEFAULT_PRUNE_SIZE(5),

    /*
    case C_ENDURANCE:
                    return 2;
                case ENDURANCE:
                    return 3;
                case C_TOUGHNESS:
                    return 3;
                case TOUGHNESS:
                    return 5;
                case C_STAMINA:
                    if (target instanceof Unit) {
                        if (ParamAnalyzer.isStaminaIgnore((Unit) target)) {
                            return 0;
                        }
                    }
                    return 8;
                case C_FOCUS:
                    if (target instanceof Unit) {
                        if (ParamAnalyzer.isFocusIgnore((Unit) target)) {
                            return 0;
                        }
                    }
                    return 6;
                case C_MORALE:
                    if (target instanceof Unit) {
                        if (ParamAnalyzer.isMoraleIgnore((Unit) target)) {
                            return 0;
                        }
                    }
                    return 3;
                case C_ESSENCE:
                    if (target instanceof Unit) {
                        if (!UnitAnalyzer.checkIsCaster((Unit) target)) {
                            return 0;
                        }
                    }
                    return 4;

                case C_N_OF_ACTIONS:
                    if (target instanceof Unit) {
                        Unit heroObj = (Unit) target;
                        if (heroObj.isImmobilized()) {
                            return 10;
                        }
                    }
                    return 50;
                case C_N_OF_COUNTERS:
                    if (target instanceof Unit) {
                        Unit heroObj = (Unit) target;
                        if (!heroObj.canCounter()) {
                            return 0;
                        }
                    }
                    return 30;
                case SPIRIT:
                    if (target instanceof Unit) {
                        Unit heroObj = (Unit) target;
                        if (!heroObj.isLiving()) {
                            return 0;
                        }
                    }
                    return 30;
                case CONCEALMENT:
                    if (target instanceof Unit) {
                        // TODO ownership!
                        Unit heroObj = (Unit) target;
                        if (!heroObj.checkPassive(UnitEnums.STANDARD_PASSIVES.DARKVISION)) {
                            if (target.getOwner().isMe()) {
                                return 3;
                            } else {
                                return 0; // if sneak/tank/brute...
                            }
                        } else {
                            if (target.getOwner().isMe()) {
                                return 0;
                            } else {
                                return 2;
                            }
                        }
                    }
                    return 0;
                case C_INITIATIVE_BONUS:
                    if (!((Unit) target).canActNow()) {
                        return 0;
                    }
                    if (target.getGame().getTurnManager().getUnitQueue().size() <= 2) {
                        return 0;
                    }
                    if (target.getOwner().equals(
                            target.getGame().getTurnManager().getActiveUnit()
                                    .getOwner())) {
                        if (target.getGame().getTurnManager().getUnitQueue()
                                .indexOf((Unit) target) > 1) {
                            return 1;
                        }
                        return 0;
                    } else if (target.getIntParam(PARAMS.C_INITIATIVE) > target
                            .getGame().getRules().getTimeRule()
                            .getTimeRemaining())
                    // target.getGame().getTurnManager().getUnitQueue().
                    // }
                    {
                        return 2;
                    }
                case ARMOR:
                    return 5;
                case RESISTANCE:
                    return 4;
                case BASE_DAMAGE:
                    return 2.5f;
                case DAMAGE_BONUS:
                    return 2.5f;
                case DEFENSE:
                    return 3;
                case ATTACK:
                    return 3;

                case ATTACK_MOD:
                    return new Float(Math.sqrt(target
                            .getIntParam(PARAMS.ATTACK))
                            * target.getIntParam(PARAMS.DAMAGE)
                            / 100
                            + Math.sqrt(target
                            .getIntParam(PARAMS.OFF_HAND_ATTACK))
                            * target.getIntParam(PARAMS.OFF_HAND_DAMAGE) / 100)

                            ;
                case DEFENSE_MOD:
                    return target.getIntParam(PARAMS.DEFENSE) * 4 / 100;
                case DAMAGE_MOD:
                    return target.getIntParam(PARAMS.DAMAGE) * 3 / 100;

                case STAMINA:
                    if (target instanceof Unit) {
                        if (ParamAnalyzer.isStaminaIgnore((Unit) target)) {
                            return 0;
                        }
                    }
                    return 12;
                case FOCUS:
                    if (target instanceof Unit) {
                        if (ParamAnalyzer.isFocusIgnore((Unit) target)) {
                            return 0;
                        }
                    }
                    return 9;
                case MORALE:
                    if (target instanceof Unit) {
                        if (ParamAnalyzer.isMoraleIgnore((Unit) target)) {
                            return 0;
                        }
                    }
                    return 5;
                case ESSENCE:
                    if (target instanceof Unit) {
                        if (!UnitAnalyzer.checkIsCaster((Unit) target)) {
                            return 0;
                        }
                    }
                    return 6;

                case N_OF_ACTIONS:
                    if (target instanceof Unit) {
                        Unit heroObj = (Unit) target;
                        if (heroObj.isImmobilized()) {
                            return 10;
                        }
                    }
                    return 80;
                case N_OF_COUNTERS:
                    if (target instanceof Unit) {
                        Unit heroObj = (Unit) target;
                        if (!heroObj.canCounter()) {
                            return 0;
                        }
                    }
                    return 40;
 if (param.isAttribute()) {
        return 50;
    }
    case ATTACK:
     case DEFENSE:
     case ATTACK_MOD:
     case DEFENSE_MOD:
     return 75;
            case C_STAMINA:
     case C_FOCUS:
     case C_MORALE:
     return 25;
            case C_ESSENCE:
     return 25;

            case C_ENDURANCE:
     return 65;
            case C_TOUGHNESS:
     return 100;


      if (unit_ai.getBehaviorMode() == AiEnums.BEHAVIOR_MODE.BERSERK) {
            basePriority = 100;
        } else if (targetObj.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
            if (!Analyzer.isBlockingMovement(unit_ai.getUnit(), (Unit) targetObj)) {
                return 0;
            }
            basePriority = 20;
        }

         Integer healthMod = 100;
        if (less_or_more_for_health != null) {
            healthMod = getHealthFactor(targetObj, less_or_more_for_health);
        } else {
            if (target_unit.isUnconscious()) {
                return basePriority / 4; // [QUICK FIX] - more subtle?
            }
        }


         private int getRestorationPriorityMod(Unit unit) {
        int mod = getConstInt(AiConst.DEFAULT_RESTORATION_PRIORITY_MOD);
        if (unit.getAI().getType() == AI_TYPE.BRUTE) {
            mod -= 15;
        }
        if (unit.getAI().checkMod(AI_MODIFIERS.TRUE_BRUTE)) {
            mod -= 15;
        }
        if (unit.isMine()) {
            mod += 15;
        }
        return mod;
    }
     */
    ;

    private float defValue;

    AiConst() {
    }

    public float getDefValue() {
        return defValue;
    }

    AiConst(float f) {
        this.defValue = f;
    }
}
