package elements.exec.targeting.area;

import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import framework.field.FieldAnalyzer;
import framework.field.FieldGeometry;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static elements.content.enums.FieldConsts.*;

/**
 * Created by Alexander on 8/23/2023
 * <p>
 * custom melee adjacency! Perhaps if we do that first anyway, then we'll see how to do ... geometric adj Btw - that
 * graze random on miss may have to use Melee adjacency! E.g. if Rev is on Van cell - ?
 * <p>
 * Then there is the Close/Long Reach. >> Close can never attack over max diagonal >> Long can do so even when blocked;
 * plus reach the back-row target directly in front of them (with penalty?) Wazzup with penalties btw? Maybe we should
 * do that actually! Disadvantage - atk roll grade is 1 below (1 auto-fail) Range - a la SoC, 2|3? Using geometric diff:
 * From rear - Van=3, Front=4, Back=5, Rear=6... so there is quite a bit of space for this. What about diagonal? by
 * logic of steps: from B Flank - van=3/4, front=4/5/6, back = 5/6/7, rear=7 (assuming that we count it as 2 steps to
 * our own front)
 * <p>
 * OK, I like the idea of range via steps The idea of DISADV is also great - and perhaps it can be proportional to range
 * excess! Aye, so with range, let's let them shoot always... Auto-fails
 * <p>
 * Is there inversely, advantage? When closer than range? Hardly! So with ranged - we basically allow ALL visible
 * targets (that are not blocked), but count DISADV? For melee - close quarters gets disadv? and Long Reach - in
 * front-melee?
 * <p>
 * i: Flanked - when an ally is on enemy flank, adj front enemy has disadv on def
 * <p>
 * Geom vs melee - we do let unit attack the next front unit, but with disadv? Mind it - such stuff as Disadv has to be
 * precalced to show tooltip!
 */
public class MeleeTargeter {
    public static Condition getCloseQuartersCondition() {
        return new Condition() {
            @Override
            public boolean check(EntityRef ref) {
                return false;
            }
        };
    }

    public static Condition getMeleeCondition() {
        return null;
    }

    //is targeting reversible? For rear/front pair e.g., does it work the same both ways?
    // i: start with full list for each cell (preset), then remove on if's()
    public static boolean checkTarget(Cell srcCell, Cell targetCell, boolean ally, boolean closeQuarters, boolean longReach) {
        Set<Cell> set = getCellSet(srcCell, ally, closeQuarters, longReach);
        if (set.contains(targetCell)) {
            return true;
        }
        return false;
    }

    public static Set<Cell> getCellSet(Cell srcCell, boolean ally, boolean closeQuarters, boolean longReach) {
        Set<Cell> set = getBaseSet(srcCell);
        if (longReach) {
            set.add(CellSets.rear(!ally));
        }
        //what about flanks being attackable in melee?
        //RULE: adj enemy flank - only from van or another flank | your own flank - by adjacency (reverse for from-flank?)
        //then we can check if melee could target US from that flank and add?

        if (!srcCell.isFlank())
            set.addAll(getReverseFlanks(srcCell, ally, closeQuarters, longReach));
        else {
            //TODO special logic for flank vs flank
        }
        //VAN:
        // set.addAll(getReverseVans(srcCell, ally, closeQuarters, longReach));


        //to consider: (!) OBSTRUCTIVE creatures like Amalgam - how to impl additional obstruction? For ranged only?
        // remove based on field being OCCUPIED
        return removeBlockedCells(set, srcCell, ally, closeQuarters, longReach);

    }

    //++cache - or can it depend on ref?
    private static Set<Cell> removeBlockedCells(Set<Cell> set, Cell srcCell,
                                                boolean ally, boolean closeQuarters, boolean longReach) {
        // ----- REAR: 1/5 -----
        if (srcCell.isRear()) {
            if (!longReach) {
                retain(set, CellSets.back(ally));
            } else {
                remove(set, CellSets.back(!ally));
            }
        } else if (srcCell.isFlank()) {
            // ----- FLANK: 2/5 -----
            if (!FieldAnalyzer.isDuoClosestToFlankFree(ally, srcCell.y < 0)) {
                retain(set, FieldAnalyzer.getClosestToFlank(ally, srcCell.y < 0));
            }
        } else if (srcCell.isBack()) {
            // ----- BACK: 3/5 -----
            /* for back, our own front can block | all enemy area */
            if (!FieldAnalyzer.isFirstRowFree(ally)) {
                retain(set, CellSets.area(ally));
            }
        } else {
            /* for front/van, enemy front can block | enemy back */
            boolean removeFarthestBack = false;
            if (!FieldAnalyzer.isFirstRowFree(!ally)) {
                remove(set, CellSets.back(ally));
            } else {
                removeFarthestBack = true;
            }
            if (srcCell.isVan()) {
                // ----- VAN: 4/5 -----
                //TODO what can block here at all? other van being enemy?
            } else {
                // ----- FRONT: 5/5 -----
                /* for front, enemies in same row can block | enemy farthest target */
                if (srcCell.y != FieldGeometry.MIDDLE_Y)
                    if (!longReach) {
                        if (removeFarthestBack) {
                            if (closeQuarters || !FieldAnalyzer.isAllButFarthestFree(false, srcCell.y, !ally)) {
                                remove(set, FieldAnalyzer.getFarthest(false, srcCell.y, !ally));
                            }
                        }
                        if (closeQuarters || !FieldAnalyzer.isAllButFarthestFree(true, srcCell.y, !ally)) {
                            remove(set, FieldAnalyzer.getFarthest(true, srcCell.y, !ally));
                        }

                    }
            }
        }

        return set;
    }

    private static void retain(Set<Cell> set, Cell... cells) {
        retain(set, CellSets.toSet(cells));
    }

    private static void retain(Set<Cell> set, Set<Cell> cells) {
        set.removeIf(cell -> !cells.contains(cell));
    }

    private static void remove(Set<Cell> set, Cell... cells) {
        for (Cell cell : cells) {
            set.remove(cell);
        }
    }

    //TODO Q: INCLUDE ALLY CELLS?
    private static Set<Cell> getBaseSet(Cell srcCell) {
        Set<Cell> set = switch (srcCell) {
            // case Back_Player_1, Back_Enemy_1: yield CellSets.melee_back_1;
            // case Back_Player_2, Back_Enemy_2: yield CellSets.melee_back_2;
            // case Back_Player_3, Back_Enemy_3: yield CellSets.melee_back_3;
            // case Front_Player_1, Front_Enemy_1: yield CellSets.melee_front_1;
            // case Front_Player_2, Front_Enemy_2: yield CellSets.melee_front_2;
            // case Front_Player_3, Front_Enemy_3: yield CellSets.melee_front_3;
            default:
                yield CellSets.mainAreaSet;
        };
        Set<Cell> copy = new LinkedHashSet<>();
        copy.addAll(set);
        if (srcCell.isEnemyZone()) {
            // return Transformer.flip(set);
        }
        return copy;
    }


    private static Set<Cell> getReverseFlanks(Cell srcCell, boolean ally, boolean closeQuarters, boolean longReach) {
        Set<Cell> set = new HashSet<>();
        for (Cell flank : CellSets.flanks) {
            if (checkTarget(flank, srcCell, ally, closeQuarters, longReach)) {
                set.add(flank);
            }
        }
        return set;
    }
}
