package eidolons.game.module.dungeoncrawl.generator.test;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats.LEVEL_STAT;
import eidolons.system.options.Options.OPTION;
import main.system.auxiliary.data.MapMaster;
import main.system.data.DataUnit;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 8/2/2018.
 */
public class LevelStats extends DataUnit<LEVEL_STAT> {
    public LevelStats( ) {

    }
    public LevelStats(DungeonLevel level) {
        WeightMap<EXIT_TEMPLATE> templateMap = new WeightMap();
        WeightMap<ROOM_TYPE> typeMap = new WeightMap();
        WeightMap<ROOM_CELL> map = new WeightMap();
        for (Room room : level.getModel().getBlocks().keySet()) {
            EXIT_TEMPLATE template = room.getExitTemplate();
            ROOM_TYPE type = room.getType();
            MapMaster.addToIntegerMap(templateMap, template, 1);
            MapMaster.addToIntegerMap(typeMap, type, 1);
            for (ROOM_CELL roomCell : level.getModel().getBlocks().get(room).getTileMap().getMap().values()) {
                MapMaster.addToIntegerMap(map, roomCell, 1);
            }
        }
        setValue(LEVEL_STAT.FILLER_MAP, map+"");
        setValue(LEVEL_STAT.EXIT_TEMPLATE_COUNT, templateMap.toString());
        setValue(LEVEL_STAT.ROOM_TYPE_COUNT, typeMap.toString());
        int fill = Math.round(
         100 * level.getModel().getOccupiedCells().size() / level.getModel().getCurrentWidth()
          / level.getModel().getCurrentHeight());
        setValue(LEVEL_STAT.FILL_PERCENTAGE, fill+"");


        int distance;

      int  zoneBalance;
        //max difference
        //avrg difference  1 5 12 = 4+11+7)/3

        setValue(LEVEL_STAT.FILL_PERCENTAGE, fill+"");


        level.setStats(this);


    }

    public enum LEVEL_GEN_FLAG implements OPTION {
        isMergeLinksAllowed(false),
        isRandomizedSizeSort,
        isAdjustEvenRoomX,
        isAdjustEvenRoomY,
        //        isAltExitsAllowed,
        isShearDisplacedOnly,
        isJoinAllowed,
        isShearLinkWallsAllowed(false),;
        private final Boolean defaultValue;

        LEVEL_GEN_FLAG() {
            this(false);

        }

        LEVEL_GEN_FLAG(Boolean exclusive) {
            defaultValue = exclusive;
        }

        @Override
        public Integer getMin() {
            return null;
        }

        @Override
        public Integer getMax() {
            return null;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public Boolean isExclusive() {
            return defaultValue;
        }

        @Override
        public Object[] getOptions() {
            return new Object[0];
        }

    }

    public enum LEVEL_STAT {
        ROOMS,
        FILL_PERCENTAGE,
        GRAPH_ADHERENCE,
        EXIT_TEMPLATE_COUNT,
        ROOM_TYPE_COUNT,
        FLOOR_PERCENTAGE, FAIL_REASON, FILLER_MAP,


    }
}
