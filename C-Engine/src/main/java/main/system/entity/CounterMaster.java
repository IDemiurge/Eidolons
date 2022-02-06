package main.system.entity;

import main.content.enums.entity.EffectEnums.COUNTER;
import main.entity.obj.Obj;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;

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
            return c.getName().toUpperCase();
        valueName = StringMaster.format(valueName);
        if (!valueName.contains(Strings.COUNTER)) {
            valueName = StringMaster.format(valueName.trim())
             + Strings.COUNTER;
        }

        return valueName.toUpperCase();
    }

    public static COUNTER getCounter(String valueName, boolean strict) {
        if (valueName == null) {
            return null;
        }
        if (!StringMaster.contains(valueName, Strings.COUNTER, true, true)) {
            valueName = valueName + Strings.COUNTER;
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
            case Blaze:
                return 3; // percentage of health?
            case Bleeding:
                return 5;
            case Blight:
                return 2;
            case Corrosion:
                return 2.25f;
            case Despair:
                return 2.45f;

            case Chill:
                return 4.5f;
            case Poison:
                return 4.5f;
            case Disease:
                return 6;

            case Ensnared:
                return 7;

            case Lust:
                return 2;

            case Madness:
                return 3;

            case Moist:
                return 2;

            case Rage:

                return 5;

            case Undying:
                return 25;

        }
        return 0;
    }

    public static boolean isCounterPositive(String counterName) {
        String realName = findCounter(counterName);
        switch (new EnumMaster<COUNTER>().retrieveEnumConst(COUNTER.class, realName)) {
            case Blaze:

            case Bleeding:

            case Blight:

            case Corrosion:

            case Despair:

            case Disease:

            case Ensnared:

            case Chill:

            case Lust:

            case Madness:

            case Moist:

            case Poison:

            case Rage:

                return false;

            case Undying:
                return true;

        }

        return false;
    }

    public static String getImagePath(String counter) {
        COUNTER v = getCounter(counter, false);
        if (v != null)
            return v.getImagePath();
        return null;
    }
}
