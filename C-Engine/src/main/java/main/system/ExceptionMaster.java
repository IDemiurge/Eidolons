package main.system;

import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.Flags;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by JustMe on 9/11/2017.
 */
public class ExceptionMaster {

    static Set<String> printed = new HashSet<>();

    public static void printStackTrace(Exception e) {
        {
            // CoreEngine.isLiteLaunch();
            boolean PRINT_ALL = false;
            if (!PRINT_ALL) {
                if (Flags.isJar() || LogMaster.isOff() || !Flags.isLiteLaunch()) {
                  if (e.getMessage()!=null )  if (printed.contains(e.getMessage()))
                        return;
                    else
                        printed.add(e.getMessage());
                }
            }
            e.printStackTrace();
            // CoreEngine.isLiteLaunch();
            boolean SKIP_WRITE = false;
            if (SKIP_WRITE) {
                return;
            }
            FileLogManager.logException(e);
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
