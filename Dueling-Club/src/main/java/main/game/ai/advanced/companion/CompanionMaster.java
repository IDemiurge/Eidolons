package main.game.ai.advanced.companion;

import main.content.DC_ValueManager.VALUE_GROUP;
import main.content.PARAMS;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.entity.obj.unit.Unit;
import main.game.ai.UnitAI;

public class CompanionMaster {

    /*
         * give orders
         *  * create self-orders
         *
         *  'behavior' vs battle
         *
         *  factors of intelligence, aggression, loyalty...
         *  enemy preferences
         *
         */
    public static void initCompanionAiParams(UnitAI ai) {
        // prefs, ai type, ...

        ai.setType(getAiType(ai.getUnit()));
    }

    private static AI_TYPE getAiType(Unit hero) {
        AI_TYPE type = AI_TYPE.NORMAL;
        int maxWeight = 0;
        for (AI_TYPE v : AI_TYPE.values()) {
            int weight = getWeight(v, hero);
            if (weight > maxWeight) {
                maxWeight = weight;
                type = v;
            }
        }

        return type;
    }

    private static int getWeight(AI_TYPE v, Unit hero) {
        switch (v) {
            case BRUTE:
                return hero.getSumOfParams(PARAMS.ATTACK,
                        PARAMS.OFF_HAND_ATTACK, PARAMS.STRENGTH,
                        PARAMS.STRENGTH) / 2
                        - hero.getSumOfParams(PARAMS.STEALTH, PARAMS.INTELLIGENCE)
                        ;
            case SNEAK:
                return 2 * hero.getIntParam(PARAMS.STEALTH);
            case TANK:
                return hero.getSumOfParams(PARAMS.VITALITY, PARAMS.VITALITY,
                        PARAMS.WILLPOWER, PARAMS.ARMOR, PARAMS.STRENGTH) / 3
                        + hero.getIntParam(PARAMS.DEFENSE) / 4
                        ;
            case CASTER:
                return (hero.getSumOfParams(PARAMS.SPELLPOWER, PARAMS.WISDOM, PARAMS.INTELLIGENCE) + hero.getSumOfParams(VALUE_GROUP.MAGIC.getParams())) / 2;
            case CASTER_MELEE:
                return
                        getWeight(AI_TYPE.BRUTE, hero) / 4 + getWeight(AI_TYPE.TANK, hero) / 4 +
                                (hero.getSumOfParams(PARAMS.SPELLPOWER) + hero.getSumOfParams(VALUE_GROUP.COMBAT_MAGIC.getParams()) / 2);
            case CASTER_SUPPORT:
                return hero.getSumOfParams(PARAMS.SPELLPOWER, PARAMS.WISDOM, PARAMS.INTELLIGENCE) + hero.getSumOfParams(VALUE_GROUP.SUPPORT_MAGIC.getParams());
            case CASTER_SUMMONER:
                return hero.getSumOfParams(PARAMS.SPELLPOWER, PARAMS.WISDOM) + hero.getSumOfParams(VALUE_GROUP.SUMMON_MAGIC.getParams());
            case CASTER_OFFENSE:
                return hero.getSumOfParams(PARAMS.SPELLPOWER, PARAMS.SPELLPOWER, PARAMS.RESISTANCE_PENETRATION) + hero.getSumOfParams(VALUE_GROUP.OFFENSE_MAGIC.getParams());
            case ARCHER:
                if (hero.getRangedWeapon() == null) {
                    return 0;
                }
                return
                        (int)
                                (Math.sqrt(hero.getRangedWeapon().getIntParam(PARAMS.GOLD_COST)) * 2
                                        + hero.getIntParam(PARAMS.MARKSMANSHIP_MASTERY) * 5);

        }
        return 0;
    }

    public enum COMPANION_MODE {
        FOLLOW, SCOUT, GUARD, IDLE, ORDERS, AGGRO, STEALTH,
    }

}
