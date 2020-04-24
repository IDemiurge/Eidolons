package eidolons.ability.ignored.dialog;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.obj.unit.Unit;
import main.ability.effects.OneshotEffect;

public abstract class DialogEffect extends DC_Effect implements OneshotEffect {

    protected Unit hero;

    @Override
    public boolean applyThis() {
        hero = (Unit) ref.getTargetObj();
        if (!ref.getSourceObj().isMine() || hero.isAiControlled()) {
            if (getGame().isOnline() && !hero.isAiControlled()) {
//                String string = getGame().getCommunicator().getChoiceData();
//                processOperationCommand(string);
            } else {
                automaticDialogResolve();
            }
            return true;
        }

        boolean result = showDialog();
        if (!result) {
            ref.getActive().setCancelled(true);
        } else if (getGame().isOnline()) {
            if (hero.isMine()) {
//                getGame().getCommunicator().sendChoiceData(getOperationsData());
            }
        }

        return true;
    }


    protected abstract boolean showDialog();

    protected abstract void automaticDialogResolve();


}
