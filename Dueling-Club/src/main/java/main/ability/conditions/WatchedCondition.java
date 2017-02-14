package main.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.entity.obj.unit.DC_HeroObj;
import main.rules.action.WatchRule;

public class WatchedCondition extends MicroCondition {

    @Override
    public boolean check() {
        return WatchRule.checkWatched((DC_HeroObj) ref.getSourceObj(), (DC_HeroObj) ref
                .getMatchObj());
    }

}
