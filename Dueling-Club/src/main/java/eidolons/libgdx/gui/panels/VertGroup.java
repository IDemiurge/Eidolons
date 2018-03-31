package eidolons.libgdx.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.layout.VerticalFlowGroup;

import java.util.List;

/**
 * Created by JustMe on 2/25/2018.
 */
public class VertGroup<T extends Actor> extends VerticalFlowGroup {
    public VertGroup(float height, float spacing, List<T> actors) {
        super(spacing);
        if (height != 0)
            setHeight(height);
        for (T sub : actors) {
            addActor(sub);
        }
    }

    public VertGroup(float spacing, List<T> actors) {
        this(0, spacing, actors);
    }
}
