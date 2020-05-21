package eidolons.libgdx.bf.light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.actions.FloatActionLimited;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.libgdx.bf.grid.cell.GridCellContainer;
import eidolons.libgdx.bf.light.ShadowMap.SHADE_CELL;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.bf.overlays.OverlayingMaster;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.texture.SmartTextureAtlas;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.entity.obj.Obj;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.awt.*;
import java.util.Random;

/**
 * Created by JustMe on 8/28/2017.
 */
public class ShadeLightCell extends SuperContainer {

    private static boolean alphaFluctuation = true;
    private final float width;
    private final float height;
    FloatActionLimited alphaAction = (FloatActionLimited) ActionMaster.getAction(FloatActionLimited.class);
    private SHADE_CELL type;
    private Float originalX;
    private Float originalY;
    private boolean voidCell;
    static SmartTextureAtlas shadowMapAtlas;

    public static SmartTextureAtlas getShadowMapAtlas() {
        if (shadowMapAtlas == null) {
            shadowMapAtlas = new SmartTextureAtlas(
                    PathFinder.getImagePath() +
                            "ui/cells/outlines/shadows/shadows.txt");
        }
        return shadowMapAtlas;
    }

    public ShadeLightCell(SHADE_CELL type) {
        this(type, null);
    }

    public ShadeLightCell(SHADE_CELL type, Object arg) {
        super(new Image(getTexture(type)));
        this.type = type;
        randomize();
        baseAlpha = 0;
        setUserObject(arg);
        if (isColored()) {
            teamColor = ShadowMap.getLightColor(arg);
            if (getTeamColor() != null)
                getContent().setColor(getTeamColor());
        }
        width = getContent().getWidth();
        height = getContent().getHeight();
        getContent().setOrigin(getContent().getWidth() / 2, getContent().getHeight() / 2);

        GenericEnums.ALPHA_TEMPLATE template = ShadowMap.getTemplateForShadeLight(type);
        if (template != null)
            setAlphaTemplate(template);
        if (isTransformDisabled())
            setTransform(false);


    }

    private static TextureRegion getTexture(SHADE_CELL type) {
        TextureAtlas.AtlasRegion texture = getShadowMapAtlas().findRegionFromFullPath(
                (getTexturePath(type)));
        if (texture == null) {
            main.system.auxiliary.log.LogMaster.important(getTexturePath(type)
                    + " - " + type + " has null texture ( ");
            return TextureCache.getOrCreateR(getTexturePath(type));
        }
        return texture;
    }

    private static String getTexturePath(SHADE_CELL type) {
        switch (type) {
            //                    TODO varied enough already?
            case VOID:
                return FileManager.formatPath(PathFinder.getImagePath() +
                        StringMaster.cropFormat(type.getTexturePath()) +
                        getRandomVoidVariant() +
                        ".png", false, false);
        }
        return type.getTexturePath();
    }

    private static String getRandomVoidVariant() {
        int n = RandomWizard.getRandomIntBetween(1, 7, true);
        if (n == 1) {
            return "";
        }
        return n + "";
    }

    @Override
    protected boolean isTransformDisabled() {
        //        if (type == SHADE_CELL.LIGHT_EMITTER)
        //            return false;
        return true;
    }

    private void randomize() {


        if (type == SHADE_CELL.GAMMA_SHADOW || type == SHADE_CELL.VOID) {
            int rotation = 90 * RandomWizard.getRandomInt(4);
            getContent().setOrigin(Align.center);
            getContent().setRotation(rotation);
            getContent().setScale(new Random().nextFloat() / 5 + 1f, new Random().nextFloat() / 5 + 1f);
        }
    }

    public boolean isCachedPosition() {
        return true;
    }

