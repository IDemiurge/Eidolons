package eidolons.libgdx.utils.textures;

import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.assets.Atlases;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.data.FileManager;

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
            "main/units",
            "main/heroes",
            "ui/content",
            "ui/cells/*",
    };
    public static final String[] ui_base = {
            "ui",
            "ui/components/generic/*",
            "ui/components/ninepatch/*",
            // "main/art/",
            "main/previews", //heroes and locations/scenarios!
    };
    public static final Atlases.ATLAS[] cleanUpAtlases = {
            // Atlases.ATLAS.UI_BASE,
            Atlases.ATLAS.UI_DC,
            // Atlases.ATLAS.UNIT_VIEW,
            // Atlases.ATLAS.TEXTURES,
            // Atlases.ATLAS.SPRITES_GRID,
            // Atlases.ATLAS.SPRITES_UI,
    };

    private static final boolean OVERWRITE = false;
    private static final boolean CLEANUP = true;

    public static void main(String[] args) {
        PathFinder.init();
        if (CLEANUP)
            cleanUp();
        AtlasGen.moveFiles(true);
        AtlasGen.moveFiles(false);
    }

    private static void cleanUp() {

        for (Atlases.ATLAS atlas : cleanUpAtlases) {
            FileManager.delete(PathFinder.getAtlasGenPath() +atlas);
        }
    }

    public static Atlases.ATLAS getAtlasForPath(String path) {
        //TODO not too important, but will boost performance a bit
        path = GdxImageMaster.cropImagePath(path);
        String root = PathUtils.cropLastPathSegment(path);

        for (Atlases.ATLAS atlas : Atlases.ATLAS.values()) {
            if (check(atlas, root))
                return atlas;
        }
        return null;
    }

    private static boolean check(Atlases.ATLAS atlas, String root) {
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

    private static String[] getPaths(Atlases.ATLAS atlas) {
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

    public static void moveFiles(boolean ui) {
        moveFiles(PathFinder.getImagePath(), ui, OVERWRITE);
    }

    public static void moveFiles(String rootPath, boolean ui, boolean overwrite) {

        String pref = (ui ? "atlas img/ui/" : "atlas img/grid/main/");
        String atlasImgFolder = rootPath + pref;

        for (File file : FileManager.getFilesFromDirectory(atlasImgFolder, false, true)) {
            String path = GdxImageMaster.cropImagePath(file.getPath());
            String formatted = path.replaceFirst(pref, "");
            Atlases.ATLAS atlas = getAtlasForPath(formatted);
            if (atlas == null) {
                System.out.println("NO ATLAS FOR " + path);
                continue;
            }
            String prefix = atlas.prefix;
            String target = prefix + "/" + formatted;
                if (!overwrite)
            if (FileManager.getFile(target).exists()){
            System.out.println("Already exists "   + target);
                continue;
            }

            FileManager.copy(file.getPath(), target);
            System.out.println("Copied " + file + " to " + target);
        }
    }
}
