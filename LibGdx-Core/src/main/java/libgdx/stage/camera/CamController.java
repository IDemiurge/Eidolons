package libgdx.stage.camera;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import libgdx.bf.grid.cell.BaseView;
import libgdx.bf.grid.cell.UnitGridView;
import libgdx.bf.grid.moving.PlatformCell;
import libgdx.screens.handlers.ScreenMaster;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.LOG_CHANNEL;

public class CamController {
    private final CameraMan cameraMan;
    public static final int[] keys = {
            Input.Keys.UP, Input.Keys.W
            , Input.Keys.LEFT, Input.Keys.A
            , Input.Keys.RIGHT, Input.Keys.D
            , Input.Keys.DOWN, Input.Keys.S
    };
    private BattleFieldObject followObj;

    public CamController(CameraMan cameraMan) {
        this.cameraMan = cameraMan;
    }

    void follow(Unit unit) {
        followObj = unit;
        if (followObj == null) {
            cameraMan.centerCam();
        } else {
            cameraMan.centerCameraOn(followObj);
        }
    }

    boolean isBorderMouseMotionsOn() {
        //        return !CoreEngine.isLevelEditor();//TODO options
        return false;
    }

    boolean isArrowMotionsOn() {
        return false;
    }

    public void keyDown(int keyCode, float delta) {
        switch (keyCode) {
            case Input.Keys.UP:
            case Input.Keys.W:
                cameraMan.move(DIRECTION.UP, delta);
                return;
            case Input.Keys.LEFT:
            case Input.Keys.A:
                cameraMan.move(DIRECTION.LEFT, delta);
                return;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                cameraMan.move(DIRECTION.RIGHT, delta);
                return;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                cameraMan.move(DIRECTION.DOWN, delta);
                return;
        }
    }

    public boolean checkFollow(float delta) { //TODO make it gradual !
        if (followObj != null) {
            BaseView baseView = ScreenMaster.getGrid().getViewMap().get(followObj);
            if (baseView instanceof UnitGridView) {
                if (baseView.getParent() instanceof PlatformCell)
                    if (((UnitGridView) baseView).getPlatformController() != null) {
                        Vector2 v = new Vector2(baseView.getParent().getX(), baseView.getParent().getY());
                        // v = baseView.localToStageCoordinates(v);
                        // baseView.localToParentCoordinates(v);
                        getCam().position.x = v.x;
                        getCam().position.y = v.y;
                        if (RandomWizard.chance(1)) {
                            main.system.auxiliary.log.LogMaster.devLog(LOG_CHANNEL.CAMERA, " Following: " + v);
                        }
                        cameraMan.cameraChanged();
//                        cameraMan.getController().resetZoom();
                        return true;
                    }
            }
        }
        return false;
    }

    private Camera getCam() {
        return cameraMan.cam;
    }
}