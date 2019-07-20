package main.data.filesys;

import main.system.PathUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.TimeMaster;
import main.system.launch.CoreEngine;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PathFinder {

    public static final String HOME = System.getProperty("user.home");
    public static final String OPTIONS_PATH = HOME + "/Eidolons/";
    private static final String RES_FOLDER_NAME = "resources";
    public static String MICRO_MODULE_NAME = "duel-club";
    private static final String ABILITY_TEMPLATES_PATH = MICRO_MODULE_NAME + PathUtils.getPathSeparator() +
     "templates";
    private static String MACRO_MODULE_NAME = "macro";
    private static String XML_PATH;
    private static String ROOT_PATH;
    private static String IMG_PATH;
    private static String SND_PATH;
    private static String FONT_PATH;
    private static String TYPES_PATH;
    private static String MACRO_TYPES_PATH;
    private static String RES_PATH;
    private static Lock initLock = new ReentrantLock();
    private static volatile boolean isInitialized = false;
    private static String jarName;
    private static String spritesPath;

    private static void _init() {
        ClassLoader classLoader = PathFinder.class.getClassLoader();
        if (classLoader.getResource("") != null) {
            File temp = new File(classLoader.getResource("").getFile());
            ROOT_PATH = new File(temp.getParentFile().toURI()) + File.separator;
            XML_PATH = new File(temp.getParentFile() + File.separator + "xml") + File.separator;
            System.out.println("Root path: " + ROOT_PATH);
        } else {
            //FOR JARS
            CoreEngine.setJar(true);


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
                CoreEngine.setExe(true);
            }

            System.out.println("jarName: " + jarName);
            String path =
             new File(uri.toString().replace(jarName + PathUtils.getPathSeparator(), "")).getAbsolutePath();
            path = path.split("file:")[0];
            System.out.println("Root path for Jar: " + path);

            ROOT_PATH = path + File.separator;
            XML_PATH = path + File.separator + "xml" + File.separator;

        }

        RES_PATH = RES_FOLDER_NAME + File.separator;
        IMG_PATH = ROOT_PATH + RES_PATH + "img" + PathUtils.getPathSeparator();

        SND_PATH = ROOT_PATH + RES_PATH + "sound" + PathUtils.getPathSeparator();

        FONT_PATH = ROOT_PATH + RES_PATH + "fonts" + PathUtils.getPathSeparator();

        MACRO_TYPES_PATH = XML_PATH + MACRO_MODULE_NAME + PathUtils.getPathSeparator() + "types" + PathUtils.getPathSeparator();

        TYPES_PATH = XML_PATH + MICRO_MODULE_NAME + PathUtils.getPathSeparator() + "types" + PathUtils.getPathSeparator();

    }

    private static void init() {
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
        return XML_PATH + PathUtils.getPathSeparator() + "dungeons" + PathUtils.getPathSeparator();
    }

    public static String getDungeonMissionFolder() {
        init();
        return XML_PATH + PathUtils.getPathSeparator() + "dungeons/missions" + PathUtils.getPathSeparator();
    }

    public static String getSkirmishBattlefieldFolder() {
        init();
        return getDungeonLevelFolder() + PathUtils.getPathSeparator() + "skirmish/battlefields" + PathUtils.getPathSeparator();
    }

    public static String getDungeonLevelFolder() {
        init();
        return XML_PATH + "dungeons/levels/"  ;
    }

    public static String getTextPath() {
        init();
        return RES_PATH + PathUtils.getPathSeparator() + "text" + PathUtils.getPathSeparator();
    }
    public static String getTextPathLocale() {
        init();
        return getTextPath() + "english/";
    }

    public static String getSavesPath() {
        return getXML_PATH() + PathUtils.getPathSeparator() + "saves" + PathUtils.getPathSeparator();
    }

    public static String getScenariosPath() {
        init();
        return RES_PATH + PathUtils.getPathSeparator() + "text/scenario" + PathUtils.getPathSeparator();
    }

    public static String getLogPath() {
        init();
        return "logs" + PathUtils.getPathSeparator()+CoreEngine.VERSION+"/"
                + TimeMaster.getDateString()+ PathUtils.getPathSeparator();
    }

    public static String getUnitGroupPath() {
        init();
        return XML_PATH + PathUtils.getPathSeparator() + "groups" + PathUtils.getPathSeparator();
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
        return SND_PATH+"soundsets/";
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

    public static String getRootPath() {
        init();
        return ROOT_PATH;
    }

    public static String getResPath() {
        init();
        return ROOT_PATH + PathUtils.getPathSeparator() + RES_FOLDER_NAME + PathUtils.getPathSeparator();
    }

    public static String getMACRO_TYPES_PATH() {
        init();
        return MACRO_TYPES_PATH;
    }

    public static String getPrefsPath() {
        init();
        return ROOT_PATH + getTextPath() + "prefs" + PathUtils.getPathSeparator();
    }

    public static String getWorkspacePath() {
        init();
        return XML_PATH + PathUtils.getPathSeparator() + "workspaces" + PathUtils.getPathSeparator();
    }

    public static String getXML_PATH() {
        init();
        return XML_PATH;
    }

    public static String getMapBlockFolderPath() {
        init();
        return getLevelEditorPath() + "blocks" + PathUtils.getPathSeparator();
    }

    public static String getLevelEditorPath() {
        init();
        return XML_PATH + "Level Editor" + PathUtils.getPathSeparator();
    }

    public static String getSpellUpgradeGlyphsFolder() {
        init();
        return getImagePath() + "ui/glyphs" + PathUtils.getPathSeparator();
    }

    public static boolean isFullPath(String path) {
        init();
        return path.contains(ROOT_PATH);
    }

    public static String getVfxPath() {
        init();
        return getImagePath() + "vfx" + PathUtils.getPathSeparator();
    }
    public static String getVfxAtlasPath() {
        init();
        return getImagePath() + "vfx/atlas/"  ;
    }

    public static String getSpellVfxPath() {
        init();
        return getImagePath() + "vfx/spell" + PathUtils.getPathSeparator();
    }

    public static String getParticlePresetPath() {
        return getVfxPath();
    }

    public static String getSpritesPathFull() {
        init();
        return getImagePath() + PathUtils.getPathSeparator() +
         "mini/sprites" + PathUtils.getPathSeparator();
    }

    public static String getSpritesPath() {
        init();
        return
         "mini/sprites" + PathUtils.getPathSeparator();
    }

    public static String getUiSpritePath() {
        return getSpritesPathNew() + "ui/";
    }
    public static String getSpritesPathNew() {
        init();
        return "sprites" + PathUtils.getPathSeparator();
    }


    public static String getMusicPath() {
        init();
        return getResPath() + "music" + PathUtils.getPathSeparator();
    }

    public static String getParticleImagePath() {
        init();
        return getParticlePresetPath() + "images" + PathUtils.getPathSeparator();
    }

    public static String removeSpecificPcPrefix(String imagePath) {
        init();
        return imagePath.replace(getRootPath(), "");
    }

    public static String getJarPath() {
        return getRootPath() + jarName;
    }

    public static String getEmblemsPath() {
        return StrPathBuilder.build("ui","content", "emblems") + PathUtils.getPathSeparator();
    }

    public static String getEmblemAutoFindPath() {
        return StrPathBuilder.build("ui", "content", "emblems", "auto") + PathUtils.getPathSeparator();
    }

    public static String getWeaponAnimPath() {
        return StrPathBuilder.build(getSpritesPathMain(), "weapons3d") + PathUtils.getPathSeparator();
    }

    public static String getPotionsAnimPath() {
        return StrPathBuilder.build(getSpritesPathMain(), "potions") + PathUtils.getPathSeparator();
    }

    private static String getSpritesPathMain() {
        init();
        return
         "sprites" + PathUtils.getPathSeparator();
    }

    public static String getVideoPath() {
        return StrPathBuilder.build(getResPath(),
         "video") + PathUtils.getPathSeparator();
    }

    public static String getMacroImgPath() {
        return StrPathBuilder.build(getImagePath(),
         "global") + PathUtils.getPathSeparator();
    }

    public static String getMacroXmlPath() {
        return StrPathBuilder.build(getXML_PATH(),
         "macro") + PathUtils.getPathSeparator();
    }

    public static String getRouteImagePath() {
        return
         StrPathBuilder.build(getMacroImgPath(), "routes") + PathUtils.getPathSeparator();
    }

    public static String getMacroPath() {
        return "global";
    }

    public static String getMapLayersPath() {
        return StrPathBuilder.build(getMacroPath(), "map", "layers") + PathUtils.getPathSeparator();
    }

    public static String getComponentsPath() {
        return StrPathBuilder.build(getUiPath(),
         "components") + PathUtils.getPathSeparator();

    }

    public static String getUiPath() {
        return "ui" + PathUtils.getPathSeparator();

    }


    public static String getOutlinesPath() {
        return StrPathBuilder.build(
         "ui","cells",  "outlines") + PathUtils.getPathSeparator();
    }
    public static String getShadeCellsPath() {
        return StrPathBuilder.build(
         "ui","cells",  "outlines",  "shadows") + PathUtils.getPathSeparator();
    }

    public static String getMacroUiPath() {
        return StrPathBuilder.build(
         "ui", "macro") + PathUtils.getPathSeparator();

    }

    public static String getUiContentPath() {
        return StrPathBuilder.build(
         "ui", "content") + PathUtils.getPathSeparator();
    }

    public static String getSkinPath() {
        return StrPathBuilder.build(
         getImagePath(),
         "ui",
         "components",
         "skin",
         "neutralizer-ui.json");
    }

    public static String getPerkImagePath() {
        return StrPathBuilder.build(PathFinder.getUiPath(),
         "value icons", "perks") + PathUtils.getPathSeparator();
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
        return StrPathBuilder.build(getEmblemsPath(), "sketches") + PathUtils.getPathSeparator();
    }

    public static String getPortraitPath() {
        return StrPathBuilder.build(getImagePath(), "main",
         "chars",
         "std") +
         PathUtils.getPathSeparator();

    }

    public static String getGeneratorRootPath() {
        return "generator/";
    }

    public static String getRoomTemplatesFolder() {
        return StrPathBuilder.build(PathFinder.getXML_PATH(), "Level Editor",
         "room templates") +
         PathUtils.getPathSeparator();
    }

    public static String getRandomLevelPath() {
        return StrPathBuilder.build(PathFinder.getDungeonLevelFolder(),
         "generated") + PathUtils.getPathSeparator();
    }

    public static String getMetaDataUnitPath() {
        return StrPathBuilder.build(PathFinder.getXML_PATH(),
         "meta data.txt");
    }

    public static String getHitSpritesPath() {
        if (spritesPath == null)
            spritesPath = StrPathBuilder.build(getSpritesPathNew(),
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

    public static String getCursorPath() {
        return StrPathBuilder.build(getImagePath(), getUiPath(),
         "cursor.png");
    }

    public static String getBordersPath() {
        return "ui/components/generic/borders/";
    }

    public static String getShadersPath() {
        return   "xml/shaders/";
    }

    public static String getCampaignSavePath() {
        return getSavesPath()+"campaign/";
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

}
