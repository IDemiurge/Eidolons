package eidolons.ability.conditions;

import eidolons.entity.obj.unit.DC_UnitModel;
import main.content.enums.entity.UnitEnums.STATUS;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.obj.Obj;

public class StatusCheckCondition extends ConditionImpl {

    private STATUS status;
    private String obj_ref = Ref.KEYS.MATCH.name();

    public StatusCheckCondition(String ref, STATUS status) {
        this.status = status;
        this.obj_ref = ref;
    }

    public StatusCheckCondition(STATUS status) {
        this.status = status;
    }

    @Override
    public boolean check(Ref ref) {
        Obj obj = ref.getObj(obj_ref);
        if (obj instanceof DC_UnitModel) {
            return ((DC_UnitModel) obj).checkStatus(status);
        }
        return false;
    }
}
