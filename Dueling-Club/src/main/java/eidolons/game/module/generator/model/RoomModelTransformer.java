package eidolons.game.module.generator.model;

import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.data.ArrayMaster;

import java.util.Arrays;

/**
 * Created by JustMe on 2/14/2018.
 */
public class RoomModelTransformer {
    public static final String WRAP_SEPARATOR = ContainerUtils.getFormattedContainerSeparator();

    public static void wrapInWalls(String[][] cells, int wrapWidth, String wrapType) {
        int rows = cells[0].length;
        int cols = cells.length;
        //add wall lines above and below
        for (int i = 0; i < wrapWidth; i++) {
            cells[i] = getWallLine(rows, getWrapType(wrapType, i));
            cells[cols - 1 - i] = getWallLine(rows, getWrapType(wrapType, i));
        }
        //wrap other lines left and right
        for (int n = 1; n + 1 < cells.length; n++) {
            boolean outer = wrapWidth > 1 &&
             (n == 1 || n + 2 == cells.length);
            cells[n] = getModifiedArray(outer, cells[n], wrapWidth, wrapType);
        }

    }

    private static String[] getModifiedArray(boolean outer, String[] sub, int wrapWidth, String wrapType) {
        String[] newArray = outer ? sub : Arrays.copyOf(sub, sub.length + wrapWidth * 2);
        //shift 1 forward
        if (!outer)
            for (int j = wrapWidth; j - wrapWidth + 1 <= sub.length; j++) {
                newArray[j] = sub[j - wrapWidth];
            }
        if (outer) {
            newArray[0] = getWrapType(wrapType, 0); // try it
            newArray[newArray.length - 1] = getWrapType(wrapType, 0);
        } else
            for (int i = 0; i < wrapWidth; i++) {
                newArray[i] = getWrapType(wrapType, i); // try it
                newArray[newArray.length - i - 1] = getWrapType(wrapType, i);
            }
        return newArray;
    }

    private static String getWrapType(String wrapType, int i) {
        if (!wrapType.contains(WRAP_SEPARATOR))
            return wrapType;
        return wrapType.split(WRAP_SEPARATOR)[i];
    }

    private static String[] getWallLine(int rows, String wrapType) {
        String[] array = new String[rows];
        Arrays.fill(array, wrapType);
        return array;
    }

    public static RoomModel rotate(RoomModel model, Boolean... rotations) {
        for (Boolean sub : rotations) {
            ArrayMaster.rotateMatrix_(model.getCells(), sub);
            main.system.auxiliary.log.LogMaster.log(1, model + " rotated: "
             + model.getCellsString());
        }
        model.setRotations(rotations);
        return model;
    }

    private RoomModel createMirrorRoomModel(RoomModel roomModel) {

        return roomModel;
    }
}
