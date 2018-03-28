package main.libgdx.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;

import java.util.List;

/**
 * Created by JustMe on 2/25/2018.
 */
public class HorGroup<T extends Actor> extends HorizontalFlowGroup {
    public HorGroup(float width, float spacing, List<T> actors) {
        super(spacing);
        if (width != 0)
            setWidth(width);
        for (T sub : actors) {
            addActor(sub);
        }
    }

    public HorGroup(float spacing, List<T> actors) {
        this(0, spacing, actors);
    }
}
