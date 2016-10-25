package main.game.logic.dungeon.building;

import main.game.battlefield.Coordinates;
import main.game.logic.dungeon.building.DungeonBuilder.BLOCK_TYPE;

import java.util.List;

public class RoomBlock extends MapBlock {

    public RoomBlock(int id, BLOCK_TYPE b, MapZone zone, DungeonPlan map,
                     List<Coordinates> coordinates) {
        super(id, b, zone, map, coordinates);
        // TODO Auto-generated constructor stub
    }

}
