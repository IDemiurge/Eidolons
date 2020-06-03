package eidolons.ability.conditions.puzzle;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.core.Eidolons;
import main.entity.Ref;
import main.entity.obj.Obj;

public class VoidCondition extends DC_Condition {
    @Override
    public boolean check(Ref ref) {
        Obj matchObj = ref.getMatchObj();
        if (matchObj == null) {
            matchObj= Eidolons.getMainHero();
        }
        if (matchObj instanceof DC_Cell) {
            return ((DC_Cell) matchObj).isVOID();
        }
        return getGame().getCellByCoordinate(matchObj.getCoordinates()).isVOID();
    }
}
