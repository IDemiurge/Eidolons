package main.libgdx.gui.panels.generic.sub;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import main.libgdx.gui.panels.generic.WidgetContainer;

/**
 * Created by JustMe on 1/11/2017.
 */
public class RootTable extends Table implements WidgetContainer {
    @Override
    public void add(WidgetContainer c) {
        super.addActor((Actor) c);
        debug();
    }

    @Override
    public WidgetGroup reverse() {
        return null;
    }

    @Override
    public WidgetGroup reverse(boolean reverse) {
        return null;
    }

    @Override
    public boolean getReverse() {
        return false;
    }

    @Override
    public WidgetGroup space(float space) {
        return null;
    }

    @Override
    public float getSpace() {
        return 0;
    }

    @Override
    public WidgetGroup wrapSpace(float wrapSpace) {
        return null;
    }

    @Override
    public float getWrapSpace() {
        return 0;
    }

    @Override
    public WidgetGroup fill() {
        return null;
    }

    @Override
    public WidgetGroup fill(float fill) {
        return null;
    }

    @Override
    public float getFill() {
        return 0;
    }

    @Override
    public WidgetGroup wrap() {
        return null;
    }

    @Override
    public WidgetGroup wrap(boolean wrap) {
        return null;
    }

    @Override
    public boolean getWrap() {
        return false;
    }
}
