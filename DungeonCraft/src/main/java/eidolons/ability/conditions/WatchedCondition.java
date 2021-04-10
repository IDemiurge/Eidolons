package eidolons.ability.conditions;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.WatchRule;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class WatchedCondition extends MicroCondition {

    @Override
    public boolean check(Ref ref) {
        return WatchRule.checkWatched((Unit) ref.getSourceObj(), (Unit) ref
         .getMatchObj());
    }

}
