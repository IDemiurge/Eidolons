package main.system.launch;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.ability.ARGS;
import main.data.ability.Mapper;
import main.data.xml.XML_Reader;
import main.system.auxiliary.ContainerUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoreEngine {
    public final static String[] classFolderPaths = {"main.elements", "main.ability", "eidolons.elements", "eidolons.ability"};
    public static final String VERSION = "0.7.0";
    public static final boolean DEV_MODE =true ;
    public static boolean EXE_MODE = true;
    public static boolean swingOn = false;
    public static boolean animationTestMode;
    private static CoreEngine engineObject;
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
    private static boolean itemGenerationOff;
    private static boolean jar;
    private static boolean exe;
    private static boolean mapEditor;
    private static boolean macro;
    private static boolean jarlike;
    private static boolean fastMode;
    private static boolean jUnit;
    private static boolean initializing;
    private static boolean initialized;
    private static boolean crashSafeMode=true;
    private static boolean utility;

    public static void systemInit() {
        Chronos.mark("SYSTEM INIT");
        ImageManager.init();
        if (!graphicsOff) {
            FontMaster.init();
            GuiManager.init();

        }
        SoundMaster.initialize();
        DataManager.init();
        Chronos.logTimeElapsedForMark("SYSTEM INIT");
    }

    public static void init(boolean macro) {

        systemInit();
        CoreEngine.dataInit(macro);
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
            return ContainerUtils.checkContainer(
             selectivelyReadTypes,
             StringMaster.cropFormat(StringMaster.cropLast(name, "-")),
             false);
        }

        if (exceptionTypes != null) {
            if (ContainerUtils.checkContainer(
             exceptionTypes,
             StringMaster.cropFormat(StringMaster.cropLast(name, "-")),
             false
            )) {
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
        if (graphicsOff) {

            WaitMaster.markAsComplete(WAIT_OPERATIONS.GUI_READY);
        }
    }

    public static boolean isActionTargetingFiltersOff() {
        return actionTargetingFiltersOff;
    }

    public static void setActionTargetingFiltersOff(boolean actionTargetingFiltersOff) {
        CoreEngine.actionTargetingFiltersOff = actionTargetingFiltersOff;
    }

    public static boolean isExceptionTraceLogged() {
        return true;
    }

    public static boolean isPhaseAnimsOn() {
        return phaseAnimsOn;
    }

    public static void setPhaseAnimsOn(boolean phaseAnimsOn) {
        CoreEngine.phaseAnimsOn = phaseAnimsOn;
    }

    public static void dataInit(boolean macro) {

        Chronos.mark("TYPES INIT");

        XML_Reader.readTypes(macro);
        WaitMaster.receiveInput(WAIT_OPERATIONS.XML_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.XML_READY );
        List<String> classFolders = new ArrayList<>(Arrays.asList(classFolderPaths));
//         if (dialogueDataRequired){
//             classFolders.add( "main.data.dialogue" );
//             classFolders.add(  "main.game.battlecraft.logic.meta.scenario.dialogue.speech" );
//         }

        Chronos.logTimeElapsedForMark("TYPES INIT");
        if (!macro)
            try {
                Chronos.mark("MAPPER INIT");
                Mapper.compileArgMap(Arrays.asList(ARGS.getArgs()),
                 classFolders);
                Chronos.logTimeElapsedForMark("MAPPER INIT");
            } catch (ClassNotFoundException | SecurityException | IOException e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

    }

    public static boolean isItemGenerationOff() {
        return itemGenerationOff;
    }

    public static void setItemGenerationOff(boolean itemGenerationOff) {
        CoreEngine.itemGenerationOff = itemGenerationOff;
    }

    public static boolean isTargetingResultCachingOn() {
        return !isLogicTest();
    }

    public static CoreEngine getEngineObject() {
        return engineObject;
    }

    public static void setEngineObject(CoreEngine engineObject) {
        CoreEngine.engineObject = engineObject;
    }

    public static boolean isJar() {
        return jar;
    }

    public static void setJar(boolean jar) {
        CoreEngine.jar = jar;
    }

    public static boolean isCombatGame() {
        return !isArcaneTower() && !isArcaneVault() && !isLevelEditor();
    }

    public static boolean isIDE() {
        return !exe && !jar && !jarlike;
    }

    public static boolean isExe() {
        return exe;
    }

    public static void setExe(boolean exe) {
        CoreEngine.exe = exe;
    }

    public static boolean isDefaultValuesAddedDynamically() {
        return false;
    }

    public static boolean isMapEditor() {
        return mapEditor;
    }

    public static void setMapEditor(boolean mapEditor) {
        CoreEngine.mapEditor = mapEditor;
    }

    public static boolean isMacro() {
        return macro;
    }

    public static void setMacro(boolean macro) {
        CoreEngine.macro = macro;
    }

    public static boolean isJarlike() {
        return jarlike;
    }

    public static void setJarlike(boolean jarlike) {
        CoreEngine.jarlike = jarlike;
    }

    public static boolean isFastMode() {
        return fastMode;
    }

    public static void setFastMode(boolean fastMode) {
        CoreEngine.fastMode = fastMode;
    }

    public static boolean isJUnit() {
        return jUnit;
    }

    public static boolean isjUnit() {
        return jUnit;
    }

    public static void setjUnit(boolean jUnit) {
        CoreEngine.jUnit = jUnit;
    }

    public static boolean isInitializing() {
        return initializing;
    }

    public static void setInitializing(boolean initializing) {
        CoreEngine.initializing = initializing;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setInitialized(boolean initialized) {
        CoreEngine.initialized = initialized;
    }

    public static boolean isTestingMode() {
        return true;
    }

    public static boolean isCrashSafeMode() {
        return crashSafeMode;
    }

    public static void setCrashSafeMode(boolean crashSafeMode) {
        CoreEngine.crashSafeMode = crashSafeMode;
    }

    public static boolean isUtility() {
        return utility;
    }

    public static void setUtility(boolean utility) {
        CoreEngine.utility = utility;
    }
}
