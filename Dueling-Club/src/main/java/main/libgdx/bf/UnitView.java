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
    public static final int HIDE_ARROW = -999;
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
    private Image clockImage;


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

        if (clockTexture != null) {
            this.clockTexture = clockTexture;
            initiativeStrVal = new Label("[#00FF00FF]" + String.valueOf(clockVal) + "[]", StyleHolder.getDefaultLabelStyle());
            clockImage = new Image(clockTexture);
            addActor(clockImage);
            addActor(initiativeStrVal);
        }

        if (arrowTexture != null) {
            arrow = new Image(arrowTexture);
            addActor(arrow);
            arrow.setOrigin(getWidth() / 2 + arrow.getWidth(), getHeight() / 2 + arrow.getHeight());
            arrow.setPosition(getWidth() / 2 - arrow.getWidth() / 2, 0);
        }

        if (iconTexture != null) {
            icon = new Image(iconTexture);
            addActor(icon);
            icon.setPosition(0, getHeight() - icon.getImageHeight());
        }
        sizeChanged();
    }

    public void setVisibleVal(int val) {
        val = Math.max(0, val);
        val = Math.min(100, val);
        alpha = val * 0.01f;
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

        if (initiativeStrVal != null) {
            initiativeStrVal.setPosition(
                    getWidth() - clockTexture.getRegionWidth() / 2 - initiativeStrVal.getWidth() / 2,
                    clockTexture.getRegionHeight() / 2 - initiativeStrVal.getHeight() / 2);

            clockImage.setPosition(
                    getWidth() - clockTexture.getRegionWidth() / 2,
                    clockTexture.getRegionHeight() / 2
            );
        }

        if (imageContainer != null) {
            imageContainer.size(getWidth(), getHeight());
        }

        if (icon != null) {
            icon.setPosition(0, getHeight() - icon.getImageHeight());
        }

        if (arrow != null) {
            arrow.setOrigin(arrow.getWidth() / 2, getHeight() / 2);
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
            arrow.setRotation(arrowRotation);
        }

        if (border != null) {
/*            border.setX(-4);
            border.setY(-4);*/
            border.setHeight(getHeight());
            border.setWidth(getWidth());
        }
    }

/*    @Override
    public void updateBorderSize() {
        border.setHeight(100);
        border.setWidth(100);
    }*/

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
            sizeChanged();
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
