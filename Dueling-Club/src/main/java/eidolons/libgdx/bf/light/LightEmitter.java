package eidolons.libgdx.bf.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.ability.effects.common.LightEmittingEffect;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.anims.actions.FloatActionLimited;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.grid.OverlayView;
import eidolons.libgdx.texture.TextureCache;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by JustMe on 9/1/2018.
 */
public class LightEmitter extends SuperActor {

    public static final DIRECTION[] defaultRayDirections = {
     DIRECTION.RIGHT, DIRECTION.LEFT
    };
    public static final DIRECTION[] priorityOfRayDirections = {
     DIRECTION.RIGHT, DIRECTION.LEFT,
     DIRECTION.DOWN, DIRECTION.DOWN_RIGHT,
     DIRECTION.DOWN_LEFT, DIRECTION.UP_LEFT,
     DIRECTION.UP_RIGHT, DIRECTION.UP,
    };
    private static final boolean TEST_MODE = false;
    private final float OFFSET_Y = 14;
    private final LightEmittingEffect effect;
    Map<DIRECTION, FadeImageContainer> rays = new XLinkedMap<>();
    boolean overlaying;
    int gridX, gridY;
    DIRECTION direction;
    FadeImageContainer center;
    int freeRays = 2;
    FloatActionLimited alphaAction = (FloatActionLimited) ActorMaster.getAction(FloatActionLimited.class);
    private FadeImageContainer overlay;
    private LIGHT_RAY type;
    private float baseAlpha;
    private Boolean withinCamera;

