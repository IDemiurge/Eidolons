package main.game.battlecraft.ai.tools.path;

import main.content.DC_TYPE;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;
import main.game.battlecraft.ai.tools.Analyzer;
import main.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import main.game.bf.Coordinates;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.*;

public class CellPrioritizer extends AiHandler {
    /*
         * precalculate priority for cells and prune paths by destination!
         *
         * alternatively, I could prioritize path's cells
         */
    protected Map<Obj, Integer> cellPriorityMap;
    // many actions?
    protected Map<Obj, Integer> enemyPriorityMap;
    private Action targetAction;
    private List<DC_ActiveObj> moves;
    private Map<Coordinates, List<ActionPath>> pathMap;

    public CellPrioritizer(AiMaster master) {
        super(master);
    }

    public int getMeleePriorityForCell(Unit unit, Obj cell) {
        return getPriorityForCell(unit, cell, null);
    }

    public List<? extends DC_Obj> getApproachCells(UnitAI ai) {
        /*
         * TODO
		 * 
		 * figure out direction and choose an adjacent cell?
		 * 
		 * how to deal with special actions users? e.g. shadow step, short
		 * flight... logically, those should be used to *close in* so maybe I
		 * can make some kind of preCheck?
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
    public int getPriorityForCell(Unit unit, Obj cell,
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
            paths = getPathBuilder().init(moves, targetAction)
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
                if (targetObj.getOBJ_TYPE_ENUM() != DC_TYPE.BF_OBJ) {
                    if (Analyzer.isEnemy(targetObj, unit)) {
                        Integer cell_priority = enemyPriorityMap.get(targetObj);
                        if (cell_priority != null) {
                            priority += cell_priority;
                            continue;
                        }
                        Unit enemy = (Unit) targetObj;
                        cell_priority = DC_PriorityManager
                         .getUnitPriority(targetObj);
                        cell_priority -= DC_PriorityManager.getMeleeThreat(enemy); // "now"?
                        // should all AI-units *be afraid*? :) Maybe memory map
                        // will do nicely here?
                        // if ()
                        Action action = new Action(unit.getAction("attack"),
                         enemy);
                        // if (melee )

                        // PriorityManager.getDamagePriority(action, targetObj,
                        // false);
                        priority += DC_PriorityManager
                         .getAttackPriority(new ActionSequence(action));
                        DC_UnitAction offhand_attack = unit
                         .getAction("offhand attack");
                        // TODO find the best attack action versus target and
                        // use its priority?
                        if (offhand_attack != null) {
                            action = new Action(offhand_attack, enemy);
                            priority += DC_PriorityManager
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

    public void reset() {
        cellPriorityMap = new HashMap<>();
        enemyPriorityMap = new HashMap<>();
        pathMap = new HashMap<>();
    }

    public List<Coordinates> getMeleePriorityCellsForUnit(UnitAI ai) {
//        DC_PriorityManager.setUnitAi(ai);

        List<Coordinates> list = new ArrayList<>();
        List<Obj> cells = new ArrayList<>(50);
        int max_priority = Integer.MIN_VALUE;
        DC_Cell priority_cell = null;
        for (Unit enemy : Analyzer.getVisibleEnemies(ai)) {
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
        LogMaster.log(1, "Prioritized cells: " + list);
        return list;
    }

    private Comparator<? super Obj> getPrioritySorter(
     final Unit unit) {
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

    public List<Coordinates> getPriorityCellsForUnit(UnitAI unitAI,
                                                     List<DC_ActiveObj> moveActions, Action action) {
        moves = moveActions;
        targetAction = action;
        return getMeleePriorityCellsForUnit(unitAI);
    }

    public Map<Coordinates, List<ActionPath>> getPathMap() {
        return pathMap;
    }

}
