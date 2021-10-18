package libgdx.bf.grid.moving.flight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.sprite.SpriteX;
import libgdx.bf.SuperActor;
import libgdx.bf.SuperGroup;
import libgdx.bf.generic.Flippable;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.generic.GroupX;
import libgdx.particles.EmitterActor;
import libgdx.screens.ScreenMaster;
import main.system.auxiliary.RandomWizard;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import static main.content.enums.GenericEnums.*;
import static main.system.auxiliary.log.LogMaster.log;

/*
sends certain kinds of actors flying in certain way for the FLIGHT CINEMATICS
start    - already in motion?
 */
public class FlyingObjs extends GroupX {

    private final float minDelay, maxDelay, angle;
    private float timer = 0;
    private final int intensity;
    VisualEnums.FLY_OBJ_TYPE type;
    CinematicPlatform platform; // don't want to collide with it right? Or is it ABOVE?..
    private final OrthographicCamera camera;
    private final boolean cinematic;
    private boolean right, up;
    private float cam_x, cam_y;
    private int w, h;
    Set<FlyingObj> objects = new LinkedHashSet<>(); //might want to *STOP* them or change their ANGLE
    Stack<FlyingObj> pool = new Stack<>();
    private boolean stopping;
    private Color hue;
    private float initialDelay;
    //new Color(0.4f, 0.3f, 0.5f, 0.5f)


    public FlyingObjs(VisualEnums.FLY_OBJ_TYPE type, CinematicPlatform platform, int intensity, boolean cinematic) {
        this.type = type;
        this.platform = platform;
        this.cinematic = cinematic;
        this.minDelay = getMinDelay(type, intensity);
        this.maxDelay = getMaxDelay(type, intensity);
        //concurrent instances, distance, ...
        this.angle = platform.angle;
        this.intensity = intensity;
        camera = ScreenMaster.getScreen().getCamera();
        initialDelay = maxDelay * 3;
    }

    private float getMaxDelay(VisualEnums.FLY_OBJ_TYPE type, int intensity) {
        return 3 /type.speedFactor / (float) intensity;
    }

    private float getMinDelay(VisualEnums.FLY_OBJ_TYPE type, int intensity) {
        return 1 / type.speedFactor / (float) intensity;

    }


    public void act(float delta) {
        if (initialDelay > 0)
            initialDelay -= delta;
        if (!stopping)
            if (timer <= 0) {
                if (objects.size()<intensity/2) {
                timer = RandomWizard.getRandomFloatBetween(minDelay, maxDelay);
                sendObject();
                }
            } else {
                timer -= delta;
            }
        for (FlyingObj object : new ArrayList<>(objects)) {
            object.act(delta);
            if (object.actor instanceof EmitterActor) {
                if (((EmitterActor) object.actor).isComplete()) {
                    pool.add(object);
                    object.remove();
                }
            }
        }
    }

    public void stop(float maxDelay) {
        stopping = true;
        // for (FlyingObj object : objects) {
        // }
    }

    public void reset() {
        stopping = false;
        float zoom = ScreenMaster.getScreen().getController().getZoom();
        cam_x = camera.position.x;
        cam_y = camera.position.y;
        w = (int) (GdxMaster.getWidth() * zoom); //TODO consider zoom
        h = (int) (GdxMaster.getHeight() * zoom);

    }

    private void sendObject() {
        // if (!cinematic) {
        reset();
        // }
        FlyingObj obj = getOrCreate();
        addActor(obj);
        Vector2 v = getPosForNew();
        obj.setPosition(v.x, v.y);
        objects.add(obj);
        addActions(obj);
        if (isLogged())
            log(1, this + " spawned " + obj);
    }

    private void addActions(FlyingObj obj) {
        // MoveToBezier
        Vector2 dest = getDestination(obj, cinematic);
        float dur = getDuration(obj, cinematic);
        MoveToAction action = ActionMasterGdx.addMoveToAction(obj, dest.x, dest.y, dur);
        // action.setInterpolation(Interpolation.fade);
        // action = ActionMaster.addMoveByActionReal(obj, 0, dest.y - obj.getY(), dur);
        // action.setInterpolation(Interpolation.circleOut);

        if (isLogged())
            log(1, dest + " destination for " + obj);
        // ActionMaster.addDelayedAction(obj, delay, fadeOut);

        if (cinematic) {

        } else {
            //TODO cannot predict if player will follow, but we can increase speed and reduce alpha after it leaves
            //camera, and REMOVE it soon
        }
        ActionMasterGdx.addAfter(obj, () -> {
            if (obj.actor instanceof EmitterActor) {
                ((EmitterActor) obj.actor).allowFinish();
            } else {
                obj.remove();
                pool.add(obj);
            }
                objects.remove(obj);

        });

        if (obj.actor instanceof EmitterActor) {
            ((EmitterActor) obj.actor).getEffect().
                    scaleEffect(RandomWizard.getRandomFloatBetween(0.5f, 1f));
            ((EmitterActor) obj.actor).start();
            obj.actor.act(RandomWizard.getRandomFloat() * 5);
        }
    }

