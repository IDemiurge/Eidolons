package eidolons.libgdx.stage.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GDX;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.utils.ActTimer;
import eidolons.system.hotkey.DC_KeyManager;
import eidolons.system.options.ControlOptions;
import eidolons.system.options.OptionsMaster;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.secondary.Bools;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

import static main.system.GuiEventType.*;

/**
 * smooth camera movement
 */
public class CameraMan {
    protected static boolean cameraAutoCenteringOn = OptionsMaster.getControlOptions().
            getBooleanValue(ControlOptions.CONTROL_OPTION.AUTO_CENTER_CAMERA_ON_HERO);

    private static boolean centerCameraOnAlliesOnly = OptionsMaster.getControlOptions().
            getBooleanValue(ControlOptions.CONTROL_OPTION.CENTER_CAMERA_ON_ALLIES_ONLY);
    private boolean firstCenteringDone;
    private Boolean centerCameraAlways;
    private static Float cameraPanMod;
    private final GameScreen screen;
    OrthographicCamera cam;

    private FloatAction zoomAction;
    private List<CameraMotion> motions = new ArrayList<>();
    private ActTimer cameraTimer;

    public static class MotionData {

        public Vector2 dest;
        public float duration;
        public Interpolation interpolation = Interpolation.fade;
        public Boolean exclusive = false;

        public MotionData(float f, float duration, Interpolation interpolation) {
            this(new Vector2(f, 0), duration, interpolation);
        }

        public MotionData(Vector2 dest, float duration, Interpolation interpolation) {
            this.dest = dest;
            this.duration = duration;
            this.interpolation = interpolation;
        }

        public MotionData(Object param) {
            duration = 0;
            if (param instanceof List) {
                List list = ((List) param);
                for (Object o : list) {
                    initParam(o);
                }
            } else {
                initParam(param);
            }
        }

        private void initParam(Object o) {
            if (o instanceof Boolean) {
                exclusive = (Boolean) o;
            }
            if (o instanceof Vector2) {
                dest = (Vector2) o;
            }
            if (o instanceof Coordinates) {
                dest = GridMaster.getCenteredPos((Coordinates) o);
            }
            if (o instanceof BattleFieldObject) {
                dest = GridMaster.getCenteredPos(((BattleFieldObject) o).getCoordinates());
            }
            if (o instanceof Interpolation) {
                interpolation = ((Interpolation) o);
            }
            if (o instanceof Float) {
                duration = (float) o;
            }
        }
    }

    public CameraMan(OrthographicCamera cam, GameScreen screen) {
        this.cam = cam;
        this.screen = screen;

        cameraTimer = new ActTimer(OptionsMaster.getControlOptions().
                getIntValue(ControlOptions.CONTROL_OPTION.CENTER_CAMERA_AFTER_TIME), () -> {
            if (!firstCenteringDone) {
                centerCameraOn(Eidolons.getMainHero());
                firstCenteringDone = true;
            }
            if (isCameraAutoCenteringOn())
                if (Eidolons.getGame().getManager().checkAutoCameraCenter()) {
                    if (!Eidolons.getMainHero().isDead()) //for Shade
                        centerCameraOn(Eidolons.getMainHero());
                }
        });


        GuiEventManager.bind(GuiEventType.CAMERA_OFFSET, p -> {
            Vector2 v;
            if (p.get() instanceof Coordinates) {
                v = GridMaster.getCenteredPos((Coordinates) p.get());
            } else {
                v= (Vector2) p.get();
            }
            float x=getCam().position.x;
            float y =getCam().position.y;
            getCam().position.set(x+v.x , y+v.y, 0);

        });
        GuiEventManager.bind(GuiEventType.CAMERA_SET_TO, p -> {
            getCam().position.set((Vector2) p.get(), 0);
        });
        GuiEventManager.bind(CAMERA_PAN_TO, param -> {
            if (param.get() instanceof MotionData) {
                cameraPan((MotionData) param.get());
            } else
                cameraPan(new MotionData(param.get()));
        });
        GuiEventManager.bind(CAMERA_PAN_TO_COORDINATE, param -> {
            if (param.get() instanceof MotionData) {
                cameraPan((MotionData) param.get());
            } else
            cameraPan(new MotionData(param.get()));
        });
        GuiEventManager.bind(CAMERA_PAN_TO_UNIT, param -> {
            if (param.get() instanceof MotionData) {
                cameraPan((MotionData) param.get());
            } else
            cameraPan(new MotionData(param.get()));
        });


        GuiEventManager.bind(CAMERA_ZOOM, param -> {
            MotionData data = (MotionData) param.get();
            zoomAction = (FloatAction) ActionMaster.getAction(FloatAction.class);
            zoomAction.setStart(getCam().zoom);
            zoomAction.setEnd(data.dest.x);
            zoomAction.setDuration(data.duration);
            zoomAction.setInterpolation(data.interpolation);
            main.system.auxiliary.log.LogMaster.dev("Zooming to " + data.dest.x);
//            main.system.auxiliary.log.LogMaster.dev("Zooming to " +data.dest.x);
        });
    }


