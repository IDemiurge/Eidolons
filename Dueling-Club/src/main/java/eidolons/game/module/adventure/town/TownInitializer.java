package eidolons.game.module.adventure.town;

import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.adventure.MacroRef;
import main.content.CONTENT_CONSTS2.SHOP_LEVEL;
import main.content.CONTENT_CONSTS2.SHOP_MODIFIER;
import main.content.CONTENT_CONSTS2.SHOP_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.Map;

public class TownInitializer {

    private static final int default_shop_min = 2;
    private static final int default_shop_limit = 5;

    public static void initTownPlaces(Town town) {
        initShops(town);
        initLibraries(town);
        initFactionQuarters(town);
        initTaverns(town);
    }

    public static void initTaverns(Town town) {
        String typeName = town.getProperty(MACRO_PROPS.TAVERNS);

        if (!DataManager.isTypeName(typeName)) {
            if (typeName.isEmpty()) {
                typeName = TavernMaster.DEFAULT_TAVERN;
            } else {
                // PER T_TYPE, random or default
            }
        }
        ObjType type = DataManager
         .getType(typeName, MACRO_OBJ_TYPES.TOWN_PLACE);
        if (type == null) // TODO yeah...
        {
            type = DataManager.getTypes(MACRO_OBJ_TYPES.TOWN_PLACE).get(0);
        }
        Tavern tavern = new Tavern(town.getRef().getGame(), type, town.getRef());
        TavernMaster.generateTavernName(tavern);
        town.setTavern(tavern);
        town.addTavern(tavern);
    }

    public static void initShops(Town town) {
        MacroRef ref = MacroGame.getGame().getRef().getCopy();
        int i = 0;
        int max = town.getLevel();
        if (max == 0) {
            max = default_shop_limit;
        }
        int min = town.getLevel() / 2;
        if (min == 0) {
            min = default_shop_min;
        }
        for (String shopTypeName : StringMaster.open(town
         .getProperty(MACRO_PROPS.SHOPS))) {
            i++;
            if (i > max) {
                break;
            }
            ObjType type = new ObjType(DataManager.getType(shopTypeName,
             MACRO_OBJ_TYPES.SHOP));
            type.initType();
            if (type == null) {
                type = getGenericShopType(shopTypeName);
            }
            addShop(town, ref, type);
        }
        if (i < min) {
            // generate by preferred type/level/mod

            Map<SHOP_TYPE, Integer> typeMap = new RandomWizard<SHOP_TYPE>()
             .constructWeightMap(
              town.getProperty(MACRO_PROPS.SHOP_TYPE),
              SHOP_TYPE.class, MACRO_OBJ_TYPES.SHOP);
            Map<SHOP_MODIFIER, Integer> modMap = new RandomWizard<SHOP_MODIFIER>()
             .constructWeightMap(
              town.getProperty(MACRO_PROPS.SHOP_MODIFIER),
              SHOP_MODIFIER.class, MACRO_OBJ_TYPES.SHOP);

            while (true) {
                SHOP_TYPE shopType = new RandomWizard<SHOP_TYPE>()
                 .getObjectByWeight(typeMap);
                SHOP_MODIFIER shopMode = new RandomWizard<SHOP_MODIFIER>()
                 .getObjectByWeight(modMap);
                ObjType type = getGenericShopType(shopType.toString());
                type.setProperty(MACRO_PROPS.SHOP_MODIFIER, shopMode.toString());
                addShop(town, ref, type);
            }
        }
    }

    private static ObjType getGenericShopType(String shopType) {
        ObjType type;
        type = new ObjType(); // macro_?
        // new EnumMaster<ENUM>().retrieveEnumConst(ENUM.class, string )
        type.setProperty(MACRO_PROPS.SHOP_TYPE, shopType);
        type.setProperty(MACRO_PROPS.SHOP_LEVEL, SHOP_LEVEL.COMMON.toString()); // TODO
        type.setProperty(MACRO_PROPS.SHOP_MODIFIER,
         SHOP_MODIFIER.HUMAN.toString());
        return type;
    }

    private static void addShop(Town town, MacroRef ref, ObjType type) {
        if (town.getShop() == null) {
            Shop shop = new Shop(town.getGame(), type, ref);
            town.setShop(shop);
            town.getShops().add(shop);
        } else {
            town.getShops().add(new Shop(town.getGame(), type, ref));
        }
    }

    public static void initLibraries(Town town) {
        // TODO Auto-generated method stub

    }

    public static void initFactionQuarters(Town town) {
        // TODO Auto-generated method stub

    }

}
