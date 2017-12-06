package main.game.module.dungeoncrawl.objects;

import main.content.PARAMS;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.content.enums.entity.UnitEnums.CLASSIFICATIONS;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonWrapper;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.module.dungeoncrawl.objects.DoorMaster.DOOR_ACTION;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;


public class DoorMaster extends DungeonObjMaster<DOOR_ACTION> {

    public <E extends DungeonWrapper> DoorMaster(DungeonMaster<E> dungeonMaster) {
        super(dungeonMaster);
    }

    private static DOOR_STATE getState(DOOR_ACTION sub) {
        switch (sub) {
            case OPEN:
                return DOOR_STATE.OPEN;
            case CLOSE:
                return DOOR_STATE.CLOSED;
            case LOCK:
                return DOOR_STATE.LOCKED;
            case UNLOCK:
                return DOOR_STATE.CLOSED;
            case UNSEAL:
                return DOOR_STATE.OPEN;
        }
        return null;
    }

    public static boolean isDoor(BattleFieldObject door) {
        return door.getProperty(G_PROPS.BF_OBJECT_GROUP).equalsIgnoreCase(BF_OBJECT_GROUP.DOOR.toString());

    }

    public static boolean tryPickLock(Door door) {

        return false;
    }

    public static boolean isOpen(Door door) {
        return door.getState() == DOOR_STATE.OPEN;

    }

    @Override
    protected boolean actionActivated(DOOR_ACTION sub,
                                      Unit unit, DungeonObj obj) {
        Door door = (Door) obj;
        if (sub == DOOR_ACTION.UNLOCK) {
            if (!tryPickLock(door))
                return true;
        }
        switch (sub) {
            case OPEN:
                open(door, unit.getRef().getTargetingRef(obj));
                return true;
            case CLOSE:
                obj.getGame().fireEvent(
                 new Event(STANDARD_EVENT_TYPE.DOOR_CLOSES,
                  unit.getRef().getTargetingRef(obj)));
                break;
        }
        DOOR_STATE state = getState(sub);
        door.setState(state);
        return true;
    }

    protected boolean checkAction(Unit unit, Door door, DOOR_ACTION sub) {
        if (PositionMaster.getDistance(unit, door)>1)
            return false;
        if (FacingMaster.getSingleFacing(unit.getFacing(), unit, door)!= FACING_SINGLE.IN_FRONT)
            return false;
        switch (sub) {
            case OPEN:
//            case LOCK: //TODO
                return !isOpen(door);
            case CLOSE:
                if (door.getGame().getObjectsAt(door.getCoordinates()).size() > 1) {
                    return false;
                }
                return isOpen(door);
            case UNLOCK:
                return door.getState() == DOOR_STATE.LOCKED;
            case UNSEAL:
                return door.getState() == DOOR_STATE.SEALED;
        }

        return false;
    }

    @Override
    public DC_ActiveObj getDefaultAction(Unit source, DungeonObj target) {

        for (DOOR_ACTION sub : DOOR_ACTION.values()) {
            if (checkAction(source, (Door) target, sub)) {
                return createAction(sub, source, target);
            }
        }
        return null;
    }

    @Override
    public DC_UnitAction createAction(DOOR_ACTION sub, Unit unit, DungeonObj obj) {
        return super.createAction(sub, unit,
         StringMaster.getWellFormattedString(sub.name()) + " Door",
         obj);
    }

    public List<DC_ActiveObj> getActions(DungeonObj door, Unit unit) {
        if (!(door instanceof Door))
            return new ArrayList<>();
        if (!checkUnitCanHandleActions(unit)){
            return      new ArrayList<>() ;
        }
        //check intelligence, mastery
        List<DC_ActiveObj> list = new ArrayList<>();
        DC_UnitAction action = null;
        for (DOOR_ACTION sub : DOOR_ACTION.values()) {

            if (checkAction(unit, (Door) door, sub)) {
                action = createAction(sub, unit, door);

                if (action != null) {
                    list.add(action);

                }
            }
        }
        return list;
    }

    protected  boolean checkUnitCanHandleActions(Unit unit) {
        if ( unit.canUseItems())
            return true;
        if (unit.getChecker().checkClassification(CLASSIFICATIONS.ANIMAL)||
         unit.getChecker().checkClassification(CLASSIFICATIONS.INSECT))
        {
            if (unit.getIntParam(PARAMS.WEIGHT)<50)
            return false;
        }

        return true;
    }

    @Override
    public void open(DungeonObj obj, Ref ref) {
        Door door = (Door) obj;
        door.setState(DOOR_STATE.OPEN);
//        GuiEventManager.trigger(GuiEventType.OPEN_DOOR);
        obj.getGame().fireEvent(
         new Event(STANDARD_EVENT_TYPE.DOOR_OPENS,
          ref));
    }


    public enum DOOR_ACTION implements DUNGEON_OBJ_ACTION {
        OPEN,
        CLOSE,
        LOCK,
        UNLOCK,
        UNSEAL,
    }

    public enum DOOR_STATE {
        LOCKED, //PICKABLE
        SEALED, //INDESTRUCTIBLE
        CLOSED,
        OPEN,
    }
}
