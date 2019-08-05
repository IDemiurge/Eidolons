package eidolons.libgdx.stage.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GDX;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.stage.GuiStage;
import eidolons.libgdx.utils.ActTimer;
import eidolons.system.options.ControlOptions;
import eidolons.system.options.OptionsMaster;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.auxiliary.secondary.Bools;

import java.util.List;

import static main.system.GuiEventType.CAMERA_PAN_TO_COORDINATE;
import static main.system.GuiEventType.CAMERA_PAN_TO_UNIT;

/**
 * smooth camera movement
 */
public class CameraMan {
    public static final float MAX_CAM_DST = 500;
    protected static boolean cameraAutoCenteringOn = OptionsMaster.getControlOptions().
            getBooleanValue(ControlOptions.CONTROL_OPTION.AUTO_CENTER_CAMERA_ON_HERO);

    private static boolean centerCameraOnAlliesOnly = OptionsMaster.getControlOptions().
            getBooleanValue(ControlOptions.CONTROL_OPTION.CENTER_CAMERA_ON_ALLIES_ONLY);
    private static Float cameraPanMod;
    private final GameScreen screen;
    private Boolean centerCameraAlways;
    boolean firstCenteringDone;
    protected Vector2 cameraDestination;
    protected Vector2 velocity;
    private Vector3 lastPos;

    private ActTimer cameraTimer;
    OrthographicCamera cam;
    private Float cameraSpeedFactor;

//    List<CameraMove> moves;


    public CameraMan(OrthographicCamera cam, GameScreen screen) {
        this.cam = cam;
        this.screen = screen;

        GuiEventManager.bind(CAMERA_PAN_TO_COORDINATE, param -> {
            cameraSpeedFactor=null;
            Coordinates c=null ;
            if (param.get() instanceof List) {
                c = (Coordinates) ((List) param.get()).get(0);
                cameraSpeedFactor = (Float) ((List) param.get()).get(1);
            } else {
                c = (Coordinates) param.get();
            }
            Vector2 v = GridMaster.getCenteredPos(c);
            cameraPan(v, true);

        });
        GuiEventManager.bind(CAMERA_PAN_TO_UNIT, param -> {
            cameraSpeedFactor=null;
            BattleFieldObject c=null ;
            if (param.get() instanceof List) {
                c = (BattleFieldObject) ((List) param.get()).get(0);
                cameraSpeedFactor = (Float) ((List) param.get()).get(1);
            } else {
                c = (BattleFieldObject) param.get();
            }
            centerCameraOn(c);
        });


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
    }

    public void act(float delta) {
        cameraTimer.act(delta);
        cameraShift();
    }

    public void cameraStop() {
    }


    protected float getCameraDistanceFactor() {
        if (cameraSpeedFactor==null) {

        }
        return 6.25f;
    }

    //    protected float getCameraDistanceFactor() {
//        return 8f;
//    }
    public void centerCameraOnMainHero() {
        centerCameraOn(Eidolons.getMainHero(), true);
    }

    public static void setCameraPanMod(float mod) {
        cameraPanMod = mod;
    }

    protected void cameraPan(Vector2 unitPosition) {
        cameraPan(unitPosition, null);
    }

    protected boolean isCameraPanningOff() {
        return false; //TODO
    }

    protected void cameraPan(Vector2 unitPosition, Boolean overrideCheck) {
        if (isCameraPanningOff()) {
            return;
        }
        this.cameraDestination = unitPosition;
        float dst = getCam().position.dst(unitPosition.x, unitPosition.y, 0f);// / getCameraDistanceFactor();

        if (overrideCheck == null)
            if (isCenterAlways()) {
                overrideCheck = !getController().isWithinCamera(unitPosition.x, unitPosition.y, 128, 128);
            } else overrideCheck = false;

        if (!overrideCheck)
            if (dst < getCameraMinCameraPanDist())
                return;

        velocity =getPanVelocity(dst, getCameraDistanceFactor());

    }
    private Vector2 getPanVelocity(float max, float factor) {
        float dist = getCam().position.dst(cameraDestination.x, cameraDestination.y, 0f);
        float dest = Math.min(max,
                dist/ factor);
        return new Vector2(cameraDestination.x - getCam().position.x, cameraDestination.y
                - getCam().position.y).
                nor().scl( dest);
//        return new Vector2(cameraDestination.x - getCam().position.x, cameraDestination.y
//                - getCam().position.y).
//                nor().scl(Math.min(getCam().position.
//                dst(cameraDestination.x, cameraDestination.y, 0f), dest));
    }

    protected float getCameraMinCameraPanDist() {
        return (GDX.size(1600, 0.1f)) / 3 * getCameraPanMod(); //TODO if too close to the edge also
    }

    protected void cameraShift() {
        if (cameraDestination != null)
            if (getCam() != null && velocity != null && !velocity.isZero()) {
                float x = velocity.x > 0
                        ? Math.min(cameraDestination.x, getCam().position.x + velocity.x * Gdx.graphics.getDeltaTime())
                        : Math.max(cameraDestination.x, getCam().position.x + velocity.x * Gdx.graphics.getDeltaTime());
                float y = velocity.y > 0
                        ? Math.min(cameraDestination.y, getCam().position.y + velocity.y * Gdx.graphics.getDeltaTime())
                        : Math.max(cameraDestination.y, getCam().position.y + velocity.y * Gdx.graphics.getDeltaTime());

//                main.system.auxiliary.log.LogMaster.log(1,"cameraShift to "+ y+ ":" +x + " = "+cam);
                getCam().position.set(x, y, 0f);

                Vector2 velocityNow = getPanVelocity(MAX_CAM_DST, getCameraDistanceFactor());

                if (velocityNow.isZero() || velocity.hasOppositeDirection(velocityNow)) {
                    cameraStop(velocityNow.isZero());
                }
                getCam().update();
                getController().cameraChanged();
            }
        checkCameraFix();
    }

    public InputController getController() {
        return screen.getController();
    }

    private void checkCameraFix() {
        if (!velocity.isZero())
            if (!cameraDestination.isZero())
                return;

        if (cameraDestination.x == getCam().position.x || cameraDestination.y == getCam().position.y)
            lastPos = new Vector3(getCam().position);
        else if (lastPos != null)
            if (getCam().position.dst(lastPos) > MAX_CAM_DST) {
                getCam().position.set(lastPos);
            }
        if (velocity.isZero())
            if (!cameraDestination.isZero()) {
                lastPos = new Vector3(getCam().position);
            }
    }

    public void cameraStop(boolean fullstop) {
        if (velocity != null || fullstop) {
            velocity.setZero();
            // TODO abruptly?
            cameraDestination.set(getCam().position.x, getCam().position.y);
        }
        if (fullstop) {
            lastPos = new Vector3(getCam().position);
        }

        cameraTimer.reset();
    }

    public void centerCameraOn(BattleFieldObject hero) {
        centerCameraOn(hero, null);
    }

    public void centerCameraOn(BattleFieldObject hero, Boolean force) {
        if (!isCenterAlways())
            if (!Bools.isTrue(force))
                if (centerCameraOnAlliesOnly)
                    if (!hero.isMine())
                        return;

        Coordinates coordinatesActiveObj =
                hero.getCoordinates();
        Vector2 unitPosition = new Vector2(coordinatesActiveObj.x * GridMaster.CELL_W +
                GridMaster.CELL_W / 2,
                (coordinatesActiveObj.invertY().y) * GridMaster.CELL_H - GridMaster.CELL_H / 2);
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
