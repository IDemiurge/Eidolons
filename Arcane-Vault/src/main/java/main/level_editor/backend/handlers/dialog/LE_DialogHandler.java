package main.level_editor.backend.handlers.dialog;

import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Module;
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

    public void newModule(){
        Module module = new Module();
        ModuleData data = getDefaultModuleData(module);
//        return getGuiStage().getModuleDialog().addModule(data);
    }

    private ModuleData getDefaultModuleData(Module module) {
        return new ModuleData(new LE_Module(module));
    }

    public String textInput(String tip, String text) {
        return GdxDialogMaster.inputText(tip, text);
    }

}
