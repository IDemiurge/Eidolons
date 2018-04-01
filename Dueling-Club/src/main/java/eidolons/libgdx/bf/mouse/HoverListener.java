package eidolons.libgdx.bf.mouse;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.function.Consumer;

/**
 * Created by JustMe on 2/24/2018.
 */
public class HoverListener extends ClickListener {
    public boolean enter;
    protected Consumer<Boolean > function;

    public HoverListener(Consumer<Boolean > function) {
        this.function = function;
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        enter = false;
        function.accept(false);

    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        enter = true;
        function.accept(true);
    }
}
