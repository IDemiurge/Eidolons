package eidolons.system.text;

import static main.system.auxiliary.log.LogMaster.log;

public class DC_Logger {
    public static void logicError(Object... parts) {
        StringBuilder builder = new StringBuilder(">>> Logic Error: ");
        for (Object str : parts) {
                builder.append(str) ;
        }
        log(1, builder.toString());
    }
}
