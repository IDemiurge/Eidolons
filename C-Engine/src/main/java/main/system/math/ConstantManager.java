package main.system.math;

import main.system.auxiliary.EnumMaster;

import java.util.Arrays;
import java.util.Collection;

public class ConstantManager {

    public static Object getConst(String string) {
        CONSTANTS c = new EnumMaster<CONSTANTS>()
                .retrieveEnumConst(CONSTANTS.class, string);
        if (c != null) {
            return c.getValue();
        }
        main.system.auxiliary.LogMaster.log(5, "CONST NOT FOUND! " + string);
        return null;
    }

    public static Collection<CONSTANTS> getConstList() {
        return Arrays.asList(CONSTANTS.values());
    }

    public enum CONSTANTS {
        DEFEND_CONST(30),
        DEFEND_INCREMENT(10),
        DEFEND_MAX_INCREMENT(4),;

        private Object value;

        CONSTANTS(Object value) {
            this.setValue(value);
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

}
