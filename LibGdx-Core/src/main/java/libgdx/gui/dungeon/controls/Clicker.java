package libgdx.gui.dungeon.controls;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by JustMe on 3/29/2018.
 */
public class Clicker extends ClickListener {
    Runnable runnable;

    public Clicker(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        runnable.run();
        return super.touchDown(event, x, y, pointer, button);
    }
}
