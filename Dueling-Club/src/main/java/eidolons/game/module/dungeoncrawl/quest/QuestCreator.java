package eidolons.game.module.dungeoncrawl.quest;

import eidolons.content.PARAMS;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import main.content.DC_TYPE;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.content.enums.meta.QuestEnums.QUEST_TIME_LIMIT;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.SortMaster;
import main.system.auxiliary.Loop;

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
            case FIND:
            case HUNT:
                return Math.max(1, Math.round(quest.getPowerCoef() * 10));
        }
        return 0;
    }

    public static ObjType tryGetQuestUnitType(int powerLevel, float powerRange, DungeonQuest quest,
                                              DUNGEON_STYLE style) {
        ObjType type = getQuestUnitType(powerLevel, powerRange, quest, style);
        if (type == null) {
            type = getQuestUnitType(powerLevel, powerRange, quest, DUNGEON_STYLE.Somber);
        }
        return type;
    }

    public static ObjType getQuestUnitType(int powerLevel, float powerRange, DungeonQuest quest,
                                           DUNGEON_STYLE style) {
        List<ObjType> pool = null;
        Loop loop = new Loop(30);
        while (loop.continues()) {
            try {
                UNIT_GROUP unitGroup = RngMainSpawner.getUnitGroup(false, style);
                pool = DataManager.getFilteredTypes(DC_TYPE.UNITS,
                 unitGroup.toString(), G_PROPS.UNIT_GROUP);

                if (powerRange < 0) {
                    pool.removeIf(type ->
                     (1 - type.getIntParam(PARAMS.POWER)) / powerLevel < powerRange);
                } else {
                    pool.removeIf(type -> Math.abs(1 -
                     type.getIntParam(PARAMS.POWER) / powerLevel) > powerRange);
                }

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

    public static ObjType getBossType(int powerLevel, DungeonQuest quest,
                                      DUNGEON_STYLE style) {
        return tryGetQuestUnitType(powerLevel, 0.5f, quest, style);
    }

    public static ObjType getPreyType(int powerLevel, DungeonQuest quest,
                                      DUNGEON_STYLE style) {
        return tryGetQuestUnitType(powerLevel, -0.25f, quest, style);
    }

        public static ObjType getItemType(int powerLevel, DungeonQuest quest, DUNGEON_STYLE style) {
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
