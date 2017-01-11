package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;

/**
 * Created by JustMe on 1/11/2017.
 */
public class HorizontalContainer extends HorizontalGroup implements WidgetContainer{

    @Override
    public void add(WidgetContainer c) {
        super.addActor((Actor) c);
    }



    @Override
        public String toString() {
            return   getClass().getSimpleName()+ " " +getWidth() + " by " + getHeight()
             + " at " + getX() + ":" + getY()
             + " with " + getChildren().size + " children: " + getChildren();
        }
    }


