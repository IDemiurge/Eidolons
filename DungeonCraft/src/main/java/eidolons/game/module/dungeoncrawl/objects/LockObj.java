package eidolons.game.module.dungeoncrawl.objects;

import eidolons.game.core.game.DC_Game;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;

public class LockObj extends DungeonObj {


    public LockObj(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
    }

    @Override
    public DUNGEON_OBJ_TYPE getDungeonObjType() {
        return DUNGEON_OBJ_TYPE.LOCK;
    }

    @Override
    public LockMaster getDM() {
        return (LockMaster) super.getDM();
    }

    public void unlock() {

        addStatus(UnitEnums.STATUS.UNLOCKED.name());
    }
}
