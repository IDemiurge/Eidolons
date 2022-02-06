package eidolons.game.battlecraft.logic.meta.universal.shop;

import eidolons.game.battlecraft.logic.meta.scenario.hq.ShopInterface;
import eidolons.entity.mngr.item.ItemGenerator;
import eidolons.entity.mngr.item.ItemMaster;
import main.content.CONTENT_CONSTS2.SHOP_LEVEL;
import main.content.CONTENT_CONSTS2.SHOP_MODIFIER;
import main.content.CONTENT_CONSTS2.SHOP_TYPE;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.elements.conditions.PropCondition;
import main.entity.type.ObjType;
import main.system.auxiliary.RandomWizard;
import main.system.entity.FilterMaster;
import main.system.math.MathMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShopMaster {

    public static final int MAX_ITEM_GROUPS = 4;

    public static int getMaxItemsPerGroup(ShopInterface shop) {
        // special shops less
        return 25;
    }

    public static Integer getBaseGoldIncome(ShopInterface shop) {
        return 50;
    }

    public static Integer getBaseGoldCostMod(ShopInterface shop) {
        return 50;
    }

    public static Integer getBaseGold(ShopInterface shop) {
        Integer gold = 750; // ++ from town?! from time?
        switch (shop.getShopType()) {
            case HEAVY_WEAPONS:
                gold = MathMaster.addFactor(gold, 25);
        }
        switch (shop.getShopLevel()) {
            case OPULENT:
                gold += 1500;
                break;
            case POOR:
                gold -= 250;
                break;
            case QUALITY:
                gold += 750;
                break;
        }
        switch (shop.getShopModifier()) {
            case DARK:
            case WIZARDING:
                gold = MathMaster.addFactor(gold, 25);
                break;
            case DWARVEN:
                gold = MathMaster.addFactor(gold, 35);
                break;
            case UNDERGROUND:
                gold = MathMaster.addFactor(gold, -15);
                break;
            default:
                break;
        }
        return gold;
    }

    public static List<QUALITY_LEVEL> getQualityLevels(ShopInterface shop) {
        List<QUALITY_LEVEL> list = new ArrayList<>();
        switch (shop.getShopLevel()) {
            case COMMON:
                list.add(ItemEnums.QUALITY_LEVEL.INFERIOR);
                list.add(ItemEnums.QUALITY_LEVEL.OLD);
                list.add(ItemEnums.QUALITY_LEVEL.NORMAL);
                break;
            case OPULENT:
                list.add(ItemEnums.QUALITY_LEVEL.SUPERB);
                list.add(ItemEnums.QUALITY_LEVEL.MASTERPIECE);
                break;
            case POOR:
                list.add(ItemEnums.QUALITY_LEVEL.INFERIOR);
                list.add(ItemEnums.QUALITY_LEVEL.DAMAGED);
                list.add(ItemEnums.QUALITY_LEVEL.OLD);
                break;
            case QUALITY:
                list.add(ItemEnums.QUALITY_LEVEL.SUPERIOR);
                list.add(ItemEnums.QUALITY_LEVEL.NORMAL);
        }
        switch (shop.getShopModifier()) {
            case UNDERGROUND:
                list.add(ItemEnums.QUALITY_LEVEL.ANCIENT);
        }
        switch (shop.getShopType()) {
            case BLACK_MARKET:
                list.add(ItemEnums.QUALITY_LEVEL.ANCIENT);
        }
        return list;
    }

    public static boolean checkMaterialAllowed(ShopInterface shop, MATERIAL material) {
        if (!ShopMaster.getMaterialsForShopLevel(shop.getShopLevel()).contains(
                material)) {
            if (!ShopMaster.getMaterialsForShopModifier(shop.getShopModifier())
                    .contains(material)) {
                return getSpecialMaterials(shop).contains(material);
            }
        }

        return true;
    }

    private static List<MATERIAL> getSpecialMaterials(ShopInterface shop) {
        return new ArrayList<>();
    }

    public static List<MATERIAL> getMaterialsForShopModifier(
            SHOP_MODIFIER shopModifier) {
        MATERIAL[] array = new MATERIAL[0];
        List<MATERIAL> list = new ArrayList<>();
        switch (shopModifier) {
            case DARK:
                list.add(ItemEnums.MATERIAL.WRAITH_STEEL);
                list.add(ItemEnums.MATERIAL.WARP_STEEL);
                list.add(ItemEnums.MATERIAL.BILEWOOD);
                list.add(ItemEnums.MATERIAL.PALE_STEEL);
                list.add(ItemEnums.MATERIAL.DARK_STEEL);
                list.add(ItemEnums.MATERIAL.DEMON_STEEL);
                list.add(ItemEnums.MATERIAL.SOULSTONE);
                list.add(ItemEnums.MATERIAL.MAN_BONE);
                list.add(ItemEnums.MATERIAL.TROLL_SKIN);
                break;
            case DWARVEN:
                array = new MATERIAL[]{ItemEnums.MATERIAL.IRON, ItemEnums.MATERIAL.IRONWOOD,
                        ItemEnums.MATERIAL.STEEL, ItemEnums.MATERIAL.ADAMANTIUM, ItemEnums.MATERIAL.MITHRIL,
                        ItemEnums.MATERIAL.METEORITE, ItemEnums.MATERIAL.DRAGONHIDE,};
                list.add(ItemEnums.MATERIAL.DRAGON_BONE);
                break;
            case ELVEN:
                array = new MATERIAL[]{ItemEnums.MATERIAL.PALEWOOD, ItemEnums.MATERIAL.MITHRIL,
                        ItemEnums.MATERIAL.MOON_SILVER, ItemEnums.MATERIAL.BRONZE,
                        ItemEnums.MATERIAL.FEYWOOD, ItemEnums.MATERIAL.LIZARD_SKIN,};
                break;
            case HOLY:
                list.add(ItemEnums.MATERIAL.BRIGHT_STEEL);
                list.add(ItemEnums.MATERIAL.MOON_SILVER);
                break;
            case HUMAN:
                array = new MATERIAL[]{ItemEnums.MATERIAL.IRON, ItemEnums.MATERIAL.IRONWOOD,
                        ItemEnums.MATERIAL.STEEL, ItemEnums.MATERIAL.BRASS, ItemEnums.MATERIAL.THICK_LEATHER,};
                break;
            case UNDERGROUND:
                list.add(ItemEnums.MATERIAL.WAILWOOD);
                list.add(ItemEnums.MATERIAL.SOULSTONE);
                list.add(ItemEnums.MATERIAL.MAN_BONE);
                list.add(ItemEnums.MATERIAL.TROLL_SKIN);
                list.add(ItemEnums.MATERIAL.DARK_STEEL);
                list.add(ItemEnums.MATERIAL.DRAGON_BONE);
                break;
            case WIZARDING:
                list.add(ItemEnums.MATERIAL.STAR_EMBER);
                list.add(ItemEnums.MATERIAL.CRYSTAL);
                list.add(ItemEnums.MATERIAL.ELDRITCH_STEEL);
                list.add(ItemEnums.MATERIAL.FEYWOOD);
                list.add(ItemEnums.MATERIAL.DRAGON_BONE);
                break;
        }
        list.addAll(Arrays.asList(array));
        return list;
    }

    public static List<MATERIAL> getMaterialsForShopLevel(SHOP_LEVEL shopLevel) {
        int level = 0;
        switch (shopLevel) {
            case POOR:
                break;
            case COMMON:
                level = 1;
                break;
            case QUALITY:
                level = 2;
                break;
            case OPULENT:
                level = 3;
                break;

        }
        List<MATERIAL> list = new ArrayList<>();
        for (MATERIAL m : ItemEnums.MATERIAL.values()) {
            int material_level = 1;
            switch (m) {
                case CRYSTAL:
                case LIZARD_SKIN:
                case STEEL:
                case SILVER:
                case SILK:
                case BLACKWOOD:
                case BLACK_BONE:
                    material_level = 2;
                    break;
                case THIN_LEATHER:
                case COPPER:
                case COTTON:
                case RED_OAK:
                case ONYX:
                    material_level = 0;
                    break;

                case TROLL_SKIN:
                case ADAMANTIUM:
                case BILEWOOD:
                case BRIGHT_STEEL:
                case DARK_STEEL:
                case DEFILED_STEEL:
                case DEMON_STEEL:
                case DRAGONHIDE:
                case DRAGON_BONE:
                case ELDRITCH_STEEL:
                case FEYWOOD:
                case MAN_BONE:
                case METEORITE:
                case MITHRIL:
                case MOON_SILVER:
                case PALEWOOD:
                case PALE_STEEL:
                case PLATINUM:
                case WAILWOOD:
                case WARP_STEEL:
                case WRAITH_STEEL:
                case STAR_EMBER:
                case SOULSTONE:
                    material_level = 3;
                    break;
                default:
                    break;

            }
            if (material_level <= level) {
                list.add(m);
            }
        }
        return list;
    }

    protected static List<ObjType> createItemPool(Shop shop,
                                                  OBJ_TYPE itemsType, List<ObjType> basis, PROPERTY prop, String group) {
        List<ObjType> pool;
        if (itemsType == DC_TYPE.JEWELRY) {
            pool = basis;
        } else if (prop == null) {
            pool = DataManager.toTypeList(DataManager
                            .getTypesSubGroupNames(C_OBJ_TYPE.ITEMS, group),
                    C_OBJ_TYPE.ITEMS);
        } else {
            pool = new ArrayList<>(basis);
            try {
                FilterMaster.filter(pool, new PropCondition(prop, group));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        pool = constructPool(pool, shop);
//        pool.addAll(getSpecialItems(group));
        return pool;
    }

    public static List<ObjType> constructPool(List<ObjType> pool, Shop shop) {
        // TODO AND WHAT ABOUT NON-SLOT ITEMS?
        List<ObjType> filtered = new ArrayList<>();
        for (ObjType t : pool) {
            if (t.getOBJ_TYPE_ENUM() == DC_TYPE.JEWELRY || t.getOBJ_TYPE_ENUM() == DC_TYPE.ITEMS) {
                filtered.add(t);
            } else
                for (MATERIAL material : MATERIAL.values()) {
                    //TODO TRAITS
                    if (checkMaterialAllowed(shop, material)) {
                        if (!ItemMaster.checkMaterial(t, material.getGroup())) {
                            continue;
                        }
                        if (!rollMaterial(material, shop))
                            continue;
                        for (QUALITY_LEVEL q : getQualityLevels(shop)) {
                            if (!rollQuality(q, shop))
                                continue;
                            filtered.add(ItemGenerator.getOrCreateItemType(t,
                                    material, q));
                            //less chance if already in repertoire!
                        }
                    }

                }
        }
//        repertoire.addAll(filtered);
        // getShopLevel().getMaxCostFactor();

        return filtered;
    }

    private static boolean rollQuality(QUALITY_LEVEL q, Shop shop) {
        int chance = MathMaster.getMinMax(20 + shop.getGold() / shop.getIntParam(MACRO_PARAMS.SHOP_INCOME)
                , 33, 50);
        return RandomWizard.chance(chance);
    }

    private static boolean rollMaterial(MATERIAL material, Shop shop) {
        int chance = MathMaster.getMinMax(25 + shop.getGold() / shop.getIntParam(MACRO_PARAMS.SHOP_INCOME)
                , 42, 63);
        return RandomWizard.chance(chance);
    }

    public static String[] initItemGroups(SHOP_TYPE type) {
        // Up to 4 item groups!
        String[] item_groups = type.getItemGroups();
        if (item_groups.length > MAX_ITEM_GROUPS) {
            List<String> list = new ArrayList<>(Arrays.asList(item_groups));
            item_groups = new String[MAX_ITEM_GROUPS];
            int j = 0;
            while (j < MAX_ITEM_GROUPS) {
                String e = list.get(RandomWizard.getRandomIndex(list));
                list.remove(e);
                item_groups[j] = e;
                j++;
            }
        }
        return item_groups;
    }

    public static OBJ_TYPE getItemsType(SHOP_TYPE shopType) {

        switch (shopType) {
            case JEWELER:
                return DC_TYPE.JEWELRY;
            case ALCHEMIST:
                return DC_TYPE.ITEMS;
            case WEAPONS:
            case LIGHT_WEAPONS:
            case HEAVY_WEAPONS:
                return DC_TYPE.WEAPONS;
            case ARMOR:
            case LIGHT_ARMOR:
            case HEAVY_ARMOR:
                return DC_TYPE.ARMOR;
        }
        return C_OBJ_TYPE.ITEMS;
    }
}
