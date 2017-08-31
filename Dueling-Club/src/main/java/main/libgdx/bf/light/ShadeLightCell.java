package main.libgdx.bf.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.RandomWizard;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;

/**
 * Created by JustMe on 8/28/2017.
 */
public class ShadeLightCell extends SuperContainer {

    private float baseAlpha;

    public ShadeLightCell(SHADE_LIGHT type, int x, int y) {
        super(new Image(TextureCache.getOrCreate(type.getTexturePath())));

    }

    public float getBaseAlpha() {
        return baseAlpha;
    }

    public void setBaseAlpha(float baseAlpha) {
        this.baseAlpha = baseAlpha;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.OPTIMIZATION_ON))
            if (!DungeonScreen.getInstance().getController().
             isWithinCamera(getX()+getWidth(), getY()+getHeight(), 2*getWidth(), 2*getHeight())) {
                return;
            }
        super.draw(batch, parentAlpha);
    }

    @Override
    public Color getTeamColor() {
        return Color.WHITE;
    }

    @Override
    protected void alphaFluctuation(float delta) {
        super.alphaFluctuation((Image) getContent(), delta);
    }

    @Override
    protected float getAlphaFluctuationMin() {
        return baseAlpha*4/5;
    }

    @Override
    protected float getAlphaFluctuationMax() {
        return baseAlpha;
    }

    @Override
    protected float getAlphaFluctuationPerDelta() {
        return  new Float(RandomWizard.getRandomInt((int) (super.getAlphaFluctuationPerDelta()*50)))/100;
    }

    @Override
    protected float getFluctuatingAlpha() {
        return super.getFluctuatingAlpha();
    }

    @Override
    public void act(float delta) {
        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.OPTIMIZATION_ON))
            if (!DungeonScreen.getInstance().getController().
             isWithinCamera(getX()+getWidth(), getY()+getHeight(), 2*getWidth(), 2*getHeight()) ) {
                return;
            }

        super.act(delta);
    }
}
