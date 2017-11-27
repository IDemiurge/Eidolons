package main.libgdx.bf.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.PROPS;
import main.entity.obj.Obj;
import main.entity.obj.Structure;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.rules.mechanics.IlluminationRule;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.core.Eidolons;
import main.libgdx.GdxColorMaster;
import main.libgdx.bf.GridConst;
import main.libgdx.bf.GridMaster;
import main.libgdx.bf.SuperActor;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;

import java.awt.*;

/**
 * Created by JustMe on 8/28/2017.
 */
public class ShadeLightCell extends SuperContainer {

    private static final Color DEFAULT_COLOR = new Color(1, 0.9f, 0.7f, 1);
    private static boolean alphaFluctuation = true;
    private int x;
    private int y;
    private SHADE_LIGHT type;
    private float baseAlpha;
    private Float originalX;
    private Float originalY;

    public ShadeLightCell(SHADE_LIGHT type, int x, int y) {
        super(new Image(TextureCache.getOrCreateR(type.getTexturePath())));
        this.type = type;
        if (isColored()) {
            teamColor = initTeamColor();
            if (getTeamColor() != null)
                getContent().setColor(getTeamColor());
        }
        this.x = x;
        this.y = y;
    }

    public static void setAlphaFluctuation(boolean alphaFluctuation) {
        ShadeLightCell.alphaFluctuation = alphaFluctuation;
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
        if (type == SHADE_LIGHT.HIGLIGHT)
            return super.getWidth();
        return GridConst.CELL_W;
    }

    @Override
    public float getHeight() {
        if (type == SHADE_LIGHT.HIGLIGHT)
            return super.getHeight();
        return GridConst.CELL_H;
    }

    public float getBaseAlpha() {
        return baseAlpha;
    }

    public void setBaseAlpha(float baseAlpha) {
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
        if (!alphaFluctuation)
            return false;
        return true;
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
                    if (sub.getCoordinates().equals(new Coordinates(x,y)))
                    colorTheme = new EnumMaster<COLOR_THEME>().
                     retrieveEnumConst(COLOR_THEME.class, sub.getProperty(PROPS.COLOR_THEME, true));
                    if (colorTheme != null)
                        break;
                }
            }
        }
        if (colorTheme == null) {
            Dungeon obj = Eidolons.game.getDungeon();
            colorTheme =obj.getColorTheme();
        }

        Color c = null;
        if (colorTheme != null)
            c = GdxColorMaster.getColorForTheme(colorTheme);
        if (c != null) return c;
        return DEFAULT_COLOR;
    }

    @Override
    protected void alphaFluctuation(float delta) {
        super.alphaFluctuation((Image) getContent(), delta);
    }

    @Override
    protected float getAlphaFluctuationMin() {
        return baseAlpha * 3 / 5;
    }

    @Override
    protected float getAlphaFluctuationMax() {
        return baseAlpha;
    }

    @Override
    protected float getAlphaFluctuationPerDelta() {
        return new Float(RandomWizard.getRandomInt((int) (super.getAlphaFluctuationPerDelta() * 50))) / 100;
    }

    @Override
    protected float getFluctuatingAlpha() {
        return super.getFluctuatingAlpha();
    }

    @Override
    public void act(float delta) {
        if (!SuperActor.isCullingOff())
            if (!DungeonScreen.getInstance().getController().
             isWithinCamera(
              this
//              getX() +3*GridConst.CELL_W, getY() + getHeight(), 2 * getWidth(), 2 * getHeight()
             )) {
                return;
            }

        super.act(delta);
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
        for (Obj sub : IlluminationRule.getEffectCache().keySet()) {
            if (sub instanceof Unit)
                continue; //TODO illuminate some other way for units...

            if (sub instanceof Structure) {
                if (sub.getCoordinates().x == x)
                    if (sub.getCoordinates().y == y)
                        if (((Structure) sub).isOverlaying()) {
                            DIRECTION d = ((Structure) sub).getDirection();
                            if (d == null) {
                                setScale(0.66f, 0.66f);
                                continue;
                            }

                            setScale(d.isGrowX() == null ? 1 : 0.66f, d.isGrowY() == null ? 1 : 0.66f);
                            Dimension dim = GridMaster.getOffsetsForOverlaying(d,
                             (int) getWidth(),
                             (int) getHeight());
                            offsetX -= dim.height;
                            offsetY -= dim.width;
                            //so if 2+ overlays, will be centered between them...
                        }
            }

        }

        setPosition(originalX + offsetX / 3, originalY + offsetY / 3);
    }
}
