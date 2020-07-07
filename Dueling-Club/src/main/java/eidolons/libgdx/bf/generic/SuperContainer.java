package eidolons.libgdx.bf.generic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.libgdx.bf.SuperActor;
import main.content.enums.GenericEnums;

/**
 * Created by JustMe on 8/17/2017.
 */
public class SuperContainer extends SuperActor {
    protected Actor content;
    protected boolean fluctuateAlpha;

    //TODO background

    public SuperContainer() {

    }

    public SuperContainer(Actor content) {
        this.content = content;
        addActor(content);
        setOrigin(getWidth()/2, getHeight()/2);
    }

    public SuperContainer(GenericEnums.ALPHA_TEMPLATE alphaTemplate) {
        super(alphaTemplate);
        this.fluctuateAlpha = true;
    }

    public SuperContainer(Actor content, boolean fluctuateAlpha) {
        this(content);
        this.fluctuateAlpha = fluctuateAlpha;
    }

    @Override
    public void setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE alphaTemplate) {
        setFluctuateAlpha(true);
        super.setAlphaTemplate(alphaTemplate);
    }

    @Override
    public void setTouchable(Touchable touchable) {
        super.setTouchable(touchable);
        if (getContent()!=null )
            getContent().setTouchable(touchable);
    }

    public Actor getContent() {
        return content;
    }

    @Override
    public float getWidth() {
        return getContent().getWidth();
    }

    @Override
    public float getHeight() {
        return getContent().getHeight();
    }

    @Override
    protected void alphaFluctuation(Actor image, float delta) {
        super.alphaFluctuation(image, delta);
    }

    @Override
    protected void alphaFluctuation(float delta) {
        if (isAlphaFluctuationOn())
            super.alphaFluctuation(content, delta);
    }

    @Override
    public boolean isAlphaFluctuationOn() {
        return fluctuateAlpha;
    }

    public void setFluctuateAlpha(boolean fluctuateAlpha) {
        this.fluctuateAlpha = fluctuateAlpha;
    }

    public void setContents(Actor contents) {
        if (this.content != null)
            this.content.remove();
        this.content = contents;
        addActor(contents);
    }

    @Override
    public void setColor(Color color) {
        getContent().setColor(color);
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        getContent().setColor(r, g, b, a);
    }

    @Override
    public Color getColor() {
        return getContent().getColor();
    }


    @Override
    public float getRotation() {
        return getContent().getRotation();
    }

    @Override
    public void setRotation(float degrees) {
        getContent().setRotation(degrees);
    }


    @Override
    public void setSize(float width, float height) {
        getContent().setSize(width, height);
    }

    @Override
    public void setOrigin(float originX, float originY) {
        getContent().setOrigin(originX, originY);
    }

    @Override
    public void setScale(float scaleXY) {
        getContent().setScale(scaleXY);
    }

    @Override
    public void setOrigin(int alignment) {
        getContent().setOrigin(alignment);
    }

    @Override
    public void setScaleX(float scaleX) {
        getContent().setScaleX(scaleX);
    }

    @Override
    public void setScaleY(float scaleY) {
        getContent().setScaleY(scaleY);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        getContent().setScale(scaleX, scaleY);
    }

    @Override
    public float getScaleX() {
        return getContent().getScaleX();
    }

    @Override
    public float getScaleY() {
        return getContent().getScaleY();
    }
}
