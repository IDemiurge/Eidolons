package main.system.auxiliary.secondary;

import main.system.launch.Flags;

/**
 * Created by JustMe on 8/7/2017.
 */
public class ExceptionHandler {
    public static void handle(Exception e) {
        if (Flags.isExceptionTraceLogged())
            main.system.ExceptionMaster.printStackTrace(e);

    }
}
