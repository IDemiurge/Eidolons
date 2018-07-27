package eidolons.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.PathUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 4/29/2018.
 */
public class TiledNinePatchGenerator implements ApplicationListener {
    private static VISUALS[] replaced_sauron = {
     VISUALS.INFO_PANEL,
     VISUALS.INFO_PANEL_HC,
     VISUALS.END_PANEL,
     VISUALS.INV_PANEL,
     VISUALS.INFO_PANEL_WIDE,
     VISUALS.INFO_PANEL_DESCRIPTION,

     VISUALS.INFO_PANEL_LARGE,
     VISUALS.INFO_PANEL_TEXT_SMALL,
     VISUALS.INFO_PANEL_TEXT,

    };

    private static void generateNinePatches() {

        for (VISUALS sub : replaced_sauron) {
            generate(NINE_PATCH.SAURON, BACKGROUND_NINE_PATCH.PATTERN,
             sub.getWidth() //* 11 / 10
              - NINE_PATCH.SAURON.cornerOffsetX1
              - NINE_PATCH.SAURON.cornerOffsetX3,
             sub.getHeight() //* 105 / 100
              + NINE_PATCH.SAURON.cornerOffsetY1
              - NINE_PATCH.SAURON.cornerOffsetY3
             , sub.getImgPath());
        }
    }

    public static void main(String[] args) {
        TextureCache.setReturnEmptyOnFail(false);
        CoreEngine.systemInit();
        new LwjglApplication(new TiledNinePatchGenerator(), "", 640, 480);

    }

    public static Texture generate(NINE_PATCH ninePatch, BACKGROUND_NINE_PATCH backgroundNinePatch,
                                int maxWidth, int maxHeight) {
       return generate(ninePatch, backgroundNinePatch, maxWidth, maxHeight, null);
    }

    public static Texture generate(NINE_PATCH ninePatch, BACKGROUND_NINE_PATCH backgroundNinePatch,
                                int maxWidth, int maxHeight, String path) {
        if (path == null)
            path = getPath(ninePatch, backgroundNinePatch, maxWidth, maxHeight);

        String partPath = StrPathBuilder.build(ninePatch.path,
         "parts") + PathUtils.getPathSeparator();


      return   generate(
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

         maxWidth, maxHeight,
         ninePatch.cornerOffsetX1,
         ninePatch.cornerOffsetY1,
         ninePatch.cornerOffsetX2,
         ninePatch.cornerOffsetY2,
         ninePatch.cornerOffsetX3,
         ninePatch.cornerOffsetY3,
         ninePatch.cornerOffsetX4,
         ninePatch.cornerOffsetY4,
         path
        );
    }

    private static String getPath(NINE_PATCH ninePatch, BACKGROUND_NINE_PATCH
     backgroundNinePatch, int maxWidth, int maxHeight) {
     return    ninePatch.path +
         PathUtils.getPathSeparator() +
         backgroundNinePatch.name().toLowerCase() + " " +
         maxWidth + " " + maxHeight + ".png";
    }

    public static Texture generate(

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
     int cornerOffsetX1,
     int cornerOffsetY1,
     int cornerOffsetX2,
     int cornerOffsetY2,
     int cornerOffsetX3,
     int cornerOffsetY3,
     int cornerOffsetX4,
     int cornerOffsetY4,

     String path) {

        FileHandle handle = new FileHandle(
         PathFinder.getImagePath() +
          path);

        if (bottom == null|| bottom==TextureCache.getEmptyTexture())
        {
//            GdxImageMaster.flip()
            bottom = top;
        }
        if (left == null|| left==TextureCache.getEmptyTexture())
            left = right;

        int timesL = maxHeight / left.getHeight();
        int timesR = maxHeight / right.getHeight();
        int timesT = maxWidth / top.getWidth();
        int timesB = maxWidth / bottom.getWidth();

        int w = Math.max(timesB * bottom.getWidth(), timesT * top.getWidth());
        int h = Math.max(timesL * left.getHeight(), timesR * right.getHeight());
        w += -cornerOffsetX1 * 2;
        h += cornerOffsetY1;
        h += -cornerOffsetY3;

        Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);

