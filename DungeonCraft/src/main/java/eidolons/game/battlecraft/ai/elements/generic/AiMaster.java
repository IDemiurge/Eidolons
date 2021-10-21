package eidolons.game.battlecraft.ai.elements.generic;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.behavior.BehaviorMasterOld;
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
import eidolons.game.battlecraft.ai.tools.path.PathBuilder;
import eidolons.game.battlecraft.ai.tools.path.PathBuilderAtomic;
import eidolons.game.battlecraft.ai.tools.path.alphastar.StarBuilder;
import eidolons.game.battlecraft.ai.tools.priority.PriorityManager;
import eidolons.game.battlecraft.ai.tools.priority.PriorityModifier;
import eidolons.game.battlecraft.ai.tools.prune.PruneMaster;
import eidolons.game.battlecraft.ai.tools.target.TargetingMaster;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import main.content.values.parameters.PARAMETER;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/3/2017.
 */
public abstract class AiMaster {
    protected DC_Game game;
    protected Unit unit;
    protected StringBuffer messageBuilder;
    protected List<AiHandler> handlers = new ArrayList<>();
    
    protected TaskManager taskManager;
    protected GoalManager goalManager;
    protected ActionManager actionManager;
    protected PriorityManager priorityManager;
    protected PruneMaster pruneMaster;
    protected PathBuilder pathBuilder;
    protected TargetingMaster targetingMaster;
    protected Analyzer analyzer;
    protected ParamAnalyzer paramAnalyzer;
    protected ActionSequenceConstructor actionSequenceConstructor;
    protected AiExecutor executor;
    protected PathSequenceConstructor pathSequenceConstructor;
    protected TurnSequenceConstructor turnSequenceConstructor;
    protected SituationAnalyzer situationAnalyzer;
    protected BehaviorMasterOld behaviorMaster;
    protected AtomicAi atomicAi;
    protected AiScriptExecutor scriptExecutor;
    protected AiPriorityConstantMaster priorityConstantMaster;
    protected PriorityModifier priorityModifier;
    protected PathBuilderAtomic pathBuilderAtomic;
    private final AiAutoGroupHandler autoGroupHandler;
    private final AiGroupHandler groupHandler;
    private final StarBuilder starBuilder;

    public AiMaster(DC_Game game) {
        this.game = game;
        handlers.add(actionSequenceConstructor = new ActionSequenceConstructor(this));
        handlers.add(taskManager = new TaskManager(this));
        handlers.add(goalManager = new GoalManager(this));
        handlers.add(actionManager = new ActionManager(this));
        handlers.add(pruneMaster = new PruneMaster(this));
        handlers.add(pathBuilder = PathBuilder.getInstance(this));
        handlers.add(targetingMaster = new TargetingMaster(this));
        handlers.add(analyzer = new Analyzer(this));
        handlers.add(paramAnalyzer = new ParamAnalyzer(this));
        handlers.add(situationAnalyzer = new SituationAnalyzer(this));
        handlers.add(pathSequenceConstructor = new PathSequenceConstructor(this));
        handlers.add(turnSequenceConstructor = new TurnSequenceConstructor(this));
        handlers.add(behaviorMaster = new BehaviorMasterOld(this));
        handlers.add(atomicAi = new AtomicAi(this));
        handlers.add(scriptExecutor = new AiScriptExecutor(this));
        handlers.add(priorityConstantMaster = new AiPriorityConstantMaster(this));
        handlers.add(priorityModifier = new PriorityModifier(this));
        handlers.add(pathBuilderAtomic = new PathBuilderAtomic(this));
        handlers.add(groupHandler = new AiGroupHandler(this));
        handlers.add(autoGroupHandler = new AiAutoGroupHandler(this));
        handlers.add(starBuilder = new StarBuilder(this));

        executor = new AiExecutor(game);
    }


    public void initialize() {
        for (AiHandler handler : handlers) {
            handler.initialize();
        }

    }

    public PathBuilderAtomic getPathBuilderAtomic() {
        return pathBuilderAtomic;
    }

    public AiPriorityConstantMaster getPriorityConstantMaster() {
        return priorityConstantMaster;
    }

    public PriorityModifier getPriorityModifier() {
        return priorityModifier;
    }


    public BehaviorMasterOld getBehaviorMaster() {
        return behaviorMaster;
    }

    public AtomicAi getAtomicAi() {
        return atomicAi;
    }

    public StringBuffer getMessageBuilder() {
        if (messageBuilder == null)
            messageBuilder = new StringBuffer();
        return messageBuilder;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public GoalManager getGoalManager() {
        return goalManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public PriorityManager getPriorityManager() {
        return priorityManager;
    }

    public PruneMaster getPruneMaster() {
        return pruneMaster;
    }

    public PathBuilder getPathBuilder() {
        return pathBuilder;
    }

    public TargetingMaster getTargetingMaster() {
        return targetingMaster;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public ParamAnalyzer getParamAnalyzer() {
        return paramAnalyzer;
    }

    public ActionSequenceConstructor getActionSequenceConstructor() {
        return actionSequenceConstructor;
    }

    public AiExecutor getExecutor() {
        return executor;
    }

    public PathSequenceConstructor getPathSequenceConstructor() {
        return pathSequenceConstructor;
    }

    public TurnSequenceConstructor getTurnSequenceConstructor() {
        return turnSequenceConstructor;
    }

    public SituationAnalyzer getSituationAnalyzer() {
        return situationAnalyzer;
    }

    public float getParamPriority(PARAMETER p) {
        return getPriorityConstantMaster().getParamPriority(p);
    }

    public float getConstValue(AiConst p) {
        return getPriorityConstantMaster().getConstValue(p);
    }

    public int getConstInt(AiConst p) {
        return getPriorityConstantMaster().getConstInt(p);
    }

    public List<AiHandler> getHandlers() {
        return handlers;
    }

    public AiScriptExecutor getScriptExecutor() {
        return scriptExecutor;
    }

    public DC_Game getGame() {
        return game;
    }

    public Unit getUnit() {
        if (unit == null) {
            return Core.getMainHero(); //TODO dangerous
        }
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public UnitAI getUnitAI() {
        return getUnit().getAI();
    }

    public AI_Manager getAiManager() {
        return game.getAiManager();
    }
    public AI_Manager getManager() {
        return game.getAiManager();
    }


    public AiAutoGroupHandler getAutoGroupHandler() {
        return autoGroupHandler;
    }

    public AiGroupHandler getGroupHandler() {
        return groupHandler;
    }

    public StarBuilder getStarBuilder() {
        return starBuilder;
    }

    public boolean isDefaultAiGroupForUnitOn() {
        return false;
    }
}
