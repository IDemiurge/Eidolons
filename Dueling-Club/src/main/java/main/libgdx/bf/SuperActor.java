package main.libgdx.bf;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.core.Eidolons;
import main.libgdx.GdxColorMaster;
import main.libgdx.bf.mouse.InputController;
import main.system.auxiliary.RandomWizard;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 8/17/2017.
 */
public abstract class SuperActor extends Group implements Borderable {
    protected static final float DEFAULT_ALPHA_FLUCTUATION = 0.4f;
    protected static final float DEFAULT_ALPHA_MIN = 0.2f;
    protected static final float DEFAULT_ALPHA_MAX = 1f;
    protected static boolean alphaFluctuationOn=true;
    protected Image border = null;
    protected TextureRegion borderTexture;
    protected float fluctuatingAlpha = 1f;
    protected boolean alphaGrowing = false;
    protected boolean teamColorBorder;
    protected Color teamColor;
    protected float scaledWidth=1;
    protected float scaledHeight=1;
    protected boolean hovered;
    protected boolean active;
    private static boolean cullingOff;
    private boolean hoverResponsive;
    private float alphaStep=-1;
    private float fluctuatingAlphaPauseDuration;
    private float fluctuatingFullAlphaDuration;
    private float alphaPause;
    private float fluctuatingAlphaRandomness;

    public static boolean isCullingOff() {
        return cullingOff;
    }

    @Override
    public TextureRegion getBorder() {
        return borderTexture;
    }

    public static void setCullingOff(boolean cullingOff) {
        SuperActor.cullingOff = cullingOff;
    }

    @Override
    public void setBorder(TextureRegion texture) {
        if (border != null) {
            removeActor(border);
        }
        alphaGrowing = false;
        fluctuatingAlpha = 0.75f;

        if (texture == null) {
            border = null;
            borderTexture = null;
            setTeamColorBorder(false);
        } else {
            addActor(border = new Image(texture));
            borderTexture = texture;
            updateBorderSize();
        }
    }

    protected void updateBorderSize() {
        if (border != null) {
            border.setX(-6);
            border.setY(-6);
            if (getHeight()==0)
                return ;
            if (getWidth()==0)
                return ;
            border.setHeight(getHeight() + 12);
            border.setWidth(getWidth() + 12);
        }
    }

    protected boolean isIgnored() {
        if (!isVisible())
            return true;
//        if (getColor().a == 0)
//            return true; TODO why was this here?.. not to draw?
        if (isCullingOff())
            return false;
            if (! getController().
             isWithinCamera(
              this)) {
                return true;
            }
        return false;
    }

    public InputController getController() {
        return Eidolons.getScreen().getController();
    }

    @Override
    public void act(float delta) {
//        if (isIgnored())
//            return;
        if (isTransform()) {
            if (isTransformDisabled())
                setTransform(false);
        } else {
            if (!isTransformDisabled())
                setTransform(true);
        }
        super.act(delta);
        alphaFluctuation(delta);
    }

    protected boolean isTransformDisabled() {
        if (getScaleX() != 1) {
            return false;
        }
        if (getScaleY() != 1) {
            return false;
        }
        return true;
    }

    protected void alphaFluctuation(float delta) {
        alphaFluctuation(border, delta);
    }

    protected void alphaFluctuation(Actor image, float delta) {
        if (!isAlphaFluctuationOn())
            return;
        if (alphaPause>0){
            alphaPause =   alphaPause - delta ;
            if (alphaPause>=0) return ;
            delta = delta - (-alphaPause);
            if (delta<=0)
            return;
        }
        Color color =null ;
        if (image != null)
            color = image.getColor();
        else color = GdxColorMaster.WHITE;

        if (isTeamColorBorder())
            if (getTeamColor() != null) {
                color = getTeamColor();
            }
        fluctuatingAlpha = fluctuatingAlpha +  randomizeAlpha(getAlphaFluctuation(delta));

        fluctuatingAlpha = MathMaster.getMinMax(
         fluctuatingAlpha, getAlphaFluctuationMin(),
         getAlphaFluctuationMax());
        if (image != null) //TODO control access!
            image.setColor(color.r, color.g, color.b, fluctuatingAlpha);

    }

