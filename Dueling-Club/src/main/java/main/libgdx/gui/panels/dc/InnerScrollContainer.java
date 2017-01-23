package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;

public class InnerScrollContainer<T extends Actor> extends Container<T> {

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable) != null ? this : null;
    }
}
