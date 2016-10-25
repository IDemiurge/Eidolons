package main.entity.obj.top;

import main.entity.Ref;
import main.entity.obj.DC_Obj;
import main.entity.type.ObjType;
import main.game.MicroGame;
import main.game.player.Player;

public abstract class DC_AttachedObj extends DC_Obj {

    public DC_AttachedObj(ObjType type, Player owner, MicroGame game, Ref ref) {
        super(type, owner, game, ref);
        // TODO Auto-generated constructor stub
    }

}
