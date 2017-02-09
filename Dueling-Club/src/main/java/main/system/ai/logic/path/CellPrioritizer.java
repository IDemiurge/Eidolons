package main.system.ai.logic.path;

import main.content.OBJ_TYPES;
import main.entity.obj.*;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.system.ai.UnitAI;
import main.system.ai.logic.actions.Action;
import main.system.ai.logic.actions.ActionSequence;
import main.system.ai.logic.priority.PriorityManager;
import main.system.ai.tools.Analyzer;
import main.system.auxiliary.ListMaster;

import java.util.*;

public class CellPrioritizer {
    /*
     * precalculate priority for cells and prune paths by destination!
     *
     * alternatively, I could prioritize path's cells
     */
    public static final int prune_threshold = 2; // increase if there aren't too
    // many actions?

    protected static Map<Obj, Integer> cellPriorityMap;
    protected static Map<Obj, Integer> enemyPriorityMap;

    private static Action targetAction;

    private static List<DC_ActiveObj> moves;

    private static Map<Coordinates, List<ActionPath>> pathMap;

    public static int getMeleePriorityForCell(DC_HeroObj unit, Obj cell) {
        return getPriorityForCell(unit, cell, null);
    }

    public static List<? extends DC_Obj> getApproachCells(UnitAI ai) {
        /*
		 * TODO
		 * 
		 * figure out direction and choose an adjacent cell?
		 * 
		 * how to deal with special actions users? e.g. shadow step, short
		 * flight... logically, those should be used to *close in* so maybe I
		 * can make some kind of check?
		 */

        return ai
                .getUnit()
                .getGame()
                .getCellsForCoordinates(
                        ai.getUnit().getCoordinates().getAdjacentCoordinates());
    }

    /*
     * Perhaps one could feed an action to this method!.. Or actions...
     */
    public static int getPriorityForCell(DC_HeroObj unit, Obj cell,
                                         DC_ActiveObj targetAcsdftion) {
		/*
         * getOrCreate attack priority for each adjacent enemy...
		 */

		/*
		 * I could exclude paths for zone spells, let the range be the limit for
		 * now
		 */
        int priority = 0;
        List<ActionPath> paths = pathMap.get(cell.getCoordinates());
        if (paths == null) {
            paths = new PathBuilder(moves, targetAction)
                    .build(new ListMaster<Coordinates>().getList(cell
                            .getCoordinates()));
            pathMap.put(cell.getCoordinates(), paths);
        }
        if (!ListMaster.isNotEmpty(paths)) {
            return 0;
        }
        int path_priority = paths.get(0).getPriority();
        // adjacent is only for melee... I should have getEnemies(action)
        // instead!

		/*
		 * how plausible is this method? will it spare the time used up for
		 * pathbuilding?
		 */

        for (Coordinates c : cell.getCoordinates().getAdjacentCoordinates()) {
            Obj targetObj = unit.getGame().getObjectByCoordinate(c, false);
            if (targetObj != null) {
                if (targetObj.getOBJ_TYPE_ENUM() != OBJ_TYPES.BF_OBJ) {
                    if (Analyzer.isEnemy(targetObj, unit)) {
                        Integer cell_priority = enemyPriorityMap.get(targetObj);
                        if (cell_priority != null) {
                            priority += cell_priority;
                            continue;
                        }
                        DC_HeroObj enemy = (DC_HeroObj) targetObj;
                        cell_priority = PriorityManager
                                .getUnitPriority(targetObj);
                        cell_priority -= PriorityManager.getMeleeThreat(enemy); // "now"?
                        // should all AI-units *be afraid*? :) Maybe memory map
                        // will do nicely here?
                        // if ()
                        Action action = new Action(unit.getAction("attack"),
                                enemy);
                        // if (melee )

                        // PriorityManager.getDamagePriority(action, targetObj,
                        // false);
                        priority += PriorityManager
                                .getAttackPriority(new ActionSequence(action));
                        DC_UnitAction offhand_attack = unit
                                .getAction("offhand attack");
                        // TODO find the best attack action versus target and
                        // use its priority?
                        if (offhand_attack != null) {
                            action = new Action(offhand_attack, enemy);
                            priority += PriorityManager
                                    .getAttackPriority(new ActionSequence(
                                            action));
                        }
                        // do we calculate move costs before or after?

                        enemyPriorityMap.put(targetObj, cell_priority);

                        priority += cell_priority;
                    }
                }
            }
        }
        priority += priority * path_priority / 100;
        return priority;
    }

    public static void reset() {
        cellPriorityMap = new HashMap<Obj, Integer>();
        enemyPriorityMap = new HashMap<Obj, Integer>();
        pathMap = new HashMap<>();
    }

    public static List<Coordinates> getMeleePriorityCellsForUnit(UnitAI ai) {
        PriorityManager.setUnit_ai(ai);

        List<Coordinates> list = new LinkedList<>();
        List<Obj> cells = new ArrayList<>(50);
        int max_priority = Integer.MIN_VALUE;
        DC_Cell priority_cell = null;
        for (DC_HeroObj enemy : Analyzer.getVisibleEnemies(ai)) {
            for (Coordinates c : enemy.getCoordinates()
                    .getAdjacentCoordinates()) {
                if (!cells.contains(c)) {
                    DC_Cell cell = enemy.getGame().getCellByCoordinate(c);
                    int priority = getMeleePriorityForCell(ai.getUnit(), cell);
                    if (priority > max_priority) // threshold maybe
                    {
                        max_priority = priority;
                        // list.add(cell.getCoordinates());
                        priority_cell = cell;
                    }

                    // cells.add(enemy.getGame().getCellByCoordinate(c));
                }
            }
        }
        list.add(priority_cell.getCoordinates());
        // Collections.sort(cells, getPrioritySorter(ai.getUnit()));
        //
        // for (int i = 0; i < prune_threshold; i++)
        // list.add(cells.getOrCreate(i).getCoordinates());
        main.system.auxiliary.LogMaster.log(1, "Prioritized cells: " + list);
        return list;
    }

    private static Comparator<? super Obj> getPrioritySorter(
            final DC_HeroObj unit) {
        return new Comparator<Obj>() {

            public int compare(Obj o1, Obj o2) {
                Integer priority = cellPriorityMap.get(o1);
                if (priority == null) {
                    priority = getMeleePriorityForCell(unit, o1);
                    cellPriorityMap.put(o1, priority);
                }
                Integer priority2 = cellPriorityMap.get(o2);
                if (priority2 == null) {
                    priority2 = getMeleePriorityForCell(unit, o2);
                    cellPriorityMap.put(o2, priority2);
                }
                if (priority > priority2) {
                    return -1;
                }
                if (priority < priority2) {
                    return 1;
                }
                return 0;
            }
        };
    }

    public static List<Coordinates> getPriorityCellsForUnit(UnitAI unitAI,
                                                            List<DC_ActiveObj> moveActions, Action action) {
        moves = moveActions;
        targetAction = action;
        return getMeleePriorityCellsForUnit(unitAI);
    }

    public static Map<Coordinates, List<ActionPath>> getPathMap() {
        return pathMap;
    }

}
