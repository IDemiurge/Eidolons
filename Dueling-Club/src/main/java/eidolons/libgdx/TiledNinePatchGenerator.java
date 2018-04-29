package eidolons.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 4/29/2018.
 */
public class TiledNinePatchGenerator implements ApplicationListener {

    public static void generate(

     Texture top,
     Texture bottom,
     Texture right,
     Texture left,
     Texture corner1,
     Texture corner2,
     Texture corner3,
     Texture corner4,

     Texture background,
     int maxWidth, int maxHeight,
     String path) {

        FileHandle handle = new FileHandle(
         PathFinder.getImagePath() +
          path);

        if (bottom==null )
            bottom = top;
        if (left==null )
            left = right;

        int timesL = maxHeight / left.getHeight();
        int timesR = maxHeight / right.getHeight();
        int timesT = maxWidth / top.getWidth();
        int timesB = maxWidth / bottom.getWidth();

        int w = Math.max(timesB * left.getWidth(), timesT * right.getWidth());
        int h = Math.max(timesL * left.getHeight(), timesR * right.getHeight());
        Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);

        if (background != null)
            drawTexture(0, 0, 0, 0, background, 1, pixmap);
        //TODO tile! or stretch

        drawTexture(0, 0, 0, 1, left, timesL, pixmap);
        drawTexture(w - right.getWidth(), 0, 0, 1, right, timesR, pixmap);
        drawTexture(0, 0, 1, 0, bottom, timesB, pixmap);
        drawTexture(0, h - top.getHeight(), 1, 0, top, timesT, pixmap);

        drawTexture(0, 0, 0, 0, corner1, 1, pixmap);
        drawTexture(w - corner2.getWidth(), 0, 0, 0, corner2, 1, pixmap);
        drawTexture(0, h - corner3.getHeight(), 0, 0, corner3, 1, pixmap);
        drawTexture(w - corner4.getWidth(), h - corner4.getHeight(), 0, 0, corner4, 1, pixmap);

        GdxImageTransformer.writeImage(handle, pixmap);
    }

    private static void drawTexture(int x, int y, int dX, int dY,
                                    Texture textureRegion, int times, Pixmap pixmap) {

        textureRegion.getTextureData().prepare();
        Pixmap pixmap2 = textureRegion.getTextureData().consumePixmap();
        for (int i = 0; i < times; i++) {
            pixmap.drawPixmap(pixmap2, x, y);
            x += textureRegion.getWidth() * dX;
            y += textureRegion.getHeight() * dY;
        }
    }


    public static void main(String[] args) {
        TextureCache.setReturnEmptyOnFail(false);
        CoreEngine.systemInit();
        new LwjglApplication(new TiledNinePatchGenerator(), "", 640, 480);

    }

    @Override
    public void create() {
        generate(NINE_PATCH.SAURON, BACKGROUND_NINE_PATCH.TRANSPARENT, 800, 600);
    }
    public static void generate(NINE_PATCH ninePatch, BACKGROUND_NINE_PATCH backgroundNinePatch,
                                int maxWidth, int maxHeight) {

        String path = ninePatch.path +
         StringMaster.getPathSeparator()+
         backgroundNinePatch.name().toLowerCase() + " " +
         maxWidth + " " + maxHeight + ".png";

        String partPath = StrPathBuilder.build(ninePatch.path,
         "parts")+ StringMaster.getPathSeparator();

        generate(
         TextureCache.getOrCreate(partPath + "top.png"),
         TextureCache.getOrCreate(partPath + "bottom.png"),
         TextureCache.getOrCreate(partPath + "right.png"),
         TextureCache.getOrCreate(partPath + "left.png"),

         TextureCache.getOrCreate(partPath + "corner1.png"),
         TextureCache.getOrCreate(partPath + "corner2.png"),
         TextureCache.getOrCreate(partPath + "corner3.png"),
         TextureCache.getOrCreate(partPath + "corner4.png"),

         backgroundNinePatch == BACKGROUND_NINE_PATCH.TRANSPARENT
          ? null
          : TextureCache.getOrCreate(backgroundNinePatch.path),
         maxWidth, maxHeight, path
        );
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    public enum BACKGROUND_NINE_PATCH {
        TRANSPARENT,
        BLACK,
        SEMI,
        PATTERN,;
        String path = StrPathBuilder.build(PathFinder.getComponentsPath(),
         "ninepatch", "background " + name().toLowerCase());
    }

    public enum NINE_PATCH {
        SAURON,
        DEMIURGE,
        LIGHT,;
        String path = StrPathBuilder.build(PathFinder.getComponentsPath(),
         "ninepatch", name().toLowerCase());
    }
}
