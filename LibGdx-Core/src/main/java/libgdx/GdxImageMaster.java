package libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.consts.libgdx.GdxStringUtils;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.assets.AssetEnums;
import libgdx.gui.dungeon.panels.dc.topleft.atb.AtbPanel;
import libgdx.assets.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/9/2018.
 */
public class GdxImageMaster extends LwjglApplication {

    private static final String PATH = "gen/round/";
    private static final Map<Texture, Pixmap> pixmaps = new HashMap<>();
    private static final Map<String, Texture> sizedViewCache = new HashMap<>();


    public GdxImageMaster() {
        super(new ApplicationAdapter() {
            @Override
            public void create() {
                super.create();
                roundTextures(PATH);
            }
        });
    }

    public static Texture getPanelBackground(TiledNinePatchGenerator.NINE_PATCH ninePatch,
                                             TiledNinePatchGenerator.BACKGROUND_NINE_PATCH background,
                                             int w, int h) {

        return TiledNinePatchGenerator.getOrCreateNinePatch(ninePatch, background, w, h);
    }

    public static void genAtlas(Collection<TextureRegion> regions) {

    }

    public static void main(String[] args) {
        new GdxImageMaster();
    }

    public static Texture flip(String path, boolean x, boolean y, boolean write) {
        return flip(path, x, y, write, null);
    }

    public static Texture flip(String path, boolean x, boolean y, boolean write,
                               String newPath) {
        Texture texture = TextureCache.getOrCreate(path);
        if (newPath == null) {
            newPath = getFlippedPath(path, x, y);
        }
        FileHandle handle = GDX.file(
                PathFinder.getImagePath() +
                        newPath);
        if (handle.exists())
            return TextureCache.getOrCreate(newPath);

        Pixmap pixmap = getFlippedPixmap(getCustomPixmap(texture), x, y);

        if (write) {
            writeImage(handle, pixmap);
        }
        texture = new Texture(pixmap);
        return texture;
    }

    public static String getFlippedPath(String path, boolean x, boolean y) {

        String suffix = "";
        if (x)
            suffix += " flip x";
        if (y)
            suffix += " flip y";
        return StringMaster.cropFormat(path) + " " + suffix + StringMaster.getFormat(path);
    }

    public static Pixmap getCustomPixmap(Texture texture) {
        Pixmap pixmap = pixmaps.get(texture);
        if (pixmap != null) {
            return pixmap;
        }
        pixmap = new Pixmap(texture.getWidth(), texture.getHeight(),
                texture.getTextureData().getFormat());
        drawTextureRegion(0, 0, texture, texture.getWidth(), texture.getHeight(), pixmap);
        pixmaps.put(texture, pixmap);
        return pixmap;
    }

    public static Pixmap getPixmap(Texture texture) {
        if (!texture.getTextureData().isPrepared())
            texture.getTextureData().prepare();
        return texture.getTextureData().consumePixmap();
    }


