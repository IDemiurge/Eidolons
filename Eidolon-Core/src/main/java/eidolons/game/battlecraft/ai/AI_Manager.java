package eidolons.game.battlecraft.ai;

import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.actions.ActionManager;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequenceConstructor;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.goal.GoalManager;
import eidolons.game.battlecraft.ai.elements.task.TaskManager;
import eidolons.game.battlecraft.ai.tools.AiExecutor;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.battlecraft.ai.tools.priority.PriorityManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.system.libgdx.GdxStatic;
import main.game.bf.Coordinates;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

public class AI_Manager extends AiMaster {
    public static final boolean BRUTE_AI_MODE = false;
    public static final boolean DEV_MODE = false;
    public static final boolean MELEE_HACK = true;
    private static boolean running;
    private static boolean off;
    private static final List<ActiveObj> brokenActions = new ArrayList<>();

    public AI_Manager(DC_Game game) {
        super(game);
        //        logic = initLogic();
        priorityManager = DC_PriorityManager.init(this);
    }

    public static boolean isAiVisionHack() {
        return true;
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

    public static List<ActiveObj> getBrokenActions() {
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

    public AiAction getAction(Unit unit) {
        if (unit.isMine()) {
            unit.getQuickItemActives();
        }
        messageBuilder = new StringBuffer();
        AiAction aiAction = null;
        running = true;
        setUnit(unit);
        Coordinates bufferedCoordinates = unit.getCoordinates();
        try {
            aiAction = actionManager.chooseAction();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            running = false;
        }
        if (aiAction == null) {
            running = true;
            try {
                aiAction = actionManager.getForcedAction(getAI(unit));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.AI, getUnit() +
                        "'s Forced Action choice failed: " + e.getMessage());
                return null;
            } finally {
                running = false;
                SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.AI, getUnit() +
                        " opts for Forced Action: " + aiAction);
            }
        } else {
            try {
                getUnitAI().getUsedActions().add(aiAction.getActive());
                getMessageBuilder().append("Task: ").append(aiAction.getTaskDescription());
                if (!CoreEngine.isGraphicsOff()) {
                    if (game.isDebugMode())
                        GdxStatic.floatingText( VisualEnums.TEXT_CASES.BATTLE_COMMENT,
                                        getMessageBuilder().toString(), getUnit());
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (!bufferedCoordinates.equals(unit.getCoordinates())) {
            unit.setCoordinates(bufferedCoordinates);
        }

        return aiAction;
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

    public List<GroupAI> getGroups() {
        return getGroupHandler().getGroups();
    }

@Override
    public boolean isDefaultAiGroupForUnitOn() {
        //        return isRngDungeon()  ;
        return true;
    }

    public AiAction getDefaultAction(Unit activeUnit) {
        return getAtomicAi().getAtomicWait(activeUnit);
    }

}