    private float randomizeAlpha(float fluctuatingAlpha) {
        if (fluctuatingAlphaRandomness>0){
//            if (RandomWizard.chance())
            int alpha =(int) (fluctuatingAlphaRandomness * 100);
            int mod =
             RandomWizard.getRandomIntBetween(
              -alpha, alpha);
            return
             fluctuatingAlpha+
             fluctuatingAlpha/100*mod;
        }
        return fluctuatingAlpha;
    }

    protected float getAlphaFluctuation(float delta) {
        float fluctuation = delta * getAlphaFluctuationPerDelta();
        if (getFluctuatingAlpha() <= getAlphaFluctuationMin()  )
        {
            if (alphaGrowing){
                    alphaPause = getFluctuatingFullAlphaDuration();
            }else {
                alphaPause = getFluctuatingAlphaPauseDuration();
            }
            alphaGrowing = !alphaGrowing;
        }
        else if (fluctuatingAlpha >= getAlphaFluctuationMax()  )
            alphaGrowing = !alphaGrowing;

        if (!alphaGrowing)
            return -fluctuation;
        return fluctuation;
    }

    protected float getFluctuatingAlpha() {
        return fluctuatingAlpha;
    }

    protected float getAlphaFluctuationMin() {
        return DEFAULT_ALPHA_MIN;
    }

    protected float getAlphaFluctuationMax() {
        return DEFAULT_ALPHA_MAX;
    }

    protected float getAlphaFluctuationPerDelta() {
        if (alphaStep!=-1 )
            return alphaStep;
        return DEFAULT_ALPHA_FLUCTUATION;
    }

    public boolean isAlphaFluctuationOn() {
        return alphaFluctuationOn;
    }

    public static void setAlphaFluctuationOn(boolean alphaFluctuationOn) {
        SuperActor.alphaFluctuationOn = alphaFluctuationOn;
    }

    public void setFluctuatingAlpha(float fluctuatingAlpha) {
        this.fluctuatingAlpha = fluctuatingAlpha;
    }

    @Override
    public boolean isTeamColorBorder() {
        return teamColorBorder;
    }

    @Override
    public void setTeamColorBorder(boolean teamColorBorder) {
        this.teamColorBorder = teamColorBorder;
    }

    @Override
    public Color getTeamColor() {
        return teamColor;
    }

    @Override
    public void setTeamColor(Color teamColor) {
        this.teamColor = teamColor;
    }

    public float getScaledWidth() {
        return scaledWidth;
    }

    public void setScaledWidth(float scaledWidth) {
        this.scaledWidth = scaledWidth;
    }

    public float getScaledHeight() {
        return scaledHeight;
    }

    public void setScaledHeight(float scaledHeight) {
        this.scaledHeight = scaledHeight;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isActive() {
        if (active)
            return active;
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isHoverResponsive() {
        return hoverResponsive;
    }

    public void setHoverResponsive(boolean hoverResponsive) {
        this.hoverResponsive = hoverResponsive;
    }

    public void setAlphaStep(float alphaStep) {
        this.alphaStep = alphaStep;
    }

    public float getAlphaStep() {
        return alphaStep;
    }

    public void setFluctuatingAlphaPauseDuration(float fluctuatingAlphaPauseDuration) {
        this.fluctuatingAlphaPauseDuration = fluctuatingAlphaPauseDuration;
    }

    public float getFluctuatingAlphaPauseDuration() {
        return fluctuatingAlphaPauseDuration;
    }

    public void setFluctuatingFullAlphaDuration(float fluctuatingFullAlphaDuration) {
        this.fluctuatingFullAlphaDuration = fluctuatingFullAlphaDuration;
    }

    public float getFluctuatingFullAlphaDuration() {
        return fluctuatingFullAlphaDuration;
    }

    public void setFluctuatingAlphaRandomness(float fluctuatingAlphaRandomness) {
        this.fluctuatingAlphaRandomness = fluctuatingAlphaRandomness;
    }

    public float getFluctuatingAlphaRandomness() {
        return fluctuatingAlphaRandomness;
    }
}
