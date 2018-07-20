package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.system.auxiliary.data.ArrayMaster;

import java.util.Arrays;

/**
 * Created by JustMe on 2/14/2018.
 */
public class RoomModelTransformer {
    public static void wrapInWalls(String[][] cells) {
        int rows = cells[0].length;
        int cols = cells.length;
        //add wall lines above and below
        cells[0] = getWallLine(rows);
        cells[cols - 1] = getWallLine(rows);
        //wrap other lines left and right
        for (int n = 1; n+1 < cells.length; n++) {
            cells[n] = getWallModifiedArray(cells[n]);
        }
    }

    private static String[] getWallModifiedArray(String[] sub) {
        String[] newArray = Arrays.copyOf(sub, sub.length + 2);
        //shift 1 forward
        for (int j = 1; j <= sub.length; j++) {
            newArray[j] = sub[j - 1];
        }
        newArray[0] = getWallSymbol();
        newArray[newArray.length-1] = getWallSymbol();
        return newArray;
    }

    private static String getWallSymbol() {
        return ROOM_CELL.WALL.getSymbol();
    }

    private static String[] getWallLine(int rows) {
        String[] array = new String[rows];
        Arrays.fill(array, getWallSymbol());
        return array;
    }

    public static RoomModel rotate(RoomModel model, Boolean... rotations) {
        for (Boolean sub : rotations) {
            ArrayMaster.rotateMatrix_(model.getCells(), sub);
            main.system.auxiliary.log.LogMaster.log(1,model+" rotated: "
             +model.getCellsString() );
        }
        model.setRotated(rotations);
        return model;
    }
}
