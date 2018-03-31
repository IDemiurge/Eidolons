package eidolons.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.WatchRule;

public class WatchedCondition extends MicroCondition {

    @Override
    public boolean check(Ref ref) {
        return WatchRule.checkWatched((Unit) ref.getSourceObj(), (Unit) ref
         .getMatchObj());
    }

}
