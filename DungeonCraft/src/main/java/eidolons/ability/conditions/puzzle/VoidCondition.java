package eidolons.ability.conditions.puzzle;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.GridCell;
import eidolons.game.core.Core;
import main.content.CONTENT_CONSTS;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.system.launch.CoreEngine;

public class VoidCondition extends DC_Condition {
    @Override
    public boolean check(Ref ref) {
        Obj matchObj = ref.getMatchObj();
        if (matchObj == null) {
            matchObj= Core.getMainHero();
        }
        if (matchObj instanceof GridCell) {
            return ((GridCell) matchObj).isVOID();
        }

        GridCell cell = getGame().getCell(matchObj.getCoordinates());
        if (CoreEngine.TEST_LAUNCH && getGame().isDebugMode()) {
            if (!cell.getMarks().contains(CONTENT_CONSTS.MARK.togglable)) {
                return false;
            }
        }
        return cell. isVOID();
    }
}
