package libgdx.gui.panels.dc.actionpanel;

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
import eidolons.game.core.Core;
import libgdx.StyleHolder;
import libgdx.bf.SuperActor;
import libgdx.bf.generic.ImageContainer;
import libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;
import main.system.math.MathMaster;

import static libgdx.texture.TextureCache.getOrCreateR;

public class OrbElement extends SuperActor {
    private static final String EMPTY_PATH = StrPathBuilder.build(
            PathFinder.getComponentsPath(), "dc", "orbs", "orb 64.png");
    private final PARAMS parameter;
    private final Label label;
    private Image gem;
    private ImageContainer gemLight;
    private Image overlay;

    private Image lighting;
    private final TextureRegion orbRegion;
    private final TextureRegion iconRegion;
    private int orbFullness = 62;
    private float fluctuation = 0;

    public OrbElement(TextureRegion iconRegion, TextureRegion texture, String value, PARAMS param) {
        Image icon = new Image(iconRegion);
        orbRegion = texture;
        this.iconRegion = iconRegion;
        this.parameter = param;
        label = new Label(value, StyleHolder.
                getSizedLabelStyle(FONT.AVQ, 18));
        calculateOrbFullness(value);
        addActor( new Image(TextureCache.getOrCreateR(EMPTY_PATH)));
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

    public OrbElement(PARAMS param, String value) {
        this(TextureCache.getOrCreateR(
                StringMaster.getAppendedImageFile(
                        ImageManager.getValueIconPath(param), " alpha")),
                TextureCache.getOrCreateR(getOrbPath(param.getName())), value, param);

        //        lighting = new Image(getOrCreateR(SHADE_LIGHT.LIGHT_EMITTER.getTexturePath()));
        //        lighting.sizeBy(0.3f);
        Texture texture = TextureCache.getOrCreate(StringMaster.getAppendedImageFile(
                getOrbPath(param.getName()), " border"
        ));

        if (texture == TextureCache.getMissingTexture()) return;
        lighting = new Image(texture);
        addActor(lighting);
        lighting.setPosition(-15, -15);

    }

    private static String getOrbPath(String value) {
        return getPath() + "/orb " + value +
                ".png";
    }

    private static String getPath() {
        return StrPathBuilder.build(
                PathFinder.getComponentsPath(), "dc", "orbs");
    }

    private String getGemLightPath(String value) {
        return getGemPath(value + " light");
    }

    private String getGemPath(String value) {
        return StrPathBuilder.build(getPath(), "gem",
                value + ".png ");
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

    public boolean updateValue(String val) {
        return calculateOrbFullness(val);
    }

    private boolean calculateOrbFullness(String value) {
        int orbFullnessPrevious = orbFullness;
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
            final int cur = Integer.parseInt(split[0]);
            final int max = Integer.parseInt(split[1]);
            orbFullness = Math.min(Math.round(cur / (max / 62f)), 62);
            if (cur <= 0) {
                orbFullness = 0;
            }
        } else {
            orbFullness = 62;
        }
        if (orbFullnessPrevious == orbFullness)
            return false;

        if (!isAlphaFluctuationOn()) {
            if (lighting != null)
                lighting.setColor(1, 1, 1, 0.5f + (float) orbFullness / 100);
        } else
            fluctuation = MathMaster.getMinMax(
                    super.getAlphaFluctuationPerDelta() / (1 + orbFullness) * 30, 0.4f, 0.7f);

        return true;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
//        if (EidolonsGame.isAltControlPanel())
//            return;
        if (getColor().a == 0)
            return;
        super.draw(batch, parentAlpha);
        batch.flush();
        if (fluctuatingAlpha != 1) {
            batch.setColor(new Color(1, 1, 1, 1));
        } else {

            batch.setColor(new Color(1, 1, 1, getColor().a));

        }
        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(getX(), getY(), 62, Math.max(1, orbFullness));
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
        if (Core.game.isDebugMode() || Gdx.input.isKeyPressed(Keys.ALT_LEFT))
            label.draw(batch, parentAlpha);

        //TODO if hover
        //        batch.draw(iconRegion, 30, 30);
    }

    public PARAMS getParameter() {
        return parameter;
    }
}
