package eidolons.game.battlecraft.ai.elements.actions.sequence;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.actions.AiUnitActionMaster;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.AiLogger;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.battlecraft.ai.tools.target.TargetingMaster;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.elements.targeting.Targeting;
import main.game.bf.Coordinates;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.XMap;
import main.system.math.PositionMaster;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 3/3/2017.
 */
public class PathSequenceConstructor extends AiHandler {
    Map<List<Coordinates>, List<ActionPath>> pathCache = new HashMap<>();
    Map<Targeting, List<Coordinates>> cellsCache = new XMap<>();

    public PathSequenceConstructor(AiMaster master) {
        super(master);
    }

    public void clearCache() {
        pathCache.clear();
        cellsCache.clear();
    }

    public List<ActionPath> getRetreatPaths(Object arg) {
        return getPathSequences(AiUnitActionMaster.getMoveActions(getUnit()), new AiAction(getUnit()
                        .getAction("Move"), getUnit().getRef().getCopy())
                // *flee* action?
                , new ListMaster<Coordinates>().getList(game.getObjectById((Integer) arg)
                        .getCoordinates()));
    }

    public ActionPath getOptimalPathSequence(UnitAI ai, Coordinates targetCell) {
        List<ActiveObj> moves =
                AiUnitActionMaster.getMoveActions(ai.getUnit());
        AiAction aiAction = AiActionFactory.newAction("Move", ai);
        List<Coordinates> coordinates = new ArrayList<>();
        coordinates.add(targetCell);
        List<ActionPath> paths = getPathSequences(moves, aiAction, coordinates);
        //      paths.forEach(path->{
        //          path.getPriority()
        //      });
        return paths.get(0);
    }

    private List<ActionPath> getPathSequences(List<ActiveObj> moveActions, AiAction aiAction,
                                              List<Coordinates> targetCells) {
        List<ActionPath> paths = pathCache.get(targetCells);
        if (isPathCacheOn() && ListMaster.isNotEmpty(paths)) {
            LogMaster.log(LOG_CHANNEL.PATHING_DEBUG, "path cache success: "
                    + paths);
            return paths;
        }
        if (isStar(aiAction, targetCells)) {
            paths = getStarBuilder().build(getUnit(), targetCells);
        }
        if (!ListMaster.isNotEmpty(paths)) {
            paths = getPathBuilder().init(moveActions, aiAction).build(targetCells);
        }
        if (aiAction != null) {
            paths = filterPaths(aiAction, paths);
        }
        pathCache.put(targetCells, paths);

        return paths;
    }

    private boolean isStar(AiAction aiAction, List<Coordinates> targetCells) {
        // for (Coordinates targetCell : targetCells) {
        //     if (targetCell.dst(action.getActive().getOwnerObj().getCoordinates()) >= StarBuilder.PREF_MIN_RANGE)
        //         return true;
        // }
        return true;
    }


    private boolean isPathCacheOn() {
        return true;
    }

    public List<ActionPath> getPathSequences(List<ActiveObj> moveActions, AiAction aiAction) {
        Chronos.mark("getTargetCells");
        // TODO need multiple, by level of priority
        List<Coordinates> targetCells = getTargetCells(aiAction, true);
        List<ActionPath> pathSequences = getPathSequences(moveActions, aiAction, targetCells);
        if (pathSequences.isEmpty()) {
            targetCells = getTargetCells(aiAction, false);
            pathSequences = getPathSequences(moveActions, aiAction, targetCells);
        }
        return pathSequences;
    }

    private List<Coordinates> getTargetCells(AiAction targetAiAction, int limit, int range) {

        Coordinates enemyC = targetAiAction.getTarget().getCoordinates();

        return Arrays.stream(
                CoordinatesMaster.getInRange(enemyC, range)).filter(
                c -> canTarget(c, targetAiAction) && c!=enemyC). //close quarters?
                sorted(CoordinatesMaster.getSorter(getUnit().getCoordinates(), true)).
                limit(limit).
                collect(Collectors.toList());
    }

