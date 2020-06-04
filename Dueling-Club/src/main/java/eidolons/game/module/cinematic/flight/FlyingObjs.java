package eidolons.game.module.cinematic.flight;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.generic.Flippable;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.screens.ScreenMaster;
import main.data.filesys.PathFinder;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import static main.content.enums.GenericEnums.*;
import static main.system.auxiliary.log.LogMaster.log;

/*
sends certain kinds of actors flying in certain way for the FLIGHT CINEMATICS
start    - already in motion?
 */
public class FlyingObjs extends GroupX {

    private final float minDelay;
    private final float maxDelay;
    private final float angle;
    private final OrthographicCamera camera;
    private final int intensity;
    FLY_OBJ_TYPE type;
    CinematicPlatform platform; // don't want to collide with it right? Or is it ABOVE?..
    private float timer = 0;
    Set<FlyingObj> objects = new LinkedHashSet<>(); //might want to *STOP* them or change their ANGLE
    private final boolean cinematic;
    private boolean right;
    private boolean up;
    private float cam_x;
    private float cam_y;
    private int w;
    private int h;
    private float gridW;
    private float gridH;
    private boolean stopping;

    public FlyingObjs(FLY_OBJ_TYPE type, CinematicPlatform platform, int intensity, boolean cinematic) {
        this.type = type;
        this.platform = platform;
        this.cinematic = cinematic;
        this.minDelay = getMinDelay(type, intensity);
        this.maxDelay = getMaxDelay(type, intensity);
        //concurrent instances, distance, ...
        this.angle = platform.angle;
        this.intensity = intensity;
        camera = ScreenMaster.getScreen().getCamera();
    }

    private float getMaxDelay(FLY_OBJ_TYPE type, int intensity) {
        return 5 * 10 / new Float(intensity);
    }

    private float getMinDelay(FLY_OBJ_TYPE type, int intensity) {
        return 2 * 10 / new Float(intensity);
    }


    public void act(float delta) {
        if (!stopping)
            if (timer <= 0) {
                timer = RandomWizard.getRandomFloatBetween(minDelay, maxDelay);
                sendObject();
            } else {
                timer -= delta;
            }
        for (FlyingObj object : new ArrayList<>(objects)) {
            try {
                object.act(delta);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                object.remove();
                objects.remove(object);
            }
            //TODO reuse these objects ... just put them back in place, add new actions and voila!
        }
    }

    public void stop(float maxDelay) {
        stopping = true;
        for (FlyingObj object : objects) {
        }
    }

    public void start() {
        stopping = false;
        gridW = ScreenMaster.getDungeonGrid().getWidth();
        gridH = ScreenMaster.getDungeonGrid().getHeight();
        float zoom = ScreenMaster.getScreen().getController().getZoom();
        cam_x = camera.position.x;
        cam_y = camera.position.y;
        w = (int) (GdxMaster.getWidth() * zoom); //TODO consider zoom
        h = (int) (GdxMaster.getHeight() * zoom);

    }

    private void sendObject() {
        // if (!cinematic) {
        start();
        // }
        FlyingObj obj = create();
        addActor(obj);
        Vector2 v = getPosForNew();
        obj.setPosition(v.x, v.y);
        objects.add(obj);
        addActions(obj);
        if (isLogged())
            log(1, this + " spawned " + obj);
    }

    private void addActions(FlyingObj obj) {
        //MoveToBezier


        Vector2 dest = getDestination(obj, cinematic);
        float dur = getDuration(obj, cinematic);
        MoveToAction action = ActionMaster.addMoveToAction(obj, dest.x, dest.y, dur);
        action.setInterpolation(Interpolation.sine);
        // action = ActionMaster.addMoveByActionReal(obj, 0, dest.y - obj.getY(), dur);
        // action.setInterpolation(Interpolation.circleOut);

        if (isLogged())
            log(1, dest + " destination for " + obj);
        // ActionMaster.addDelayedAction(obj, delay, fadeOut);

        if (cinematic) {

        } else {
            //cannot predict if player will follow, but we can increase speed and reduce alpha after it leaves
            //camera, and REMOVE it soon
        }
        ActionMaster.addAfter(obj, () -> {
            if (obj.actor instanceof EmitterActor) {
                ((EmitterActor) obj.actor).allowFinish();
            } else
                obj.remove();
            // pool.add(obj);

        });

        if (obj.actor instanceof EmitterActor) {
            ((EmitterActor) obj.actor).start();
        }
    }

    private boolean isLogged() {
        return false;
    }

    private float getDuration(FlyingObj obj, boolean cinematic) {
        //this is completely up to me! Depends on TYPE? Or also intensity?
        float base = cinematic ? 5 : 5 * getGridSizeFactor();
        return (float) (base *
                type.speedFactor
                * (1 / Math.sqrt(intensity)));
    }

    private float getGridSizeFactor() {
        return 1;
    }

