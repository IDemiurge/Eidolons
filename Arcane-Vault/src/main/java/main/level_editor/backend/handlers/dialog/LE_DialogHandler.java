package main.level_editor.backend.handlers.dialog;

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

    public void newModule(){
        ModuleData data = getDefaultModuleData();
//        return getGuiStage().getModuleDialog().addModule(data);
    }

    private ModuleData getDefaultModuleData() {
        return new ModuleData();
    }

    public String textInput(String tip, String text) {
        return GdxDialogMaster.inputText(tip, text);
    }

}
