package main.system.auxiliary;

import main.data.ConcurrentMap;
import main.system.launch.CoreEngine;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Map;

public class Chronos {
    public static final boolean CONSTRUCT = false;
    private static final Logger logger = LogMaster.getInstance();
    private static Map<String, Calendar> timeMap = new ConcurrentMap<>();

    public static void mark(String string) {
        // if (!CoreEngine.isTEST_MODE())
        // return;
        Calendar calendar = Calendar.getInstance();
        timeMap.put(string, calendar);
    }

    public static Long getTimeElapsedForMark(String string) {
        Calendar calendar = Calendar.getInstance();
        // Logger l = ;

        long x = -1;
        try {
            x = timeMap.get(string).getTime().getTime();
        } catch (Exception e) {
            // main.system.auxiliary.LogMaster.log(LogMaster.PERFORMANCE_DEBUG,
            // "No mark for "
            // + (string));
            return x;
        }
        return calendar.getTime().getTime() - x;
    }

    public static void logTimeElapsedForMark(Boolean flag, String string) {
        if (!flag) {
            return;
        }
        logTimeElapsedForMark(string);

    }

    public static void logTimeElapsedForMark(String string) {
        if (!CoreEngine.isTEST_MODE()) {
            return;
        }
        long timeElapsedForMark = getTimeElapsedForMark(string);

        if (timeElapsedForMark == -1) {
            return;
        }
        timeMap.remove(string);
        if (timeElapsedForMark > 500) {
            LogMaster.log(LogMaster.PERFORMANCE_DEBUG, string
                    + " FINISHED AFTER " + timeElapsedForMark);
        }
    }

    public static long getMinTimeLogged() {

        return 500;
    }

}
