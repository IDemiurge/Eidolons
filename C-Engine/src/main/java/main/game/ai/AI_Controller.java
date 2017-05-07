package main.game.ai;

import main.entity.obj.Obj;
import main.game.ai.ActionTypeManager.ACTION_TYPES;
import main.game.core.game.MicroGame;

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
