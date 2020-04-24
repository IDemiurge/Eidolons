package main.swing.generic.services;

import main.data.DataManager;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogMaster {

    public static final WAIT_OPERATIONS ASK_WAIT = WAIT_OPERATIONS.OPTION_DIALOG;
    private static final String INPUT = "Input text...";
    private static final String ERROR = "Error!";

    public static String inputText() {
        return inputText(INPUT);
    }

    public static String inputTextNotNull(String tip) {
        String input = inputText(tip);
        if (input == null) {
            return "";
        }
        return input;
    }

    public static String inputText(String tip) {
        return JOptionPane.showInputDialog(tip);
    }

    public static Integer inputInt(int initial) {
        return inputInt("Enter an integer", initial);
    }

    public static Integer inputInt(String msg, int initial) {
        return inputInt("Enter an integer", initial, false);
    }

    public static Integer inputInt(String msg, int initial, boolean nullIfCancelled) {
        String input = JOptionPane.showInputDialog(msg, initial);
        if (input == null) {
            if (nullIfCancelled) {
                return null;
            }
            return -1;
        }
        try {
            return Integer.valueOf(input);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            if (nullIfCancelled) {
                return null;
            }
            return -1;
        }
    }

    public static int inputInt() {
        String input = JOptionPane.showInputDialog("Enter an integer");
        try {
            return Integer.valueOf(input);
        } catch (Exception e) {
            return -1;
        }
    }

    public static String inputText(String tip, String initial) {
        return JOptionPane.showInputDialog(tip, initial);
    }

    public static void error(String string) {
        JOptionPane.showMessageDialog(null, string, ERROR, JOptionPane.ERROR_MESSAGE);
    }

    public static int optionChoice(String message, Object... array) {
        return optionChoice(array, message);
    }

    public static Object getChosenOption(String message, Object... array) {
        int optionChoice = optionChoice(array, message);
        if (optionChoice == -1) {
            return null;
        }
        return array[optionChoice];
    }

    public static int optionChoice(Object[] array, String message) {
        array = ListMaster.toStringList(array).toArray();
        return JOptionPane.showOptionDialog(null, message, "Choose...",
         JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, array, array[0]);
    }

    public static Obj objChoice(String string, Obj[] array) {
        List<Obj> list = new ArrayList<>(Arrays.asList(array));
        ObjType type = (ObjType) entityChoice(DataManager.toTypeList(list));
        for (Obj obj : list) {
            if (obj.getType() == type) {
                return obj;
            }
        }
        return null;
    }

    public static Entity entityChoice(List<? extends Entity> types) {
        int i = 0;
        ImageIcon[] array = new ImageIcon[types.size()];
        for (Entity t : types) {
            ImageIcon icon = t.getIcon();
            if (icon.getIconHeight() > GuiManager.getBfObjSize()) {
                icon = new ImageIcon(ImageManager.getSizedVersion(icon.getImage(), GuiManager
                 .getBfObjSize()));
            }
            array[i] = icon;
            i++;
        }
        int index = JOptionPane.showOptionDialog(null, "Select type: ", "Choose...",
         JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, array, array[0]);
        if (index == -1) {
            return null;
        }
        return types.get(index);
    }

    public static void inform(String string) {
        JOptionPane.showMessageDialog(null, string);
    }

    public static boolean confirm(String string) {
        int showConfirmDialog = JOptionPane.showConfirmDialog(null, string, "Confirm",
         JOptionPane.YES_NO_OPTION);
        return showConfirmDialog == JOptionPane.YES_OPTION;
    }

    public enum CONTROLS_SCHEME {

    }

}
