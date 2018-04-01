package eidolons.entity.item;

import eidolons.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;

import static main.content.enums.entity.ItemEnums.GARMENT_TYPE;

public class Garment extends DC_HeroSlotItem {
    /*
     * Gloves - unarmed self-damage, increase atk-speed, sneak/crit/attack mods
     * Boots/Greaves move speed, defense
     * Cloak/Pauldrons (shoulders)  stealth or armor
     * ++ helmet???
     * minor armor bonus
     * durability - do they break down? if so, how to split between armor and these?
     */
    private GARMENT_TYPE garment_type;

    // TODO GENERATION: material types? noise level...
    public Garment(ObjType type, Player owner, MicroGame game, Ref ref,
                   PARAMETER[] params) {
        super(type, owner, game, ref, params);
        garment_type =
         new EnumMaster<GARMENT_TYPE>().retrieveEnumConst(GARMENT_TYPE.class, getProperty(G_PROPS.GARMENT_TYPE));
    }

    @Override
    protected void applyPenaltyReductions() {
        int penalty_reduction = -getHero().getIntParam(PARAMS.STRENGTH);

        switch (garment_type) {
            case BOOTS:
                modifyParameter(PARAMS.MOVE_AP_PENALTY, penalty_reduction, 0,
                 true);
                modifyParameter(PARAMS.MOVE_STA_PENALTY, penalty_reduction, 0,
                 true);
        }

    }

    @Override
    protected PARAMETER getDurabilityParam() {

        return null;
    }

}
