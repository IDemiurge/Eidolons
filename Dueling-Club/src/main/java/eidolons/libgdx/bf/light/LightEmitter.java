package eidolons.libgdx.bf.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.ability.effects.common.LightEmittingEffect;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.actions.FloatActionLimited;
import eidolons.libgdx.anims.sprite.FadeSprite;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.grid.OverlayView;
import eidolons.libgdx.bf.overlays.OverlayingMaster;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
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
    private Map<DIRECTION, FadeImageContainer> rays = new XLinkedMap<>();
    private boolean overlaying;
    private int gridX, gridY;
    private DIRECTION direction;
    private FadeImageContainer center;
    private int freeRays = 2;
    private FloatActionLimited alphaAction = (FloatActionLimited) ActionMaster.getAction(FloatActionLimited.class);
    private FadeImageContainer overlay;
    private LIGHT_RAY type;
    private float baseAlpha;
    private Boolean withinCamera;
    private boolean alphaChanging;
    private boolean off;


    public LightEmitter(BattleFieldObject obj, LightEmittingEffect effect) {
        this.overlaying = obj.isOverlaying();
        this.gridX = obj.getX();
        this.gridY = obj.getY();
        this.direction = obj.getDirection();
        this.type = LIGHT_RAY.WHITE;
        this.effect = effect;
        String imagePath = StrPathBuilder.build(PathFinder.getShadeCellsPath(),
                "rays", type.name(), "center" + (overlaying ? " overlaying.png"
                        : ".png"));
        if (isSpriteCenterLight()) {
            imagePath= StrPathBuilder.build(PathFinder.getShadeCellsPath(),
                    "light",  "atlas.txt");
            center = new FadeSprite(imagePath);
        } else {
            center = new FadeImageContainer(imagePath);
        }
        setSize(128, 128);
        if (isAddCenterLight()) {
            addActor(center);
            GenericEnums.ALPHA_TEMPLATE template = GenericEnums.ALPHA_TEMPLATE.SHADE_CELL_LIGHT_EMITTER;
            center.setAlphaTemplate(template);
            center.setPosition(GdxMaster.centerWidth(center),
                    GdxMaster.centerHeight(center) + OFFSET_Y);
            center.setOrigin(center.getWidth() / 2, center.getHeight() / 2);
        }
        if (overlaying && isAddOverlayObj()) {
            overlay = new FadeImageContainer(obj.getImagePath());
            overlay.setScale(OverlayView.SCALE, OverlayView.SCALE);
            addActor(overlay);
            Dimension dim = OverlayingMaster.getOffsetsForOverlaying(direction,
                    (int) 64,
                    (int) 64);
            //get grid?
            overlay.setPosition((float) dim.getWidth(),
                    (float) dim.getHeight());
            //                overlay.setPosition(GdxMaster.centerWidth(overlay),
            //                 GdxMaster.centerHeight(overlay));
        }
        setTransform(false);
        setUserObject(obj);
    }

    private boolean isSpriteCenterLight() {
        return false;
    }

    private boolean isAddOverlayObj() {
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (Cinematics.ON)
            return;
        super.draw(batch, parentAlpha);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    private boolean isAddCenterLight() {
        return !overlaying;
    }

    public void update() {
        if (freeRays > 0 || isDynamicUpdates()) {
            //TODO only diag/ no diags option for orderly lights
            List<DIRECTION> directionList = new ArrayList<>();
            if (overlaying) {
                if (direction == null) {
                    direction = DirectionMaster.getRandomDirection();
                }
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
                        removeRay(ray, d);

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
        }
        int i = 0;
        if (center != null) {
            center.setTransform(false);
        }
        for (DIRECTION d : rays.keySet()) {
            FadeImageContainer ray = rays.get(d);
            float x = getWidth() / 2;
            float y = getHeight() / 2;
            if (overlaying) {
                Dimension dim = OverlayingMaster.getOffsetsForOverlaying(direction,
                        64,
                        64 );
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

            if (!overlaying)
                ray.setY(ray.getY() + OFFSET_Y);
            i++;
        }
        if (overlay != null) {
            overlay.setZIndex(Integer.MAX_VALUE);
        }
        center.setZIndex(Integer.MAX_VALUE);

    }

    private void removeRay(FadeImageContainer ray, DIRECTION d) {
        freeRays++;
        ray.fadeOut();
        ActionMaster.addRemoveAfter(ray);
        rays.remove(d);
    }

    private boolean isRandomLightDirection() {
        return true;
    }

    private boolean isDynamicUpdates() {
        return false;
    }

    @Override
    public BattleFieldObject getUserObject() {
        return (BattleFieldObject) super.getUserObject();
    }

    private boolean checkCellFree(Coordinates c) {
        if (effect.getCache() != null)
            if (!effect.getCache().contains(c))
                return false;
        for (BattleFieldObject obj : DC_Game.game.getObjectsAt(c)) {
            if (obj.isWall()) {
                return false;
            }
        }
        return true;
    }

    public void setOff(boolean off) {
        this.off = off;
        if (off) {
            alphaAction.setEnd(0);
        } else {
            alphaAction.setEnd(baseAlpha);

        }
    }

    @Override
    public void act(float delta) {

//        if (getUserObject().checkStatus(UnitEnums.STATUS.OFF)) {
//            alphaAction.setEnd(0);
//            off = true;
//        } else {
//            if (off){
//                off=false;
//            }
//        }

        boolean b = alphaAction.getTime() < alphaAction.getDuration();
        boolean changed = b != alphaChanging;
        alphaChanging = b;
        if (!alphaChanging && isIgnored()) {
            return;
        }
        super.act(delta);
        rotate(delta);
        if (alphaChanging) {
            getColor().a = alphaAction.getValue();
//            setAlpha(alphaAction.getValue(), changed);
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
        if (isSpriteCenterLight())
            return;
        center.setRotation(center.getRotation() + delta * 2.5f);
    }

    public FadeImageContainer createRay(DIRECTION direction, boolean overlaying
            , LIGHT_RAY rayType) {
        String imagePath = StrPathBuilder.build(PathFinder.getShadeCellsPath(),
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
            String existing = StrPathBuilder.build(PathFinder.getShadeCellsPath(),
                    "rays", rayType.name(), d + (overlaying ? " overlaying.png"
                            : ".png"));

            if (direction.isDiagonal()) {
                GdxImageMaster.flip(existing, !d.isVertical(), d.isVertical(), true, imagePath);
            }
            texture = GdxImageMaster.flip(existing, d.isGrowX() == direction.isGrowX(),
                    d.isGrowY() == direction.isGrowY(), true, imagePath);
        }
        Image image = new Image(texture);
        FadeImageContainer ray = new LightRay(image);
        GenericEnums.ALPHA_TEMPLATE template = GenericEnums.ALPHA_TEMPLATE.LIGHT_EMITTER_RAYS;
        ray.setAlphaTemplate(template);

        ray.setTransform(false);
        Color c = ShadowMap.getLightColor(getUserObject());
        ray.setColor(c);

        return ray;
    }

    public float getBaseAlpha() {
        return baseAlpha;
    }

    public void setBaseAlphaForChildren(float alpha) {
        for (int i = 0; i < rays.size(); i++) {
            LightRay ray = (LightRay) rays.values().toArray()[i];
            ray.setBaseAlpha((float) (alpha / Math.sqrt(i + 1)));
        }
        if (center != null)
            center.setBaseAlpha(alpha);
    }

    public void setAlpha(float alpha, boolean changed) {
        for (int i = 0; i < rays.size(); i++) {
            LightRay ray = (LightRay) rays.values().toArray()[i];
            float a = ray.getColor().a * alpha / 2 + (float) (alpha / Math.sqrt(i + 1));
            if (changed) ray.setFluctuatingAlpha(a);
            ray.getColor().a = a;
        }
        if (center != null) {
            if (changed) center.setFluctuatingAlpha(alpha);
            center.getColor().a = center.getColor().a * alpha / 2 + alpha / 2;
        }
    }


    public void setBaseAlpha(float baseAlpha) {
        if (TEST_MODE) {
            baseAlpha = 1f;
        }
        this.baseAlpha = baseAlpha;

        alphaAction.reset();
        alphaAction.setStart(getColor().a);
        alphaAction.setEnd(baseAlpha * ShadowMap.getInitialAlphaCoef());
        addAction(alphaAction);
        alphaAction.setTarget(this);
        alphaAction.setDuration(0.4f + (Math.abs(getColor().a - baseAlpha)) / 2);

        setBaseAlphaForChildren(baseAlpha);

        main.system.auxiliary.log.LogMaster.log(0, baseAlpha + " alpha for " +
                getUserObject().getNameAndCoordinate() +
                " set in " + alphaAction.getDuration());
    }

    public void reset() {
        for (DIRECTION direction1 : rays.keySet()) {
            removeRay(rays.get(direction1), direction1);
        }
        freeRays = 2;
        update();
    }

    public enum LIGHT_RAY {
          FIRE, MOON, SUN, WHITE, MAGIC, SHADOW
    }

    @Override
    public void setTransform(boolean transform) {
        super.setTransform(transform);
    }

    class LightRay extends FadeImageContainer {
        public LightRay(Image image) {
            super(image);
        }

        public LightRay(String path) {
            super(path);
        }

        @Override
        public void setTransform(boolean transform) {
            if (transform)
                return;
            super.setTransform(transform);
        }

        @Override
        public boolean isAlphaFluctuationOn() {
            return true;
//            return alphaAction.getTime() >= alphaAction.getDuration();
        }
    }


}
