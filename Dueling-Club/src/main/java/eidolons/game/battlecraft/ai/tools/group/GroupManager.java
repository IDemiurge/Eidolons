package eidolons.game.battlecraft.ai.tools.group;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.Floor;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class GroupManager {

    public void initGroupManually(ObjAtCoordinate oac) {

    }

    public void initGroups(Floor floor) {
        // by block if possible?
        // break into blocks if necessary...
        // go thru them, but make groups via adjacency
        Integer factor = 4;
        int blockWidth = floor.getCellsX() / factor;
        int blockHeight = floor.getCellsY() / factor;
        List<Unit> units = new ArrayList<>();
//        GroupAI group = new GroupAI(null);
        // minimum group area...
        for (int n = 0; n < factor; n++) {
            Coordinates[] coordinates = new Coordinates[blockWidth * blockHeight];
            int i = 0;
            for (int x = 0; x < blockWidth; x++) {
                for (int y = 0; y < blockWidth; y++) {
                    i++;
                    coordinates[i] = Coordinates.get(n * blockWidth + x, n * blockHeight + y);
                }
            }

            floor.getGame().getUnitsForCoordinates(coordinates);
            // sort(); leader = units.getOrCreate(0);

//			group.setLeader(leader);

        }
    }

}
