package main.system.launch;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.ability.ARGS;
import main.data.ability.Mapper;
import main.data.filesys.ResourceManager;
import main.data.xml.XML_Reader;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.graphics.FontMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.sound.SoundMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.io.IOException;
import java.util.Arrays;

public class CoreEngine {
    public final static String[] classFolderPaths = {"main.elements", "main.ability"};
    public static final String VERSION = "0.104";
    public static boolean swingOn = false;
    public static boolean animationTestMode;
    private static boolean TEST_MODE = true;
    private static SoundMaster sm;
    private static boolean arcaneVault;
    private static boolean concurrentLaunch;
    private static boolean menuScope = true;
    private static boolean levelEditor;
    private static String selectivelyReadTypes;
    private static String exceptionTypes;
    private static boolean enumCachingOn = true;
    private static boolean writingLogFilesOn;
    private static boolean arcaneTower = false;
    private static boolean graphicTestMode = false;
    private static boolean graphicsOff;
    private static boolean guiTestMode;
    private static boolean actionTargetingFiltersOff;
    private static boolean phaseAnimsOn;
    private static boolean logicTest;

    public static void dataInit(boolean newThread, final boolean macro) {
        if (newThread) {
            new Thread(new Runnable() {
                public void run() {
                    new CoreEngine().dataInit(macro);
                }

            }).start();
            return;
        }
        new CoreEngine().dataInit(macro);

    }

    public static void systemInit() {

        Chronos.mark("SYSTEM INIT");
        ImageManager.init();
        FontMaster.init();
        GuiManager.init();
        SoundMaster.initialize();
        ResourceManager.init();
        DataManager.init();
        Chronos.logTimeElapsedForMark("SYSTEM INIT");
    }

    public static void init(boolean macro) {

        systemInit();
        new CoreEngine().dataInit(macro);
    }

    public static SoundMaster getSm() {
        return sm;
    }

    public static void setSm(SoundMaster sm) {
        CoreEngine.sm = sm;
    }

    public static synchronized boolean isArcaneVault() {
        return arcaneVault;
    }

    public static void setArcaneVault(boolean b) {
        arcaneVault = b;
    }

    public static boolean isLevelEditor() {
        return levelEditor;
    }

    public static void setLevelEditor(boolean le) {
        levelEditor = le;
    }

    public static boolean isConcurrentLaunch() {
        return concurrentLaunch;
    }

    public static void setConcurrentLaunch(boolean concurrentLaunch) {
        CoreEngine.concurrentLaunch = concurrentLaunch;
    }

    public static void setExceptionTypes(String exceptionTypes) {
        CoreEngine.exceptionTypes = exceptionTypes;
    }

    public static boolean checkReadNecessary(String name) {

        if (selectivelyReadTypes != null) {
            return StringMaster.checkContainer(selectivelyReadTypes, StringMaster
                            .cropFormat(StringMaster.cropLast(name, "-"))

                    , false);
        }

        if (exceptionTypes != null) {
            if (StringMaster.checkContainer(exceptionTypes, StringMaster
                            .cropFormat(StringMaster.cropLast(name, "-"))
                    , false)) {
                return false;
            }
        }

        DC_TYPE TYPE = new EnumMaster<DC_TYPE>().retrieveEnumConst(DC_TYPE.class, name);

        if (isMenuScope() && !arcaneVault && !TEST_MODE) {
            if (TYPE == DC_TYPE.CHARS) {
                return true;
            }
            if (TYPE == DC_TYPE.PARTY) {
                return true;
            }
            if (TYPE == DC_TYPE.DEITIES) {
                return true;
            }
            if (TYPE == DC_TYPE.ARMOR) {
                return true;
            }
            if (TYPE == DC_TYPE.WEAPONS) {
                return true;
            }
            return TYPE == DC_TYPE.ITEMS;
        }
        return true;
    }

    public static boolean isLogicTest() {
         return logicTest;
    }

    public static void setLogicTest(boolean tEST_MODE) {
        logicTest = tEST_MODE;
    }

    public static boolean isMenuScope() {
        return menuScope;
    }

    public static void setMenuScope(boolean menuScope) {
        CoreEngine.menuScope = menuScope;
    }

    public static void setSelectivelyReadTypes(String types) {
        selectivelyReadTypes = types;
    }

    public static void setArcaneVaultMode(boolean b) {
        // TODO Auto-generated method stub

    }

    public static boolean isEnumCachingOn() {
        return enumCachingOn;
    }

    public static boolean isMinimizeLogging() {
        return true;
    }

    public static boolean isWritingLogFilesOn() {
        return writingLogFilesOn;
    }

    public static void setWritingLogFilesOn(boolean writingLogFilesOn) {
        CoreEngine.writingLogFilesOn = writingLogFilesOn;
    }

    public static boolean isGraphicTestMode() {
        return graphicTestMode;
    }

    public static void setGraphicTestMode(boolean graphicTestMode) {
        CoreEngine.graphicTestMode = graphicTestMode;
    }

    public static boolean isGuiTestMode() {
        return guiTestMode;
    }

    public static void setGuiTestMode(boolean guiTestMode) {
        CoreEngine.guiTestMode = guiTestMode;
    }

    public static boolean isAnimationTestMode() {
        return animationTestMode;
    }

    public static boolean isArcaneTower() {
        return arcaneTower;
    }

    public static void setArcaneTower(boolean arcaneTower) {
        CoreEngine.arcaneTower = arcaneTower;
    }

    public static boolean isSwingOn() {
        return swingOn;
    }

    public static boolean isGraphicsOff() {
        return graphicsOff;
    }

    public static void setGraphicsOff(boolean graphicsOff) {
        CoreEngine.graphicsOff = graphicsOff;
        if (graphicsOff){

            WaitMaster.markAsComplete(WAIT_OPERATIONS.GUI_READY);
            WaitMaster.markAsComplete(WAIT_OPERATIONS.GDX_READY);
        }
    }

    public static boolean isActionTargetingFiltersOff() {
        return actionTargetingFiltersOff;
    }

    public static void setActionTargetingFiltersOff(boolean actionTargetingFiltersOff) {
        CoreEngine.actionTargetingFiltersOff = actionTargetingFiltersOff;
    }

    public static boolean isPhaseAnimsOn() {
        return phaseAnimsOn;
    }

    public static void setPhaseAnimsOn(boolean phaseAnimsOn) {
        CoreEngine.phaseAnimsOn = phaseAnimsOn;
    }

    public void dataInit(boolean macro) {

        Chronos.mark("TYPES INIT");
        XML_Reader.readTypes(macro);
        Chronos.logTimeElapsedForMark("TYPES INIT");
        // if (!macro)
        try {
            Chronos.mark("MAPPER INIT");
            Mapper.compileArgMap(Arrays.asList(ARGS.getArgs()), Arrays.asList(classFolderPaths));
            Chronos.logTimeElapsedForMark("MAPPER INIT");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
