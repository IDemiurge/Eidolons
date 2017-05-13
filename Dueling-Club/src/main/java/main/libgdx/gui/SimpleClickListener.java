package main.libgdx.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SimpleClickListener extends ClickListener {

    private final Runnable callback;

    public SimpleClickListener(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        callback.run();
    }
}
