package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;

/**
 * Created by JustMe on 10/28/2018.
 */
public class Orders {
    ActionSequence actionSequence;

    public Orders(ActionSequence orders) {
        this.actionSequence = orders;
        status=ORDER_STATUS.RUNNING;
    }

    public void removeFirstAction() {
        getActionSequence().removeFirstAction();
    }

    public AiAction peekNextAction() {
        return getActionSequence().peekNextAction();
    }

    public AiAction popNextAction() {
        return getActionSequence().popNextAction();
    }

    public enum ORDER_STATUS{
        RUNNING,
        FAILED,
        COMPLETE,
    }

    @Override
    public String toString() {
        return actionSequence.toString() + "; status: " + status;
    }

    ORDER_STATUS status;

    public void setStatus(ORDER_STATUS status) {
        this.status = status;
    }

    public ActionSequence getActionSequence() {
        return actionSequence;
    }

    public ORDER_STATUS getStatus() {
        return status;
    }
}
