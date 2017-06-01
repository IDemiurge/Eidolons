package main.game.module.adventure.town;

import main.content.CONTENT_CONSTS2.SHOP_LEVEL;
import main.content.CONTENT_CONSTS2.SHOP_MODIFIER;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.game.battlecraft.logic.meta.scenario.hq.ShopInterface;
import main.system.math.MathMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ShopMaster {

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
                gold = MathMaster.addFactor(gold, 25);
                break;
            case DWARVEN:
                gold = MathMaster.addFactor(gold, 35);
                break;
            case UNDERGROUND:
                gold = MathMaster.addFactor(gold, -15);
                break;
            case WIZARDING:
                gold = MathMaster.addFactor(gold, 25);
                break;
            default:
                break;
        }
        return gold;
    }

    public static List<QUALITY_LEVEL> getQualityLevels(ShopInterface shop) {
        List<QUALITY_LEVEL> list = new LinkedList<>();
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
                if (!getSpecialMaterials(shop).contains(material)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static List<MATERIAL> getSpecialMaterials(ShopInterface shop) {
        List<MATERIAL> list = new LinkedList<>();
        return list;
    }

    public static List<MATERIAL> getMaterialsForShopModifier(
            SHOP_MODIFIER shopModifier) {
        MATERIAL[] array = new MATERIAL[0];
        List<MATERIAL> list = new LinkedList<>();
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
        List<MATERIAL> list = new LinkedList<>();
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

}
