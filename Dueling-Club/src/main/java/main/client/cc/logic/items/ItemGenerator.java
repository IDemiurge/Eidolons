package main.client.cc.logic.items;

import main.content.CONTENT_CONSTS.*;
import main.content.*;
import main.content.DC_CONSTS.*;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.data.ConcurrentMap;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.DC_ArmorObj;
import main.entity.obj.DC_HeroItemObj;
import main.entity.obj.DC_WeaponObj;
import main.entity.type.ObjType;
import main.game.MicroGame;
import main.system.ContentGenerator;
import main.system.auxiliary.Chronos;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.LogMaster.LOG_CHANNELS;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.system.math.MathMaster;

import java.util.*;

public class ItemGenerator {

    public static final MATERIAL[] DEFAULT_MATERIALS_STONE = {MATERIAL.ONYX, MATERIAL.OBSIDIAN,
            MATERIAL.CRYSTAL, MATERIAL.SOULSTONE, MATERIAL.STAR_EMBER};
    public static final MATERIAL[] BASIC_MATERIALS_STONE = {MATERIAL.ONYX, MATERIAL.OBSIDIAN,};
    public static final MATERIAL[] DEFAULT_MATERIALS_WOOD = {MATERIAL.RED_OAK, MATERIAL.IRONWOOD,
            MATERIAL.BLACKWOOD, MATERIAL.PALEWOOD, MATERIAL.WAILWOOD};
    public static final MATERIAL[] BASIC_MATERIALS_WOOD = {MATERIAL.RED_OAK, MATERIAL.IRONWOOD,
            MATERIAL.BLACKWOOD, MATERIAL.PALEWOOD};
    public static final MATERIAL[] BASIC_MATERIALS_METALS = {MATERIAL.COPPER, MATERIAL.BRASS,
            MATERIAL.BRONZE, MATERIAL.IRON, MATERIAL.STEEL, MATERIAL.MITHRIL};
    public static final MATERIAL[] DEFAULT_MATERIALS_METALS = {MATERIAL.COPPER, MATERIAL.BRASS,
            MATERIAL.BRONZE, MATERIAL.IRON, MATERIAL.STEEL, MATERIAL.MITHRIL, MATERIAL.PLATINUM,
            MATERIAL.DARK_STEEL, MATERIAL.WARP_STEEL, MATERIAL.PALE_STEEL, MATERIAL.WRAITH_STEEL,
            MATERIAL.ADAMANTIUM, MATERIAL.METEORITE, MATERIAL.MOON_SILVER, MATERIAL.ELDRITCH_STEEL,
            MATERIAL.BRIGHT_STEEL, MATERIAL.DEFILED_STEEL

    };
    public static final MATERIAL[] DEFAULT_MATERIALS_CLOTH = {MATERIAL.COTTON, MATERIAL.SILK,};
    public static final MATERIAL[] DEFAULT_MATERIALS_BONES = {MATERIAL.IVORY, MATERIAL.BLACK_BONE,
            MATERIAL.MAN_BONE, MATERIAL.DRAGON_BONE};
    public static final MATERIAL[] BASIC_MATERIALS_BONES = {MATERIAL.IVORY, MATERIAL.BLACK_BONE,};
    public static final MATERIAL[] DEFAULT_MATERIALS_NATURAL = {MATERIAL.PUNY, MATERIAL.PETTY,
            MATERIAL.AVERAGE, MATERIAL.SIZABLE, MATERIAL.DIRE, MATERIAL.FEARSOME,
            MATERIAL.MONSTROUS};
    public static final MATERIAL[] BASIC_MATERIALS_NATURAL = {MATERIAL.PUNY, MATERIAL.PETTY,
            MATERIAL.AVERAGE, MATERIAL.SIZABLE, MATERIAL.DIRE,};
    public static final MATERIAL[] DEFAULT_MATERIALS_SKINS = {MATERIAL.THIN_LEATHER,
            MATERIAL.TOUGH_LEATHER, MATERIAL.THICK_LEATHER, MATERIAL.LIZARD_SKIN,
            MATERIAL.DRAGONHIDE, MATERIAL.TROLL_SKIN, MATERIAL.FUR};
    public static final MATERIAL[] BASIC_MATERIALS_SKINS = {MATERIAL.THIN_LEATHER,
            MATERIAL.TOUGH_LEATHER, MATERIAL.THICK_LEATHER,};
    public static final PARAMS[] WEAPON_PARAMS = {PARAMS.DAMAGE_BONUS, PARAMS.DIE_SIZE,
            PARAMS.DURABILITY};
    public static final PARAMS[] WEAPON_MOD_PARAMS = {PARAMS.BASE_DAMAGE_MODIFIER, PARAMS.DICE,
            PARAMS.DURABILITY_MODIFIER};
    public static final PARAMS[] ARMOR_PARAMS = {PARAMS.ARMOR, PARAMS.RESISTANCE,
            PARAMS.DURABILITY};
    public static final PARAMS[] ARMOR_MOD_PARAMS = {PARAMS.ARMOR_MODIFIER,
            PARAMS.RESISTANCE_MODIFIER, PARAMS.DURABILITY_MODIFIER};
    public static final MATERIAL[] METAL_JEWELRY_MATERIALS = {MATERIAL.COPPER, MATERIAL.SILVER,
            MATERIAL.GOLD,};
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
    private static Map<QUALITY_LEVEL, Map<MATERIAL, Map<ObjType, ObjType>>> itemMaps = new ConcurrentMap();
    private static List<ObjType> baseWeaponTypes = new ArrayList<>();
    private static List<ObjType> baseJewelryTypes = new ArrayList<>();
    private static List<ObjType> baseItemTypes = new ArrayList<>();
    private static List<ObjType> baseArmorTypes = new ArrayList<>();
    private static List<ObjType> baseGarmentTypes = new ArrayList<>();
    private static boolean switcher = true;
    private static boolean basicMode = true;
    public QUALITY_LEVEL[] defaultQualityLevels = QUALITY_LEVEL.values();
    QUALITY_LEVEL[] qualityLevels;
    MATERIAL[] materials;
    private boolean basic;

