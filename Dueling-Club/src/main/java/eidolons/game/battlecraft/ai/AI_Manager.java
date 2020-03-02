package eidolons.game.battlecraft.ai;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Cell;
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
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner.UNIT_GROUP_TYPE;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.PLAYER_AI_TYPE;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.SortMaster;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AI_Manager extends AiMaster {
    public static final boolean BRUTE_AI_MODE = false;
    public static final boolean DEV_MODE = false;
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
            return new GroupAI(unit);
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
        if (unit.isBoss()) {
            return getBossAi(unit).getAction();
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
                SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.AI, getUnit() +
                 "'s Forced Action choice failed: " + e.getMessage());
                return null;
            } finally {
                running = false;
                SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.AI, getUnit() +
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

    private void initGroups() {
        groups = new ArrayList<>();
        for (LevelBlock block : game.getDungeonMaster().getDungeonLevel().getBlocks()) {
            for (List<ObjAtCoordinate> list : block.getUnitGroups().keySet()) {
                GroupAI group = new GroupAI();
                group.setType(block.getUnitGroups().get(list));
                group.setBlock(block);
                for (ObjAtCoordinate at : list) {
                    game.getUnitsForCoordinates(at.getCoordinates()).stream().filter(
                     u -> u.getName().equals(at.getType().getName())
                    ).collect(Collectors.toList()).forEach(obj -> group.add(obj));
                }
                if (group.getMembers().isEmpty())
                    continue;
                Unit leader = group.getMembers().stream().sorted(SortMaster.getObjSorterByExpression(
                 unit -> unit.getIntParam(PARAMS.POWER)
                )).findFirst().get();
                group.setLeader(leader);
                try {
                    group.setArg(getArg(group.getType(), block, leader));
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
                groups.add(group);
            }
        }
//        if (!CoreEngine.isIggDemo())
        if (game.getDungeonMaster().getDungeonLevel().isPregen()) {
            for (GroupAI group : groups) {
                Coordinates c=group.getBlock().getCenterCoordinate();
                for (Unit member : group.getMembers()) {
                    if (!DC_Game.game.getRules().getStackingRule().canBeMovedOnto(member, c)) {
                        // TODO tactics?
                        c = Positioner.adjustCoordinate(member, c, FacingMaster.getRandomFacing()); // direction
                        // preference?
                    }
                    main.system.auxiliary.log.LogMaster.important( member+ " coordinate adjusted from " +
                            member.getCoordinates() +
                            " to " +c );
                    member.setCoordinates(c);
                }

            }
        }
    }

    private Object getArg(UNIT_GROUP_TYPE type, LevelBlock block, Unit leader) {
        switch (type) {
            case BOSS:
            case GUARDS:
                List<Coordinates> sorted = block.getCoordinatesList().stream().sorted(new SortMaster<Coordinates>().getSorterByExpression_(
                 c -> -c.dst(leader.getCoordinates())
                )).collect(Collectors.toList());

                for (Coordinates coordinates : sorted) {
                    DC_Cell cell = game.getCellByCoordinate(coordinates);
                    switch (block.getTileMap().getMap().get(coordinates)) {
                        case DOOR:
                        case CONTAINER:
                            return cell;
                    }
                }
                DIRECTION d = leader.getFacing().getDirection();
                DC_Cell cell = game.getCellByCoordinate(leader.getCoordinates().getAdjacentCoordinate(
                 d).getAdjacentCoordinate(
                 d));
                if (cell != null) {
                    return cell;
                }
                cell = game.getCellByCoordinate(leader.getCoordinates().getAdjacentCoordinate(d));
                if (cell != null) {
                    return cell;
                }
        }
        return null;
    }

    private void resetGroups() {
        if (isAutoGroups()) {
            if (groups == null)
                initGroups();
//            if (isOnlyLargeGroups())
//                return;
        }
        //by proximity... not all mobs will be part of a group

        //        if (groups!=null ){
        //            if (!groups.isEmpty()) {
        //            }
        //        }
        if (groups == null)
            groups = new ArrayList<>();

        for (Object sub : game.getBattleMaster().getPlayerManager().getPlayers()) {
            DC_Player player = (DC_Player) sub;
            if (player.isMe()) {
                continue;
            }
            for (Unit unit : player.collectControlledUnits_()) {
                //if (unit.getAI().getGroupAI()!=null )
                //    continue;


                GroupAI group = unit.getAI().getGroup();
                if (group == null)
                    group = new GroupAI(unit);
                for (Unit unit1 : player.collectControlledUnits_()) {
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

        String report = ">>>>>>>>> " +
                groups.size() +
                " AI groups created: \n";
        report+= "" + groups.stream().filter(g->g.getMembers().size()>1).count() +
                " (non-singletons)\n";
        StringBuilder reportBuilder = new StringBuilder(report);
        for (GroupAI group : groups) {
            reportBuilder.append(group).append("\n");
        }
        report = reportBuilder.toString();
        int checkNumber =   groups.stream().mapToInt(group -> group.getMembers().size()).sum();
        if (checkNumber!= game.getPlayer(false).collectControlledUnits_().size()){
            main.system.auxiliary.log.LogMaster.log(1,">>>> AI GROUP UNIT COUNT MISMATCH!!! " );
            main.system.auxiliary.log.LogMaster.log(1,game.getPlayer(false).collectControlledUnits_().size()+
                    " VS " +checkNumber);
            // find unit who is in 2+ groups!
        }

        main.system.auxiliary.log.LogMaster.log(1," "  + report);
        if (!groups.isEmpty()) {
        }
        else {
        }
    }

    private boolean isAutoGroups() {
        return game.getMetaMaster().isRngDungeon();
    }

    public Action getDefaultAction(Unit activeUnit) {
        return getAtomicAi().getAtomicWait(activeUnit);
    }

    public boolean isDefaultAiGroupForUnitOn() {
//        return isRngDungeon()  ;
        return false;
    }
}
