package main.game.module.dungeoncrawl.objects;

import main.ability.AbilityType;
import main.ability.ActiveAbilityObj;
import main.ability.conditions.FacingCondition;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.data.DataManager;
import main.elements.conditions.Conditions;
import main.elements.conditions.DistanceCondition;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 9/23/2017.
 * <p>
 * functions for interacting with various objects
 * <p>
 * > Do I need a class for each?
 */
public abstract class DungeonObjMaster<T extends DUNGEON_OBJ_ACTION> {
    DungeonMaster dungeonMaster;

    public DungeonObjMaster(DungeonMaster dungeonMaster) {
        this.dungeonMaster = dungeonMaster;
    }

    public void takeItem(DungeonObj obj) {

    }

    protected abstract boolean actionActivated(T sub,
                                               Unit unit, DungeonObj obj);

    public abstract List<DC_ActiveObj> getActions(DungeonObj obj, Unit unit);

    public abstract void open(DungeonObj obj, Ref ref);

    public DC_UnitAction createAction(T sub, Unit unit,
                                      DungeonObj obj) {
        return createAction(sub, unit, sub.toString(), obj);
    }

    public DC_UnitAction createAction(T sub, Unit unit, String typeName,
                                      DungeonObj obj) {
        //TODO CACHE
        DC_UnitAction action =
         unit.getGame().getActionManager().getOrCreateAction(typeName, unit);
        action.setTargeting(new SelectiveTargeting(
         new Conditions(new DistanceCondition("1", true)
         , new FacingCondition(FACING_SINGLE.IN_FRONT))));

        action.setTargetingCachingOff(true);
        action.setActionTypeGroup(ACTION_TYPE_GROUPS.STANDARD);
        action.setAbilities(null);
        List<ActiveObj> actives = new LinkedList<>();
        actives.add(new ActiveAbilityObj((AbilityType)
         DataManager.getType("Dummy Ability", DC_TYPE.ABILS),
         unit.getRef(), unit.getOwner(), unit.getGame()) {
            @Override
            public boolean activatedOn(Ref ref) {
                if (!actionActivated(sub, unit, obj))
                    return false;
                return true;
            }
        });
        action.setActives(actives);
        action.setConstructed(true);
        action.setActionTypeGroup(ACTION_TYPE_GROUPS.DUNGEON);
        return action;
    }

    public abstract DC_ActiveObj getDefaultAction(Unit source, DungeonObj target);

    //     public abstract boolean isVisible(BattleFieldObject obj);
//    isObstructing
//     isPassable
//    applyAction

}
