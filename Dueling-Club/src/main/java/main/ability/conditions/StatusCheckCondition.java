package main.ability.conditions;

import main.content.CONTENT_CONSTS.STATUS;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.obj.DC_UnitObj;
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
    public boolean check() {
        Obj obj = ref.getObj(obj_ref);
        if (obj instanceof DC_UnitObj) {
            return ((DC_UnitObj) obj).checkStatus(status);
        }
        return false;
    }
}
