package main.game.logic.dungeon.special;

import main.content.CONTENT_CONSTS.STATUS;
import main.entity.Ref;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.player.Player;

public class LockObj extends DC_HeroObj {



    public LockObj(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
    }

    public void unlock() {

        addStatus(STATUS.UNLOCKED.name());
    }
}
