package main.system.ai;

import main.content.CONTENT_CONSTS.AI_TYPE;
import main.content.CONTENT_CONSTS.PLAYER_AI_TYPE;
import main.data.XLinkedMap;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.system.ai.logic.actions.Action;
import main.system.ai.logic.actions.ActionManager;
import main.system.ai.logic.goal.GoalManager;
import main.system.ai.logic.priority.PriorityManager;
import main.system.ai.logic.task.TaskManager;
import main.system.ai.tools.Executor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AI_Manager {
    static Set<Action> brokenActions = new HashSet<>();
    private static GroupAI customGroup;
    private static boolean running;
    private DC_Game game;
    private PriorityManager priorityManager;
    private ActionManager actionManager;
    private GoalManager goalManager;
    private Executor executor;
    private TaskManager taskManager;
    private Map<DC_HeroObj, UnitAI> aiMap = new XLinkedMap<>();
    private PLAYER_AI_TYPE type = PLAYER_AI_TYPE.BRUTE;
    public AI_Manager(DC_Game game) {

        this.game = game;
        priorityManager = new PriorityManager(game);
        taskManager = new TaskManager();
        executor = new Executor(game);
    }

    public static boolean isRunning() {
        return running;
    }

    public static Set<Action> getBrokenActions() {
        return brokenActions;
    }

    public static DC_HeroObj chooseEnemyToEngage(DC_HeroObj obj, List<DC_HeroObj> units) {
        if (obj.getAiType() == AI_TYPE.CASTER)
            return null;
        if (obj.getAiType() == AI_TYPE.ARCHER)
            return null;
        if (obj.getAiType() == AI_TYPE.SNEAK)
            return null;
        DC_HeroObj topPriorityUnit = null;
        int topPriority = -1;
        for (DC_HeroObj u : units) {
            int priority = PriorityManager.getUnitPriority(u, true);
            if (priority > topPriority) {
                topPriority = priority;
                topPriorityUnit = u;
            }
        }
        return topPriorityUnit;
    }

    public static GroupAI getCustomUnitGroup() {
        if (customGroup == null)
            customGroup = new GroupAI(null);
        return customGroup;
    }

    public void init() {
        actionManager = new ActionManager(this);
        game.getPlayer(false).setPlayerAI(new PlayerAI(getType()));
    }

    public boolean makeAction(final DC_HeroObj unit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Action action = null;
                running = true;
                try {
                    action = actionManager.chooseAction(getAI(unit));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    running = false;
                }
                if (action == null) {
                    running = true;
                    try {
                        action = actionManager.getForcedAction(getAI(unit));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        running = false;
                    }
                }
                if (action == null) {
                    game.getManager().freezeUnit(unit);
                    game.getManager().unitActionCompleted(null, true);
                } else
                    try {
                        getAI(unit).setLastAction(action);
                        if (!executor.execute(action))
                            brokenActions.add(action);
                        else
                            brokenActions.remove(action);
                        getAI(unit).standingOrderActionComplete();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        // TODO block action, and try again!
                    }
            }
        }, unit.getName() + " AI Thread").start();

        return true;

    }

    public UnitAI getAI(DC_HeroObj unit) {
        return unit.getUnitAI();
    }

    public DC_Game getGame() {
        return game;
    }

    public PriorityManager getPriorityManager() {
        return priorityManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public GoalManager getGoalManager() {
        return goalManager;
    }

    public Executor getExecutor() {
        return executor;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public PLAYER_AI_TYPE getType() {
        return type;
    }

    public void setType(PLAYER_AI_TYPE type) {
        this.type = type;
    }

}
