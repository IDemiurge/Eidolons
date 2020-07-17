package eidolons.libgdx.assets.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.assets.AssetEnums;
import eidolons.libgdx.texture.TexturePackerLaunch;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.secondary.Bools;

import java.io.File;

public class AtlasGen {

    public static final String[] textures = {
            "oneframe", //TODO
            "main/textures/*"
    };
    public static final String[] ui_dc = {
            "gen/entity/buffs/*",
            "gen/entity/items/*",
            "gen/entity/spells/*",
            "img/main/item/*",
            "ui/components/dc/*",
            "ui/content/*",
    };
    public static final String[] ui_sprites = {
            "sprites/ui",
    };
    public static final String[] sprites = {
            "sprites/bf/*",
            "sprites/cells/*",
            "sprites/hit/*",
            "sprites/fly objs/*",
    };
    public static final String[] unitview = {
            "gen/entity/bf obj/*",
            "img/main/bf/*",
            "main/units/*",
            "main/heroes/*",
            "ui/content",
            "ui/cells/*",
    };
    public static final String[] ui_base = {
            "ui",
            "ui/components/generic/*",
            "ui/components/ninepatch/*",
            "ui/main/hiero/*",
            // "main/art/",
            "main/previews", //heroes and locations/scenarios!
    };
    public static final AssetEnums.ATLAS[] cleanUpAtlases = {
            // Atlases.ATLAS.UI_BASE,
            // AssetEnums.ATLAS.UI_DC,
            // Atlases.ATLAS.UNIT_VIEW,
            // Atlases.ATLAS.TEXTURES,
            // Atlases.ATLAS.SPRITES_GRID,
            // Atlases.ATLAS.SPRITES_UI,
    };

    private static final boolean OVERWRITE = false;
    private static boolean CLEANUP = true;
    private static boolean UPDATE_GEN_IMAGES = false;
    private static final boolean UPDATE_ATLASES = false;

    public static final String[] roundedFolders = {
            // "gen/entity/bf obj/",
            // "img/main/bf/",
            "main/units/",
            "main/heroes/",
    };

    public static void main(String... args) {
        PathFinder.init();
        if (CLEANUP = args.length > 0)
            cleanUp();
        if (UPDATE_GEN_IMAGES = args.length > 1)
            update();
        AtlasGen.moveFiles();
        generateRounded();
        if (UPDATE_GEN_IMAGES = args.length > 2)
            TexturePackerLaunch.generateAtlases();

    }

    private static void generateRounded() {
        for (String roundedFolder : roundedFolders) {
            for (File file : FileManager.getFilesFromDirectory(
                    PathFinder.getImagePath() +
                            roundedFolder, false, true)) {
                FileHandle handle = new FileHandle(
                        AssetEnums.ATLAS.UNIT_VIEW.prefix + "/" +
                                GdxImageMaster.getRoundedPath(file.getPath()));
                if (FileManager.isFile(
                        PathFinder.getAtlasImgPath()+
                        GdxImageMaster.getRoundedPath(file.getPath()))) {
                    if (Bools.isTrue(FileManager.copy(PathFinder.getImagePath() +
                            GdxImageMaster.getRoundedPath(file.getPath()), handle.toString()))) {
                        main.system.auxiliary.log.LogMaster.log(1, " rounded generated   " + handle);
                    }
                } else if (GdxMaster.isGdxThread()) {
                    TextureRegion round = GdxImageMaster.round(file.getPath(), false);
                    GdxImageMaster.writeImage(handle, round);
                }
            }
        }
    }

    private static void update() {
        // copy back from res folder!
    }

    private static void cleanUp() {
        for (AssetEnums.ATLAS atlas : cleanUpAtlases) {
            FileManager.delete(PathFinder.getAtlasGenPath() + atlas);
        }
    }

    public static AssetEnums.ATLAS getAtlasForPath(String path) {
        path = GdxImageMaster.cropImagePath(path);
        String root = PathUtils.cropLastPathSegment(path);
        for (AssetEnums.ATLAS atlas : AssetEnums.ATLAS.values()) {
            if (check(atlas, root))
                return atlas;
        }
        return null;
    }

    private static boolean check(AssetEnums.ATLAS atlas, String root) {
        String[] paths = getPaths(atlas);
        for (String s : paths) {
            if (s.endsWith("*")) {
                s = s.substring(0, s.length() - 1);
                if (root.startsWith(s))
                    return true;
            }
            if (root.equalsIgnoreCase(s)) {
                return true;
            }
            if (root.substring(0, root.length() - 1).equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    private static String[] getPaths(AssetEnums.ATLAS atlas) {
        switch (atlas) {
            case UI_BASE:
                return ui_base;
            // case UI_MACRO:
            //     return ui_base;
            case UI_DC:
                return ui_dc;
            case TEXTURES:
                return textures;
            case UNIT_VIEW:
                return unitview;
            case SPRITES_GRID:
                return sprites;
            case SPRITES_UI:
                return ui_sprites;
        }
        return new String[0];
    }

    public static void moveFiles() {
        moveFiles(PathFinder.getImagePath(), OVERWRITE);
    }

    public static void moveFiles(String rootPath, boolean overwrite) {
        String pref = "atlas img";
        String atlasImgFolder = rootPath + pref;

        for (File file : FileManager.getFilesFromDirectory(atlasImgFolder, false, true)) {
            String path = GdxImageMaster.cropImagePath(file.getPath());
            String formatted = path.replaceFirst(pref, "");
            AssetEnums.ATLAS atlas = getAtlasForPath(formatted);
            if (atlas == null) {
                System.out.println("NO ATLAS FOR " + path);
                continue;
            }
            String prefix = atlas.prefix;
            String target = prefix + "/" + formatted;
            if (!overwrite)
                if (FileManager.getFile(target).exists()) {
                    System.out.println("Already exists " + target);
                    continue;
                }

            FileManager.copy(file.getPath(), target);
            System.out.println("Copied " + file + " to " + target);
        }
    }
}
