package main.game.ai.logic;

import main.game.logic.generic.ActionManager;
import main.entity.obj.Obj;
import main.game.core.game.MicroGame;
import main.game.ai.AI;
import main.game.ai.AI_Logic;
import main.game.ai.logic.ActionTypeManager.ACTION_TYPES;
import main.game.logic.battle.player.Player;

import java.util.Set;

public class Old_AI_Manager {

    protected AI_Logic logic;

    public Old_AI_Manager(AI_Logic logic) {
        this.logic = logic;

    }

    public int getPriorityForUnit(Obj unit, Set<Obj> units) {
        return logic.getPriorityForUnit(unit, units);
    }

    public Analyzer getAnalyzer() {
        return logic.getAnalyzer();
    }

    public ACTION_TYPES getAction() {
        return logic.initAction();
    }

    public Object[] getArgsForExecution(Obj unit) {
        return logic.getArgsForExecution(unit);
    }

    public ActionManager getActionManager() {
        return logic.getActionManager();
    }

    public MicroGame getGame() {
        return logic.getGame();
    }

    public Player getPlayer() {
        return logic.getPlayer();
    }

    public AI getAi() {
        return logic.getAi();
    }

    public TargetingManager gettManager() {
        return logic.gettManager();
    }

    public OldPriorityManager getpManager() {
        return logic.getpManager();
    }

    public ActionTypeManager getaManager() {
        return logic.getaManager();
    }

    public Obj getUnit() {
        return logic.getUnit();
    }

    public Set<Obj> getUnits() {
        return logic.getUnits();
    }

    public Obj getPriorityUnit() {
        return logic.getPriorityUnit();
    }

    public Obj getActive() {
        return logic.getActive();
    }

    public int getTargetId() {
        return logic.initTargetId();
    }

}
