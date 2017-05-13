package main.game.battlecraft.logic.dungeon.location.building;

import main.game.battlecraft.logic.dungeon.location.building.LocationBuilder.BLOCK_TYPE;
import main.game.bf.Coordinates;

import java.util.List;

public class RoomBlock extends MapBlock {

    public RoomBlock(int id, BLOCK_TYPE b, MapZone zone, DungeonPlan map,
                     List<Coordinates> coordinates) {
        super(id, b, zone, map, coordinates);
        // TODO Auto-generated constructor stub
    }

}
