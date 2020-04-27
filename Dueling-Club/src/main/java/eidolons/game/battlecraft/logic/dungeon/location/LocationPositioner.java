package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import main.game.bf.Coordinates;

/**
 * Created by JustMe on 5/10/2017.
 */
public class LocationPositioner extends Positioner  {

    public LocationPositioner(DungeonMaster master) {
        super(master);
    }

    @Override
    public Coordinates getPlayerSpawnCoordinates() {
        return getFloorWrapper().getDefaultPlayerSpawnCoordinates();
    }
}
