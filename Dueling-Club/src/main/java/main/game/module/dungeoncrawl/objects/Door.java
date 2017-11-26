package main.game.module.dungeoncrawl.objects;

import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;
import main.game.module.dungeoncrawl.objects.DoorMaster.DOOR_STATE;

/**
 * Created by JustMe on 9/23/2017.
 */
public class Door extends DungeonObj {

    DOOR_STATE state;

    public Door(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
    }

    public DOOR_STATE getState() {
        if (state == null) {
            state = DOOR_STATE.CLOSED;
        }
        return state;
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
        if (getState() == DOOR_STATE.OPEN)
            return false;

            return true;
    }

    @Override
    public boolean isObstructing() {
        if (getState() == DOOR_STATE.OPEN)
            return false;
        if ( checkPassive(UnitEnums.STANDARD_PASSIVES.NON_OBSTRUCTING)) {
            return false;
        }
        return true;
    }
}
