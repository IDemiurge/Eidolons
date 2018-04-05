package eidolons.ability.effects.special.media;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.active.DC_ActiveObj;
import main.content.C_OBJ_TYPE;
import main.data.DataManager;
import main.data.ability.AE_ConstrArgs;
import main.data.ability.OmittedConstructor;
import main.entity.type.ObjType;
import main.system.images.ImageManager;

import javax.swing.*;

public class AnimationEffect extends DC_Effect {

    private DC_ActiveObj action;
    private String actionName;

    @OmittedConstructor
    public AnimationEffect(DC_ActiveObj action) {
        this.action = action;
    }

    public AnimationEffect() {
    }

    @AE_ConstrArgs(argNames = {"type name for icon anim or img path"})
    public AnimationEffect(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public boolean applyThis() {
        if (action != null) {
            getGame().getAnimationManager().actionResolves(action, ref);
        } else if (actionName == null) {
            getGame().getAnimationManager().actionResolves(
             (DC_ActiveObj) ref.getActive(), ref);
        } else {
            ImageIcon icon;
            ObjType type = DataManager.getType(actionName, C_OBJ_TYPE.ACTIVE);
            if (type == null) {
                type = DataManager.getType(actionName);
            }
            if (type == null) {
                icon = ImageManager.getIcon(actionName);
            } else {
                icon = type.getIcon();
            }
            getGame().getAnimationManager().actionResolves(icon, ref);

        }
        return true;
    }
}
