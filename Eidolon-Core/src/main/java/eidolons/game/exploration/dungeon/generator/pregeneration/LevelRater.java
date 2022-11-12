package eidolons.game.exploration.dungeon.generator.pregeneration;

import eidolons.game.exploration.dungeon.generator.GeneratorEnums;
import eidolons.game.exploration.dungeon.generator.LevelData;
import eidolons.game.exploration.dungeon.generator.LevelDataMaker;
import eidolons.game.exploration.dungeon.generator.graph.GraphPath;
import eidolons.game.exploration.dungeon.generator.graph.LevelGraph;
import eidolons.game.exploration.dungeon.generator.graph.LevelGraphEdge;
import eidolons.game.exploration.dungeon.generator.graph.LevelGraphNode;
import eidolons.game.exploration.dungeon.generator.test.LevelStats;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.exploration.dungeon.struct.DungeonLevel;
import eidolons.game.exploration.dungeon.struct.LevelZone;
import eidolons.game.exploration.dungeon.generator.model.LevelModel;
import eidolons.game.exploration.dungeon.generator.model.Room;
import main.system.auxiliary.secondary.GeometryMaster;
import main.system.data.DataUnit;
import main.system.datatypes.WeightMap;
import main.system.math.MathMaster;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 8/27/2018.
 */
public class LevelRater {
    private final LevelData data;
    private final LevelModel model;
    private final LevelStats stats;
    private final LevelGraph graph;
    private final DungeonLevel level;

    public LevelRater(DungeonLevel level) {
        this.level = level;
        this.data = level.getLevelData();
        this.model = level.getModel();
        this.graph = model.getGraph();
        DataUnit<LevelDataMaker.LEVEL_REQUIREMENTS> reqs = level.getLevelData().getReqs();
        this.stats = new LevelStats(level);
    }

    /*
        :: check against previous levels - make sure it’s real different
        :: same random seed, but with an offset

        heuristics

        balances of zones, room, exits

        best packed

        maximum graph adherence

        or at least linkage...
         */
    public float rateLevel() {
        float rate = 0;
//make sure all room templates are used
        rate += getParametersRate();
        rate += getDistancesRate();
//        rate += getGraphRate();
        rate += getVariety();
        rate += getZoneBalanceRate();
        rate += getRoomBalanceRate();
        rate += getLayoutRate();

        return rate;
    }

    private float getLayoutRate() {
        List<Room> deadEnds = model.getRoomMap().values().stream().filter(r ->
         r.getUsedExits().isEmpty()).collect(Collectors.toList());

        int pen = 0;
        for (Room deadEnd : deadEnds) {
            switch (deadEnd.getType()) {
                case DEATH_ROOM:
                case GUARD_ROOM:
                    pen += 2;
                case TREASURE_ROOM:
                    break;
                default:
                    pen += 1;
            }
        }

        return model.getRoomMap().size()*2+100 - pen * 10;
    }

    private float getGraphRate() {
        for (GraphPath path : graph.getPaths()) {

        }
        LevelGraphNode tip = graph.findFirstNodeOfType(ROOM_TYPE.ENTRANCE_ROOM);
//        model.findFirstRoomOfType(ROOM_TYPE.ENTRANCE_ROOM)
        for (LevelGraphEdge edge : graph.getAdjList().get(tip)) {
//            model.getRoomLinkMap().
//            graph.getAdjList().getVar(node)
        }
        return 0;
    }

    private float getZoneBalanceRate() {
        WeightMap<String> map = new WeightMap<>(
         );
        for (LevelZone zone : model.getZones()) {
            map.put(zone.getIndex() + "", zone.getSubParts().size());
        }
        return MathMaster.getBalanceCoef(map);
    }
    private float getVariety() {
        WeightMap<GeneratorEnums.EXIT_TEMPLATE> map = new WeightMap<>(stats.getValue(LevelStats.LEVEL_STAT.EXIT_TEMPLATE_COUNT), GeneratorEnums.EXIT_TEMPLATE.class);
        return MathMaster.getBalanceCoef(map);

        //exit templates?
    }
    private float getParametersRate() {
        return (float) stats.getIntValue(LevelStats.LEVEL_STAT.FILL_PERCENTAGE);
    }

    private float getDistancesRate() {
        float preferred = getPreferredExitToEntranceDimensionRatio();
        float ratio = (float) (level.getEntranceCoordinates().dst_(level.getExitCoordinates()) /
         GeometryMaster.hyp(model.getCurrentWidth(), model.getCurrentHeight()));
        float diff = Math.abs(ratio - preferred);
        return 50 - diff * 100;
    }

    private float getPreferredExitToEntranceDimensionRatio() {
        return 0.6f;
    }



    private float getRoomBalanceRate() {
        float diff = 0;
        WeightMap<ROOM_TYPE> map = new WeightMap<>(stats.getValue(LevelStats.LEVEL_STAT.ROOM_TYPE_COUNT), ROOM_TYPE.class);

        for (ROOM_TYPE type : ROOM_TYPE.mainRoomTypes) {
            Integer n = map.get(type);
            if (n == null) {
                n = 0;
            }
            diff += Math.pow(
             Math.abs(data.getRoomCoeF(type) - n), 2);

        }
        return 100 - diff;
    }
}
