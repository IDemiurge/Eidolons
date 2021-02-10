package main.gui.components.menu;

import eidolons.system.text.TextMaster;
import main.gui.components.menu.AV_Menu.MENU_ITEMS;
import main.handlers.AvManager;
import main.handlers.mod.AvModelHandler;
import main.handlers.mod.AvSaveHandler;
import main.launch.ArcaneVault;
import main.system.auxiliary.EnumMaster;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AV_MenuHandler implements MenuListener, ActionListener {
    // delegate to ModelManager, WS manager, WS controls
    @Override
    public void menuSelected(MenuEvent e) {
        // e.getSource()

    }

    public void actionPerformed(ActionEvent e) {
        MENU_ITEMS i = new EnumMaster<MENU_ITEMS>().retrieveEnumConst(MENU_ITEMS.class, e
                .getActionCommand());
        JMenuItem item = (JMenuItem) e.getSource();
        MENU_ITEMS subItem = new EnumMaster<MENU_ITEMS>().retrieveEnumConst(MENU_ITEMS.class, item
                .getActionCommand());
        String[] args = null;
        switch (i) {
            case HC:
                switch (subItem) {
                    case PARTY:

                }
//				FAST_HC.main(args);
                break;
            case DC:
//				TestLauncher.launchDC(args);

                break;
            case GENERATE_MISSING_DESCRIPTIONS:
                TextMaster.generateMissingDescrTemplate();
                break;
            case ADD_TO_WORKSPACE:
                AvModelHandler.addToWorkspace();
                break;
            case ADD:
                AvModelHandler.add();
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

            case ADD_TO_CUSTOM_WORKSPACE:
                break;
            case AUTO_SAVE:
                break;
            case BACK:
                AvModelHandler.undo();
                break;
            case BACKUP:
                AvModelHandler.backUp();
                break;
            case CUSTOM_FILTER:
                break;
            case DELETE:
                AvModelHandler.remove();
                break;
            case DELETE_WORKSPACE:
                break;
            case FILTER_WORKSPACE_VIEW:
                // filter = chooseFilter();
                // ArcaneVault.getWorkspaceManager().getActiveWorkspace().setFilter();
                break;
            case FORMULA:
                break;
            case FORWARD:
                break;
            case GROUPING:
                break;
            case GROUPING_BACK:
                break;
            case GROUP_TOGGLE:
                break;
            case NEW_FILTER:
                break;
            case NODE_DOWN:
                break;
            case NODE_UP:
                break;
            case PERIOD:
                break;
            case REDO:
                break;
            case RELOAD:
                break;
            case REMOVE_GROUPING:
                break;
            case REMOVE_VALUE:
                break;
            case RENAME_WORKSPACE:
                break;
            case SAVE:
                AvSaveHandler.saveAll();
                break;
            case SAVE_TEMPLATE:
                break;
            case SAVE_WORKSPACE:
                break;
            case SAVE_WORKSPACE_AS:
                break;

            case SORT_WORKSPACE:
                break;
            case TOGGLE_AS:
                break;
            case TOGGLE_SIM:
                break;
            case UNDO:
                AvModelHandler.undo();
                break;
            case UPGRADE:
                break;
            case WRAP_NODE:
                break;
            default:
                break;
        }

    }

    @Override
    public void menuCanceled(MenuEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void menuDeselected(MenuEvent e) {
        // TODO Auto-generated method stub

    }

}
