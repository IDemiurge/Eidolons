package eidolons.game.module.dungeoncrawl.quest;

import eidolons.content.PARAMS;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import main.content.DC_TYPE;
import main.content.enums.entity.UnitEnums.UNITS_TYPES_CORRUPTED;
import main.content.enums.entity.UnitEnums.UNITS_TYPES_MONSTER;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.content.enums.meta.QuestEnums.QUEST_TIME_LIMIT;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.SortMaster;
import main.system.auxiliary.Loop;
import main.system.datatypes.WeightMap;

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

    public static String getDescriptor(DungeonQuest quest) {
        switch (quest.getType()) {
            case BOSS:
                return getBossName(quest);
            case FIND:
                return getItemName(quest);
            case HUNT:
                return getHuntedUnitName(quest);
        }
        return null;
    }

    private static String getItemName(DungeonQuest quest) {
        switch (quest.getLocationType()) {
            case CAVE:
                return new WeightMap<>()
                 .chain(UNITS_TYPES_CORRUPTED.EVIL_EYE, 1)
                 .chain(UNITS_TYPES_CORRUPTED.CORRUPTED_MIND_FLAYER, 1)
                 .chain(UNITS_TYPES_CORRUPTED.TROGLODYTE_MUTANT, 1)
                 .getRandomByWeight().toString();
        }
        return null;
    }

    private static String getHuntedUnitName(DungeonQuest quest) {
        switch (quest.getLocationType()) {
            case CAVE:
                return new WeightMap<>()
                 .chain(UNITS_TYPES_CORRUPTED.EVIL_EYE, 1)
                 .chain(UNITS_TYPES_CORRUPTED.CORRUPTED_MIND_FLAYER, 1)
                 .chain(UNITS_TYPES_CORRUPTED.TROGLODYTE_MUTANT, 1)
                 .getRandomByWeight().toString();
        }
        return null;
    }

    private static String getBossName(DungeonQuest quest) {
        switch (quest.getLocationType()) {
            case CAVE:
                return new WeightMap<>()
                 .chain(UNITS_TYPES_MONSTER.HYDRA, 1)
                 .chain(UNITS_TYPES_MONSTER.BLACK_DRAGON, 1)
                 .chain(UNITS_TYPES_MONSTER.MANTICORE, 1)
                 .getRandomByWeight().toString();
        }
        return null;
    }

    public static Integer getNumberRequired(DungeonQuest quest) {
        switch (quest.getType()) {
            case BOSS:
            case FIND:
                return 1;
            case HUNT:
                return 10;
        }
        return 0;
    }

    public static ObjType getBossType(int powerLevel, DungeonQuest quest,
                                      DUNGEON_STYLE style) {
        List<ObjType> pool = null;
        while (new Loop(20).continues()) {
            UNIT_GROUP unitGroup = RngMainSpawner.getUnitGroup(false, style);
            pool = DataManager.getFilteredTypes(DC_TYPE.UNITS,
             unitGroup.toString(), G_PROPS.UNIT_GROUP);

            pool.removeIf(type -> Math.abs(1 -
             type.getIntParam(PARAMS.POWER) / powerLevel) > 0.5f);

            if (!pool.isEmpty()) {
                break;
            }
        }

        new SortMaster<ObjType>().sortByExpression_(pool,
         type -> type.getIntParam(PARAMS.POWER));
        return pool.get(0);
    }

    public DungeonQuest create(ObjType result) {
        return new DungeonQuest(result);
    }
}
