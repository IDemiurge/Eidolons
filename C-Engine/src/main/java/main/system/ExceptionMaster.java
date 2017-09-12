package main.system;

import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 9/11/2017.
 */
public class ExceptionMaster {
    public static void printStackTrace(Exception e) {
        if (!CoreEngine.isExe())
            e.printStackTrace();
    }
}
