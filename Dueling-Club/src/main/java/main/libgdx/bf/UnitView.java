package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class UnitView extends Group implements Borderable {
    private Image arrow;
    private Image clock;
    private Image portrait;
    private Image icon;
    private int baseHeight;
    private int baseWidth;
    private int arrowRotation;
    private String clockVal;
    private Image border = null;
    private boolean overlaying;

    public UnitView(Texture arrowTexture, int arrowRotation, Texture clockTexture, String clockVal, Texture portraitTexture, Texture iconTexture) {
        init(arrowTexture, arrowRotation, clockTexture, clockVal, portraitTexture, iconTexture);
    }

    private void init(Texture arrowTexture, int arrowRotation, Texture clockTexture, String clockVal, Texture portraitTexture, Texture iconTexture) {
        this.arrowRotation = arrowRotation + 90;
        this.clockVal = clockVal;

        portrait = new Image(portraitTexture);
        addActor(portrait);

        baseHeight = portraitTexture.getHeight();
        baseWidth = portraitTexture.getHeight();
        setHeight(baseHeight * getScaleY());
        setWidth(baseWidth * getScaleX());

        if (clockTexture != null) {
            clock = new Image(clockTexture);
            addActor(clock);
            clock.setX(getWidth() - clock.getHeight());
            clock.setY(0);
        }

        if (arrowTexture != null) {
            arrow = new Image(arrowTexture);
            addActor(arrow);
            arrow.setOrigin(getWidth() / 2 + arrow.getWidth(), getHeight() / 2 + arrow.getHeight());
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
            arrow.setY(0);
            //arrow.setRotation(arrowRotation);
//            arrow.setDebug(true);
        }

        if (iconTexture != null) {
            icon = new Image(iconTexture);
            addActor(icon);
            icon.setX(0);
            icon.setY(getHeight() - icon.getImageHeight());
        }
//        setDebug(true);
    }

    public UnitView(UnitViewOptions o) {
        if (o.getOverlaying()) {
            overlaying = true;
            init(o.getDirectionValue(), o.getPortrateTexture());
        } else {
            init(o.getDirectionPointerTexture(), o.getDirectionValue(), o.getClockTexture(), o.getClockValue(), o.getPortrateTexture(), o.getIconTexture());
        }
        o.getUnitMap().put(o.getObj(), this);//todo fix this shit
    }

    public boolean isOverlaying() {
        return overlaying;
    }

    private void init(int directionValue, Texture portrateTexture) {
        portrait = new Image(portrateTexture);
        addActor(portrait);

        switch (directionValue) {
            case -1://center
                break;
            case 360://RIGHT
                break;
            case 90://UP
                break;
            case 180://LEFT
                break;
            case 270://DOWN
                break;
            case 135://UP_LEFT
                break;
            case 45://UP_RIGHT
                break;
            case 225://DOWN_RIGHT
                break;
            case 315://DOWN_LEFT
                break;
        }
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
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //setHeight(baseHeight * getScaleY());
        //setWidth(baseWidth * getScaleX());
        if (arrow != null) {
            arrow.setOrigin(arrow.getWidth() / 2, getHeight() / 2);
            //arrow.rotateBy(1);
            arrow.setRotation(arrowRotation);
            //arrow.setOrigin(getWidth()/2 , getHeight()/2);
        }


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

    @Override
    public void setBorder(Image image) {
        if (border != null) {
            removeActor(border);
        }

        if (image == null) {
            border = null;
        } else {
            addActor(image);
            border = image;
        }
    }

    @Override
    public int getW() {
        return (int) getWidth();
    }

    @Override
    public int getH() {
        return (int) getHeight();
    }

    @Override
    public Image getBorder() {
        return border;
    }
}
