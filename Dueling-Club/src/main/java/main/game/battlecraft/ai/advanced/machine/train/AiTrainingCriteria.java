package main.game.battlecraft.ai.advanced.machine.train;

import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager.COMBAT_STATS;
import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager.STAT;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiTrainingCriteria {

    List<Triple<CRITERIA_TYPE_BOOLEAN, Object, Float>> booleanCriteria; //type, arg, reward
    List<Pair<CRITERIA_TYPE_NUMERIC, Float>> numericCriteria; //type, factor

    //define with string syntax?

    public enum CRITERIA_TYPE_NUMERIC {
        //from stats
        DAMAGE_DEALT,
        DAMAGE_TAKEN,
        ALLIES_DIED,

    }
        public enum CRITERIA_TYPE_BOOLEAN {
        UNIT_ALIVE,
        UNIT_DEAD,SELF_ALIVE,

    }


    public static STAT getStatForCriteria(CRITERIA_TYPE_NUMERIC key) {
        switch (key) {
            case DAMAGE_DEALT:
                return COMBAT_STATS.DAMAGE_DEALT;
            case DAMAGE_TAKEN:
                return COMBAT_STATS.DAMAGE_DEALT;
            case ALLIES_DIED:
                return COMBAT_STATS.DAMAGE_DEALT;


        }
    return null ;
    }

}
