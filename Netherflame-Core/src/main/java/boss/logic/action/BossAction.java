package boss.logic.action;

import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.unit.Unit;
import main.entity.type.ObjType;

public class BossAction extends UnitAction {
    public BossAction(ObjType type, Unit unit) {
        super(
                type, unit
        );
    }

    @Override
    public boolean canBeTargeted(Integer id) {
        return true;
    }
}
