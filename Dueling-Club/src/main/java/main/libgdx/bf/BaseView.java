package main.libgdx.bf;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.system.GuiEventManager;

import static main.system.GuiEventType.CALL_BLUE_BORDER_ACTION;

public class BaseView extends Group implements Borderable {
    private static final float DEFAULT_ALPHA_FLUCTUATION = 0.6f;
    private static final float DEFAULT_ALPHA_MIN = 0.2f;
    private static final float DEFAULT_ALPHA_MAX = 1f;
    protected Image portrait;
    protected Image border = null;
    private TextureRegion borderTexture;
    private float borderAlpha = 1f;
    private boolean alphaGrowing=false;
    private boolean teamColorBorder;
    private Color teamColor;
    private float scaledWidth;
    private float scaledHeight;
    private boolean hovered;


    public BaseView(UnitViewOptions o) {
        this(o.getPortrateTexture());
    }

    public BaseView(TextureRegion portraitTexture) {
        portrait = new Image(portraitTexture);
        addActor(portrait);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    event.handle();
                    GuiEventManager.trigger(CALL_BLUE_BORDER_ACTION, BaseView.this);
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!isAlphaFluctuationOn())
            return;
        if (border == null)
            return;
        Color color = border.getColor();
        if (isTeamColorBorder()) {
            color = getTeamColorBorder();
        }
        borderAlpha = borderAlpha + getAlphaFluctuation(delta);
        border.setColor(color.r, color.g, color.b, borderAlpha);
    }

    private float getAlphaFluctuation(float delta) {
        float fluctuation =delta* DEFAULT_ALPHA_FLUCTUATION;
        if (borderAlpha <=DEFAULT_ALPHA_MIN- fluctuation)
            alphaGrowing = !alphaGrowing;
        else if (borderAlpha > DEFAULT_ALPHA_MAX+fluctuation)
            alphaGrowing = !alphaGrowing;

        if (!alphaGrowing)
            return -fluctuation;
        return fluctuation;
    }

    private boolean isAlphaFluctuationOn() {
        return true;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        portrait.setSize(getWidth(), getHeight());

    }

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

    private void updateBorderSize() {
        if (border != null) {
            border.setX(-6);
            border.setY(-6);
            border.setHeight(getHeight() + 12);
            border.setWidth(getWidth() + 12);
        }
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
}
