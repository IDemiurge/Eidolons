package main.game.logic.macro.global;

import main.game.logic.macro.global.GameDate.TIME_UNITS;
import main.system.auxiliary.StringMaster;
import main.system.net.data.DataUnit;

/**
 * months?
 *
 * @author JustMe
 */
public class GameDate extends DataUnit<TIME_UNITS> {

    private static final int HOUR_OFFSET = 6;
    private final String DAY = "pm";
    private final String NIGHT = "am";
    private final String MORNING = "Early hour";
    // midday
    private final String EVENING = "Night";
    private String era;
    private int year;
    private int monthNumber;
    private MONTH month;
    private int day;
    private int hour;
    private boolean day_or_night; // pm or am
    private boolean humanMonthsDisplayed = true;

    // midnight
    public GameDate(String era, int year, MONTH month, int day,
                    boolean day_or_night) {
        this.era = era;
        this.year = year;
        this.month = month;
        this.day = day;
        this.day_or_night = day_or_night;
    }

    public GameDate() {
        // TODO Auto-generated constructor stub
    }

    public String toString() {
        return getShortString() + ", " + getStringExtension();

    }

    public String getStringExtension() {
        return "Year " + year + " of the " + era;
    }

    ;

    public String getShortString() {
        return
                // ((day_or_night) ? MORNING : EVENING) +
                getHourString() + " on the " + day + StringMaster.getOrdinalEnding(day)
                        + " of " + month.toString();
    }

    private String getHourString() {
        return (getHour() + getHourOffset()) + " "
                + ((day_or_night) ? DAY : NIGHT);
    }

    private int getHourOffset() {
        return HOUR_OFFSET;
    }

    public void nextTurn() {
        setHour(0);
        day_or_night = !day_or_night;
        if (!day_or_night) {
            if (day < getMonth().getDays()) {
                day++;
            } else {
                day = 1;
                if (getMonth().isLastMonthInYear()) {
                    year++;

                }
                month = getMonth().getNextMonth();
            }
        }
    }

    public String getEra() {
        return era;
    }

    public void setEra(int era) {
        this.era = era + StringMaster.getOrdinalEnding(era) + " Era";
    }

    public void setEra(String era) {
        this.era = era;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public MONTH getMonth() {
        if (month == null) {
            if (humanMonthsDisplayed) {
                month = HUMAN_MONTHS.values()[getMonthNumber()];
            } else {
                month = ELEDARI_MONTHS.values()[getMonthNumber()];
            }
        }
        return month;
    }

    public void setMonth(MONTH month) {
        this.month = month;
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isDay_or_night() {
        return day_or_night;
    }

    public void setDay_or_night(boolean day_or_night) {
        this.day_or_night = day_or_night;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public enum TIME_UNITS {
        ERA, YEAR, MONTH, DAY, HOUR

    }

    public enum HUMAN_MONTHS implements MONTH {
        // JANUARY(1, 31, "January", "February", "December"),
        // FEBRUARY(2, 28, "February", "March", "January"),
        // MARCH(3, 31, "March", "April", "February"),

        SNOWFALL(1, 31, "Snowfall", "Winterdeep", "Winterdeep"),
        WINTERDEEP(2, 31, "Winterdeep", "Icebreak", "Snowfall"),
        ICEBREAK(3, 31, "Icebreak", "Icebreak", "Snowfall"),
        GREYFIELD(4, 31, "Winterdeep", "Sapling", "Icebreak"),

        // SCYTHE, SEEDLING, MISTWIND, COLDWIND, CLOUDSKY, CHANGE, REAPER,
        // NEW_SUN, NEW_MOON,
        // BRIGHTSTAR, EQUINOX, LAST_SEED, LAST_LEAVES, ICESTORM,

        // EAGLE, RAVEN, WOLF, KING,
        ;

        private int n;
        private int days;
        private String prevName;
        private String nextName;
        private String name;
        private boolean last;

        HUMAN_MONTHS(int n, int days, String name, String nextName,
                     String prevName) {
            this(n, days, name, nextName, prevName, false);
        }

        HUMAN_MONTHS(int n, int days, String name, String nextName,
                     String prevName, boolean last) {
            this.n = n;
            this.days = days;
            this.name = name;
            this.nextName = nextName;
            this.prevName = prevName;
            this.last = last;
        }

        @Override
        public int getMonthNumber() {
            return n;
        }

        @Override
        public int getDays() {
            return days;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public MONTH getNextMonth() {
            return HUMAN_MONTHS.valueOf(nextName.toUpperCase());
        }

        @Override
        public MONTH getPreviousMonth() {
            return HUMAN_MONTHS.valueOf(prevName.toUpperCase());
        }

        @Override
        public boolean isLastMonthInYear() {
            return last;
        }

    }

    public enum ELEDARI_MONTHS implements MONTH {
        JANUARY(1, 31, "January", "February", "December"),;

        private int n;
        private int days;
        private String prevName;
        private String nextName;
        private String name;
        private boolean last;

        ELEDARI_MONTHS(int n, int days, String name, String nextName,
                       String prevName) {
            this(n, days, name, nextName, prevName, false);
        }

        ELEDARI_MONTHS(int n, int days, String name, String nextName,
                       String prevName, boolean last) {
            this.n = n;
            this.days = days;
            this.name = name;
            this.nextName = nextName;
            this.prevName = prevName;
            this.last = last;
        }

        @Override
        public int getMonthNumber() {
            return n;
        }

        @Override
        public int getDays() {
            return days;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public MONTH getNextMonth() {
            return HUMAN_MONTHS.valueOf(nextName.toUpperCase());
        }

        @Override
        public MONTH getPreviousMonth() {
            return HUMAN_MONTHS.valueOf(prevName.toUpperCase());
        }

        @Override
        public boolean isLastMonthInYear() {
            return last;
        }

    }

    public interface MONTH {
        int getMonthNumber();

        int getDays();

        String getName();

        MONTH getNextMonth();

        MONTH getPreviousMonth();

        boolean isLastMonthInYear();

    }

}