    public static Pixmap getPixMapFromRegion(TextureRegion region) {
        Texture texture = region.getTexture();
        TextureData data = texture.getTextureData();
        if (!data.isPrepared()) {
            data.prepare();
        }
        Pixmap pixmap = data.consumePixmap();
        int width = region.getRegionWidth();
        int height = region.getRegionHeight();
        Pixmap px = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int colorInt = pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y);
                px.drawPixel(x, y, colorInt);
            }
        }
        return px;
    }

    public static Pixmap getFlippedPixmap(Pixmap src, boolean flipX, boolean flipY) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        Pixmap flipped = new Pixmap(width, height, src.getFormat());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int x1 = flipX ? width - x - 1 : x;
                int y1 = flipY ? height - y - 1 : y;
                flipped.drawPixel(x, y, src.getPixel(x1, y1));
            }
        }
        return flipped;
    }

    public static Texture size(String path, int size, boolean write) {
        return size(path, size, size, write);
    }

    //TODO gdx revamp - INTO REGION FOR ATLASES!!!
    public static Texture size(String path, int width, int height, boolean write) {
        int size = (width + height) / 2;
        Texture texture;
        if (height == AtbPanel.imageSize && width == AtbPanel.imageSize) {
            texture = sizedViewCache.get(path);
            if (texture != null) {
                return texture;
            }
        }
        texture = TextureCache.getOrCreate(path);
        if (texture.getWidth() == size) {
            if (texture.getHeight() == size) {
                return texture;
            }
        }
        if (texture.equals(TextureCache.getMissingTexture())) {
            return null;
        }
        return createSized(path, texture, size, write);
    }

    public static Texture createSized(String path, Texture texture, int size, boolean write) {
        String newPath = GdxStringUtils.getSizedImagePath(path, size);

        FileHandle handle = GDX.file(
                PathFinder.getImagePath() +
                        newPath);
        if (handle.exists())
            return TextureCache.getOrCreate(newPath);
        if (!texture.getTextureData().isPrepared())
            texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        Pixmap pixmap2 = new Pixmap(size, size, pixmap.getFormat());
        pixmap2.drawPixmap(pixmap,
                0, 0, pixmap.getWidth(), pixmap.getHeight(),
                0, 0, pixmap2.getWidth(), pixmap2.getHeight()
        );
        if (size == AtbPanel.imageSize) {
            sizedViewCache.put(path, texture);
        }
        if (write) {
            writeImage(handle, pixmap2);
            texture = new Texture(pixmap2);
            if (!Flags.isUtility()) {
                pixmap.dispose();
                pixmap2.dispose();
            }
            return texture;
        } else {
            return TextureCache.getInstance().createAndCacheTexture(path, pixmap2);
        }
    }

    public static void writeImage(FileHandle handle, Pixmap pixmap) {
        PixmapIO.writePNG(handle, pixmap);
    }

    public static void writeImage(FileHandle handle, Texture texture) {
        Pixmap pixMap;
        PixmapIO.writePNG(handle, pixMap = getPixmap(texture));
        pixMap.dispose();
    }

    public static void writeImage(FileHandle handle, TextureRegion region) {
        Pixmap pixMap;
        PixmapIO.writePNG(handle, pixMap = getPixMapFromRegion(region));
        pixMap.dispose();
    }

    public static TextureRegion getSizeTemplate(String root) {
        return null;
    }

    public static TextureRegion round(String path, boolean write, String customPath) {
        path = GdxStringUtils.cropImagePath(path);
        if (!customPath.isEmpty()) {
            path = customPath;
        }

        String newPath = getRoundedPath(path);
        TextureRegion roundedRegion = TextureCache.getOrCreateR(newPath, false, AssetEnums.ATLAS.UNIT_VIEW);
        if (roundedRegion != null)
            if (roundedRegion.getTexture() != TextureCache.getMissingTexture())
                return roundedRegion;

        TextureRegion textureRegion = TextureCache.getOrCreateR(path);

        if (!Flags.isIDE() || textureRegion.getTexture() == TextureCache.getMissingTexture())
            return textureRegion;

        if (!GdxMaster.isLwjglThread())
            return null;
        //CREATING
        if (TextureCache.atlasesOn) {
            textureRegion = new TextureRegion(TextureCache.getOrCreate(path));
        }
        return createRounded(write, textureRegion, newPath, path);
    }

    public static TextureRegion createRounded(boolean write, TextureRegion textureRegion, String newPath, String path) {
        Pixmap rounded = roundTexture(textureRegion);
        FileHandle handle = GDX.file(
                PathFinder.getImagePath() + newPath);
        if (write) {
            try {
                PixmapIO.writePNG(handle, rounded);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        } else
            return TextureCache.getInstance().createAndCacheRegion(path, rounded);

        return TextureCache.getOrCreateR(newPath);
    }

    public static void roundTextures(String directory) {
        CoreEngine.systemInit();
        for (String filePath : FileManager.getFileNames(FileManager.
                getFilesFromDirectory(PathFinder.getImagePath() + directory, false))) {
            //            FileHandle handle=GDX.file(filePath);
            round(directory + filePath, true, "");

        }
    }

    public static Pixmap roundTexture(TextureRegion textureRegion) {
        Texture texture = textureRegion.getTexture();
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        return roundPixmap(texture.getTextureData().consumePixmap());
    }

    public static Pixmap invert(Pixmap pixmap) {
        int width = pixmap.getWidth();
        int height = pixmap.getHeight();
        Pixmap inverted = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                inverted.drawPixel(x, y, Color.rgba8888(Color.WHITE) - pixmap.getPixel(x, y));
            }
        }
        Gdx.app.log("info", "pixmal rounded!");
        return inverted;
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

    public static String getRoundedPathRadial(String path) {
        return "gen/radial icons/" + PathUtils.getLastPathSegment(path);
    }

    public static String getRoundedPath(String path) {
        path = GdxStringUtils.cropImagePath(path);
        return StringMaster.cropFormat(path) + " rounded.png";
    }

    public static void drawTexture(int x, int y, int dX, int dY,
                                   Texture texture, int times, Pixmap pixmap) {

        texture.getTextureData().prepare();
        Pixmap pixmap2 = texture.getTextureData().consumePixmap();
        for (int i = 0; i < times; i++) {
            pixmap.drawPixmap(pixmap2, x, y);
            x += texture.getWidth() * dX;
            y += texture.getHeight() * dY;
        }
    }

    public static void drawTextureRegion(int x, int y, Texture texture,
                                         int width, int height, Pixmap pixmap) {

        drawTextureRegion(x, y, texture, width, height, pixmap, false);
    }


    public static void drawTextureRegion(int x, int y, Texture texture,
                                         int width, int height, Pixmap pixmap, boolean sourceOver) {

        if (!texture.getTextureData().isPrepared())
            texture.getTextureData().prepare();
        Pixmap pixmap2 = texture.getTextureData().consumePixmap();
        if (sourceOver)
            pixmap.setBlending(Blending.SourceOver);
        //        else pixmap.setBlending(Blending.None);

        pixmap.drawPixmap(pixmap2, x, y, 0, 0, width, height);
    }


    /*
    could be a batch job for util to init all existing sprite-atlases and create these singles
     */
    public static void genSingleFrameSprite(SpriteAnimation anim, String origPath) {
        /*
        image or atlas?
         */
        TextureRegion frame = anim.getKeyFrames()[0];
        // TexturePackerLaunch.pack();
        // writeImage(handle, frame);
    }

}
