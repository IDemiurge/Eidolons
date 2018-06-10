package eidolons.game.module.adventure.map.travel.old;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.macro.entity.party.MacroParty;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.ItemEnums.ARMOR_TYPE;
import main.content.enums.entity.ItemEnums.WEAPON_TYPE;
import main.content.enums.rules.ArcadeEnums.LOOT_GROUP;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.DequeImpl;
import main.system.entity.FilterMaster;

import java.util.ArrayList;
import java.util.List;

public class LootMaster {

    private static final int DEFAULT_ITEM_MINIMUM = 3;

    public static void battleIsWon(DC_Game game) {
//        PartyObj party = game.getParty();
//        DequeImpl<Unit> enemiesSlain = game.getBattleManager()
//                .getSlainEnemies();
//        Dungeon dungeon = game.getDungeonMaster().getDungeonWrapper();
        // additional choice for leader - share more, share equal, claim all
//        int lootValue = calculateLootValue(dungeon, enemiesSlain);
//        List<Entity> loot = generateLoot(lootValue, dungeon);
//        for (Entity item : new ArrayList<>(loot)) {
//            for (Unit h : party.getMembers()) {
////				if (checkAwardLootItem(h, item, loot))
//                break;
//            }
//        }
//
//        for (Unit h : party.getMembers()) {
//            int xpAwarded = awardXp(enemiesSlain, h, party);
//            for (Entity item : new ArrayList<>(loot)) {
//                checkAwardLootItem(h, item, loot);
//            }
//            int goldAwarded = awardGold(lootValue, h);
//        }
        // if encounter -> prompt gather dropped items
        // TimeMaster.hoursPassed(hours);
        game.getDroppedItemManager().getAllDroppedItems();

        // ++ dropped items!
        // for items - simple dialogs if 'disputed'?

    }

    private static void checkAwardLootItem(Unit h, Entity item,
                                           List<? extends Entity> loot) {
        if (h.isLeader()) {
            // Claim / Leave / Contend (random?)
        }
        boolean wanted = checkItemDesired(h, item);
        Boolean choice;
        if (wanted) {
            choice = DialogMaster.askAndWait(h.getName()
              + ": I am going to claim " + item.getName(), true, "Sure",
             "Better not", "Absolutely not");
            if (choice == null) {
                // reduce loyalty
                return;
            }
        } else {
            choice = DialogMaster.askAndWait(h.getName() + ": What about " + ""
              + item.getName() + ", shall I claim it?", true,
             "Certainly", "Better not", "If you wish");
            if (choice == null) {
                // random? preCheck wanted by others
            }
        }

    }

    private static List<Unit> checkItemDesired(MacroParty party,
                                               Entity item) {
        return null;
    }

    private static boolean checkItemDesired(Unit h, Entity item) {
        // TODO h.getProperty( WANTED_ITEM_TYPES)
//		for (String s :  StringMaster.openContainer(string )){
//			prop = wanted.split()[0];
//		}
        item.getProperty(G_PROPS.WEAPON_CLASS);
        return false;
    }

    private static int awardGold(int lootValue, Unit h) {
        h.getIntParam(MACRO_PARAMS.C_GOLD_SHARE);

        return 0;
    }

    private static int awardXp(DequeImpl<Unit> enemiesSlain,
                               Unit her, Party party) {
        for (Unit unit : enemiesSlain) {
            Obj killer = unit.getRef().getObj(KEYS.KILLER);
            for (Unit h : party.getMembers()) {
//				if (killer.equals(h))
//					percent = 100;
//				else
//					percent = 100 / party.getMembers().size();
            }
            // some percent to others...
            // increase share of loot?
            // remainder split evenly...
        }
        return 0;
    }

    private static List<Entity> generateLoot(int lootValue, Dungeon dungeon) {
        int min = 0;
        // dungeon.getIntParam(PARAMS.MIN_ITEMS_LOOT);
        if (min <= 0) {
            min = DEFAULT_ITEM_MINIMUM;
        }
        int max = min * 3 / 2;
        List<Entity> loot = new ArrayList<>();
        Loop.startLoop(max);
        int total = 0;
        while (!Loop.loopEnded()) {
            if (total >= lootValue) {
                break;
            }
            LOOT_GROUP group = getLootGroup(dungeon);
            int value = lootValue / min;
            ObjType type = generateLootItem(value, group);
            loot.add(type);
            total += getItemValue(type);
        }
        return loot;
    }

