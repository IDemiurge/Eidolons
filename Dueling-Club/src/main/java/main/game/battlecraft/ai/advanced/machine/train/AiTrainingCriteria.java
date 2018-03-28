package main.game.battlecraft.ai.advanced.machine.train;

import main.game.battlecraft.ai.advanced.machine.train.AiTrainingParameters.STANDARD_TRAINING_CRITERIA;
import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager.COMBAT_STATS;
import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager.PLAYER_STATS;
import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager.STAT;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.MapMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiTrainingCriteria {

    List<Triple<CRITERIA_TYPE_BOOLEAN, Object, Float>> booleanCriteria = new ArrayList<>(); //type, arg, reward
    List<Pair<CRITERIA_TYPE_NUMERIC, Float>> numericCriteria = new ArrayList<>(); //type, factor

    //define with string syntax?


    public AiTrainingCriteria(String[] split) {
        String numericData = split[0];
        Map<CRITERIA_TYPE_NUMERIC, Integer> modMap = null;
        if (split.length > 2) {
            modMap = new HashMap<>();
            numericData = split[2];
            for (String sub : numericData.split(",")) {
                STANDARD_TRAINING_CRITERIA stdMods = new EnumMaster<STANDARD_TRAINING_CRITERIA>().retrieveEnumConst(STANDARD_TRAINING_CRITERIA.class, sub);
                Map<CRITERIA_TYPE_NUMERIC, Integer> finalModMap = modMap;
                Arrays.stream(stdMods.mods).forEach(mod -> {
                    for (CRITERIA_TYPE_NUMERIC c : mod.getMod().consts) {
                        MapMaster.addToIntegerMap(finalModMap, c, (int) mod.getFactor());
                    }
                });
            }
        }

        int i = 0;
        for (String sub : numericData.split(",")) {
            CRITERIA_TYPE_NUMERIC criteria = CRITERIA_TYPE_NUMERIC.values()[i]; //CRITERIA_TYPE_NUMERIC.valueOf(sub.split("=")[0]);
            i++;
            Float n = Float.valueOf(
             sub
//             sub.split("=")[1]
            );
            if (modMap != null) {
                Integer mod = modMap.get(criteria);
                if (mod != null)
                    n = n * mod / 100;
            }

            Pair<CRITERIA_TYPE_NUMERIC, Float> pair = new ImmutablePair(criteria, n);
            numericCriteria.add(pair);
        }
        if (split.length > 1) {
//TODO boolean
        }

    }

    public static STAT getStatForCriteria(CRITERIA_TYPE_NUMERIC key) {
        switch (key) {

            case ALLIES_DAMAGE_TAKEN:
            case ALLIES_DAMAGE_DEALT:
            case ALLIES_DIED_POWER:
            case ALLY_HEROES_DIED:
            case ALLY_ENEMIES_KILLED_POWER:
                return PLAYER_STATS.valueOf(key.name());


        }

        return COMBAT_STATS.valueOf(key.name());
    }

    public enum CRITERIA_TYPE_BOOLEAN {
        UNIT_ALIVE,
        UNIT_DEAD, SELF_ALIVE,

    }


    public enum CRITERIA_TYPE_NUMERIC {
        //from stats
        DAMAGE_DEALT_ALLIES(-0.04f),
        DAMAGE_DEALT_ENEMIES(0.05f),

        //        ENEMIES_KILLED(1f),
        ENEMIES_KILLED_POWER(1f),
        DAMAGE_TAKEN(-0.005f),
        FALLEN_UNCONSCIOUS(-5f),
        DIED(-25f),

        //        ALLIES_DIED(),
        ALLY_HEROES_DIED(-25f),
        ALLIES_DIED_POWER(-0.6f),
        ALLIES_DAMAGE_TAKEN(-0.02f),

        ALLIES_DAMAGE_DEALT(0.02f),
        //        ALLY_ENEMIES_KILLED,
        ALLY_ENEMIES_KILLED_POWER(0.7f),;
        float defaultValue;

        CRITERIA_TYPE_NUMERIC() {
        }

        CRITERIA_TYPE_NUMERIC(float defaultValue) {
            this.defaultValue = defaultValue;
        }

        public float getDefaultValue() {
            return defaultValue;
        }
    }

}
