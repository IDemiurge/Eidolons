package main.game.core;

import main.entity.active.DC_ActiveObj;

/**
 * Created by JustMe on 2/21/2017.
 */
public class ActionThread extends Thread {
    DC_ActiveObj action;

    public DC_ActiveObj getAction() {
        return action;
    }

    public void setAction(DC_ActiveObj action) {
        this.action = action;
    }

    @Override
    public void run() {
        action.activate();
    }
}
