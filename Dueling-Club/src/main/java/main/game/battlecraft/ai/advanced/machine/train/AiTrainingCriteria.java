package main.game.battlecraft.ai.advanced.machine.train;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiTrainingCriteria {

    List<Triple<CRITERIA_TYPE_BOOLEAN, Object, Float>> booleanCriteria; //type, arg, reward
    List<Pair<CRITERIA_TYPE_BOOLEAN, Float>> numericCriteria; //type, factor

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
}
