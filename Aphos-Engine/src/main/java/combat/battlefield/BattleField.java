package combat.battlefield;

import combat.BattleHandler;
import combat.sub.BattleManager;
import elements.exec.condition.Condition;
import elements.exec.condition.ConditionImpl;
import elements.exec.targeting.TargetGroup;
import elements.exec.targeting.Targeting;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import framework.entity.Entity;
import framework.entity.field.FieldEntity;
import framework.entity.field.Unit;
import framework.field.Environment;
import framework.field.FieldPos;
import framework.field.Visibility;

import java.util.*;
import java.util.stream.Collectors;

import static elements.content.enums.FieldConsts.*;

/**
 * Created by Alexander on 6/13/2023
 * <p>
 * Handles everything related to entities on the FIELD This includes movement?
 */
public class BattleField extends BattleHandler {
    Environment env;
    FieldPos[] cells = all;
    Map<Cell, FieldPos> cellMap = new HashMap<>();
    Map<FieldPos, Set<FieldPos>> adjacentMap = new HashMap<>();

    public BattleField(BattleManager battleManager) {
        super(battleManager);
        for (FieldPos cell : cells) {
            cellMap.put(cell.getCell(), cell);
        }
        for (FieldPos cell : cells) {
            //the best we can do is mirror some preset I guess
            Set<Cell> adjCells = getAdjacent(cell.getCell());
            Set<FieldPos> set = adjCells.stream().map(c -> cellMap.get(c)).collect(Collectors.toSet());
            adjacentMap.put(cell, set); //such that step-movement is in theory possible between these cells
        }
        // cellMap.get(FieldPos.Cell.Vanguard_Bot).getAreaPos()[0];
    }


    ///////////////// region CELLS ///////////////////

    public boolean isCellFree(FieldPos pos, boolean allVisible, ConditionImpl swap) {
        FieldEntity entity = getByPos(pos);
        if (entity == null)
            return true;
        //TODO
        // if (swap.checkEntity(entity) && entity.getTeamId() == teamId) {
        //     return true;
        // }
        if (allVisible)
            return false;

        if (entity.getEnemyVisibility() == Visibility.Hidden) {
            return true;
        }
        return false;
    }

    //endregion
    ///////////////// region MOVEMENT ///////////////////

    public void stepMove(FieldEntity entity, FieldPos pos) {
        //cells need very clear X:Y !
        //best interface here would be just gen args? then get by class? well, for non-entity stuff, aye!
        // manager.event(CombatEventType.Unit_Being_Moved, new EntityRef(entity), pos);
        entity.setPos(pos);
        entity.addCurValue(UnitParam.Moves, -1);
    }

    /*
    consider how to set some data structures that will provide static text info for CLIENT - e.g. tooltips why unit can't go
    to Enemy Flank etc!
     */
    public Set<FieldPos> getAvailablePositions(Unit unit) {
        int moves = unit.getInt(UnitParam.Moves);
        //swaps?
        //cell is free if there is no VISIBLE unit there
        Set<FieldPos> fullSet = new HashSet<>();
        Set<FieldPos> originSet = new HashSet<>();
        originSet.add(unit.getPos());
        for (int i = 0; i < moves; i++) {
            for (FieldPos pos : originSet) {
                Set<FieldPos> set = adjacentMap.get(pos);
                //filter out enemy cells
                // set.removeIf(pos -> pos.getTeam() != unit.isAlly());

                //filter out diagonal adjacency
                if (unit.isTrue(UnitProp.Agile)) {
                    // set.removeIf(pos -> pos.getTeam() != unit.isAlly());
                }
                for (FieldPos cell : set) {
                    // ConditionImpl<Unit> swapCondition = null; //  e -> e.canMoveDst(e.getPos().dst(unit.getPos()));
                    if (isCellFree(cell, false, null )) {
                        if (!canMove(unit, cell))
                            continue;
                        if (dst(cell, unit.getPos()) > moves)
                            continue;
                        set.add(cell);
                    }
                }
                fullSet.addAll(set);
                //TODO FIX multi-move - or do we even need such a feature? Are moves always one-step?
                originSet = set; //nah, this is not recursive - it will break!
            }
        }
        return fullSet;
    }

    private boolean canMove(Unit unit, FieldPos cell) {
        if (cell.isFlank()) {

        }
        if (unit.isTrue(elements.stats.UnitProp.Infiltrator)) {

        }
        return true;
    }

    //endregion

    ///////////////// region TARGETS ///////////////////
    public TargetGroup getAvailableTargets(Targeting targeting, FieldPos origin) {
        return null;
    }

    public TargetGroup getInArea(Shape shape, Direction direction, FieldPos origin, Condition filter) {
        //TODO will spells really use this?
        return null;
    }

    //TODO
    //NOT FOR MOVEMENT
    private int dst(FieldPos from, FieldPos to) {
        Integer x = from.getCell().x;
        // int xDiff =Math.abs(getX() - pos2.getX());
        // int yDiff =Math.abs(getY() - pos2.getY());
        return 0;
    }
    //endregion

    ///////////////// region GETTERS ///////////////////
    public FieldEntity getByPos(FieldPos pos) {
        for (Entity unit : getData().getFieldEntities()) {
            if (((FieldEntity)unit).getPos().equals(pos)) {
                return (FieldEntity) unit;
            }
        }
        return null;
    }

    public FieldPos getPos(int cardinal) {
        return cells[cardinal];
    }

    public FieldPos getPos(Boolean ally, String s) {
        //TODO do we need string syntax?
        //should we just have some ID's for each position?!
        if (s.contains("flank")) {

        }
        return null;
    }
    //endregion

}
