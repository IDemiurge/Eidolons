package eidolons.game.battlecraft.ai.advanced.companion;

import eidolons.content.values.DC_ValueManager.VALUE_GROUP;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.unit.Unit;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.system.auxiliary.EnumMaster;

public class CompanionMaster {

    static AI_TYPE[] aiTypes = AI_TYPE.values();

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
    public static void initCompanionAiParams(Unit u) {
        // prefs, ai type, ...

        u.getAI().setType(getAiType(u));
    }

    private static AI_TYPE getAiType(Unit hero) {
        AI_TYPE type = new EnumMaster<AI_TYPE>().retrieveEnumConst(AI_TYPE.class,
                hero.getProperty(PROPS.AI_TYPE));
        if (type != null) {
            main.system.auxiliary.log.LogMaster.log(1, hero.getName() + "'s Ai Type is pre-set: " + type);
            return type;
        }
        int maxWeight = 0;
        int total = 0;

        for (AI_TYPE v : aiTypes) {
            int weight = getWeight(v, hero);

            total += weight;
            main.system.auxiliary.log.LogMaster.log(1, v + "-Ai Type has " + weight + " weight for " + hero.getName());
            if (weight > maxWeight) {
                maxWeight = weight;
                type = v;
            }
        }
        //        int average = total / AI_TYPE.values().length;
        //        if (average > maxWeight/2)
        //            type = AI_TYPE.NORMAL;
        //        main.system.auxiliary.log.LogMaster.log(1, hero.getName() + "'s Ai Type chosen: " + type);
        return type;
    }

    private static int getWeight(AI_TYPE v, Unit hero) {
        switch (v) {
            case NORMAL:
                return hero.getPower();
            case BRUTE:
                return hero.getSumOfParams(PARAMS.ATTACK,
                        PARAMS.OFF_HAND_ATTACK, PARAMS.STRENGTH,
                        PARAMS.STRENGTH) / 2
                        - hero.getSumOfParams(PARAMS.STEALTH, PARAMS.INTELLIGENCE)
                        ;
            case SNEAK:
                return hero.getIntParam(PARAMS.STEALTH);
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


}
