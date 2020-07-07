package eidolons.libgdx.stage.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.module.cinematic.Cinematics;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.utils.ActTimer;
import eidolons.system.options.ControlOptions;
import eidolons.system.options.OptionsMaster;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.secondary.Bools;
import main.system.threading.WaitMaster;

import java.util.*;

import static eidolons.libgdx.stage.camera.CameraOptions.options;
import static main.system.GuiEventType.*;
import static main.system.auxiliary.log.LogMaster.devLog;

/**
 * smooth camera movement
 */
public class CameraMan {

    private final CamController camController = new CamController(this);

    private boolean firstCenteringDone;
    private static Float cameraPanMod;
    private final GameScreen screen;
    OrthographicCamera cam;

    private FloatAction zoomAction;
    private final Set<CameraMotion> motions = new LinkedHashSet<>();
    private final ActTimer cameraTimer;
    private boolean mustFinish;
    Map<DIRECTION, CameraMotion> moveMap = new HashMap<>();

    private BattleFieldObject pendingPanTarget;


    public void unitActive(BattleFieldObject hero) {
        devLog(LOG_CHANNEL.CAMERA, "Request pan camera to active unit" + hero);
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
        if (!options.AUTO_CAMERA_OFF)
            WaitMaster.doAfterWait(time, () -> {
                if (hero.getGame().getManager().getActiveObj() == hero) {
                    motions.clear();
                    centerCameraOn(hero);
                    pendingPanTarget = null;
                    //                mustFinish=true;
                    devLog(LOG_CHANNEL.CAMERA, "Panning camera to active unit" + hero);
                }
            });
    }

    public void defaultZoom() {
        getCam().zoom = 1;
    }

    public void maxZoom() {
        screen.getController().maxZoom();
    }


    public void centerCam() {
        Unit mainHero = Eidolons.getMainHero();
        if (mainHero != null) {
            centerCameraOnMainHero();
        } else
            screen.getController().centerCam();
    }


    public CameraMan(OrthographicCamera cam, GameScreen screen) {
        this.cam = cam;
        this.screen = screen;
        CameraOptions.update(OptionsMaster.getControlOptions());
        cameraTimer = new ActTimer(options.CENTER_CAMERA_AFTER_TIME, () -> {
            if (!firstCenteringDone) {
                centerCameraOn(Eidolons.getMainHero());
                firstCenteringDone = true;
            }
            if (!options.AUTO_CAMERA_OFF)
                if (Eidolons.getGame().getManager().checkAutoCameraCenter()) {
                    if (!Eidolons.getMainHero().isDead()) //for Shade
                        centerCameraOn(Eidolons.getMainHero());
                }
        });

        GuiEventManager.bind(CAMERA_FOLLOW_OFF, p -> {
            camController.follow(null);
        });
        GuiEventManager.bind(GuiEventType.CAMERA_FOLLOW_MAIN, p -> {
            camController.follow(Eidolons.getMainHero());
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
            devLog("Zooming to " + data.zoom);
        });
    }

    private void follow(Unit unit) {
        camController.follow(unit);
    }


    public void act(float delta) {
        // cameraTimer.setPeriod(options.CENTER_CAMERA_AFTER_TIME);
        // cameraTimer.act(delta);
        // gdx review - is this a useful feature?
        if (!Eidolons.getGame().isPaused())
            if (!Eidolons.getGame().isDebugMode())
                if (motions.isEmpty()) {
                    if (camController.checkFollow(delta))
                        return;
                }

        doMotions(delta);
        if (zoomAction != null) {
            if (zoomAction.getTime()<zoomAction.getDuration()) {
                zoomAction.act(delta);
                getCam().zoom = zoomAction.getValue();
                getController().cameraZoomChanged();
                getCam().update();
            }
        }
        if (camController.isArrowMotionsOn()) {
            for (int key : CamController.keys) {
                if (Gdx.input.isKeyPressed(key)) {
                    camController.keyDown(key, delta);
                }
            }
        }
        if (camController.isBorderMouseMotionsOn()) {
            DIRECTION mouseBorder = screen.getController().getMouseBorder();
            if (mouseBorder != null) {
                switch (mouseBorder) {
                    case UP:
                        camController.keyDown(Input.Keys.UP, delta * 100);
                        break;
                    case DOWN:
                        camController.keyDown(Input.Keys.DOWN, delta * 100);
                        break;
                    case LEFT:
                        camController.keyDown(Input.Keys.LEFT, delta * 100);
                        break;
                    case RIGHT:
                        camController.keyDown(Input.Keys.RIGHT, delta * 100);
                        break;
                }
            }
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
                devLog(LOG_CHANNEL.CAMERA, "cleared pan motions! ");
                motions.clear();
            } else {
                devLog(LOG_CHANNEL.CAMERA, "mustFinish pan motions! ");
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
        // devLog("cameraPan to " + destination);
        float dst = getCam().position.dst(destination.x, destination.y, 0f);// / getCameraDistanceFactor();

        if (overrideCheck == null)
            if (options.CAMERA_ON_ACTIVE) {
                overrideCheck = !getController().isWithinCamera(destination.x, destination.y, 128, 128);
            } else
                overrideCheck = false;

        if (!overrideCheck && !Cinematics.ON)  //&& !EidolonsGame.DUEL
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
        return 200 * options.CENTER_CAMERA_DISTANCE_MOD; //TODO if too close to the edge also
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
        if (!options.CAMERA_ON_ACTIVE) //TODO refactor this shit
            if (!Bools.isTrue(force))
                if (!hero.isMine())
                    return;

        Vector2 unitPosition = GridMaster.getCenteredPos(hero.getCoordinates());
        cameraPan(unitPosition, force);


    }

    //TODO
    public void setCameraTimer(int intValue) {
        if (cameraTimer == null) {
            return;
        }
        cameraTimer.setPeriod(intValue);
    }

    public Vector2 getCameraCenter() {
        return new Vector2(getCam().position.x, getCam().position.y);
    }


    public OrthographicCamera getCam() {
        return cam;
    }

}
