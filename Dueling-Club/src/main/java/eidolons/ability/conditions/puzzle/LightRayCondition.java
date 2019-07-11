package eidolons.ability.conditions.puzzle;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.BattleFieldObject;
import main.entity.Ref;

public class LightRayCondition extends DC_Condition {



    @Override
    public boolean check(Ref ref) {

        //only overlaying?

        BattleFieldObject lightEmitter = (BattleFieldObject) ref.getMatchObj();
        int rayLength;

        int distanceReq;

        lightEmitter.getDirection();

//        cell =

        return false;
    }
}
