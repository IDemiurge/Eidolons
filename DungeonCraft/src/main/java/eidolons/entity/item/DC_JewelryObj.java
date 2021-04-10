package eidolons.entity.item;

import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.ItemEnums.JEWELRY_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;

public class DC_JewelryObj extends DC_HeroItemObj {

    public DC_JewelryObj(ObjType type, Player owner, GenericGame game, Ref ref) {
        super(type, owner, game, ref, getJewelryParams());
    }

    private static PARAMETER[] getJewelryParams() {
        String entity = DC_TYPE.CHARS.getName();
        return ContentValsManager.getParamsForType(entity, false).toArray(
         new PARAMETER[ContentValsManager.getParamsForType("chars", false)
          .size()]);

    }

    @Override
    protected void applyDurability() {

    }

    public boolean isAmulet() {
        return checkProperty(G_PROPS.JEWELRY_TYPE, JEWELRY_TYPE.AMULET.toString());
    }

    protected PARAMETER getDurabilityDependentParam() {
        return null;
    }

    protected boolean isActivatePassives() {
        return false;
    }
}
