package eidolons.game.battlecraft.logic.meta.arcade;

import eidolons.client.cc.logic.party.Party;
import eidolons.content.DC_CONSTS.MAGICAL_ITEM_LEVEL;
import eidolons.content.DC_CONSTS.MAGIC_ITEM_PASSIVE_ENCHANTMENT;
import eidolons.content.DC_CONSTS.MAGIC_ITEM_PASSIVE_TRAIT;
import eidolons.content.PARAMS;
import main.content.enums.rules.ArcadeEnums.ARCADE_LOOT_TYPE;
import main.content.enums.rules.ArcadeEnums.LOOT_GROUP;
import main.content.values.parameters.MACRO_PARAMS;
import eidolons.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.core.game.DC_Game;

public class LootManager {

	/*
     * "Normal loot" - the stuff that drops from units, items mostly, but
	 * perhaps natural weapons as well and some 'parts' - distributing it among
	 * heroes... *1) Player's choices *2) Hero 'minimum' share - with Loyalty
	 * falling if not met *3) Hero's 'wanted items' - per class or mastery, with
	 * some Loyalty effects
	 * 
	 * What kind of GUI could there be for this? The big stash and the hero
	 * portraits in line - select item(s) and click to give them to X Select
	 * hero to see his wanted items... and current share
	 */

    private DC_Game game;

    public LootManager(DC_Game game) {
        this.game = game;
    }

    public void generateEnhancedItem(MAGICAL_ITEM_LEVEL level,
                                     ObjType baseType, MAGIC_ITEM_PASSIVE_TRAIT traits) {

    }

    // Enchanter
    public void generateEnchantedItem(MAGICAL_ITEM_LEVEL level,
                                      ObjType baseType, MAGIC_ITEM_PASSIVE_ENCHANTMENT... enchantments) {
        // choose traits and add them on appropriate level
        // use item generator

    }

    public void randomizeItem(LOOT_GROUP type) {

        // quality/material/type

    }

    public void awardLoot(Party party, Dungeon dungeon) {
        ARCADE_LOOT_TYPE loot_type = null;
        int amount = 0; // measure in GOLD, of course!

        // game.getArcadeManager().getDifficulty();

        // how to manage this between heroes?
        // perhaps each item should require hero-choice, i.e.
        // "choose hero to gift the item to, or claim it for yourself"
        // in the future, main_hero vs companions will matter!

        // *spend points*?
        // weight-alg for choices

        dungeon.getIntParam(PARAMS.GOLD_REWARD); // from quests?
        // * loot_gold_mod
        if (amount == 0) {
            // amount = calculateGoldReward(dungeon, party);
        }
        // party's gold - for the leader? "Give gold" option, without
        // "take gold"?
        // perhaps if Loyalty is high enough...
        for (Unit m : party.getMembers()) {
            if (m == party.getLeader()) {
                continue;
            }
            int share = amount * m.getIntParam(MACRO_PARAMS.C_GOLD_SHARE) / 100;
            amount -= share;
            m.modifyParameter(PARAMS.GOLD, share);
        }
        party.getLeader().modifyParameter(PARAMS.GOLD, amount);
    }
}
