package main.entity.item;

import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.values.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;

public class DC_JewelryObj extends DC_HeroItemObj {

    public DC_JewelryObj(ObjType type, Player owner, MicroGame game, Ref ref) {
        super(type, owner, game, ref, getJewelryParams());
    }

    private static PARAMETER[] getJewelryParams() {
        String entity = DC_TYPE.CHARS.getName();
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
