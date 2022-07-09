package libgdx.stage.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.exploration.story.cinematic.Cinematics;
import eidolons.system.options.OptionsMaster;
import libgdx.bf.GridMaster;
import libgdx.stage.camera.generic.CameraMotion;
import libgdx.stage.camera.generic.CameraOptions;
import libgdx.stage.camera.generic.MotionData;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LOG_CHANNEL;

import java.util.*;

import static main.system.GuiEventType.*;
import static main.system.auxiliary.log.LogMaster.devLog;

public class CameraMan {

    private Camera cam;
    private final Runnable cameraZoomChangedCallback;
    private final DragCamControl dragController;
    private final BorderCamControl borderController;
    private final ZoomCamControl zoomController;
    private final PanCamControl panCamControl;
    List<CamControl> controls = new ArrayList<>(4);

    private final Set<CameraMotion> motions = new LinkedHashSet<>();
    protected float width,height,halfWidth,halfHeight;


    public CameraMan(Camera cam, Runnable cameraZoomChangedCallback) {
        this.cam = cam;
        this.cameraZoomChangedCallback = cameraZoomChangedCallback;

        controls.add(dragController = new DragCamControl(this));
        controls.add(borderController = new BorderCamControl(this));
        controls.add(panCamControl = new PanCamControl(this));
        controls.add(zoomController = new ZoomCamControl(this));

        CameraOptions.update(OptionsMaster.getControlOptions());

        bindEvents();
    }

    private void bindEvents() {
        GuiEventManager.bind(CAMERA_ZOOM, param -> {
            MotionData data = (MotionData) param.get();
            zoomController.zoom(data);
        });
        GuiEventManager.bind(CAMERA_OFFSET, p -> {
            Vector2 v;
            if (p.get() instanceof Coordinates) {
                v = GridMaster.getCenteredPos((Coordinates) p.get());
            } else {
                v = (Vector2) p.get();
            }
            float x = getCam().position.x;
            float y = getCam().position.y;
            getCam().position.set(x + v.x, y + v.y, 0);

        });
        GuiEventManager.bind(CAMERA_SET_TO, p -> {
            getCam().position.set((Vector2) p.get(), 0);
        });
//        GuiEventManager.bind(CAMERA_PAN_TO, param -> {
//            if (param.get() instanceof MotionData) {
//                cameraPan((MotionData) param.get());
//            } else
//                cameraPan(new MotionData(param.get()));
//        });
//        GuiEventManager.bind(CAMERA_PAN_TO_COORDINATE, param -> {
//            if (param.get() instanceof MotionData) {
//                cameraPan((MotionData) param.get());
//            } else
//                cameraPan(new MotionData(param.get()));
//        });
//        GuiEventManager.bind(CAMERA_PAN_TO_UNIT, param -> {
//            if (param.get() instanceof MotionData) {
//                cameraPan((MotionData) param.get());
//            } else
//                cameraPan(new MotionData(param.get()));
//        });
    }

    public void drag(int screenX, int screenY) {
        dragController.drag(screenX, screenY);
    }

    public void zoom(float zoom) {
        zoomController.zoom(zoom);
    }

    public void act(float delta) {
        doMotions(delta);
        for (CamControl control : controls) {
            control.act(delta);
        }
        CameraMotion motion = borderController.getMotion();
        if (motion != null)
            motions.add(motion);
    }
    private void doMotions(float delta) {
        for (CameraMotion motion : new ArrayList<>(motions)) {
            if (!motion.act(delta)) {
                motions.remove(motion);
            } else {
                cameraZoomChangedCallback.run();
            }
        }
    }

    public void cameraChanged() {
        cameraZoomChangedCallback.run();
    }

    public void centerCam(float x, float y) {
        getCam().position.x = x;
        getCam().position.x = y;
    }


    //TODO
    public void centerCameraOnMainHero() {
    }


    //TODO
    public void unitActive(BattleFieldObject hero) {
    }

    public void cameraStop(boolean fullstop) {
        if (fullstop) {
            //            lastPos = new Vector3(getCam().position);
        }
        motions.clear();
    }


    @Deprecated
    public void centerCameraOn(BattleFieldObject hero) {
    }

    @Deprecated
    public void centerCameraOn(BattleFieldObject hero, Boolean force) {

    }

    public Vector2 getCameraCenter() {
        return new Vector2(getCam().position.x, getCam().position.y);
    }

    public Camera getCam() {
        return cam;
    }

    public void setWidth(float width) {
        this.width = width;
        this.halfWidth = width/2;
    }

    public float getWidth() {
        return width;
    }

    public void setHeight(float height) {
        this.height = height;
        this.halfHeight = height/2;
    }

    public float getHeight() {
        return height;
    }
}
