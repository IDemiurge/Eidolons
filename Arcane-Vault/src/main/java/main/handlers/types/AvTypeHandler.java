package main.handlers.types;

import main.content.OBJ_TYPE;
import main.data.SmartBackend;
import main.entity.type.ObjType;
import main.handlers.AvHandler;
import main.handlers.AvManager;

import java.util.LinkedHashSet;
import java.util.Set;

public class AvTypeHandler extends AvHandler {
    SmartBackend backend;

    public AvTypeHandler(AvManager manager) {
        super(manager);
    }

    public Set<ObjType> getSubTypes(ObjType type) {
        Set<ObjType> set = new LinkedHashSet<>();
        for (ObjType sub : backend.getTypes(type.getOBJ_TYPE_ENUM())) {
            if (backend.getParent(sub) == type) {
                set.add(sub);
            }
        }
        return set;
    }

    public Set<ObjType> getParentTypes(OBJ_TYPE TYPE) {
        Set<ObjType> set = new LinkedHashSet<>();
        for (ObjType sub : backend.getTypes(TYPE)) {
            if (backend.getParent(sub) == null ) {
                set.add(sub);
            }
        }
        return set;
    }
}
