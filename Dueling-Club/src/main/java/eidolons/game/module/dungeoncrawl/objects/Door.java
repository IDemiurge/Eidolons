package eidolons.game.module.dungeoncrawl.objects;

import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.objects.DoorMaster.DOOR_STATE;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;

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