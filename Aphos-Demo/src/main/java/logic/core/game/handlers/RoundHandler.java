package logic.core.game.handlers;

import content.LOG;
import logic.core.game.Game;
import logic.entity.Entity;
import logic.functions.atb.AtbEntity;
import logic.functions.atb.AtbLoop;
import main.system.threading.WaitMaster;

public class RoundHandler extends GameHandler {

    private AtbLoop loop;
    private RoundActions actions;
    private int round=1;
    private boolean gameOver;

    public RoundHandler(Game game) {
        super(game);
        loop = game.getController().getAtbLogic().getLoop();
        actions = new RoundActions();
    }

    public boolean newRound() {
        game.getUnits().stream().filter(unit -> unit.getPos().cell != 0).forEach(unit -> actions.roundAction(unit));
        LOG.log("--------------- Round #", round, " ---------------");
        loop.newRound();
        updateAtbUnits();
        //TODO
        AtbEntity next = null;
        boolean roundEnded = false;
        while (!roundEnded) {

            while (next == null)
                next = loop.step();

            roundEnded = active(next.getEntity());
            updateAtbUnits();
        }
        round++;
        return isGameOver();
    }

    private boolean isGameOver() {
//        game.getCoreHandler().isAlive();
        return gameOver;
    }


    private void updateAtbUnits() {
        loop.checkForRemoval();
        game.getHeroes().forEach(hero -> loop.addEntity(hero));
        game.getUnits().stream().filter(unit -> unit.getPos().cell == 0).forEach(unit ->
                loop.addEntity(unit));
    }

    private boolean active(Entity entity) {
        LOG.log("--------------- Active: ", entity, " ---------------");
        game.getController().active(entity);
        if (entity.isPlayerControlled()) {
            //highlight hero
            LOG.log("Your move!");
            WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.ACTION_COMPLETE);

        } else {
            game.getAiHandler().act(entity);
        }
        return loop.isNextTurn();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        LOG.log("GAMEOVER!");
    }
}
