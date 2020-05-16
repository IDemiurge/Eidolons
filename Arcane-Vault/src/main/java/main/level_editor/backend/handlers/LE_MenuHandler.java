package main.level_editor.backend.handlers;

import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.structure.FloorManager;

public class LE_MenuHandler extends LE_Handler {

    public LE_MenuHandler(LE_Manager manager) {
        super(manager);
    }

    public void clicked(FUNCTION_BUTTONS subFunc) {
        switch (subFunc) {
            case add_floor:
                FloorManager.addFloor();
                break;
            case open:
                //campaign?
                //close current?
                getDataHandler().openFloor();
                //how to dispose safely?
                break;
            case save_version:
                getDataHandler().saveVersion();
                break;
            case save_as:
                getDataHandler().saveAs();
                break;
            case save_module:
                getDataHandler().saveModule();
                break;
            case save_modules:
                getDataHandler().saveModulesSeparately();
                break;
            case save_all:
//                LevelEditor.getCurrent()
                getDataHandler().saveAll();
                break;
            case clone:
                //floor
                FloorManager.cloneFloor();
                break;
            case toggle_all:
                getModel().getDisplayMode().toggleAll();
                break;
            case colors:
                getModel().getDisplayMode().toggleColors();
            case coordinates:
                getModel().getDisplayMode().toggleCoordinates();
                break;
            case meta_info:
                getModel().getDisplayMode().toggleScripts();
                break;
            case ai_info:
                getModel().getDisplayMode().toggleAi();
                break;
        }
    }


    public enum FUNCTION_BUTTONS {
        add_dungeon,
        add_floor,
        add_module,
        edit_module, resize, swap_modules, remove_module,
        edit_floor, remove_floor,

        //////////////
        open, save_all, clone,
        save_version, save_as, save_module, save_modules,
        file(open, clone, save_all, save_version, save_as, save_module, save_modules),
        //////////////
        /*
        showStacks;
    boolean showMetaAi;
    boolean showScripts;

    boolean showCoordinates; //TODO use options?
    boolean showIllumination;
    boolean showSpace;

    boolean showAllColors
         */
        toggle_all, colors, coordinates, meta_info, ai_info,
        view(toggle_all, colors, coordinates, meta_info, ai_info),
        //        edit(),
//        layer(),
        module(edit_module, resize, swap_modules, remove_module),
        floor(add_module, edit_floor, remove_floor),
//        dungeon(true, edit_dungeon), TODO use string regex to add floors from folder
//        campaign(),


        ;
        boolean campaignMode;
        public final FUNCTION_BUTTONS[] subFuncs;

        FUNCTION_BUTTONS(FUNCTION_BUTTONS... functionButtons) {
            this.subFuncs = functionButtons;
        }

    }
}