    public ItemGenerator(boolean basic) {
        this.basic = basic;

    }

    public static void init() {
        if (!isGenerationOn()) {
            return;
        }
        if (defaultGenerator != null) {
            return;
        }

        ContentGenerator.initMaterials();

        for (QUALITY_LEVEL q : QUALITY_LEVEL.values()) {
            ConcurrentMap qMap = new ConcurrentMap<>();
            itemMaps.put(q, qMap);
            for (MATERIAL m : MATERIAL.values()) {
                qMap.put(m, new ConcurrentMap<>());
            }
        }
        baseWeaponTypes.addAll(DataManager.getTypes(OBJ_TYPES.WEAPONS));
        baseArmorTypes.addAll(DataManager.getTypes(OBJ_TYPES.ARMOR));
        baseJewelryTypes.addAll(DataManager.getTypes(OBJ_TYPES.JEWELRY));
        baseItemTypes.addAll(DataManager.getTypes(OBJ_TYPES.ITEMS));
        // baseGarmentTypes.addAll(DataManager.getTypes(OBJ_TYPES.GARMENT));

        DataManager.setBaseGarmentTypes(baseGarmentTypes);
        DataManager.setBaseWeaponTypes(baseWeaponTypes);
        DataManager.setBaseArmorTypes(baseArmorTypes);
        DataManager.setBaseJewelryTypes(baseJewelryTypes);
        DataManager.setBaseItemTypes(baseItemTypes);

        defaultGenerator = new ItemGenerator(false);
        basicGenerator = new ItemGenerator(true);
        try {
            (isBasicMode() ? basicGenerator : defaultGenerator).generateItemObjTypes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataManager.setItemMaps(itemMaps);

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

    private static boolean isRing(ObjType type) {
        return type.checkProperty(G_PROPS.JEWELRY_TYPE, JEWELRY_TYPE.RING + "");
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
                                               JEWELRY_PASSIVE_ENCHANTMENT ench, MAGICAL_ITEM_LEVEL level) {
        String value = ench.getValue(level);
        type.addProperty(ench.getProp(), value);
        String string = StringMaster.cropFormat(type.getImagePath());
        string += ench.getIconVariantLetter();
        type.setProperty(PROPS.JEWELRY_PASSIVE_ENCHANTMENT, string);
        int costMod = ench.getCostBase();
        if (level != null) {
            if (level.getInt() > 0) {
                string += (level.getInt());
            }
            costMod = costMod * level.getCostFactor();
        }
        string += ImageManager.DEFAULT_ENTITY_IMAGE_FORMAT;
        type.modifyParameter(PARAMS.GOLD_COST, costMod);
        type.setProperty(G_PROPS.IMAGE, string);
        string = generateName(ench, level, type);
        type.setProperty(G_PROPS.NAME, string);

        DataManager.addType(type.getName(), OBJ_TYPES.JEWELRY, type);
        return type;

    }

    private static void generateJewelryItem(boolean ring, ObjType type, JEWELRY_ITEM_TRAIT trait,
                                            MAGICAL_ITEM_LEVEL level) {
        PARAMETER[] params = trait.getParams();
        for (PARAMETER p : params) {
            ObjType newType = generateEmptyJewelryItem(ring, type, null);
            int amount = trait.getIntegers()[level.getInt()]; // double for
            // amulets?
            if (!ring && trait.isDoubleAmulet()) {
                amount = amount * 2;
            }
            newType.setParam(p, amount);

            type.getGame().initType(newType);
            newType.setGenerated(true);
            String name = generateName(trait, level, type, p);
            newType.setProperty(G_PROPS.NAME, name);
            DataManager.addType(newType.getName(), OBJ_TYPES.JEWELRY, newType);

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

            String group = DataManager.MISC;
            if (trait == JEWELRY_ITEM_TRAIT.ATTRIBUTE_BONUS) {
                group = DataManager.ATTR;
            }

            newType.setProperty(G_PROPS.JEWELRY_GROUP, group);

            String code = "" + (level.getInt());
            if (level.getInt() > 0) {
                String img = generateImgPath(code, type);
                if (ImageManager.isImage(img)) {
                    newType.setProperty(G_PROPS.IMAGE, img);
                }
            }
        }

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

    public static String generateName(JEWELRY_ITEM_TRAIT trait, MAGICAL_ITEM_LEVEL level,
                                      ObjType type, PARAMETER p) {
        String name = type.getName();
        int amount = trait.getIntegers()[level.getInt()];
        if (!isRing(type) && trait.isDoubleAmulet()) {
            amount *= 2;
        }
        name = level.toString() + " " + name + " of " + p.getName()
                + StringMaster.wrapInParenthesis("+" + amount);
        return name;
    }

    public static String generateName(JEWELRY_PASSIVE_ENCHANTMENT ench, MAGICAL_ITEM_LEVEL level,
                                      ObjType type) {
        String prefix = (level == null) ? "" : level.toString() + " ";
        String name = prefix + type.getName() + " of " + ench.toString();
        return name;
    }

    private static void generateItemTypes(boolean weapon, QUALITY_LEVEL[] qualityLevels,
                                          MATERIAL[] materials, ITEM_MATERIAL_GROUP group) {
        List<ObjType> types = DataManager.getTypes((weapon) ? OBJ_TYPES.WEAPONS : OBJ_TYPES.ARMOR);
        for (ObjType type : types) {
            if (checkSpecialType(type)) {
                initSpecialItem(type);
                continue;
            }
            for (QUALITY_LEVEL quality : qualityLevels) {
                for (MATERIAL material : materials) {

                    if (type.isGenerated()) {
                        continue;
                    }
                    if (!checkMaterial(type, group)) {
                        continue;
                    }

                    ObjType newType = generateItem(quality, material, type,
                            (weapon) ? WEAPON_PARAMS : ARMOR_PARAMS, (weapon) ? WEAPON_MOD_PARAMS
                                    : ARMOR_MOD_PARAMS);
                    main.system.auxiliary.LogMaster.log(LOG_CHANNELS.GENERATION, "Generated: "
                            + newType);
                    DataManager.addType(newType.getName(), (weapon) ? OBJ_TYPES.WEAPONS
                            : OBJ_TYPES.ARMOR, newType);
                }
            }

        }
    }

    private static void initSpecialItem(ObjType type) {
        MATERIAL m = new EnumMaster<MATERIAL>().retrieveEnumConst(MATERIAL.class, type
                .getProperty(G_PROPS.MATERIAL));

        // init traits/enchantments?

    }

    private static boolean checkSpecialType(ObjType type) {

        return type.checkBool(STD_BOOLS.SPECIAL_ITEM);
    }

    public static boolean checkMaterial(ObjType type, ITEM_MATERIAL_GROUP group) {
        return StringMaster.compare(group.toString(),
                type.getProperty(G_PROPS.ITEM_MATERIAL_GROUP), true);
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

        return newType;
    }

    private static void applyWeightPenalties(ObjType type) {
        // TODO magical item exception!
        if (type.getGroupingKey().equalsIgnoreCase("" + WEAPON_TYPE.NATURAL)) {
            return;
        }
        // robes?
        // ++ apply durability from quality?
        int w = type.getIntParam(PARAMS.WEIGHT);

        for (PARAMETER p : (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.WEAPONS ? DC_ContentManager
                .getWeaponWeightPenaltyParams() : DC_ContentManager.getArmorWeightPenaltyParams())) {
            if (type.getGroupingKey().equalsIgnoreCase("" + WEAPON_TYPE.MAGICAL)) {
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
            int amount = 0;
            int modifier = material.getModifier();
            if (p == PARAMS.DURABILITY_MODIFIER) {
                if (material.getDurabilityMod() != 0) {
                    modifier = material.getDurabilityMod();
                }
            }
            if (p == PARAMS.DICE) {
                amount = newType.getIntParam(p) * modifier; // TODO
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
        if (newType.getOBJ_TYPE_ENUM() == OBJ_TYPES.ARMOR) {
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

                if (StringMaster.contains(WEAPON_GROUP.TOWER_SHIELDS + "|" + WEAPON_GROUP.BUCKLERS
                        + "|" + WEAPON_GROUP.SHIELDS, newType.getProperty(G_PROPS.WEAPON_GROUP))) {
                    abilName = "DamagePercOnHitThis"; // TODO APPENDIX
                } else if (newType.getProperty(G_PROPS.WEAPON_GROUP).equalsIgnoreCase(
                        "" + WEAPON_GROUP.WANDS)) {
                    abilName = "DamagePercOnSpellImpact";
                } else if (newType.getProperty(G_PROPS.WEAPON_GROUP).equalsIgnoreCase(
                        "" + WEAPON_GROUP.ORBS)) {
                    abilName = "DamagePercOnSpellHit";
                }
                newType.addProperty(G_PROPS.PASSIVES, abilName
                        + StringMaster.wrapInParenthesis(perc + ","
                        + material.getDmg_type().getName()));

                newType.appendProperty(G_PROPS.DESCRIPTION, StringMaster.NEW_LINE
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

        if (new EnumMaster<WEAPON_GROUP>().retrieveEnumConst(WEAPON_GROUP.class, newType
                .getProperty(G_PROPS.WEAPON_GROUP)) == WEAPON_GROUP.SHIELDS) {
            newType.setParam(PARAMS.ARMOR, newType.getIntParam(PARAMS.DAMAGE_BONUS));
            ContentGenerator.generateArmorPerDamageType(newType, material);
        }

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
        if (StringMaster.isInteger("" + path.charAt(path.length() - 1))) {
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
        String qual = (quality != QUALITY_LEVEL.NORMAL && quality != null) ? quality.toString()
                + " " : "";
        return qual + material.toString() + " " + type.getName();
    }

    public static ItemGenerator getDefaultGenerator() {
        return defaultGenerator;
    }

    public static ObjType getGeneratedItem(ObjType t, MATERIAL material, QUALITY_LEVEL q) {
        return itemMaps.get(q).get(material).get(t);
    }

    public static List<ObjType> getBaseTypes(OBJ_TYPE type) {
        List<ObjType> list = new LinkedList<>();
        if (type instanceof C_OBJ_TYPE) {
            if (type.equals(OBJ_TYPES.WEAPONS)) {
                list.addAll(baseWeaponTypes);
            }
            if (type.equals(OBJ_TYPES.JEWELRY)) {
                list.addAll(baseJewelryTypes);
            }
            if (type.equals(OBJ_TYPES.ITEMS)) {
                list.addAll(baseItemTypes);
            }
            if (type.equals(OBJ_TYPES.ARMOR)) {
                list.addAll(baseArmorTypes);
            }
            if (type.equals(OBJ_TYPES.GARMENT)) {
                list.addAll(baseGarmentTypes);
            }
        } else {
            if (type.equals(OBJ_TYPES.WEAPONS)) {
                return (baseWeaponTypes);
            }
            if (type.equals(OBJ_TYPES.JEWELRY)) {
                return (baseJewelryTypes);
            }
            if (type.equals(OBJ_TYPES.ITEMS)) {
                return (baseItemTypes);
            }
            if (type.equals(OBJ_TYPES.ARMOR)) {
                return (baseArmorTypes);
            }
            if (type.equals(OBJ_TYPES.GARMENT)) {
                return (baseGarmentTypes);
            }
        }

        return list;
    }

    public void generateItemObjTypes() {
        generateItemObjTypes(defaultQualityLevels, DEFAULT_MATERIALS_METALS);
    }

    public void generateItemObjTypes(QUALITY_LEVEL[] qualitylevels, MATERIAL[] materialsMetals) {

        qualityLevels = qualitylevels;
        materials = materialsMetals;
        Chronos.mark("ITEM_GENERATION"); // better to generate them into xml
        // eventually
        generateArmor();

        generateWeapons();

        try {
            generateJewelry();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // try {
        // generateGarments();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        try {
            generateUsableItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Chronos.logTimeElapsedForMark("ITEM_GENERATION");
    }

    private void generateGarments() {
        generateBoots();
        generateGloves();
        generateHelmets();
        generateCloaks();

    }

    private void generateCloaks() {
        List<MATERIAL> materials = new LinkedList<>();
        materials.addAll(Arrays.asList(DEFAULT_MATERIALS_CLOTH));
        materials.addAll(Arrays.asList(DEFAULT_MATERIALS_SKINS));
        for (ObjType type : DataManager
                .getTypesSubGroup(OBJ_TYPES.GARMENT, GARMENT_TYPE.BOOTS + "")) {
            for (QUALITY_LEVEL q : QUALITY_LEVEL.values()) {
                for (MATERIAL m : materials) {
                    // generateItem(quality, material, type)

                }
            }

        }

    }

    private void generateHelmets() {
        // TODO Auto-generated method stub

    }

    private void generateGloves() {
        // TODO Auto-generated method stub

    }

    private void generateBoots() {
        // TODO Auto-generated method stub

    }

    private void generateArmor() {
        generateItemTypes(false, defaultQualityLevels, basic ? BASIC_MATERIALS_METALS
                : DEFAULT_MATERIALS_METALS, ITEM_MATERIAL_GROUP.METAL);
        generateItemTypes(false, defaultQualityLevels, DEFAULT_MATERIALS_CLOTH,
                ITEM_MATERIAL_GROUP.CLOTH);
        generateItemTypes(false, defaultQualityLevels, basic ? BASIC_MATERIALS_SKINS
                : DEFAULT_MATERIALS_SKINS, ITEM_MATERIAL_GROUP.LEATHER);
    }

    private void generateWeapons() {
        generateItemTypes(true, defaultQualityLevels, basic ? BASIC_MATERIALS_METALS
                : DEFAULT_MATERIALS_METALS, ITEM_MATERIAL_GROUP.METAL);
        generateItemTypes(true, defaultQualityLevels, basic ? BASIC_MATERIALS_BONES
                : DEFAULT_MATERIALS_BONES, ITEM_MATERIAL_GROUP.BONE);
        generateItemTypes(true, defaultQualityLevels, basic ? BASIC_MATERIALS_STONE
                : DEFAULT_MATERIALS_STONE, ITEM_MATERIAL_GROUP.STONE);
        generateItemTypes(true, defaultQualityLevels, basic ? BASIC_MATERIALS_WOOD
                : DEFAULT_MATERIALS_WOOD, ITEM_MATERIAL_GROUP.WOOD);
        generateItemTypes(true, new QUALITY_LEVEL[]{QUALITY_LEVEL.NORMAL},
                basic ? BASIC_MATERIALS_NATURAL : DEFAULT_MATERIALS_NATURAL,
                ITEM_MATERIAL_GROUP.NATURAL);

        generateItemTypes(true, new QUALITY_LEVEL[]{QUALITY_LEVEL.NORMAL}, new MATERIAL[]{
                MATERIAL.GRANITE, MATERIAL.CRYSTAL, MATERIAL.ONYX, MATERIAL.OBSIDIAN,
                MATERIAL.SOULSTONE,}, ITEM_MATERIAL_GROUP.STONE);
    }

    public void generateUsableItems() {

        for (ObjType type : DataManager.getTypes(OBJ_TYPES.ITEMS)) {

            if (type.getProperty(G_PROPS.ITEM_TYPE).equalsIgnoreCase(ITEM_TYPE.ALCHEMY + "")) {
                if (type.getProperty(G_PROPS.ITEM_GROUP).equalsIgnoreCase(
                        ITEM_GROUP.CONCOCTIONS + "")
                        || type.getProperty(G_PROPS.ITEM_GROUP).equalsIgnoreCase(
                        ITEM_GROUP.COATING + "")
                        || type.getProperty(G_PROPS.GROUP).equals("Elixirs")) {
                    generateConcoctions(type);
                } else {
                    generatePotions(type);
                }

            }

        }

    }

    public void generateJewelry() {
        for (ObjType type : DataManager.getTypes(OBJ_TYPES.JEWELRY)) {
            boolean ring = isRing(type);
            for (MATERIAL material : getJewelryMaterials(type
                    .getProperty(G_PROPS.ITEM_MATERIAL_GROUP))) {
                ObjType newType = generateEmptyJewelryItem(ring, type, material);
                newType.setProperty(G_PROPS.JEWELRY_GROUP, DataManager.EMPTY);
                DataManager.addType(newType.getName(), OBJ_TYPES.JEWELRY, newType);
            }
        }

        for (JEWELRY_PASSIVE_ENCHANTMENT ench : JEWELRY_PASSIVE_ENCHANTMENT.values()) {
            boolean leveled = ench.isLeveled();
            List<ObjType> types = null;
            types = DataManager.toTypeList(
                    // (leveled) ? PAS_LEVEL_JEWELRY:
                    ench.getItemTypes(), OBJ_TYPES.JEWELRY);
            for (ObjType type : types) {
                if (type.isGenerated()) {
                    continue;
                }
                boolean ring = isRing(type);
                if (ring) {
                    if (!ench.isRing()) {
                        continue;
                    }
                }

                if (!leveled) {
                    ObjType newType = generateEmptyJewelryItem(ring, type, null);

                    newType = generateJewelryItem(ring, newType, ench, null);
                    newType.setProperty(G_PROPS.JEWELRY_GROUP, DataManager.ENCH);
                    continue;
                }
                for (MAGICAL_ITEM_LEVEL level : MAGICAL_ITEM_LEVEL.values()) {
                    ObjType newType = generateEmptyJewelryItem(ring, type, null);
                    newType = generateJewelryItem(ring, newType, ench, level);
                    newType.setProperty(G_PROPS.JEWELRY_GROUP, DataManager.ENCH);
                }
            }
        }
        for (JEWELRY_ITEM_TRAIT trait : JEWELRY_ITEM_TRAIT.values()) {

            // if (trait == MAGICAL_ITEM_TRAIT.ATTRIBUTE_BONUS)
            // continue;
            for (ObjType type : DataManager.toTypeList(trait.getJewelryTypes(), OBJ_TYPES.JEWELRY)) {

                if (type.isGenerated()) {
                    continue;
                }
                for (MAGICAL_ITEM_LEVEL level : MAGICAL_ITEM_LEVEL.values()) {
                    boolean ring = isRing(type);
                    generateJewelryItem(ring, type, trait, level);

                }
            }
        }

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
        PARAMS[] params = (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.WEAPONS) ? WEAPON_PARAMS
                : ARMOR_PARAMS;
        PARAMS[] mod_params = (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.WEAPONS) ? WEAPON_MOD_PARAMS
                : ARMOR_MOD_PARAMS;

        return generateItem(quality, material, type, params, mod_params);

    }

    public DC_HeroItemObj createItem(ObjType type, Ref ref, boolean addMaterialParams) {
        // if (addMaterialParams)//if building from blueprint type
        // type = generateItem(quality, material, type, params, mod_params);
        MicroGame game = (MicroGame) ref.getGame();
        DC_HeroItemObj item = (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.WEAPONS) ? new DC_WeaponObj(
                type, ref.getPlayer(), game, ref) : new DC_ArmorObj(type, ref.getPlayer(), game,
                ref);

        return item;
    }

}
