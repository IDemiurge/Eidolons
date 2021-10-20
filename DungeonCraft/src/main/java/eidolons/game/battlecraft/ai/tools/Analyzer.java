package eidolons.game.battlecraft.ai.tools;

import eidolons.ability.conditions.WaitingFilterCondition;
import eidolons.ability.effects.oneshot.unit.RaiseEffect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.active.Spell;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.rules.action.StackingRule;
import eidolons.game.core.Core;
import eidolons.game.core.master.EffectMaster;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.data.XList;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.logic.battle.player.Player;
import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Analyzer extends AiHandler {
    public Analyzer(AiMaster master) {
        super(master);
    }

    public static List<DC_Cell> getProtectCells(UnitAI ai) {
        List<Unit> list = getAllies(ai);
        //TODO filter
        return list.stream().map(unit -> unit.getGame().getCell(unit.getCoordinates())).collect(Collectors.toList());
    }

    public static List<Unit> getAllies(UnitAI ai) {
        List<Unit> list = new XList<>();
        for (Unit unit : Core.game.getUnits()) {
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
        for (Unit unit : Core.game.getUnits()) {
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
        if (ai.getGroup().getBehavior() == UnitAI.AI_BEHAVIOR_MODE.AGGRO
        || AI_Manager.isAiVisionHack()
        ) {
            return (List<Unit>) getEnemies(ai.getUnit(), false, false, false);
        }
        Boolean unconscious = isTargetingUnconscious(ai);
        Boolean visionRequired = !EidolonsGame.BOSS_FIGHT;
        List<Unit> enemies = getUnits(ai, false, true, visionRequired, false, false, unconscious);
        if (enemies.isEmpty())
            if (unconscious != null) {
                if (!unconscious)
                    enemies = getUnits(ai, false, true, true, false, false, true);
            }
        if (enemies.isEmpty())
            if (ai.getUnit().isMine()) {
                if (!ai.getUnit().isPlayerCharacter()) { //TODO IGG HACK
                    enemies = getVisibleEnemies(Core.getMainHero().getAI());
                }
            }

        return enemies;
    }

    private static Boolean isTargetingUnconscious(UnitAI ai) {
        return true;// ai.checkMod(AI_MODIFIERS.CRUEL);
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
            return !unit.getQuickItems().isEmpty();
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
        for (Spell s : target.getSpells()) {
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

    public static boolean isBlockingMovement(Unit unit, BattleFieldObject target) {
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
                list.add(obj.getGame().getCell(
                        coordinates));
            }
        }

        return list;
    }

    public static boolean isEnemy(Obj targetObj, DC_Obj source) {
        if (targetObj != null) {
            if (!targetObj.getOwner().equals(Player.NEUTRAL)) {
                if (!targetObj.getOwner().equals(source.getOwner())) {
                    return !targetObj.getOBJ_TYPE_ENUM().equals(DC_TYPE.BF_OBJ);
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
        for (Unit sub : unit.getGame().getObjMaster().getUnitsArray()) {
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
            if (enemy_or_ally_only) {
                list.removeIf(e -> e.getOwner().equals(unit.getOwner()));
                list.removeIf(e -> e.getOwner().equals(DC_Player.NEUTRAL));
            }
            if (!enemy_or_ally_only)
                list.removeIf(e -> !e.getOwner().equals(unit.getOwner()));
        }
        if (checkAct)
            list.removeIf(e -> !e.canActNow());
        if (checkAttack)
            list.removeIf(e -> !e.canAttack());
        list.removeIf(Entity::isDead);
        if (!ExplorationMaster.isExplorationOn())
            list.removeIf(u -> u.getAI().isOutsideCombat());
        return list;
    }

    public static List<Unit> getUnits(UnitAI ai, Boolean ally,
                                      Boolean enemy, Boolean vision_no_vision, Boolean dead,
                                      Boolean neutral, Boolean unconscious) {
        //getCache()
        List<Unit> list = new XList<>();
        for (Unit unit : Core.game.getUnits()) {
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
        // TODO performance dicatates adjacent only...
        return getCells(ai, true, false, true);
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
            }
            list.add(cell);

        }

        return list;
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
            if (PositionMaster.getDistance(cell, ai.getUnit()) <= 5) {
                list.add(cell);
            }
        }
        if (list.isEmpty()) {
            // change direction?
        }
        return list;
    }

    public static List<DC_Cell> getSummonCells(UnitAI ai, DC_ActiveObj action) {

        if (EffectMaster.check(action, RaiseEffect.class)) {
            return new ArrayList<>(getCorpseCells(ai.getUnit()));
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
            boolean result = false;
            if (ai.getUnit().getGame().getObjectByCoordinate(cell.getCoordinates()) instanceof
                    BattleFieldObject) {
                if (((BattleFieldObject)
                        ai.getUnit().getGame().getObjectByCoordinate(cell.getCoordinates())).isWall())
                    result = true;
            }
            return result;
        });
        return cells;
    }

    private static Set<DC_Cell> getCorpseCells(Unit unit) {
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
                .getCell(
                        new RandomWizard<Coordinates>().getRandomSetItem(ai
                                .getUnit().getCoordinates()
                                .getAdjacentCoordinates())));
        // List<DC_Cell> list = new ArrayList<>();
        //
        // DC_HeroObj unit = ai.getUnit();
        // Coordinates originalCoordinates = unit.getCoordinates();
        // for (DC_Cell cell : getCellsSet(ai, true)) {
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
                return ai.getUnit().getRangedWeapon().getAmmo() != null;
        }
        return false;
    }
}
