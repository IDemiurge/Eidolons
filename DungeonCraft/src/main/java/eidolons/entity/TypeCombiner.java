package eidolons.entity;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.values.ValuePages;
import eidolons.entity.obj.DC_Obj;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.type.ObjType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeCombiner {
    public static final List<VALUE> toModify = new ArrayList<>();
    public static final  List<VALUE> valuesToSet = new ArrayList<>();

    public static final VALUE[] VALUES_SET = {
            G_PROPS.IMAGE,
    };
    static{
        valuesToSet.addAll(Arrays.asList(VALUES_SET));
        for (VALUE[] unitPage : ValuePages.UNIT_PAGES) {
            toModify.addAll(Arrays.asList(unitPage));
        }
    }
    public static void applyType(DC_Obj obj, ObjType applied) {
        if (obj.getAppliedTypes().isEmpty()) {
            obj.setOriginalType(obj.getType());
            obj.setType(new ObjType(obj.getType()));
        }
        obj.getAppliedTypes().add(applied);
        applyType(obj.getType(), applied);
    }
        public static void applyType(ObjType base, ObjType applied) {

        for (VALUE item : toModify) {
            if (!DC_ContentValsManager.isValueForOBJ_TYPE(base.getOBJ_TYPE_ENUM(), item)) {
                continue;
            }
            if (item instanceof PARAMETER) {
                Integer n = applied.getIntParam((PARAMETER) item);
                base.modifyParameter((PARAMETER) (item), n, null, true);
            } else {
                base.addProperty(((PROPERTY) item), applied.getProperty((PROPERTY) item), true);
            }

        }
        for (VALUE item :  VALUES_SET) {
            if (!DC_ContentValsManager.isValueForOBJ_TYPE(base.getOBJ_TYPE_ENUM(), item)) {
                continue;
            }
            base.setValue(item, applied.getValue(item));
        }
    }
}
