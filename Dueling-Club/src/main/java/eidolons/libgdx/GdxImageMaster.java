package eidolons.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryFactory;
import eidolons.libgdx.gui.panels.dc.topleft.atb.AtbPanel;
import eidolons.libgdx.texture.TextureCache;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.system.PathUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;
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

    public static Texture getPanelBackground(NINE_PATCH ninePatch,
                                             BACKGROUND_NINE_PATCH background,
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

    public static Texture size(String path, int width, int height, boolean write) {
        int size = (width + height) / 2;
        Texture texture = null;
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
        String newPath = getSizedImagePath(path, size);

        FileHandle handle = GDX.file(
                PathFinder.getImagePath() +
                        newPath);
        if (handle.exists())
            return TextureCache.getOrCreate(newPath);
        if (!texture.getTextureData().isPrepared())
            texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        Pixmap pixmap2 = new Pixmap(width, height, pixmap.getFormat());
        pixmap2.drawPixmap(pixmap,
                0, 0, pixmap.getWidth(), pixmap.getHeight(),
                0, 0, pixmap2.getWidth(), pixmap2.getHeight()
        );
        if (height == AtbPanel.imageSize && width == AtbPanel.imageSize) {
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

    public static String getSizedImagePath(String path, int size) {
        path = FileManager.formatPath(path);
        return StringMaster.cropFormat(path) + " " + size + StringMaster.getFormat(path);
    }

    public static void writeImage(FileHandle handle, Pixmap pixmap) {
        PixmapIO.writePNG(handle, pixmap);
    }

    public static void writeImage(FileHandle handle, Texture texture) {
        PixmapIO.writePNG(handle, getPixmap(texture));
    }

    public static void writeImage(FileHandle handle, TextureRegion region) {
        PixmapIO.writePNG(handle, getPixMapFromRegion(region));
    }

    public static TextureRegion getSizeTemplate(String root) {
        return null;
    }

    public static TextureRegion round(String path, boolean write) {
        if (!GdxMaster.isLwjglThread())
            return null;
        TextureRegion textureRegion = TextureCache.getOrCreateR(path);
        if (textureRegion.getTexture() == TextureCache.getMissingTexture())
            return textureRegion;

        String newPath = getRoundedPath(path);
        TextureRegion roundedRegion = null;
        if (FileManager.isFile(PathFinder.getImagePath() + newPath))
            roundedRegion = TextureCache.getOrCreateR(cropImagePath(newPath));
        if (roundedRegion != null)
            if (roundedRegion.getTexture() != TextureCache.getMissingTexture())
                return roundedRegion;

        Pixmap rounded = roundTexture(textureRegion);
        FileHandle handle = GDX.file(
                PathFinder.getImagePath() + newPath);
        if (write) {
            PixmapIO.writePNG(handle, rounded);
        } else
            return TextureCache.getInstance().createAndCacheRegion(path, rounded);

        return TextureCache.getOrCreateR(newPath);
    }

    public static void roundTextures(String directory) {
        CoreEngine.systemInit();
        for (String filePath : FileManager.getFileNames(FileManager.
                getFilesFromDirectory(PathFinder.getImagePath() + directory, false))) {
            //            FileHandle handle=GDX.file(filePath);
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

    public static String getRoundedPathNew(String path) {
        return "gen/radial icons/" + PathUtils.getLastPathSegment(path);
    }

    public static String getRoundedPath(String path) {
        path = FileManager.formatPath(path);
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

    public static String appendImagePath(String s) {
        s = cropImagePath(s);
        return PathFinder.getImagePath().toLowerCase() + "/" + s;
    }

    public static String cropImagePath(String s) {
        s = FileManager.formatPath(s, true, true);
        return s.replace(PathFinder.getImagePath().toLowerCase(), "");
    }

    public static String getAttackActionPath(DC_ActiveObj obj) {
        return getAttackActionPath(obj, obj.getActiveWeapon());
    }

    public static String getAttackActionPath(DC_ActiveObj obj, DC_WeaponObj weapon) {
        return (!obj.isStandardAttack() || obj.isThrow()) ? InventoryFactory.getWeaponIconPath(weapon)
                : getStandardAttackIcon(obj);
        //            if (obj.isOffhand()){
        //                Texture texture = GdxImageMaster.flip(path, true, false, true);
        //                return new TextureRegion(texture);
        //            }
    }

    private static String getStandardAttackIcon(DC_ActiveObj obj) {
        DC_WeaponObj weapon = obj.getActiveWeapon();
        return getStandardAttackIcon(obj.getType(), weapon.getType());
    }

    private static String getStandardAttackIcon(String baseType, String weaponGroup,
                                                ObjType action) {
        String path = StrPathBuilder.build("main", "actions", "standard attack",
                weaponGroup,
                baseType,
                action.getName().replace(ActionEnums.OFFHAND, "").replace(" ", "_") + ".png");
        return path;
    }

    private static String getStandardAttackIcon(ObjType action, ObjType weapon) {
        String baseType = weapon.getProperty(G_PROPS.BASE_TYPE);
        String weaponGroup = weapon.getProperty(G_PROPS.WEAPON_GROUP);
        String path = getStandardAttackIcon(baseType, weaponGroup, action);


        if (!ImageManager.isImage(path)) {
            path = path.replace("_", "");
            if (!ImageManager.isImage(path))
                path = findClosestIcon(action, weapon).replace("_", "");
        }
        return path;
    }


    private static String findClosestIcon(ObjType action, ObjType weapon) {
        String path = "";
        String subgroup = weapon.getSubGroupingKey();
        String baseType = "";
        String weaponGroup = weapon.getProperty(G_PROPS.WEAPON_GROUP);
        for (ObjType sub : DataManager.getTypesSubGroup(DC_TYPE.WEAPONS, subgroup)) {
            baseType = sub.getName();
            path = getStandardAttackIcon(baseType, weaponGroup, action);
            if (ImageManager.isImage(path)) {
                return path;
            }
        }
        return weapon.getImagePath();
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
