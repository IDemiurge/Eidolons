package main.system;

import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 9/11/2017.
 */
public class ExceptionMaster {
    public static void printStackTrace(Exception e) {
        if (!CoreEngine.isExe())
            e.printStackTrace();
        else
            try {
                SpecialLogger.getInstance().appendExceptionToFileLog(
                 e.toString() + " from " + e.getStackTrace()[0]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        {
        }
    }
}
