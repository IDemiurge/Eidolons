package eidolons.game.module.herocreator.logic.items;

import eidolons.content.DC_CONSTS.ITEM_LEVEL;
import eidolons.content.DC_ContentValsManager;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.module.herocreator.logic.items.ItemTraits.EIDOLON_ASPECT;
import eidolons.game.module.herocreator.logic.items.ItemTraits.ITEM_TRAIT;
import eidolons.game.module.herocreator.logic.items.ItemTraits.ITEM_TRAIT_RARITY;
import eidolons.game.module.herocreator.logic.items.ItemTraits.ITEM_TRAIT_TYPE;
import main.content.DC_TYPE;
import main.content.enums.entity.ItemEnums.ITEM_SHOP_CATEGORY;
import main.data.DataManager;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.system.ExceptionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ArrayMaster;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static eidolons.game.module.herocreator.logic.items.ItemTraits.EIDOLON_ASPECT.DARK;

/**
 * Created by JustMe on 12/8/2018.
 */
public class ItemTraitGenerator {
    private static final boolean TEST_MODE = true;
    static Map<ITEM_TRAIT_TYPE, List<String>> commonTraits;
    static List<ITEM_TRAIT> traits = new ArrayList<>(Arrays.asList(ITEM_TRAIT.values()));

    public static ObjType generateRandomItemWithTraits(ITEM_LEVEL level,
                                                       DC_TYPE TYPE, Object... args) {
        ITEM_TRAIT_RARITY rarity = chooseRarity(level);
        EIDOLON_ASPECT style = chooseStyle(level);
        String group = null;
        if (args[0] instanceof ITEM_SHOP_CATEGORY) {
            //                group=chooseItemGroup(TYPE, level);
        } else {
            //loot?
        }
        return generateItemWithTraits(TYPE, style, rarity, level, group);
    }

    private static EIDOLON_ASPECT chooseStyle(ITEM_LEVEL level) {
        return DARK;
    }

    private static ITEM_TRAIT_RARITY chooseRarity(ITEM_LEVEL level) {
        return ITEM_TRAIT_RARITY.UNCOMMON;
    }

    private static ObjType generateItemWithTraits(DC_TYPE TYPE, EIDOLON_ASPECT style,
                                                  ITEM_TRAIT_RARITY rarity,
                                                  ITEM_LEVEL level,
                                                  String group) {
        ObjType type = chooseType(TYPE, group, level);
        ObjType newType = new ObjType(type);

        int n = getTraitNumber(level);

        //use the Eidolon system?

        List<ItemTrait> traits = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ITEM_LEVEL c_level = adjustLevelForTraitNumber(n, level, i);
            ITEM_TRAIT template = chooseTemplate(TYPE, style, rarity, traits);
            ItemTrait trait = new ItemTrait(template, c_level);
            try {
                ItemTraitParser.applyTrait(newType, trait);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            traits.add(trait);
        }
        //shuffle
        String name = ItemTraitNamer.getName(type, traits);
        newType.setDisplayedName(name);
        newType.setName(ItemTraitNamer.getDescriptor(type, traits));
        return newType;
    }

    private static ObjType chooseType(DC_TYPE type, String group, ITEM_LEVEL level) {
        return DataManager.getRandomType(type, group);
    }

    private static ITEM_TRAIT chooseTemplate(DC_TYPE type, EIDOLON_ASPECT style, ITEM_TRAIT_RARITY rarity, List<ItemTrait> traits) {
        List<ITEM_TRAIT> pool = ItemTraitGenerator.traits.stream().filter(
         trait -> {
             if (TEST_MODE)
                 return true;
             if (trait.rarity == rarity)
                 if (ArrayMaster.contains_(trait.types, type)) {
                     if (trait.styleMap == null) {
                         return false;
                     }
                     try {
                         EIDOLON_ASPECT pickedStyle = trait.styleMap.getRandomByWeight();
                         if (pickedStyle == style)
                             return true;
                     } catch (Exception e) {
                         ExceptionMaster.printStackTrace(e);
                     }
                 }
             return false;
         }
        ).collect(Collectors.toList());
        if (traits!=null ){
    for (ItemTrait trait : traits) {
        pool.remove(trait.getTemplate());
        //or add up levels?
    }
}
        //while ...
        return new RandomWizard<ITEM_TRAIT>().getRandomListItem(pool);
    }

    private static ITEM_LEVEL adjustLevelForTraitNumber(int n, ITEM_LEVEL level, int i) {
        int j = EnumMaster.getEnumConstIndex(ITEM_LEVEL.class, level);
        int offset = 1;
        if (n > 3) {
            offset -= 2;
        }
        if (n > 2) {
            if (i > 1)
                offset--;
            offset -= 1;
        } else {
            if (i > 0)
                offset--;
        }
        if (j + offset >= ITEM_LEVEL.values().length) {
            return ITEM_LEVEL.values()[ITEM_LEVEL.values().length - 1];
        }
        if (j + offset < 0) {
            return ITEM_LEVEL.values()[0];
        }
        return ITEM_LEVEL.values()[j + offset];
    }

    private static int getTraitNumber(ITEM_LEVEL level) {
        switch (level) {
            case COMMON:
                return 2;
            case GREATER:
                return 3;
            case LEGENDARY:
                return 4;
        }
        return 1;
    }

    @Test
    public void test() {
//        DC_Engine.mainMenuInit();
        DC_Engine.systemInit();
        new DC_ContentValsManager().init();
        XML_Reader.readTypeFile(false, DC_TYPE.WEAPONS);
        for (EIDOLON_ASPECT aspect : EIDOLON_ASPECT.values()) {
        for (ITEM_LEVEL level : ITEM_LEVEL.values()) {
            ObjType type = generateItemWithTraits(DC_TYPE.WEAPONS, aspect,
             ITEM_TRAIT_RARITY.COMMON, level, null);
            main.system.auxiliary.log.LogMaster.log(1,
//             level+ " " + aspect + " generated " +
             type.getDisplayedName() + " " + StringMaster.wrapInParenthesis(type.getName()));
        }
        }
    }
}
