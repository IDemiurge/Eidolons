package eidolons.game.battlecraft.ai.tools.priority;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.PlayerAI.SITUATION;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import eidolons.game.core.atb.AtbController;
import main.content.ContentValsManager;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.values.parameters.PARAMETER;
import main.system.math.MathMaster;

public class ParamPriorityAnalyzer extends AiHandler {


    public ParamPriorityAnalyzer(AiMaster master) {
        super(master);
    }

    public static float getParamNumericPriority(PARAMS param) {
        switch (param) {
            case FORTITUDE:
                return 100;
            case SPIRIT:
                return 100;
            case C_N_OF_ACTIONS:
                return 50;
            case C_N_OF_COUNTERS:
                return 120;
            case N_OF_ACTIONS:
                return 150;
            case N_OF_COUNTERS:
                return 150;

            case DETECTION:
                return 1.5f;
            case C_ENDURANCE:
                return 2;
            case ENDURANCE:
                return 3;
            case C_TOUGHNESS:
                return 3;
            case TOUGHNESS:
                return 5;
            case C_STAMINA:
                return 8;
            case C_FOCUS:
                return 6;
            case C_MORALE:
                return 3;
            case C_ESSENCE:
                return 4;
            case CONCEALMENT:
                return 3;
            case C_INITIATIVE:
                return 2f * AtbController.TIME_TO_READY/ AtbController.TIME_LOGIC_MODIFIER;
            case C_INITIATIVE_BONUS:
                return 2;
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
            case DEFENSE_MOD:
                return 2;
            case ATTACK_MOD:
                return 2;
            case DAMAGE_MOD:
                return 2.5f;
            case STAMINA:
                return 12;
            case FOCUS:
                return 9;
            case MORALE:
                return 5;
            case ESSENCE:
                return 6;

            case STRENGTH:
            case VITALITY:
            case AGILITY:
            case DEXTERITY:
            case WILLPOWER:
            case INTELLIGENCE:
            case SPELLPOWER:
            case KNOWLEDGE:
            case WISDOM:
            case CHARISMA:
            case BASE_STRENGTH:
            case BASE_VITALITY:
            case BASE_AGILITY:
            case BASE_DEXTERITY:
            case BASE_WILLPOWER:
            case BASE_INTELLIGENCE:
            case BASE_SPELLPOWER:
            case BASE_KNOWLEDGE:
            case BASE_WISDOM:
            case BASE_CHARISMA:
                return 10;
        }
        return 0;
    }

    private static int getParamPercentPriority(PARAMS param) {
        if (param.isAttribute()) {
            return 50;
        }
        // depending on AI_TYPE
        if (param.isDynamic()) {
            param = (PARAMS) ContentValsManager.getCurrentParam(param);
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
