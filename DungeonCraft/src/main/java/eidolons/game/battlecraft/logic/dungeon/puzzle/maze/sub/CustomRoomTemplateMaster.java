package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.sub;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.exploration.dungeons.generator.model.RoomModel;
import eidolons.game.exploration.dungeons.generator.model.RoomTemplateMaster;
import eidolons.game.exploration.dungeons.generator.tilemap.TileMapper;
import main.data.filesys.PathFinder;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.data.FileManager;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static eidolons.game.exploration.dungeons.generator.GeneratorEnums.EXIT_TEMPLATE;
import static eidolons.game.exploration.dungeons.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import static main.system.auxiliary.log.LogMaster.log;

public class CustomRoomTemplateMaster {
    private final Map<ROOM_TEMPLATE_GROUP, Set<RoomModel>> roomMap = new LinkedHashMap<>();

    public CustomRoomTemplateMaster(ROOM_TEMPLATE_GROUP[] data) {
        for (ROOM_TEMPLATE_GROUP group : data) {
            Set<RoomModel> models = new LinkedHashSet<>();
            for (LocationBuilder.ROOM_TYPE type : LocationBuilder.ROOM_TYPE.values()) {
                String contents = FileManager.readFile(
                        PathFinder.getMapBlockFolderPath() +
                                group + "/" + type + ".txt");
                if (contents.isEmpty()) {
                    continue;
                }
                for (String room : contents.split(RoomTemplateMaster.MODEL_SPLITTER)) {
                    room = room.trim();

                    String[][] cells = TileMapper.toSymbolArray(room);
                    models.add(new RoomModel(cells, type, EXIT_TEMPLATE.CROSSROAD));
                }
            }
            log(1, group + " room groups has models: \n " + models);
            roomMap.put(group, models);
        }
    }

    public RoomModel getNextRandomModel(LocationBuilder.ROOM_TYPE roomTypeForPuzzle,
                                        EXIT_TEMPLATE template,
                                        FACING_DIRECTION randomFacing,
                                        ROOM_TEMPLATE_GROUP templateGroup) {
        Set<RoomModel> rooms = roomMap.get(templateGroup);
        return rooms.stream().filter(room -> room.getType() == roomTypeForPuzzle).findAny().get();
    }
}
