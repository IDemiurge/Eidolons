package main.system.auxiliary;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeMaster {

    private static final String TIME_DELIMITER = ":";
    private static final String ALT_TIME_DELIMITER = "-";
    private static Integer millisInDay;

    public static String getFormattedTime() {
        return getFormattedTime(true);
    }

    public static String getFormattedTimeAlt(boolean secondsIn) {
        return getFormattedTime(secondsIn, true);
    }

    public static String getFormattedTime(boolean secondsIn) {
        return getFormattedTime(secondsIn, false);
    }

    public static String getFormattedTime(long timeRemaining, boolean showSeconds, boolean alt) {
        return getFormattedTime(new Date(timeRemaining), showSeconds, alt);
    }

    public static String getFormattedTime(boolean secondsIn, boolean alt) {
        return getFormattedTime(Calendar.getInstance().getTime(), secondsIn, alt);
    }

    public static String getFormattedTime(Date time, boolean secondsIn, boolean alt) {

        String timeDelimiter = alt ? ALT_TIME_DELIMITER : TIME_DELIMITER;
        int minutes = time.getMinutes();
        String minStr = "" + minutes;
        if (minutes / 10 == 0) {
            minStr = "0" + minutes;
        }
        String secStr = "";
        if (secondsIn) {
            int seconds = time.getSeconds();
            secStr = timeDelimiter + seconds;
            if (seconds / 10 == 0) {
                secStr = "0" + seconds;
            }
        }

        return time.getHours() + timeDelimiter + minStr + secStr;
    }

    // String[] monthName = { "January", "February", "March", "April", "May",
    // "June", "July",
    // "August", "September", "October", "November", "December" };
    //
    // Calendar cal = Calendar.getInstance();
    // String month = monthName[cal.get(Calendar.MONTH)];

    public static String getMonthName() {
        Format formatter = new SimpleDateFormat("MMMM");
        String s = formatter.format(new Date());
        return s;
        // switch(getMonth()+1){
        // case 1:
        // return "January";
        // }
        // return null;
    }

    public static String getWeekString() {
        int n = getWeek();
        return "Week " + n + " of " + getMonthName();
    }

    private static int getWeek() {
        return getDay() / 7 + 1;
    }

    public static String getMonthString() {
        int month = Calendar.getInstance().getTime().getMonth() + 1;
        String string = "" + month;
        if (month < 10) {
            string = "0" + month;
        }
        return string;
    }

    public static int getMinutes(long time) {
        return (int) (time / 60000);
    }

    public static int getHours(long time) {
        return (int) (time / 3600000);
    }

    public static int getDays(long time) {
        return Math.round(getHours(time) / 24);
    }

    public static int getSeconds(long time) {
        return (int) (time / 1000);
    }

    public static long getTime() {
        return Calendar.getInstance().getTime().getTime();
    }

    public static int getMonth() {
        return Calendar.getInstance().getTime().getMonth();
    }

    public static long getMillisInDay() {
        if (millisInDay == null) {
            millisInDay = 24 * 3600000;
        }
        return millisInDay;
    }

    public static String getDayString() {
        @SuppressWarnings("deprecation")
        int day = Calendar.getInstance().getTime().getDate() + 1;
        String string = "" + day;
        if (day < 10) {
            string = "0" + day;
        }
        return string;
    }

    public static String getDateString() {
        return StringMaster.getFormattedTimeString(getDay(), 2) + "."
                + StringMaster.getFormattedTimeString(getMonth() + 1, 2);

    }

    public static String getDayText() {
        int day = getDay();
        return day + StringMaster.getOrdinalEnding(getDay()) + " of " + getMonthName();
    }

    public static Integer getDaysInMonth(Integer month) {
        int iYear = getYear();
        Calendar mycal = new GregorianCalendar(iYear, month, 1);

        return mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int getYear() {
        return Calendar.getInstance().getTime().getYear();
    }

    public static int getDay() {
        return Calendar.getInstance().getTime().getDate();
    }

    public static String getFormattedDate() {
        return getFormattedDate(false);
    }

    public static String getFormattedDate(boolean time, String millis) {
        if (millis.isEmpty()) {
            return "";
        }
        return getFormattedDate(time, Long.parseLong(millis));
    }

    public static String getFormattedDate(boolean time, long millis) {
        if (millis <= 0) {
            return "";
        }
        return getFormattedDate(new Date(millis), time);
    }

    public static String getFormattedDate(boolean time) {
        Date date = Calendar.getInstance().getTime();
        return getFormattedDate(date, time);
    }

    public static String getFormattedDate(Date date, boolean time) {
        return getFormattedDate(date, time, ".");
    }

    public static String getFormattedDate(Date date, boolean time, String delimiter) {
        @SuppressWarnings("deprecation")
        String string = (date.getDate()) + delimiter + (date.getMonth() + 1);
        if (time) {
            string += " - " + getFormattedTime(false).replace(TIME_DELIMITER, ALT_TIME_DELIMITER);
        }
        return string;
    }

    public static boolean isToday(int time) {
        long difference = TimeMaster.getTime() - time;
        return difference < TimeMaster.getMillisInDay();
    }

    public static boolean isThisWeek(int time) {
        if (!isThisMonth(time)) {
            return false;
        }
        long difference = TimeMaster.getTime() - time;
        return difference < TimeMaster.getMillisInDay() * 7;
    }

    public static boolean isThisMonth(int time) {
        return new Date(time).getMonth() == getMonth();
    }

}
