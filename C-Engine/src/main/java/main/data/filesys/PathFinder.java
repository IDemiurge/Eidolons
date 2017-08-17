package main.data.filesys;

import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PathFinder {

    private static final String BG_PATH = "big\\";
    private static final String PRESENTATION = "presentation\\";
    public static String MICRO_MODULE_NAME = "duel-club";
    private static final String ABILITY_TEMPLATES_PATH = MICRO_MODULE_NAME + "//templates";
    private static String MACRO_MODULE_NAME = "macro";
    private static String XML_PATH;
    private static String ENGINE_PATH;
    private static String IMG_PATH;
    private static String SND_PATH;
    private static String FONT_PATH;
    private static String TYPES_PATH;
    private static String MACRO_TYPES_PATH;
    private static boolean PRESENTATION_MODE;
    private static String RES_PATH;
    private static Lock initLock = new ReentrantLock();
    private static volatile boolean isInitialized = false;
    private static String jarName;

    private static void _init() {
        ClassLoader classLoader = PathFinder.class.getClassLoader();
        if (classLoader.getResource("") != null) {
            File temp = new File(classLoader.getResource("").getFile());
            ENGINE_PATH = new File(temp.getParentFile().toURI()) + File.separator;
            XML_PATH = new File(temp.getParentFile() + File.separator + "XML") + File.separator;
            System.out.println("Engine path: " + ENGINE_PATH);
        } else {
            //FOR JARS
            CoreEngine.setJar(true);

            URI uri = null;
            try {
                uri =
                new CoreEngine().getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (jarName==null )
                jarName = StringMaster.getLastPathSegment((uri.toString()));

            System.out.println("jarName: " + jarName);
            String path =
             new File(uri.toString().replace(jarName+StringMaster.getPathSeparator(), "")).getAbsolutePath();
            path = path.split("file:")[0];
            System.out.println("Engine path for Jar: " + path);

            ENGINE_PATH = path + File.separator;
            XML_PATH = path + File.separator + "XML" + File.separator;

        }

        RES_PATH = "resource" + File.separator;
        IMG_PATH = ENGINE_PATH + RES_PATH + "img\\";

        SND_PATH = ENGINE_PATH + RES_PATH + "sound\\";

        FONT_PATH = ENGINE_PATH + RES_PATH + "Fonts\\";

        MACRO_TYPES_PATH = XML_PATH + MACRO_MODULE_NAME + "\\types\\";

        TYPES_PATH = XML_PATH + MICRO_MODULE_NAME + "\\types\\"
         + ((PRESENTATION_MODE) ? PRESENTATION : "");

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
        return XML_PATH + "\\dungeons\\";
    }

    public static String getDungeonMissionFolder() {
        init();
        return XML_PATH + "\\dungeons\\missions\\";
    }

    public static String getSkirmishBattlefieldFolder() {
        init();
        return getDungeonLevelFolder() + "\\skirmish\\battlefields\\";
    }

    public static String getDungeonLevelFolder() {
        init();
        return XML_PATH + "\\dungeons\\levels\\";
    }

    public static String getTextPath() {
        init();
        return RES_PATH + "\\text\\";
    }

    public static String getScenariosPath() {
        init();
        return RES_PATH + "\\text\\scenario\\";
    }

    public static String getLogPath() {
        init();
        return getTextPath() + "\\log\\";
    }

    public static String getUnitGroupPath() {
        init();
        return XML_PATH + "\\groups\\";
    }

    public static String getImagePath() {
        init();
        return IMG_PATH;
    }

    public static String getSoundPath() {
        init();
        return SND_PATH;
    }

    public static String getFontPath() {
        init();
        return FONT_PATH;
    }

    public static String getTemplatesPath() {
        init();
        return XML_PATH + ABILITY_TEMPLATES_PATH;
    }

    public static String getBgPicsPath() {
        init();
        return BG_PATH;
    }

    public static String getTYPES_PATH() {
        init();
        return TYPES_PATH;
    }

    public static void setPresentationMode(boolean PRESENTATION) {
        PRESENTATION_MODE = PRESENTATION;
    }

    public static String getEnginePath() {
        init();
        return ENGINE_PATH;
    }

    public static String getModulePath() {
        init();
//        if (CoreEngine.isJar())
//            return ENGINE_PATH;
        return StringMaster.cropLastPathSegment(ENGINE_PATH);
    }
    public static String getEnginePathPlusNewResourceProject() {
        init();
        return ENGINE_PATH + "\\\\resource\\";
    }

    public static String getThemedBgPicsPath() {
        init();
        return IMG_PATH + "\\mini\\bg\\";
    }

    public static String getMACRO_TYPES_PATH() {
        init();
        return MACRO_TYPES_PATH;
    }

    public static String getPrefsPath() {
        init();
        return ENGINE_PATH + getTextPath() + "prefs\\";
    }

    public static String getWorkspacePath() {
        init();
        return XML_PATH + "\\workspaces\\";
    }

    public static String getXML_PATH() {
        init();
        return XML_PATH;
    }

    public static String getMapBlockFolderPath() {
        init();
        return getLevelEditorPath() + "blocks\\";
    }

    public static String getLevelEditorPath() {
        init();
        return XML_PATH + "Level Editor\\";
    }

    public static String getSpellUpgradeGlyphsFolder() {
        init();
        return getImagePath() + "ui\\glyphs\\";
    }

    public static boolean isFullPath(String path) {
        init();
        return path.contains(ENGINE_PATH);
    }

    public static String getSfxPath() {
        init();
        return getImagePath() + "mini\\sfx\\";
    }

    public static String getSpritesPath() {
        init();
        return getImagePath() + "mini\\sprites\\";
    }

    public static String getParticlePresetPath() {
        init();
        return getImagePath() + "mini\\sfx\\";
    }

    public static String getParticleImagePath() {
        init();
        return getParticlePresetPath() + "images\\";
    }

    public static String removeSpecificPcPrefix(String imagePath) {
        init();
        return imagePath.replace(getEnginePath(), "");
    }

    public static String getJarPath() {
        return getEnginePath()+jarName;
    }
}
