package main.game.ai;

import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.PLAYER_AI_TYPE;
import main.data.XLinkedMap;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.game.ai.elements.actions.Action;
import main.game.ai.elements.actions.ActionManager;
import main.game.ai.elements.goal.GoalManager;
import main.game.ai.elements.task.TaskManager;
import main.game.ai.tools.Executor;
import main.game.ai.tools.priority.DC_PriorityManager;
import main.game.ai.tools.priority.PriorityManager;

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
    private Map<Unit, UnitAI> aiMap = new XLinkedMap<>();
    private PLAYER_AI_TYPE type = AiEnums.PLAYER_AI_TYPE.BRUTE;
    public AI_Manager(DC_Game game) {

        this.game = game;
        priorityManager =  DC_PriorityManager.init();
        taskManager = new TaskManager();
        executor = new Executor(game);
    }

    public static boolean isRunning() {
        return running;
    }

    public static Set<Action> getBrokenActions() {
        return brokenActions;
    }

    public static Unit chooseEnemyToEngage(Unit obj, List<Unit> units) {
        if (obj.getAiType() == AiEnums.AI_TYPE.CASTER) {
            return null;
        }
        if (obj.getAiType() == AiEnums.AI_TYPE.ARCHER) {
            return null;
        }
        if (obj.getAiType() == AiEnums.AI_TYPE.SNEAK) {
            return null;
        }
        Unit topPriorityUnit = null;
        int topPriority = -1;
        for (Unit u : units) {
            int priority = DC_PriorityManager.getUnitPriority(u, true);
            if (priority > topPriority) {
                topPriority = priority;
                topPriorityUnit = u;
            }
        }
        return topPriorityUnit;
    }

    public static GroupAI getCustomUnitGroup() {
        if (customGroup == null) {
            customGroup = new GroupAI(null);
        }
        return customGroup;
    }

    public void init() {
        actionManager = new ActionManager(this);
        game.getPlayer(false).setPlayerAI(new PlayerAI(getType()));
    }

    public boolean makeAction(final Unit unit) {
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
                } else {
                    try {
                        getAI(unit).setLastAction(action);
                        if (!executor.execute(action)) {
                            brokenActions.add(action);
                        } else {
                            brokenActions.remove(action);
                        }
                        getAI(unit).standingOrderActionComplete();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        // TODO block action, and try again!
                    }
                }
            }
        }, unit.getName() + " AI Thread").start();

        return true;

    }

    public UnitAI getAI(Unit unit) {
        return unit.getUnitAI();
    }

    public DC_Game getGame() {
        return game;
    }

    public  PriorityManager getPriorityManager() {
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
