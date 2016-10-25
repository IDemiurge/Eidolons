package main.game.ai;

import main.entity.ActionManager;
import main.entity.obj.Active;
import main.entity.obj.Obj;
import main.game.MicroGame;
import main.game.ai.logic.ActionTypeManager;
import main.game.ai.logic.ActionTypeManager.ACTION_TYPES;
import main.game.ai.logic.Analyzer;
import main.game.ai.logic.OldPriorityManager;
import main.game.ai.logic.TargetingManager;
import main.game.player.Player;

import java.util.Set;

public interface AI_Logic {
    void init();

    int getPriorityForUnit(Obj unit, Set<Obj> units);

    ACTION_TYPES initAction();

    Object[] getArgsForExecution(Obj unit);

    ActionManager getActionManager();

    void setActionManager(ActionManager actionManager);

    MicroGame getGame();

    void setGame(MicroGame game);

    Player getPlayer();

    void setPlayer(Player player);

    AI getAi();

    void setAi(AI ai);

    TargetingManager gettManager();

    void settManager(TargetingManager tManager);

    OldPriorityManager getpManager();

    void setpManager(OldPriorityManager pManager);

    ActionTypeManager getaManager();

    void setaManager(ActionTypeManager aManager);

    void newTurn();

    Obj getUnit();

    void setUnit(Obj unit);

    Set<Obj> getUnits();

    void setUnits(Set<Obj> units);

    Obj getPriorityUnit();

    Obj getActive();

    void setActive(Active active);

    int initTargetId();

    Analyzer getAnalyzer();

    void setAnalyzer(Analyzer analyzer);

    Set<Obj> getEnemyUnits();

    Player getEnemy();

    Integer getTarget();

    void setTarget(Integer target);

    boolean isTurnOver();

    ACTION_TYPES getAction();

    void setAction(ACTION_TYPES action);

    boolean isUnitDone(Obj unit);

    void reset();
}
