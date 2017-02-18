package main.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.entity.obj.unit.Unit;
import main.rules.action.WatchRule;

public class WatchedCondition extends MicroCondition {

    @Override
    public boolean check() {
        return WatchRule.checkWatched((Unit) ref.getSourceObj(), (Unit) ref
                .getMatchObj());
    }

}