    public void act(float delta) {
        if (Eidolons.getGame().isPaused())
            return;
        cameraTimer.act(delta);
        doMotions(delta);
        if (zoomAction != null) {
            if (zoomAction.getValue() != zoomAction.getEnd()) {
                zoomAction.act(delta);
//                main.system.auxiliary.log.LogMaster.dev("Zoom from " +
//                        getCam().zoom + " to " + zoomAction.getValue());
                getCam().zoom = zoomAction.getValue();
                getController().cameraZoomChanged();
                getCam().update();
            }
        }
    }

    private void doMotions(float delta) {
        for (CameraMotion motion : new ArrayList<>(motions)) {
            if (!motion.act(delta)) {
                motions.remove(motion);
            } else {
                getController().cameraPosChanged();
            }
        }
    }

    private boolean isOldCamera() {
        return false;
    }

    public void centerCameraOnMainHero() {
        centerCameraOn(Eidolons.getMainHero(), true);
    }

    public static void setCameraPanMod(float mod) {
        cameraPanMod = mod;
    }

    protected boolean isCameraPanningOff() {
        return false; //TODO
    }

    private void cameraPan(MotionData motionData) {
        if (motionData.exclusive) {
            if (!motions.isEmpty()) {
                return;
            }
        }
        cameraPan(motionData.dest, motionData.duration, motionData.interpolation, null);

    }

    protected void cameraPan(Vector2 unitPosition, Boolean overrideCheck) {
        cameraPan(unitPosition, 0, Interpolation.bounce, overrideCheck);
    }

    protected void cameraPan(Vector2 destination, float duration, Interpolation interpolation, Boolean overrideCheck) {
        if (isCameraPanningOff()) {
            return;
        }
        if (destination == null) {
            return;
        }
        float dst = getCam().position.dst(destination.x, destination.y, 0f);// / getCameraDistanceFactor();

        if (overrideCheck == null)
            if (isCenterAlways()) {
                overrideCheck = !getController().isWithinCamera(destination.x, destination.y, 128, 128);
            } else
                overrideCheck = false;

        if (!overrideCheck && !Cinematics.ON)
            if (dst < getCameraMinCameraPanDist())
                return;


        if (duration == 0) {
            duration = 1 + dst / getPanSpeed();
        }
        motions.add(new CameraMotion(this, duration, destination, interpolation));

    }

    private float getPanSpeed() {
        return 450;
    }

    protected float getCameraMinCameraPanDist() {
        return (GDX.size(1600, 0.1f)) / 3 * getCameraPanMod(); //TODO if too close to the edge also
    }

    public InputController getController() {
        return screen.getController();
    }

    public void cameraStop(boolean fullstop) {
        if (fullstop) {
//            lastPos = new Vector3(getCam().position);
        }
        motions.clear();
        cameraTimer.reset();
    }

    public void centerCameraOn(BattleFieldObject hero) {
        centerCameraOn(hero, null);
    }

    public void centerCameraOn(BattleFieldObject hero, Boolean force) {
        if (!isCenterAlways()) //TODO refactor this shit
            if (!Bools.isTrue(force))
                if (centerCameraOnAlliesOnly)
                    if (!hero.isMine())
                        return;

        Vector2 unitPosition = GridMaster.getCenteredPos(hero.getCoordinates());
        cameraPan(unitPosition, force);


    }

    public static boolean isCameraAutoCenteringOn() {
        return cameraAutoCenteringOn;
    }

    public static void setCameraAutoCenteringOn(boolean b) {
        cameraAutoCenteringOn = b;
    }

    public static void setCenterCameraOnAlliesOnly(boolean b) {
        centerCameraOnAlliesOnly = b;
    }

    public void setCameraTimer(int intValue) {
        if (cameraTimer == null) {
            return;
        }
        cameraTimer.setPeriod(intValue);
    }

    public Boolean isCenterAlways() {
        if (centerCameraAlways == null) {
            centerCameraAlways = OptionsMaster.getControlOptions().getBooleanValue(ControlOptions.CONTROL_OPTION.ALWAYS_CAMERA_CENTER_ON_ACTIVE);
        }
        return centerCameraAlways;
    }

    public Vector2 getCameraCenter() {
        return new Vector2(getCam().position.x, getCam().position.y);
    }

    public static float getCameraPanMod() {
        if (cameraPanMod == null)
            cameraPanMod = new Float(OptionsMaster.getControlOptions().
                    getIntValue(ControlOptions.CONTROL_OPTION.CENTER_CAMERA_DISTANCE_MOD)) / 100;

        return cameraPanMod;
    }


    public OrthographicCamera getCam() {
        return cam;
    }

}
