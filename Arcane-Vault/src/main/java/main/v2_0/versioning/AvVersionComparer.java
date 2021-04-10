package main.v2_0.versioning;

import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.entity.type.ObjType;

public class AvVersionComparer {
    /*
    read previous xml
     */

    public AvVersions.VALUE_STATUS getValueStatus(ObjType type, VALUE value){
        ObjType prev = getPrevious(type);
        if (value instanceof PARAMETER) {
            Double d = type.getParamDouble((PARAMETER) value);
            Double d2 = prev.getParamDouble((PARAMETER) value);
            if (d==d2) {
                return AvVersions.VALUE_STATUS.UNCHANGED;
            }
        } else {

        }
        return AvVersions.VALUE_STATUS.MOD_CHANGED;
    }

    private ObjType getPrevious(ObjType type) {
        return null;
    }
}
