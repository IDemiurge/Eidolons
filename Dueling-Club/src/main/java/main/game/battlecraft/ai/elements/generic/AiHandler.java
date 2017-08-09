package main.game.battlecraft.ai.elements.generic;

import main.content.values.parameters.PARAMETER;
import main.entity.obj.unit.Unit;
import main.game.ai.AI_Logic;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.advanced.behavior.BehaviorMaster;
import main.game.battlecraft.ai.advanced.companion.MetaGoalMaster;
import main.game.battlecraft.ai.advanced.machine.AiConst;
import main.game.battlecraft.ai.advanced.machine.AiPriorityConstantMaster;
import main.game.battlecraft.ai.advanced.machine.PriorityProfile;
import main.game.battlecraft.ai.advanced.machine.PriorityProfileManager;
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
import main.game.battlecraft.ai.tools.priority.PriorityModifier;
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
    protected DC_Game game;


    public AiHandler(AiMaster master) {
        this.master = master;
        if (master != null)
            this.game = master.getGame();
    }

    public float getParamPriority(PARAMETER p) {
        return getMaster().getParamPriority(p);
    }

    public float getConstValue(AiConst p) {
        return getMaster().getConstValue(p);
    }

    public int getConstInt(AiConst p) {
        return getMaster().getConstInt(p);
    }


    public boolean isAtomicAiOn() {
        return getAtomicAi().isOn();
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

    public PriorityProfile getProfile() {
        return getMaster().getProfile();
    }

    public AiPriorityConstantMaster getPriorityConstantMaster() {
        return getMaster().getPriorityConstantMaster();
    }

    public PriorityProfileManager getPriorityProfileManager() {
        return getMaster().getPriorityProfileManager();
    }

    public List<AiHandler> getHandlers() {
        return master.getHandlers();
    }

    public BehaviorMaster getBehaviorMaster() {
        return master.getBehaviorMaster();
    }

    public PriorityModifier getPriorityModifier() {
        return getMaster().getPriorityModifier();
    }

    public AtomicAi getAtomicAi() {
        return master.getAtomicAi();
    }

    public DC_Game getGame() {
        return game;
    }

    public Unit getUnit() {
        return getMaster().getUnit();
    }
    public UnitAI getUnitAI() {
        return getMaster().getUnitAI();
    }

}
