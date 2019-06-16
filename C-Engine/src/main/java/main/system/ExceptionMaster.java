package main.system;

import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 9/11/2017.
 */
public class ExceptionMaster {

    private static  boolean SKIP_WRITE = CoreEngine.isLiteLaunch();
    private static  boolean PRINT_ALL =false;// CoreEngine.isLiteLaunch();
    static Set<String> printed = new HashSet<>();

    public static void printStackTrace(Exception e) {
        {
            if (!PRINT_ALL) {
                if (CoreEngine.isJar() || LogMaster.isOff() || CoreEngine.isFastMode())
                    if (printed.contains(e.getMessage()))
                        return;
                    else
                        printed.add(e.getMessage());
            }
            e.printStackTrace();
            if (SKIP_WRITE) {
                return;
            }
            LogMaster.getExceptions().add(e);
            LogMaster.logException(e);
        }
        //        else
        //            try {
        //                SpecialLogger.getInstance().appendExceptionToFileLog(
        //                 e.toString() + " from " + e.getStackTrace()[0]);
        //            } catch (Exception ex) {
        //                ex.printStackTrace();
        //            }
        //        {
        //        }
    }
}