    public LightEmitter(BattleFieldObject obj, LightEmittingEffect effect) {
        this.overlaying = obj.isOverlaying();
        this.gridX = obj.getX();
        this.gridY = obj.getY();
        this.direction = obj.getDirection();
        this.type = LIGHT_RAY.FIRE;
        this.effect = effect;
        String imagePath = StrPathBuilder.build(PathFinder.getShadeLightPath(),
         "rays", type.name(), "center" + (overlaying ? " overlaying.png"
          : ".png"));
        center = new LightContainer(imagePath);
        setSize(128, 128);
        if (isAddCenterLight()) {
            addActor(center);
            ALPHA_TEMPLATE template = ALPHA_TEMPLATE.SHADE_CELL_LIGHT_EMITTER;
            center.setAlphaTemplate(template);
            center.setPosition(GdxMaster.centerWidth(center),
             GdxMaster.centerHeight(center) + OFFSET_Y);
            center.setOrigin(center.getWidth() / 2, center.getHeight() / 2);
        }
        if (overlaying) {
            overlay = new FadeImageContainer(obj.getImagePath());
            overlay.setScale(OverlayView.SCALE, OverlayView.SCALE);
            addActor(overlay);
            Dimension dim = GridMaster.getOffsetsForOverlaying(direction,
             (int) 64,
             (int) 64);
            //get grid?
            overlay.setPosition((float) dim.getWidth(),
             (float) dim.getHeight());
            //                overlay.setPosition(GdxMaster.centerWidth(overlay),
            //                 GdxMaster.centerHeight(overlay));
        }

        setUserObject(obj);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    private boolean isAddCenterLight() {
        return !overlaying;
    }

    public void update() {
        //TODO only diag/ no diags option
        List<DIRECTION> directionList = new ArrayList<>();
        if (overlaying) {
            directionList.add(direction);
            boolean random = RandomWizard.random();
            //      only 1 ray for now...      directionList.add(direction.rotate45(random));
            directionList.add(direction.rotate45(!random));
        } else {
            directionList = new ArrayList<>(Arrays.asList(priorityOfRayDirections));
            if (isRandomLightDirection())
                Collections.shuffle(directionList);
            for (DIRECTION d : rays.keySet()) {
                directionList.add(0, d);
            }
        }
        for (DIRECTION d : directionList) {
            if (d == null)
                continue;
            //                d = DirectionMaster.getRandomDirection();
            Coordinates c = Coordinates.get(gridX, gridY).getAdjacentCoordinate(d);
            if (c == null) {
                continue;
            }
            FadeImageContainer ray = rays.get(d);
            if (ray != null) {
                 if (!checkCellFree(c)) {
                    freeRays++;
                    ray.fadeOut();
                    ActorMaster.addRemoveAfter(ray);
                    rays.remove(d);
                }
            } else {
                if (freeRays > 0) {
                    ray = createRay(d, overlaying, type);
                    addActor(ray);
                    rays.put(d, ray);
                    freeRays--;
                }
            }

        }
        int i = 0;
        for (DIRECTION d : rays.keySet()) {
            FadeImageContainer ray = rays.get(d);

            Color c = ShadeLightCell.getLightColor(getUserObject());
            float a = ray.getColor().a;
            ray.setColor(c);
            ray.getColor().a = a;
            float x = getWidth() / 2;
            float y = getHeight() / 2;
            if (overlaying) {
                Dimension dim = GridMaster.getOffsetsForOverlaying(direction,
                 64,
                 64);
                x = (float) dim.getWidth() + 32;
                y = (float) dim.getHeight() + 32;
            }

            float offsetX = d.isDiagonal() ? ray.getWidth() / 2 : -ray.getWidth() / 2;
            if (d.growX != null) {
                offsetX = d.growX ? 0 : -ray.getWidth();
            }

            float offsetY = d.isDiagonal() ? ray.getHeight() / 2 : -ray.getHeight() / 2;
            if (d.growY != null) {
                offsetY = d.growY ? -ray.getHeight() : 0;
            }
            ray.setPosition(x + offsetX, y + offsetY);

            /*

            float offsetX = ray.getWidth() / 2;
            if (d.growX != null) {
                if (d.isDiagonal()) {
                    offsetX = d.growX ? 0 : -ray.getWidth();
                } else {
                    offsetX =  d.growX ? 0 :  ray.getWidth()  ;
                }
            }

            float offsetY = ray.getHeight() / 2;
            if (d.growY != null) {
                if (d.isDiagonal())
                    offsetY = d.growY ? -ray.getHeight() : 0;
                else
                    offsetY =  d.growY ?  ray.getHeight()   : 0;
            }
            if (!d.isDiagonal()) {
                ray.setPosition(x - offsetX, y - offsetY);
            } else
                ray.setPosition(x + offsetX, y + offsetY);
             */

            if (!overlaying)
                ray.setY(ray.getY() + OFFSET_Y);
            ray.setBaseAlpha(baseAlpha / (i + 1));
            i++;
        }
        if (overlay != null) {
            overlay.setZIndex(Integer.MAX_VALUE);
        }
        center.setBaseAlpha(baseAlpha);
        center.setZIndex(Integer.MAX_VALUE);

    }

    private boolean isRandomLightDirection() {
        return true;
    }

    private float getOffsetCoef(boolean x, boolean diagonal, boolean greater) {
        float c = getOffsetCoef(x);
        if (diagonal) {
            c = c * 1.25f;
        }
        if (greater)
            return 1 / c;
        return c;
    }

    private float getOffsetCoef(boolean x) {
        if (overlaying) {
            return x ? 1.15f : 1.25f;
        }
        return x ? 0.8f : 1.1f;
    }

    @Override
    public BattleFieldObject getUserObject() {
        return (BattleFieldObject) super.getUserObject();
    }

    private boolean checkCellFree(Coordinates c) {
        if (effect.getCache() != null)
            if (!effect.getCache().contains(c))
                return false;
        //        DC_Cell cell = DC_Game.game.getCellByCoordinate(c);
        //        try {
        //             getUnitVisibilityStatus(cell, getUserObject()) == UNIT_VISION.BLOCKED)
        //                return false;
        //        } catch (Exception e) {
        //TODO find a better way
        //        }
        for (BattleFieldObject obj : DC_Game.game.getObjectsAt(c)) {
            if (obj.isWall()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void act(float delta) {
        if (isIgnored()){
             return;
        }
        super.act(delta);
        rotate(delta);

        if (alphaAction.getTime() < alphaAction.getDuration()) {
            getColor().a = alphaAction.getValue();
            rays.values().forEach(ray -> ray.setFluctuatingAlpha(alphaAction.getValue()));
            center.setFluctuatingAlpha(alphaAction.getValue());
        }
    }

    protected boolean isIgnored() {
        if (withinCamera != null)
            return !withinCamera;
        withinCamera = getController().isWithinCamera(this);
        getController().addCachedPositionActor(this);
        return !withinCamera;
    }

    private void rotate(float delta) {
        center.setRotation(center.getRotation() + delta * 2.5f);
    }

    public FadeImageContainer createRay(DIRECTION direction, boolean overlaying
     , LIGHT_RAY rayType) {
        String imagePath = StrPathBuilder.build(PathFinder.getShadeLightPath(),
         "rays", rayType.name(), direction + (overlaying ? " overlaying.png"
          : ".png"));
        Texture texture = TextureCache.getOrCreate(imagePath);
        if (texture.getWidth() == 64) {
            DIRECTION d = DIRECTION.DOWN;
            if (direction.isDiagonal()) {
                d = DIRECTION.DOWN_LEFT;
            }
            if (!direction.isVertical()) {
                d = DIRECTION.RIGHT;
            }
            String existing = StrPathBuilder.build(PathFinder.getShadeLightPath(),
             "rays", rayType.name(), d + (overlaying ? " overlaying.png"
              : ".png"));

            if (direction.isDiagonal()) {
                GdxImageMaster.flip(existing, !d.isVertical(), d.isVertical(), true, imagePath);
            }
            texture = GdxImageMaster.flip(existing, d.isGrowX() == direction.isGrowX(),
             d.isGrowY() == direction.isGrowY(), true, imagePath);
        }
        Image image = new Image(texture);
        FadeImageContainer ray = new LightContainer(image);
        ALPHA_TEMPLATE template = ALPHA_TEMPLATE.LIGHT_EMITTER_RAYS;
        ray.setAlphaTemplate(template);


        return ray;
    }

    public float getBaseAlpha() {
        return baseAlpha;
    }

    public void setBaseAlpha(float baseAlpha) {
        if (TEST_MODE) {
            baseAlpha = 1f;
        } else if (overlaying) {
            baseAlpha *= 1.75f;
        }
        this.baseAlpha = baseAlpha;

        alphaAction.reset();
        alphaAction.setStart(getColor().a);
        alphaAction.setEnd(baseAlpha * ShadowMap.getInitialAlphaCoef());
        addAction(alphaAction);
        alphaAction.setTarget(this);
        alphaAction.setDuration(0.4f + (Math.abs(getColor().a - baseAlpha)) / 2);

   main.system.auxiliary.log.LogMaster.log(1,baseAlpha + " alpha for " +
    getUserObject().getNameAndCoordinate() +
    " set in " + alphaAction.getDuration());
    }

    public enum LIGHT_RAY {
        MOON, SUN, FIRE, MAGIC, SHADOW
    }

    class LightContainer extends FadeImageContainer {
        public LightContainer(Image image) {
            super(image);
        }

        public LightContainer(String path) {
            super(path);
        }

        @Override
        public boolean isAlphaFluctuationOn() {
            return alphaAction.getTime() >= alphaAction.getDuration();
        }
    }


}
