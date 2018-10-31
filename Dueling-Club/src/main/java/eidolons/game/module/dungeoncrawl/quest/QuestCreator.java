package eidolons.game.module.dungeoncrawl.quest;

import eidolons.content.PARAMS;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner;
import eidolons.game.module.dungeoncrawl.generator.init.RngUnitProvider;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import eidolons.game.module.dungeoncrawl.objects.ContainerMaster;
import eidolons.game.module.herocreator.logic.items.ItemGenerator;
import eidolons.game.module.herocreator.logic.items.ItemMaster;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.content.enums.entity.ItemEnums.ITEM_RARITY;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.content.enums.meta.QuestEnums.QUEST_TIME_LIMIT;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.SortMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestCreator extends QuestHandler {


    public QuestCreator(QuestMaster questMaster) {
        super(questMaster);
    }

    public static Integer getTimeInSeconds(DungeonQuest quest, QUEST_TIME_LIMIT timing) {
        return 1000;
    }

    public static Integer getNumberRequired(DungeonQuest quest) {
        if (QuestMaster.TEST_MODE)
            return 1;
        switch (quest.getType()) {
            case BOSS:
                return 1;
            case OBJECTS:
                return Math.max(1, Math.round(quest.getPowerCoef() * 15));
            case SECRETS:
            case COMMON_ITEMS:
                return Math.max(1, Math.round(quest.getPowerCoef() * 10));
            case HUNT:
                return Math.max(1, Math.round(quest.getPowerCoef() * 7));
            case SPECIAL_ITEM:
                break;
            case ESCAPE:
                break;
        }
        return 1;
    }

    public static ObjType tryGetQuestUnitType(Boolean type, int powerLevel, float powerRange, DungeonQuest quest,
                                              DUNGEON_STYLE style) {
        ObjType t = getQuestUnitType(type, powerLevel, powerRange, quest, style);
        if (t == null) {
            t = getQuestUnitType(type, powerLevel, powerRange, quest, DungeonEnums.DUNGEON_STYLE.Somber);
        }
        return t;
    }

    public static ObjType getQuestUnitType(Boolean TYPE, int powerLevel,
                                           float powerRange, DungeonQuest quest,
                                           DUNGEON_STYLE style) {
        List<ObjType> pool = null;
        Loop loop = new Loop(30);
        while (loop.continues()) {
            try {
                UNIT_GROUP unitGroup = RngMainSpawner.getUnitGroup(false, style);
                WeightMap<String> weightMap = RngUnitProvider.getUnitWeightMap(unitGroup, TYPE);
                pool = DataManager.toTypeList(weightMap.keySet(), DC_TYPE.UNITS);
                filter(pool, powerRange, powerLevel);
                if (pool.isEmpty()) {
                    pool = DataManager.getFilteredTypes(DC_TYPE.UNITS,
                     unitGroup.toString(), G_PROPS.UNIT_GROUP);
                }
                filter(pool, powerRange, powerLevel);
                if (!pool.isEmpty()) {
                    break;
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (pool.isEmpty()) {
            return null;
        }
        new SortMaster<ObjType>().sortByExpression_(pool,
         type -> type.getIntParam(PARAMS.POWER));
        return pool.get(0);
    }

    private static void filter(List<ObjType> pool, float powerRange, int powerLevel) {
        if (powerRange < 0) {
            pool.removeIf(type ->
             (1 - type.getIntParam(PARAMS.POWER)) / powerLevel < powerRange);
        } else {
            pool.removeIf(type -> Math.abs(1 -
             type.getIntParam(PARAMS.POWER) / powerLevel) > powerRange);
        }
    }

    public static ObjType getBossType(int powerLevel, DungeonQuest quest,
                                      DUNGEON_STYLE style) {
        return tryGetQuestUnitType(RngUnitProvider.BOSS, powerLevel, 0.5f, quest, style);
    }

    public static ObjType getPreyType(int powerLevel, DungeonQuest quest,
                                      DUNGEON_STYLE style) {
        ObjType type = tryGetQuestUnitType(RngUnitProvider.REGULAR, powerLevel, -0.25f, quest, style);
        if (type == null) {
            type = tryGetQuestUnitType(RngUnitProvider.ELITE, powerLevel, -0.25f, quest, style);
        }
        return type;
    }

    public static ObjType getItemTypeSpecial(ObjType questObjType, int powerLevel, DungeonQuest quest, DUNGEON_STYLE style) {
        String arg = questObjType.getProperty(MACRO_PROPS.QUEST_ARG);
        DC_TYPE TYPE = DC_TYPE.getType(arg);
        if (TYPE == null) {
            TYPE = DC_TYPE.WEAPONS;
            if (RandomWizard.chance(20)) {
                TYPE = DC_TYPE.ARMOR;
            }
        }
        //            switch (TYPE) {
        //                case WEAPONS:
        //                    name= "";
        //                    break;
        //                case ARMOR:
        //                    break;
        //            }
        //            ObjType base=DataManager.getType(TYPE, name);
        MATERIAL m = MATERIAL.DARK_STEEL;
        ObjType base = (ObjType) RandomWizard.getRandomListObject(ItemGenerator.getTypesForShop(TYPE));
        //            DataManager.getBaseWeaponTypes()

        if (base.isGenerated()) {
            base = base.getType();
        }
        for (MATERIAL material : new HashSet<>(Arrays.asList(ContainerMaster.getMaterials(
         RandomWizard.random() ?
          ITEM_RARITY.RARE : ITEM_RARITY.EXCEPTIONAL)))) {
            if (ItemMaster.checkMaterial(base, material)) {
                m = material;
                break;
            }
        }
        QUALITY_LEVEL q = QUALITY_LEVEL.ANCIENT;
        ObjType type = ItemGenerator.getOrCreateItemType(base.getType(), m, q);

        return type;
    }

    public static ObjType getItemTypeCommon(int powerLevel, DungeonQuest quest, DUNGEON_STYLE style) {
        //        quest.getArg().toString()
        //        DataManager.getTypesGroup()
        return DataManager.getType("Food", DC_TYPE.ITEMS);
    }


    public DungeonQuest create(ObjType result) {
        return new DungeonQuest(result);
    }
}


//    public static String getDescriptor(DungeonQuest quest) {
//        switch (quest.getType()) {
//            case BOSS:
//                return getBossName(quest);
//            case FIND:
//                return getItemName(quest);
//            case HUNT:
//                return getHuntedUnitName(quest);
//        }
//        return null;
//    }
//    private static String getItemName(DungeonQuest quest) {
//        switch (quest.getLocationType()) {
//            case CAVE:
//                return new WeightMap<>()
//                 .chain(UNITS_TYPES_CORRUPTED.EVIL_EYE, 1)
//                 .chain(UNITS_TYPES_CORRUPTED.CORRUPTED_MIND_FLAYER, 1)
//                 .chain(UNITS_TYPES_CORRUPTED.TROGLODYTE_MUTANT, 1)
//                 .getRandomByWeight().toString();
//        }
//        return null;
//    }
//
//    private static String getHuntedUnitName(DungeonQuest quest) {
//        switch (quest.getLocationType()) {
//            case CAVE:
//                return new WeightMap<>()
//                 .chain(UNITS_TYPES_CORRUPTED.EVIL_EYE, 1)
//                 .chain(UNITS_TYPES_CORRUPTED.CORRUPTED_MIND_FLAYER, 1)
//                 .chain(UNITS_TYPES_CORRUPTED.TROGLODYTE_MUTANT, 1)
//                 .getRandomByWeight().toString();
//        }
//        return null;
//    }
//
//    private static String getBossName(DungeonQuest quest) {
//        switch (quest.getLocationType()) {
//            case CAVE:
//                return new WeightMap<>()
//                 .chain(UNITS_TYPES_MONSTER.HYDRA, 1)
//                 .chain(UNITS_TYPES_MONSTER.BLACK_DRAGON, 1)
//                 .chain(UNITS_TYPES_MONSTER.MANTICORE, 1)
//                 .getRandomByWeight().toString();
//        }
//        return null;
//    }
