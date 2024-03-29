package libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import libgdx.assets.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.PathUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

/**
 * Created by JustMe on 4/29/2018.
 */
public class TiledNinePatchGenerator implements ApplicationListener {
    private static final VISUALS[] replaced_sauron = {
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
                                   int maxWidth, int maxHeight, boolean fillWithBlack) {
        return generate(ninePatch, backgroundNinePatch, maxWidth, maxHeight, null, fillWithBlack);
    }

    public static Texture generate(NINE_PATCH ninePatch, BACKGROUND_NINE_PATCH backgroundNinePatch,
                                   int maxWidth, int maxHeight, String path) {
        return generate(ninePatch, backgroundNinePatch, maxWidth, maxHeight, path, false);
    }

    public static Texture generate(NINE_PATCH ninePatch, BACKGROUND_NINE_PATCH backgroundNinePatch,
                                   int maxWidth, int maxHeight, String path, boolean fillWithBlack) {
        if (path == null)
            path = getPath(ninePatch, backgroundNinePatch, maxWidth, maxHeight);

        String partPath = StrPathBuilder.build(ninePatch.path,
         "parts") + PathUtils.getPathSeparator();

        return generate(
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
         path,
         ninePatch.isPreventOverlapping(),
         fillWithBlack
        );
    }

    private static String getPath(NINE_PATCH ninePatch, BACKGROUND_NINE_PATCH
     backgroundNinePatch, int maxWidth, int maxHeight) {
        return ninePatch.path +
         PathUtils.getPathSeparator() +
         backgroundNinePatch.name().toLowerCase() + " " +
         maxWidth + " " + maxHeight + ".png";
    }
//Gdx revamp - try to pass a batch and just draw, maybe make a Drawable class like this - worthy of libgdx contrib!
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
     String path,
     boolean preventOverlapping,
     boolean fillWithBlack) {
        boolean write = Flags.isIDE() && !Flags.isIggDemo();

        FileHandle handle = GDX.file(
         PathFinder.getImagePath() +
          path);
        if (bottom == null || bottom == TextureCache.getMissingTexture()) {
            //            GdxImageMaster.flip()
            bottom = top;
        }
        if (left == null || left == TextureCache.getMissingTexture())
            left = right;
        int offset = preventOverlapping ? 1 : 0;
        int timesL = maxHeight / left.getHeight();
        int timesR = maxHeight / right.getHeight();
        int timesT = maxWidth / top.getWidth();
        int timesB = maxWidth / bottom.getWidth();

        int w = Math.max(timesB * bottom.getWidth(), timesT * top.getWidth());
        int h = Math.max(timesL * left.getHeight(), timesR * right.getHeight());
        w += -cornerOffsetX1 * 2;
        h += cornerOffsetY1;
        h += -cornerOffsetY3;

        Pixmap pixmap =
         (fillWithBlack)
          ? new Pixmap(maxWidth, maxHeight, Format.RGBA8888)
          : new Pixmap(w, h, Format.RGBA8888);

        int offsetX = -cornerOffsetX1 / 2;
        int offsetY = cornerOffsetY1 / 2;
        int offsetY2 = cornerOffsetY3 / 2;

        if (background != null) {
            GdxImageMaster.drawTextureRegion(offsetX, offsetY, background,
             w - offsetX * 2,
             h - offsetY + offsetY2,
             pixmap);
        }
        GdxImageMaster.drawTexture(offsetX, offsetY, 0, 1, left, timesL - offset, pixmap);
        GdxImageMaster.drawTexture(-offsetX + w - right.getWidth(), offsetY, 0, 1, right, timesR - offset, pixmap);
        GdxImageMaster.drawTexture(offsetX, h - top.getHeight() + offsetY2, 1, 0, bottom, timesT - offset, pixmap);
        GdxImageMaster.drawTexture(offsetX, offsetY, 1, 0, top, timesB - offset, pixmap);


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


        if (fillWithBlack) {
            //            for (int x = 0; x < (maxWidth - w) / 2; x++)
            //                for (int y = 0; y < (maxHeight - h) / 2; y++)
            //                    pixmap.drawPixel(y, y, Color.BLACK.toIntBits());
            // TODO it seems we're always offset to 0-0!
            //            for (int x = maxWidth - w; x < (maxWidth) / 2; x++)
            //                for (int y = 0; y < (maxHeight - h) / 2; y++)
            //                    pixmap.drawPixel(y, y, Color.BLACK.toIntBits());
            for (int x = maxWidth - w; x < (maxWidth) / 2; x++)
                for (int y = maxHeight - h; y < (maxHeight) / 2; y++)
                    pixmap.drawPixel(y, y, Color.BLACK.toIntBits());
            for (int x = 0; x < (maxWidth - w) / 2; x++)
                for (int y = maxHeight - h; y < (maxHeight) / 2; y++)
                    pixmap.drawPixel(y, y, Color.BLACK.toIntBits());
        }
        if (write) {
            GdxImageMaster.writeImage(handle, pixmap);
            return TextureCache.getOrCreate(path);
        } else {

            return new Texture(pixmap);
        }
    }
