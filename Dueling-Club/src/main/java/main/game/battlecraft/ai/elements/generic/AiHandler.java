package main.game.battlecraft.ai.elements.generic;

import main.entity.obj.unit.Unit;
import main.game.ai.AI_Logic;
import main.game.battlecraft.ai.advanced.behavior.BehaviorMaster;
import main.game.battlecraft.ai.advanced.companion.MetaGoalMaster;
import main.game.battlecraft.ai.elements.actions.ActionManager;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequenceConstructor;
import main.game.battlecraft.ai.elements.actions.sequence.PathSequenceConstructor;
import main.game.battlecraft.ai.elements.actions.sequence.TurnSequenceConstructor;
import main.game.battlecraft.ai.elements.goal.GoalManager;
import main.game.battlecraft.ai.elements.task.TaskManager;
import main.game.battlecraft.ai.logic.types.atomic.AtomicAi;
import main.game.battlecraft.ai.tools.*;
import main.game.battlecraft.ai.tools.path.CellPrioritizer;
import main.game.battlecraft.ai.tools.path.PathBuilder;
import main.game.battlecraft.ai.tools.priority.PriorityManager;
import main.game.battlecraft.ai.tools.priority.ThreatAnalyzer;
import main.game.battlecraft.ai.tools.prune.PruneMaster;
import main.game.battlecraft.ai.tools.target.TargetingMaster;
import main.game.core.game.DC_Game;

import java.util.List;

/**
 * Created by JustMe on 3/3/2017.
 */
public abstract class AiHandler {
    protected AiMaster master;
    protected   DC_Game game;
    protected   Unit unit;


    public AiHandler() {

    }
    public AiHandler(AiHandler master) {
        this.master = (AiMaster) master;
        this.game= master.getGame();
    }

    public void initialize() {
        master.getHandlers().add(this);
    }

    public AI_Logic getLogic() {
        return master.getLogic();
    }

    public SituationAnalyzer getSituationAnalyzer() {
        return master.getSituationAnalyzer();
    }

    public TaskManager getTaskManager() {
        return master.getTaskManager();
    }

    public ThreatAnalyzer getThreatAnalyzer() {
        return master.getThreatAnalyzer();
    }

    public GoalManager getGoalManager() {
        return master.getGoalManager();
    }


    public ActionManager getActionManager() {
        return master.getActionManager();
    }

    public AiMaster getMaster() {
        return master;
    }

    public MetaGoalMaster getMetaGoalMaster() {
        return master.getMetaGoalMaster();
    }

    public AiScriptExecutor getScriptExecutor() {
        return master.getScriptExecutor();
    }

    public PriorityManager getPriorityManager() {
        return master.getPriorityManager();
    }


    public PruneMaster getPruneMaster() {
        return master.getPruneMaster();
    }


    public PathBuilder getPathBuilder() {
        return master.getPathBuilder();
    }


    public TargetingMaster getTargetingMaster() {
        return master.getTargetingMaster();
    }


    public Analyzer getAnalyzer() {
        return master.getAnalyzer();
    }


    public ParamAnalyzer getParamAnalyzer() {
        return master.getParamAnalyzer();
    }


    public ActionSequenceConstructor getActionSequenceConstructor() {
        return master.getActionSequenceConstructor();
    }


    public AiExecutor getExecutor() {
        return master.getExecutor();
    }


    public CellPrioritizer getCellPrioritizer() {
        return master.getCellPrioritizer();
    }

    
    public PathSequenceConstructor getPathSequenceConstructor() {
        return master.getPathSequenceConstructor();
    }


    public TurnSequenceConstructor getTurnSequenceConstructor() {
        return master.getTurnSequenceConstructor();
    }


    public List<AiHandler> getHandlers() {
        return master.getHandlers();
    }

    public BehaviorMaster getBehaviorMaster() {
        return master.getBehaviorMaster();
    }

    public AtomicAi getAtomicAi() {
        return master.getAtomicAi();
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }


    public DC_Game getGame() {
        return game;
    }
}
