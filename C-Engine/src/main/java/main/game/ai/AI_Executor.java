package main.game.ai;

import main.game.MicroGame;
import main.system.net.Communicator;
import main.system.net.Communicator.COMMAND;

public class AI_Executor {

    private Communicator communicator;
    private MicroGame game;

    public AI_Executor(AI ai) {
        this.game = ai.getGame();
        this.communicator = game.getCommunicator();
    }

    public void execute(final Object[] args) {
        // game.getManager().refresh();
        communicator.executeCommand(COMMAND.ACTIVATE, args);
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