    private static int getItemValue(Entity type) {
        return type.getIntParam(PARAMS.GOLD_COST);
    }

    private static void awardLoot(ObjType type, Unit h) {
        DC_HeroItemObj item = ItemFactory.createItemObj(type,
         h.getOriginalOwner(), h.getGame(), h.getRef(), false);
        h.addItemToInventory(item);
        // SAVE: containers into props for type
    }

    private static LOOT_GROUP getLootGroup(Dungeon dungeon) {
        return null;
//		return new RandomWizard<>().getObjectByWeight(
//				dungeon.getProperty(PROPS.LOOT_GROUPS), LOOT_GROUP.class);
    }

    private static int calculateLootValue(Dungeon dungeon,
                                          DequeImpl<Unit> enemiesSlain) {
        int value = 0;
        for (Unit h : enemiesSlain) {
            value += h.getIntParam(PARAMS.POWER);
        }
        return value;
    }

    public static ObjType generateLootItem(int value, LOOT_GROUP group) {
        Object[] filterValues = getFilteringValues(group);
        List<ObjType> list = DataManager.getTypes(getTYPE(group));
        for (Object val : filterValues) {
            PROPERTY prop = ContentValsManager.findPROP(val.getClass()
             .getSimpleName());
            FilterMaster.filterByProp(list, prop.getName(), val.toString());
        }

        return new RandomWizard<ObjType>().getRandomListItem(list);
    }

    public static Object[] getFilteringValues(LOOT_GROUP group) {
        switch (group) {
            case AMMO:
                return new WEAPON_TYPE[]{WEAPON_TYPE.AMMO};
            case ARMOR:

                break;
            case CONCONCTIONS:
                break;
            case HEAVY_ARMOR:
                return new ARMOR_TYPE[]{ARMOR_TYPE.HEAVY};
            case HEAVY_WEAPONS:
                break;
            case JEWELRY:
                break;
            case JEWELRY_SPECIAL:
                break;
            case LIGHT_ARMOR:
                break;
            case LIGHT_WEAPONS:
                break;
            case MAGIC_ARMOR:
                break;
            case MAGIC_ITEMS:
                break;
            case MAGIC_MATERIAL_WEAPONS:
                break;
            case MAGIC_WEAPONS:
                break;
            case POISONS:
                break;
            case POTIONS:
                break;
            case WEAPONS:
                break;
            case WEIRD_ARMOR:
                break;
            case WEIRD_ITEMS:
                break;
            case WEIRD_WEAPONS:
                break;
            default:
                break;

        }
        return null;
    }

    private static OBJ_TYPE getTYPE(LOOT_GROUP group) {
        switch (group) {
            case AMMO:
                return DC_TYPE.WEAPONS;
            case ARMOR:
                return DC_TYPE.ARMOR;
            case CONCONCTIONS:
                return DC_TYPE.ITEMS;
            case HEAVY_ARMOR:
                return DC_TYPE.ARMOR;
            case HEAVY_WEAPONS:
                return DC_TYPE.WEAPONS;
            case JEWELRY:
                return DC_TYPE.JEWELRY;
            case JEWELRY_SPECIAL:
                return DC_TYPE.JEWELRY;
            case LIGHT_ARMOR:
                return DC_TYPE.ARMOR;
            case LIGHT_WEAPONS:
                return DC_TYPE.WEAPONS;
            case MAGIC_ARMOR:
                return DC_TYPE.ARMOR;
            case MAGIC_ITEMS:
                return DC_TYPE.ITEMS;
            case MAGIC_MATERIAL_WEAPONS:
                return DC_TYPE.WEAPONS;
            case MAGIC_WEAPONS:
                return DC_TYPE.WEAPONS;
            case POISONS:
                return DC_TYPE.ITEMS;
            case POTIONS:
                return DC_TYPE.ITEMS;
            case WEAPONS:
                return DC_TYPE.WEAPONS;
            case WEIRD_ARMOR:
                return DC_TYPE.ARMOR;
            case WEIRD_ITEMS:
                return DC_TYPE.ITEMS;
            case WEIRD_WEAPONS:
                return DC_TYPE.WEAPONS;

        }
        return null;
    }
}
