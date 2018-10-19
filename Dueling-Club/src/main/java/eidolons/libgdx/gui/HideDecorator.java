package eidolons.libgdx.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.libgdx.GDX;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.game.bf.directions.DIRECTION;

/**
 * Created by JustMe on 10/16/2018.
 */
public class HideDecorator {

    public static GroupX decorate(boolean hiddenDefault, STD_BUTTON style, DIRECTION at, Actor actor, Runnable runnable) {
        GroupX group = new GroupX(true);
        Touchable touchable = actor.getTouchable();
        group.addActor(actor);
        if (hiddenDefault) {
            actor.setVisible(false);
            actor.setTouchable(Touchable.disabled);
            group.setTouchable(Touchable.childrenOnly);
        }
        SmartButton btn= new SmartButton(style, ()->{

            if (actor.isVisible()) {
                actor.setTouchable(Touchable.disabled);
                group.setTouchable(Touchable.childrenOnly);
            } else {
                actor.setTouchable(touchable);
                group.setTouchable(touchable);
            }
            if (actor instanceof TablePanelX) {
                ((TablePanelX) actor).toggleFade();
            } else {
                if (actor instanceof GroupX) {
                    ((GroupX) actor).toggleFade();
                } else {
                    //TODO
                }
            }

            if (runnable != null) {
                runnable.run();
            }
        });
        group.addActor(btn);
        GDX.position(btn, at);

        return group;
    }
}
