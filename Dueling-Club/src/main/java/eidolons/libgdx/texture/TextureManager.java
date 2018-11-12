package eidolons.libgdx.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.bf.GridMaster;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.NumberUtils;
import main.system.images.ImageManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 12/30/2016.
 */
public class TextureManager {
    public static final int CELL_TEXTURE_ID = 1;
    private static final String emptyCellPath = "ui/cells/Empty Cell v3.png";
    //    public static final int CELL_SHADOW_TEXTURE_ID = 1;
//    public static final int CELL_SHADE_TEXTURE_ID = 1;
//    public static final int CELL_LIGHT_TEXTURE_ID = 1;
//    public static final int CELL_EMITTER_TEXTURE_ID = 1;
    public static String SINGLE_SPRITE = "[SINGLE_SPRITE]";
    private static SpriteCache spriteCache;
    private static int gridHeight;
    private static int cellIdStart;
    private static int cacheId;
    private static int backgroundId;
    private static int cellSpriteCacheId;

    public static SpriteCache getSpriteCache() {
        if (spriteCache == null) {
            cacheId = 0;
            spriteCache = new SpriteCache();
        }
        return spriteCache;
    }

    public static void addCellsToCache(int cols, int rows) {
        cellIdStart = cacheId;
        gridHeight = rows;
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                getSpriteCache().beginCache();
                getSpriteCache().add(TextureCache.createTexture(emptyCellPath, false),
                 x * GridMaster.CELL_W,
                 y * GridMaster.CELL_H);
                cacheId++;
                getSpriteCache().endCache();

            }
        }
        cellSpriteCacheId = cacheId;
    }

    public static void drawFromSpriteCache(int id) {
        getSpriteCache().begin();
        getSpriteCache().draw(id);
        getSpriteCache().end();
    }

    public static void initBackgroundCache(TextureRegion backTexture) {
        backgroundId = cacheId;
        getSpriteCache().beginCache();
        getSpriteCache().add(backTexture, 0, 0);
        cacheId++;
        getSpriteCache().endCache();
    }

    public static int getBackgroundId() {
        return backgroundId;
    }

    public static int getCellSpriteCacheId() {
        return cellSpriteCacheId;
    }


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
        if (sheet == TextureCache.getEmptyTexture()) {
            main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>> Sprite not found: " + path);
        }
        TextureRegion[][] tmp = null;
        try {
            tmp = TextureRegion.split(sheet,
             sheet.getWidth() / FRAME_COLS,
             sheet.getHeight() / FRAME_ROWS);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
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
            if (NumberUtils.isNumber(y, true)) {
                return NumberUtils.getInteger(y);
            }
        }
        path = StringMaster.cropLast(path, y);
        String x = StringMaster.getLastPart(path, " ");
        if (NumberUtils.isNumber(x, true)) {
            return NumberUtils.getInteger(x);
        }
        return xOrY ?
         getXY(origPath).getKey() :
         getXY(origPath).getValue();
    }

    public static float getFrameNumber(String path) {
        return getRows(path) * getColumns(path);
    }

    public static Pair<Integer, Integer> getXY(String origPath) {
        int x = 1;
        int y = 1;
        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();
        Texture texture = TextureCache.getOrCreate(origPath);
        for (int i = 7; i >= 1; i--) {
            if (texture.getWidth() % i == 0) {
                x = i;
                xs.add(i);
            }

            if (texture.getHeight() % i == 0) {
                y = i;
                ys.add(i);
            }
        }
        //prefer square
        {
            for (int x1 : xs) {
                if (x1 == 0) {
                    continue;
                }
                final int w = texture.getWidth() / x1;
                for (int y1 : ys) {
                    if (y1 == 0) {
                        continue;
                    }
                    int h = texture.getHeight() / y1;
                    if (w == h) {
                        return new ImmutablePair<>(x1, y1);
                    }
                }
            }
        }

        if (x == 0) {
            x = 1;
        }
        if (y == 0) {
            y = 1;
        }
        return new ImmutablePair<>(x, y);

    }

    public static int getCellSpriteCacheId(int gridX, int gridY) {
        return cellIdStart + gridX * gridHeight + gridY;
    }

    public static TextureRegion getParamTexture(PARAMETER sub) {
        return TextureCache.getOrCreateR(ImageManager.getValueIconPath(sub));
    }


//    public static Texture toTexture(BufferedImage img) throws IOException {
//
//        ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
//        ImageIO.write(img, "jpg", baos);
//        baos.flush();
//
//        String base64String= Base64.encode(baos.toByteArray());
//        baos.close();
//
//        byte[] bytearray = Base64.decode(base64String);
//
//        Gdx2DPixmap gpm = new Gdx2DPixmap(new ByteArrayInputStream(bytearray),
//         Gdx2DPixmap.GDX2D_FORMAT_RGB888);
//        Pixmap pixmap = new Pixmap(gpm);
//        return  new Texture(pixmap);
//    }
}
