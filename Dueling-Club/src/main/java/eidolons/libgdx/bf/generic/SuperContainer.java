package eidolons.libgdx.bf.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.libgdx.bf.SuperActor;

/**
 * Created by JustMe on 8/17/2017.
 */
public class SuperContainer extends SuperActor {
    Actor content;
    private boolean fluctuateAlpha;


    public SuperContainer() {

    }

    public SuperContainer(Actor content) {
        this.content = content;
        addActor(content);
    }

    public SuperContainer(ALPHA_TEMPLATE alphaTemplate) {
        super(alphaTemplate);
        this.fluctuateAlpha = true;
    }

    public SuperContainer(Actor content, boolean fluctuateAlpha) {
        this(content);
        this.fluctuateAlpha = fluctuateAlpha;
    }

    @Override
    public void setAlphaTemplate(ALPHA_TEMPLATE alphaTemplate) {
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
        if (fluctuateAlpha)
            super.alphaFluctuation(content, delta);
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
}
