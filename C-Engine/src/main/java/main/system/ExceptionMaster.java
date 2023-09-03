package main.system;

import main.system.auxiliary.ClassFinder;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 9/11/2017.
 */
public class ExceptionMaster {

    static Set<String> printed = new HashSet<>();
    private static boolean fileLoggingOn=true;

    private static boolean isExceptionTraceLogged(StackTraceElement[] stackTrace) {
        for (StackTraceElement el : stackTrace) {
            if (LogMaster.getIgnoredClasses().contains(el.getClassName())) {
                return false;
            }
        }
        return true;
    }

    public static void setFileLoggingOn(boolean fileLoggingOn) {
        ExceptionMaster.fileLoggingOn = fileLoggingOn;
    }

    public static void printStackTrace(Exception e) {
        {
            if (!Flags.isExceptionTraceLogged()) return;
            if (!isExceptionTraceLogged(e.getStackTrace())) return;
            // CoreEngine.isLiteLaunch();
            boolean PRINT_ALL = CoreEngine.isArcaneVault() || CoreEngine.isLevelEditor();
            if (!PRINT_ALL) {
                if (Flags.isJar() || LogMaster.isOff() || !Flags.isLiteLaunch()) {
                    //Do not print out the same exception twice
                    if (e.getMessage() != null) if (printed.contains(e.getMessage()))
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
            if (fileLoggingOn)
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
