package main.game.ai;

import main.entity.obj.Obj;
import main.game.core.game.MicroGame;
import main.game.ai.logic.ActionTypeManager.ACTION_TYPES;
import main.system.auxiliary.log.LogMaster;
import main.system.threading.WaitMaster;

import java.util.Set;

public class AI_Controller {

    private AI ai;
    private AI_Executor executor;
    private MicroGame game;
    // private Set<Obj> units;
    // private Set<Obj> enemyUnits;
    // private Player player;
    private AI_Logic logic;

    public AI_Controller(AI ai) {

        this.ai = ai;
        this.game = ai.getGame();
        this.executor = ai.getExecutor();
    }

    public boolean makeTurn() {
        this.logic.newTurn();

        // while (!logic.isTurnOver()) {
        // Obj unit = logic.getPriorityUnit();
        for (Obj unit : logic.getUnits()) {
            LogMaster
                    .log(LogMaster.AI_DEBUG, "unit chosen:" + unit);
            if (!makeTurn(unit)) {
                return false;
            }

        }

        LogMaster.log(LogMaster.AI_DEBUG, "Turn made!");
        return true;
    }

    public boolean makeTurn(Obj unit) {
        while (!logic.isUnitDone(unit)) {
            try {
                Object[] args = logic.getArgsForExecution(unit);
                executor.execute(args);
                logic.reset();
                WaitMaster.WAIT(100);
            } catch (Exception e) {
                // e.printStackTrace();
                LogMaster.log(LogMaster.AI_DEBUG, unit
                        + " failed!");
                logic.reset();
                e.printStackTrace();
                break;
            }

        }
        LogMaster.log(LogMaster.AI_DEBUG, "Unit done: "
                + unit);
        return true;
    }

    public AI getAi() {
        return ai;
    }

    public void setAi(AI ai) {
        this.ai = ai;
    }

    public AI_Executor getExecutor() {
        return executor;
    }

    public void setExecutor(AI_Executor executor) {
        this.executor = executor;
    }

    public MicroGame getGame() {
        return game;
    }

    public void setGame(MicroGame game) {
        this.game = game;
    }

    public AI_Logic getLogic() {
        return logic;
    }

    public void setLogic(AI_Logic logic) {
        this.logic = logic;
    }

    public ACTION_TYPES getAction() {
        return logic.initAction();
    }

    public Obj getUnit() {
        return logic.getUnit();
    }

    public Obj getActive() {
        return logic.getActive();
    }

    public Set<Obj> getEnemyUnits() {
        return logic.getEnemyUnits();
    }

    public Set<Obj> getUnits() {
        return logic.getUnits();
    }

}
