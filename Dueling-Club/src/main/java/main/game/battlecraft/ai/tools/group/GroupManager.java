package main.game.battlecraft.ai.tools.group;

import main.entity.obj.unit.Unit;
import main.entity.type.ObjAtCoordinate;
import main.game.battlecraft.ai.GroupAI;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.bf.Coordinates;

import java.util.LinkedList;
import java.util.List;

public class GroupManager {

    private Integer factor = 4;

    public void initGroupManually(ObjAtCoordinate oac) {

    }

    public void initGroups(Dungeon dungeon) {
        // by block if possible?
        // break into blocks if necessary...
        // go thru them, but make groups via adjacency
        int blockWidth = dungeon.getCellsX() / factor;
        int blockHeight = dungeon.getCellsY() / factor;
        List<Unit> units = new LinkedList<>();
        GroupAI group = new GroupAI(null);
        // minimum group area...
        for (int n = 0; n < factor; n++) {
            Coordinates[] coordinates = new Coordinates[blockWidth * blockHeight];
            int i = 0;
            for (int x = 0; x < blockWidth; x++) {
                for (int y = 0; y < blockWidth; y++) {
                    i++;
                    coordinates[i] = new Coordinates(n * blockWidth + x, n * blockHeight + y);
                }
            }

            dungeon.getGame().getUnitsForCoordinates(coordinates);
            // sort(); leader = units.getOrCreate(0);

//			group.setLeader(leader);

        }
    }

}
