package main.game.module.dungeoncrawl.objects;

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
        if (state == DOOR_STATE.LOCKED)
            return true;
        if (state == DOOR_STATE.SEALED)
            return true;
        return false;
    }

    @Override
    public boolean isObstructing() {
        if (state == DOOR_STATE.OPEN)
            return false;
        return true;
    }
}
