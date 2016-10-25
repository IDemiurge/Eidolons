package main.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.entity.obj.DC_HeroObj;
import main.rules.mechanics.WatchRule;

public class WatchedCondition extends MicroCondition {

    @Override
    public boolean check() {
        return WatchRule.checkWatched((DC_HeroObj) ref.getSourceObj(), (DC_HeroObj) ref
                .getMatchObj());
    }

}
