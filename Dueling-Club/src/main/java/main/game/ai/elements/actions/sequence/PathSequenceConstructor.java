package main.game.ai.elements.actions.sequence;

import main.entity.active.DC_ActiveObj;
import main.game.ai.UnitAI;
import main.game.ai.elements.actions.Action;
import main.game.ai.elements.actions.AiUnitActionMaster;
import main.game.ai.elements.generic.AiHandler;
import main.game.ai.tools.path.ActionPath;
import main.game.ai.tools.prune.PruneMaster;
import main.game.ai.tools.target.TargetingMaster;
import main.game.ai.tools.time.TimeLimitMaster;
import main.game.battlefield.Coordinates;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNELS;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 3/3/2017.
 */
public class PathSequenceConstructor extends AiHandler {
    Map<List<Coordinates>, List<ActionPath>> pathCache = new HashMap<>();

    public PathSequenceConstructor(AiHandler master) {
        super(master);
    }

    public void clearCache() {
        pathCache.clear();

    }

    public List<ActionPath> getRetreatPaths(Object arg) {
        return getPathSequences(AiUnitActionMaster.getMoveActions(unit), new Action(unit
                        .getAction("Move"), unit.getRef().getCopy())
                // *flee* action?
                , new ListMaster<Coordinates>().getList(game.getObjectById((Integer) arg)
                        .getCoordinates()));
    }

    private List<ActionPath> getPathSequences(List<DC_ActiveObj> moveActions, Action action,
                                              List<Coordinates> targetCells) {
        List<ActionPath> paths = pathCache.get(targetCells);
        if (!pathCache.containsKey(paths)) {
            // Set<Path> set = new HashSet<>();
            // for (Coordinates c : targetCells) {
            // paths = CellPrioritizer.getPathMap().getOrCreate(c);
            // if (paths != null)
            // set.addAll(paths);
            // }
            // if (!set.isEmpty())
            //
            // paths = new ArrayList<>(set);
            //
            // else
            paths = getPathBuilder().init(moveActions, action).build(targetCells);
            if (action != null) {
                paths = filterPaths(action, paths);
            }
            pathCache.put(targetCells, paths);

        } else {
            LogMaster.log(LOG_CHANNELS.PATHING_DEBUG, "path cache success: "
                    + paths);
            return paths;
        }
        return paths;
    }

    List<ActionPath> getPathSequences(List<DC_ActiveObj> moveActions, Action action) {
        Chronos.mark("getTargetCells");

        List<Coordinates> targetCells = null;
        try {
            targetCells = getTargetCells(action); // TODO replace with
            // PRIORITY_CELLS
        } catch (Exception e) {
            e.printStackTrace();
            LogMaster.log(1, "***Action failed to getOrCreate target cells: "
                    + action);
        }
        LogMaster.log(1, Chronos.getTimeElapsedForMark("getTargetCells")
                + " time to getOrCreate valid cells for  " + action + targetCells);
        return getPathSequences(moveActions, action, targetCells);
    }

    private List<Coordinates> getTargetCells(Action targetAction) {
        if (targetAction.isDummy()) {
            return new ListMaster<Coordinates>().getList(targetAction.getTarget().getCoordinates());
        }

        boolean fastPickClosest = false;
        if (TimeLimitMaster.isFastPickMeleeCell()) {
            if (targetAction.getActive().isMelee()) {
                // if (targetAction.getSource().getAiType()!=AI_TYPE.SNEAK)
                fastPickClosest = true;
            }
        }

        Coordinates originalCoordinate = unit.getCoordinates();
        List<Coordinates> list = new ArrayList<>();
        try {
            if (fastPickClosest) {
                double min = Integer.MAX_VALUE;
                for (Coordinates c : targetAction.getTarget().getCoordinates()
                        .getAdjacentCoordinates()) {
                    if (!TargetingMaster.isValidTargetingCell(targetAction, c, unit)) // TODO
                    {
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
                List<Coordinates> coordinatesList = null;// TODO prioritizedCells;
                if (!ListMaster.isNotEmpty(coordinatesList)) {
                    coordinatesList = unit.getGame().getBattleField().getGrid()
                            .getCoordinatesList();
                }
                // TODO FILTER THESE!!!
                // prune by distance/direction from target?
                for (Coordinates c : coordinatesList) {
                    if (!TargetingMaster.isValidTargetingCell(targetAction, c, unit)) {
                        continue;
                    }
                    unit.setCoordinates(c); // TODO causes visuals!

                    if (TargetingMaster.canBeTargeted(targetAction)) {
                        list.add(c);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            unit.setCoordinates(originalCoordinate);
        }
        if (list.size() > 1) {
            list = PruneMaster.pruneTargetCells(targetAction, list);
        }
        if (unit.getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_BASIC) {
            LogMaster.log(LOG_CHANNELS.AI_DEBUG, "***" + targetAction
                    + " has target cells for PB: " + list);
        }
        return list;
    }

    private List<ActionPath> filterPaths(Action action, List<ActionPath> paths) {
        // TODO by priority?
        return paths;
    }

}
