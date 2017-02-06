package main.system.auxiliary;

import main.entity.obj.Obj;

public class Utilities {

    public static boolean compare(Obj e, Obj e2) {
        if (e == null && e2 == null) {
            return true;
        }
        if (e == null) {
            return false;
        }
        if (e2 == null) {
            return false;
        }
        return e.equals(e2);

    }

}
