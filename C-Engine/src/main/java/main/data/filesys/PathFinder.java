package main.data.filesys;

import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PathFinder {

    private static final String BG_PATH = "big"+StringMaster.getPathSeparator();
    private static final String PRESENTATION = "presentation"+StringMaster.getPathSeparator();
    private static final String RES_FOLDER_NAME = "resources";
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
                main.system.ExceptionMaster.printStackTrace(e);
            }
            if (jarName==null )
                jarName = StringMaster.getLastPathSegment((uri.toString()));

            if (jarName.contains(".exe")){
                CoreEngine.setExe(true);
            }

            System.out.println("jarName: " + jarName);
            String path =
             new File(uri.toString().replace(jarName+StringMaster.getPathSeparator(), "")).getAbsolutePath();
            path = path.split("file:")[0];
            System.out.println("Engine path for Jar: " + path);

            ENGINE_PATH = path + File.separator;
            XML_PATH = path + File.separator + "XML" + File.separator;

        }

        RES_PATH = RES_FOLDER_NAME + File.separator;
        IMG_PATH = ENGINE_PATH + RES_PATH + "img"+StringMaster.getPathSeparator();

        main.system.auxiliary.log.LogMaster.log(1,"IMG_PATH PATH= "+IMG_PATH   );
        SND_PATH = ENGINE_PATH + RES_PATH + "sound"+StringMaster.getPathSeparator();

        FONT_PATH = ENGINE_PATH + RES_PATH + "Fonts"+StringMaster.getPathSeparator();

        MACRO_TYPES_PATH = XML_PATH + MACRO_MODULE_NAME + StringMaster.getPathSeparator()+"types"+StringMaster.getPathSeparator();

        TYPES_PATH = XML_PATH + MICRO_MODULE_NAME + StringMaster.getPathSeparator()+"types"+StringMaster.getPathSeparator()
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
        return XML_PATH + StringMaster.getPathSeparator()+"dungeons"+StringMaster.getPathSeparator();
    }

    public static String getDungeonMissionFolder() {
        init();
        return XML_PATH + StringMaster.getPathSeparator()+"dungeons"+StringMaster.getPathSeparator()+"missions"+StringMaster.getPathSeparator();
    }

    public static String getSkirmishBattlefieldFolder() {
        init();
        return getDungeonLevelFolder() + StringMaster.getPathSeparator()+"skirmish"+StringMaster.getPathSeparator()+"battlefields"+StringMaster.getPathSeparator();
    }

    public static String getDungeonLevelFolder() {
        init();
        return XML_PATH  +"dungeons"+StringMaster.getPathSeparator()+"levels"+StringMaster.getPathSeparator();
    }

    public static String getTextPath() {
        init();
        return RES_PATH + StringMaster.getPathSeparator()+"text"+StringMaster.getPathSeparator();
    }

    public static String getScenariosPath() {
        init();
        return RES_PATH + StringMaster.getPathSeparator()+"text"+StringMaster.getPathSeparator()+"scenario"+StringMaster.getPathSeparator();
    }

    public static String getLogPath() {
        init();
        return getTextPath() + StringMaster.getPathSeparator()+"log"+StringMaster.getPathSeparator();
    }

    public static String getUnitGroupPath() {
        init();
        return XML_PATH + StringMaster.getPathSeparator()+"groups"+StringMaster.getPathSeparator();
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
        return ENGINE_PATH  +StringMaster.getPathSeparator()+RES_FOLDER_NAME+StringMaster.getPathSeparator();
    }

    public static String getThemedBgPicsPath() {
        init();
        return IMG_PATH + StringMaster.getPathSeparator()+"mini"+StringMaster.getPathSeparator()+"bg"+StringMaster.getPathSeparator();
    }

    public static String getMACRO_TYPES_PATH() {
        init();
        return MACRO_TYPES_PATH;
    }

    public static String getPrefsPath() {
        init();
        return ENGINE_PATH + getTextPath() + "prefs"+StringMaster.getPathSeparator();
    }

    public static String getWorkspacePath() {
        init();
        return XML_PATH + StringMaster.getPathSeparator()+"workspaces"+StringMaster.getPathSeparator();
    }

    public static String getXML_PATH() {
        init();
        return XML_PATH;
    }

    public static String getMapBlockFolderPath() {
        init();
        return getLevelEditorPath() + "blocks"+StringMaster.getPathSeparator();
    }

    public static String getLevelEditorPath() {
        init();
        return XML_PATH + "Level Editor"+StringMaster.getPathSeparator();
    }

    public static String getSpellUpgradeGlyphsFolder() {
        init();
        return getImagePath() + "ui"+StringMaster.getPathSeparator()+"glyphs"+StringMaster.getPathSeparator();
    }

    public static boolean isFullPath(String path) {
        init();
        return path.contains(ENGINE_PATH);
    }

    public static String getSfxPath() {
        init();
        return getImagePath() + StringMaster.getPathSeparator()+"mini"+StringMaster.getPathSeparator()+"sfx"+StringMaster.getPathSeparator();
    }

    public static String getSpritesPath() {
        init();
        return getImagePath() +  StringMaster.getPathSeparator()+
         "mini"+StringMaster.getPathSeparator()+"sprites"+StringMaster.getPathSeparator() ;
    }

    public static String getSpritesPathNew() {
        init();
        return getImagePath() +  StringMaster.getPathSeparator()+
         "main"+StringMaster.getPathSeparator()+"sprites"+StringMaster.getPathSeparator() ;
    }
    public static String getParticlePresetPath() {
        init();
        return getImagePath() + StringMaster.getPathSeparator()+"mini"+StringMaster.getPathSeparator()+"sfx"+StringMaster.getPathSeparator();
    }

    public static String getMusicPath() {
        init();
        return getEnginePathPlusNewResourceProject()+"music"+StringMaster.getPathSeparator();
    }
    public static String getParticleImagePath() {
        init();
        return getParticlePresetPath() + "images"+StringMaster.getPathSeparator();
    }

    public static String removeSpecificPcPrefix(String imagePath) {
        init();
        return imagePath.replace(getEnginePath(), "");
    }

    public static String getJarPath() {
        return getEnginePath()+jarName;
    }

    public static String getEmblemAutoFindPath() {
        return StrPathBuilder.build( "ui", "emblems", "auto")+StringMaster.getPathSeparator();
    }

    public static String getWeaponAnimPath() {
        return StrPathBuilder.build(getSpritesPathMain(), "weapons3d")+StringMaster.getPathSeparator();
    }

    public static String getPotionsAnimPath() {
        return StrPathBuilder.build(getSpritesPathMain(), "potions")+StringMaster.getPathSeparator();
    }
    private static String getSpritesPathMain() {
        init();
        return
         "main"+StringMaster.getPathSeparator()+"sprites"+StringMaster.getPathSeparator() ;
    }

    public static String getVideoPath() {
        return StrPathBuilder.build(getEnginePathPlusNewResourceProject(),
         "video") + StringMaster.getPathSeparator();
    }
}
