package eidolons.libgdx.gui.panels.dc.actionpanel;

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
import eidolons.content.PARAMS;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;
import main.system.math.MathMaster;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class OrbElement extends SuperActor {
    private static final String EMPTY_PATH = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "new", "orb 64.png");
    private static final String OVERLAY_PATH = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "2018","orbs", "overlay.png");
    private Label label;
    private Image background;
    private Image gem;
    private ImageContainer gemLight;
    private Image overlay;

    private Image icon;
    private Image lighting;
    private TextureRegion orbRegion;
    private TextureRegion iconRegion;
    private int orbFullness = 62;
    private int orbFullnessPrevious = 62;
    private float fluctuation = 0;

    public OrbElement(TextureRegion iconRegion, TextureRegion texture, String value, PARAMS param) {
        icon = new Image(iconRegion);
        orbRegion = texture;
        this.iconRegion = iconRegion;
        label = new Label(value, StyleHolder.
         getSizedLabelStyle(FONT.AVQ, 18));
        calculateOrbFullness(value);
        addActor( background = new Image(getOrCreateR(EMPTY_PATH)));
//        addActor(icon);
        icon.setPosition(orbRegion.getRegionWidth() / 2 - icon.getWidth() / 2,
         orbRegion.getRegionHeight() / 2 - icon.getHeight() / 2);

//        addActor( overlay = new Image(getOrCreateR(OVERLAY_PATH)));
//        String p = ContentManager.getBaseParameterFromCurrent(param).getName();
//        addActor(gem = new Image(getOrCreateR(getGemPath(p))));
//        addActor( gemLight = new ImageContainer((getGemLightPath(p))));
//        gemLight.setAlphaTemplate(ALPHA_TEMPLATE.HIGHLIGHT);

        //TODO ORB GOES TO THE FORE ON HOVER
    }

    private String getGemLightPath(String value) {
        return getGemPath(value + " light");
    }
    private String getGemPath(String value) {
        return StrPathBuilder.build(
         PathFinder.getComponentsPath(), "2018","orbs", "gem",
         value+ ".png ");
    }

    public OrbElement(PARAMS param, String value) {
        this(getOrCreateR(
         StringMaster.getAppendedImageFile(
          ImageManager.getValueIconPath(param), " alpha")),
         getOrCreateR("/UI/components/new/orb " +
          param.getName() +
          ".png"), value, param);

//        lighting = new Image(getOrCreateR(SHADE_LIGHT.LIGHT_EMITTER.getTexturePath()));
//        lighting.sizeBy(0.3f);
        Texture texture = TextureCache.getOrCreate("/UI/components/new/orb " +
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
        if (gemLight != null)
            alphaFluctuation(gemLight, delta);
        super.act(delta);
    }

    @Override
    protected float getAlphaFluctuationPerDelta() {
        return fluctuation;
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
            if (lighting != null)
                lighting.setColor(1, 1, 1, 0.5f + new Float(orbFullness) / 100);
        } else
            fluctuation = MathMaster.getMinMax(
             super.getAlphaFluctuationPerDelta() / (1 + orbFullness) * 30, 0.4f, 0.7f);

    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.flush();
        if (fluctuatingAlpha != 1)
            batch.setColor(new Color(1, 1, 1, 1));
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
//                main.system.ExceptionMaster.printStackTrace(e); //TODO spams into console!
//                logged = true;
//            }

        }
        if (Eidolons.game.isDebugMode() || Gdx.input.isKeyPressed(Keys.ALT_LEFT))
            label.draw(batch, parentAlpha);

//TODO if hover
//        batch.draw(iconRegion, 30, 30);
    }
}
