package main.ability.conditions.shortcut;

import main.content.PARAMS;
import main.elements.conditions.DistanceCondition;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;

public class RangeCondition extends DistanceCondition {

    public RangeCondition() {
super (StringMaster.getValueRef(KEYS.ACTIVE, PARAMS.RANGE));
    }

    // public RangeCondition(String obj1, String obj2){
    //
    // }
//    @Override
//    public boolean check(Ref ref) {
//        return new DistanceCondition(ref.getObj(KEYS.ACTIVE).getParams(
//                PARAMS.RANGE)).preCheck(ref);
//    }

}
