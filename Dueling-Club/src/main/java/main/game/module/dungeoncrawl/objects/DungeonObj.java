package main.game.module.dungeoncrawl.objects;

import main.entity.Ref;
import main.entity.obj.Structure;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;

/**
 * Created by JustMe on 9/23/2017.
 */
public abstract class DungeonObj extends Structure {

    public enum DUNGEON_OBJ_TYPE{
        DOOR, TRAP, LOCK, ENTRANCE, CONTAINER, INTERACTIVE, ITEM,
    }
    protected DungeonObjMaster DM;
    public DungeonObj(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {

        super(type,x, y, owner, game, ref);
    }

    public boolean isLocked() {
        return false;
    }

    public DungeonObjMaster getDM() {
        return DM;
    }

    public abstract DUNGEON_OBJ_TYPE getDungeonObjType();

    @Override
    public void construct() {
        super.construct();
        this.DM = getGame().getDungeonMaster().getDungeonObjMaster(getDungeonObjType());
    }



    /*
    canBePicked


     */
}
