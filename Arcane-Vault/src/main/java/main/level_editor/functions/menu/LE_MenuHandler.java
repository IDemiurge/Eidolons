package main.level_editor.functions.menu;

import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import main.level_editor.functions.LE_Handler;
import main.level_editor.functions.LE_Manager;
import main.level_editor.struct.campaign.Campaign;
import main.level_editor.struct.level.Floor;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_MenuHandler extends LE_Handler {

    public LE_MenuHandler(LE_Manager manager) {
        super(manager);
    }

    public void clicked(FUNCTION_BUTTONS subFunc) {
        switch (subFunc) {

        }
    }


    public enum FUNCTION_BUTTONS{
        add_dungeon,
        add_floor,
        add_module,
        edit_module, resize, swap_modules, remove_module,
        edit_floor, remove_floor,
        open,
        save_all,
        clone,
        toggle_all, all_off, all_on, show_map,

        file(open, clone, save_all),
        view(toggle_all, all_off, all_on, show_map),
//        edit(),
//        layer(),
        module(edit_module, resize, swap_modules, remove_module ),
        floor( add_module, edit_floor, remove_floor),
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
