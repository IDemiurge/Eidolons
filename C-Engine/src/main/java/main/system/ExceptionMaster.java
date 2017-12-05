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
     else    SpecialLogger.getInstance().appendExceptionToFileLog(
//         e.getStackTrace()[0]
//         Thread.currentThread().con
         e.toString()+ " from " +  e.getStackTrace()[0]);
          {
//             e.getMessage()
        }
//        try{}catch(Exception e){main.system.ExceptionMaster.printStackTrace( e);}
    }
}
