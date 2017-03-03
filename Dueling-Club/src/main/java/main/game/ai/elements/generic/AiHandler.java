package main.game.ai.elements.generic;

import main.entity.obj.unit.Unit;
import main.game.ai.elements.actions.ActionManager;
import main.game.ai.elements.actions.sequence.ActionSequenceConstructor;
import main.game.ai.elements.actions.sequence.PathSequenceConstructor;
import main.game.ai.elements.actions.sequence.TurnSequenceConstructor;
import main.game.ai.elements.goal.GoalManager;
import main.game.ai.elements.task.TaskManager;
import main.game.ai.tools.AiExecutor;
import main.game.ai.tools.Analyzer;
import main.game.ai.tools.ParamAnalyzer;
import main.game.ai.tools.path.CellPrioritizer;
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
    protected   ActionSequenceConstructor actionSequenceConstructor;
    protected AiExecutor executor;
    protected CellPrioritizer cellPrioritizer;
    protected    AiHandler master;
    protected   DC_Game game;
    protected   Unit unit;
    protected   PathSequenceConstructor pathSequenceConstructor;
    protected   TurnSequenceConstructor turnSequenceConstructor;

    public AiHandler(DC_Game game) {
        this.game = game;
        this.master = this;
        this.actionSequenceConstructor = new ActionSequenceConstructor(this);
        this.taskManager =  new TaskManager(this);
        this.goalManager =  new GoalManager(this);
        this.actionManager =  new ActionManager(this);
        this.pruneMaster =  new PruneMaster(this);
        this.pathBuilder =  new PathBuilder(this);
        this.targetingMaster =  new TargetingMaster(this);
        this.analyzer =  new Analyzer(this);
        this.paramAnalyzer =  new ParamAnalyzer(this);
        this.cellPrioritizer =  new CellPrioritizer(this);
        pathSequenceConstructor = new PathSequenceConstructor(master);
        turnSequenceConstructor = new TurnSequenceConstructor(master);

        executor = new AiExecutor(game);

        this.actionSequenceConstructor  .initialize();
        this.taskManager  .initialize(); 
        this.goalManager  .initialize(); 
        this.actionManager  .initialize(); 
        this.pruneMaster  .initialize(); 
        this.pathBuilder  .initialize(); 
        this.targetingMaster  .initialize(); 
        this.analyzer  .initialize();  
        this.paramAnalyzer  .initialize(); 
        this.cellPrioritizer  .initialize();
        this.pathSequenceConstructor  .initialize();
        this.turnSequenceConstructor  .initialize();
    }

    public AiHandler(AiHandler master) {
        this.master = master;
    }
    public void initialize() {
        this.pathSequenceConstructor = master.pathSequenceConstructor ;
        this.turnSequenceConstructor =  master.turnSequenceConstructor ;
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

    public AiExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(AiExecutor executor) {
        this.executor = executor;
    }

    public CellPrioritizer getCellPrioritizer() {
        return cellPrioritizer;
    }

    public void setCellPrioritizer(CellPrioritizer cellPrioritizer) {
        this.cellPrioritizer = cellPrioritizer;
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



    public void setPathSequenceConstructor(PathSequenceConstructor pathSequenceConstructor) {
        this.pathSequenceConstructor = pathSequenceConstructor;
    }

    public void setTurnSequenceConstructor(TurnSequenceConstructor turnSequenceConstructor) {
        this.turnSequenceConstructor = turnSequenceConstructor;
    }

    public PathSequenceConstructor getPathSequenceConstructor() {
        return pathSequenceConstructor;
    }

    public TurnSequenceConstructor getTurnSequenceConstructor() {
        return turnSequenceConstructor;
    }
}
