package eidolons.libgdx.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;

/**
 * Created by JustMe on 10/10/2018.
 */
public class HideButton extends SmartButton {


    public HideButton(GroupX group) {
        super(STD_BUTTON.EYE, ()-> toggleHide(group));
    }
    public HideButton(Actor actor) {
        super(STD_BUTTON.EYE, ()-> toggleHide(actor));
    }

        private static void toggleHide(Actor actor) {
        if (actor.isVisible()){
            if (actor instanceof GroupX) {
                ((GroupX) actor).fadeOut();
            } else {
                ActorMaster.addFadeInAction(actor);
            }
            ActorMaster.addHideAfter(actor);
        } else {
            actor.setVisible(true);
            if (actor instanceof GroupX) {
                ((GroupX) actor).fadeIn();
            } else {
                ActorMaster.addFadeInAction(actor);
            }
        }
    }
}
