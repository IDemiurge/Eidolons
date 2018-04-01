package main.game.logic.event;

import main.entity.Entity;
import main.entity.obj.Obj;
import main.system.auxiliary.Manager;

import javax.swing.*;

public class MessageManager extends Manager {

    public static boolean confirm(String message) {
        int result = JOptionPane
         .showConfirmDialog(null, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return (result == JOptionPane.YES_OPTION);
    }

    public static void alert(String cannotAttack) {
        JOptionPane.showMessageDialog(null, cannotAttack);
    }

    public static boolean promptItemSwap(String itemName, Obj hero, Entity type) {
        if (!game.isSimulation()) {
            return false; // TODO refactor
        }

        String message = "Do you want to swap " + itemName + " for "
         + type.getNameOrId() + "?";

        return confirm(message);
    }

    public static boolean promptSpellReplace(Obj hero, Entity newType, Entity type) {
        String message = "Do you want to replace " + type.getName() + " with "
         + newType.getName() + " upgrade?";
        return confirm(message);
    }
}
