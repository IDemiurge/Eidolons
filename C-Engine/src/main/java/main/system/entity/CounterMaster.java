package main.system.entity;

import main.content.CONTENT_CONSTS.STD_COUNTERS;
import main.entity.obj.Obj;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

public class CounterMaster {

    public static STD_COUNTERS findCounterConst(String valueName) {
        if (valueName == null) {
            return null;
        }
        STD_COUNTERS counter = new EnumMaster<STD_COUNTERS>().retrieveEnumConst(STD_COUNTERS.class,
                valueName);
        if (counter == null) {
            counter = new EnumMaster<STD_COUNTERS>().retrieveEnumConst(STD_COUNTERS.class,
                    valueName, true);
        }
        return counter;
    }

    public static String findCounter(String valueName) {
        return findCounter(valueName, false);
    }

    public static String findCounter(String valueName, boolean strict) {
        if (valueName == null) {
            return null;
        }
        if (!StringMaster.contains(valueName, StringMaster.COUNTER, true, true)) {
            valueName = valueName + StringMaster.COUNTER;
        }
        STD_COUNTERS counter = new EnumMaster<STD_COUNTERS>().retrieveEnumConst(STD_COUNTERS.class,
                valueName);
        if (counter == null) {
            if (strict) {
                return null;
            }
        } else {
            counter = new EnumMaster<STD_COUNTERS>().retrieveEnumConst(STD_COUNTERS.class,
                    valueName, true);
        }
        if (counter != null) {
            return counter.getName();
        }
        valueName = StringMaster.getWellFormattedString(valueName);
        if (!valueName.contains(StringMaster.COUNTER)) {
            valueName = StringMaster.getWellFormattedString(valueName.trim())
                    + StringMaster.COUNTER;
        }

        return valueName;
    }

    public static float getCounterPriority(String counterName, Obj target) {
        String realName = findCounter(counterName);
        switch (new EnumMaster<STD_COUNTERS>().retrieveEnumConst(STD_COUNTERS.class, realName)) {
            case Blaze_Counter:
                return 3; // percentage of health?
            case Bleeding_Counter:
                return 5;
            case Blight_Counter:
                return 2;
            case Corrosion_Counter:
                return 2.25f;
            case Despair_Counter:
                return 2.45f;

            case Freeze_Counter:
                return 4.5f;
            case Poison_Counter:
                return 4.5f;
            case Disease_Counter:
                return 6;

            case Ensnared_Counter:
                return 7;
            case Hatred_Counter:
                return 1;

            case Lust_Counter:
                return 2;

            case Madness_Counter:
                return 3;

            case Moist_Counter:
                return 2;

            case Rage_Counter:

                return 5;

            case Soul_Counter:
            case Undying_Counter:
                return 25;

        }
        return 0;
    }

    public static boolean isCounterPositive(String counterName) {
        String realName = findCounter(counterName);
        switch (new EnumMaster<STD_COUNTERS>().retrieveEnumConst(STD_COUNTERS.class, realName)) {
            case Blaze_Counter:

            case Bleeding_Counter:

            case Blight_Counter:

            case Corrosion_Counter:

            case Despair_Counter:

            case Disease_Counter:

            case Ensnared_Counter:

            case Freeze_Counter:

            case Hatred_Counter:

            case Lust_Counter:

            case Madness_Counter:

            case Moist_Counter:

            case Poison_Counter:

            case Rage_Counter:

                return false;

            case Soul_Counter:
            case Undying_Counter:
                return true;

        }

        return false;
    }

}
