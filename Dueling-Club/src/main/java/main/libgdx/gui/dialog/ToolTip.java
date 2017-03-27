package main.libgdx.gui.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.libgdx.gui.panels.dc.TablePanel;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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
    public void setUserObject(Object userObject) {
        if (userObject instanceof Supplier) {
            setUserObject((Supplier) userObject);
        } else if (userObject instanceof List) {
            setUserObject((List) userObject);
        } else {
            setUserObject(Arrays.asList(userObject));
        }
    }

    public void setUserObject(Supplier userObject) {
        super.setUserObject(userObject);
    }

    public void setUserObject(List list) {
        super.setUserObject((Supplier) () -> list);
    }
}
