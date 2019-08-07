package main.libgdx.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 12/30/2016.
 */
public class TextureManager {
    public static String SINGLE_SPRITE = "[SINGLE_SPRITE]";

    public static Array<TextureRegion> getSpriteSheetFrames(String path,
                                                            boolean singleSprite, Texture texture) {
        if (path == null) {
            return getSpriteSheetFrames(path.replace(SINGLE_SPRITE, ""), 1, 1, texture);
        }
        if (path.contains(SINGLE_SPRITE)) {
            return getSpriteSheetFrames(path.replace(SINGLE_SPRITE, ""), 1, 1, texture);
        }
        if (singleSprite) {
            return getSpriteSheetFrames(path, 1, 1, null);
        }
        return getSpriteSheetFrames(path, getColumns(path), getRows(path), texture);
    }

    public static Array<TextureRegion> getSpriteSheetFrames(String path,
                                                            int FRAME_COLS,
                                                            int FRAME_ROWS, Texture texture) {
//if (FRAME_COLS==1 && FRAME_ROWS==1){
//    Pair<Integer, Integer> xy = get
//}

        if (FRAME_COLS == 0) {
            FRAME_COLS = 1;
        }
        if (FRAME_ROWS == 0) {
            FRAME_ROWS = 1;
        }
        Texture sheet = path == null ? texture : TextureCache.getOrCreate(path);
        TextureRegion[][] tmp = null;
        try {
            tmp = TextureRegion.split(sheet,
                            sheet.getWidth() / FRAME_COLS,
                            sheet.getHeight() / FRAME_ROWS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Array<>();
        }

        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        return new Array<>(frames);
    }

    public static int getColumns(String path) {
        return getDimension(path, true);
    }

    public static int getRows(String path) {
        return getDimension(path, false);

    }

    public static int getDimension(String origPath, boolean xOrY) {
        String path = StringMaster.cropFormat(origPath);
        String y = StringMaster.getLastPart(path, " ");

        if (!xOrY) {
            if (StringMaster.isNumber(y, true)) {
                return StringMaster.getInteger(y);
            }
        }
        path = StringMaster.cropLast(path, y);
        String x = StringMaster.getLastPart(path, " ");
        if (StringMaster.isNumber(x, true)) {
            return StringMaster.getInteger(x);
        }
        return 0;
    }

    public static float getFrameNumber(String path) {
        return getRows(path) * getColumns(path);
    }

}
