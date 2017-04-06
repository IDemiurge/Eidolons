package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.gui.panels.dc.ValueContainer;

public class ActionValueContainer extends ValueContainer {

    private Runnable clickAction;

    public ActionValueContainer(TextureRegion texture, String name, String value, Runnable action) {
        super(texture, name, value);
        bindAction(action);
    }

    public ActionValueContainer(TextureRegion texture, Runnable action) {
        super(texture);
        bindAction(action);
    }

    public ActionValueContainer(TextureRegion texture, String value, Runnable action) {
        super(texture, value);
        bindAction(action);
    }

    public ActionValueContainer(String name, String value, Runnable action) {
        super(name, value);
        bindAction(action);
    }

    public void bindAction(Runnable action) {
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (action != null) {
                    clickAction = action::run;
                }
            }
        });
    }

    public Runnable getClickAction() {
        return clickAction;
    }
}
