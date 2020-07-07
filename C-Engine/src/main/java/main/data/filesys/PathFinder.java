package main.data.filesys;

import main.system.PathUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.TimeMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PathFinder {
//TODO ALL METHODS TO CONSTS!

    public static final String HOME = System.getProperty("user.home");
    public static final String OPTIONS_PATH = HOME + "/Eidolons/";
    private static final String RES_FOLDER_NAME = "resources";
    public static String MICRO_MODULE_NAME = "duel-club";
    private static final String ABILITY_TEMPLATES_PATH = MICRO_MODULE_NAME + "/" +
            "templates";
    public static final String SKIN_NAME = "neutralizer-ui" ;
    private static String XML_PATH;
    private static String ROOT_PATH;
    private static String IMG_PATH;
    private static String SND_PATH;
    private static String FONT_PATH;
    private static String TYPES_PATH;
    private static String MACRO_TYPES_PATH;
    private static String BACKUP_TYPES_PATH;
    private static String RES_PATH;
    private static final Lock initLock = new ReentrantLock();
    private static volatile boolean isInitialized = false;
    private static String jarName;
    private static String spritesPath;

    private static void _init() {
        ClassLoader classLoader = PathFinder.class.getClassLoader();
        if (classLoader.getResource("") != null) {
            File temp = new File(classLoader.getResource("").getFile());
            ROOT_PATH = PathUtils.fixSlashes(new File(temp.getParentFile().toURI()).getPath());
            XML_PATH = ROOT_PATH + "xml/";
            System.out.println("Root path: " + ROOT_PATH);
        } else {
            //FOR JARS
            Flags.setJar(true);

            URI uri = null;
            try {
                uri =
                        new CoreEngine().getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            } catch (URISyntaxException e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            if (jarName == null)
                jarName = PathUtils.getLastPathSegment((uri.toString()));

            if (jarName.contains(".exe")) {
                Flags.setExe(true);
            }

            System.out.println("jarName: " + jarName);
            String path =
                    new File(uri.toString().replace(jarName + "/", "")).getAbsolutePath();
            path = path.split("file:")[0];
            System.out.println("Root path for Jar: " + path);

            ROOT_PATH = PathUtils.fixSlashes(path);
            XML_PATH = ROOT_PATH + "xml/";

        }

        RES_PATH = RES_FOLDER_NAME + "/";

        IMG_PATH = ROOT_PATH + RES_PATH + "img/";

        SND_PATH = ROOT_PATH + RES_PATH + "sound/";

        FONT_PATH = ROOT_PATH + RES_PATH + "fonts/";

        String MACRO_MODULE_NAME = "macro";
        MACRO_TYPES_PATH = XML_PATH + MACRO_MODULE_NAME + "/types/";

        TYPES_PATH = XML_PATH + MICRO_MODULE_NAME + "/types/";
       BACKUP_TYPES_PATH = XML_PATH + MICRO_MODULE_NAME + "/types/backup/";

    }

    public static void init() {
        if (!isInitialized) {
            try {
                initLock.lock();
                if (!isInitialized) {
                    _init();
                    isInitialized = true;
                }
            } finally {
                initLock.unlock();
            }
        }
    }

    public static String getDungeonFolder() {
        init();
        return XML_PATH + "/dungeons/";
    }

    public static String getDungeonMissionFolder() {
        init();
        return XML_PATH + "/dungeons/missions/";
    }

    public static String getSkirmishBattlefieldFolder() {
        init();
        return getDungeonLevelFolder() + "/skirmish/battlefields/";
    }

    public static String getDungeonLevelFolder() {
        init();
        return XML_PATH + "dungeons/levels/";
    }

    public static String getModuleTemplatesPath() {
        return XML_PATH + "dungeons/levels/templates/modules/";
    }

    public static String getFloorTemplatesPath() {
        return XML_PATH + "dungeons/levels/templates/floors/";
    }

    public static String getModulesPath() {
        return XML_PATH + "dungeons/levels/modules/";
    }

    public static String getTextPath() {
        init();
        return RES_PATH + "/text/";
    }

    public static String getTextPathLocale() {
        init();
        return getTextPath() + "english/";
    }

    public static String getSavesPath() {
        return getXML_PATH() + "/saves/";
    }

    public static String getScenariosPath() {
        init();
        return RES_PATH + "/text/scenario/";
    }

    public static String getLogPath() {
        init();
        return "logs/" + CoreEngine.VERSION + "/"
                + TimeMaster.getDateString() + "/";
    }

    public static String getUnitGroupPath() {
        init();
        return XML_PATH + "/groups/";
    }

    public static String getImagePath() {
        init();
        return IMG_PATH;
    }

    public static String getSoundPath() {
        init();
        return SND_PATH;
    }

    public static String getSoundsetsPath() {
        init();
        return SND_PATH + "soundsets/";
    }

    public static String getFontPath() {
        init();
        return FONT_PATH;
    }

    public static String getFontsHieroPath() {
        init();
        return FONT_PATH + "hiero/";
    }

    public static String getTemplatesPath() {
        init();
        return XML_PATH + ABILITY_TEMPLATES_PATH;
    }

    public static String getBgPicsPath() {
        init();
        return StrPathBuilder.build("main", "background");
    }

    public static String getTYPES_PATH() {
        init();
        return TYPES_PATH;
    }
    public static String getBACKUP_TYPES_PATH() {
        init();
        return BACKUP_TYPES_PATH;
    }

    public static String getRootPath() {
        init();
        return ROOT_PATH;
    }

    public static String getResPath() {
        init();
        return ROOT_PATH + RES_FOLDER_NAME + "/";
    }

    public static String getMACRO_TYPES_PATH() {
        init();
        return MACRO_TYPES_PATH;
    }

    public static String getPrefsPath() {
        init();
        return ROOT_PATH + getTextPath() + "prefs/";
    }

    public static String getWorkspacePath() {
        init();
        return XML_PATH + "/workspaces/";
    }

    public static String getEditorWorkspacePath() {
        init();
        return getLevelEditorPath() + "/workspaces/palettes/";
    }

    public static String getXML_PATH() {
        init();
        return XML_PATH;
    }

    public static String getMapBlockFolderPath() {
        init();
        return getLevelEditorPath() + "blocks/";
    }

    public static String getLevelEditorPath() {
        init();
        return XML_PATH + "Level Editor/";
    }

    public static String getSpellUpgradeGlyphsFolder() {
        init();
        return getImagePath() + "ui/glyphs/";
    }

    public static boolean isFullPath(String path) {
        init();
        return path.contains(ROOT_PATH);
    }

    public static String getVfxPath() {
        init();
        return getImagePath() + "vfx/";
    }

    public static String getVfxAtlasPath() {
        init();
        return getImagePath() + "vfx/atlas/";
    }

    public static String getSpellVfxPath() {
        init();
        return getImagePath() + "vfx/spell/";
    }

    public static String getParticlePresetPath() {
        return getVfxPath();
    }

    public static String getSpritesPathFull() {
        init();
        return getImagePath() + "/" +
                "mini/sprites/";
    }

    public static String getTexturesPath() {
        return getImagePath() + "main/textures/";
    }

    public static String getSpellSpritesPath() {
        init();
        return
                "sprites/spells/";
    }

    public static String getUiSpritePath() {
        return getSpritesPath() + "ui/";
    }

    public static String getSpritesPath() {
        return "sprites/";
    }

    public static String getBossSpritesPath() {
        return "sprites/boss/";
    }

    public static String getMusicPath() {
        init();
        return getResPath() + "music/";
    }

    public static String getParticleImagePath() {
        init();
        return getParticlePresetPath() + "images/";
    }

    public static String removeSpecificPcPrefix(String imagePath) {
        init();
        return imagePath.replace(getRootPath(), "");
    }

    public static String getJarPath() {
        return getRootPath() + jarName;
    }

    public static String getEmblemsPath() {
        return StrPathBuilder.build("ui", "content", "emblems") + "/";
    }

    public static String getEmblemAutoFindPath() {
        return StrPathBuilder.build("ui", "content", "emblems", "auto") + "/";
    }

    public static String getWeaponAnimPath() {
        return StrPathBuilder.build(getSpritesPathMain(), "weapons3d") + "/";
    }

    public static String getPotionsAnimPath() {
        return StrPathBuilder.build(getSpritesPathMain(), "potions") + "/";
    }

    private static String getSpritesPathMain() {
        init();
        return
                "sprites/";
    }

    public static String getVideoPath() {
        return StrPathBuilder.build(getResPath(),
                "video") + "/";
    }

    public static String getMacroImgPath() {
        return StrPathBuilder.build(getImagePath(),
                "global") + "/";
    }

    public static String getMacroXmlPath() {
        return StrPathBuilder.build(getXML_PATH(),
                "macro") + "/";
    }

    public static String getRouteImagePath() {
        return
                StrPathBuilder.build(getMacroImgPath(), "routes") + "/";
    }

    public static String getMacroPath() {
        return "global";
    }

    public static String getMapLayersPath() {
        return StrPathBuilder.build(getMacroPath(), "map", "layers") + "/";
    }

    public static String getComponentsPath() {
        return StrPathBuilder.build(getUiPath(),
                "components") + "/";

    }

    public static String getUiPath() {
        return "ui/";

    }


    public static String getOutlinesPath() {
        return StrPathBuilder.build(
                "ui", "cells", "outlines") + "/";
    }

    public static String getShadeCellsPath() {
        return StrPathBuilder.build(
                "ui", "cells", "outlines", "shadows") + "/";
    }

    public static String getMacroUiPath() {
        return StrPathBuilder.build(
                "ui", "macro") + "/";

    }

    public static String getUiContentPath() {
        return StrPathBuilder.build(
                "ui", "content") + "/";
    }

    public static String getSkinPath() {
        return StrPathBuilder.build(
                getImagePath(),
                "ui",
                "components",
                "skin",
                SKIN_NAME +
                        ".json");
    }

    public static String getPerkImagePath() {
        return StrPathBuilder.build(PathFinder.getUiPath(),
                "value icons", "perks") + "/";
    }

    public static String getWeaponIconPath() {
        return StrPathBuilder.build("main",
                "item",
                "weapon",
                "icons");
    }

    public static String getJewelryIconPath() {
        return StrPathBuilder.build("main",
                "item",
                "Jewelry",
                "icons");
    }

    public static String getArmorIconPath() {
        return StrPathBuilder.build("main",
                "item",
                "armor",
                "icons");
    }

    public static String getSketchPath() {
        return StrPathBuilder.build(getEmblemsPath(), "sketches") + "/";
    }

    public static String getPortraitPath() {
        return StrPathBuilder.build(getImagePath(), "main",
                "chars",
                "std") +
                "/";

    }

    public static String getGeneratorRootPath() {
        return "generator/";
    }

    public static String getRoomTemplatesFolder() {
        return StrPathBuilder.build(PathFinder.getXML_PATH(), "Level Editor",
                "room templates") +
                "/";
    }

    public static String getRandomLevelPath() {
        return StrPathBuilder.build(PathFinder.getDungeonLevelFolder(),
                "generated") + "/";
    }

    public static String getMetaDataUnitPath() {
        return StrPathBuilder.build(PathFinder.getXML_PATH(),
                "meta data.txt");
    }

    public static String getHitSpritesPath() {
        if (spritesPath == null)
            spritesPath = StrPathBuilder.build(getSpritesPath(),
                    "hit");
        return spritesPath;
    }

    public static String getTargetingCursorPath() {
        return StrPathBuilder.build(getImagePath(), getUiPath(),
                "cursor target.png");
    }

    public static String getLoadingCursorPath() {
        return StrPathBuilder.build(getImagePath(), getUiPath(),
                "cursor loading.png");
    }

    public static String getAttackCursorPath() {
        return StrPathBuilder.build(getImagePath(), getUiPath(),
                "cursor_sword.png");
    }

    public static String getSneakAttackCursorPath() {
        return StrPathBuilder.build(getImagePath(), getUiPath(),
                "cursor_dagger.png");
    }

    public static String getEmptyCursorPath() {
        return StrPathBuilder.build(getImagePath(), getUiPath(),
                "cursor empty.png");
    }

    public static String getCursorPath() {
        return StrPathBuilder.build(getImagePath(), getUiPath(),
                "cursor.png");
    }

    public static String getFlyObjPath() {
        return "main/textures/fly objs/";
    }

    public static String getBordersPath() {
        return "ui/components/generic/borders/";
    }

    public static String getShadersPath() {
        return "xml/shaders/";
    }

    public static String getCampaignSavePath() {
        return getSavesPath() + "campaign/";
    }

    public static String getCellImagesPath() {
        return "ui/cells/advanced/";
    }

    public static String getArtFolder() {
        return "main/art/";
    }

    //    public static String getDialoguesPath(String locale) {
    //        return getDialoguesPath(texmaster)
    //    }
    public static String getDialoguesPath(String locale) {
        return getRootPath() + PathFinder.getTextPath()
                + locale + "/dialogue/";
    }

    public static String getHqPath() {
        return "ui/components/hq/";
    }

    public static String getSoundCuesPath() {
        return getSoundPath() + "moments/cues/";
    }

    public static String getAtlasImgPath() {
        return getImagePath()+"atlas img/";
    }
    public static String getAtlasGenPath() {
        return getImagePath()+"gen/atlas/";
    }

    public static String getPillarsPath() {
        return  "ui/cells/pillars/";
    }

    public static String getWallSetsFolder() {
        return "main/bf/walls/sets/";
    }
}
