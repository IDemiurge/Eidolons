package libgdx.gui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import libgdx.GDX;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.gui.generic.btn.ButtonStyled;
import main.game.bf.directions.DIRECTION;

/**
 * Created by JustMe on 10/16/2018.
 */
public class HideDecorator {

    public static GroupX decorate(boolean hiddenDefault, ButtonStyled.STD_BUTTON style, DIRECTION at,
                                  Actor actor, Runnable runnable) {
        return decorate(hiddenDefault, style, at, actor, runnable, null);
    }
    public static GroupX decorate(boolean hiddenDefault, DIRECTION d, Actor actor) {
        return decorate(hiddenDefault, d, actor, null, new Vector2());
    }

    public static GroupX decorate(boolean hiddenDefault, DIRECTION at,
                                  Actor actor, Runnable runnable, Vector2 offset) {
        return decorate(hiddenDefault, ButtonStyled.STD_BUTTON.EYE, at, actor, runnable, offset);
    }


    public static GroupX decorate(boolean hiddenDefault, ButtonStyled.STD_BUTTON style, DIRECTION at,
                                  Actor actor, Runnable runnable, Vector2 offset) {
        GroupX group = new GroupX(true);
        Touchable touchable = Touchable.enabled; //TODO is this right?
        group.addActor(actor);
        if (hiddenDefault) {
            actor.setVisible(false);
            actor.setTouchable(Touchable.disabled);
            group.setTouchable(Touchable.childrenOnly);
        }
        SmartTextButton btn = new SmartTextButton(style, () -> {

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
        if (offset != null) {
            btn.setPosition(btn.getX() + offset.x,
             btn.getY() + offset.y);
        }
        return group;
    }

}
