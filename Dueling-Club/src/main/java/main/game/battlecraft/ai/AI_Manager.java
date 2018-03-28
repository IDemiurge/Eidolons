package main.game.battlecraft.ai;

import main.client.dc.Launcher;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.PLAYER_AI_TYPE;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.ActionManager;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequenceConstructor;
import main.game.battlecraft.ai.elements.generic.AiMaster;
import main.game.battlecraft.ai.elements.goal.GoalManager;
import main.game.battlecraft.ai.elements.task.TaskManager;
import main.game.battlecraft.ai.tools.AiExecutor;
import main.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import main.game.battlecraft.ai.tools.priority.PriorityManager;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.libgdx.anims.text.FloatingTextMaster;
import main.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;

public class AI_Manager extends AiMaster {
    private static boolean running;
    private static boolean off;
    private static List<DC_ActiveObj> brokenActions = new ArrayList<>();
    private GroupAI allyGroup;
    private GroupAI enemyGroup;
    private PLAYER_AI_TYPE type = AiEnums.PLAYER_AI_TYPE.BRUTE;
    private List<GroupAI> groups;

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

    public static void setBrokenActions(List<DC_ActiveObj> brokenActions) {
        AI_Manager.brokenActions = brokenActions;
    }

    public GroupAI getAllyGroup() {
        if (allyGroup == null) {
            allyGroup = new GroupAI();
        }
        return allyGroup;
    }

    public GroupAI getEnemyGroup() {
        if (enemyGroup == null) {
            enemyGroup = new GroupAI();
        }
        return enemyGroup;
    }

    public GroupAI getCustomUnitGroup(Unit unit) {
        if (unit.isMine()) {
            return
             getAllyGroup();
        }
        if (isCustomGroups())
            return null;
        return getEnemyGroup();
    }

    private boolean isCustomGroups() {
        return ExplorationMaster.isExplorationSupported(getGame());
    }

    public void init() {
        initialize();
        game.getPlayer(false).setPlayerAI(new PlayerAI(getType()));
    }

    public Action getAction(Unit unit) {
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
            } finally {
                running = false;
                SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.AI, getUnit() +
                 " opts for Forced Action: " + action);
            }
        } else {
            try {
                getMessageBuilder().append("Task: " + action.getTaskDescription());
                if (!CoreEngine.isGraphicsOff()) {
                    if (game.isDebugMode() || Launcher.DEV_MODE)
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

    public PLAYER_AI_TYPE getType() {
        return type;
    }

    public void setType(PLAYER_AI_TYPE type) {
        this.type = type;
    }


    public List<GroupAI> getGroups() {
        resetGroups();
        return groups;
    }

    public void setGroups(List<GroupAI> groups) {
        this.groups = groups;
    }

    private void updateGroups() {
        double join_distance = 1;
        double leave_distance = 5;
        for (GroupAI group : new ArrayList<>(groups))
            for (Unit unit : group.getMembers()) {
                double distance = PositionMaster.getExactDistance(
                 group.getLeader().getCoordinates(),
                 unit.getCoordinates());
                if (distance > leave_distance) {
                    group.remove(unit);
                    unit.getAI().setGroupAI(new GroupAI(unit));
                    groups.add(unit.getAI().getGroup());
                    //wait until clear that they're unassigned?
                }
            }
// join
        for (GroupAI group : groups)
            for (Unit unit : group.getMembers()) {
                double distance = PositionMaster.getExactDistance(
                 group.getLeader().getCoordinates(),
                 unit.getCoordinates());
                if (distance > leave_distance) {
                    group.remove(unit);
                    unit.getAI().setGroupAI(new GroupAI(unit));
                }
            }
    }

    private void resetGroups() {
        //by proximity... not all mobs will be part of a group

//        if (groups!=null ){
//            if (!groups.isEmpty()) {
//            }
//        }
        if (groups == null)
            groups = new ArrayList<>();

        for (Object sub : game.getBattleMaster().getPlayerManager().getPlayers()) {
            DC_Player player = (DC_Player) sub;
            for (Unit unit : player.getControlledUnits_()) {
                GroupAI group = unit.getAI().getGroup();
                if (group == null)
                    group = new GroupAI(unit);
                for (Unit unit1 : player.getControlledUnits_()) {
                    if (unit1.getAI().getGroup() != null)
                        continue;
                    if (unit1.equals(unit))
                        continue;
                    double max_distance = 2.5;
                    if (PositionMaster.getExactDistance(unit1.getCoordinates(),
                     unit.getCoordinates()) >= max_distance)
                        continue;
                    if (!game.getVisionMaster().getSightMaster().getClearShotCondition().check(unit, unit1))
                        continue;
                    group.add(unit1);

                }
                if (!groups.contains(group))
                    groups.add(group);
            }


        }
        if (!groups.isEmpty())
            return;
        else
            return;
    }

}
