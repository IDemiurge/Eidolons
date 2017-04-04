package main.libgdx.gui.tooltips;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.libgdx.gui.panels.dc.TablePanel;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public abstract class ToolTip<T extends Actor> extends TablePanel<T> {

    public ToolTip() {

    }

    public InputListener getController() {
        return new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new EventCallbackParam<>(ToolTip.this));
                return true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new EventCallbackParam<>(ToolTip.this));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new EventCallbackParam<>(null));
            }
        };
    }

    @Override
    public Object getUserObject() {
        return super.getUserObject();
    }
}
