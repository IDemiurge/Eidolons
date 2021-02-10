package eidolons.ability.conditions;

import eidolons.entity.obj.BattleFieldObject;
import main.entity.Ref;

public class SneakingCondition extends DC_Condition {
    @Override
    public boolean check(Ref ref) {
        if (ref.getMatchObj() instanceof BattleFieldObject) {
            return ((BattleFieldObject) ref.getMatchObj()).isSneaking();
        }
        return false;
    }
}
