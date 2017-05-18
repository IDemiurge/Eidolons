package main.game.battlecraft.logic.dungeon.location;

import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.battlecraft.logic.dungeon.universal.Positioner;
import main.game.bf.Coordinates;

/**
 * Created by JustMe on 5/10/2017.
 */
public class LocationPositioner extends Positioner<Location> {

    public LocationPositioner(DungeonMaster<Location> master) {
        super(master);
    }

    @Override
    public Coordinates getPlayerSpawnCoordinates() {
        return getDungeon().getPlayerSpawnCoordinates();
    }
}