    private boolean isColored() {
        switch (type) {
            case GAMMA_LIGHT:
            case LIGHT_EMITTER:
            case GAMMA_SHADOW:
                return ShadowMap.isColoringSupported();
            case CONCEALMENT:
                break;
        }
        return false;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isIgnored())
            return;
        if (getBaseAlpha() < getMinAlpha())
            return;
        //        if (isBlendingOn())
        //        storeBlendingFuncData(batch);
        //        initBlending(batch);
        super.draw(batch, parentAlpha);
        //        restoreBlendingFuncData(batch);
    }

    private float getMinAlpha() {
        return 0.05f;
    }

    @Override
    public boolean isIgnored() {
        // check cell is visible TODO
        if (!InputController.cameraMoved)
            return !withinCamera;
        withinCamera = getController().isWithinCamera(this);
        return !withinCamera;
    }

    @Override
    public boolean isAlphaFluctuationOn() {
        if (!alphaFluctuationOn) {
            if (type == SHADE_CELL.LIGHT_EMITTER || type == SHADE_CELL.GAMMA_LIGHT)
                return false;
        }
        if (getActions().size > 0) {
            return false;
        }
        if (type == SHADE_CELL.VOID) {
            return false;
        }
        return alphaFluctuation;
    }

    @Override
    public Color getTeamColor() {
        return teamColor;
    }

    @Override
    protected void alphaFluctuation(float delta) {
        super.alphaFluctuation(getContent(), delta);
    }

    @Override
    protected float getAlphaFluctuationMin() {
        return super.getAlphaFluctuationMin();

    }

    @Override
    protected float getAlphaFluctuationMax() {
        return super.getAlphaFluctuationMax();
    }

    @Override
    protected float getAlphaFluctuationPerDelta() {
        return super.getAlphaFluctuationPerDelta();
    }

    @Override
    public void act(float delta) {
        if (isIgnored()) {
            return;
        }
        super.act(delta);
        if (alphaAction.getTime() < alphaAction.getDuration()) {
            getColor().a = alphaAction.getValue();
            fluctuatingAlpha = alphaAction.getValue();
        }
    }

    @Override
    public void setPosition(float x, float y) {
        if (originalX == null)
            originalX = x;
        if (originalY == null)
            originalY = y;
        super.setPosition(x, y);
    }

    private void initBlending(Batch batch) {
        GenericEnums.BLENDING mode = null;
        switch (type) {
            case GAMMA_LIGHT:
            case LIGHT_EMITTER:

                mode = (!Gdx.input.isButtonPressed(Keys.SHIFT_LEFT))
                        ? GenericEnums.BLENDING.OVERLAY
                        : GenericEnums.BLENDING.SATURATE;
                break;
            case CONCEALMENT:
            case GAMMA_SHADOW:
                mode = (!Gdx.input.isButtonPressed(Keys.SHIFT_LEFT))
                        ? GenericEnums.BLENDING.OVERLAY
                        : GenericEnums.BLENDING.MULTIPLY;
            case BLACKOUT:
            case HIGHLIGHT:
                break;
        }
        if (mode != null)
            batch.setBlendFunction(mode.blendSrcFunc, mode.blendDstFunc);
    }

    public void adjustPosition(int x, int y) {
        float offsetX = 0;
        float offsetY = 0;
        setScale(1f, 1f);
        setVisible(true);
        for (Obj sub : DC_Game.game.getRules().getIlluminationRule().getEffectCache().keySet()) {
            if (sub instanceof Unit)
                continue; //TODO illuminate some other way for units...

            if (sub instanceof Structure) {
                if (sub.getCoordinates().x == x)
                    if (sub.getCoordinates().y == y)
                        if (((Structure) sub).isOverlaying()) {
                            DIRECTION d = ((Structure) sub).getDirection();
                            if (d == null) {
                                setScale(0.7f, 0.7f);
                                continue;
                            }

                            setScale(d.growX == null ? 1 : 0.8f, d.growY == null ? 1 : 0.8f);
                            Dimension dim = OverlayingMaster.getOffsetsForOverlaying(d,
                                    (int) getWidth() - 64,
                                    (int) getHeight() - 64);
                            offsetX += dim.width;
                            offsetY += dim.height;
                            //so if 2+ overlays, will be centered between them...
                        } else {

                            BaseView view = ScreenMaster.getDungeonGrid().getViewMap().get(sub);
                            offsetX += view.getX() * 3;
                            offsetY += view.getY() * 3;
                            if (view.getParent() instanceof GridCellContainer) {
                                if ((((GridCellContainer) view.getParent()).getUnitViews(true).size() > 1)) {
                                    if (!view.isHovered())
                                        setVisible(false);
                                }
                            }
                        }
            }

        }

        setPosition(originalX + offsetX / 3, originalY + offsetY / 3);
    }

    public void setVoid(boolean aVoid) {
        this.voidCell = aVoid;
    }

    public float getBaseAlpha() {
        return baseAlpha;
    }

    public void setBaseAlpha(float baseAlpha) {
        alphaAction.reset();
        float a = getColor().a;
        alphaAction.setStart(a);
        alphaAction.setEnd(baseAlpha * ShadowMap.getInitialAlphaCoef());
        addAction(alphaAction);
        alphaAction.setTarget(this);
        alphaAction.setDuration(0.4f + (Math.abs(a - baseAlpha)) / 2);
        this.baseAlpha = baseAlpha;

        if (isColored()) {
            teamColor = ShadowMap.getLightColor(getUserObject());
            setColor(new Color(teamColor));
            getColor().a = a;
        }
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
    }
}
