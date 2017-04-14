package main.game.ai.tools.priority;

import main.content.ContentManager;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.STD_COUNTERS;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.AiEnums.PLAYER_AI_TYPE;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.PlayerAI.SITUATION;
import main.game.ai.elements.actions.Action;
import main.game.ai.elements.goal.Goal.GOAL_TYPE;
import main.game.ai.tools.ParamAnalyzer;
import main.rules.UnitAnalyzer;
import main.system.math.MathMaster;

public class ParamPriorityAnalyzer {


    public static int getParamPriority(PARAMETER param, Obj target) {
        int factor = 100;
        if (target instanceof Unit) {
            if (param == PARAMS.C_STAMINA || param == PARAMS.C_FOCUS
                    || param == PARAMS.C_MORALE || param == PARAMS.C_ESSENCE) {
                factor = getUnitParamRelevance(param, (Unit) target);
            }
        }
        return MathMaster.getFractionValueCentimal(
                getParamPercentPriority((PARAMS) param), factor);
    }

    private static int getParamPercentPriority(PARAMS param) {
        if (param.isAttribute()) {
            return 50;
        }
        // depending on AI_TYPE
        if (param.isDynamic())
            param = (PARAMS) ContentManager.getCurrentParam(param);
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

    public static float getParamNumericPriority(PARAMETER param, DC_Obj target) {
        // instead of adding the 'fraction of total' factor...
        if (param.isAttribute()) {
            return 15;
        }
//        if (param.isDynamic()) C_ is less important than total!
//            param = (PARAMS) ContentManager.getCurrentParam(param);
        if (target.checkPassive(UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE))
            if (param instanceof PARAMS)
                switch ((PARAMS) param) {
                    case C_ENDURANCE:
                    case ENDURANCE:
                    case C_TOUGHNESS:
                    case TOUGHNESS:
                        return 0;
                }


        if (param instanceof PARAMS) {
            switch ((PARAMS) param) {
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

            }
        }

        // TODO Auto-generated method stub

        return 0;

    }

    public static int getPriorityForCounters(STD_COUNTERS c) {
        return 0;

    }

    public static int getUnitLifeFactor(Unit unit) {
        int e = unit.getIntParam(PARAMS.ENDURANCE_PERCENTAGE)
                / MathMaster.MULTIPLIER;
        int t = unit.getIntParam(PARAMS.TOUGHNESS_PERCENTAGE)
                / MathMaster.MULTIPLIER;
        // undying
        return Math.min(e, t);

    }

    public static int getAI_TypeFactor(GOAL_TYPE goal, PLAYER_AI_TYPE type) {
        switch (type) {
            case BRUTE:
                if (goal == GOAL_TYPE.ATTACK) {
                    return 65;
                }
                break;
        }
        return 0;
    }

    public static int getSituationFactor(GOAL_TYPE type, SITUATION situation) {
        if (situation == null) {
            return 0;
        }
        if (type == null) {
            return 0;
        }
        switch (situation) {
            case STALLING: {
                if (type == GOAL_TYPE.WAIT) {
                    return 30;
                }
                break;
            }
            case PREPARING: {
                if (type == GOAL_TYPE.PREPARE) {
                    return 20;
                }
                break;
            }
            case ENGAGED: {
                if (type == GOAL_TYPE.ATTACK) {
                    return 25;
                }
                if (type == GOAL_TYPE.DEBILITATE) {
                    return 15;
                }
                if (type == GOAL_TYPE.DEBUFF) {
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
                if (goal == GOAL_TYPE.ATTACK) {
                    return 50;
                }
                break;
            case CASTER:
                if (goal == GOAL_TYPE.DEBUFF) {
                    return 50;
                }
                if (goal == GOAL_TYPE.BUFF) {
                    return 50;
                }
                break;
            case SNEAK:
                break;
            case TANK:
                if (goal == GOAL_TYPE.DEFEND) {
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

    public int getPercentagePriority(boolean negative, PARAMETER p, Entity unit) {
        return 0;
    }

}
