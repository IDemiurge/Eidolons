package main.game.module.dungeoncrawl.objects;

import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;

/**
 * Created by JustMe on 9/23/2017.
 */
public class Door extends DungeonObj{

    public Door(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
    }

    @Override
    public DUNGEON_OBJ_TYPE getDungeonObjType() {
        return DUNGEON_OBJ_TYPE.DOOR;
    }
}
