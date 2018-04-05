package eidolons.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 2/9/2018.
 */
public class GdxImageTransformer extends LwjglApplication {

    private static final String PATH = "gen\\round\\";

    public GdxImageTransformer() {
        super(new ApplicationAdapter() {
            @Override
            public void create() {
                super.create();
                roundTextures(PATH);
            }
        });
    }


    public static void main(String[] args) {
        new GdxImageTransformer();
    }

    public static Texture size(String path, int size, boolean write) {
        Texture texture = TextureCache.getOrCreate(path);
        if (texture.getWidth() == size) {
            if (texture.getHeight() == size) {
                return texture;
            }
        }
        path = StringMaster.cropFormat(path) + " " + size + StringMaster.getFormat(path);
        FileHandle handle = new FileHandle(
         PathFinder.getImagePath() +
          path);
        if (handle.exists())
            return TextureCache.getOrCreate(path);
        texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        Pixmap pixmap2 = new Pixmap(size, size, pixmap.getFormat());
        pixmap2.drawPixmap(pixmap,
         0, 0, pixmap.getWidth(), pixmap.getHeight(),
         0, 0, pixmap2.getWidth(), pixmap2.getHeight()
        );
        if (write) {
            PixmapIO.writePNG(handle, pixmap2);
        }
        return new Texture(pixmap2);
    }

    public static TextureRegion round(String path, boolean write) {
        if (GdxMaster.isLwjglThread()) {
            Pixmap rounded = roundTexture(TextureCache.getOrCreateR(path));
            path = TextureCache.getRoundedPath(path);
            FileHandle handle = new FileHandle(
             PathFinder.getImagePath() + path);
            if (write) {
                PixmapIO.writePNG(handle, rounded);
            }
        } else {
            return null;
        }
        return TextureCache.getOrCreateR(path);
    }

    public static void roundTextures(String directory) {
        CoreEngine.systemInit();
        for (String filePath : FileManager.getFileNames(FileManager.
         getFilesFromDirectory(PathFinder.getImagePath() + directory, false))) {
//            FileHandle handle=new FileHandle(filePath);
            round(directory + filePath, true);

        }
    }

    public static Pixmap roundTexture(TextureRegion textureRegion) {
        Texture texture = textureRegion.getTexture();
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        return roundPixmap(texture.getTextureData().consumePixmap());
    }

    public static Pixmap roundPixmap(Pixmap pixmap) {
        int width = pixmap.getWidth();
        int height = pixmap.getHeight();
        Pixmap round = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
        if (width != height) {
            Gdx.app.log("error", "Cannot create round image if width != height");
            round.dispose();
            return pixmap;
        }
        double radius = width / 2.0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //check if pixel is outside circle. Set pixel to transparent;
                double dist_x = (radius - x);
                double dist_y = radius - y;
                double dist = Math.sqrt((dist_x * dist_x) + (dist_y * dist_y));
                if (dist < radius) {
                    round.drawPixel(x, y, pixmap.getPixel(x, y));
                } else
                    round.drawPixel(x, y, 0);
            }
        }
        Gdx.app.log("info", "pixmal rounded!");
        return round;
    }

}
