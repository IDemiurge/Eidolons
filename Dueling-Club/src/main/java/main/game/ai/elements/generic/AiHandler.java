package main.game.ai.elements.generic;

import main.entity.obj.unit.Unit;
import main.game.ai.AI_Logic;
import main.game.ai.elements.actions.ActionManager;
import main.game.ai.elements.actions.sequence.ActionSequenceConstructor;
import main.game.ai.elements.goal.GoalManager;
import main.game.ai.elements.task.TaskManager;
import main.game.ai.tools.Analyzer;
import main.game.ai.tools.ParamAnalyzer;
import main.game.ai.tools.path.PathBuilder;
import main.game.ai.tools.priority.PriorityManager;
import main.game.ai.tools.prune.PruneMaster;
import main.game.ai.tools.target.TargetingMaster;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 3/3/2017.
 */
public class AiHandler {
   protected TaskManager taskManager;
    protected  GoalManager goalManager;
    protected  ActionManager actionManager;
    protected  PriorityManager priorityManager;
    protected  PruneMaster pruneMaster;
    protected  PathBuilder pathBuilder;
    protected  TargetingMaster targetingMaster;
    protected  Analyzer analyzer;
    protected  ParamAnalyzer paramAnalyzer;
    protected   AI_Logic logic;
    protected   ActionSequenceConstructor actionSequenceConstructor;
    protected    AiHandler master;
    protected   DC_Game game;
    protected   Unit unit;

    public AiHandler() {
        this.master = this;
        this.actionSequenceConstructor = new ActionSequenceConstructor(this);
//        this.taskManager =  new TaskManager(this);
//        this.goalManager =  new GoalManager(this);
//        this.actionManager =  new ActionManager(this);
//        this.priorityManager =  new PriorityManager(this) {
//        };
//        this.pruneMaster =  new PruneMaster(this);
//        this.pathBuilder =  new PathBuilder(this);
//        this.targetingMaster =  new TargetingMaster(this);
//        this.analyzer =  new Analyzer(this);
//        this.paramAnalyzer =  new ParamAnalyzer(this);
//        this.logic =  new DC_AI_Logic(this) {
//        };
    }

    public AiHandler(AiHandler master) {
        this.master = master;
        this.actionSequenceConstructor = master.actionSequenceConstructor;
        this.taskManager = master.taskManager;
        this.goalManager = master.goalManager;
        this.actionManager = master.actionManager;
        this.priorityManager = master.priorityManager;
        this.pruneMaster = master.pruneMaster;
        this.pathBuilder = master.pathBuilder;
        this.targetingMaster = master.targetingMaster;
        this.analyzer = master.analyzer;
        this.paramAnalyzer = master.paramAnalyzer;
        this.logic = master.logic;
    }

    public ActionSequenceConstructor getActionSequenceConstructor() {
        return actionSequenceConstructor;
    }

    public void setActionSequenceConstructor(ActionSequenceConstructor actionSequenceConstructor) {
        this.actionSequenceConstructor = actionSequenceConstructor;
    }

    public AiHandler getMaster() {
        return master;
    }

    public void setMaster(AiHandler master) {
        this.master = master;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public GoalManager getGoalManager() {
        return goalManager;
    }

    public void setGoalManager(GoalManager goalManager) {
        this.goalManager = goalManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public void setActionManager(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    public PriorityManager getPriorityManager() {
        return priorityManager;
    }

    public void setPriorityManager(PriorityManager priorityManager) {
        this.priorityManager = priorityManager;
    }

    public PruneMaster getPruneMaster() {
        return pruneMaster;
    }

    public void setPruneMaster(PruneMaster pruneMaster) {
        this.pruneMaster = pruneMaster;
    }

    public PathBuilder getPathBuilder() {
        return pathBuilder;
    }

    public void setPathBuilder(PathBuilder pathBuilder) {
        this.pathBuilder = pathBuilder;
    }

    public TargetingMaster getTargetingMaster() {
        return targetingMaster;
    }

    public void setTargetingMaster(TargetingMaster targetingMaster) {
        this.targetingMaster = targetingMaster;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public ParamAnalyzer getParamAnalyzer() {
        return paramAnalyzer;
    }

    public void setParamAnalyzer(ParamAnalyzer paramAnalyzer) {
        this.paramAnalyzer = paramAnalyzer;
    }

    public AI_Logic getLogic() {
        return logic;
    }

    public void setLogic(AI_Logic logic) {
        this.logic = logic;
    }
}
