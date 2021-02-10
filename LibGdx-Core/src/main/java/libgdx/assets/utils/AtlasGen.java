package libgdx.assets.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.assets.AssetEnums;
import eidolons.libgdx.bf.grid.cell.QueueView;
import eidolons.libgdx.gui.panels.dc.topleft.atb.AtbPanel;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.TexturePackerLaunch;
import eidolons.system.utils.GdxUtil;
import libgdx.GdxImageMaster;
import libgdx.GdxMaster;
import libgdx.texture.TextureCache;
import libgdx.texture.TexturePackerLaunch;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.Bools;
import main.system.threading.WaitMaster;

import java.io.File;

import static eidolons.libgdx.GdxImageMaster.*;

public class AtlasGen extends GdxUtil {

    public static final String[] ui_base = {
            "ui",
            "ui/components/generic/*",
            "ui/components/ninepatch/*",
            "ui/main/hiero/*",
            // "main/art/",
            "main/previews", //heroes and locations/scenarios!
    };
    public static final String[] ui_dc = {
            "gen/entity/buffs/*",
            "gen/entity/items/*",
            "gen/entity/spells/*",
            "main/item/*",
            "main/actions/*",
            "ui/components/dc/*",
            "ui/components/hq/*",
            "ui/components/tiny/*",
            "ui/components/small/*",
            "ui/content/value icons/*",
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
    public static final String[] sprites_oneframe = {
            "gen/one_frame/*"
    };
    public static final String[] textures = {
            "main/textures/*"
    };
    public static final String[] unitview = {
            "gen/entity/bf obj/*",
            "main/bf/*",
            "main/units/*",
            "main/heroes/*",
            "ui/cells/outlines/*",
            "ui/cells/set/*",
            "ui/cells/advanced/platform/*",
            "ui/content/dc_icons/*",
            "ui/content/emblems/*",
            "ui/content/modes/*",
    };
    public static final AssetEnums.ATLAS[] cleanUpAtlases = {
            // Atlases.ATLAS.UI_BASE,
            // AssetEnums.ATLAS.UI_DC,
            // Atlases.ATLAS.UNIT_VIEW,
            // Atlases.ATLAS.TEXTURES,
            // Atlases.ATLAS.SPRITES_GRID,
            // Atlases.ATLAS.SPRITES_UI,
    };

    public static boolean OVERWRITE = false;
    private static final boolean UPDATE_ATLASES = false;

    public static final String[] roundedFolders = {
            "main/units/",
            "main/heroes/",
            PathFinder.getOutlinesPath(),
    };
    public static final String[] sizeFolders32 = {
    };
    public static final String[] sizeFolders50 = {
            "gen/entity/spells/",
            "gen/entity/items/",
            "main/item/potions/",
            "main/item/usable/",
    };
    public static final String[] sizeFolders96 = {
            "main/item/weapon/sprites/",
            // "main/units/",
            // "main/heroes/",
    };
    private final String[] args;

    public static final String root = PathFinder.getAtlasImgPath();
    public static final String out = PathFinder.getAtlasGenPath();

    public AtlasGen(String[] args) {
        super();
        this.args = args;
    }

    public static void main(String... args) {
        new AtlasGen(args).start();
    }

    @Override
    protected void execute() {
        PathFinder.init();
        boolean result=true;
        try {
            boolean CLEANUP = true;
            if (CLEANUP = args.length > 0)
                cleanUp();
            boolean UPDATE_GEN_IMAGES = false;
            if (UPDATE_GEN_IMAGES = args.length > 1)
                update();
            AtlasGen.moveFiles();
            generateRounded();
            generateSized();
            if (UPDATE_GEN_IMAGES = args.length > 2)
                TexturePackerLaunch.generateAtlases(args.length > 2);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            result = false;
        } finally{
            WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.GDX_READY, result);
        }
    }

    private static void generateSized() {
        for (String folder : sizeFolders32) {
            sizeFiles(AssetEnums.ATLAS.UI_DC, folder, 32, null );
        }
        for (String folder : sizeFolders50) {
            sizeFiles(AssetEnums.ATLAS.UI_DC, folder, 50,  " 64");
        }
        for (String folder : sizeFolders96) {
            sizeFiles(AssetEnums.ATLAS.UI_DC, folder, 96, null );
        }
    }

