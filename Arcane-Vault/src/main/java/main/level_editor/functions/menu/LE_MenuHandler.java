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
        add,
        open,
        save_all,
        clone,
        floor(),
        dungeon(),
        campaign(),
        view(),
        show_map,
        ;

        public final FUNCTION_BUTTONS[] subFuncs;

        FUNCTION_BUTTONS(FUNCTION_BUTTONS... functionButtons) {
            this.subFuncs = functionButtons;
        }

    }
}
