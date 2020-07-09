package main.level_editor.gui.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.stage.camera.CameraMan;
import eidolons.libgdx.stage.camera.MotionData;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_CameraHandler extends LE_Handler {


    private static final int MODES = 2;
    private int cameraMode = 0;

    public LE_CameraHandler(LE_Manager manager) {
        super(manager);
        GuiEventManager.bind(GuiEventType.LE_CENTER_ON_SELECT, p -> centerOnSelection());
//        GuiEventManager.bind(GuiEventType. , p-> centerOnSelection());

    }

    @Override
    public void afterLoaded() {
        Module m = LevelEditor.getCurrent().getDefaultModule();
        Gdx.app.postRunnable(() ->
                centerModule(m)
        );
        GuiEventManager.trigger(GuiEventType.CAMERA_ZOOM, new MotionData(2.3f, 1f, null));
    }

    public void centerOnSelection() {
        if (getSelectionHandler().getSelection().getIds().size() == 1) {
            Obj obj = getIdManager().getObjectById(getSelectionHandler().getSelection().getIds().iterator().next());
            GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_COORDINATE, obj.getCoordinates());
        }
    }

    public void centerModule(Module module) {

    }

    public void cycleCameraMode() {
        cameraMode++;
        if (cameraMode >= MODES)
            cameraMode = 0;
        setCameraMode(cameraMode);
    }

    public void panToEdge(DIRECTION d) {
        Coordinates c = getFloorWrapper().getEdge(d);
        Vector2 v = GridMaster.getCenteredPos(c);
        MotionData data = new MotionData(v, 0.5f, null);
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_COORDINATE, data);

    }
    private void setCameraMode(int cameraMode) {
        switch (cameraMode) {
            case 0:
                getCameraMan().maxZoom();
                break;
            case 1:
                getCameraMan().defaultZoom();
                break;
            case 2:
                getCameraMan().centerCam();
                break;
            case 3:
                centerOnSelection();
                break;
            case 4:

        }
    }


    private CameraMan getCameraMan() {
        return ScreenMaster.getScreen().getCameraMan();
    }

}
