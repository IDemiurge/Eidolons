package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

class UnitView extends Group {
    private Image arrow;
    private Image clock;
    private Image portrait;
    private Image icon;
    private int baseHeight;
    private int baseWidth;
    private int arrowRotation;
    private String clockVal;

    public UnitView(Texture arrowTexture, int arrowRotation, Texture clockTexture, String clockVal, Texture portraitTexture, Texture iconTexture) {
        init(arrowTexture, arrowRotation, clockTexture, clockVal, portraitTexture, iconTexture);
    }

    private void init(Texture arrowTexture, int arrowRotation, Texture clockTexture, String clockVal, Texture portraitTexture, Texture iconTexture) {
        this.arrowRotation = arrowRotation;
        this.clockVal = clockVal;

        portrait = new Image(portraitTexture);
        addActor(portrait);

        baseHeight = portraitTexture.getHeight();
        baseWidth = portraitTexture.getHeight();
        setHeight(baseHeight * getScaleY());
        setWidth(baseWidth * getScaleX());

        if (arrowTexture != null) {
            arrow = new Image(arrowTexture);
            addActor(arrow);
            arrow.setOrigin(getWidth() / 2 + arrow.getWidth(), getHeight() / 2 + arrow.getHeight());
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
            arrow.setY(0);
            //arrow.setRotation(arrowRotation);
            arrow.setDebug(true);
        }

        if (clockTexture != null) {
            clock = new Image(clockTexture);
            addActor(clock);
            clock.setX(getWidth() - clock.getHeight());
            clock.setY(0);
        }

        if (iconTexture != null) {
            icon = new Image(iconTexture);
            addActor(icon);
            icon.setX(0);
            icon.setY(getHeight() - icon.getImageHeight());
        }
        setDebug(true);
    }

    public UnitView(UnitViewOptions o) {
        init(o.getDirectionPointerTexture(), o.getDirectionValue(), o.getClockTexture(), o.getClockValue(), o.getPortrateTexture(), o.getIconTexture());
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        setHeight(baseHeight * getScaleY());
        setWidth(baseWidth * getScaleX());
        arrow.setOrigin(getWidth() / 2 + arrow.getWidth(), getHeight() / 2 + arrow.getHeight());
        arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        //setHeight(baseHeight * getScaleY());
        //setWidth(baseWidth * getScaleX());
        if (arrow != null) {
            arrow.setOrigin(arrow.getWidth()/2, getHeight()/2);
            //arrow.rotateBy(1);
            //arrow.setRotation(getRotation()+5);
            //arrow.setOrigin(getWidth()/2 , getHeight()/2);
        }


        super.draw(batch, parentAlpha);
    }

    @Override
    protected void sizeChanged() {
        //setHeight(baseHeight * getScaleY());
        //setWidth(baseWidth * getScaleX());

        portrait.setWidth(getWidth());
        portrait.setHeight(getHeight());

        if (clock != null) {
            clock.setX(getWidth() - clock.getHeight());
            clock.setY(0);
        }

        if (icon != null) {
            icon.setX(0);
            icon.setY(getHeight() - icon.getImageHeight());
        }

        if (arrow != null) {
            arrow.setOrigin(arrow.getWidth()/2, getHeight()/2);
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
            arrow.setRotation(arrowRotation-90);
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) return null;
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }
}