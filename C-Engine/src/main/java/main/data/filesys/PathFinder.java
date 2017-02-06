package main.data.filesys;

import main.system.auxiliary.LogMaster;
import main.system.launch.CoreEngine;

import java.io.File;

public class PathFinder {

    private static final String BG_PATH = "big\\";
    private static final String PRESENTATION = "presentation\\";
    private static String MICRO_MODULE_NAME = "duel-club";
    private static final String ABILITY_TEMPLATES_PATH = MICRO_MODULE_NAME + "//templates";
    private static String MACRO_MODULE_NAME = "macro";
    private static String XML_PATH;
    private static String CLASS_PATH = "";
    private static String fullpath;
    private static String ENGINE_PATH = "";
    private static String RESOURCE_PATH = "";
    private static String IMG_PATH;
    private static String SND_PATH;
    private static String FONT_PATH;
    private static String TYPES_PATH;
    private static String MACRO_TYPES_PATH;
    private static boolean PRESENTATION_MODE;
    private static String RES_PATH = "";

    public static void init() {
        ClassLoader classLoader = PathFinder.class.getClassLoader();
        File temp = new File(classLoader.getResource("").getFile());
        ENGINE_PATH = new File(temp.getParentFile().toURI()) + File.separator;
        if (CoreEngine.isArcaneVault()) {
            ENGINE_PATH = ENGINE_PATH.replace("Arcane-Vault", "Dueling-Club");
        }
        if (CoreEngine.isArcaneTower()) {
            ENGINE_PATH = ENGINE_PATH.replace("Arcane-Tower", "Dueling-Club");
        }
        fullpath = System.getProperty("java.class.path");
        main.system.auxiliary.LogMaster.log(LogMaster.CORE_DEBUG, fullpath);
        // Err.info(fullpath);

        if (fullpath.contains(System.getProperty("path.separator"))) {
            setClassPath();
            setEnginePath();
            setRES_PATH("resource" + File.separator);
        }

        setXML_PATH(new File(temp.getParentFile() + File.separator + "XML") + File.separator);
        if (CoreEngine.isArcaneVault() || CoreEngine.isArcaneTower()) {
            XML_PATH = XML_PATH.replace("Arcane-Vault", "Dueling-Club");
        }
        if (CoreEngine.isArcaneTower()) {
            XML_PATH = XML_PATH.replace("Arcane-Tower", "Dueling-Club");
        }
        IMG_PATH = ENGINE_PATH + getRES_PATH() + "img\\";

        SND_PATH = ENGINE_PATH + getRES_PATH() + "sound\\";

        FONT_PATH = ENGINE_PATH + getRES_PATH() + "Fonts\\";

        setMACRO_TYPES_PATH(getXML_PATH() + MACRO_MODULE_NAME + "\\types\\");

        setTYPES_PATH(getXML_PATH() + MICRO_MODULE_NAME + "\\types\\"
                + ((PRESENTATION_MODE) ? PRESENTATION : ""));

    }

    private static void setEnginePath() {

        for (String s : fullpath.split(System.getProperty("path.separator"))) {

            if (s.contains("C-Engine") && s.contains("bin")) {

                ENGINE_PATH = s.substring(0, s.length() - 3);
                break;
            }
        }

    }

    private static void setClassPath() {

        String str = fullpath.substring(0, fullpath.indexOf(System.getProperty("path.separator")));
        CLASS_PATH = str.substring(0, str.length() - 3);

    }

    public static String getDungeonFolder() {
        return getXML_PATH() + "\\dungeons\\";
    }

    public static String getDungeonMissionFolder() {
        return getXML_PATH() + "\\dungeons\\missions\\";
    }

    public static String getSkirmishBattlefieldFolder() {
        return getDungeonLevelFolder() + "\\skirmish\\battlefields\\";
    }

    public static String getDungeonLevelFolder() {
        return getXML_PATH() + "\\dungeons\\levels\\";
    }

    public static String getTextPath() {
        return getRES_PATH() + "\\text\\";
    }

    public static String getLogPath() {
        return getTextPath() + "\\log\\";
    }

    public static String getUnitGroupPath() {
        return getXML_PATH() + "\\groups\\";
    }

    public static String getXmlTypesFolderPath() {

        return getTYPES_PATH();
    }

    public static String getImagePath() {
        return IMG_PATH;
    }

    public static String getDefaultImageLocation() {
        return IMG_PATH;
    }

    public static String getSoundPath() {
        return SND_PATH;
    }

    public static String getFontPath() {
        return FONT_PATH;
    }

    public static String getTemplatesPath() {
        return getXML_PATH() + ABILITY_TEMPLATES_PATH;
    }

    public static String getBgPicsPath() {
        return BG_PATH;
    }

    public static String getTYPES_PATH() {
        return TYPES_PATH;
    }

    public static void setTYPES_PATH(String path) {
        TYPES_PATH = path;
    }

    public static void setPresentationMode(boolean PRESENTATION) {
        PRESENTATION_MODE = PRESENTATION;
    }

    public static String getClassPath() {
        return CLASS_PATH;
    }

    public static String getEnginePath() {
        return ENGINE_PATH;
    }

    public static String getEnginePathPlusNewResourceProject() {
        return ENGINE_PATH + "\\\\resource\\";
    }
    public static String getThemedBgPicsPath() {
        // TODO Auto-generated method stub
        return IMG_PATH + "\\mini\\bg\\";
    }

    public static String getMACRO_TYPES_PATH() {
        return MACRO_TYPES_PATH;
    }

    public static void setMACRO_TYPES_PATH(String mACRO_TYPES_PATH) {
        MACRO_TYPES_PATH = mACRO_TYPES_PATH;
    }

    public static String getPrefsPath() {
        return PathFinder.getEnginePath() + getTextPath() + "prefs\\";
    }

    // public static String getValueIconsPath() {
    // return VALUE_ICONS_PATH;
    // }

    public static String getRES_PATH() {
        return RES_PATH;
    }

    public static void setRES_PATH(String rES_PATH) {
        RES_PATH = rES_PATH;
    }

    public static String getWorkspacePath() {
        return getXML_PATH() + "\\workspaces\\";
    }

    public static String getXML_PATH() {
        return XML_PATH;
    }

    public static void setXML_PATH(String xML_PATH) {
        XML_PATH = xML_PATH;
    }

    public static String getMapBlockFolderPath() {
        return getLevelEditorPath() + "blocks\\";
    }

    public static String getLevelEditorPath() {
        return getXML_PATH() + "Level Editor\\";
    }

    public static String getSpellUpgradeGlyphsFolder() {
        return getImagePath() + "ui\\glyphs\\";
    }

    public static boolean isFullPath(String path) {
        return path.contains(ENGINE_PATH);
    }
    public static String getSfxPath() {
        return getImagePath()+ "mini\\sfx\\";
    }
    public static String getSpritesPath() {
        return getImagePath()+ "mini\\sprites\\";
    }

    public static String getParticlePresetPath() {
        return getImagePath() + "mini\\sfx\\";
    }

    public static String getParticleImagePath() {
        return getParticlePresetPath() + "images\\";
    }

    public static String removeSpecificPcPrefix(String imagePath) {
        return imagePath.replace(getEnginePath(), "");
    }
}
