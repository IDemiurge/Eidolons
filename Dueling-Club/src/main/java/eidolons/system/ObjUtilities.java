package eidolons.system;

import main.entity.Entity;
import main.entity.obj.Obj;

import java.util.Collection;

public class ObjUtilities {

    public static Obj findObjByType(Entity type, Collection<? extends Obj> container) {
        for (Obj o : container) {
            if (o.getType() == type) {
                return o;
            }
        }
        return null;

    }

    public static boolean compare(Object o, Object o2) {
        if (o == null) {
            return o2 == null;
        }
        return o.equals(o2);
    }

}
