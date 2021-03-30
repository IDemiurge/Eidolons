package eidolons.game.module.herocreator.logic.items;

import eidolons.content.DC_CONSTS.*;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.game.EidolonsGame;
import eidolons.system.utils.content.ContentGenerator;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.*;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.ConcurrentMap;
import main.data.DataManager;
import main.data.GenericItemGenerator;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.log.Chronos;
import main.system.images.ImageManager;
import main.system.launch.Flags;
import main.system.math.MathMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemGenerator implements GenericItemGenerator {

    public static final MATERIAL[] DEFAULT_MATERIALS_STONE = {ItemEnums.MATERIAL.ONYX, ItemEnums.MATERIAL.OBSIDIAN,
     ItemEnums.MATERIAL.CRYSTAL, ItemEnums.MATERIAL.SOULSTONE, ItemEnums.MATERIAL.STAR_EMBER};
    public static final MATERIAL[] BASIC_MATERIALS_STONE = {ItemEnums.MATERIAL.ONYX, ItemEnums.MATERIAL.OBSIDIAN,};
    public static final MATERIAL[] DEFAULT_MATERIALS_WOOD = {ItemEnums.MATERIAL.RED_OAK, ItemEnums.MATERIAL.IRONWOOD,
     ItemEnums.MATERIAL.BLACKWOOD, ItemEnums.MATERIAL.PALEWOOD, ItemEnums.MATERIAL.WAILWOOD};
    public static final MATERIAL[] BASIC_MATERIALS_WOOD = {ItemEnums.MATERIAL.RED_OAK, ItemEnums.MATERIAL.IRONWOOD,
     ItemEnums.MATERIAL.BLACKWOOD, ItemEnums.MATERIAL.PALEWOOD};
    public static final MATERIAL[] BASIC_MATERIALS_METALS = {ItemEnums.MATERIAL.COPPER, ItemEnums.MATERIAL.BRASS,
     ItemEnums.MATERIAL.BRONZE, ItemEnums.MATERIAL.IRON, ItemEnums.MATERIAL.STEEL, ItemEnums.MATERIAL.MITHRIL};
    public static final MATERIAL[] DEFAULT_MATERIALS_METALS = {ItemEnums.MATERIAL.COPPER, ItemEnums.MATERIAL.BRASS,
     ItemEnums.MATERIAL.BRONZE, ItemEnums.MATERIAL.IRON, ItemEnums.MATERIAL.STEEL, ItemEnums.MATERIAL.MITHRIL, ItemEnums.MATERIAL.PLATINUM,
     ItemEnums.MATERIAL.DARK_STEEL, ItemEnums.MATERIAL.WARP_STEEL, ItemEnums.MATERIAL.PALE_STEEL, ItemEnums.MATERIAL.WRAITH_STEEL,
     ItemEnums.MATERIAL.ADAMANTIUM, ItemEnums.MATERIAL.METEORITE, ItemEnums.MATERIAL.MOON_SILVER, ItemEnums.MATERIAL.ELDRITCH_STEEL,
     ItemEnums.MATERIAL.BRIGHT_STEEL, ItemEnums.MATERIAL.DEFILED_STEEL

    };
    public static final MATERIAL[] DEFAULT_MATERIALS_CLOTH = {ItemEnums.MATERIAL.COTTON, ItemEnums.MATERIAL.SILK,};
    public static final MATERIAL[] DEFAULT_MATERIALS_BONES = {ItemEnums.MATERIAL.IVORY, ItemEnums.MATERIAL.BLACK_BONE,
     ItemEnums.MATERIAL.MAN_BONE, ItemEnums.MATERIAL.DRAGON_BONE};
    public static final MATERIAL[] BASIC_MATERIALS_BONES = {ItemEnums.MATERIAL.IVORY, ItemEnums.MATERIAL.BLACK_BONE,};


    public static final MATERIAL[] DEFAULT_MATERIALS_NATURAL = {ItemEnums.MATERIAL.PUNY, ItemEnums.MATERIAL.PETTY,
     ItemEnums.MATERIAL.AVERAGE, ItemEnums.MATERIAL.SIZABLE, ItemEnums.MATERIAL.DIRE, ItemEnums.MATERIAL.FEARSOME,
     ItemEnums.MATERIAL.MONSTROUS};
    public static final MATERIAL[] BASIC_MATERIALS_NATURAL = {ItemEnums.MATERIAL.PUNY, ItemEnums.MATERIAL.PETTY,
     ItemEnums.MATERIAL.AVERAGE, ItemEnums.MATERIAL.SIZABLE, ItemEnums.MATERIAL.DIRE,};
    public static final MATERIAL[] DEFAULT_MATERIALS_SKINS = {ItemEnums.MATERIAL.THIN_LEATHER,
     ItemEnums.MATERIAL.TOUGH_LEATHER, ItemEnums.MATERIAL.THICK_LEATHER, ItemEnums.MATERIAL.LIZARD_SKIN,
     ItemEnums.MATERIAL.DRAGONHIDE, ItemEnums.MATERIAL.TROLL_SKIN, ItemEnums.MATERIAL.FUR};
    public static final MATERIAL[] BASIC_MATERIALS_LEATHER = {ItemEnums.MATERIAL.THIN_LEATHER,
     ItemEnums.MATERIAL.TOUGH_LEATHER, ItemEnums.MATERIAL.THICK_LEATHER,};
    public static final PARAMS[] WEAPON_PARAMS = {PARAMS.DAMAGE_BONUS, PARAMS.DIE_SIZE,
     PARAMS.DURABILITY};
    public static final PARAMS[] WEAPON_MOD_PARAMS = {PARAMS.BASE_DAMAGE_MODIFIER, PARAMS.DICE,
     PARAMS.DURABILITY_MODIFIER};
    public static final PARAMS[] ARMOR_PARAMS = {PARAMS.ARMOR, PARAMS.RESISTANCE,
     PARAMS.DURABILITY};
    public static final PARAMS[] ARMOR_MOD_PARAMS = {PARAMS.ARMOR_MODIFIER,
     PARAMS.RESISTANCE_MODIFIER, PARAMS.DURABILITY_MODIFIER};
    public static final MATERIAL[] METAL_JEWELRY_MATERIALS = {ItemEnums.MATERIAL.COPPER, ItemEnums.MATERIAL.SILVER,
     ItemEnums.MATERIAL.GOLD,};
    public static final MATERIAL[] STONE_JEWELRY_MATERIALS = DEFAULT_MATERIALS_STONE;
    public static final MATERIAL[] BONE_JEWELRY_MATERIALS = DEFAULT_MATERIALS_BONES;
    // public static final String ATTR_JEWELRY = "Amulet;Ring";
    // public static final String MAGIC_MASTERY_JEWELRY = "Magic Ring";
    // public static final String WEAPON_MASTERY_JEWELRY = "War Ring";
    // public static final String MISC_MASTERY_JEWELRY = "Signet";
    // public static final String OTHER_TRAITS_JEWELRY = "Ring"; // letter
    // variants
    // public static final String CUSTOM_PARAM_JEWELRY = "Power Ring";
    public static final String PAS_JEWELRY = "Necklace;Necklace;";
    public static final String PAS_LEVEL_JEWELRY = "Pendant;";
    private static final int JEWELRY_COST_MODIFIER = 40;
    private static final int ITEM_COST_MODIFIER = -50;
    private static final int SPELL_DAMAGE_FACTOR = 10;
    private static final int ARMOR_MODIFIER = 5;
    private static ItemGenerator defaultGenerator;
    private static ItemGenerator basicGenerator;
    private static final Map<QUALITY_LEVEL, Map<MATERIAL, Map<ObjType, ObjType>>> itemMaps = new ConcurrentMap();
    private static final List<ObjType> baseWeaponTypes = new ArrayList<>();
    private static List<ObjType> baseWeaponTypesForShops;
    private static final List<ObjType> baseJewelryTypes = new ArrayList<>();
    private static final List<ObjType> baseItemTypes = new ArrayList<>();
    private static final List<ObjType> baseArmorTypes = new ArrayList<>();
    private static final List<ObjType> baseGarmentTypes = new ArrayList<>();
    private static boolean switcher = true;
    private static boolean basicMode = true;

    private static boolean jewelryGenerated;
    private static boolean usablesGenerated;

    public QUALITY_LEVEL[] defaultQualityLevels = ItemEnums.QUALITY_LEVEL.values();
    QUALITY_LEVEL[] qualityLevels;
    MATERIAL[] materials;
    private final boolean basic;
    private boolean preGenerate;

    public ItemGenerator(boolean basic) {
        this.basic = basic;
        defaultGenerator = this;
    }

    public static boolean isBasicMode() {
        return basicMode;
    }

    public static void setBasicMode(boolean basicMode) {
        ItemGenerator.basicMode = basicMode;
    }

    private static boolean isGenerationOn() {
        return switcher;
    }

    public static void setGenerationOn(boolean switcher) {
        ItemGenerator.switcher = switcher;
    }

    public static ObjType generateWeaponItem(QUALITY_LEVEL quality, MATERIAL material, ObjType type) {
        return ItemGenerator
         .generateItem(quality, material, type, WEAPON_PARAMS, WEAPON_MOD_PARAMS);
    }

    private static void generatePotions(ObjType type) {
        for (POTION_LEVEL lvl : POTION_LEVEL.values()) {
            ObjType newType = generateUsableItem(type, lvl.getName(), lvl.getImgCode(), lvl
             .getCost(), lvl.getQuality());
            newType.setParam(PARAMS.CHARGES, lvl.getCharges());
            // newType.setProp(PARAMS.POTION_LEVEL, lvl.getQuality());
        }
    }

    private static ObjType generateUsableItem(ObjType type, String prefix, String img,
                                              Integer cost, QUALITY_LEVEL quality) {
        ObjType newType = getNewType(type);
        String name = prefix + " " + type.getName();
        String imgPath = generateImgPath(img, newType);
        newType.setName(name);
        newType.setImage(imgPath);
        Integer baseCost = type.getIntParam(PARAMS.GOLD_COST);
        baseCost = baseCost * cost / 100;
        newType.setParam(PARAMS.GOLD_COST, baseCost);
        newType.modifyParamByPercent(PARAMS.GOLD_COST, ITEM_COST_MODIFIER);
        newType.setProperty(G_PROPS.QUALITY_LEVEL, quality.getName());
        DataManager.addType(newType);
        return newType;
    }

    private static void generateConcoctions(ObjType type) {
        for (CONCOCTION_LEVEL lvl : CONCOCTION_LEVEL.values()) {
            ObjType newType = generateUsableItem(type, lvl.getName(), lvl.getImgCode(), lvl
             .getCost(), lvl.getQuality());
            newType.setParam(PARAMS.ENERGY, lvl.getMod());
        }
    }

    private static MATERIAL[] getJewelryMaterials(String materialGroup) {
        switch (new EnumMaster<ITEM_MATERIAL_GROUP>().retrieveEnumConst(ITEM_MATERIAL_GROUP.class,
         materialGroup)) {
            case BONE:
                return BONE_JEWELRY_MATERIALS;
            case METAL:
                return METAL_JEWELRY_MATERIALS;
            case STONE:
                return STONE_JEWELRY_MATERIALS;
            default:
                break;
        }
        return null;
    }

    private static ObjType generateJewelryItem(boolean ring, ObjType type,
                                               JEWELRY_PASSIVE_ENCHANTMENT ench, ITEM_LEVEL level) {
        String value = ench.getValue(level);
        type.addProperty(ench.getProp(), value);
        String string = StringMaster.cropFormat(type.getImagePath());

        String suffix = ench.getIconVariantLetter();
        type.setProperty(PROPS.JEWELRY_PASSIVE_ENCHANTMENT, string);
        int costMod = ench.getCostBase();
        if (level != null) {
            if (level.getLevel() > 0) {
                suffix += (level.getLevel());
            }
            costMod = costMod * level.getCostFactor();
        }
        if (ImageManager.isImage(string + ench.getIconVariantLetter()
         +ImageManager.PNG)) {
            string += suffix;
        }
        string += ImageManager.DEFAULT_ENTITY_IMAGE_FORMAT;
        type.modifyParameter(PARAMS.GOLD_COST, costMod);
        type.setProperty(G_PROPS.IMAGE, string);
        string = generateName(ench, level, type);
        type.setProperty(G_PROPS.NAME, string);

        DataManager.addType(type.getName(), DC_TYPE.JEWELRY, type);
        return type;

    }

    private static ObjType generateJewelryItem(ObjType type, JEWELRY_ITEM_TRAIT trait,
                                               ITEM_LEVEL level) {
        boolean ring = ItemMaster.isRing(type);
        if (trait == null)
            return null;
        PARAMETER[] params = trait.getParams();
        PARAMETER p = params[0];
        ObjType newType = generateEmptyJewelryItem(ring, type, null);
        int amount = trait.getIntegers()[level.getLevel()]; // double for
        // amulets?
        if (!ring && trait.isDoubleAmulet()) {
            amount = amount * 2;
        }
        newType.setParam(p, amount);

        type.getGame().initType(newType);
        newType.setGenerated(true);
        String name = generateName(trait, level, type);
        newType.setProperty(G_PROPS.NAME, name);
        DataManager.addType(newType.getName(), DC_TYPE.JEWELRY, newType);

        // String img = type.getProperty(prop) + suffix + format;
        // newType.setProperty(G_PROPS.IMAGE, name);
        newType.setProperty(PROPS.MAGICAL_ITEM_TRAIT, trait.toString());
        newType.setProperty(PROPS.MAGICAL_ITEM_LEVEL, level.toString());
        int costMod = trait.getCostBase() * level.getCostFactor();
        if (!ring && trait.isDoubleAmulet()) {
            costMod = costMod * 5 / 2;
        }

        newType.modifyParameter(PARAMS.GOLD_COST, costMod);
        newType.modifyParamByPercent(PARAMS.GOLD_COST, JEWELRY_COST_MODIFIER);

        if (trait == JEWELRY_ITEM_TRAIT.ATTRIBUTE_BONUS) {
            newType.setProperty(G_PROPS.JEWELRY_GROUP, JEWELRY_GROUP.ATTRIBUTE.name());
        } else {
            newType.setProperty(G_PROPS.JEWELRY_GROUP, JEWELRY_GROUP.PARAMETER.name());
        }

        String code = "" + (level.getLevel());
        if (level.getLevel() > 0) {
            String img = generateImgPath(code, type);
            if (ImageManager.isImage(img)) {
                newType.setProperty(G_PROPS.IMAGE, img);
            }
        }
        return newType;
    }

    private static ObjType generateEmptyJewelryItem(boolean ring, ObjType type, MATERIAL material) {
        ObjType newType = getNewType(type);
        // new ObjType(type);
        // type.getGame().initType(newType);
        // newType.setGenerated(true);
        if (material != null) {
            newType.modifyParameter(PARAMS.ENCHANTMENT_CAPACITY, material.getEnchantmentCapacity());
            int costMod = material.getCost();
            if (ring) {
                newType.modifyParameter(PARAMS.CHARISMA, material.getModifier());
            } else {
                newType.modifyParameter(PARAMS.CHARISMA, 2 * material.getModifier());
            }

            newType.setProperty(G_PROPS.NAME, generateName(null, material, newType));

            newType.modifyParameter(PARAMS.GOLD_COST, costMod);
        }
        return newType;
    }

    public static String generateName(JEWELRY_ITEM_TRAIT trait, ITEM_LEVEL level,
                                      ObjType type) {
        PARAMETER p = trait.getParams()[0];
        String name = type.getName();
        int amount = trait.getIntegers()[level.getLevel()];
        if (!ItemMaster.isRing(type) && trait.isDoubleAmulet()) {
            amount *= 2;
        }
        name = level.toString() + " " + name + " of " + p.getName()
         + StringMaster.wrapInParenthesis("+" + amount);
        return name;
    }

    public static String generateName(JEWELRY_PASSIVE_ENCHANTMENT ench, ITEM_LEVEL level,
                                      ObjType type) {
        String prefix = (level == null) ? "" : level.toString() + " ";
        return prefix + type.getName() + " of " + ench.toString();
    }

    public static ObjType generateItem_(boolean weapon, QUALITY_LEVEL quality,
                                        MATERIAL material, ObjType type) {
        return defaultGenerator.generateItem(weapon, quality, material, type);
    }

    private static void generateItemTypes(boolean weapon, QUALITY_LEVEL[] qualityLevels,
                                          MATERIAL[] materials, ITEM_MATERIAL_GROUP group) {
        List<ObjType> types = DataManager.getBaseTypes((weapon) ? DC_TYPE.WEAPONS : DC_TYPE.ARMOR);
        for (ObjType type : types) {
            if (ItemMaster.checkSpecialType(type)) {
                initSpecialItem(type);
                continue;
            }
            for (QUALITY_LEVEL quality : qualityLevels) {
                for (MATERIAL material : materials) {

                    if (type.isGenerated()) {
                        continue;
                    }
                    if (!ItemMaster.checkMaterial(type, group)) {
                        continue;
                    }
                    generateItem_(weapon, quality, material, type);

                }
            }

        }
    }

    private static void initSpecialItem(ObjType type) {
        MATERIAL m = new EnumMaster<MATERIAL>().retrieveEnumConst(MATERIAL.class, type
         .getProperty(G_PROPS.MATERIAL));

        // init traits/enchantments?

    }

    public static ObjType getNewType(ObjType type) {
        ObjType newType = new ObjType(type);
        type.getGame().initType(newType);
        newType.setGenerated(true);

        newType.setProperty(G_PROPS.BASE_TYPE, type.getName());
        return newType;
    }

    public static ObjType generateItem(QUALITY_LEVEL quality, MATERIAL material, ObjType type,
                                       PARAMS[] params, PARAMS[] mod_params) {
        if (type.isGenerated()) {
            return null;
        }
        ObjType newType = getNewType(type);
        initParams(quality, material, newType, params, mod_params);

        String name = generateName(quality, material, type);
        newType.setProperty(G_PROPS.NAME, name);
        String code = "" + material.getCode();
        if (material.getCode() < 1) {
            code = "";
        }
        String img = generateImgPath(code, type);

        newType.setProperty(G_PROPS.MATERIAL, material.getName());
        newType.setProperty(G_PROPS.QUALITY_LEVEL, quality.toString());

        if (img != null) {
            newType.setProperty(G_PROPS.IMAGE, img);
        }

        applyWeightPenalties(newType);

        itemMaps.get(quality).get(material).put(type, newType);
        DataManager.addType(newType);
        return newType;
    }

    private static void applyWeightPenalties(ObjType type) {
        // TODO magical item exception!
        if (type.getGroupingKey().equalsIgnoreCase("" + ItemEnums.WEAPON_TYPE.NATURAL)) {
            return;
        }
        // robes?
        // ++ apply durability from quality?
        int w = type.getIntParam(PARAMS.WEIGHT);

        for (PARAMETER p : (type.getOBJ_TYPE_ENUM() == DC_TYPE.WEAPONS ? DC_ContentValsManager
         .getWeaponWeightPenaltyParams() : DC_ContentValsManager.getArmorWeightPenaltyParams())) {
            if (type.getGroupingKey().equalsIgnoreCase("" + ItemEnums.WEAPON_TYPE.MAGICAL)) {
                if (!p.getName().contains("move") && !p.getName().contains("attack")) {
                    continue;
                }
            }
            type.modifyParameter(p, w);
        }

        type.modifyParameter(PARAMS.DEFENSE_MOD, -w);
        type.modifyParameter(PARAMS.ATTACK_MOD, -w);
    }

    private static String getDescriptionAppendix() {
        return null;

    }

    private static void initParams(QUALITY_LEVEL quality, MATERIAL material, ObjType newType,
                                   PARAMS[] params, PARAMS[] mod_params) {
        int n = newType.getIntParam(PARAMS.MATERIAL_QUANTITY);
        newType.modifyParameter(PARAMS.WEIGHT, (int) Math.round(material.getWeight() * n));

        int i = 0;
        for (PARAMS p : mod_params) {
            int amount;
            int modifier = material.getModifier();
            if (p == PARAMS.DURABILITY_MODIFIER) {
                if (material.getDurabilityMod() != 0) {
                    modifier = material.getDurabilityMod();
                }
            }
            if (p == PARAMS.DICE) {
                amount =
                 //                 newType.getIntParam(portrait) *
                 modifier / 2; // TODO DICE ALREADY USED, DIE_SIZE TO BE SET FOR WEAPONS!
            } else if (p == PARAMS.ARMOR_MODIFIER) {
                // TODO *NEW
                amount = newType.getIntParam(PARAMS.ARMOR_LAYERS) * modifier * ARMOR_MODIFIER;
                // Integer cover = newType.getIntParam(PARAMS.COVER_PERCENTAGE);
                // Integer layers = newType.getIntParam(PARAMS.ARMOR_LAYERS);
                // Integer pieces =
                // newType.getIntParam(PARAMS.MATERIAL_QUANTITY);
                // System.out.println(newType.getName() + " has " + cover +
                // " cover, " + pieces
                // + " pieces, " + amount + " armor");

            } else {
                amount = newType.getIntParam(p) * modifier * n / 100;
            }
            newType.modifyParameter(params[i], amount, null, true);
            i++;
        }
        boolean magicApplied = false;
        if (newType.getOBJ_TYPE_ENUM() == DC_TYPE.ARMOR) {
            if (!isBasicMode())
                ContentGenerator.generateArmorPerDamageType(newType, material);

            // TODO [UPDATED]
            // if (material.getResistances() != null)
            // for (String s :
            // StringMaster.openContainer(material.getResistances())) {
            // Integer base =
            // StringMaster.getInteger(VariableManager.getVarPart(s));
            // int durabilityMod = quality.getDurabilityMod();
            // if (base < 0)
            // durabilityMod = 100 - (durabilityMod - 100) / 2;
            //
            // String amount = n + "*" + base;
            // amount = durabilityMod + "*" +
            // StringMaster.wrapInParenthesis(amount) + "/100";
            // newType.modifyParameter(VariableManager.removeVarPart(s) +
            // " Resistance",
            // amount);
            // magicApplied = true;
            // }
        } else {
            if (material.getDmg_type() != null) { // MAGICAL MATERIALS
                int perc = (int) Math.round(material.getEnchantmentCapacity() / SPELL_DAMAGE_FACTOR
                 * Math.sqrt(n));
                perc = MathMaster.applyMod(perc, quality.getDurabilityMod());
                String abilName = "DamagePercOnAttackThis";
                String appendix = " deals an additional " + perc + "% of "
                 + material.getDmg_type().getName() + " damage on each successful hit";

                if (StringMaster.contains(ItemEnums.WEAPON_GROUP.TOWER_SHIELDS + "|" + ItemEnums.WEAPON_GROUP.BUCKLERS
                 + "|" + ItemEnums.WEAPON_GROUP.SHIELDS, newType.getProperty(G_PROPS.WEAPON_GROUP))) {
                    abilName = "DamagePercOnHitThis"; // TODO APPENDIX
                } else if (newType.getProperty(G_PROPS.WEAPON_GROUP).equalsIgnoreCase(
                 "" + ItemEnums.WEAPON_GROUP.WANDS)) {
                    abilName = "DamagePercOnSpellImpact";
                } else if (newType.getProperty(G_PROPS.WEAPON_GROUP).equalsIgnoreCase(
                 "" + ItemEnums.WEAPON_GROUP.ORBS)) {
                    abilName = "DamagePercOnSpellHit";
                }
                newType.addProperty(G_PROPS.PASSIVES, abilName
                 + StringMaster.wrapInParenthesis(perc + ","
                 + material.getDmg_type().getName()));

                newType.appendProperty(G_PROPS.DESCRIPTION, Strings.NEW_LINE
                 + material.getName() + ": " + appendix);

                magicApplied = true;
            }
        }

        Integer mod = newType.getIntParam(PARAMS.COST_MODIFIER);
        if (mod <= 0) {
            mod = 100;
        }
        newType.modifyParameter(PARAMS.GOLD_COST, material.getCost() * n * mod / 100);

        newType.setParameter(PARAMS.HARDNESS, material.getHardness());

        //        if (new EnumMaster<WEAPON_GROUP>().retrieveEnumConst(WEAPON_GROUP.class, newType
        //         .getProperty(G_PROPS.WEAPON_GROUP)) == ItemEnums.WEAPON_GROUP.SHIELDS) {
        //            newType.setParam(PARAMS.ARMOR, newType.getIntParam(PARAMS.DAMAGE_BONUS));
        //      TODO       ContentGenerator.generateArmorPerDamageType(newType, material);
        //        }

        mod = quality.getDurabilityMod();
        if (mod > 0 && mod != 100) {
            newType.multiplyParamByPercent(PARAMS.DURABILITY, mod, false);
        }

        mod = quality.getCostMod();
        if (mod > 0 && mod != 100) {
            newType.multiplyParamByPercent(PARAMS.GOLD_COST, mod, false);
        }
        if (magicApplied) {
            newType.multiplyParamByPercent(PARAMS.GOLD_COST, mod, false);
        }
        newType.modifyParameter(PARAMS.ENCHANTMENT_CAPACITY, n * material.getEnchantmentCapacity());

        // PARAMS.DICE min/max? special dmg category?

    }

    private static String generateImgPath(String code, ObjType type) {

        String baseImg = type.getImagePath();
        String format = StringMaster.getFormat(baseImg);
        String path = StringMaster.cropFormat(baseImg);
        if (NumberUtils.isInteger("" + path.charAt(path.length() - 1))) {
            path = path.substring(0, path.length() - 1);
        }

        String newPath = path + code + format;

        if (!ImageManager.isImage(newPath)) {
            path = StringMaster.replaceFirst(baseImg, format, "");

            return baseImg;
        }
        return newPath;
    }

    public static String generateName(QUALITY_LEVEL quality, MATERIAL material, ObjType type) {
        String qual = (quality != ItemEnums.QUALITY_LEVEL.NORMAL && quality != null) ? quality.toString()
         + " " : "";
        return qual + material.toString() + " " + type.getName();
    }

    public static ItemGenerator getDefaultGenerator() {
        return defaultGenerator;
    }

    public static ObjType getOrCreateItemType(ObjType t, MATERIAL material, QUALITY_LEVEL q) {
        ObjType item = getGeneratedItem(t, material, q);
        if (item == null) {
            item = generateItem_(q, material, t);
        }
        return item;
    }

    public static ObjType getGeneratedItem(ObjType t, MATERIAL material, QUALITY_LEVEL q) {
        return itemMaps.get(q).get(material).get(t);
    }

    public static List<ObjType> getTypesForShop(OBJ_TYPE type) {
        List<ObjType> list = new ArrayList<>();
        if (type instanceof C_OBJ_TYPE) {
            for (DC_TYPE dcType : ((C_OBJ_TYPE) type).getTypes()) {
                list.addAll(getTypesForShop(dcType));
            }
        } else {
            if (type.equals(DC_TYPE.WEAPONS)) {
                if (baseWeaponTypesForShops == null) {
                    baseWeaponTypesForShops =     new ArrayList<>(baseWeaponTypes) ;
                    baseWeaponTypesForShops.removeIf(t ->
                     t.checkProperty(G_PROPS.WEAPON_TYPE, WEAPON_TYPE.MAGICAL.name()));
                    baseWeaponTypesForShops.removeIf(t ->
                     t.checkProperty(G_PROPS.WEAPON_TYPE, WEAPON_TYPE.NATURAL.name()));
                    baseWeaponTypesForShops.removeIf(t ->
                     t.checkProperty(G_PROPS.WEAPON_GROUP, WEAPON_GROUP.NATURAL.name()));
                    baseWeaponTypesForShops.removeIf(t ->
                     t.checkProperty(G_PROPS.WEAPON_GROUP, WEAPON_GROUP.FIREARMS.name()));
                    baseWeaponTypesForShops.removeIf(t ->
                     t.checkProperty(G_PROPS.WEAPON_CLASS, WEAPON_CLASS.DOUBLE.name()));
                }
                return (baseWeaponTypesForShops);
            }
            if (type.equals(DC_TYPE.ITEMS)|| type.equals(DC_TYPE.JEWELRY)) {
                if (!jewelryGenerated){
                    getDefaultGenerator().generateJewelry();
                }
                if (!usablesGenerated){
                    getDefaultGenerator().generateUsableItems();
                }
                if (type.equals(DC_TYPE.JEWELRY)) {
                    list.addAll(DataManager.getTypes(type));
                    list.removeIf(type1 -> !type1.isGenerated());
                    list.removeIf(type1 -> type1.checkProperty(
                     G_PROPS.JEWELRY_GROUP, JEWELRY_GROUP.EMPTY.name()));
                } else {
                    list.addAll(DataManager.getTypesGroup(DC_TYPE.ITEMS, ITEM_GROUP.POTIONS.name()));
                    list.removeIf(type1 -> !type1.isGenerated());
                }

            }

            if (type.equals(DC_TYPE.ARMOR)) {
                return (baseArmorTypes);
            }
            if (type.equals(DC_TYPE.GARMENT)) {
                return (baseGarmentTypes);
            }
        }

        return list;
    }

    public static ObjType generateWeapon(QUALITY_LEVEL quality,
                                         MATERIAL material, ObjType type) {
        return defaultGenerator.generateItem(true, quality, material, type);
    }

    public static ObjType generateArmor(QUALITY_LEVEL quality,
                                        MATERIAL material, ObjType type) {
        return defaultGenerator.generateItem(false, quality, material, type);
    }

    public static boolean isJewelryOn() {
        return jewelryGenerated || !Flags.isFastMode();
    }

    public static ObjType generateItem_(QUALITY_LEVEL quality, MATERIAL material, ObjType type) {
        return defaultGenerator.generateItem(quality, material, type);
    }

    public ObjType getOrCreateJewelry(ObjType baseType, JEWELRY_ITEM_TRAIT trait,
                                      ITEM_LEVEL level) {
        String name = generateName(trait, level, baseType);
        if (baseType.getOBJ_TYPE_ENUM()!= DC_TYPE.JEWELRY) {
            return null;
        }
        ObjType type = DataManager.getType(name, DC_TYPE.JEWELRY);
        if (type != null) {
            return type;
        }
        return generateJewelryItem(baseType, trait, level);
    }

    public ObjType generateJewelry(String typeName) {
        String string = StringMaster.getFirstItem(typeName, " ");
        ITEM_LEVEL itemLevel =
         new EnumMaster<ITEM_LEVEL>().
          retrieveEnumConst(ITEM_LEVEL.class, string);

        typeName = typeName.replaceFirst(string + " ", "");

        ObjType objType = null;
        for (ObjType type : baseJewelryTypes) {
            if (typeName.contains(type.getName())) {
                objType = type;
                typeName = typeName.replaceFirst(type.getName(), "");
                break;
            }
        }
        if (objType == null) {
            return null ;
        }
        JEWELRY_ITEM_TRAIT trait =
         new EnumMaster<JEWELRY_ITEM_TRAIT>().
          retrieveEnumConst(JEWELRY_ITEM_TRAIT.class, typeName);
        if (trait == null) {
            typeName = VariableManager.removeVarPart(typeName).replaceFirst("of", "").trim();
            trait =
             new EnumMaster<JEWELRY_ITEM_TRAIT>().
              retrieveEnumConst(JEWELRY_ITEM_TRAIT.class, typeName);
        }
        return generateJewelryItem(objType, trait, itemLevel);
    }

    @Override
    public ObjType generateItem(boolean weapon, QUALITY_LEVEL quality,
                                MATERIAL material, ObjType type) {

        ObjType newType = generateItem(quality, material, type,
         (weapon) ? WEAPON_PARAMS : ARMOR_PARAMS, (weapon) ? WEAPON_MOD_PARAMS
          : ARMOR_MOD_PARAMS);
        if (newType == null) {
            return null;
        }
        DataManager.addType(newType.getName(), (weapon) ? DC_TYPE.WEAPONS
         : DC_TYPE.ARMOR, newType);
        DataManager.getItemMaps().get(quality).get(material).put(type, newType);
        return newType;
    }

    public void init() {
        //        if (!basicMode)
        if (GenericItemGenerator.OFF)
            return;
        ContentGenerator.initMaterials();
        if (!isGenerationOn()) {
            return;
        }


        for (QUALITY_LEVEL q : ItemEnums.QUALITY_LEVEL.values()) {
            ConcurrentMap qMap = new ConcurrentMap<>();
            itemMaps.put(q, qMap);
            for (MATERIAL m : ItemEnums.MATERIAL.values()) {
                qMap.put(m, new ConcurrentMap<>());
            }
        }

        baseWeaponTypes.addAll(DataManager.getBaseTypes(DC_TYPE.WEAPONS));
        baseArmorTypes.addAll(DataManager.getBaseTypes(DC_TYPE.ARMOR));
        baseJewelryTypes.addAll(DataManager.getBaseTypes(DC_TYPE.JEWELRY));
        baseItemTypes.addAll(DataManager.getBaseTypes(DC_TYPE.ITEMS));
        // baseGarmentTypes.addAll(DataManager.getBaseTypes(OBJ_TYPES.GARMENT));

        DataManager.setBaseWeaponTypes(baseWeaponTypes.toArray(new ObjType[0]));

        DataManager.setBaseGarmentTypes(baseGarmentTypes.toArray(new ObjType[0]));
        DataManager.setBaseArmorTypes(baseArmorTypes.toArray(new ObjType[0]));
        DataManager.setBaseJewelryTypes(baseJewelryTypes.toArray(new ObjType[0]));
        DataManager.setBaseItemTypes(baseItemTypes.toArray(new ObjType[0]));
        //Arrays.
        List<ObjType> list = new ArrayList<>(baseWeaponTypes);
        if (!EidolonsGame.BRIDGE){

        list.addAll(baseArmorTypes);
        list.addAll(baseJewelryTypes);
        list.addAll(baseItemTypes);
        }
        DataManager.setBaseAllItemTypes(list.toArray(new ObjType[0]));
        //        defaultGenerator = new ItemGenerator(false);
        //        basicGenerator = new ItemGenerator(true);
        try {
            //            (isBasicMode() ? basicGenerator : defaultGenerator).
            generateItemObjTypes();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        DataManager.setItemMaps(itemMaps);

    }

    @Override
    public ObjType generateItemType(String typeName, OBJ_TYPE type) {
        if (type == (DC_TYPE.JEWELRY)) {
            if (ItemGenerator.isJewelryOn())
                return generateJewelry(typeName);
            return null;
        }
        if (type == (DC_TYPE.ITEMS)) {
            //            return getOrCreateItemType(typeName);
        }
        boolean weapon = type.equals(DC_TYPE.WEAPONS); //TODO generic C_TYPE for armor?
        //        String baseTypeName = typeName;
        String name = StringMaster.getFirstItem(typeName, " ");
        QUALITY_LEVEL quality =
         new EnumMaster<QUALITY_LEVEL>().retrieveEnumConst(
          QUALITY_LEVEL.class, name);
        if (quality != null)
            typeName = typeName.replace(quality.toString(), "").trim();
        else quality = QUALITY_LEVEL.NORMAL;
        name = StringMaster.getFirstItem(typeName, " ");
        MATERIAL material =
         new EnumMaster<MATERIAL>().retrieveEnumConst(
          MATERIAL.class, name, true, false);
        if (material == null) {
            name = name + " " + StringMaster.getFirstItem(typeName.replace(name, "").trim(), " ");
            material =
             new EnumMaster<MATERIAL>().retrieveEnumConst(
              MATERIAL.class, name);
        }
        if (material == null) {
            return null;
        }
        typeName = typeName.replace(material.toString(), "").trim();

        ObjType baseType = DataManager.getType(typeName, type, true);
        if (baseType == null)
            return null;
        if (GenericItemGenerator.OFF) {
            return baseType;
        }
        return generateItem(weapon, quality, material, baseType);

    }

    public void generateItemObjTypes() {
        generateItemObjTypes(defaultQualityLevels, DEFAULT_MATERIALS_METALS);
    }

    public void generateItemObjTypes(QUALITY_LEVEL[] qualitylevels, MATERIAL[] materialsMetals) {

        qualityLevels = qualitylevels;
        materials = materialsMetals;


        Chronos.mark("ITEM_GENERATION"); // better to generate them into xml
        // eventually
        if (preGenerate) {
            generateArmor();
            generateWeapons();
        }
        if (!basicMode)
            try {
                generateJewelry();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        // try {
        // generateGarments();
        // } catch (Exception e) {
        // main.system.ExceptionMaster.printStackTrace(e);
        // }
        try {
            generateUsableItems();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        Chronos.logTimeElapsedForMark("ITEM_GENERATION");
    }


    private void generateArmor() {

        generateItemTypes(false, defaultQualityLevels, basic ? BASIC_MATERIALS_METALS
         : DEFAULT_MATERIALS_METALS, ItemEnums.ITEM_MATERIAL_GROUP.METAL);
        generateItemTypes(false, defaultQualityLevels, DEFAULT_MATERIALS_CLOTH,
         ItemEnums.ITEM_MATERIAL_GROUP.CLOTH);
        generateItemTypes(false, defaultQualityLevels, basic ? BASIC_MATERIALS_LEATHER
         : DEFAULT_MATERIALS_SKINS, ItemEnums.ITEM_MATERIAL_GROUP.LEATHER);


        boolean armorGenerated = true;
    }

    private void generateWeapons() {
        generateItemTypes(true, defaultQualityLevels, basic ? BASIC_MATERIALS_METALS
         : DEFAULT_MATERIALS_METALS, ItemEnums.ITEM_MATERIAL_GROUP.METAL);
        generateItemTypes(true, defaultQualityLevels, basic ? BASIC_MATERIALS_BONES
         : DEFAULT_MATERIALS_BONES, ItemEnums.ITEM_MATERIAL_GROUP.BONE);
        generateItemTypes(true, defaultQualityLevels, basic ? BASIC_MATERIALS_STONE
         : DEFAULT_MATERIALS_STONE, ItemEnums.ITEM_MATERIAL_GROUP.STONE);
        generateItemTypes(true, defaultQualityLevels, basic ? BASIC_MATERIALS_WOOD
         : DEFAULT_MATERIALS_WOOD, ItemEnums.ITEM_MATERIAL_GROUP.WOOD);
        generateItemTypes(true, new QUALITY_LEVEL[]{ItemEnums.QUALITY_LEVEL.NORMAL},
         basic ? BASIC_MATERIALS_NATURAL : DEFAULT_MATERIALS_NATURAL,
         ItemEnums.ITEM_MATERIAL_GROUP.NATURAL);

        generateItemTypes(true, new QUALITY_LEVEL[]{ItemEnums.QUALITY_LEVEL.NORMAL}, new MATERIAL[]{
         ItemEnums.MATERIAL.GRANITE, ItemEnums.MATERIAL.CRYSTAL, ItemEnums.MATERIAL.ONYX, ItemEnums.MATERIAL.OBSIDIAN,
         ItemEnums.MATERIAL.SOULSTONE,}, ItemEnums.ITEM_MATERIAL_GROUP.STONE);

        boolean weaponsGenerated = true;
    }

    public void generateUsableItems() {

        for (ObjType type : DataManager.getBaseTypes(DC_TYPE.ITEMS)) {

            if (type.getProperty(G_PROPS.ITEM_TYPE).equalsIgnoreCase(ItemEnums.ITEM_TYPE.ALCHEMY + "")) {
                if (type.getProperty(G_PROPS.ITEM_GROUP).equalsIgnoreCase(
                 ItemEnums.ITEM_GROUP.CONCOCTIONS + "")
                 || type.getProperty(G_PROPS.ITEM_GROUP).equalsIgnoreCase(
                 ItemEnums.ITEM_GROUP.COATING + "")
                 || type.getProperty(G_PROPS.GROUP).equals("Elixirs")) {
                    generateConcoctions(type);
                } else {
                    generatePotions(type);
                }

            }

        }
        usablesGenerated = true;
    }

    public void generateJewelry() {
        for (ObjType type : DataManager.getBaseTypes(DC_TYPE.JEWELRY)) {
            boolean ring = ItemMaster.isRing(type);
            for (MATERIAL material : getJewelryMaterials(type
             .getProperty(G_PROPS.ITEM_MATERIAL_GROUP))) {
                ObjType newType = generateEmptyJewelryItem(ring, type, material);
                newType.setProperty(G_PROPS.JEWELRY_GROUP, JEWELRY_GROUP.EMPTY.name());
                DataManager.addType(newType.getName(), DC_TYPE.JEWELRY, newType);
            }
        }

        for (JEWELRY_PASSIVE_ENCHANTMENT ench : JEWELRY_PASSIVE_ENCHANTMENT.values()) {
            boolean leveled = ench.isLeveled();
            List<ObjType> types;
            types = DataManager.toTypeList(
             // (leveled) ? PAS_LEVEL_JEWELRY:
             ench.getItemTypes(), DC_TYPE.JEWELRY);
            for (ObjType type : types) {
                //           TODO      ObjType newType = generateJewelryPassiveEnchantment(ench, type, level);
                if (type.isGenerated()) {
                    continue;
                }
                boolean ring = ItemMaster.isRing(type);
                if (ring) {
                    if (!ench.isRing()) {
                        continue;
                    }
                }

                if (!leveled) {
                    ObjType newType = generateEmptyJewelryItem(ring, type, null);

                    newType = generateJewelryItem(ring, newType, ench, null);
                    newType.setProperty(G_PROPS.JEWELRY_GROUP, JEWELRY_GROUP.PASSIVE.name());
                    continue;
                }
                for (ITEM_LEVEL level : ITEM_LEVEL.values()) {
                    ObjType newType = generateEmptyJewelryItem(ring, type, null);
                    newType = generateJewelryItem(ring, newType, ench, level);
                    newType.setProperty(G_PROPS.JEWELRY_GROUP, DataManager.ENCH);
                }
            }
        }
        for (JEWELRY_ITEM_TRAIT trait : JEWELRY_ITEM_TRAIT.values()) {

            // if (trait == MAGICAL_ITEM_TRAIT.ATTRIBUTE_BONUS)
            // continue;
            for (ObjType type : DataManager.toTypeList(trait.getJewelryTypes(), DC_TYPE.JEWELRY)) {

                if (type.isGenerated()) {
                    continue;
                }
                for (ITEM_LEVEL level : ITEM_LEVEL.values()) {
                    boolean ring = ItemMaster.isRing(type);
                    generateJewelryItem(type, trait, level);

                }
            }
        }
        jewelryGenerated = true;
        // MAGICAL_ITEM_TRAIT trait = MAGICAL_ITEM_TRAIT.ATTRIBUTE_BONUS;
        // for (ObjType type : DataManager
        // .convertToTypeList(ATTR_JEWELRY, OBJ_TYPES.JEWELRY)) {
        // if (type.isGenerated())
        // continue;
        // for (MAGICAL_ITEM_LEVEL level : MAGICAL_ITEM_LEVEL.values()) {
        // boolean ring = type
        // .checkProperty(G_PROPS.JEWELRY_TYPE, JEWELRY_TYPE.RING
        // + "");
        // generateJewelryItem(ring, type, trait, level);
        // }
        // }

    }

    public ObjType generateItem(QUALITY_LEVEL quality, MATERIAL material, ObjType type) {
        PARAMS[] params = (type.getOBJ_TYPE_ENUM() == DC_TYPE.WEAPONS) ? WEAPON_PARAMS
         : ARMOR_PARAMS;
        PARAMS[] mod_params = (type.getOBJ_TYPE_ENUM() == DC_TYPE.WEAPONS) ? WEAPON_MOD_PARAMS
         : ARMOR_MOD_PARAMS;

        return generateItem(quality, material, type, params, mod_params);

    }

    public DC_HeroItemObj createItem(ObjType type, Ref ref, boolean addMaterialParams) {
        // if (addMaterialParams)//if building from blueprint type
        // type = generateItem_(quality, material, type, params, mod_params);
        GenericGame game = (GenericGame) ref.getGame();

        return (type.getOBJ_TYPE_ENUM() == DC_TYPE.WEAPONS) ? new DC_WeaponObj(
         type, ref.getPlayer(), game, ref) : new DC_ArmorObj(type, ref.getPlayer(), game,
         ref);
    }
}
