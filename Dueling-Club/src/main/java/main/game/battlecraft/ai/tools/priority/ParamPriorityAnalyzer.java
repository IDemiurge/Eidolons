package main.game.battlecraft.ai.tools.priority;

import main.content.ContentManager;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.values.parameters.PARAMETER;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.PlayerAI.SITUATION;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;
import main.game.battlecraft.ai.tools.ParamAnalyzer;
import main.game.battlecraft.rules.UnitAnalyzer;
import main.system.math.MathMaster;

public class ParamPriorityAnalyzer extends AiHandler {


    public ParamPriorityAnalyzer(AiMaster master) {
        super(master);
    }


    private static int getParamPercentPriority(PARAMS param) {
        if (param.isAttribute()) {
            return 50;
        }
        // depending on AI_TYPE
        if (param.isDynamic()) {
            param = (PARAMS) ContentManager.getCurrentParam(param);
        }
        switch (param) {
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
        }
        return 0;
    }

    private static int getUnitParamRelevance(PARAMETER param, Unit unit) {
        if (ParamAnalyzer.isParamIgnored(unit, param)) {
            return 0;
        }

        return 100;

    }

    public static boolean isParamIgnored(PARAMETER param, DC_Obj target) {
        if (param instanceof PARAMS) {
            switch ((PARAMS) param) {
                case C_STAMINA:
                    if (target instanceof Unit) {
                        if (ParamAnalyzer.isStaminaIgnore((Unit) target)) {
                            return true;
                        }
                    }
                    return false;
                case C_FOCUS:
                    if (target instanceof Unit) {
                        if (ParamAnalyzer.isFocusIgnore((Unit) target)) {
                            return true;
                        }
                    }
                    return false;
                case C_MORALE:
                    if (target instanceof Unit) {
                        if (ParamAnalyzer.isMoraleIgnore((Unit) target)) {
                            return true;
                        }
                    }
                    return false;
                case C_ESSENCE:
                    if (target instanceof Unit) {
                        if (!UnitAnalyzer.checkIsCaster((Unit) target)) {
                            return true;
                        }
                    }
                    return false;

                case C_N_OF_ACTIONS:
                    if (target instanceof Unit) {
                        Unit heroObj = (Unit) target;
                        if (heroObj.isImmobilized()) {
                            return true;
                        }
                    }
                    return false;
                case C_N_OF_COUNTERS:
                    if (target instanceof Unit) {
                        Unit heroObj = (Unit) target;
                        if (!heroObj.canCounter()) {
                            return true;
                        }
                    }
                    return false;
                case SPIRIT:
                    if (target instanceof Unit) {
                        Unit heroObj = (Unit) target;
                        if (!heroObj.isLiving()) {
                            return true;
                        }
                    }
                    return false;
                case CONCEALMENT:
                    if (target instanceof Unit) {
                        // TODO ownership!
                        Unit heroObj = (Unit) target;
                        if (!heroObj.checkPassive(UnitEnums.STANDARD_PASSIVES.DARKVISION)) {
                            return !target.getOwner().isMe();
                        } else {
                            return target.getOwner().isMe();
                        }
                    }
                    return true;
                case C_ENDURANCE:
                case ENDURANCE:
                case C_TOUGHNESS:
                case TOUGHNESS:
                    if (target.checkPassive(UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE))
                        return true;
//                case C_INITIATIVE_BONUS:
//                    if (!((Unit) target).canActNow()) {
//                        return 0;
//                    }
//                    if (target.getGame().getTurnManager().getUnitQueue().size() <= 2) {
//                        return 0;
//                    }
//                    if (target.getOwner().equals(
//                            target.getGame().getTurnManager().getActiveUnit()
//                                    .getOwner())) {
//                        if (target.getGame().getTurnManager().getUnitQueue()
//                                .indexOf((Unit) target) > 1) {
//                            return 1;
//                        }
//                        return 0;
//                    } else if (target.getIntParam(PARAMS.C_INITIATIVE) > target
//                            .getGame().getRules().getTimeRule()
//                            .getTimeRemaining())
//                    // target.getGame().getTurnManager().getUnitQueue().
//                    // }
//                    {
//                        return 2;
//                    }


//                case ATTACK_MOD:
//                    return new Float(Math.sqrt(target
//                            .getIntParam(PARAMS.ATTACK))
//                            * target.getIntParam(PARAMS.DAMAGE)
//                            / 100
//                            + Math.sqrt(target
//                            .getIntParam(PARAMS.OFF_HAND_ATTACK))
//                            * target.getIntParam(PARAMS.OFF_HAND_DAMAGE) / 100)


            }
        }

        return false;

    }


    public static int getUnitLifeFactor(Unit unit) {
        int e = unit.getIntParam(PARAMS.ENDURANCE_PERCENTAGE)
         / MathMaster.MULTIPLIER;
        int t = unit.getIntParam(PARAMS.TOUGHNESS_PERCENTAGE)
         / MathMaster.MULTIPLIER;
        // undying
        return Math.min(e, t);

    }

    //TODO generate combinatorical AiConsts per GOAL_SITUATION !
    public static int getSituationFactor(GOAL_TYPE type, SITUATION situation) {
        if (situation == null) {
            return 0;
        }
        if (type == null) {
            return 0;
        }
        switch (situation) {
            case STALLING: {
                if (type == AiEnums.GOAL_TYPE.WAIT) {
                    return 30;
                }
                break;
            }
            case PREPARING: {
                if (type == AiEnums.GOAL_TYPE.PREPARE) {
                    return 20;
                }
                break;
            }
            case ENGAGED: {
                if (type == AiEnums.GOAL_TYPE.ATTACK) {
                    return 25;
                }
                if (type == AiEnums.GOAL_TYPE.DEBILITATE) {
                    return 15;
                }
                if (type == AiEnums.GOAL_TYPE.DEBUFF) {
                    return 15;
                }
                break;
            }
        }
        return 0;
    }

    public static int getAI_TypeFactor(GOAL_TYPE goal, AI_TYPE type) {
        if (type == null) {
            return 0;
        }
        switch (type) {
            case ARCHER:
                break;
            case BRUTE:
                if (goal == AiEnums.GOAL_TYPE.ATTACK) {
                    return 50;
                }
                break;
            case CASTER:
                if (goal == AiEnums.GOAL_TYPE.DEBUFF) {
                    return 50;
                }
                if (goal == AiEnums.GOAL_TYPE.BUFF) {
                    return 50;
                }
                break;
            case SNEAK:
                break;
            case TANK:
                if (goal == AiEnums.GOAL_TYPE.DEFEND) {
                    return 50;
                }
                break;

        }
        return 0;
    }

    public static int getResistanceFactor(Action action) {
        DC_ActiveObj active = action.getActive();
        DC_Obj target = action.getTarget();
        Integer resistance = target.getIntParam(PARAMS.RESISTANCE);
        resistance -= action.getSource().getIntParam(
         PARAMS.RESISTANCE_PENETRATION);
        Integer mod = active.getIntParam(PARAMS.RESISTANCE_MODIFIER);
        if (mod > 0) {
            resistance = resistance * mod / 100;
        }
        return Math.min(0, -resistance);
    }


}
