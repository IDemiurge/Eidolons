package eidolons.game.battlecraft.ai.elements.generic;

import eidolons.content.PARAMS;
import eidolons.entity.obj.GridCell;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.exploration.dungeon.struct.LevelBlock;
import main.content.enums.EncounterEnums;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.SortMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AiAutoGroupHandler extends AiHandler {
    private GroupAI allyGroup;
    private GroupAI enemyGroup;

    public AiAutoGroupHandler(AiMaster master) {
        super(master);
    }

    private void createEncounterGroup(ObjType objType, AiData data) {

    }

    @Override
    public void initialize() {
        autoAssignGroups();
    }

    private void initGroups() {
        setGroups(new ArrayList<>());
        for (LevelBlock block : game.getDungeonMaster().getStructMaster().getBlocks()) {
            for (List<ObjAtCoordinate> list : block.getUnitGroups().keySet()) {
                //TODO ai Review - allow to create group for entire block!
                GroupAI group = new GroupAI();
                group.setType(block.getUnitGroups().get(list));
                group.setBlock(block);
                for (ObjAtCoordinate at : list) {
                    game.getUnitsForCoordinates(at.getCoordinates()).stream().filter(
                            u -> u.getName().equals(at.getType().getName())
                    ).collect(Collectors.toList()).forEach(group::add);
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
                getGroups().add(group);
            }
        }
    }

    private Object getArg(EncounterEnums.UNIT_GROUP_TYPE type, LevelBlock block, Unit leader) {
        switch (type) {
            case BOSS:
            case GUARDS:
                List<Coordinates> sorted = block.getCoordinatesSet().stream().sorted(new SortMaster<Coordinates>().getSorterByExpression_(
                        c -> -c.dst(leader.getCoordinates())
                )).collect(Collectors.toList());

                for (Coordinates coordinates : sorted) {
                    GridCell cell = game.getCell(coordinates);
                    switch (block.getTileMap().getMap().get(coordinates)) {
                        case DOOR:
                        case CONTAINER:
                            return cell;
                    }
                }
                GridCell cell = game.getCell(leader.getCoordinates().getAdjacentCoordinate(
                        DIRECTION.NONE));
                if (cell != null) {
                    return cell;
                }
        }
        return null;
    }

    private void updateGroups() {
        double join_distance = 1;
        double leave_distance = 5;
        for (GroupAI group : new ArrayList<>(getGroups()))
            for (Unit unit : group.getMembers()) {
                double distance = PositionMaster.getExactDistance(
                        group.getLeader().getCoordinates(),
                        unit.getCoordinates());
                if (distance > leave_distance) {
                    group.remove(unit);
                    unit.getAI().setGroupAI(new GroupAI(unit));
                    getGroups().add(unit.getAI().getGroup());
                    //wait until clear that they're unassigned?
                }
            }
        // join
        for (GroupAI group : getGroups())
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

    private void autoAssignGroups() {
        initGroups();
        if (!isAutoGroups()) {
            return;
        }
        //by proximity... not all mobs will be part of a group

        //        if (groups!=null ){
        //            if (!groups.isEmpty()) {
        //            }
        //        }
        if (getGroups() == null)
            return;

        for (Object sub : game.getMissionMaster().getPlayerManager().getPlayers()) {
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
                    double max_distance = 3.5;
                    if (PositionMaster.getExactDistance(unit1.getCoordinates(),
                            unit.getCoordinates()) >= max_distance)
                        continue;
                    if (!game.getVisionMaster().getSightMaster().getClearShotCondition().check(unit, unit1))
                        continue;
                    group.add(unit1);

                }
                if (!getGroups().contains(group))
                    getGroups().add(group);
            }


        }

        String report = ">>>>>>>>> " +
                getGroups().size() +
                " AI groups created: \n";
        report += "" + getGroups().stream().filter(g -> g.getMembers().size() > 1).count() +
                " (non-singletons)\n";
        StringBuilder reportBuilder = new StringBuilder(report);
        for (GroupAI group : getGroups()) {
            reportBuilder.append(group).append("\n");
        }
        report = reportBuilder.toString();
        int checkNumber = getGroups().stream().mapToInt(group -> group.getMembers().size()).sum();
        if (checkNumber != game.getPlayer(false).collectControlledUnits_().size()) {
            main.system.auxiliary.log.LogMaster.log(1, ">>>> AI GROUP UNIT COUNT MISMATCH!!! ");
            main.system.auxiliary.log.LogMaster.log(1, game.getPlayer(false).collectControlledUnits_().size() +
                    " VS " + checkNumber);
            // find unit who is in 2+ groups!
        }

        main.system.auxiliary.log.LogMaster.log(1, " " + report);
        if (!getGroups().isEmpty()) {
        } else {
        }
    }

    private boolean isAutoGroups() {
        return getMaster().isDefaultAiGroupForUnitOn();
    }

    public List<GroupAI> getGroups() {
        return getGroupHandler().getGroups();
    }

    public void setGroups(List<GroupAI> groups) {
        getGroupHandler().setGroups(groups);
    }
}
