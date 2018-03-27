package main.swing.generic.services.dialog;

import main.entity.Entity;
import main.entity.obj.Obj;
import main.swing.generic.components.G_Dialog;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;
import java.util.List;

public class DialogMaster {

    public static final WAIT_OPERATIONS ASK_WAIT = WAIT_OPERATIONS.OPTION_DIALOG;
    private static final String INPUT = "Input text...";
    private static final String ERROR = "Error!";

    public static String inputText() {
        return main.swing.generic.services.DialogMaster.inputText();
    }

    public static String inputTextNotNull(String tip) {
        return main.swing.generic.services.DialogMaster.inputTextNotNull(tip);
    }

    public static String inputText(String tip) {
        return main.swing.generic.services.DialogMaster.inputText(tip);
    }

    public static Integer inputInt(int initial) {
        return main.swing.generic.services.DialogMaster.inputInt(initial);
    }

    public static Integer inputInt(String msg, int initial) {
        return main.swing.generic.services.DialogMaster.inputInt(msg, initial);
    }

    public static Integer inputInt(String msg, int initial, boolean nullIfCancelled) {
        return main.swing.generic.services.DialogMaster.inputInt(msg, initial, nullIfCancelled);
    }

    public static int inputInt() {
        return main.swing.generic.services.DialogMaster.inputInt();
    }

    public static String inputText(String tip, String initial) {
        return main.swing.generic.services.DialogMaster.inputText(tip, initial);
    }

    public static void error(String string) {
        main.swing.generic.services.DialogMaster.error(string);
    }

    public static int optionChoice(String message, Object... array) {
        return main.swing.generic.services.DialogMaster.optionChoice(message, array);
    }

    public static Object getChosenOption(String message, Object... array) {
        return main.swing.generic.services.DialogMaster.getChosenOption(message, array);
    }

    public static int optionChoice(Object[] array, String message) {
        return main.swing.generic.services.DialogMaster.optionChoice(array, message);
    }

    public static Obj objChoice(String string, Obj[] array) {
        return main.swing.generic.services.DialogMaster.objChoice(string, array);
    }

    public static Entity entityChoice(List<? extends Entity> types) {
        return main.swing.generic.services.DialogMaster.entityChoice(types);
    }

    public static void inform(String string) {
        main.swing.generic.services.DialogMaster.inform(string);
    }

    public static boolean confirm(String string) {
        return main.swing.generic.services.DialogMaster.confirm(string);
    }

    /**
     * use WaitMaster.waitForInput(WAIT_OPERATIONS.CONFIRM_DIALOG)
     *
     * @param string
     * @param wide
     * @param TRUE
     * @param FALSE
     * @param NULL
     */
    public static void ask(String string, boolean wide, String TRUE, String FALSE, String NULL) {
        final G_Dialog dialog = new ConfirmDialog(string, wide, TRUE, FALSE, NULL);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dialog.show();
            }
        });
    }

    public static Integer askOptionsAndWait(String string, boolean vertical, Object... options) {
        new OptionDialog(string, vertical, true, VISUALS.INFO_PANEL_WIDE, 3, options).show();

        return (Integer) WaitMaster.waitForInput(ASK_WAIT);
    }

    public static Boolean askAndWait(String string, String TRUE, String FALSE, String NULL) {
        return askAndWait(string, true, TRUE, FALSE, NULL);
    }

    /**
     * Never use on EDT!
     */
    public static Boolean askAndWait(String string, boolean wide, String TRUE, String FALSE,
                                     String NULL) {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("askAndWait used on EDT!");
        }

        ask(string, wide, TRUE, FALSE, NULL);
        return (Boolean) WaitMaster.waitForInput(ASK_WAIT);

    }

    public enum CONTROLS_SCHEME {

    }

}
