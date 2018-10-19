package eidolons.game.battlecraft.ai.elements.actions.sequence;

import eidolons.entity.active.DC_ActionManager.STD_SPEC_ACTIONS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.actions.AiUnitActionMaster;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.AiLogger;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.battlecraft.ai.tools.target.TargetingMaster;
import eidolons.game.battlecraft.ai.tools.time.TimeLimitMaster;
import main.elements.targeting.Targeting;
import main.game.bf.Coordinates;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.XMap;
import main.system.math.PositionMaster;

import java.util.*;

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
        return getPathSequences(AiUnitActionMaster.getMoveActions(getUnit()), new Action(getUnit()
          .getAction("Move"), getUnit().getRef().getCopy())
         // *flee* action?
         , new ListMaster<Coordinates>().getList(game.getObjectById((Integer) arg)
          .getCoordinates()));
    }

    public ActionPath getOptimalPathSequence(UnitAI ai, Coordinates targetCell) {
        List<DC_ActiveObj> moves =
         AiUnitActionMaster.getMoveActions(ai.getUnit());
        Action action = AiActionFactory.newAction("Move", ai);
        List<Coordinates> coordinates = new ArrayList<>();
        coordinates.add(targetCell);
        List<ActionPath> paths = getPathSequences(moves, action, coordinates);
//      paths.forEach(path->{
//          path.getPriority()
//      });
        return paths.get(0);
    }

    private List<ActionPath> getPathSequences(List<DC_ActiveObj> moveActions, Action action,
                                              List<Coordinates> targetCells) {
        List<ActionPath> paths = pathCache.get(targetCells);
        if (ListMaster.isNotEmpty(paths)) {
            LogMaster.log(LOG_CHANNEL.PATHING_DEBUG, "path cache success: "
             + paths);
            return paths;
        }
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

        return paths;
    }

    public List<ActionPath> getPathSequences(List<DC_ActiveObj> moveActions, Action action) {
        Chronos.mark("getTargetCells");

        List<Coordinates> targetCells = null;
        try {
            targetCells = getTargetCells(action); // TODO replace with
            // PRIORITY_CELLS
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            LogMaster.log(1, "***Action failed to getOrCreate target cells: "
             + action);
        }
        LogMaster.log(0, Chronos.getTimeElapsedForMark("getTargetCells")
         + " time to getOrCreate valid cells for  " + action + targetCells);
        return getPathSequences(moveActions, action, targetCells);
    }

    private List<Coordinates> getTargetCells(Action targetAction) {
        if (targetAction.isDummy()) {
            return new ListMaster<Coordinates>().getList(targetAction.getTarget().getCoordinates());
        }
        if (targetAction.getActive().getName().equalsIgnoreCase(
         StringMaster.getWellFormattedString(STD_SPEC_ACTIONS.Guard_Mode.toString()))) {
            return new ListMaster<Coordinates>().toList_(targetAction.getTask().getObjArg().getCoordinates());
        }
        Boolean fastPickClosestToTargetOrSelf = null;
        if (TimeLimitMaster.isFastPickCell()) {
            if (targetAction.getActive().isRanged()) {
                fastPickClosestToTargetOrSelf = false;
            }
            if (targetAction.getActive().isMelee()) {
//                 if (targetAction.getSource().getAiType()!= AI_TYPE.SNEAK) TODO just behind!
                fastPickClosestToTargetOrSelf = true;
            }
        }

        Coordinates originalCoordinate = getUnit().getCoordinates();
        List<Coordinates> list = cellsCache.get(targetAction.getTargeting());
        if (list != null) {
            return list;
        }
        list = new ArrayList<>();
        try {
            if (fastPickClosestToTargetOrSelf != null) {
                double min = Integer.MAX_VALUE;
                DC_Obj center = fastPickClosestToTargetOrSelf ? targetAction.getTarget() :
                 targetAction.getSource();
                for (Coordinates c : center.getCoordinates()
                 .getAdjacentCoordinates()) {
                    if (!TargetingMaster.isValidTargetingCell(targetAction, c, getUnit())) // TODO
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
                Set<Coordinates> coordinatesList = null;// TODO prioritizedCells;
                if (!ListMaster.isNotEmpty(coordinatesList)) {
                    coordinatesList = getUnit().getGame().getGrid()
                     .getCoordinatesList();
                }
                // TODO FILTER THESE!!!
                // prune by distance/direction from target?
                for (Coordinates c : coordinatesList) {
                    if (!TargetingMaster.isValidTargetingCell(targetAction, c, getUnit())) {
                        continue;
                    }
                    getUnit().setCoordinates(c); // TODO causes visuals!

                    if (TargetingMaster.canBeTargeted(targetAction, true, true)) {
                        list.add(c);
                    }
                }
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            getUnit().setCoordinates(originalCoordinate);
        }
        if (list.size() > 1) {
            list = getPruneMaster().pruneTargetCells(targetAction, list);
        }
        if (getUnit().getUnitAI().getLogLevel() > AiLogger.LOG_LEVEL_BASIC) {
            LogMaster.log(LOG_CHANNEL.AI_DEBUG, "***" + targetAction
             + " has target cells for PB: " + list);
        }
        cellsCache.put(targetAction.getTargeting(), list);
        return list;
    }

    private List<ActionPath> filterPaths(Action action, List<ActionPath> paths) {
        // TODO by priority?
        return paths;
    }

}
