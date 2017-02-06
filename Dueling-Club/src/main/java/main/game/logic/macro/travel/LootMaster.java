package main.game.logic.macro.travel;

import main.client.cc.logic.party.PartyObj;
import main.content.CONTENT_CONSTS.ARMOR_TYPE;
import main.content.CONTENT_CONSTS.LOOT_GROUP;
import main.content.CONTENT_CONSTS.WEAPON_TYPE;
import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.parameters.MACRO_PARAMS;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroItemObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.ItemFactory;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.logic.dungeon.Dungeon;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.FilterMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.DequeImpl;

import java.util.LinkedList;
import java.util.List;

public class LootMaster {

    private static final int DEFAULT_ITEM_MINIMUM = 3;

    public static void battleIsWon(DC_Game game) {
        PartyObj party = game.getParty();
        DequeImpl<DC_HeroObj> enemiesSlain = game.getBattleManager()
                .getSlainEnemies();
        Dungeon dungeon = game.getDungeonMaster().getDungeon();
        // additional choice for leader - share more, share equal, claim all
        int lootValue = calculateLootValue(dungeon, enemiesSlain);
        List<Entity> loot = generateLoot(lootValue, dungeon);
        for (Entity item : new LinkedList<>(loot)) {
            for (DC_HeroObj h : party.getMembers()) {
//				if (checkAwardLootItem(h, item, loot))
                break;
            }
        }

        for (DC_HeroObj h : party.getMembers()) {
            int xpAwarded = awardXp(enemiesSlain, h, party);
            for (Entity item : new LinkedList<>(loot)) {
                checkAwardLootItem(h, item, loot);
            }
            int goldAwarded = awardGold(lootValue, h);
        }
        // if encounter -> prompt gather dropped items
        // TimeMaster.hoursPassed(hours);
        game.getDroppedItemManager().getAllDroppedItems();

        // ++ dropped items!
        // for items - simple dialogs if 'disputed'?

    }

    private static void checkAwardLootItem(DC_HeroObj h, Entity item,
                                           List<? extends Entity> loot) {
        if (h.isLeader()) {
            // Claim / Leave / Contend (random?)
        }
        boolean wanted = checkItemDesired(h, item);
        Boolean choice = null;
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
                // random? check wanted by others
            }
        }

    }

    private static List<DC_HeroObj> checkItemDesired(MacroParty party,
                                                     Entity item) {
        return null;
    }

    private static boolean checkItemDesired(DC_HeroObj h, Entity item) {
        // TODO h.getProperty( WANTED_ITEM_TYPES)
//		for (String s :  StringMaster.openContainer(string )){
//			prop = wanted.split()[0];
//		}
        item.getProperty(G_PROPS.WEAPON_CLASS);
        return false;
    }

    private static int awardGold(int lootValue, DC_HeroObj h) {
        h.getIntParam(MACRO_PARAMS.C_GOLD_SHARE);

        return 0;
    }

    private static int awardXp(DequeImpl<DC_HeroObj> enemiesSlain,
                               DC_HeroObj her, PartyObj party) {
        for (DC_HeroObj unit : enemiesSlain) {
            Obj killer = unit.getRef().getObj(KEYS.KILLER);
            for (DC_HeroObj h : party.getMembers()) {
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
        List<Entity> loot = new LinkedList<>();
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

    private static void awardLoot(ObjType type, DC_HeroObj h) {
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
                                          DequeImpl<DC_HeroObj> enemiesSlain) {
        int value = 0;
        for (DC_HeroObj h : enemiesSlain) {
            value += h.getIntParam(PARAMS.POWER);
        }
        return value;
    }

    public static ObjType generateLootItem(int value, LOOT_GROUP group) {
        Object[] filterValues = getFilteringValues(group);
        List<ObjType> list = DataManager.getTypes(getTYPE(group));
        for (Object val : filterValues) {
            PROPERTY prop = ContentManager.findPROP(val.getClass()
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
                return OBJ_TYPES.WEAPONS;
            case ARMOR:
                return OBJ_TYPES.ARMOR;
            case CONCONCTIONS:
                return OBJ_TYPES.ITEMS;
            case HEAVY_ARMOR:
                return OBJ_TYPES.ARMOR;
            case HEAVY_WEAPONS:
                return OBJ_TYPES.WEAPONS;
            case JEWELRY:
                return OBJ_TYPES.JEWELRY;
            case JEWELRY_SPECIAL:
                return OBJ_TYPES.JEWELRY;
            case LIGHT_ARMOR:
                return OBJ_TYPES.ARMOR;
            case LIGHT_WEAPONS:
                return OBJ_TYPES.WEAPONS;
            case MAGIC_ARMOR:
                return OBJ_TYPES.ARMOR;
            case MAGIC_ITEMS:
                return OBJ_TYPES.ITEMS;
            case MAGIC_MATERIAL_WEAPONS:
                return OBJ_TYPES.WEAPONS;
            case MAGIC_WEAPONS:
                return OBJ_TYPES.WEAPONS;
            case POISONS:
                return OBJ_TYPES.ITEMS;
            case POTIONS:
                return OBJ_TYPES.ITEMS;
            case WEAPONS:
                return OBJ_TYPES.WEAPONS;
            case WEIRD_ARMOR:
                return OBJ_TYPES.ARMOR;
            case WEIRD_ITEMS:
                return OBJ_TYPES.ITEMS;
            case WEIRD_WEAPONS:
                return OBJ_TYPES.WEAPONS;

        }
        return null;
    }
}
