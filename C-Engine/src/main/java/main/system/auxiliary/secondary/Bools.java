package main.system.auxiliary.secondary;

import main.system.auxiliary.RandomWizard;

public class Bools {

    public static final Boolean[] FALSE_TRUE = {false, true};
    public static final Boolean[] TRUE_FALSE = {true, false};
    public static final Boolean[] FALSE_FALSE = {false, false};
    public static final Boolean[] TRUE_TRUE = {true, true};
    public static final Boolean[] NULL_TRUE_FALSE = {null, true, false};
    public static final Boolean[] TRUE_FALSE_NULL = {true, false, null};
    public static final Boolean[] FALSE_TRUE_NULL = {false, true, null};
    public static final Boolean[] NULL_FALSE_TRUE = {null, false, true};

    public static boolean areOpposite(Boolean b, Boolean b2) {
        if (b == null) {
            return false;
        }
        if (b2 == null) {
            return false;
        }
        return b != b2;
    }

    public static int compare(Number n, Number n2) {
        if (n.intValue() > n2.intValue()) {
            return 1;
        }
        if (n2.intValue() > n.intValue()) {
            return -1;
        }
        return 0;
    }

    public static boolean isTrue(Boolean b) {
        if (b == null) {
            return false;
        }
        return b;
    }

    public static boolean isFalse(Boolean b) {
        if (b == null) {
            return false;
        }
        return !b;
    }

    public static boolean random() {
        return RandomWizard.random();
    }

    public static Boolean[][] getBoolArrayCombinatorics2D() {
        return new Boolean[][]{FALSE_FALSE, TRUE_TRUE, TRUE_FALSE, TRUE_FALSE};
    }

    public static Boolean getBool(String var) {
        if (var.equalsIgnoreCase("true")) {
            return true;
        }
        if (var.equalsIgnoreCase("false")) {
            return false;
        }
        return null;
    }
}
