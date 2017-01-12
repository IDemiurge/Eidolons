package main.libgdx.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.Boxer;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 12/30/2016.
 */
public class TextureManager {
    static TextureCache cache;

    public static Texture getOrCreate(String p) {
        if (ImageManager.getPATH() != null)
            if (!ImageManager.isImage(p)) {
                p  =StringMaster.addMissingPathSegments(p, ImageManager.getPATH() );
                p  =StringMaster.removePreviousPathSegments(p, ImageManager.getPATH() );
                if (!ImageManager.isImage(p))
                return getCache().get(ImageManager.getAltEmptyListIcon());
                // don't cache if missing!
            }
        return getCache().getOrCreate(p);
    }

    public static TextureCache getCache() {
        if (cache == null)
            cache = new TextureCache(PathFinder.getImagePath());
        return cache;
    }

    public static Texture getOrCreate(Boxer<String> stringBoxer) {
       return  getOrCreate(stringBoxer.get());
    }


    public static Array<TextureRegion> getSpriteSheetFrames(String path
                                                          ) {
        return getSpriteSheetFrames(path, getColumns(path), getRows(path));
    }

    public static Array<TextureRegion> getSpriteSheetFrames(String path,
                                                            int FRAME_COLS, int FRAME_ROWS) {

        Texture sheet = TextureManager.getOrCreate(path);
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
    private static int getColumns(String path) {
        return getDimension(path, false);
    }

    private static int getRows(String path) {
        return getDimension(path, true);

    }

    private static int getDimension(String path, boolean xOrY) {
        for (String part : path.split(" ")) {
            if (part.startsWith(xOrY ? "x" : "y"))
                if (StringMaster.isNumber(part.substring(1), true)) {
                    return StringMaster.getInteger(part.substring(1));
                }
        }
        return 1;
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
