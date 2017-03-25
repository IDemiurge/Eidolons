package main.libgdx.old.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

/**
 * Created by JustMe on 1/10/2017.
 */
public class VerticalContainer extends VerticalGroup implements WidgetContainer {

    @Override
    public void add(WidgetContainer c) {
        super.addActor((Actor) c);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + getWidth() + " by " + getHeight()
                + " at " + getX() + ":" + getY()
                + " with " + getChildren().size + " children: " + getChildren();
    }


}