/////////////////////////

    public static TiledDrawableX getDrawable(
            TextureRegion top,
            TextureRegion bottom,
            TextureRegion right,
            TextureRegion left,
            TextureRegion corner,
            Texture background,
            int maxWidth, int maxHeight,
            int cornerOffsetX1,
            int cornerOffsetY1,
            int cornerOffsetY3,
            boolean fillWithBlack) {
        return getDrawable(top, bottom, right, left,
                corner,
                corner,
                corner,
                corner,
                background, maxWidth, maxHeight, cornerOffsetX1, cornerOffsetY1, cornerOffsetY3, fillWithBlack);
    }
    public static TiledDrawableX getDrawable(
            TextureRegion top,
            TextureRegion bottom,
            TextureRegion right,
            TextureRegion left,
            TextureRegion corner1,
            TextureRegion corner2,
            TextureRegion corner3,
            TextureRegion corner4,

            Texture background,
            int maxWidth, int maxHeight,
            int cornerOffsetX1,
            int cornerOffsetY1,
            int cornerOffsetY3,
            boolean fillWithBlack) {

        if (bottom == null  ) {
            bottom = top;
        }
        if (left == null  )
            left = right;

        int timesL = maxHeight / left.getRegionHeight();
        int timesR = maxHeight / right.getRegionHeight();
        int timesT = maxWidth / top.getRegionWidth();
        int timesB = maxWidth / bottom.getRegionWidth();

        int w = Math.max(timesB * bottom.getRegionWidth(), timesT * top.getRegionWidth());
        int h = Math.max(timesL * left.getRegionHeight(), timesR * right.getRegionHeight());
        w += -cornerOffsetX1 * 2;
        h += cornerOffsetY1;
        h += -cornerOffsetY3;

        int offsetX = -cornerOffsetX1 / 2;
        int offsetY = cornerOffsetY1 / 2;

        return new TiledDrawableX(w, h, background, fillWithBlack,
                corner1,
                corner2,
                corner3,
                corner4,
                bottom, left, right, top,
                offsetX,offsetY
                );

    }
    public static TextureRegionDrawable getOrCreateNinePatchDrawable(NINE_PATCH ninePatch,
                                                                     BACKGROUND_NINE_PATCH background,
                                                                     int w, int h) {
        return new TextureRegionDrawable(
         new TextureRegion(getOrCreateNinePatch(ninePatch, background, w, h)));
    }

    public static Texture getOrCreateNinePatch(NINE_PATCH ninePatch,
                                               BACKGROUND_NINE_PATCH background,
                                               int w, int h,
                                               boolean fillWithBlack) {
        String path = getPath(ninePatch, background, w, h);
        Texture texture = TextureCache.getOrCreate(path, true);
        if (texture != null && texture != TextureCache.getMissingTexture())
            return texture;
        main.system.auxiliary.log.LogMaster.log(1,"NinePatch generated " + path );
        return generate(ninePatch, background, w, h,
         fillWithBlack);
    }

    public static TiledDrawableX getDrawable(NINE_PATCH ninePatch,
                                                BACKGROUND_NINE_PATCH background,
                                                int w, int h) {
        String partPath = StrPathBuilder.build(ninePatch.path,
                "parts") + PathUtils.getPathSeparator();

        return getDrawable(
                TextureCache.getOrCreateR(partPath + "top.png"),
                TextureCache.getOrCreateR(partPath + "bottom.png"),
                TextureCache.getOrCreateR(partPath + "right.png"),
                TextureCache.getOrCreateR(partPath + "left.png"),

                TextureCache.getOrCreateR(partPath + "corner1.png"),
                TextureCache.getOrCreateR(partPath + "corner2.png"),
                TextureCache.getOrCreateR(partPath + "corner3.png"),
                TextureCache.getOrCreateR(partPath + "corner4.png"),
                background == BACKGROUND_NINE_PATCH.TRANSPARENT
                        ? null
                        : TextureCache.getOrCreate(background.path),
w, h,
                ninePatch.cornerOffsetX1,
                ninePatch.cornerOffsetY1,
                ninePatch.cornerOffsetY3,
                false
        );
    }
    public static Texture getOrCreateNinePatch(NINE_PATCH ninePatch,
                                                BACKGROUND_NINE_PATCH background,
                                                int w, int h) {
        return getOrCreateNinePatch(ninePatch, background, w, h, false);
    }

    public static boolean isUseDynamicDrawable() {
        return true;
    }

    @Override
    public void create() {
        generate(NINE_PATCH.HL, BACKGROUND_NINE_PATCH.TRANSPARENT, 256, 196, false);
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

    public enum BACKGROUND_NINE_PATCH {
        TRANSPARENT,
        BLACK,
        SEMI,
        PATTERN, SEMI_THICK;
        String path = StrPathBuilder.build(PathFinder.getComponentsPath(),
         "ninepatch", "background", name().toLowerCase() + ".png");

        public String getPath() {
            return path;
        }
    }

    public enum NINE_PATCH {
        SAURON(-16, 60, -16, -34),
        FRAME(),
        DEMIURGE,
        LIGHT,
        HL,
        ZARK,
        VIGNETTE {
            public boolean isPreventOverlapping() {
                return true;
            }
        },
        SAURON_ALT(-6, 20, 0, 0);
        String path = StrPathBuilder.build(PathFinder.getComponentsPath(),
         "ninepatch", name().toLowerCase().replace("_", "/"));

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

        public boolean isPreventOverlapping() {
            return false;
        }
    }

    public enum NINE_PATCH_PADDING {
        SAURON(40, 30, 20, 20), FRAME(60, 60, 55, 55);
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
}
