package eidolons.game.module.dungeoncrawl.objects;

import eidolons.content.PROPS;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.objects.DoorMaster.DOOR_STATE;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;

/**
 * Created by JustMe on 9/23/2017.
 */
public class Door extends DungeonObj {

    private   boolean obstructing=true;
    DOOR_STATE state;

    public Door(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
          obstructing =  checkPassive(UnitEnums.STANDARD_PASSIVES.NON_OBSTRUCTING);
    }

    public DOOR_STATE getState() {
        if (state == null) {
            state =getInitialState();
            if (state == DOOR_STATE.SEALED) {
                addProperty(true, G_PROPS.STANDARD_PASSIVES, UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE.getName());
            }
        }
        return state;
    }

    private DOOR_STATE getInitialState() {
        if (!getProperty( PROPS.KEY_TYPE).isEmpty()) {
            return DOOR_STATE.SEALED;
        }
        if (KeyMaster.isSealedDoor(this)){
            return DOOR_STATE.SEALED;
        }
            if (!getProperty(G_PROPS.CUSTOM_PROPS).isEmpty()) {
            return new EnumMaster<DOOR_STATE>().retrieveEnumConst(DOOR_STATE.class,
                    getProperty(G_PROPS.CUSTOM_PROPS));
        }
        return  DOOR_STATE.CLOSED;

    }

    public void setState(DOOR_STATE state) {
        this.state = state;
    }

    @Override
    public DUNGEON_OBJ_TYPE getDungeonObjType() {
        return DUNGEON_OBJ_TYPE.DOOR;
    }

    @Override
    public boolean isWall() {
        return getState() != DOOR_STATE.OPEN;
    }

    @Override
    public boolean isObstructing() {
        if (isDead())
            return false;
        if (getState() == DOOR_STATE.OPEN)
            return false;
        return !obstructing;
    }
}
