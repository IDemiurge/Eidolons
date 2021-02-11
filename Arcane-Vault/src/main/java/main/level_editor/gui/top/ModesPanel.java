package main.level_editor.gui.top;

import libgdx.gui.panels.TablePanelX;
import main.level_editor.backend.handlers.LE_MenuHandler;

public class ModesPanel extends TablePanelX {
//duplicate in MENU
    public static final LE_MenuHandler.FUNCTION_BUTTONS[] modeButtons={

};
    public enum EDITOR_VIEW{
        normal,
        no_ui,
        lite,
        symbolic, //for zoom out?
        debug, //with all info...

        //these should be available via menu I guess!
    }
//    SmartButton[] modeButtons;
//    public void init(){
//        initModeButton(mode);
//    }

}