    private static void sizeFiles(AssetEnums.ATLAS atlas, String folder, int size, String cropSuffix) {
        for (File file : FileManager.getFilesFromDirectory(
                root + folder, false, true)) {
            String path = file.getPath();
            if (GdxImageMaster.isGeneratedFile(path)) {
                continue;
            }
            if (cropSuffix!=null ){
                FileManager.rename(path, path =StringMaster.cropSuffix(path, cropSuffix));
            }
            FileHandle handle = new FileHandle(
                    atlas.prefix + "/" + GdxImageMaster.getSizedImagePath(PathUtils.cropPath(path,
                            root), size,cropSuffix));
            if (!OVERWRITE && handle.exists()) continue;
            String orig = root +
                    GdxImageMaster.getSizedImagePath(path, size);
            if (FileManager.isFile(orig)) {
                if (Bools.isTrue(FileManager.copy(orig, handle.toString())))
                    LogMaster.log(1, size + " sized copied   " + handle);
            } else if (GdxMaster.isGdxThread()) {
                Texture tex = TextureCache.getOrCreate(path);
                Texture round = GdxImageMaster.createSized(path, tex, size, false);
                GdxImageMaster.writeImage(handle, round);
                LogMaster.log(1, size + " sized generated   " + handle);
            }
        }
    }

    private static void generateRounded() {
        for (String roundedFolder : roundedFolders) {
            for (File file : FileManager.getFilesFromDirectory(
                    root +
                            roundedFolder, false, true)) {
                if (GdxImageMaster.isGeneratedFile(file.getName())) {
                    continue;
                }
                FileHandle handle = new FileHandle(
                        AssetEnums.ATLAS.UNIT_VIEW.prefix + "/" +
                                QueueView.getProperViewPath(PathUtils.cropPath(file.getPath(),
                                        root) ));
                String orig = root + QueueView.getProperViewPath(file.getPath());
                if (!OVERWRITE && handle.exists()) continue;
                if (FileManager.isFile(
                        orig)) {
                    if (Bools.isTrue(FileManager.copy(orig, handle.toString()))) {
                        LogMaster.log(1, " rounded copied   " + handle);
                    }
                } else if (GdxMaster.isGdxThread()) {
                    TextureRegion region = new TextureRegion(GdxImageMaster.size(file.getPath(),  AtbPanel.imageSize, false));
                    TextureRegion round = GdxImageMaster.createRounded(false, region, handle.toString(), "");
                    GdxImageMaster.writeImage(handle, round);
                    LogMaster.log(1, " rounded generated   " + handle);
                }
            }
        }
    }

    /*exceptions - sized and rounded versions?         */
    private static void checkDeleteFiles() {

        for (File file : FileManager.getFilesFromDirectory(
                out, false, true)) {
            if (GdxImageMaster.isRoundedPath(file.getName())) {
                continue;
            }
            if (GdxImageMaster.isSizedPath(file.getName())) {
                continue;
            }
            String path =  PathUtils.cropPath(file.getPath(), out);
            if (PathUtils.splitPath(path).size()<2)
                continue;
            path =root +PathUtils.cropFirstPathSegment(path);
            if (!FileManager.isFile(path)) {
                FileManager.delete(file);
                log(true, "Removed " + file);
            }
        }

    }

    private static void update() {
        // copy back from res folder!
    }

    private static void cleanUp() {
        for (AssetEnums.ATLAS atlas : cleanUpAtlases) {
            FileManager.delete(out + atlas);
        }
        checkDeleteFiles();
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
            case SPRITES_ONEFRAME:
                return sprites_oneframe;
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
        log(true, "Copying atlas images from: " + atlasImgFolder);

        for (File file : FileManager.getFilesFromDirectory(atlasImgFolder, false, true)) {
            String path = GdxImageMaster.cropImagePath(file.getPath());
            String formatted = path.replaceFirst(pref, "");
            AssetEnums.ATLAS atlas = getAtlasForPath(formatted);
            if (atlas == null) {
                log(true, "NO ATLAS FOR " + path);
                continue;
            }
            String prefix = atlas.prefix;
            String target = prefix + "/" + formatted;

            boolean aNew = false;
            if (FileManager.getFile(target).exists()) {
                if (!overwrite) {
                    log(false, "Already exists " + target);
                    continue;
                }
                log(false, "Overwritten " + file + " to " + target);
            } else {
                aNew = true;
            }
            Boolean result = FileManager.copy(file.getPath(), target);
            if (Bools.isTrue(result) && aNew) {
                log(true, "Copied NEW " + file + " to " + target);
            }
        }
    }

    private static void log(boolean report, String s) {
        if (report)
            if (SteamPacker.report != null) {
                SteamPacker.report.append(s);
            }
        LogMaster.log(1, s);
    }

    @Override
    protected boolean isExitOnDone() {
        return false;
    }
}
