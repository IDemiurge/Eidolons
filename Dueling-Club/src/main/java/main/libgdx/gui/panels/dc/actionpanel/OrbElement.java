package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import main.content.PARAMS;
import main.game.core.Eidolons;
import main.libgdx.StyleHolder;
import main.libgdx.bf.SuperActor;
import main.libgdx.texture.TextureCache;
import main.system.graphics.FontMaster.FONT;
import main.system.math.MathMaster;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class OrbElement extends SuperActor {
    private static final String EMPTY_PATH = "/UI/components/new/orb 64.png";
    private   Label label;
    private   Image background;
    private   Image icon;
    private   Image lighting;
    private TextureRegion orbRegion;
    private TextureRegion iconRegion;
    private int orbFullness = 62;
    private int orbFullnessPrevious = 62;
    private float fluctuation=0;

    public OrbElement(TextureRegion iconRegion, TextureRegion texture, String value) {
        background = new Image(getOrCreateR(EMPTY_PATH));
        icon = new Image(iconRegion);
        orbRegion = texture;
        this.iconRegion = iconRegion;
        label = new Label(value, StyleHolder.
         getSizedLabelStyle(FONT.AVQ, 18));
        calculateOrbFullness(value);
        addActor(background);
//        addActor(icon);
        icon.setPosition(orbRegion.getRegionWidth() / 2 - icon.getWidth() / 2,
         orbRegion.getRegionHeight() / 2 - icon.getHeight() / 2);


    }

    public OrbElement(PARAMS param, String value) {
        this(getOrCreateR("/UI/value icons/" +
          param.getName() +
          ".png"),
         getOrCreateR("/UI/components/new/orb " +
          param.getName() +
          ".png"), value);

//        lighting = new Image(getOrCreateR(SHADE_LIGHT.LIGHT_EMITTER.getTexturePath()));
//        lighting.sizeBy(0.3f);
        Texture texture =TextureCache. getOrCreate ("/UI/components/new/orb " +
         param.getName() +
         " border.png");
        if (texture == TextureCache.getEmptyTexture()) return;
        lighting = new Image(texture);
        addActor(lighting);
            lighting.setPosition(-15, -15);
    }

    @Override
    public void act(float delta) {
//        alphaFluctuation(icon, delta);
        if (lighting != null)
            alphaFluctuation(lighting, delta);
        super.act(delta);
    }

    @Override
    protected float getAlphaFluctuationPerDelta() {
        return fluctuation ;
    }

    @Override
    protected void alphaFluctuation(float delta) {

    }

    public TextureRegion getIconRegion() {
        return iconRegion;
    }

    public void updateValue(String val) {
        calculateOrbFullness(val);
    }

    private void calculateOrbFullness(String value) {
        orbFullnessPrevious = orbFullness;
        label.setText(value);

        label.setStyle(StyleHolder.
         getSizedLabelStyle(FONT.MAIN, 20 - value.length() / 2));

        label.setX(orbRegion.getRegionWidth() / 2
         - label.getWidth() / 2);
        label.setY(orbRegion.getRegionHeight() / 2 - label.getHeight() / 2);
        Vector2 v2 = localToStageCoordinates(new Vector2(label.getX(), label.getY()));
        label.setPosition(v2.x, v2.y);


        final String[] split = value.split("/");
        if (split.length == 2) {
            final int cur = Integer.valueOf(split[0]);
            final int max = Integer.valueOf(split[1]);
            orbFullness = Math.min(Math.round(cur / (max / 62f)), 62);
        } else {
            orbFullness = 62;
        }
        if (!isAlphaFluctuationOn()) {
            if (lighting!=null )
                lighting.setColor(1,1,1, 0.5f + new Float(orbFullness)/100 );
        } else
        fluctuation=MathMaster.getMinMax(
         super.getAlphaFluctuationPerDelta() /(1+orbFullness)*30, 0.4f, 0.7f);

    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.flush();
        if (fluctuatingAlpha!=1)
            batch.setColor(new Color(1,1,1,1));
        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(getX(), getY(), 62, orbFullness);
        getStage().calculateScissors(clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
        batch.draw(orbRegion, getX(), getY());
        batch.flush();
        try {
            ScissorStack.popScissors();
        } catch (Exception e) {
//            if (!logged) {
//                e.printStackTrace(); //TODO spams into console!
//                logged = true;
//            }

        }
        if (Eidolons.game.isDebugMode() || Gdx.input.isKeyPressed(Keys.ALT_LEFT))
            label.draw(batch, parentAlpha);

//TODO if hover
//        batch.draw(iconRegion, 30, 30);
    }
}
