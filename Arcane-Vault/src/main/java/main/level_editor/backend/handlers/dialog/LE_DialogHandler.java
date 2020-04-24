package main.level_editor.backend.handlers.dialog;

import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.libgdx.utils.GdxDialogMaster;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.gui.screen.LE_Screen;
import main.level_editor.gui.stage.LE_GuiStage;


public class LE_DialogHandler extends LE_Handler {

    public LE_DialogHandler(LE_Manager manager) {
        super(manager);
    }

    public <T extends Enum<T>> T  chooseEnum(Class<T> c){
        return getGuiStage().getEnumChooser().chooseEnum(  c);
    }

    private LE_GuiStage getGuiStage() {
        return LE_Screen.getInstance().getGuiStage();
    }


    private ModuleData getDefaultModuleData(Module module) {
        return (ModuleData) new ModuleData(module).setData("width:45;height:45;");
    }

    public String textInput(String tip, String text) {
        return GdxDialogMaster.inputText(tip, text);
    }

}