        int offsetX = -cornerOffsetX1 / 2;
        int offsetY = cornerOffsetY1 / 2;
        int offsetY2 = cornerOffsetY3 / 2;

        if (background != null)
        {
            GdxImageMaster.drawTextureRegion(offsetX, offsetY, background,
             w-offsetX*2,
             h-offsetY+offsetY2 ,
             pixmap);
        }
        GdxImageMaster.drawTexture(offsetX, offsetY, 0, 1, left, timesL, pixmap);
        GdxImageMaster.drawTexture(-offsetX + w - right.getWidth(), offsetY, 0, 1, right, timesR, pixmap);
        GdxImageMaster.drawTexture(offsetX, h - top.getHeight() + offsetY2, 1, 0, bottom, timesT, pixmap);
        GdxImageMaster.drawTexture(offsetX, offsetY, 1, 0, top, timesB, pixmap);

        GdxImageMaster.drawTexture(0,
         0,
         0, 0, corner1, 1, pixmap);
        GdxImageMaster.drawTexture(w - corner2.getWidth(),
         0,
         0, 0, corner2, 1, pixmap);
        GdxImageMaster.drawTexture(0,
         h - corner3.getHeight(),
         0, 0, corner3, 1, pixmap);
        GdxImageMaster.drawTexture(w - corner4.getWidth(),
         h - corner4.getHeight(),
         0, 0, corner4, 1, pixmap);

        GdxImageMaster.writeImage(handle, pixmap);
        return TextureCache.getOrCreate(path);
    }

    @Override
    public void create() {

        generate(NINE_PATCH.FRAME, BACKGROUND_NINE_PATCH.TRANSPARENT,1950, 1210);
//        generateNinePatches();
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

    public static Texture getOrCreateNinePatch(NINE_PATCH ninePatch,
                                               BACKGROUND_NINE_PATCH background,
                                               int w, int h) {
        Texture texture = TextureCache.getOrCreate(getPath(ninePatch, background, w, h));
        if (texture!=null && texture!=TextureCache.getEmptyTexture())
            return texture;
        return generate(ninePatch, background, w, h);
    }

    public enum BACKGROUND_NINE_PATCH {
        TRANSPARENT,
        BLACK,
        SEMI,
        PATTERN,;
        String path = StrPathBuilder.build(PathFinder.getComponentsPath(),
         "ninepatch", "background" , name().toLowerCase()+".png");
    }

    public enum NINE_PATCH_PADDING {
        SAURON(40, 30, 20, 20),
        ;
        public int top;
        public int bottom;
        public int left;
        public int right;

        NINE_PATCH_PADDING(int top, int bottom, int left, int right) {
            this.top = top;
            this.bottom = bottom;
            this.left = left;
            this.right = right;
        }
    }
        public enum NINE_PATCH {
        SAURON(-16, 60, -16, -34),
        FRAME(),
        DEMIURGE,
        LIGHT,;
        String path = StrPathBuilder.build(PathFinder.getComponentsPath(),
         "ninepatch", name().toLowerCase());

        int cornerOffsetX1;
        int cornerOffsetY1;
        int cornerOffsetX2;
        int cornerOffsetY2;
        int cornerOffsetX3;
        int cornerOffsetY3;
        int cornerOffsetX4;
        int cornerOffsetY4;

        NINE_PATCH() {
        }

        NINE_PATCH(int cornerOffsetX1, int cornerOffsetY1, int cornerOffsetX3, int cornerOffsetY3) {
            this.cornerOffsetX1 = cornerOffsetX1;
            this.cornerOffsetY1 = cornerOffsetY1;
            this.cornerOffsetX3 = cornerOffsetX3;
            this.cornerOffsetY3 = cornerOffsetY3;

            this.cornerOffsetX2 = -cornerOffsetX1;
            this.cornerOffsetY2 = cornerOffsetY1;
            this.cornerOffsetX4 = -cornerOffsetX3;
            this.cornerOffsetY4 = cornerOffsetY3;
        }
    }
}
