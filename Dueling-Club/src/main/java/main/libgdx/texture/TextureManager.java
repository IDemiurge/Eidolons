package main.libgdx.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import javafx.util.Pair;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.Boxer;
import main.system.images.ImageManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 12/30/2016.
 */
public class TextureManager {
    private static TextureCache cache;
    public static String SINGLE_SPRITE="[SINGLE_SPRITE]";

    public static Texture getOrCreate(String p) {
        if (ImageManager.getPATH() != null)
            if (!ImageManager.isImage(p)) {
                p = StringMaster.addMissingPathSegments(p, ImageManager.getPATH());
                p = StringMaster.removePreviousPathSegments(p, ImageManager.getPATH());
                if (!ImageManager.isImage(p))
                    return getCache().get(ImageManager.getAltEmptyListIcon());
                // don't cache if missing!
            }
        return getCache().getOrCreate(p);
    }

    public static Texture create(String p) {
        return getCache().create(p);

    }
    public static TextureCache getCache() {
        if (cache == null)
            cache = new TextureCache(PathFinder.getImagePath());
        return cache;
    }

    public static Texture getOrCreate(Boxer<String> stringBoxer) {
        return getOrCreate(stringBoxer.get());
    }


    public static Array<TextureRegion> getSpriteSheetFrames(String path,
                                                            boolean singleSprite, Texture texture) {
        if (path==null )
            return getSpriteSheetFrames(path.replace(SINGLE_SPRITE,""), 1, 1, texture );
        if (path.contains(SINGLE_SPRITE))
            return getSpriteSheetFrames(path.replace(SINGLE_SPRITE,""), 1, 1, texture );
        if (singleSprite)
        return getSpriteSheetFrames(path, 1, 1, null );
        return getSpriteSheetFrames(path, getColumns(path), getRows(path), texture );
    }

    public static Array<TextureRegion> getSpriteSheetFrames(String path,
                                                            int FRAME_COLS,
                                                            int FRAME_ROWS, Texture texture) {
//if (FRAME_COLS==1 && FRAME_ROWS==1){
//    Pair<Integer, Integer> xy = get
//}

        if (FRAME_COLS==0   )
            FRAME_COLS=1;
        if (FRAME_ROWS==0   )
            FRAME_ROWS=1;
        Texture sheet =path==null ? texture : TextureManager.getOrCreate(path);
        TextureRegion[][] tmp = TextureRegion.split(sheet,
                sheet.getWidth() / FRAME_COLS,
                sheet.getHeight() / FRAME_ROWS);

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
        List<Integer> xs = new LinkedList<>();
        List<Integer> ys = new LinkedList<>();
        Texture texture = getOrCreate(origPath);
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
                if (x1==0)continue;
                final int w = texture.getWidth() / x1;
                for (int y1 : ys) {
                    if (y1==0)continue;
                    int h = texture.getHeight() / y1;
                    if (w == h)
                        return new Pair<>(x1, y1);
                }
            }
        }

        if (x ==0)x=1;
        if (y ==0)y=1;
        return new Pair<>(x, y);

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