    private Vector2 getPosForNew() {
        boolean hor = RandomWizard.random();
        float offset = (hor ? h : w) * RandomWizard.getRandomFloatBetween(0.31f, 0.68f);
        //angle should affect - we want diagonal more? or maybe true random..
        return hor
                ? new Vector2(cam_x - w / 2 * getDirectionX(), cam_y + offset * getDirectionY() - h / 2 * getDirectionY() + 1)
                : new Vector2(cam_x + offset * getDirectionX() - w / 2 * getDirectionX(), cam_y - h / 2 * getDirectionY());
    }

    private Vector2 getDestination(FlyingObj obj, boolean cinematic) {

        if (cinematic) {
            boolean horizontal = false;
            float x = camera.position.x + w / 2 * getDirectionX();
            float y = camera.position.y + h / 2 * getDirectionY();
            if (obj.getY() == 0 || obj.getY() == getMaxY()) {
                horizontal = true; //angle sticks to the hor axis
            }
            double tan = Math.tan(Math.toRadians(horizontal ? angle : 90 - angle));
            if (horizontal) {
                float ySide = (float) (tan * getScreenX(obj.getX()));
                y = y - ySide;
            } else {
                float xSide = (float) (tan * (getScreenY(obj.getY())));
                x = x - xSide;
                //TODO  - w?
            }
            float offset = RandomWizard.getRandomFloatBetween(-10, 10); //slight random in vector then
            return horizontal ? new Vector2(x, y + offset) : new Vector2(x + offset, y);
        } else {
/*
        what difference?
        we just take bigger W/H?
 */
        }
        return null;
    }

    private double getScreenX(float x) {
        return cam_x - x + w / 2;
    }

    private double getScreenY(float y) {
        return cam_y - y + h / 2;
    }

    private float getMaxY() {
        return cam_y - h / 2;
    }

    private int getDirectionX() {
        if (right)
            return 1;
        return -1;
    }

    private int getDirectionY() {
        if (up)
            return 1;
        return -1;
    }


    public FlyingObj create() { //override?
        // sprites, textures, vfx - generally any actor perhaps
        //use actions after all? That would mean probably NOT responsive to input/... which is OK?
        // MoveController controller = new FlyMoveController(data);
        SuperActor actor = createActor();
        return new FlyingObj(actor);
    }

    @Override
    public String toString() {
        return type + "FlyingObjs";
    }

    private SuperActor createActor() {
        SuperActor actor = null;
        String path = type.getPathVariant();
        switch (type) {
            case mist:
            case cinders:
                actor = new EmitterActor(path);
                break;
            default:
                actor = new ImageContainer(path);
        }
        // actor.setColor(type.getHue());
        // actor.getColor().mul(getHue());
        if (type.alpha != null) {
            actor.setAlphaTemplate(type.alpha);
        }
        if (actor instanceof Flippable) {
            if (type.flipX)
                ((Flippable) actor).setFlipX(RandomWizard.random());
            if (type.flipY)
                ((Flippable) actor).setFlipY(RandomWizard.random());
        }
        float scale = RandomWizard.getRandomFloatBetween(0.5f, 1f);
        actor.setScale(scale);
        return actor;
    }


    public enum FLY_OBJ_TYPE {
        cloud(0.3f, ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        isle(0.5f, ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        stars(2.5f, ALPHA_TEMPLATE.SUN, true, true, 0f),
        wraith(0.2f, ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        smoke(0.3f, ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        black_smoke(0.3f, ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        // star_field,
        // cloud_field, //this would require some alpha tricks!
        //
        mist(2f,
                VFX.missile_arcane_intense, VFX.missile_death, VFX.missile_arcane, VFX.missile_nether_nox),
                // VFX.MIST_WHITE3, VFX.MIST_WHITE2, VFX.MIST_TRUE2, VFX.MIST_WIND),
        cinders(3f,
                        VFX.missile_warp, VFX.missile_pale, VFX.missile_chaos, VFX.missile_arcane_pink),
                // VFX.CINDERS3, VFX.CINDERS2, VFX.CINDERS),

        debris(0.4f, ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        light(0.3f, ALPHA_TEMPLATE.CLOUD, true, false, 0f), //sprite?

        ;

        private String path;
        BLENDING blending;
        boolean scaling;
        VFX[] vfx;

        FLY_OBJ_TYPE(float speedFactor, VFX... vfx) {
            this.vfx = vfx;
            this.speedFactor = speedFactor;
        }

        FLY_OBJ_TYPE(float speedFactor, ALPHA_TEMPLATE alpha,
                     boolean flipX, boolean flipY, float weightFactor) {
            path = PathFinder.getFlyObjPath() + name() + ".png";
            this.speedFactor = speedFactor;
            this.alpha = alpha;
            this.flipX = flipX;
            this.flipY = flipY;
            this.weightFactor = weightFactor;
        }

        public float speedFactor;
        public ALPHA_TEMPLATE alpha;
        boolean flipX;
        boolean flipY;
        float weightFactor;

        public String getPathVariant() {
            if (vfx != null) {
                return vfx[RandomWizard.getRandomInt(vfx.length)].getPath();
            }
            return FileManager.getRandomFilePathVariant(
                    PathFinder.getImagePath() +
                            path, ".png", false);
        }
    }


}
