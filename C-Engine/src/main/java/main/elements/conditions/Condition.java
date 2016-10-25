package main.elements.conditions;

import main.entity.Entity;
import main.entity.Ref;
import main.entity.Referred;

import java.io.Serializable;

public interface Condition extends Referred, Serializable {

    boolean check(Ref ref);

    boolean check();

    String getTooltip();

    boolean check(Entity match);

    Condition join(Condition condition);

    boolean isTrue();

    enum CONDITION {
        MATCH_ALLY, MATCH_UNIT_TYPE, MATCH_RANGE,;

        private Condition c;

        public Condition getCondition() {
            return c;
        }

        public void setCondition(Condition c) {
            this.c = c;
        }
    }

}
