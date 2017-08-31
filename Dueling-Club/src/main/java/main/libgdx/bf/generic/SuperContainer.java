package main.libgdx.bf.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import main.libgdx.bf.SuperActor;

/**
 * Created by JustMe on 8/17/2017.
 */
public  class SuperContainer extends SuperActor {
    Actor content;

    public SuperContainer(Actor content) {
        this.content = content;
        addActor(content);
    }

    public Actor getContent() {
        return content;
    }

    @Override
    protected void alphaFluctuation(Actor image, float delta) {
        super.alphaFluctuation(image, delta);
    }
}
