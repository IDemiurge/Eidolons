package main.game.ai;

import main.game.core.game.MicroGame;

public class AI_Executor {

    private MicroGame game;

    public AI_Executor(AI ai) {
        this.game = ai.getGame();
    }

    public void execute(final Object[] args) {
        // game.getManager().refresh();
//        communicator.executeCommand(COMMAND.ACTIVATE, args);
        // WaitMaster.WAIT(500);
        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        // game.getManager().refresh();
        // communicator.executeCommand(COMMAND.ACTIVATE, args);
        // }
        // }).start();
    }

}
