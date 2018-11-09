package eidolons.libgdx.bf.light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import eidolons.content.PROPS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.anims.actions.FloatActionLimited;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.light.ShadowMap.SHADE_CELL;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.texture.TextureCache;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.data.filesys.PathFinder;
import main.entity.obj.Obj;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.awt.*;
import java.util.Random;

/**
 * Created by JustMe on 8/28/2017.
 */
public class ShadeLightCell extends SuperContainer {

    private static final Color DEFAULT_COLOR = new Color(1, 0.9f, 0.7f, 1);
    private static boolean alphaFluctuation = true;
    private final Float width;
    private final Float height;
    FloatActionLimited alphaAction = (FloatActionLimited) ActorMaster.getAction(FloatActionLimited.class);
    private SHADE_CELL type;
    private Float originalX;
    private Float originalY;
    private boolean voidCell;


    public ShadeLightCell(SHADE_CELL type) {
        this(type, null);
    }

    public ShadeLightCell(SHADE_CELL type, Object arg) {
        super(new Image(TextureCache.getOrCreateR(getTexturePath(type, arg))));
        this.type = type;
        randomize();
        baseAlpha = 0;
        if (isColored()) {
            teamColor = getLightColor(null);
            if (getTeamColor() != null)
                getContent().setColor(getTeamColor());
        }
        width = getContent().getWidth();
        height = getContent().getHeight();
        getContent().setOrigin(getContent().getWidth() / 2, getContent().getHeight() / 2);

        ALPHA_TEMPLATE template = ShadowMap.getTemplateForShadeLight(type);
        if (template != null)
            setAlphaTemplate(template);
        if (isTransformDisabled())
            setTransform(false);


    }

    private static String getTexturePath(SHADE_CELL type, Object arg) {
        switch (type) {
            //                    TODO varied enough already?
            case VOID:
                return FileManager.getRandomFilePathVariant(
                 PathFinder.getImagePath(),
                 StringMaster.cropFormat(type.getTexturePath()), ".png", false, false);
        }
        return type.getTexturePath();
    }

    public static Color getLightColor(BattleFieldObject userObject) {

        COLOR_THEME colorTheme = null;
        if (userObject != null) {
            colorTheme = new EnumMaster<COLOR_THEME>().
             retrieveEnumConst(COLOR_THEME.class, userObject.getProperty
              (PROPS.COLOR_THEME, true));
        }
        if (colorTheme == null) {
            Dungeon obj = Eidolons.game.getDungeon();
            colorTheme = obj.getColorTheme();
        }

        Color c = null;
        if (colorTheme != null)
            c = GdxColorMaster.getColorForTheme(colorTheme);
        if (c != null) return c;
        return DEFAULT_COLOR;
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
                return ShadowMap.isColoringSupported();
            case CONCEALMENT:
            case GAMMA_SHADOW:
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
        //        if (isBlendingOn())
        //        storeBlendingFuncData(batch);
        //        initBlending(batch);
        super.draw(batch, parentAlpha);
        //        restoreBlendingFuncData(batch);
    }

    @Override
    public boolean isIgnored() {
        // check cell is visible TODO
        if (withinCamera != null)
            return !withinCamera;
        withinCamera = getController().isWithinCamera(this);
        getController().addCachedPositionActor(this);
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
        BLENDING mode = null;
        switch (type) {
            case GAMMA_LIGHT:
            case LIGHT_EMITTER:

                mode = (!Gdx.input.isButtonPressed(Keys.SHIFT_LEFT))
                 ? BLENDING.OVERLAY
                 : BLENDING.SATURATE;
                break;
            case CONCEALMENT:
            case GAMMA_SHADOW:
                mode = (!Gdx.input.isButtonPressed(Keys.SHIFT_LEFT))
                 ? BLENDING.OVERLAY
                 : BLENDING.MULTIPLY;
            case BLACKOUT:
                break;
            case HIGLIGHT:
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
                            Dimension dim = GridMaster.getOffsetsForOverlaying(d,
                             (int) getWidth() - 64,
                             (int) getHeight() - 64);
                            offsetX += dim.width;
                            offsetY += dim.height;
                            //so if 2+ overlays, will be centered between them...
                        } else {

                            BaseView view = DungeonScreen.getInstance().getGridPanel().getViewMap().get(sub);
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
        alphaAction.setStart(getColor().a);
        alphaAction.setEnd(baseAlpha * ShadowMap.getInitialAlphaCoef());
        addAction(alphaAction);
        alphaAction.setTarget(this);
        alphaAction.setDuration(0.4f + (Math.abs(getColor().a - baseAlpha)) / 2);
        this.baseAlpha = baseAlpha;

        if (isColored())
            teamColor = getLightColor(null);
    }
}
