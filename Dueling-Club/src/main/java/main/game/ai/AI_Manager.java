package main.game.ai;

import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.PLAYER_AI_TYPE;
import main.data.XLinkedMap;
import main.entity.obj.unit.Unit;
import main.game.ai.elements.actions.Action;
import main.game.ai.elements.actions.ActionManager;
import main.game.ai.elements.actions.sequence.ActionSequenceConstructor;
import main.game.ai.elements.generic.AiMaster;
import main.game.ai.elements.goal.GoalManager;
import main.game.ai.elements.task.TaskManager;
import main.game.ai.tools.AiExecutor;
import main.game.ai.tools.priority.DC_PriorityManager;
import main.game.ai.tools.priority.PriorityManager;
import main.game.core.game.DC_Game;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AI_Manager extends AiMaster {
    static Set<Action> brokenActions = new HashSet<>();
    private static GroupAI customGroup;
    private static boolean running;
    private Map<Unit, UnitAI> aiMap = new XLinkedMap<>();
    private PLAYER_AI_TYPE type = AiEnums.PLAYER_AI_TYPE.BRUTE;

    public AI_Manager(DC_Game game) {
        super(game);
//        logic = initLogic();
        priorityManager = DC_PriorityManager.init(this);
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

    public static boolean isRunning() {
        return running;
    }

    public static Set<Action> getBrokenActions() {
        return brokenActions;
    }

    public void init() {
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

    public PriorityManager getPriorityManager() {
        return priorityManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public GoalManager getGoalManager() {
        return goalManager;
    }

    public ActionSequenceConstructor getActionSequenceConstructor() {
        return actionSequenceConstructor;
    }

    public AiExecutor getExecutor() {
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
