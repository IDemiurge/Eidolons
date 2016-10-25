package main.game.logic.macro.town;

import main.content.CONTENT_CONSTS.MATERIAL;
import main.content.CONTENT_CONSTS.QUALITY_LEVEL;
import main.content.CONTENT_CONSTS2.SHOP_LEVEL;
import main.content.CONTENT_CONSTS2.SHOP_MODIFIER;
import main.system.math.MathMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ShopMaster {

    public static int getMaxItemsPerGroup(Shop shop) {
        // special shops less
        return 25;
    }

    public static Integer getBaseGoldIncome(Shop shop) {
        return 50;
    }

    public static Integer getBaseGoldCostMod(Shop shop) {
        return 50;
    }

    public static Integer getBaseGold(Shop shop) {
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

    public static List<QUALITY_LEVEL> getQualityLevels(Shop shop) {
        List<QUALITY_LEVEL> list = new LinkedList<>();
        switch (shop.getShopLevel()) {
            case COMMON:
                list.add(QUALITY_LEVEL.INFERIOR);
                list.add(QUALITY_LEVEL.OLD);
                list.add(QUALITY_LEVEL.NORMAL);
                break;
            case OPULENT:
                list.add(QUALITY_LEVEL.SUPERB);
                list.add(QUALITY_LEVEL.MASTERPIECE);
                break;
            case POOR:
                list.add(QUALITY_LEVEL.INFERIOR);
                list.add(QUALITY_LEVEL.DAMAGED);
                list.add(QUALITY_LEVEL.OLD);
                break;
            case QUALITY:
                list.add(QUALITY_LEVEL.SUPERIOR);
                list.add(QUALITY_LEVEL.NORMAL);
        }
        switch (shop.getShopModifier()) {
            case UNDERGROUND:
                list.add(QUALITY_LEVEL.ANCIENT);
        }
        switch (shop.getShopType()) {
            case BLACK_MARKET:
                list.add(QUALITY_LEVEL.ANCIENT);
        }
        return list;
    }

    public static boolean checkMaterialAllowed(Shop shop, MATERIAL material) {
        if (!ShopMaster.getMaterialsForShopLevel(shop.getShopLevel()).contains(
                material))
            if (!ShopMaster.getMaterialsForShopModifier(shop.getShopModifier())
                    .contains(material))
                if (!getSpecialMaterials(shop).contains(material))
                    return false;

        return true;
    }

    private static List<MATERIAL> getSpecialMaterials(Shop shop) {
        List<MATERIAL> list = new LinkedList<>();
        return list;
    }

    public static List<MATERIAL> getMaterialsForShopModifier(
            SHOP_MODIFIER shopModifier) {
        MATERIAL[] array = new MATERIAL[0];
        List<MATERIAL> list = new LinkedList<>();
        switch (shopModifier) {
            case DARK:
                list.add(MATERIAL.WRAITH_STEEL);
                list.add(MATERIAL.WARP_STEEL);
                list.add(MATERIAL.BILEWOOD);
                list.add(MATERIAL.PALE_STEEL);
                list.add(MATERIAL.DARK_STEEL);
                list.add(MATERIAL.DEMON_STEEL);
                list.add(MATERIAL.SOULSTONE);
                list.add(MATERIAL.MAN_BONE);
                list.add(MATERIAL.TROLL_SKIN);
                break;
            case DWARVEN:
                array = new MATERIAL[]{MATERIAL.IRON, MATERIAL.IRONWOOD,
                        MATERIAL.STEEL, MATERIAL.ADAMANTIUM, MATERIAL.MITHRIL,
                        MATERIAL.METEORITE, MATERIAL.DRAGONHIDE,};
                list.add(MATERIAL.DRAGON_BONE);
                break;
            case ELVEN:
                array = new MATERIAL[]{MATERIAL.PALEWOOD, MATERIAL.MITHRIL,
                        MATERIAL.MOON_SILVER, MATERIAL.BRONZE,
                        MATERIAL.FEYWOOD, MATERIAL.LIZARD_SKIN,};
                break;
            case HOLY:
                list.add(MATERIAL.BRIGHT_STEEL);
                list.add(MATERIAL.MOON_SILVER);
                break;
            case HUMAN:
                array = new MATERIAL[]{MATERIAL.IRON, MATERIAL.IRONWOOD,
                        MATERIAL.STEEL, MATERIAL.BRASS, MATERIAL.THICK_LEATHER,};
                break;
            case UNDERGROUND:
                list.add(MATERIAL.WAILWOOD);
                list.add(MATERIAL.SOULSTONE);
                list.add(MATERIAL.MAN_BONE);
                list.add(MATERIAL.TROLL_SKIN);
                list.add(MATERIAL.DARK_STEEL);
                list.add(MATERIAL.DRAGON_BONE);
                break;
            case WIZARDING:
                list.add(MATERIAL.STAR_EMBER);
                list.add(MATERIAL.CRYSTAL);
                list.add(MATERIAL.ELDRITCH_STEEL);
                list.add(MATERIAL.FEYWOOD);
                list.add(MATERIAL.DRAGON_BONE);
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
        for (MATERIAL m : MATERIAL.values()) {
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
            if (material_level <= level)
                list.add(m);
        }
        return list;
    }

}
