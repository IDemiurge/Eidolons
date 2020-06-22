package eidolons.libgdx.stage.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.libgdx.bf.grid.cell.UnitGridView;
import eidolons.libgdx.bf.grid.moving.PlatformCell;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.utils.ActTimer;
import eidolons.system.options.ControlOptions;
import eidolons.system.options.OptionsMaster;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

import java.util.*;

import static main.system.GuiEventType.*;

/**
 * smooth camera movement
 */
public class CameraMan {
    protected static boolean cameraAutoCenteringOn = OptionsMaster.getControlOptions().
            getBooleanValue(ControlOptions.CONTROL_OPTION.CAMERA_ON_HERO);

    private boolean firstCenteringDone;
    private Boolean centerCameraAlways;
    private static Float cameraPanMod;
    private final GameScreen screen;
    OrthographicCamera cam;

    private FloatAction zoomAction;
    private final Set<CameraMotion> motions = new LinkedHashSet<>();
    private final ActTimer cameraTimer;
    private boolean mustFinish;
    private BattleFieldObject pendingPanTarget;

    public static final int[] keys = {
            Input.Keys.UP, Input.Keys.W
            , Input.Keys.LEFT, Input.Keys.A
            , Input.Keys.RIGHT, Input.Keys.D
            , Input.Keys.DOWN, Input.Keys.S
    };

    BattleFieldObject followObj;

    public void defaultZoom() {
        getCam().zoom = 1;
    }

    public void centerCam() {
        Unit mainHero = Eidolons.getMainHero();
        if (mainHero != null) {
            centerCameraOnMainHero();
        } else
            screen.getController().centerCam();
    }

    public void maxZoom() {
        screen.getController().maxZoom();
    }


    public static class MotionData {

        public Vector2 dest;
        public float duration;
        public float zoom;
        public Interpolation interpolation = Interpolation.fade;
        public Boolean exclusive = false;

