package main.gui.components.menu;

import eidolons.system.text.TextMaster;
import main.gui.components.menu.AV_Menu.AV_MENU_ITEMS;
import main.handlers.AvManager;
import main.handlers.mod.AvModelHandler;
import main.handlers.mod.AvSaveHandler;
import main.handlers.mod.AvVersionHandler;
import main.launch.ArcaneVault;
import main.system.auxiliary.EnumMaster;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;


public class AV_MenuHandler implements   ActionListener {
    // delegate to ModelManager, WS manager, WS controls

    /*
    priority of menu functionality:

    filters
    color schemes


     */

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        AV_MENU_ITEMS i = new EnumMaster<AV_MENU_ITEMS>().retrieveEnumConst(AV_MENU_ITEMS.class,actionCommand);
        String[] args = null;
        switch (i) {
            case GENERATE_MISSING_DESCRIPTIONS:
                TextMaster.generateMissingDescrTemplate();
                break;
            case ADD_TO_WORKSPACE:
                AvModelHandler.addToWorkspace();
                break;
            case FIND_TYPE:
                AvModelHandler.findType();
                break;
            case SET_VALUE:
                ArcaneVault.getGame().getValueHelper().promptSetValue();
                break;
            case SIMULATION:
                AvManager.toggle();
                break;
            case SAVE:
                AvSaveHandler.saveAll();
                break;
            case COMMIT:
                AvVersionHandler.commitVersion();
                break;
            case UNDO:
                AvModelHandler.undo();
                break;
            case BACK:
                AvModelHandler.undo();
                break;
            case BACKUP:
                AvModelHandler.backUp();
                break;
            case DELETE:
                AvModelHandler.remove();
                break;
        }

    }


}
