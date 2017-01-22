package main.libgdx.gui.dialog;

import com.badlogic.gdx.scenes.scene2d.Group;

public class LogMessage extends Group {
    public LogMessage pack() {
        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getHeight() != getChildren().first().getHeight()) {
            setHeight(getChildren().first().getHeight());
        }
    }
}
