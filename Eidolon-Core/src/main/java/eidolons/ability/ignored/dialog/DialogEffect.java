package eidolons.ability.ignored.dialog;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.unit.Unit;
import main.ability.effects.OneshotEffect;

public abstract class DialogEffect extends DC_Effect implements OneshotEffect {

    protected Unit hero;

    @Override
    public boolean applyThis() {
        hero = (Unit) ref.getTargetObj();
        if (!ref.getSourceObj().isMine() || hero.isAiControlled()) {
                automaticDialogResolve();
            return true;
        }

        boolean result = showDialog();
        if (!result) {
            ref.getActive().setCancelled(true);
        }

        return true;
    }


    protected abstract boolean showDialog();

    protected abstract void automaticDialogResolve();


}
