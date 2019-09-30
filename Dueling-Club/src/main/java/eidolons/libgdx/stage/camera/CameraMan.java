package eidolons.libgdx.stage.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.utils.ActTimer;
import eidolons.system.options.ControlOptions;
import eidolons.system.options.OptionsMaster;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.secondary.Bools;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;

import static main.system.GuiEventType.*;

/**
 * smooth camera movement
 */
public class CameraMan {
    protected static boolean cameraAutoCenteringOn = OptionsMaster.getControlOptions().
            getBooleanValue(ControlOptions.CONTROL_OPTION.AUTO_CENTER_CAMERA_ON_HERO);

    private boolean firstCenteringDone;
    private Boolean centerCameraAlways;
    private static Float cameraPanMod;
    private final GameScreen screen;
    OrthographicCamera cam;

    private FloatAction zoomAction;
    private List<CameraMotion> motions = new ArrayList<>();
    private ActTimer cameraTimer;
    private boolean mustFinish;
    private BattleFieldObject pendingPanTarget;


    public static class MotionData {

        public Vector2 dest;
        public float duration;
        public float zoom;
        public Interpolation interpolation = Interpolation.fade;
        public Boolean exclusive = false;

        public MotionData(float zoom, float duration, Interpolation interpolation) {
            this(  duration, interpolation, zoom);
        }

        public MotionData(Vector2 dest, float duration, Interpolation interpolation) {
            this.dest = dest;
            this.duration = duration;
            this.interpolation = interpolation;
        }

        public MotionData(Object... params) {
            duration = 0;
            for (Object param : params) {
            if (param instanceof List) {
                List list = ((List) param);
                for (Object o : list) {
                    initParam(o);
                }
            } else {
                initParam(param);
            }
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
                if (duration==0) {
                duration = (float) o;
                } else zoom = (float) o;
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
            zoomAction.setEnd(data.zoom);
            if (data.duration<=0) {
                data.duration= Math.abs(cam.zoom - data.zoom)*15;
            }
            zoomAction.setDuration(data.duration);
            zoomAction.setInterpolation(data.interpolation);
            main.system.auxiliary.log.LogMaster.dev("Zooming to " + data.zoom);
        });
    }


    public void act(float delta) {
        if (Eidolons.getGame().isPaused())
            return;
        // TODO is there a use for it?
//        cameraTimer.act(delta);
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
        return OptionsMaster.getControlOptions().getBooleanValue(ControlOptions.CONTROL_OPTION.AUTO_CAMERA_OFF); //TODO
    }

    private void cameraPan(MotionData motionData) {
//        if (motionData.exclusive)
        if (motions.isEmpty()) {
            mustFinish=false;
        }
            if (!motions.isEmpty()) {
                if (!mustFinish || motionData.exclusive)
                {
                    main.system.auxiliary.log.LogMaster.dev("cleared pan motions! " );
                    motions.clear();
                }
                else {
                    main.system.auxiliary.log.LogMaster.dev("mustFinish pan motions! " );
                    return;
                }
//                return;
        }
        cameraPan(motionData.dest, motionData.duration, motionData.interpolation, null);

    }

    protected void cameraPan(Vector2 unitPosition, Boolean overrideCheck) {
        cameraPan(unitPosition, 0, Interpolation.fade, overrideCheck);
    }

    protected void cameraPan(Vector2 destination, float duration, Interpolation interpolation, Boolean overrideCheck) {
        if (!Cinematics.ON)
        if (isCameraPanningOff()) {
            return;
        }
        if (destination == null) {
            return;
        }
        if (Cinematics.ON) {
            destination.y= destination.y-210;
        } else {
//            destination.y= destination.y+100;
        }
        main.system.auxiliary.log.LogMaster.dev("cameraPan to " +destination);
        float dst = getCam().position.dst(destination.x, destination.y, 0f);// / getCameraDistanceFactor();

        if (overrideCheck == null)
            if (isCenterAlways()) {
                overrideCheck = !getController().isWithinCamera(destination.x, destination.y, 128, 128);
            } else
                overrideCheck = false;

        if (!overrideCheck && !Cinematics.ON&& !EidolonsGame.DUEL)
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
        return 200 * getCameraPanMod(); //TODO if too close to the edge also
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

    public void unitActive(BattleFieldObject hero) {
        main.system.auxiliary.log.LogMaster.dev("Request pan camera to active unit" +hero);
        if (pendingPanTarget==hero){
            return ;
        }
        pendingPanTarget = hero;
        int time = 1500;
        if (AnimMaster.getInstance().isDrawingPlayer()){
            time = 2200;
        }
        if (hero.isPlayerCharacter()) {
            time = 1000;
        }
        WaitMaster.doAfterWait(time, ()->{
            if (hero.getGame().getManager().getActiveObj()==hero) {
                motions.clear();
                centerCameraOn(hero);
                pendingPanTarget=null;
//                mustFinish=true;
                main.system.auxiliary.log.LogMaster.dev("Panning camera to active unit" +hero);

            }
        });
    }
    public void centerCameraOn(BattleFieldObject hero) {
        centerCameraOn(hero, null);
    }

    public void centerCameraOn(BattleFieldObject hero, Boolean force) {
        if (!isCenterAlways()) //TODO refactor this shit
            if (!Bools.isTrue(force))
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
