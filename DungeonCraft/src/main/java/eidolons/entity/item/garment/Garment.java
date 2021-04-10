package eidolons.entity.item.garment;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.item.DC_HeroSlotItem;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;

import static main.content.enums.entity.ItemEnums.GARMENT_TYPE;

public class Garment extends DC_ArmorObj {
    private final GARMENT_TYPE garment_type;

    // TODO GENERATION: material types? noise level...
    public Garment(ObjType type, Player owner, GenericGame game, Ref ref) {
        super(type, owner, game, ref);
        garment_type = new EnumMaster<GARMENT_TYPE>().retrieveEnumConst(GARMENT_TYPE.class, getProperty(G_PROPS.GARMENT_TYPE));
    }

    @Override
    protected void applyPenaltyReductions() {
        int penalty_reduction = -getHero().getIntParam(PARAMS.STRENGTH);

    }

    @Override
    protected PARAMETER getDurabilityDependentParam() {

        return null;
    }

}
