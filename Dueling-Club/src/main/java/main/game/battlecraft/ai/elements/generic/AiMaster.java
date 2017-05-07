package main.game.battlecraft.ai.elements.generic;

import main.entity.obj.unit.Unit;
import main.game.ai.AI_Logic;
import main.game.battlecraft.ai.advanced.behavior.BehaviorMaster;
import main.game.battlecraft.ai.elements.actions.ActionManager;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequenceConstructor;
import main.game.battlecraft.ai.elements.actions.sequence.PathSequenceConstructor;
import main.game.battlecraft.ai.elements.actions.sequence.TurnSequenceConstructor;
import main.game.battlecraft.ai.elements.goal.GoalManager;
import main.game.battlecraft.ai.elements.task.TaskManager;
import main.game.battlecraft.ai.logic.types.atomic.AtomicAi;
import main.game.battlecraft.ai.tools.AiExecutor;
import main.game.battlecraft.ai.tools.Analyzer;
import main.game.battlecraft.ai.tools.ParamAnalyzer;
import main.game.battlecraft.ai.tools.SituationAnalyzer;
import main.game.battlecraft.ai.tools.path.CellPrioritizer;
import main.game.battlecraft.ai.tools.path.PathBuilder;
import main.game.battlecraft.ai.tools.priority.PriorityManager;
import main.game.battlecraft.ai.tools.priority.ThreatAnalyzer;
import main.game.battlecraft.ai.tools.prune.PruneMaster;
import main.game.battlecraft.ai.tools.target.TargetingMaster;
import main.game.core.game.DC_Game;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 3/3/2017.
 */
public class AiMaster extends AiHandler {
    protected AI_Logic logic;

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
    protected CellPrioritizer cellPrioritizer;
    protected PathSequenceConstructor pathSequenceConstructor;
    protected TurnSequenceConstructor turnSequenceConstructor;
    private SituationAnalyzer situationAnalyzer;
    private ThreatAnalyzer threatAnalyzer;
    private BehaviorMaster behaviorMaster;
    private AtomicAi atomicAi;
    private List<AiHandler> handlers = new LinkedList<>();

    public AiMaster(DC_Game game) {
        this.game = game;
        this.master = this;
        this.actionSequenceConstructor = new ActionSequenceConstructor(this);
        this.taskManager = new TaskManager(this);
        this.goalManager = new GoalManager(this);
        this.actionManager = new ActionManager(this);
        this.pruneMaster = new PruneMaster(this);
        this.pathBuilder = PathBuilder.getInstance(this);
        this.targetingMaster = new TargetingMaster(this);
        this.analyzer = new Analyzer(this);
        this.paramAnalyzer = new ParamAnalyzer(this);
        this.situationAnalyzer = new SituationAnalyzer(this);
        this.threatAnalyzer = new ThreatAnalyzer(this);
        this.cellPrioritizer = new CellPrioritizer(this);
        this.pathSequenceConstructor = new PathSequenceConstructor(master);
        this. turnSequenceConstructor = new TurnSequenceConstructor(master);
        this.behaviorMaster = new BehaviorMaster(master);
        this. atomicAi = new AtomicAi(master);

        executor = new AiExecutor(game);


    }

    @Override
    public void initialize() {
        this.actionSequenceConstructor.initialize();
        this.taskManager.initialize();
        this.goalManager.initialize();
        this.actionManager.initialize();
        this.pruneMaster.initialize();
        this.pathBuilder.initialize();
        this.targetingMaster.initialize();
        this.analyzer.initialize();
        this.paramAnalyzer.initialize();
        this.cellPrioritizer.initialize();
        this.pathSequenceConstructor.initialize();
        this.turnSequenceConstructor.initialize();
        this.behaviorMaster.initialize();
        this.atomicAi.initialize();
        this.threatAnalyzer.initialize();
        this.situationAnalyzer.initialize();
    }

    public void setUnit(Unit unit) {
        this.unit=unit;
getHandlers().forEach(handler -> handler.setUnit(unit));
    }

    public BehaviorMaster getBehaviorMaster() {
        return behaviorMaster;
    }

    public AtomicAi getAtomicAi() {
        return atomicAi;
    }

    public AI_Logic getLogic() {
        return logic;
    }

    @Override
    public TaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public GoalManager getGoalManager() {
        return goalManager;
    }

    @Override
    public ActionManager getActionManager() {
        return actionManager;
    }

    @Override
    public PriorityManager getPriorityManager() {
        return priorityManager;
    }

    @Override
    public PruneMaster getPruneMaster() {
        return pruneMaster;
    }

    @Override
    public PathBuilder getPathBuilder() {
        return pathBuilder;
    }

    @Override
    public TargetingMaster getTargetingMaster() {
        return targetingMaster;
    }

    @Override
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    @Override
    public ParamAnalyzer getParamAnalyzer() {
        return paramAnalyzer;
    }

    @Override
    public ActionSequenceConstructor getActionSequenceConstructor() {
        return actionSequenceConstructor;
    }

    @Override
    public AiExecutor getExecutor() {
        return executor;
    }

    @Override
    public CellPrioritizer getCellPrioritizer() {
        return cellPrioritizer;
    }

    @Override
    public PathSequenceConstructor getPathSequenceConstructor() {
        return pathSequenceConstructor;
    }

    @Override
    public TurnSequenceConstructor getTurnSequenceConstructor() {
        return turnSequenceConstructor;
    }

    public ThreatAnalyzer getThreatAnalyzer() {
        return threatAnalyzer;
    }

    public SituationAnalyzer getSituationAnalyzer() {
        return situationAnalyzer;
    }

    @Override
    public List<AiHandler> getHandlers() {
        return handlers;
    }
}
