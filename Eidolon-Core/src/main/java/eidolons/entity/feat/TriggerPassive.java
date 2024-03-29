package eidolons.entity.feat;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.DC_Obj;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;

/**
 * Aka Feat. How will it be created?
 * ActivatePassives() same way
 * ChargeBasedTrigger subclass?
 *
 */
public class TriggerPassive extends DC_Obj  implements Feat {

    public TriggerPassive(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public ActiveObj getActive() {
        return null;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

}
