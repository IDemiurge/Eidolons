package main.ability.conditions.shortcut;

import main.content.PARAMS;
import main.elements.conditions.DistanceCondition;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class RangeCondition extends MicroCondition {

    public RangeCondition() {

    }

    // public RangeCondition(String obj1, String obj2){
    //
    // }
    @Override
    public boolean check(Ref ref) {
        return new DistanceCondition(ref.getObj(KEYS.ACTIVE).getParam(
                PARAMS.RANGE)).preCheck(ref);
    }

}
