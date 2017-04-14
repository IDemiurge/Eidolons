package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.StyleHolder;
import main.system.auxiliary.log.LogMaster;

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
    private TextureRegion portraitTexture;
    private Container<Image> imageContainer;
    private TextureRegion clockTexture;
    private boolean needRepaint = true;
    private Label initiativeStrVal;
    private float alpha = 1f;
    private TextureRegion outlineTexture;


    public UnitView(UnitViewOptions o) {
        super(o);
        curId = lastId.getAndIncrement();
        init(o.getDirectionPointerTexture(), o.getDirectionValue(), o.getClockTexture(), o.getClockValue(), o.getPortrateTexture(), o.getIconTexture());
    }

    private void init(TextureRegion arrowTexture, int arrowRotation, TextureRegion clockTexture, int clockVal, TextureRegion portraitTexture, Texture iconTexture) {
        this.arrowRotation = arrowRotation + 90;
        this.clockVal = clockVal;
        this.portraitTexture = portraitTexture;
        portrait = new Image(portraitTexture);

        baseHeight = portraitTexture.getRegionHeight();
        baseWidth = portraitTexture.getRegionWidth();
        setSize(baseWidth * getScaleX(), baseHeight * getScaleY());

        imageContainer = new Container<>(portrait);
        imageContainer.width(getW()).height(getH()).bottom().left();
        addActor(imageContainer);

        if (clockTexture != null) {
            this.clockTexture = clockTexture;
            initiativeStrVal = new Label("[#00FF00FF]" + String.valueOf(clockVal) + "[]", StyleHolder.getDefaultLabelStyle());
            initiativeStrVal.setPosition(
                    getWidth() - clockTexture.getRegionWidth() / 2 - initiativeStrVal.getWidth() / 2,
                    clockTexture.getRegionHeight() / 2 - initiativeStrVal.getHeight() / 2);
            addActor(new Image(clockTexture));
            addActor(initiativeStrVal);
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

/*    @Override
    public void setBorder(Image image) {
        border = image;
        if (border != null) {
            updateBorderSize();
        }
        needRepaint = true;
    }*/

/*    @Override
    public void updateBorderSize() {
        super.updateBorderSize();
        border.setBounds(
                -4, -4,
                portraitTexture.getRegionWidth(), portraitTexture.getRegionHeight()
        );
    }*/

    public void setVisibleVal(int val) {
        val = Math.max(0, val);
        val = Math.min(100, val);
        alpha = val * 0.01f;
        needRepaint = true;
    }

    @Override
    public void act(float delta) {
        if (needRepaint) {

/*
            if (border != null) {
                border.draw(sp, 1);
            }
*/

/*            if (clockTexture != null) {
                textureData = clockTexture.getTexture().getTextureData();
                if (!textureData.isPrepared()) {
                    textureData.prepare();
                }
                source = textureData.consumePixmap();
                dest.drawPixmap(source, portraitTexture.getRegionWidth() - clockTexture.getRegionWidth(), 0,
                        clockTexture.getRegionX(), clockTexture.getRegionY(),
                        clockTexture.getRegionWidth(), clockTexture.getRegionHeight());
            }*/

/*            if (initiativeStrVal != null) {
                initiativeStrVal.draw(sp, alpha);
            }
  */
/*            TextureRegion textureRegion = new TextureRegion(new Texture(dest));

            imageContainer.setActor(new Image(textureRegion));

            imageContainer.width(getWidth()).height(getHeight()).bottom().left().pack();

            if (clockTexture != null) {
                if (alpha != 0) {
                    InitiativePanelParam panelParam = new InitiativePanelParam(textureRegion, curId, clockVal);
                    GuiEventManager.trigger(GuiEventType.ADD_OR_UPDATE_INITIATIVE, new OnDemandEventCallBack(panelParam));
                } else {
                    InitiativePanelParam panelParam = new InitiativePanelParam(curId);
                    GuiEventManager.trigger(GuiEventType.REMOVE_FROM_INITIATIVE_PANEL, new OnDemandEventCallBack(panelParam));
                }
            }*/
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
                    portraitTexture.getRegionWidth() - clockTexture.getRegionWidth() / 2 - initiativeStrVal.getWidth() / 2,
                    clockTexture.getRegionHeight() / 2 - initiativeStrVal.getHeight() / 2);
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
        if (touchable && this.getTouchable() != Touchable.enabled) {
            return null;
        }
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }

    public void updateInitiative(Integer val) {
        if (clockTexture != null) {
            clockVal = val;
            initiativeStrVal.setText("[#00FF00FF]" + String.valueOf(val) + "[]");
            initiativeStrVal.setPosition(
                    portraitTexture.getRegionWidth() - clockTexture.getRegionWidth() / 2 - initiativeStrVal.getWidth() / 2,
                    clockTexture.getRegionHeight() / 2 - initiativeStrVal.getHeight() / 2);

            needRepaint = true;
        } else {
            LogMaster.error("Initiative set to wrong object type != OBJ_TYPES.UNITS");
        }
    }

    public int getId() {
        return curId;
    }

    public void setOutlineTexture(TextureRegion outlineTexture) {
        this.outlineTexture = outlineTexture;
    }
}
