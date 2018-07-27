package eidolons.game.module.dungeoncrawl.generator.model;

import main.system.auxiliary.data.ArrayMaster;

import java.util.Arrays;

/**
 * Created by JustMe on 2/14/2018.
 */
public class RoomModelTransformer {
    public static void wrapInWalls(String[][] cells, int wrapWidth, String wrapType) {
        int rows = cells[0].length;
        int cols = cells.length;
        //add wall lines above and below
        for (int i = 0; i < wrapWidth; i++) {
            cells[i] = getWallLine(rows, wrapType);
            cells[cols - 1-i] = getWallLine(rows, wrapType);
        }
        //wrap other lines left and right
        for (int n = wrapWidth; n+wrapWidth < cells.length; n++) {
            cells[n] = getModifiedArray(cells[n], wrapWidth, wrapType);
        }
    }

    private static String[] getModifiedArray(String[] sub, int wrapWidth, String wrapType) {
        String[] newArray = Arrays.copyOf(sub, sub.length + wrapWidth*2);
        //shift 1 forward
        for (int j = wrapWidth; j <= sub.length; j++) {
            newArray[j] = sub[j - wrapWidth];
        }
        for (int i = 0; i < wrapWidth; i++) {
            newArray[i] = wrapType;
            newArray[newArray.length-i-1] = wrapType;
        }
        return newArray;
    }

    private static String[] getWallLine(int rows, String wrapType) {
        String[] array = new String[rows];
        Arrays.fill(array, wrapType);
        return array;
    }

    public static RoomModel rotate(RoomModel model, Boolean... rotations) {
        for (Boolean sub : rotations) {
            ArrayMaster.rotateMatrix_(model.getCells(), sub);
            main.system.auxiliary.log.LogMaster.log(1,model+" rotated: "
             +model.getCellsString() );
        }
        model.setRotations(rotations);
        return model;
    }
    private RoomModel createMirrorRoomModel(RoomModel roomModel) {

        return roomModel;
    }
}
