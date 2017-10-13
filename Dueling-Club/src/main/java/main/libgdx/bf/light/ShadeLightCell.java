package main.libgdx.bf.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.entity.DataModel;
import main.entity.obj.Structure;
import main.game.core.Eidolons;
import main.libgdx.bf.GridConst;
import main.libgdx.bf.SuperActor;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.RandomWizard;

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

        DataModel obj = null;
        if (type == SHADE_LIGHT.GAMMA_LIGHT || !Eidolons.game.isStarted())
            obj = Eidolons.game.getDungeon();
        else {
            obj = Eidolons.game.getDungeon();
            for (Structure sub : Eidolons.game.getStructures()) {
                if (sub.isLightEmitter()) {
                    obj = sub;
                    break;
                }
            }
        }
        if (obj == null)
            return DEFAULT_COLOR;
        COLOR_THEME color = null;
//        new EnumMaster<COLOR_THEME>().
//         retrieveEnumConst(COLOR_THEME.class, obj.getProperty(PROPS.COLOR_THEME, true));
        if (color != null)
            switch (color) {
                case BLUE:
                    return new Color(0.7f, 0.8f, 1f, 1);
                case GREEN:
                    return new Color(0.7f, 0.9f, 0.7f, 1);
                case RED:
                    return new Color(1f, 0.7f, 0.7f, 1);
                case DARK:
                    return new Color(0.6f, 0.5f, 0.7f, 1);
                case LIGHT:
                    return new Color(1f, 1f, 1f, 1);
                case YELLOW:
                    return new Color(1, 0.9f, 0.7f, 1);
                case PURPLE:
                    return new Color(0.8f, 0.7f, 0.9f, 1);
            }

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
        if (x == 3)
            if (y == 5)
                x = x;
        if (!SuperActor.isCullingOff())
            if (!DungeonScreen.getInstance().getController().
             isWithinCamera(

              getX() +3*GridConst.CELL_W, getY() + getHeight(), 2 * getWidth(), 2 * getHeight()
             )) {
                return;
            }

        super.act(delta);
    }

}
