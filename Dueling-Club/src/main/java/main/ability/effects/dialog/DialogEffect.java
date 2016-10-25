package main.ability.effects.dialog;

import main.ability.effects.DC_Effect;
import main.entity.obj.DC_HeroObj;

public abstract class DialogEffect extends DC_Effect {

    protected DC_HeroObj hero;

    @Override
    public boolean applyThis() {
        hero = (DC_HeroObj) ref.getTargetObj();
        if (!ref.getSourceObj().isMine() || hero.isAiControlled()) {
            if (getGame().isOnline() && !hero.isAiControlled()) {
                String string = getGame().getCommunicator().getChoiceData();
                processOperationCommand(string);
            } else {
                automaticDialogResolve();
            }
            return true;
        }

        boolean result = showDialog();
        if (!result)
            ref.getActive().setCancelled(true);
        else if (getGame().isOnline()) {
            if (hero.isMine()) {
                getGame().getCommunicator().sendChoiceData(getOperationsData());
            }
        }

        return true;
    }

    protected abstract String getOperationsData();

    protected abstract boolean showDialog();

    protected abstract void automaticDialogResolve();

    protected abstract void processOperationCommand(String string);

}
