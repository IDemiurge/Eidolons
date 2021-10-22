package boss.logic.action;

import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.unit.Unit;
import main.entity.type.ObjType;

public class BossAction extends DC_UnitAction {
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
