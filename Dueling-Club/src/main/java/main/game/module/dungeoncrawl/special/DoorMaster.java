package main.game.module.dungeoncrawl.special;

import main.ability.Abilities;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.active.DC_UnitAction;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;


public class DoorMaster {

    public static List<DC_UnitAction> getDoorActions(BattleFieldObject door, Unit unit) {
//if (unit.is)
        //check intelligence, mastery
        List<DC_UnitAction> list = new LinkedList<>();
        DC_UnitAction action = null;
        for (DOOR_ACTION sub : DOOR_ACTION.values()) {
            if (checkAction(unit, door, sub)) {
                action = getAction(sub, unit, door);
                if (action != null) {
                    list.add(action);

                }
            }
        }
        return list;
    }

    private static DC_UnitAction getAction(DOOR_ACTION sub, Unit unit, BattleFieldObject door) {
        String typeName = StringMaster.getWellFormattedString(sub.name()) + " Door";
        DC_UnitAction action =
         unit.getGame().getActionManager().getOrCreateAction(typeName, unit);
        action.setAbilities(new Abilities() {
            @Override
            public boolean activatedOn(Ref ref) {
                if (!actionActivated(sub, unit, (BattleFieldObject) ref.getTargetObj()))
                    return false;
                return super.activatedOn(ref);
            }
        });
        return action;
    }

    private static boolean actionActivated(DOOR_ACTION sub,
                                        Unit unit, BattleFieldObject door) {
        if (sub == DOOR_ACTION.UNLOCK){
            if (!tryPickLock(door))
                return true;
        }
        DOOR_STATE state = getState(sub);
        setDoorState( door, state);
        return true;
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

    private static boolean checkAction(Unit unit, BattleFieldObject door, DOOR_ACTION sub) {
        switch (sub) {
            case OPEN:
                break;
            case CLOSE:
                break;
            case LOCK:
//                return isLocked(door);
            case UNLOCK:
                break;
            case UNSEAL:
                break;
        }

        return false;
    }

    public static DOOR_STATE getDoorState(BattleFieldObject door) {
        return new EnumMaster<DOOR_STATE>().retrieveEnumConst(DOOR_STATE.class, door.getProperty(G_PROPS.MODE));
    }

    public static void setDoorState(BattleFieldObject door, DOOR_STATE state) {

    }

    public static void applyDoorState(BattleFieldObject door) {
/*GIRTH change
        transparency
  not only in explore
         */


    }

    public static boolean isDoor(BattleFieldObject door) {
        return door.getProperty(G_PROPS.BF_OBJECT_GROUP).equalsIgnoreCase(BF_OBJECT_GROUP.DOOR.toString());

    }

    public static boolean openDoor(BattleFieldObject door) {
//        door.addStatus(value);
        return true;

    }

    public static boolean tryPickLock(BattleFieldObject door) {

        return false;
    }

    public static boolean isOpen(BattleFieldObject door) {
        return getDoorState(door) == DOOR_STATE.OPEN;

    }

    public enum DOOR_ACTION {
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
