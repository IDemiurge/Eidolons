package main.game.battlecraft.ai.advanced.machine.train;

import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager.COMBAT_STATS;
import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager.PLAYER_STATS;
import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager.STAT;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiTrainingCriteria {

    List<Triple<CRITERIA_TYPE_BOOLEAN, Object, Float>> booleanCriteria = new LinkedList<>(); //type, arg, reward
    List<Pair<CRITERIA_TYPE_NUMERIC, Float>> numericCriteria = new LinkedList<>(); //type, factor

    //define with string syntax?


    public AiTrainingCriteria(String[] split) {
        String numericData = split[0];
        int i=0;
        for (String sub : numericData.split(",")) {
            CRITERIA_TYPE_NUMERIC criteria =CRITERIA_TYPE_NUMERIC.values()[i]; //CRITERIA_TYPE_NUMERIC.valueOf(sub.split("=")[0]);
            i++;
            Float n = Float.valueOf(
             sub
//             sub.split("=")[1]
            );
            Pair<CRITERIA_TYPE_NUMERIC, Float> pair = new ImmutablePair(criteria, n);
            numericCriteria.add(pair);
        }
        if (split.length > 1) {
//TODO
        }
    }

    public static STAT getStatForCriteria(CRITERIA_TYPE_NUMERIC key) {
        switch (key) {

            case ALLIES_DAMAGE_TAKEN:
            case ALLIES_DAMAGE_DEALT:
            case ALLIES_DIED_POWER:
            case ALLY_HEROES_DIED:
            case ALLY_ENEMIES_KILLED_POWER:
                return  PLAYER_STATS.valueOf(key.name());


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
        DAMAGE_TAKEN(0.01f),
        FALLEN_UNCONSCIOUS(-5f),
        DIED(-25f),

//        ALLIES_DIED(),
        ALLY_HEROES_DIED(-25f),
        ALLIES_DIED_POWER(-0.6f),
        ALLIES_DAMAGE_TAKEN(-0.02f),

        ALLIES_DAMAGE_DEALT(0.02f),
//        ALLY_ENEMIES_KILLED,
        ALLY_ENEMIES_KILLED_POWER(0.7f),
;
        float defaultValue;

        public float getDefaultValue() {
            return defaultValue;
        }

        CRITERIA_TYPE_NUMERIC() {
        }

        CRITERIA_TYPE_NUMERIC(float defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

}
