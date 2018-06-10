package eidolons.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryFactory;
import eidolons.libgdx.texture.TextureCache;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 2/9/2018.
 */
public class GdxImageMaster extends LwjglApplication {

    private static final String PATH = "gen\\round\\";

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

    public static void main(String[] args) {
        new GdxImageMaster();
    }

    public static Texture flip(String path, boolean x, boolean y, boolean write) {
        Texture texture = TextureCache.getOrCreate(path);
        String suffix = "";
        if (x)
            suffix += " flip x";
        if (y)
            suffix += " flip y";
        path = StringMaster.cropFormat(path) + " " + suffix + StringMaster.getFormat(path);
        FileHandle handle = new FileHandle(
         PathFinder.getImagePath() +
          path);
        if (handle.exists())
            return TextureCache.getOrCreate(path);

        texture.getTextureData().prepare();
        Pixmap pixmap = getFlippedPixmap(texture.getTextureData().consumePixmap(), x, y);

        if (write) {
            writeImage(handle, pixmap);
        }
        texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public static Pixmap getPixmap(Texture texture) {
        if (!texture.getTextureData().isPrepared())
            texture.getTextureData().prepare();
        return texture.getTextureData().consumePixmap();
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
        Texture texture = TextureCache.getOrCreate(path);
        if (texture.getWidth() == size) {
            if (texture.getHeight() == size) {
                return texture;
            }
        }
        if (texture.equals(TextureCache.getEmptyTexture())) {
            return null;
        }
        path = getSizedImagePath(path, size);

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
            writeImage(handle, pixmap2);
        }
        texture = new Texture(pixmap2);
        pixmap.dispose();
        pixmap2.dispose();
        return texture;
    }

    public static String getSizedImagePath(String path, int size) {
        return StringMaster.cropFormat(path) + " " + size + StringMaster.getFormat(path);
    }

    public static void writeImage(FileHandle handle, Pixmap pixmap) {
        PixmapIO.writePNG(handle, pixmap);
    }

    public static void writeImage(FileHandle handle, Texture texture) {
        PixmapIO.writePNG(handle, getPixmap(texture));
    }

    public static TextureRegion round(String path, boolean write) {
        if (!GdxMaster.isLwjglThread())
            return null;
        TextureRegion textureRegion = TextureCache.getOrCreateR(path);
        if (textureRegion.getTexture() == TextureCache.getEmptyTexture())
            return textureRegion;

        path = getRoundedPath(path);
        TextureRegion roundedRegion = TextureCache.getOrCreateR(path);
        if (roundedRegion.getTexture() != TextureCache.getEmptyTexture())
            return roundedRegion;

        Pixmap rounded = roundTexture(textureRegion);
        FileHandle handle = new FileHandle(
         PathFinder.getImagePath() + path);
        if (write) {
            PixmapIO.writePNG(handle, rounded);
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

    public static String getRoundedPath(String path) {
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

    public static String cropImageImage(String s) {
        return s.replace('/', '\\').toLowerCase().replace(PathFinder.getImagePath().toLowerCase(), "");
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
         action.getName().replace(DC_ActionManager.OFFHAND, "") + ".png");
        return path;
    }

    private static String getStandardAttackIcon(ObjType action, ObjType weapon) {
        String baseType = weapon.getProperty(G_PROPS.BASE_TYPE);
        String weaponGroup = weapon.getProperty(G_PROPS.WEAPON_GROUP);
        String path = getStandardAttackIcon(baseType, weaponGroup, action);
        if (!ImageManager.isImage(path)) {
            path = findClosestIcon(action, weapon);
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

}