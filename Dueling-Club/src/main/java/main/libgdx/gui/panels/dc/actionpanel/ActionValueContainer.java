package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.gui.panels.dc.ValueContainer;

public class ActionValueContainer extends ValueContainer {
    private Runnable action;

    @Override
    protected void init(TextureRegion texture, String name, String value) {
        super.init(texture, name, value);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (action != null) {
                    action.run();
                }
            }
        });
    }

    public ActionValueContainer setAction(Runnable action) {
        this.action = action;
        return this;
    }
}
