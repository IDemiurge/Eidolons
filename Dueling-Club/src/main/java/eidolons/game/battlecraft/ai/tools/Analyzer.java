package eidolons.game.battlecraft.ai.tools;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.ability.conditions.WaitingFilterCondition;
import eidolons.ability.effects.oneshot.unit.RaiseEffect;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.rules.VisionEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.data.XList;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.battlecraft.rules.action.StackingRule;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import eidolons.game.core.Eidolons;
import main.game.logic.battle.player.Player;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Analyzer extends AiHandler {
    public Analyzer(AiMaster master) {
        super(master);
    }

    public static List<DC_Cell> getProtectCells(UnitAI ai) {
        List<Unit> list = getAllies(ai);
        List<DC_Cell> cells = list.stream().map(unit -> unit.getGame().getCellByCoordinate(unit.getCoordinates())).collect(Collectors.toList());
        //TODO filter
        return cells;
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
            if (!ExplorationMaster.isExplorationOn())
                if (unit.getAI().isOutsideCombat()) {
                    continue;
                }
            list.add(unit);
        }

        return list;
    }

    public static List<Unit> getWaitUnits(UnitAI ai) {
        List<Unit> list = new XList<>();
        for (Unit unit : Eidolons.game.getUnits()) {
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
        Boolean unconscious = isTargetingUnconscious(ai);
        List<Unit> enemies = getUnits(ai, false, true, true, false, false, unconscious);
        if (enemies.isEmpty())
            if (unconscious != null) {
                if (!unconscious)
                    enemies = getUnits(ai, false, true, true, false, false, true);
            }
        return enemies;
    }

    private static Boolean isTargetingUnconscious(UnitAI ai) {
        return ai.checkMod(AI_MODIFIERS.CRUEL);
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


    public static boolean hasSpecialActions(Unit unit) {
        DequeImpl<DC_UnitAction> actions = ((unit.getActionMap().get(
         ACTION_TYPE.SPECIAL_ACTION)));
        if (!ListMaster.isNotEmpty(actions))
            return false;
        for (DC_UnitAction sub : actions) {
            if (sub.getActionGroup() == ACTION_TYPE_GROUPS.SPECIAL) {
                return true;
            }
        }
//        for (STD_SPEC_ACTIONS action : DC_ActionManager.STD_SPEC_ACTIONS.values()) {
//            actions.remove(unit.getAction(
//             StringMaster.getWellFormattedString(
//              action.toString())));
//        }
//        if (!ListMaster.isNotEmpty(actions))
//            return false;
        return false;
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
        if (target.getRangedWeapon() != null) {
            return true;
        }
        if (canCast(target)) {
            return true;
        }
        return hasAnySpecialActions(target);

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
        return !StackingRule.checkCanPlace(c, unit, null);

    }

    public static List<DC_Cell> getLastSeenEnemyCells(UnitAI ai) {
        List<DC_Cell> list = new ArrayList<>();
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

    private static List<Unit> getUnits(Unit unit, Boolean enemy_or_ally_only,
                                       boolean checkAct, boolean checkAttack,
                                       Collection<Coordinates> coordinatesToCheck) {

        List<Unit> list = new ArrayList<>();
        for (Unit sub : unit.getGame().getMaster().getUnitsArray()) {
            if (coordinatesToCheck == null) {
                list.add(sub);
            } else
                for (Coordinates coordinates : coordinatesToCheck)
                    if (sub.getCoordinates().equals(coordinates))
                        list.add(sub);
        }
//        for (Coordinates coordinates : coordinatesToCheck)
//            for (Unit obj : unit.getGame().getUnitsForCoordinates(coordinates)) {
//                if (obj == null) {
//                    continue;
//                }
//                list.add(obj);
//            }
        if (enemy_or_ally_only != null) {
            if (enemy_or_ally_only)
                list.removeIf(e -> e.getOwner().equals(unit.getOwner()));
            if (!enemy_or_ally_only)
                list.removeIf(e -> !e.getOwner().equals(unit.getOwner()));
        }
        if (checkAct)
            list.removeIf(e -> !e.canActNow());
        if (checkAttack)
            list.removeIf(e -> !e.canAttack());
        list.removeIf(u -> u.isDead());
        if (!ExplorationMaster.isExplorationOn())
            list.removeIf(u -> u.getAI().isOutsideCombat());
        return list;
    }

    public static List<Unit> getUnits(UnitAI ai, Boolean ally,
                                      Boolean enemy, Boolean vision_no_vision, Boolean dead,
                                      Boolean neutral, Boolean unconscious) {
//getCache()
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
                if (!VisionManager.checkVisible(unit, true)) {
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

    public static List<Unit> getUnits(Unit unit,
                                      Boolean enemy_or_ally_only, boolean checkAct,
                                      boolean checkAttack, boolean adjacent) {
        Collection<Coordinates> coordinatesToCheck = adjacent ? unit.getCoordinates()
         .getAdjacentCoordinates() : null; //unit.getGame().getCoordinates();
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
        // List<DC_Cell> remove = new ArrayList<>();
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
        List<DC_Cell> list = new ArrayList<>();
        for (Obj obj : targetUnit.getGame().getCells()) {
            DC_Cell cell = (DC_Cell) obj;

            if (adjacent) {
                if (!obj.getCoordinates().isAdjacent(
                 targetUnit.getCoordinates())) {
                    continue;
                }
            }

            if (free) {
                if (!StackingRule.checkCanPlace(cell.getCoordinates(), targetUnit, null))
                    continue;
//                Unit unit = targetUnit.getGame().getUnitByCoordinate(
//                 cell.getCoordinates());
//                if (unit != null) {
//                    if (VisionManager.checkVisible(unit)) {
//                        continue;
//                    }
//                }
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
//        List<DC_Obj> list = new ArrayList<>();
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
        List<DC_Obj> list = new ArrayList<>();
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
        cells.removeIf(cell -> {
            boolean result = true;
            if (ai.getUnit().getGame().getObjectByCoordinate(cell.getCoordinates()) instanceof
             BattleFieldObject) {
                if (
                 ((BattleFieldObject)
                  ai.getUnit().getGame().getObjectByCoordinate(cell.getCoordinates())).isWall())
                    result = false;
            }
            return result;
        });
        return cells;
    }

    private static List<DC_Cell> getCorpseCells(Unit unit) {

        return unit.getGame().getCellsForCoordinates(
         unit.getGame().getGraveyardManager().getCorpseCells());
    }

    public static List<? extends DC_Obj> getZoneDamageCells(Unit unit) {
        List<DC_Cell> cells = getCells(unit.getUnitAI(), false, false, true);
        List<DC_Cell> remove_cells = new ArrayList<>();
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
        // List<DC_Cell> list = new ArrayList<>();
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
        List<? extends DC_Obj> list = getEnemies(unit, false, false, false);
        if (list.isEmpty()) {
            return null;
        }
        list.sort(
         SortMaster.getObjSorterByExpression(
          (t) -> PositionMaster.getDistance(t.getCoordinates(), unit.getCoordinates())));
        return (Unit) list.get(0);
    }


    public boolean isRanged(UnitAI ai) {
        if (ai.getType() == AI_TYPE.ARCHER) {
            if (ai.getUnit().getRangedWeapon() != null)
                if (ai.getUnit().getRangedWeapon().getAmmo() != null)
                    return true;
        }
        return false;
    }
}
