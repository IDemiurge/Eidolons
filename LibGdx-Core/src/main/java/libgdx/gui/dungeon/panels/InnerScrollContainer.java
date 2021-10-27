package libgdx.gui.dungeon.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;

public class InnerScrollContainer<T extends Actor> extends Container<T> {

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
//        return super.hit(x, y, touchable) != null ? this : null;
    }
}
