package main.entity.obj;

import main.content.ContentManager;
import main.content.OBJ_TYPES;
import main.content.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.MicroGame;
import main.game.player.Player;

public class DC_JewelryObj extends DC_HeroItemObj {

    public DC_JewelryObj(ObjType type, Player owner, MicroGame game, Ref ref) {
        super(type, owner, game, ref, getJewelryParams());
    }

    private static PARAMETER[] getJewelryParams() {
        String entity = OBJ_TYPES.CHARS.getName();
        return ContentManager.getParamsForType(entity, false).toArray(
                new PARAMETER[ContentManager.getParamsForType("chars", false)
                        .size()]);

    }

    protected PARAMETER getDurabilityParam() {
        return null;
    }

    protected boolean isActivatePassives() {
        return false;
    }
}
