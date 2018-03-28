package main.libgdx.bf.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.PROPS;
import main.entity.obj.Obj;
import main.entity.obj.Structure;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.bf.Coordinates.DIRECTION;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.libgdx.GdxColorMaster;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.actions.FloatActionLimited;
import main.libgdx.bf.*;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;

import java.awt.*;
import java.util.Random;

/**
 * Created by JustMe on 8/28/2017.
 */
public class ShadeLightCell extends SuperContainer {

    private static final Color DEFAULT_COLOR = new Color(1, 0.9f, 0.7f, 1);
    private static boolean alphaFluctuation = true;
    FloatActionLimited alphaAction = (FloatActionLimited) ActorMaster.getAction(FloatActionLimited.class);
    private int x;
    private int y;
    private SHADE_LIGHT type;
    private float baseAlpha = 0;
    private Float originalX;
    private Float originalY;

    public ShadeLightCell(SHADE_LIGHT type, int x, int y) {
        super(new Image(TextureCache.getOrCreateR(getTexturePath(type))));
        this.type = type;
        transform();
        if (isColored()) {
            teamColor = initTeamColor();
            if (getTeamColor() != null)
                getContent().setColor(getTeamColor());
        }
        this.x = x;
        this.y = y;
    }

    private static String getTexturePath(SHADE_LIGHT type) {
        switch (type) {
//            case GAMMA_SHADOW:
//               return  FileManager.getRandomFilePathVariant(
//                PathFinder.getImagePath() ,
//                type.getTexturePath(), ".png", false, false) ;
        }
        return type.getTexturePath();
    }

    public static void setAlphaFluctuation(boolean alphaFluctuation) {
        ShadeLightCell.alphaFluctuation = alphaFluctuation;
    }

    private void transform() {
        if (type == SHADE_LIGHT.GAMMA_SHADOW) {
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
                return true;
            case CONCEALMENT:
            case GAMMA_SHADOW:
                break;
        }
        return false;
    }

    @Override
    public float getWidth() {
        if (type == SHADE_LIGHT.GAMMA_SHADOW)
            return super.getWidth();
        if (type == SHADE_LIGHT.HIGLIGHT)
            return super.getWidth();
        return GridConst.CELL_W;
    }

    @Override
    public float getHeight() {
        if (type == SHADE_LIGHT.GAMMA_SHADOW)
            return super.getHeight();
        if (type == SHADE_LIGHT.HIGLIGHT)
            return super.getHeight();
        return GridConst.CELL_H;
    }

    public float getBaseAlpha() {
        return baseAlpha;
    }

    public void setBaseAlpha(float baseAlpha) {
        if (isAnimated()) {
            alphaAction.reset();
            alphaAction.setStart(this.baseAlpha);
            alphaAction.setEnd(baseAlpha);
            addAction(alphaAction);
            alphaAction.setTarget(this);
            alphaAction.setDuration(0.25f + (Math.abs(this.baseAlpha - baseAlpha)) / 2);
        } else
            this.baseAlpha = baseAlpha;

        if (isColored())
            teamColor = initTeamColor();
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isIgnored())
            return;

        super.draw(batch, parentAlpha);
    }

    @Override
    public boolean isIgnored() {
        // check cell is visible TODO
        return super.isIgnored();
    }

    @Override
    public boolean isAlphaFluctuationOn() {
        if (!alphaFluctuationOn)
            return false;
        return alphaFluctuation;
    }

    @Override
    public Color getTeamColor() {
        return teamColor;
    }

    public Color initTeamColor() {
//for each coordinate?
        //default per dungeon
//        Eidolons.getGame().getMaster().getObjCache()
//        IlluminationRule.


        COLOR_THEME colorTheme = null;
        if (type == SHADE_LIGHT.LIGHT_EMITTER) {
            for (Structure sub : Eidolons.game.getStructures()) {
                if (sub.isLightEmitter()) {
//                    if (sub.getCoordinates().equals(new Coordinates(x,y)))
                    colorTheme = new EnumMaster<COLOR_THEME>().
                     retrieveEnumConst(COLOR_THEME.class, sub.getProperty(PROPS.COLOR_THEME, true));
                    if (colorTheme != null)
                        break;
                }
            }
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
    protected void alphaFluctuation(float delta) {
        super.alphaFluctuation(getContent(), delta);
    }

    @Override
    protected float getAlphaFluctuationMin() {
        if (type == SHADE_LIGHT.GAMMA_SHADOW)
            return baseAlpha / 2;
        return baseAlpha * 3 / 5;
    }

    @Override
    protected float getAlphaFluctuationMax() {
        if (type == SHADE_LIGHT.GAMMA_SHADOW)
            return baseAlpha * 5 / 6;
        return baseAlpha;
    }

    @Override
    protected float getAlphaFluctuationPerDelta() {
        return new Float(RandomWizard.getRandomInt((int) (super.getAlphaFluctuationPerDelta() * 50))) / 100;
    }

    @Override
    public void act(float delta) {
        if (!SuperActor.isCullingOff())
            if (!DungeonScreen.getInstance().controller.
             isWithinCamera(
              this
//              getX() +3*GridConst.CELL_W, getY() + getHeight(), 2 * getWidth(), 2 * getHeight()
             )) {
                return;
            }
        if (isAnimated())
            baseAlpha = alphaAction.getValue();
        super.act(delta);
    }

    private boolean isAnimated() {
        return true;
    }

    @Override
    public void setPosition(float x, float y) {
        if (originalX == null)
            originalX = x;
        if (originalY == null)
            originalY = y;
        super.setPosition(x, y);
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

                            BaseView view = DungeonScreen.getInstance().getGridPanel().getUnitMap().get(sub);
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
}
