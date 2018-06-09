package eidolons.game.module.adventure.global.time;

import eidolons.game.module.adventure.global.time.GameDate.TIME_UNITS;
import eidolons.libgdx.screens.map.ui.time.MapTimePanel.MOON;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;

import static eidolons.libgdx.screens.map.ui.time.MapTimePanel.MOON.*;

/**
 * months?
 *
 * @author JustMe
 */
public class GameDate extends DataUnit<TIME_UNITS> {

    private static final int HOUR_OFFSET = 6;
    private String era="3rd Era";
    private int year;
    private int monthNumber;
    private MONTH month=HUMAN_MONTHS.ARDENT;
    private int day;
    private int hour;
    private boolean humanMonthsDisplayed = true;
    private DAY_TIME dayTime;


    public GameDate() {
        // TODO Auto-generated constructor stub
    }

    public String toString() {
        return getShortString() + ", " + getStringExtension();

    }

    public String getStringExtension() {
        return "Year " + year + " of the " + era;
    }

    public String getShortString() {
        return
       dayTime + " on the " + day + StringMaster.getOrdinalEnding(day)
          + " of " + month.toString();
    }



    private int getHourOffset() {
        return HOUR_OFFSET;
    }

    public boolean nextDay() {
        if (day < getMonth().getDays()) {
            day++;
            return false;
        } else {
            nextMonth();
            return true;
        }
    }

    public boolean nextMonth() {
        day = 1;
        if (getMonth().isLastMonthInYear()) {
            year++;
            month = HUMAN_MONTHS.values()[0];
            return true;
        }
        month = getMonth().getNextMonth();
        return false;
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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public DAY_TIME getDayTime() {
        return dayTime;
    }

    public void setDayTime(DAY_TIME dayTime) {
        this.dayTime = dayTime;
    }

    public enum ELEDARI_MONTHS implements MONTH {
        JANUARY(1, 30, "January", "February", "December") {
            @Override
            public MOON getActiveMoon(boolean night) {
                return null;
            }
        },;

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

    public enum HUMAN_MONTHS implements MONTH {


        LIGHTWIND(30, false, RIME, FAE),
        GREYFIELD(31, false, FAE, FEL),
        BLOSSOM(30, false, FAE, HAVEN),

        BRIGHTSTAR(30, false, TEMPEST, HAVEN),
        ARDENT(31, false, TEMPEST, HAVEN),
        HARVESTING(30, false, HAVEN, FEL),

        STORMGALE(30, true, SHADE, TEMPEST),
        BLACKLEAF(31, true, SHADE, FAE),
        HOLLOWMOURN(30, true, SHADE, FEL),

        RIMEMYST(30, true, RIME, SHADE),
        WINTERDEEP(31, true, RIME, FEL),
        ICEBREAK(31, true, RIME, TEMPEST) {
            @Override
            public boolean isLastMonthInYear() {
                return true;
            }
        },

        // BRIGHTSTAR,   ICESTORM,
        ;

        private MOON[] moons;
        private boolean nightly;
        private int days;

        HUMAN_MONTHS(int days, boolean nightly, MOON... moons) {
            this.days = days;
            this.nightly = nightly;
            this.moons = moons;
        }


        @Override
        public MOON getActiveMoon(boolean night) {
            if (nightly) night = !night;
            int i = night ? 1 : 0;
            return moons[i];
        }

        @Override
        public int getMonthNumber() {
            return EnumMaster.getEnumConstIndex(HUMAN_MONTHS.class, this);
        }

        @Override
        public int getDays() {
            return days;
        }

        @Override
        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public MONTH getNextMonth() {
            return HUMAN_MONTHS.values()[EnumMaster.getEnumConstIndex(HUMAN_MONTHS.class, this) + 1];
        }

        @Override
        public MONTH getPreviousMonth() {
            return HUMAN_MONTHS.values()
             [EnumMaster.getEnumConstIndex(HUMAN_MONTHS.class, this) - 1];

        }

        @Override
        public boolean isLastMonthInYear() {
            return false;
        }

    }

    public enum TIME_UNITS {
        ERA, YEAR, MONTH, DAY, HOUR

    }

    public interface MONTH {
        int getMonthNumber();

        int getDays();

        String getName();

        MONTH getNextMonth();

        MONTH getPreviousMonth();

        boolean isLastMonthInYear();

        MOON getActiveMoon(boolean night);
    }

}
