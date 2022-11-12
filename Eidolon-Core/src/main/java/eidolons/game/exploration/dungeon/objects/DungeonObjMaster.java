package eidolons.game.exploration.dungeon.objects;

import eidolons.ability.conditions.special.ClearShotCondition;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.ability.AbilityType;
import main.ability.ActiveAbilityObj;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.data.DataManager;
import main.elements.conditions.Conditions;
import main.elements.conditions.DistanceCondition;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.entity.obj.IActiveObj;

import java.util.ArrayList;
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

    public abstract List<ActiveObj> getActions(DungeonObj obj, Unit unit);

    public abstract void open(DungeonObj obj, Ref ref);

    public UnitAction createAction(T sub, Unit unit,
                                   DungeonObj obj) {
        return createAction(sub, unit, sub.toString(), obj);
    }

    public UnitAction createAction(T sub, Unit unit, String typeName,
                                   DungeonObj obj) {
        //TODO CACHE
        UnitAction action =
         unit.getGame().getActionManager().getOrCreateAction(typeName, unit);
        action.setTargeting(new SelectiveTargeting(

         new Conditions(new DistanceCondition("1", true)
          ,  new ClearShotCondition())));
        action.setConstructed(true);
        action.getTargeter().setTargetingInitialized(true);
        action.setTargetingCachingOff(true);
        action.setActionTypeGroup(ACTION_TYPE_GROUPS.STANDARD);
        action.setAbilities(null);
        List<IActiveObj> actives = new ArrayList<>();
        actives.add(new ActiveAbilityObj((AbilityType)
         DataManager.getType("Dummy Ability", DC_TYPE.ABILS),
         unit.getRef(), unit.getOwner(), unit.getGame()) {
            @Override
            public boolean activatedOn(Ref ref) {
                return actionActivated(sub, unit, obj);
            }
        });

        action.setActives(actives);
        action.setActionTypeGroup(ACTION_TYPE_GROUPS.DUNGEON);
        return action;
    }

    public abstract ActiveObj getDefaultAction(Unit source, DungeonObj target);

    //     public abstract boolean isVisible(BattleFieldObject obj);
//    isObstructing
//     isPassable
//    applyAction

}
