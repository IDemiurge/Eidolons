package main.system.entity;

import main.content.enums.entity.UnitEnums.COUNTER;
import main.entity.obj.Obj;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

public class CounterMaster {

    public static COUNTER findCounterConst(String valueName) {
        if (valueName == null) {
            return null;
        }
        COUNTER counter = new EnumMaster<COUNTER>().retrieveEnumConst(COUNTER.class,
         valueName);
        if (counter == null) {
            counter = new EnumMaster<COUNTER>().retrieveEnumConst(COUNTER.class,
             valueName, true);
        }
        return counter;
    }

    public static String findCounter(String valueName) {
        return findCounter(valueName, false);
    }

    public static String findCounter(String valueName, boolean strict) {
        COUNTER c = getCounter(valueName, strict);
        if (c != null)
            return c.getName();
        valueName = StringMaster.getWellFormattedString(valueName);
        if (!valueName.contains(StringMaster.COUNTER)) {
            valueName = StringMaster.getWellFormattedString(valueName.trim())
             + StringMaster.COUNTER;
        }

        return valueName;
    }

    public static COUNTER getCounter(String valueName, boolean strict) {
        if (valueName == null) {
            return null;
        }
        if (!StringMaster.contains(valueName, StringMaster.COUNTER, true, true)) {
            valueName = valueName + StringMaster.COUNTER;
        }
        COUNTER counter = new EnumMaster<COUNTER>().retrieveEnumConst(COUNTER.class,
         valueName);
        if (counter == null) {
            if (strict) {
                return null;
            }
        } else {
            counter = new EnumMaster<COUNTER>().retrieveEnumConst(COUNTER.class,
             valueName, true);
        }
        return counter;

    }

    public static float getCounterPriority(String counterName, Obj target) {
        String realName = findCounter(counterName);
        switch (new EnumMaster<COUNTER>().retrieveEnumConst(COUNTER.class, realName)) {
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
        switch (new EnumMaster<COUNTER>().retrieveEnumConst(COUNTER.class, realName)) {
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
