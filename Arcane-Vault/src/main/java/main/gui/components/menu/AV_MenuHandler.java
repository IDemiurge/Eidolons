package main.gui.components.menu;

import main.gui.components.menu.AV_Menu.AV_MENU_ITEMS;
import main.handlers.AvManager;
import main.handlers.mod.Av2_Xml;
import main.handlers.mod.AvModelHandler;
import main.handlers.mod.AvSaveHandler;
import main.v2_0.AV2;
import main.system.auxiliary.EnumMaster;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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
            case TOGGLE_SIM -> {
                AvManager.toggle();
            }
            case SAVE -> {
            }
            case COMMIT -> {
                AV2.getVersionHandler().commitVersion();
            }
            case BACKUP -> {
                AV2.getSaveHandler().fullBackUp(null );
            }
            case RELOAD -> {
            }
            case ADD_TAB -> {
                Av2_Xml.addTab();
            }
            case SAVE_ALL -> {
                AvSaveHandler.saveAll();
            }
            case CUSTOM_FILTER -> {
            }
            case NEW_FILTER -> {
            }
            case FIND_TYPE -> {
                AV2.getModelHandler().findType();
            }


            case FILTER_WORKSPACE_VIEW -> {
            }
            case ADD_TO_WORKSPACE -> {
                AvModelHandler.addToWorkspace();
            }
            case ADD_TO_CUSTOM_WORKSPACE -> {

                AV2.getWorkspaceHandler().addToCustomWorkspace();
            }
            case LOAD_WORKSPACE -> {
                AV2.getWorkspaceManager().loadWorkspace();
            }
            case SAVE_WORKSPACE -> {
                AV2.getWorkspaceManager().save();
            }
            case SAVE_WORKSPACE_AS -> {
                AV2.getWorkspaceManager().save();
            }
            case DELETE_WORKSPACE -> {
                AV2.getWorkspaceHandler().removeWorkspace();
            }
            // case RENAME_WORKSPACE -> {
            //     AV2.getWorkspaceManager().loadWorkspace();
            // }
            case GROUPING -> {
            }
            case SORT_WORKSPACE -> {
            }
        }

    }


}
