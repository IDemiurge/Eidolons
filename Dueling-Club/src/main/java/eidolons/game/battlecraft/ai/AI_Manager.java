package eidolons.game.battlecraft.ai;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.ActionManager;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequenceConstructor;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.goal.GoalManager;
import eidolons.game.battlecraft.ai.elements.task.TaskManager;
import eidolons.game.battlecraft.ai.tools.AiExecutor;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.battlecraft.ai.tools.priority.PriorityManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.netherflame.boss.ai.BossAi;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.game.bf.Coordinates;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

public class AI_Manager extends AiMaster {
    public static final boolean BRUTE_AI_MODE = false;
    public static final boolean DEV_MODE = false;
    private static boolean running;
    private static boolean off;
    private static final List<DC_ActiveObj> brokenActions = new ArrayList<>();
    protected BossAi bossAi;

    public AI_Manager(DC_Game game) {
        super(game);
        //        logic = initLogic();
        priorityManager = DC_PriorityManager.init(this);
    }

    public static boolean isSimplifiedLogic() {
        return true;
    }

    public static boolean isRunning() {
        return running;
    }

    public static boolean isOff() {
        return off;
    }

    public static void setOff(boolean off) {
        AI_Manager.off = off;
    }

    public static List<DC_ActiveObj> getBrokenActions() {
        return brokenActions;
    }


    public GroupAI getAllyGroup() {
        return getAutoGroupHandler().getAllyGroup();
    }

    public GroupAI getEnemyGroup() {
        return getAutoGroupHandler().getEnemyGroup();
    }

    public GroupAI getCustomUnitGroup(Unit unit) {
        if (unit.isMine()) {
            return
                    getAllyGroup();
        }
        if (isCustomGroups())
            return new GroupAI(unit);
        return getEnemyGroup();
    }

    private boolean isCustomGroups() {
        return ExplorationMaster.isExplorationSupported(getGame());
    }

    public void init() {
        initialize();
    }

    public Action getAction(Unit unit) {
        if (unit.isBoss()) {
            return getBossAi().getAction(unit);
        }
        if (unit.isMine()) {
            unit.getQuickItemActives();
        }
        messageBuilder = new StringBuffer();
        Action action = null;
        running = true;
        setUnit(unit);
        Coordinates bufferedCoordinates = unit.getCoordinates();
        try {
            action = actionManager.chooseAction();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            running = false;
        }
        if (action == null) {
            running = true;
            try {
                action = actionManager.getForcedAction(getAI(unit));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.AI, getUnit() +
                        "'s Forced Action choice failed: " + e.getMessage());
                return null;
            } finally {
                running = false;
                SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.AI, getUnit() +
                        " opts for Forced Action: " + action);
            }
        } else {
            try {
                getUnitAI().getUsedActions().add(action.getActive());
                getMessageBuilder().append("Task: ").append(action.getTaskDescription());
                if (!CoreEngine.isGraphicsOff()) {
                    if (game.isDebugMode())
                        FloatingTextMaster.getInstance().
                                createFloatingText(TEXT_CASES.BATTLE_COMMENT,
                                        getMessageBuilder().toString(), getUnit());
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (!bufferedCoordinates.equals(unit.getCoordinates())) {
            unit.setCoordinates(bufferedCoordinates);
        }

        return action;
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

    protected BossAi getBossAi() {
        return getGame().getMetaMaster().getBossManager().getAi();
    }

    public List<GroupAI> getGroups() {
        return getGroupHandler().getGroups();
    }


    public boolean isDefaultAiGroupForUnitOn() {
        //        return isRngDungeon()  ;
        return true;
    }

    public Action getDefaultAction(Unit activeUnit) {
        return getAtomicAi().getAtomicWait(activeUnit);
    }

}