    private boolean isLogged() {
        return false;
    }

    private float getDuration(FlyingObj obj, boolean cinematic) {
        //this is completely up to me! Depends on TYPE? Or also intensity?
        float i = RandomWizard.getRandomFloatBetween(3, 5);
        float base = cinematic ? i : i * getGridSizeFactor();
        return (base / type.speedFactor);
    }

    private float getGridSizeFactor() {
        return 1;
    }

    private Vector2 getPosForNew() {
        boolean hor = RandomWizard.random();
        float offset = (hor ? h : w) * RandomWizard.getRandomFloatBetween(0.41f, 0.68f);
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
            float angle = this.angle;
            float range = getRandomizeAngleRange();
            if (range > 0)
                angle += RandomWizard.getRandomFloatBetween(
                        -range, range);
            double tan = Math.tan(Math.toRadians(horizontal ? angle : 90 - angle));
            if (horizontal) {
                float ySide = (float) (tan * getScreenX(obj.getX()));
                y = y - ySide;
            } else {
                float xSide = (float) (tan * (getScreenY(obj.getY())));
                x = x - xSide;
                //TODO  - w?
            }
            float offset = 0;
            // RandomWizard.getRandomFloatBetween(
            //         -range, range); //slight random in vector then
            return horizontal ? new Vector2(x, y + offset) : new Vector2(x + offset, y);
        } else {
/*
        what difference?
        we just take bigger W/H?
 */
        }
        return null;
    }

    private float getRandomizeAngleRange() {
        return type.angleRange;
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


    public FlyingObj getOrCreate() {
        if (pool.size() >= getMaxPoolSize()) {
            FlyingObj obj = pool.pop();
            reset(obj);
            return obj;
        }
        SuperActor actor = createActor();
        return new FlyingObj(actor);
    }

    private int getMaxPoolSize() {
        return 5; //depends on speed?
    }

    private void reset(FlyingObj obj) {
        if (obj.actor instanceof EmitterActor) {
            ((EmitterActor) obj.actor).reset();
        }
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
            case comet_bright:
            case comet_pale:
                actor = new EmitterActor(path);
                break;
            case thunder:
            case thunder2:
            case thunder3:
                SpriteX sprite = new SpriteX(path, type.spriteTemplate, type.alpha, BLENDING.SCREEN);
                if (type.host != null) {
                    actor = new ImageContainer(type.host.path);
                    initActor(actor, type.host);
                }
                initActor(sprite, type);
                SuperGroup merged = new SuperGroup();
                merged.addActor(sprite);
                merged.addActor(actor);
                merged.setSize(actor.getWidth(), actor.getHeight());
                GdxMaster.center(sprite);
                return merged;
            default:
                actor = new ImageContainer(path);
        }
        initActor(actor, type);
        return actor;
    }

    private void initActor(SuperActor actor, VisualEnums.FLY_OBJ_TYPE type) {
        if (hue != null)
            if (type.isHued()) {
                actor.setColor(new Color(hue)); //randomize a bit? fluct period ?
                actor.setBaseAlpha(type.getBaseAlpha());
                GdxColorMaster.randomize(actor.getColor(), 0.15f);
            }
        // actor.getColor().mul(getHue());
        if (isAlphaFluctuationSupported())
        if (type.alpha != null) {
            actor.setFluctuatingAlphaPeriod(4);
            actor.setAlphaTemplate(type.alpha);
        } else {
            if (type.baseAlpha!=0) {
                actor.getColor().a = RandomWizard.getRandomFloatBetween(type.baseAlpha/3*2 , 1);
            }
        }
        if (actor instanceof Flippable) {
            if (type.flipX)
                ((Flippable) actor).setFlipX(RandomWizard.random());
            if (type.flipY)
                ((Flippable) actor).setFlipY(RandomWizard.random());
        }
        float scale = RandomWizard.getRandomFloatBetween(0.5f+ type.weightFactor, 1f);
        actor.setScale(scale);
    }

    private boolean isAlphaFluctuationSupported() {
        return false;
    }


    public void setHue(Color hue) {
        this.hue = hue;
    }
}
