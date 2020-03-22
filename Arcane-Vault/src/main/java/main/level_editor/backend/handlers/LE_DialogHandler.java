package main.level_editor.backend.handlers;

import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.gui.screen.LE_Screen;


public class LE_DialogHandler extends LE_Handler {

    public LE_DialogHandler(LE_Manager manager) {
        super(manager);
    }

    public <T extends Enum<T>> T  chooseEnum(Class<T> c){
        return LE_Screen.getInstance().getGuiStage().getEnumChooser().chooseEnum(  c);
    }
}
