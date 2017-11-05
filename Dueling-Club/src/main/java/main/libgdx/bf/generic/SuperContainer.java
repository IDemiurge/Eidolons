package main.libgdx.bf.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import main.libgdx.bf.SuperActor;

/**
 * Created by JustMe on 8/17/2017.
 */
public  class SuperContainer extends SuperActor {
    Actor content;
    private boolean fluctuateAlpha;

    public SuperContainer(Actor content) {
        this.content = content;
        addActor(content);
    }

    public SuperContainer(Actor content, boolean fluctuateAlpha) {
        this(content);
        this.fluctuateAlpha = fluctuateAlpha;
    }

    public Actor getContent() {
        return content;
    }

    @Override
    public float getWidth() {
        return  getContent().getWidth();
    }

    @Override
    public float getHeight() {
        return  getContent().getHeight();
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
}
