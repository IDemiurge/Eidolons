package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

/**
 * Created by JustMe on 1/10/2017.
 */
public class VerticalContainer extends VerticalGroup {

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
//        actor.setY(getHeight()-spaceUsed-actor.getHeight());
//        spaceUsed+=actor.getHeight();
//        actor.setfi
    }

    @Override
    public String toString() {
        return   getClass().getSimpleName()+ " " +getWidth() + " by " + getHeight()
         + " at " + getX() + ":" + getY()
         + " with " + getChildren().size + " children: " + getChildren();
    }
}

