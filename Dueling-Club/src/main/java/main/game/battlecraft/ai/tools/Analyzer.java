package main.game.battlecraft.ai.tools;

import main.ability.conditions.WaitingFilterCondition;
import main.ability.effects.oneshot.unit.RaiseEffect;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.rules.VisionEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.data.XList;
import main.entity.active.DC_ActionManager;
import main.entity.active.DC_ActionManager.STD_SPEC_ACTIONS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import main.game.core.Eidolons;
import main.game.logic.battle.player.Player;
import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;
import main.system.math.PositionMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Analyzer extends AiHandler {
    public Analyzer(AiMaster master) {
        super(master);
    }

    public static List<Unit> getAllies(UnitAI ai) {
        List<Unit> list = new XList<>();
        for (Unit unit : Eidolons.game.getUnits()) {
            if (unit.getOwner() != ai.getUnit().getOwner()) {
                continue;
            }
            if (unit.isDead()) {
                continue;
            }
            list.add(unit);
        }

        return list;
    }

    public static List<Unit> getWaitUnits(UnitAI ai) {
        List<Unit> list = new XList<>();
        for (Unit unit :Eidolons.game.getUnits()) {
            if (unit.equals(ai.getUnit())) {
                continue;
            }
            if (!WaitingFilterCondition.canBeWaitedUpon(ai.getUnit(), unit)) {
                continue;
            }
            list.add(unit);
        }

        return list;
    }

    public static List<Unit> getVisibleEnemies(UnitAI ai) {
        return getUnits(ai, false, true, true, false);
    }

    public static List<Unit> getUnits(UnitAI ai, Boolean ally,
                                      Boolean enemy, Boolean vision_no_vision, Boolean dead) {
        return getUnits(ai, ally, enemy, vision_no_vision, dead, false);
    }
    public static List<Unit> getUnits(UnitAI ai, Boolean ally,
                                      Boolean enemy, Boolean vision_no_vision, Boolean dead,
                                      Boolean neutral) {
        return getUnits(ai, ally, enemy, vision_no_vision, dead, neutral, false);
    }

    public static List<Unit> getUnits(UnitAI ai, Boolean ally,
                                      Boolean enemy, Boolean vision_no_vision, Boolean dead,
                                      Boolean neutral, Boolean unconscious) {

        List<Unit> list = new XList<>();
        for (Unit unit : Eidolons.game.getUnits()) {
            if (unit.getZ() != ai.getUnit().getZ())// TODO
            {
                continue;
            }
            if (!enemy) {
                if (unit.getOwner() != ai.getUnit().getOwner()) {
                    continue;
                }
            }
            if (!ally) {
                if (unit.getOwner() == ai.getUnit().getOwner()) {
                    continue;
                }
            }
            if (!neutral) {
                if (unit.getOwner() == Player.NEUTRAL || unit.isBfObj()) {
                    continue;
                }
            }
            if (vision_no_vision) {
                if (!VisionManager.checkVisible(unit)) {
                    continue;
                }
            }
            if (!dead) {
                if (unit.isDead()) {
                    continue;
                }
            }
            if (!unconscious) {
                if (unit.isUnconscious()) {
                    continue;
                }
            }
            list.add(unit);
        }

        return list;
    }

    public static boolean hasSpecialActions(Unit unit) {
        DequeImpl<DC_UnitAction> actions =     new DequeImpl<>((unit.getActionMap().get(
         ACTION_TYPE.SPECIAL_ACTION)));
        if (!ListMaster.isNotEmpty(actions))
            return false;
        for (STD_SPEC_ACTIONS action : DC_ActionManager.STD_SPEC_ACTIONS.values()){
            actions.remove(unit.getAction(
              StringMaster.getWellFormattedString(
             action.toString())));
        }
        if (!ListMaster.isNotEmpty(actions))
            return false;
        return true;
    }

        public static boolean hasAnySpecialActions(Unit unit) {
        if (hasSpells(unit)) {
            return true;
        }
        if (hasQuickItems(unit)) {
                return true;
        }
            return hasSpecialActions(unit);
    }

    public static boolean hasQuickItems(Unit unit) {
        if (unit.getQuickItems() != null) {
            if (!unit.getQuickItems().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSpells(Unit unit) {
        return !unit.getSpells().isEmpty();
    }

    public static boolean canCast(Unit target) {
        if (!hasSpells(target)) {
            return false;
        }
        for (DC_SpellObj s : target.getSpells()) {
            if (s.canBeActivated()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlocking(Unit unit, Unit target) {
        // TODO melee vs ranged

        return false;

    }

    public static boolean checkRangedThreat(Unit target) {
        if (!hasAnySpecialActions(target)) {
            return false;
        }
        return canCast(target);

    }

    public static boolean isBlockingMovement(Unit unit, Unit target) {
        if (!unit.getCoordinates().isAdjacent(target.getCoordinates())) {
            return false;
        }

        Coordinates c = unit.getCoordinates().getAdjacentCoordinate(
                unit.getFacing().getDirection());
        if (c == null) {
            return false;
        }
        return c.equals(target.getCoordinates());

    }

    public static List<DC_Cell> getLastSeenEnemyCells(UnitAI ai) {
        List<DC_Cell> list = new LinkedList<>();
        for (DC_Obj obj : ai.getUnit().getOwner().getLastSeenCache().keySet()) {
            if (isEnemy(obj, ai.getUnit())) {
                Coordinates coordinates = ai.getUnit().getOwner()
                        .getLastSeenCache().get(obj);
                list.add(obj.getGame().getCellByCoordinate(
                        coordinates));
            }
        }

        return list;
    }

    public static boolean isEnemy(Obj targetObj, DC_Obj source) {
        if (targetObj != null) {
            if (!targetObj.getOwner().equals(Player.NEUTRAL)) {
                if (!targetObj.getOwner().equals(source.getOwner())) {
                    if (!targetObj.getOBJ_TYPE_ENUM().equals(DC_TYPE.BF_OBJ)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<? extends DC_Obj> getMeleeEnemies(Unit unit) {
        return getAdjacentEnemies(unit, true);
    }

    public static List<? extends DC_Obj> getAdjacentEnemies(Unit unit,
                                                            boolean checkAttack) {
        return getAdjacentEnemies(unit, false, checkAttack);
    }

    public static List<? extends DC_Obj> getAdjacentEnemies(Unit unit,
                                                            boolean checkAct, boolean checkAttack) {
        return getEnemies(unit, checkAct, checkAttack, true);
    }

    public static List<? extends DC_Obj> getEnemies(Unit unit,
                                                    boolean checkAct, boolean
                                                            checkAttack, boolean adjacentOnly) {
        return getUnits(unit, true, checkAct, checkAttack, adjacentOnly);
    }

    private static List<Unit> getUnits(Unit unit, Boolean enemy_or_ally_only, boolean checkAct, boolean checkAttack, Collection<Coordinates> coordinatesToCheck) {
        List<Unit> list = new LinkedList<>();
        for (Coordinates coordinates : coordinatesToCheck)
            for (  Obj obj  :  unit.getGame().getUnitsForCoordinates(coordinates)) {
                if (obj == null) {
                    continue;
                }

                Unit enemy = (Unit) obj;
                if (enemy_or_ally_only != null) {
                    if (enemy_or_ally_only) {
                        if (obj.getOwner().equals(unit.getOwner())) {
                            continue;
                        }
                    } else {
                        if (!obj.getOwner().equals(unit.getOwner())) {
                            continue;
                        }
                    }
                }

                if (!checkAct) {
                    list.add(enemy);
                }
                if (enemy.canActNow()) {
                    if (!checkAttack) {
                        list.add(enemy);
                    } else if (enemy.canAttack()) {
                        list.add(enemy);
                    }
                }

            }

        return list;
    }
    public static List<Unit> getUnits(Unit unit,
                                      Boolean enemy_or_ally_only, boolean checkAct,
                                      boolean checkAttack, boolean adjacent) {
        Collection<Coordinates> coordinatesToCheck = adjacent ? unit.getCoordinates()
                .getAdjacentCoordinates() : unit.getGame().getCoordinates();
        return getUnits(unit, enemy_or_ally_only, checkAct, checkAttack, coordinatesToCheck);
    }


    public static List<DC_Cell> getSafeCells(UnitAI ai) {
        // filter by distance?
        // prune
        // to speed it up... now that I've disabled the special actions on
        // retreat...
        // I can just choose an *adjacent* cell for *Move* and make the right
        // turning sequence if necessary
        // when stuck, "Cower" mode would be nice to have :)

		/*
         * let's say, all *detected* cells that are not adjacent to enemies!
		 */
        // TODO performance dicatates adjacent only...
        return getCells(ai, true, false, true);

        // List<DC_Cell> cells = getCells(ai, false, false, true);
        // List<DC_Cell> list = new ArrayList<>();
        // int max_distance = -1;
        // loop: for (DC_Cell c : cells) {
        // for (Coordinates c1 : c.getCoordinates().getAdjacentCoordinates()) {
        // DC_HeroObj enemy = ai.getUnit().getGame()
        // .getUnitByCoordinate(c1);
        // if (!isEnemy(enemy, ai.getUnit()))
        // continue;
        // if (enemy.canAct())
        // continue loop;
        // }
        // int distance = PositionMaster.getDistance(ai.getUnit()
        // .getCoordinates(), c.getCoordinates());
        //
        // if (distance >= max_distance + 1) {
        // max_distance = distance;
        // list.add(c);
        // }
        // }
        // List<DC_Cell> remove = new LinkedList<>();
        // for (DC_Cell c : list) {
        // int distance = PositionMaster.getDistance(ai.getUnit()
        // .getCoordinates(), c.getCoordinates());
        // if (distance + 1 < max_distance) {
        // remove.add(c);
        // }
        // }
        //
        // for (DC_Cell c : remove)
        // list.remove(c);
        //
        // DIRECTION direction =
        // FacingManager.rotate180(ai.getUnit().getFacing())
        // .getDirection();
        // Coordinates behind = ai.getUnit().getCoordinates()
        // .getAdjacentCoordinate(direction);
        //
        // if (!list.contains(behind)) {
        // if (ai.getUnit().getGame().getUnitByCoordinate(behind) == null)
        // list.add(ai.getUnit().getGame().getCellByCoordinate(behind));
        // }

        // return list;

    }

    public static List<DC_Cell> getMoveTargetCells(UnitAI ai) {
        return getCells(ai, true);
    }

    public static List<DC_Cell> getCells(UnitAI ai, boolean detected) {
        return getCells(ai, false, detected, true);
    }

    public static List<DC_Cell> getCells(UnitAI ai, boolean adjacent,
                                         boolean detected, boolean free) {
        return getCells(ai.getUnit(), adjacent, detected, free);
    }

    public static List<DC_Cell> getCells(Unit targetUnit,
                                         boolean adjacent, boolean detected, boolean free) {
        List<DC_Cell> list = new LinkedList<>();
        for (Obj obj : targetUnit.getGame().getCells()) {
            DC_Cell cell = (DC_Cell) obj;

            if (adjacent) {
                if (!obj.getCoordinates().isAdjacent(
                        targetUnit.getCoordinates())) {
                    continue;
                }
            }

            if (free) {
                Unit unit = targetUnit.getGame().getUnitByCoordinate(
                        cell.getCoordinates());
                if (unit != null) {
                    if (VisionManager.checkVisible(unit)) {
                        continue;
                    }
                }
            }
            if (detected) // TODO in sight etc
            {
                if (cell.getActivePlayerVisionStatus() != VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED) {
                    continue;
                }
            }
            list.add(cell);

        }

        return list;
    }

    public static List<? extends DC_Obj> getStalkCells(UnitAI ai) {
        // getOrCreate closest enemy?
//        DC_HeroObj enemy = getClosestEnemy(ai);
//        List<DC_Obj> list = new LinkedList<>();
//        for (DC_Cell cell : getCells(ai, false, false, true)) {
//            if (PositionMaster.getDistance(cell, enemy) <= HearingRule
//                    .getSafeDistance(ai.getUnit(), enemy))
//                list.add(cell);
//        }
//        return list;
        return null;
    }

    public static List<? extends DC_Obj> getWanderCells(UnitAI ai) {
        DIRECTION d = ai.getGroup().getWanderDirection();
        // permittedCells = ai.getGroup().getWanderBlocks();
        List<DC_Obj> list = new LinkedList<>();
        for (DC_Cell cell : getCells(ai, false, false, true)) {
            if (d != null) {
                if (DirectionMaster.getRelativeDirection(cell, ai.getUnit()) != d) {
                    continue;
                }
            }
            if (PositionMaster.getDistance(cell, ai.getUnit()) <= ai
                    .getMaxWanderDistance()) {
                list.add(cell);
            }
        }
        if (list.isEmpty()) {
            // change direction?
        }
        return list;
    }

    public static List<DC_Cell> getSummonCells(UnitAI ai, DC_ActiveObj action) {

        if (EffectFinder.check(action, RaiseEffect.class)) {
            return getCorpseCells(ai.getUnit());
        }
        List<DC_Cell> cells = getCells(ai, true, true, true);
        boolean melee = true;
        // try{
        // } preCheck melee TODO
        if (melee) {
            for (Unit e : getVisibleEnemies(ai)) {
                // e.getCoordinates().getAdjacentCoordinates()
                cells.addAll(getCells(e, true, false, true));
            }
        }

        return cells;
    }

    private static List<DC_Cell> getCorpseCells(Unit unit) {

        return unit.getGame().getCellsForCoordinates(
                unit.getGame().getGraveyardManager().getCorpseCells());
    }

    public static List<? extends DC_Obj> getZoneDamageCells(Unit unit) {
        List<DC_Cell> cells = getCells(unit.getUnitAI(), false, false, true);
        List<DC_Cell> remove_cells = new LinkedList<>();
        loop:
        for (DC_Cell c : cells) {
            for (Coordinates c1 : c.getCoordinates().getAdjacentCoordinates()) {
                Unit enemy = unit.getGame().getUnitByCoordinate(c1);
                if (isEnemy(enemy, unit)) {
                    continue loop;
                }
            }
            remove_cells.add(c);
        }

        for (DC_Cell c : remove_cells) {
            cells.remove(c);
        }
        return cells;
    }

    public static List<DC_Cell> getSearchCells(UnitAI ai) {
        List<DC_Cell> cells = getLastSeenEnemyCells(ai);
        if (!cells.isEmpty()) {
            return new ListMaster<DC_Cell>()
                    .getList(new RandomWizard<DC_Cell>()
                            .getRandomListItem(cells));
        }
        return new ListMaster<DC_Cell>().getList(ai
                .getUnit()
                .getGame()
                .getCellByCoordinate(
                        new RandomWizard<Coordinates>().getRandomListItem(ai
                                .getUnit().getCoordinates()
                                .getAdjacentCoordinates())));
        // List<DC_Cell> list = new LinkedList<>();
        //
        // DC_HeroObj unit = ai.getUnit();
        // Coordinates originalCoordinates = unit.getCoordinates();
        // for (DC_Cell cell : getCells(ai, true)) {
        // unit.setCoordinates(cell.getCoordinates());
        // for (DC_Cell target_cell : cells) {
        // if (!list.contains(cell))
        // if (target_cell.checkInSightForUnit(unit))
        // list.add(cell);
        // }
        // }
        // unit.setCoordinates(originalCoordinates);
        // return list;
    }

    public int getClosestEnemyDistance(Unit unit) {
        Unit enemy = getClosestEnemy(unit);
        if (enemy == null) {
            return 999;
        }
        return PositionMaster.getDistance(enemy, unit);
    }

    public Unit getClosestEnemy(Unit unit) {
        List<? extends DC_Obj> list = getEnemies(unit, true, false, false);
        if (list.isEmpty()) {
            return null;
        }
        list.sort(
                SortMaster.getObjSorterByExpression(
                        (t) -> PositionMaster.getDistance(t.getCoordinates(), unit.getCoordinates())));
        return (Unit) list.get(0);
    }


    public boolean isRanged(UnitAI ai) {
        if (ai.getType()== AI_TYPE.ARCHER){
            if (ai.getUnit().getRangedWeapon()!=null )
                if (ai.getUnit().getRangedWeapon().getAmmo()!=null)
                    return true;
        }
        return false;
    }
}
