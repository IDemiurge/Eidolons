package main.libgdx.bf;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by JustMe on 8/17/2017.
 */
public abstract class SuperActor extends Group implements Borderable {
    protected static final float DEFAULT_ALPHA_FLUCTUATION = 0.6f;
    protected static final float DEFAULT_ALPHA_MIN = 0.2f;
    protected static final float DEFAULT_ALPHA_MAX = 1f;
    protected Image border = null;
    protected TextureRegion borderTexture;
    protected float borderAlpha = 1f;
    protected boolean alphaGrowing=false;
    protected boolean teamColorBorder;
    protected Color teamColor;
    protected float scaledWidth;
    protected float scaledHeight;
    protected boolean hovered;
    private boolean active;

    @Override
    public TextureRegion getBorder() {
        return borderTexture;
    }

    @Override
    public void setBorder(TextureRegion texture) {
        if (border != null) {
            removeActor(border);
        }
        alphaGrowing=false;
        borderAlpha=0.75f;

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
            border.setHeight(getHeight() + 12);
            border.setWidth(getWidth() + 12);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        alphaFluctuation(border, delta);
    }

    protected void alphaFluctuation(Image image, float delta) {
        if (!isAlphaFluctuationOn())
            return;
        if (image == null)
            return;
        Color color = image.getColor();
        if (isTeamColorBorder())
            if (getTeamColorBorder()!=null ) {
                color = getTeamColorBorder();
            }
        borderAlpha = borderAlpha + getAlphaFluctuation(delta);
        if (image != null) //TODO control access!
            image.setColor(color.r, color.g, color.b, borderAlpha);

    }
    protected float getAlphaFluctuation(float delta) {
        float fluctuation =delta* DEFAULT_ALPHA_FLUCTUATION;
        if (borderAlpha <=DEFAULT_ALPHA_MIN- fluctuation)
            alphaGrowing = !alphaGrowing;
        else if (borderAlpha > DEFAULT_ALPHA_MAX+fluctuation)
            alphaGrowing = !alphaGrowing;

        if (!alphaGrowing)
            return -fluctuation;
        return fluctuation;
    }

    protected boolean isAlphaFluctuationOn() {
        return true;
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
    public void setTeamColor(Color teamColor) {
        this.teamColor = teamColor;
    }
    @Override
    public Color getTeamColorBorder() {
        return teamColor;
    }

    public void setScaledWidth(float scaledWidth) {
        this.scaledWidth = scaledWidth;
    }

    public float getScaledWidth() {
        return scaledWidth;
    }

    public void setScaledHeight(float scaledHeight) {
        this.scaledHeight = scaledHeight;
    }

    public float getScaledHeight() {
        return scaledHeight;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }}
