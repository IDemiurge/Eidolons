package main.ability;

import main.client.cc.HeroManager;
import main.content.CONTENT_CONSTS2.SHOP_LEVEL;
import main.content.*;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_JewelryObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.ItemFactory;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.module.adventure.town.ShopMaster;
import main.system.DC_Formulas;
import main.system.SortMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.WeightMap;
import main.system.math.Formula;
import main.system.math.MathMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class UnitShop {

    private static final String DEFAULT_QUALITY_RANGE = "Inferior;Superior";
    private static final Integer GOLD_COMPENSATION = 35;
    private static int goldPercentageToSpend;
    private static int goldOriginalAmount;
    private static Unit unit;
    private static HeroManager heroManager;

    // MATERIAL[] DEFAULT_MATERIALS_1 = {
    // };

    // TODO quick items - ammunition, poisons, even potions!
    public static void awardGold(Unit unit) {
        int gold = DC_Formulas.getGoldForLevel(unit.getIntParam(PARAMS.LEVEL));

        Integer mod = unit.getIntParam(PARAMS.GOLD_MOD);
        if (mod > 0 && mod < 100) {
            mod += Math.min(100, mod + mod * GOLD_COMPENSATION / 100);
        } else if (mod == 100) {
            mod += GOLD_COMPENSATION / 2;
        }

        gold = MathMaster.applyMod(gold, mod);
        unit.setParam(PARAMS.GOLD, gold);
        goldOriginalAmount = gold;
    }

    public static void buyItemsForUnit(Unit shopper) {
        unit = shopper;
        awardGold(unit);

        goldPercentageToSpend = unit.getIntParam(PARAMS.JEWELRY_GOLD_PERCENTAGE);
        if (goldPercentageToSpend > 0) {
            buyJewelry(); // amulet and rings separately ; how to define which
        }
        // traits unit prefers?
        goldPercentageToSpend = unit.getIntParam(PARAMS.QUICK_ITEM_GOLD_PERCENTAGE);
        if (goldPercentageToSpend > 0) {
            buyQuickItems(); // quick slots; quality range - for potions,
        }
        // elixirs, conc., *weapons*?

        Integer armorGoldPercentage = unit.getIntParam(PARAMS.ARMOR_GOLD_PERCENTAGE);
        if (StringMaster.isEmpty(unit.getProperty(PROPS.ARMOR_REPERTOIRE))) {
            armorGoldPercentage = 0;
        }
        goldPercentageToSpend = 100 - armorGoldPercentage;
        if (!StringMaster.isEmpty(unit.getProperty(PROPS.OFF_HAND_REPERTOIRE))) {
            goldPercentageToSpend = MathMaster.applyMod(goldPercentageToSpend, unit
                    .getIntParam(PARAMS.MAIN_HAND_GOLD_PERCENTAGE));
        }
        if (StringMaster.isEmpty(unit.getProperty(PROPS.MAIN_HAND_REPERTOIRE))) {
            generateWeaponRepertoire(shopper, false);
        }
        if (!buy(unit.getProperty(PROPS.MAIN_HAND_REPERTOIRE), unit, ItemEnums.ITEM_SLOT.MAIN_HAND,
                DC_TYPE.WEAPONS)) { // make sure main
            // hand item is
            // bought, maybe not
            // for tanks...
            goldPercentageToSpend = 100 - armorGoldPercentage;
            if (!buy(unit.getProperty(PROPS.MAIN_HAND_REPERTOIRE), unit, ItemEnums.ITEM_SLOT.MAIN_HAND,
                    DC_TYPE.WEAPONS)) {
                goldPercentageToSpend = 100;
                buy(unit.getProperty(PROPS.MAIN_HAND_REPERTOIRE), unit, ItemEnums.ITEM_SLOT.MAIN_HAND,
                        DC_TYPE.WEAPONS);
                return;
            }
        }

        goldPercentageToSpend = armorGoldPercentage;
        if (StringMaster.isEmpty(unit.getProperty(PROPS.ARMOR_REPERTOIRE))) {
            generateArmorRepertoire(shopper);
        }
        buy(unit.getProperty(PROPS.ARMOR_REPERTOIRE), unit, ItemEnums.ITEM_SLOT.ARMOR, DC_TYPE.ARMOR);

        goldPercentageToSpend = 100;
        if (StringMaster.isEmpty(unit.getProperty(PROPS.OFF_HAND_REPERTOIRE))) {
            generateWeaponRepertoire(shopper, true);
        }
        buy(unit.getProperty(PROPS.OFF_HAND_REPERTOIRE), unit, ItemEnums.ITEM_SLOT.OFF_HAND,
                DC_TYPE.WEAPONS);
    }

    private static void generateWeaponRepertoire(Unit hero, boolean offhand) {
        // TODO just add per mastery plus magical if caster...
        // let the weights be determined by weight vs strength!
        // common classes first, then as per offhand
        if (!offhand) {
            // ParamMaster.getOrCreate
        } else {

        }

    }

    private static void generateArmorRepertoire(Unit hero) {
        // TODO
        /*
         * if has armorer... if isn't caster... preCheck robe per mastery... preCheck
		 * strength...
		 */

    }

    private static void buyQuickItems() {
        // TODO preCheck slots
        while (true) {
            if (unit.getRemainingQuickSlots() <= 0) {
                break;
            }
            if (!buy(unit.getProperty(PROPS.QUICK_ITEM_REPERTOIRE), unit, null, C_OBJ_TYPE.ITEMS)) {
                break;
            }
        }

    }

    private static void buyJewelry() {
        buyJewelry(PROPS.JEWELRY_ITEM_TRAIT_REPERTOIRE);

        if (checkGoldLimit()) {
            buyJewelry(PROPS.JEWELRY_PASSIVE_ENCHANTMENT_REPERTOIRE);
        }
        // buyJewelry(PROPS.JEWELRY_ATTR_TRAIT_REPERTOIRE);
    }

    private static boolean checkGoldLimit() {
        return unit.getIntParam(PARAMS.GOLD) * goldPercentageToSpend / 100 > goldOriginalAmount
                - unit.getIntParam(PARAMS.GOLD);
    }

    private static void buyJewelry(PROPS property) {
        String repertoire = ""; // TODO 1) sort types by plan and cost 2)
        // prioritize/preCheck amulet 3)
        // randomize
        String prop = unit.getProp(property.getName());
        // ++ attr jewelry ++ passive enchantment
        // quality level range?

        for (String trait : StringMaster.openContainer(prop)) {
            // DataManager.getTypesSubGroup(OBJ_TYPES.JEWELRY, subgroup);
            ObjType type = DataManager.findType(VariableManager.removeVarPart(trait),
                    DC_TYPE.JEWELRY);
            if (type != null)
            // preCheck what, exactly? quality range? proper match? (resistance
            // could be resistance penetration... TODO preCheck doesn't contain
            // other trait
            {
                repertoire += VariableManager.removeVarPart(type.getName())
                        + VariableManager.getVarPart(trait) + ";";
            }

        }
        if (!repertoire.isEmpty()) {
            while (true) {
                try {
                    if (!buy(repertoire, unit, null, DC_TYPE.JEWELRY)) {
                        return;
                    }
                    if (!checkGoldLimit()) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private static boolean buy(String repertoire, Unit unit, ITEM_SLOT slot,
                               OBJ_TYPE OBJ_TYPE_ENUM) {
        // Map<ObjType, Integer>
        List<ObjType> itemPool = new LinkedList<>();
        // ++ add weight! choose from repertoire!
        WeightMap<ObjType> map = new WeightMap<>(new RandomWizard<ObjType>()
                .constructWeightMap(repertoire, ObjType.class, OBJ_TYPE_ENUM));
        Loop.startLoop(map.size());
        while (!Loop.loopEnded() && !map.isEmpty()) {
            ObjType baseType = getItem(map);
            map.remove(baseType);
            if (baseType == null) {
                return false; // *empty*
            }

            for (ObjType type : DataManager.getTypes(OBJ_TYPE_ENUM)) {
                if (!checkItemType(type, baseType)) {
                    continue;
                }

                if (!checkCanEquip(baseType, unit, slot)) {
                    continue;
                }

                if (!specialCheck(unit, type)) {
                    continue;
                }
                // TODO for potions/jewelry?
                if (!checkQualityRange(type, unit)) // for potions/ammo?
                {
                    continue;
                }
                itemPool.add(type);
            }
            itemPool = (List<ObjType>) SortMaster.sortByValue(itemPool, PARAMS.GOLD_COST, true);
            DC_HeroItemObj item = null;
            for (ObjType type : itemPool) {
                // sort by cost? then go from top to bottom trying to buy...
                if (!checkCost(type, unit)) {
                    continue;
                }
                item = buy(type, unit);
                break;
            }
            if (item == null) {
                continue;
            }
            equip(unit, item, slot);
            return true;
        }
        return false;
        // ++ sell TODO
    }

    private static ObjType getItem(WeightMap<ObjType> map) {
        ObjType baseType;
        if (UnitTrainingMaster.isRandom()) {
            baseType = new RandomWizard<ObjType>().getObjectByWeight(map);
        } else {
            baseType = (map).getGreatest();
        }
        return baseType;
    }

    private static boolean specialCheck(Unit unit, ObjType type) {
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.JEWELRY) {
            // TODO
            return true;
        }
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.ITEMS) {
            // TODO
            return true;
        }
        String property = unit.getProperty(PROPS.ALLOWED_MATERIAL);
        if (property.isEmpty()) {
            SHOP_LEVEL shopLevel = SHOP_LEVEL.COMMON;
            if (unit.getLevel() > 5) {
                shopLevel = SHOP_LEVEL.QUALITY;
            }
            if (unit.getLevel() > 8) {
                shopLevel = SHOP_LEVEL.OPULENT;
            }
            if (unit.getLevel() < 3) {
                shopLevel = SHOP_LEVEL.POOR;
            }
            List<MATERIAL> levelMaterials = ShopMaster.getMaterialsForShopLevel(shopLevel);
            property = StringMaster.constructStringContainer(levelMaterials);
        }
        return StringMaster.compare(type.getProperty(G_PROPS.MATERIAL), property, false);
    }

    private static void equip(Unit unit, DC_HeroItemObj item, ITEM_SLOT slot) {
        if (slot != null) {
            if (!unit.equip(item, slot)) {
                LogMaster.log(1, unit.getName() + " failed to equip "
                        + item.getName());
            }
        } else {
            if (item instanceof DC_JewelryObj) {
                unit.addJewelryItem((DC_JewelryObj) item);
            } else {
                if (item instanceof DC_QuickItemObj) {
                    unit.getQuickItems().add((DC_QuickItemObj) item);
                } else {
                    DC_HeroItemObj itemObj = ItemFactory.createItemObj(item.getType(), unit
                            .getOriginalOwner(), unit.getGame(), unit.getRef(), true);
                    unit.getQuickItems().add((DC_QuickItemObj) itemObj);
                }
            }
        }

    }

    private static boolean checkCanEquip(ObjType type, Unit unit, ITEM_SLOT slot) {
        if (slot == null) {
            if (type.getOBJ_TYPE_ENUM() == DC_TYPE.JEWELRY) {
                return getHeroManager().checkCanEquipJewelry(unit, type);
            } else {
                // return !unit.isQuickSlotsFull(); just don't

            }
        }
        return true;
    }

    private static HeroManager getHeroManager() {
        if (heroManager == null) {
            heroManager = new HeroManager(DC_Game.game);
        }
        heroManager.setTrainer(true);
        return heroManager;
    }

    private static DC_HeroItemObj buy(ObjType type, Unit unit) {
        unit.modifyParameter(PARAMS.GOLD, -type.getIntParam(PARAMS.GOLD_COST));
        return ItemFactory.createItemObj(type, unit.getOwner(), unit.getGame(), unit.getRef(),
                false);

    }

    private static boolean checkCost(ObjType type, Unit unit) {
        int cost = new Formula(HeroManager.getCost(type, unit)).getInt(unit.getRef());
        return unit.checkParam(PARAMS.GOLD, cost + "*100/" + goldPercentageToSpend);
    }

    private static boolean checkItemType(ObjType type, ObjType baseType) {
        if (baseType.getOBJ_TYPE_ENUM() == DC_TYPE.JEWELRY) {
            boolean result = StringMaster.compareByChar(baseType
                    .getProperty(PROPS.MAGICAL_ITEM_TRAIT), type
                    .getProperty(PROPS.MAGICAL_ITEM_TRAIT));
            if (baseType.getProperty(PROPS.MAGICAL_ITEM_TRAIT).isEmpty()) {
                result = false;
            }
            if (!baseType.getProperty(PROPS.JEWELRY_PASSIVE_ENCHANTMENT).isEmpty()) {
                result = StringMaster.compareByChar(baseType
                        .getProperty(PROPS.JEWELRY_PASSIVE_ENCHANTMENT), type
                        .getProperty(PROPS.JEWELRY_PASSIVE_ENCHANTMENT));
            }
            if (!result) {
                return false;
            }
            return StringMaster.compareByChar(baseType.getType().getName(), type.getType()
                    .getName());

        }
        return (StringMaster.compareByChar(type.getProperty(G_PROPS.BASE_TYPE), baseType.getName(),
                true));

    }

    private static boolean checkQualityRange(ObjType type, Unit unit) {
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.JEWELRY) {
            return true;
        }
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.ITEMS) {
            return true;
        }
        String itemProperty = type.getProperty(G_PROPS.QUALITY_LEVEL);
        String property = unit.getProperty(PROPS.QUALITY_LEVEL_RANGE);
        if (property.isEmpty()) {
            property = DEFAULT_QUALITY_RANGE;
        }
        if (!property.contains(StringMaster.CONTAINER_SEPARATOR)) {
            return property.equalsIgnoreCase(itemProperty);
        }
        QUALITY_LEVEL quality = ItemEnums.QUALITY_LEVEL.valueOf(StringMaster.getEnumFormat(itemProperty));
        int index = Arrays.asList(ItemEnums.QUALITY_LEVEL.values()).indexOf(quality);

        List<String> range = StringMaster.openContainer(property);

        int min = Arrays.asList(ItemEnums.QUALITY_LEVEL.values()).indexOf(
                ItemEnums.QUALITY_LEVEL.valueOf(StringMaster.getEnumFormat(range.get(0))));
        int max = -1;

        try {
            max = Arrays.asList(ItemEnums.QUALITY_LEVEL.values()).indexOf(
                    ItemEnums.QUALITY_LEVEL.valueOf(StringMaster.getEnumFormat(range.get(1))));
        } catch (Exception e) {

        }
        if (max == -1) {
            max = min;
        }

        return !(index < min || index > max);
    }
}
