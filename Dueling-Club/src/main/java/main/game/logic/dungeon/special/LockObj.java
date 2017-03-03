package main.game.logic.dungeon.special;

import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;

public class LockObj extends Unit {


    public LockObj(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
    }

    public void unlock() {

        addStatus(UnitEnums.STATUS.UNLOCKED.name());
    }
}
