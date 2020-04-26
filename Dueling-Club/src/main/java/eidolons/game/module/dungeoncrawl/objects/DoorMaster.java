package eidolons.game.module.dungeoncrawl.objects;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.dungeoncrawl.objects.DoorMaster.DOOR_ACTION;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.content.enums.entity.UnitEnums.CLASSIFICATIONS;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.ArrayList;
import java.util.List;


public class DoorMaster extends DungeonObjMaster<DOOR_ACTION> {

    public   DoorMaster(DungeonMaster dungeonMaster) {
        super(dungeonMaster);
    }

    private static DOOR_STATE getState(DOOR_ACTION sub) {
        switch (sub) {
            case OPEN:
            case UNSEAL:
                return DOOR_STATE.OPEN;
            case CLOSE:
            case UNLOCK:
                return DOOR_STATE.CLOSED;
            case LOCK:
                return DOOR_STATE.LOCKED;
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
            case UNSEAL:
                KeyMaster.doorUnsealed(door, unit);
            case OPEN:
            case UNLOCK:
                open(door, unit.getRef().getTargetingRef(obj));
                return true;
            case CLOSE:
                close(door,  unit.getRef().getTargetingRef(obj) );
                break;
        }
        DOOR_STATE state = getState(sub);
        door.setState(state);
        return true;
    }

    private void close(Door door, Ref targetingRef) {
        door.getGame().fireEvent(
         new Event(STANDARD_EVENT_TYPE.DOOR_CLOSES,
         targetingRef));
        door.setState(DOOR_STATE.CLOSED);
        DC_SoundMaster.playStandardSound(STD_SOUNDS.NEW__CLICK_DISABLED);
    }

    protected boolean checkAction(Unit unit, Door door, DOOR_ACTION sub) {
        if (PositionMaster.getDistance(unit, door) > 1)
            return false;
        if (FacingMaster.getSingleFacing(unit.getFacing(), unit, door) != FACING_SINGLE.IN_FRONT)
            return false;
        switch (sub) {
            case OPEN:
//            case LOCK: //TODO
                return door.getState() == DOOR_STATE.CLOSED;
            case CLOSE:
                if (door.getGame().getObjectsOnCoordinate(  door.getCoordinates(), true, true, false).size() > 1) {
                    return false;
                }
                return isOpen(door);
            case UNLOCK:
                return door.getState() == DOOR_STATE.LOCKED;
            case UNSEAL:
                if (door.getState() == DOOR_STATE.SEALED){
                    return checkCanUnseal(unit, door);
                }
                return false;
        }

        return false;
    }

    private boolean checkCanUnseal(Unit unit, Door door) {
       return KeyMaster.hasKey(unit, door);
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
        if (!checkUnitCanHandleActions(unit)) {
            return new ArrayList<>();
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

    protected boolean checkUnitCanHandleActions(Unit unit) {
        if (unit.canUseItems())
            return true;
        if (unit.getChecker().checkClassification(CLASSIFICATIONS.ANIMAL) ||
         unit.getChecker().checkClassification(CLASSIFICATIONS.INSECT)) {
            return unit.getIntParam(PARAMS.WEIGHT) >= 50;
        }

        return true;
    }

    @Override
    public void open(DungeonObj obj, Ref ref) {
        Door door = (Door) obj;
        if (CoreEngine.isActiveTestMode())
            door.setState(DOOR_STATE.SEALED);

        if (door.getState()==DOOR_STATE.SEALED){
            KeyMaster.initAnimRef(obj, ref);
            obj.getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.DOOR_IS_UNLOCKED  , ref));
//            GuiEventManager.trigger(GuiEventType.SINGLE_ANIM, LockKeyAnimation.class, ref);
            door.setState(DOOR_STATE.OPEN);
            DC_SoundMaster.playStandardSound(STD_SOUNDS.NEW__UNLOCK);
            return;
        }
//        door.setState(DOOR_STATE.OPEN);
        obj.getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.DOOR_OPENS, ref));
        door.setState(DOOR_STATE.OPEN);
        DC_SoundMaster.playStandardSound(STD_SOUNDS.NEW__GATE);
    }


    public enum DOOR_ACTION implements DUNGEON_OBJ_ACTION {
        UNSEAL,
        OPEN,
        CLOSE,
        LOCK,
        UNLOCK,
    }

    public enum DOOR_STATE {
        LOCKED, //PICKABLE
        SEALED, //INDESTRUCTIBLE
        CLOSED,
        OPEN,
    }
}
