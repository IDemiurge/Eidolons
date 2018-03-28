package main.libgdx.bf.mouse;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by JustMe on 2/24/2018.
 */
public class HoverListener extends ClickListener {
    public boolean enter;
    protected Runnable runnable;

    public HoverListener(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        enter = false;
        runnable.run();
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        enter = true;
        runnable.run();
    }
}
