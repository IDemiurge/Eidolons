package libgdx.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import libgdx.anims.actions.ActionMaster;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.generic.btn.ButtonStyled;

/**
 * Created by JustMe on 10/10/2018.
 */
public class HideButton extends SymbolButton {


    public HideButton(GroupX group) {
        super(ButtonStyled.STD_BUTTON.EYE, ()-> toggleHide(group));
    }
    public HideButton(Actor actor) {
        super(ButtonStyled.STD_BUTTON.EYE, ()-> toggleHide(actor));
    }

        private static void toggleHide(Actor actor) {
        if (actor.isVisible()){
            if (actor instanceof GroupX) {
                ((GroupX) actor).fadeOut();
            } else {
                ActionMaster.addFadeInAction(actor);
            }
            ActionMaster.addHideAfter(actor);
        } else {
            actor.setVisible(true);
            if (actor instanceof GroupX) {
                ((GroupX) actor).fadeIn();
            } else {
                ActionMaster.addFadeInAction(actor);
            }
        }
    }
}