        public MotionData(float zoom, float duration, Interpolation interpolation) {
            this(duration, interpolation, zoom);
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
                if (duration == 0) {
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

        GuiEventManager.bind(CAMERA_FOLLOW_OFF, p -> {
            follow(null);
        });
        GuiEventManager.bind(GuiEventType.CAMERA_FOLLOW_MAIN, p -> {
            follow(Eidolons.getMainHero());
        });

        GuiEventManager.bind(GuiEventType.CAMERA_OFFSET, p -> {
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
            if (data.duration <= 0) {
                data.duration = Math.abs(cam.zoom - data.zoom) * 15;
            }
            zoomAction.setDuration(data.duration);
            zoomAction.setInterpolation(data.interpolation);
            main.system.auxiliary.log.LogMaster.dev("Zooming to " + data.zoom);
        });
    }

    private void follow(Unit unit) {
        followObj = unit;
        if (followObj == null) {
            centerCam();
        } else {
            centerCameraOn(followObj);
        }
    }


    public void act(float delta) {
        if (Eidolons.getGame().isPaused())
            return;
        // TODO is there a use for it?
        //        cameraTimer.act(delta);
        if (!Eidolons.getGame().isDebugMode())
            if (motions.isEmpty())
                if (followObj != null) {
                    BaseView baseView = ScreenMaster.getGrid().getViewMap().get(followObj);
                    if (baseView instanceof UnitGridView) {
                        if (baseView.getParent() instanceof PlatformCell)
                            if (((UnitGridView) baseView).getPlatformController() != null) {
                                Vector2 v = new Vector2(baseView.getX(), baseView.getY());
                                v = baseView.localToStageCoordinates(v);
                                // baseView.localToParentCoordinates(v);
                                getCam().position.x = v.x;
                                getCam().position.y = v.y;
                                if (RandomWizard.chance(1)) {
                                    main.system.auxiliary.log.LogMaster.dev(LOG_CHANNEL.CAMERA, " Following: " + v);
                                }
                                getController().cameraPosChanged();
                                return;
                            }
                    }
                }

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
        if (isArrowMotionsOn()) {
            for (int key : keys) {
                if (Gdx.input.isKeyPressed(key)) {
                    keyDown(key, delta);
                }
            }
        }
        if (isBorderMouseMotionsOn()) {
            DIRECTION mouseBorder = screen.getController().getMouseBorder();
            if (mouseBorder != null) {
                switch (mouseBorder) {
                    case UP:
                        keyDown(Input.Keys.UP, delta * 100);
                        break;
                    case DOWN:
                        keyDown(Input.Keys.DOWN, delta * 100);
                        break;
                    case LEFT:
                        keyDown(Input.Keys.LEFT, delta * 100);
                        break;
                    case RIGHT:
                        keyDown(Input.Keys.RIGHT, delta * 100);
                        break;
                }
            }
        }
    }

    private boolean isBorderMouseMotionsOn() {
        //        return !CoreEngine.isLevelEditor();//TODO options
        return false;
    }


    private boolean isArrowMotionsOn() {
        return false;
    }

    Map<DIRECTION, CameraMotion> moveMap = new HashMap<>();

    public void keyDown(int keyCode, float delta) {
        //        boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ||
        //                Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
        //        boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
        //                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        //        if (!ctrl && !alt)
        switch (keyCode) {
            case Input.Keys.UP:
            case Input.Keys.W:
                move(DIRECTION.UP, delta);
                return;
            case Input.Keys.LEFT:
            case Input.Keys.A:
                move(DIRECTION.LEFT, delta);
                return;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                move(DIRECTION.RIGHT, delta);
                return;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                move(DIRECTION.DOWN, delta);
                return;
        }
    }

    public void move(DIRECTION direction, float delta) {

        float xDiff = 0;
        float yDiff = 0;
        float step = 200 * cam.zoom * cam.zoom * delta;
        switch (direction) {
            case UP:
                yDiff = step;
                break;
            case DOWN:
                yDiff = -step;
                break;
            case LEFT:
                xDiff = -step;
                break;
            case RIGHT:
                xDiff = step;
                break;
        }
        CameraMotion motion = moveMap.get(direction);
        float x = 0;
        float y = 0;
        x = cam.position.x;
        y = cam.position.y;
        if (motion == null) {
            moveMap.put(direction, motion = new CameraMotion(this, 0.1f, new Vector2(x, y), Interpolation.fade));
        } else {
            motion.reset(0.1f);
        }
        motion.getSpeedActionX().setStart(x);
        motion.getSpeedActionY().setStart(y);
        motion.getSpeedActionX().setEnd(x + xDiff);
        motion.getSpeedActionY().setEnd(y + yDiff);
        motions.add(motion);
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
            mustFinish = false;
        }
        if (!motions.isEmpty()) {
            if (!mustFinish || motionData.exclusive) {
                main.system.auxiliary.log.LogMaster.dev(LOG_CHANNEL.CAMERA, "cleared pan motions! ");
                motions.clear();
            } else {
                main.system.auxiliary.log.LogMaster.dev(LOG_CHANNEL.CAMERA, "mustFinish pan motions! ");
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
            destination.y = destination.y - 210;
        } else {
            //            destination.y= destination.y+100;
        }
        main.system.auxiliary.log.LogMaster.dev("cameraPan to " + destination);
        float dst = getCam().position.dst(destination.x, destination.y, 0f);// / getCameraDistanceFactor();

        if (overrideCheck == null)
            if (isCenterAlways()) {
                overrideCheck = !getController().isWithinCamera(destination.x, destination.y, 128, 128);
            } else
                overrideCheck = false;

        if (!overrideCheck && !Cinematics.ON)  //&& !EidolonsGame.DUEL
            if (dst < getCameraMinCameraPanDist())
                return;


        if (duration == 0) {
            duration = 1 + dst / getPanSpeed();
        }
        if (CoreEngine.TEST_LAUNCH) {
            duration = 0.1f;
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
        main.system.auxiliary.log.LogMaster.dev(LOG_CHANNEL.CAMERA, "Request pan camera to active unit" + hero);
        if (pendingPanTarget == hero) {
            return;
        }
        pendingPanTarget = hero;
        int time = 1500;
        if (AnimMaster.getInstance().isDrawingPlayer()) {
            time = 2200;
        }
        if (hero.isPlayerCharacter()) {
            time = 1000;
        }
        if (isCameraAutoCenteringOn())
            WaitMaster.doAfterWait(time, () -> {
                if (hero.getGame().getManager().getActiveObj() == hero) {
                    motions.clear();
                    centerCameraOn(hero);
                    pendingPanTarget = null;
                    //                mustFinish=true;
                    main.system.auxiliary.log.LogMaster.dev(LOG_CHANNEL.CAMERA, "Panning camera to active unit" + hero);

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
        //        return cameraAutoCenteringOn;
        return false;
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
            centerCameraAlways = OptionsMaster.getControlOptions().getBooleanValue(ControlOptions.CONTROL_OPTION.CAMERA_ON_ACTIVE);
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
