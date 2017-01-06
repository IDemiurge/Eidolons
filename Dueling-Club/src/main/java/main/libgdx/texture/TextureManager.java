package main.libgdx.texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import main.data.filesys.PathFinder;
import main.system.images.ImageManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by JustMe on 12/30/2016.
 */
public class TextureManager {
    static TextureCache cache;

    public static Texture getOrCreate(String p) {
        if (ImageManager.getPATH() != null)
            if (!ImageManager.isImage(p)) {
                return   getCache().get(ImageManager.getAltEmptyListIcon());
                // don't cache if missing!
            }
       return  getCache().getOrCreate(p);
    }

    public static TextureCache getCache() {
        if (cache==null )
            cache = new TextureCache(PathFinder.getImagePath());
        return cache;
    }

    public static Texture toTexture(BufferedImage img) throws IOException {

        ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
        ImageIO.write(img, "jpg", baos);
        baos.flush();

        String base64String= Base64.encode(baos.toByteArray());
        baos.close();

        byte[] bytearray = Base64.decode(base64String);

        Gdx2DPixmap gpm = new Gdx2DPixmap(new ByteArrayInputStream(bytearray),
         Gdx2DPixmap.GDX2D_FORMAT_RGB888);
        Pixmap pixmap = new Pixmap(gpm);
        return  new Texture(pixmap);
    }
}
