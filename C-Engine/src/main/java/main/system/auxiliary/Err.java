package main.system.auxiliary;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class Err {
    public static int LEVEL = 2;
    public static int EXCLUSIVE = -1;
    // private static final Logger = Logger.getLogger(Err.class);
    private static boolean switcher = true;
    private static List<String> errorsShown = new LinkedList<>();

    public static void NOTE(String s, int level) {
        if (EXCLUSIVE != -1) {
            if (level != EXCLUSIVE) {
                return;
            } else {
                info(s);
                return;
            }
        }

        if (level >= LEVEL) {
            info(s);
        }
    }

    public static void error(String string) {
        // if (switcher) return;
        if (errorsShown.contains(string)) {
            return;
        }
        errorsShown.add(string);
        printStackTrace();
        newDialog(JOptionPane.ERROR_MESSAGE, string);
    }

    public static List<String> getErrorsShown() {
        return errorsShown;
    }

    public static void setErrorsShown(List<String> errorsShown) {
        Err.errorsShown = errorsShown;
    }

    public static boolean isSwitcher() {
        return switcher;
    }

    public static void setSwitcher(boolean switcher) {
        Err.switcher = switcher;
    }

    public static void info(String string) {
        if (errorsShown.contains(string)) {
            return;
        }
        errorsShown.add(string);
        newDialog(JOptionPane.INFORMATION_MESSAGE, string);
    }

    private static void printStackTrace() {
        try {
            throw new Exception();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void newDialog(int errorMessage, String string) {
        // SoundMaster.playPuzzlementSound();

        JOptionPane.showMessageDialog(null, string, "Oops...", errorMessage);
    }

    public static void warn(String name) {
        // boolean b = switcher;
        // switcher = false;
        // info(name);
        // switcher = b;
    }

}
