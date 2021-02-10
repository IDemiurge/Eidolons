package eidolons.game.battlecraft.ai.elements.generic;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.behavior.BehaviorMasterOld;
import eidolons.game.battlecraft.ai.advanced.companion.MetaGoalMaster;
import eidolons.game.battlecraft.ai.advanced.machine.AiConst;
import eidolons.game.battlecraft.ai.advanced.machine.AiPriorityConstantMaster;
import eidolons.game.battlecraft.ai.elements.actions.ActionManager;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequenceConstructor;
import eidolons.game.battlecraft.ai.elements.actions.sequence.PathSequenceConstructor;
import eidolons.game.battlecraft.ai.elements.actions.sequence.TurnSequenceConstructor;
import eidolons.game.battlecraft.ai.elements.goal.GoalManager;
import eidolons.game.battlecraft.ai.elements.task.TaskManager;
import eidolons.game.battlecraft.ai.logic.atomic.AtomicAi;
import eidolons.game.battlecraft.ai.tools.*;
import eidolons.game.battlecraft.ai.tools.path.CellPrioritizer;
import eidolons.game.battlecraft.ai.tools.path.PathBuilder;
import eidolons.game.battlecraft.ai.tools.path.alphastar.StarBuilder;
import eidolons.game.battlecraft.ai.tools.priority.PriorityManager;
import eidolons.game.battlecraft.ai.tools.priority.PriorityModifier;
import eidolons.game.battlecraft.ai.tools.priority.ThreatAnalyzer;
import eidolons.game.battlecraft.ai.tools.prune.PruneMaster;
import eidolons.game.battlecraft.ai.tools.target.TargetingMaster;
import eidolons.game.core.game.DC_Game;
import main.content.values.parameters.PARAMETER;

import java.util.List;

/**
 * Created by JustMe on 3/3/2017.
 */
public abstract class AiHandler {
    protected AiMaster master;
    protected DC_Game game;


    protected boolean isDebug() {
        // if (OptionsMaster.getGameplayOptions().getBooleanValue(GameplayOptions.GAMEPLAY_OPTION.AI_DEBUG)) {
            return false;
        // }
        // return false;
    }

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
    }

    public SituationAnalyzer getSituationAnalyzer() {
        return master.getSituationAnalyzer();
    }

    public TaskManager getTaskManager() {
        return master.getTaskManager();
    }
    public StarBuilder getStarBuilder() {
        return master.getStarBuilder();
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

    public AiAutoGroupHandler getAutoGroupHandler() {
        return master.getAutoGroupHandler();
    }

    public AiGroupHandler getGroupHandler() {
        return master.getGroupHandler();
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

    public AiPriorityConstantMaster getPriorityConstantMaster() {
        return getMaster().getPriorityConstantMaster();
    }

    public List<AiHandler> getHandlers() {
        return master.getHandlers();
    }

    public BehaviorMasterOld getBehaviorMaster() {
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

    public UnitAI getUnitAi() {
        return getMaster().getUnitAI();
    }

}
