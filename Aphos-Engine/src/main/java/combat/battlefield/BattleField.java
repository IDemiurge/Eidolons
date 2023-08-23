package combat.battlefield;

import combat.BattleHandler;
import combat.sub.BattleManager;
import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import elements.exec.condition.EntityCondition;
import elements.exec.targeting.TargetGroup;
import elements.exec.targeting.Targeting;
import elements.stats.UnitParam;
import framework.entity.Entity;
import framework.entity.field.FieldEntity;
import framework.entity.field.Unit;
import framework.field.Environment;
import framework.field.FieldPos;
import framework.field.Visibility;
import logic.execution.event.combat.CombatEventType;

import java.util.HashSet;
import java.util.Set;

import static elements.content.enums.FieldEnums.*;

/**
 * Created by Alexander on 6/13/2023
 * <p>
 * Handles everything related to entities on the FIELD This includes movement?
 */
public class BattleField extends BattleHandler {
    Environment env;
    FieldPos[] cells = all;

    public BattleField(BattleManager battleManager) {
        super(battleManager);
    }

    ///////////////// region CELLS ///////////////////

    public boolean isCellFree(FieldPos pos, boolean allVisible, EntityCondition swap) {
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

    public void move(FieldEntity entity, FieldPos pos) {
        //cells need very clear X:Y !
        //best interface here would be just gen args? then get by class? well, for non-entity stuff, aye!
        manager.event(CombatEventType.Unit_Being_Moved, new EntityRef(entity), pos);
    }

    public Set<FieldPos> getAvailablePositions(Unit unit) {
        int moves = unit.getInt(UnitParam.Moves);
        //swaps?
        //cell is free if there is no VISIBLE unit there
        Set<FieldPos> set = new HashSet<>();
        for (FieldPos cell : cells) {
            EntityCondition<Unit> swapCondition =null; //  e -> e.canMoveDst(e.getPos().dst(unit.getPos()));
            if (isCellFree(cell, false, swapCondition)) {

            }
        }
        return set;
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
    //endregion

    ///////////////// region GETTERS ///////////////////
    public FieldEntity getByPos(FieldPos pos) {
        for (Entity unit : getData().getUnits()) {
            if (((FieldEntity) unit).getPos().equals(pos)) {
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
        if (s.contains("flank")){

        }
        return null;
    }
    //endregion

}
