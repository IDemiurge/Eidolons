package main.system.auxiliary.log;

import main.data.ConcurrentMap;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Map;

public class Chronos {
    public static final boolean CONSTRUCT = false;
    private static Map<String, Calendar> timeMap = new ConcurrentMap<>();
    private static boolean on = true;

    public static boolean isOn() {
        return on;
    }

    public static void setOn(boolean on) {
        Chronos.on = on;
    }

    public static void mark(String string) {
        if (!on) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        timeMap.put(string, calendar);
    }

    public static Long getTimeElapsedForMark(String string) {
        if (!on) {
            return new Long(0);
        }
        Calendar calendar = Calendar.getInstance();
        // Logger l = ;

        long x = -1;
        if (timeMap.get(string) == null)
            return x;

        x = timeMap.get(string).getTime().getTime();

        return calendar.getTime().getTime() - x;
    }

    public static void logTimeElapsedForMark(Boolean flag, String string) {
        if (!flag) {
            return;
        }
        logTimeElapsedForMark(string);

    }

    public static void logTimeElapsedForMark(String string) {
        logTimeElapsedForMark(string, false);
    }

    public static void logTimeElapsedForMark(String string, boolean forced) {
        if (!on) {
            return;
        }
        long timeElapsedForMark = getTimeElapsedForMark(string);

        if (timeElapsedForMark == -1) {
            return;
        }
        timeMap.remove(string);
        if (timeElapsedForMark > 100 ||
                forced)
        {
            LogMaster.log(LogMaster.PERFORMANCE_DEBUG, string
             + " FINISHED AFTER " + timeElapsedForMark);
        }
    }

    public static long getMinTimeLogged() {

        return 500;
    }

}
