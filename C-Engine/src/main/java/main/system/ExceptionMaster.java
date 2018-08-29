package main.system;

import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 9/11/2017.
 */
public class ExceptionMaster {

    static List<String> printed = new ArrayList<>();

    public static void printStackTrace(Exception e) {
//
        {
            if (CoreEngine.isJar() || LogMaster.isOff())
                if (printed.contains(e.getMessage()))
                    return;
            LogMaster.getExceptionList().add(e);
            LogMaster.logException(e);

            printed.add(e.getMessage());
            e.printStackTrace();
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
