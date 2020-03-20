package main.level_editor.gui.grid;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.entity.obj.Obj;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_CameraHandler extends LE_Handler {


    public LE_CameraHandler(LE_Manager manager) {
        super(manager);
        GuiEventManager.bind(GuiEventType.LE_CENTER_ON_SELECT , p-> centerOnSelection());
//        GuiEventManager.bind(GuiEventType. , p-> centerOnSelection());

    }

    public void centerOnSelection(){
        if (getSelectionHandler().getSelection().getIds().size()==1) {
            Obj obj = getIdManager().getObjectById(getSelectionHandler().getSelection().getIds().iterator().next());
            GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_COORDINATE, obj.getCoordinates());
        }
    }

    public void centerModule(Module module) {

    }
}
