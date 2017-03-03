package main.game.logic.dungeon.special;

import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.dungeon.building.DungeonBuilder.BLOCK_TYPE;
import main.game.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.logic.dungeon.building.DungeonPlan;
import main.game.logic.dungeon.building.MapBlock;

import java.util.Map;

public class ChestMaster {

    public static Map<Coordinates, ObjType> initTreasures(Dungeon dungeon) {
        DungeonPlan plan = dungeon.getPlan();
        for (MapBlock block : plan.getBlocks()) {
            if (block.getType() == BLOCK_TYPE.CULDESAC) {

            }
            if (block.getRoomType() == ROOM_TYPE.SECRET_ROOM) {

            }
            if (block.getRoomType() == ROOM_TYPE.TREASURE_ROOM) {

            }
        }

//        new Treasure(type, value);

//        return map;
        return null;
    }

    public class Treasure {

    }
}
