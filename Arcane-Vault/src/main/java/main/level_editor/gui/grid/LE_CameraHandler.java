package main.level_editor.gui.grid;

import com.badlogic.gdx.Gdx;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.libgdx.stage.camera.CameraMan;
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

    @Override
    public void afterLoaded() {
        Module m = getModel().getModule();
        Gdx.app.postRunnable(()->
        centerModule(m)
        );
        GuiEventManager.trigger(GuiEventType.CAMERA_ZOOM, new CameraMan.MotionData(2.3f, 1f, null));
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