    private List<Coordinates> getTargetCells(AiAction targetAiAction, boolean allowFastPick) {

        if (targetAiAction.getActive().isMelee()) {
            int maxRange = 4;
            int range = targetAiAction.getActive().getRange();
            int limit = allowFastPick ? 1 : 3;
            for (int i = range; i <= range + maxRange; i++) {
                List<Coordinates> list = getTargetCells(targetAiAction, limit, range);
                if (!list.isEmpty()) {
                    return list;
                }
            }
        }


        if (targetAiAction.isDummy()) {
            return new ListMaster<Coordinates>().getList(targetAiAction.getTarget().getCoordinates());
        }
        Boolean fastPickClosestToTargetOrSelf = null;
        if (allowFastPick) {
            if (targetAiAction.getActive().isRanged()) {
                fastPickClosestToTargetOrSelf = false;
            }
            if (targetAiAction.getActive().isMelee()) {
                //                 if (targetAction.getSource().getAiType()!= AI_TYPE.SNEAK) TODO just behind!
                fastPickClosestToTargetOrSelf = true;
            }
        }

        Coordinates originalCoordinate = getUnit().getCoordinates();
        List<Coordinates> list = cellsCache.get(targetAiAction.getTargeting());
        if (list != null) {
            return list;
        }
        list = new ArrayList<>();
        try {
            if (fastPickClosestToTargetOrSelf != null) {
                double min = Integer.MAX_VALUE;
                DC_Obj center = fastPickClosestToTargetOrSelf ? targetAiAction.getTarget() :
                        targetAiAction.getSource();
                for (Coordinates c : center.getCoordinates()
                        .getAdjacentCoordinates()) {
                    if (!TargetingMaster.isValidTargetingCell(targetAiAction, c, getUnit())) {
                        continue;
                    }
                    double distance = PositionMaster.getExactDistance(c, originalCoordinate);
                    if (distance <= min) {
                        list = new ListMaster<Coordinates>().getList(c);
                        min = distance;
                    }
                }
            }
            if (list.isEmpty()) {
                Collection<Coordinates> coordinatesList = null;// TODO prioritizedCells;
                if (!ListMaster.isNotEmpty(coordinatesList)) {
                    coordinatesList = getUnit().getGame().getGrid()
                            .getCoordinatesList();
                }
                coordinatesList = getPruneMaster().pruneTargetCells(targetAiAction, coordinatesList);
                // TODO FILTER THESE!!!
                // prune by distance/direction from target?
                for (Coordinates c : coordinatesList) {
                    if (!TargetingMaster.isValidTargetingCell(targetAiAction, c, getUnit())) {
                        continue;
                    }
                    getUnit().setTempCoordinates(c);

                    if (TargetingMaster.canBeTargeted(targetAiAction, true, true)) {
                        list.add(c);
                    }
                }
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {            // watch it
            getUnit().setTempCoordinates(originalCoordinate);
        }
        if (list.size() > 1) {
            list = getPruneMaster().pruneTargetCells(targetAiAction, list);
            cellsCache.put(targetAiAction.getTargeting(), list);
            if (getUnit().getUnitAI().getLogLevel() > AiLogger.LOG_LEVEL_BASIC)
                LogMaster.log(LOG_CHANNEL.AI_DEBUG, "***" + targetAiAction
                        + " has target cells for PB: " + list);
        } else if (list.size() == 0) {
            list = new ListMaster<Coordinates>().getList(targetAiAction.getTarget().getCoordinates());
            if (getUnit().getUnitAI().getLogLevel() > AiLogger.LOG_LEVEL_BASIC)
                LogMaster.log(LOG_CHANNEL.AI_DEBUG, "***" + targetAiAction
                        + " has no target cells  ");
        }

        return list;
    }

    private boolean canTarget(Coordinates c, AiAction a) {
        return TargetingMaster.isValidTargetingCell(a, c, getUnit());
    }

    private List<ActionPath> filterPaths(AiAction aiAction, List<ActionPath> paths) {
        // TODO by priority?
        return paths;
    }


}
