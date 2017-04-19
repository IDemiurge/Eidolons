package main.rules.counter;

import main.entity.obj.unit.Unit;

/**
 * Created by JustMe on 4/19/2017.
 */
public class CounterInteractionMaster {
    public void afterRoundEnds(Unit unit) {

    }

    public void afterActionDone(Unit unit) {

    }

    public enum COUNTER_INTERACTION_TYPE {
        MUTUAL_DELETION, DELETE_OTHER, DELETE_SELF,
        TRANSFORM_UP, TRANSFORM_DOWN,

    }
}
