package eidolons.game.battlecraft.ai.explore;

import main.elements.conditions.Condition;
import main.elements.conditions.DistanceCondition;

import java.util.List;

public class Constraint {
    CONSTRAINT_TYPE type;
    List<String> args;
    List<SPEC_ARG> specArgs;
    private Condition c;

    public Condition getCondition() {
        if (c == null) {
            c = initCondition();
        }
        return c;
    }

    private Condition initCondition() {
        switch (type) {
            case DISTANCE:
                return new DistanceCondition("");
            default:
                break;

        }
        return null;
    }

    public enum CONSTRAINT_TYPE {
        DISTANCE, STATUS, VISIBILITY,

    }

    public enum SPEC_ARG {
        LEADER,
    }
}
