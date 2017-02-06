package main.libgdx.bf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.InitiativePanelParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.OnDemandEventCallBack;
import main.system.auxiliary.LogMaster;

import java.util.concurrent.atomic.AtomicInteger;

public class UnitView extends BaseView {
    private static AtomicInteger lastId = new AtomicInteger(1);
    private final int curId;
    private Image arrow;
    private Image icon;
    private int baseHeight;
    private int baseWidth;
    private int arrowRotation;
    private int clockVal;
    private FrameBuffer fbo;
    private Texture portraitTexture;
    private Container<Image> imageContainer;
    private Texture clockTexture;
    private boolean needRepaint = true;
    private Label initiativeStrVal;
    private float alpha = 1;

    public UnitView(UnitViewOptions o) {
        super(o);
        curId = lastId.getAndIncrement();
        init(o.getDirectionPointerTexture(), o.getDirectionValue(), o.getClockTexture(), o.getClockValue(), o.getPortrateTexture(), o.getIconTexture());
    }

    private void init(Texture arrowTexture, int arrowRotation, Texture clockTexture, int clockVal, Texture portraitTexture, Texture iconTexture) {
        this.arrowRotation = arrowRotation + 90;
        this.clockVal = clockVal;
        this.portraitTexture = portraitTexture;
        portrait = new Image(portraitTexture);

        baseHeight = portraitTexture.getHeight();
        baseWidth = portraitTexture.getHeight();
        setHeight(baseHeight * getScaleY());
        setWidth(baseWidth * getScaleX());

        fbo = new FrameBuffer(Pixmap.Format.RGBA4444, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        imageContainer = new Container<>();
        imageContainer.width(getW()).height(getH()).bottom().left();
        addActor(imageContainer);

        if (clockTexture != null) {
            this.clockTexture = clockTexture;
            initiativeStrVal = new Label("[#00FF00FF]" + String.valueOf(clockVal) + "[]", StyleHolder.getDefaultLabelStyle());
            initiativeStrVal.setPosition(
                    getWidth() - clockTexture.getWidth() / 2 - initiativeStrVal.getWidth() / 2,
                    clockTexture.getHeight() / 2 - initiativeStrVal.getHeight() / 2);
        }

        if (arrowTexture != null) {
            arrow = new Image(arrowTexture);
            addActor(arrow);
            arrow.setOrigin(getWidth() / 2 + arrow.getWidth(), getHeight() / 2 + arrow.getHeight());
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
            arrow.setY(0);
        }

        if (iconTexture != null) {
            icon = new Image(iconTexture);
            addActor(icon);
            icon.setX(0);
            icon.setY(getHeight() - icon.getImageHeight());
        }
//        setDebug(true);
        needRepaint = true;
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        setHeight(baseHeight * getScaleY());
        setWidth(baseWidth * getScaleX());
        if (arrow != null) {
            arrow.setOrigin(getWidth() / 2 + arrow.getWidth(), getHeight() / 2 + arrow.getHeight());
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
        }

        needRepaint = true;
    }

    @Override
    public void setBorder(Image image) {
        border = image;
        if (border != null) {
            updateBorderSize();
        }
        needRepaint = true;
    }

    @Override
    public void updateBorderSize() {
        super.updateBorderSize();
        border.setHeight(portraitTexture.getWidth() + 8);
        border.setWidth(portraitTexture.getHeight() + 8);
    }

    public void setVisibleVal(int val) {
        val = Math.min(0, val);
        val = Math.max(100, val);
        alpha = val * 0.01f;
        needRepaint = true;
    }

    @Override
    public void act(float delta) {
        if (needRepaint) {
            SpriteBatch sp = new SpriteBatch(1);
            fbo.begin();
            Gdx.gl.glClearColor(1, 1, 1, 0);
            Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
            sp.begin();


            sp.setColor(1, 1, 1, 1 * alpha);
            sp.draw(portraitTexture, 0, 0, portraitTexture.getWidth(), portraitTexture.getHeight());
            if (clockTexture != null) {
                sp.draw(clockTexture, portraitTexture.getWidth() - clockTexture.getWidth(), 0);

            }
            if (border != null) {
                border.draw(sp, alpha);
            }
            if (initiativeStrVal != null) {
                initiativeStrVal.draw(sp, alpha);
            }
            sp.end();
            fbo.end();

            TextureRegion textureRegion = new TextureRegion(fbo.getColorBufferTexture(), 0, 0, portraitTexture.getWidth(), portraitTexture.getHeight());
            textureRegion.flip(false, true);
            imageContainer.setActor(new Image(textureRegion));
            imageContainer.width(getW()).height(getH()).bottom().left().pack();

            if (clockTexture != null) {
                if (alpha != 0) {
                    InitiativePanelParam panelParam = new InitiativePanelParam(textureRegion, curId, clockVal);
                    GuiEventManager.trigger(GuiEventType.ADD_OR_UPDATE_INITIATIVE, new OnDemandEventCallBack(panelParam));
                } else {
                    InitiativePanelParam panelParam = new InitiativePanelParam(curId);
                    GuiEventManager.trigger(GuiEventType.REMOVE_FROM_INITIATIVE_PANEL, new OnDemandEventCallBack(panelParam));
                }
            }
            needRepaint = false;
        }

        if (arrow != null) {
            arrow.setOrigin(arrow.getWidth() / 2, getHeight() / 2);
            arrow.setRotation(arrowRotation);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void updateRotation(int val) {
        if (arrow != null) {
            arrowRotation = val + 90;
            arrow.setRotation(arrowRotation);
        }
    }

    @Override
    protected void sizeChanged() {
        //setHeight(baseHeight * getScaleY());
        //setWidth(baseWidth * getScaleX());

        if (initiativeStrVal != null) {
            initiativeStrVal.setPosition(
                    portraitTexture.getWidth() - clockTexture.getWidth() / 2 - initiativeStrVal.getWidth() / 2,
                    clockTexture.getHeight() / 2 - initiativeStrVal.getHeight() / 2);
        }

        if (imageContainer != null) {
            imageContainer.width(getWidth()).height(getHeight());
        }

        if (icon != null) {
            icon.setPosition(0, getHeight() - icon.getImageHeight());
        }

        if (arrow != null) {
            arrow.setOrigin(arrow.getWidth() / 2, getHeight() / 2);
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
            arrow.setRotation(arrowRotation);
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) return null;
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }

    public void updateInitiative(Integer val) {
        if (clockTexture != null) {
            clockVal = val;
            initiativeStrVal.setText("[#00FF00FF]" + String.valueOf(val) + "[]");
            initiativeStrVal.setPosition(
                    portraitTexture.getWidth() - clockTexture.getWidth() / 2 - initiativeStrVal.getWidth() / 2,
                    clockTexture.getHeight() / 2 - initiativeStrVal.getHeight() / 2);

            needRepaint = true;
        } else {
            LogMaster.error("Initiative set to wrong object type != OBJ_TYPES.UNITS");
        }
    }
}
