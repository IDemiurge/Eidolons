package main.system.launch;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.ability.ARGS;
import main.data.ability.Mapper;
import main.data.xml.XML_Reader;
import main.system.ExceptionMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.LogMaster;
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
    public static final String VERSION = "0.9.8c";
    public static final UPLOAD_PACKAGE uploadPackage = UPLOAD_PACKAGE.Aphotic;
    public static final String VERSION_NAME = StringMaster.getWellFormattedString(uploadPackage.toString());
    public static final boolean DEV_MODE = true;
    public static String filesVersion = "v" + VERSION.replace(".", "-");
    public static boolean swingOn = true;
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
    private static boolean vfxOff;
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
    private static boolean crashSafeMode = true;
    private static boolean utility;
    private static float memoryLevel;
    private static boolean fullFastMode;
    private static Boolean windows;
    private static long HEAP_SIZE;
    private static long TOTAL_MEMORY;
    private static int CPU_NUMBER;
    private static boolean me;
    private static boolean cinematicMode;
    private static boolean mapPreview;
    private static boolean safeMode;
    private static boolean iggDemo;
    private static boolean iggDemoRunning;
    private static boolean toolIsRunning;
    private static boolean activeTestMode;
    private static boolean liteLaunch;
    private static boolean dialogueTest;
    private static boolean skillTestMode;
    private static boolean contentTestMode;
    private static boolean ruleTestMode;
    private static boolean levelTestMode;
    private static boolean selectHeroMode;
    private static boolean debugLaunch;
    private static boolean devEnabled;
    private static boolean reverseExit;
    private static boolean ramEconomy;
    private static boolean keyCheat;
    private static boolean mainGame;
    private static boolean autoFixOn;
    private static boolean dungeonTool;
    private static boolean testerVersion;
    private static boolean superLite;
    private static boolean weakGpu;
    private static boolean youTube;

    public static void setWeakGpu(boolean weakGpu) {
        CoreEngine.weakGpu = weakGpu;
        LogMaster.important("Setting Weak GPU to " +weakGpu);
    }

    public static boolean getWeakGpu() {
        return weakGpu;
    }

    public static boolean isYouTube() {
        return youTube;
    }

    public static void setYouTube(boolean youTube) {
        CoreEngine.youTube = youTube;
    }


    public enum UPLOAD_PACKAGE {
        Aphotic, igg,
    }


    public static boolean isMyLiteLaunch() {
        return isIDE() && isLiteLaunch();
    }

    public static void systemInit() {
        Chronos.mark("SYSTEM INIT");
        System.out.println("Eidolons " + VERSION);
        System.out.println("Core Engine Init... ");


        System.out.println("Heap size:  " +
                (HEAP_SIZE = Runtime.getRuntime().maxMemory()));
        System.out.println("CPU's available:  " +
                (CPU_NUMBER = Runtime.getRuntime().availableProcessors()));
//        System.out.println("Total Memory:  " +
//         (TOTAL_MEMORY =
//          Runtime.getRuntime().totalMemory()));

        if (System.getProperty("user.home").equalsIgnoreCase("C:\\Users\\JustM")) {
            me = true;
        }

        ImageManager.init();
        if (!graphicsOff) {
            if (isSwingOn())
                FontMaster.init();
            GuiManager.init();

        }
        SoundMaster.initialize();
        DataManager.init();
        Chronos.logTimeElapsedForMark("SYSTEM INIT");

        System.out.println("...Core Engine Init finished");
        if (!me) {
            System.out.println();
            System.getProperties().list(System.out);
            System.getProperties().list(FileLogManager.getMainPrintStream());
            System.out.println();
        }
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

    public static void setSwingOn(boolean swingOn) {
        CoreEngine.swingOn = swingOn;
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
        WaitMaster.markAsComplete(WAIT_OPERATIONS.XML_READY);
        List<String> classFolders = new ArrayList<>(Arrays.asList(classFolderPaths));
        //         if (dialogueDataRequired){
        //             classFolders.add( "main.data.dialogue" );
        //             classFolders.add(  "main.game.battlecraft.logic.meta.scenario.dialogue.speech" );
        //         }

        Chronos.logTimeElapsedForMark("TYPES INIT");
        try {
            Chronos.mark("MAPPER INIT");
            Mapper.compileArgMap(Arrays.asList(ARGS.getArgs()),
                    classFolders);
            Chronos.logTimeElapsedForMark("MAPPER INIT");
        } catch (ClassNotFoundException | SecurityException | IOException e) {
            ExceptionMaster.printStackTrace(e);
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
        return isMainGame();
//        return !toolIsRunning && !isArcaneTower() && !isArcaneVault() && !isLevelEditor() && !isjUnit();
    }

    public static boolean isIDE() {
        return !exe && !jar;
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

    public static float getMemoryLevel() {
        return memoryLevel;
    }

    public static void setMemoryLevel(float memoryLevel) {
        CoreEngine.memoryLevel = memoryLevel;
    }

    public static boolean isFullFastMode() {
        return fullFastMode;
    }

    public static void setFullFastMode(boolean fullFastMode) {
        CoreEngine.fullFastMode = fullFastMode;
    }

    public static boolean isWindows() {
        if (windows==null) {
            windows= System.getProperty("os.name").startsWith("Windows");
        }
        return windows;
    }

    public static void setWindows(boolean windows) {
        CoreEngine.windows = windows;
    }

    public static boolean isMe() {
        return me;
    }

    public static void setMe(boolean me) {
        CoreEngine.me = me;
    }

    public static boolean isFootageMode() {
        return cinematicMode;
    }

    public static void setCinematicMode(boolean cinematicMode) {
        CoreEngine.cinematicMode = cinematicMode;
    }

    public static void setMapPreview(boolean mapPreview) {
        CoreEngine.mapPreview = mapPreview;
    }

    public static boolean isMapPreview() {
        return mapPreview;
    }

    public static boolean isSafeMode() {
        return safeMode;
    }

    public static void setSafeMode(boolean safeMode) {
        CoreEngine.safeMode = safeMode;
    }

    public static boolean isOutlinesFixed() {
        return true;
    }

    public static boolean isIggDemo() {
        return iggDemo;
    }

    public static void setIggDemo(boolean iggDemo) {
        CoreEngine.iggDemo = iggDemo;
    }

    public static void setIggDemoRunning(boolean iggDemoRunning) {
        CoreEngine.iggDemoRunning = iggDemoRunning;
    }

    public static boolean isIggDemoRunning() {
        return iggDemoRunning;
    }

    public static void setToolIsRunning(boolean toolIsRunning) {
        CoreEngine.toolIsRunning = toolIsRunning;
    }

    public static boolean getToolIsRunning() {
        return toolIsRunning;
    }

    public static boolean isActiveTestMode() {
        return activeTestMode;
    }

    public static void setActiveTestMode(boolean activeTestMode) {
        CoreEngine.activeTestMode = activeTestMode;
    }

    public static boolean isLiteLaunch() {
        if (isSuperLite())
            return true;
        return liteLaunch;
    }

    public static void setLiteLaunch(boolean liteLaunch) {
        CoreEngine.liteLaunch = liteLaunch;
    }

    public static boolean isDialogueTest() {
        return dialogueTest;
    }

    public static void setDialogueTest(boolean dialogueTest) {
        CoreEngine.dialogueTest = dialogueTest;
    }

    public static boolean isSkillTestMode() {
        return skillTestMode;
    }

    public static void setSkillTestMode(boolean skillTestMode) {
        CoreEngine.skillTestMode = skillTestMode;
    }

    public static boolean isContentTestMode() {
        return contentTestMode;
    }

    public static void setContentTestMode(boolean contentTestMode) {
        CoreEngine.contentTestMode = contentTestMode;
    }

    public static boolean isRuleTestMode() {
        return ruleTestMode;
    }

    public static void setRuleTestMode(boolean ruleTestMode) {
        CoreEngine.ruleTestMode = ruleTestMode;
    }

    public static boolean isLevelTestMode() {
        return levelTestMode;
    }

    public static void setLevelTestMode(boolean levelTestMode) {
        CoreEngine.levelTestMode = levelTestMode;
    }

    public static boolean isSelectHeroMode() {
        return selectHeroMode;
    }

    public static void setSelectHeroMode(boolean selectHeroMode) {
        CoreEngine.selectHeroMode = selectHeroMode;
    }

    public static boolean isDebugLaunch() {
        return debugLaunch;
    }

    public static void setDebugLaunch(boolean debugLaunch) {
        CoreEngine.debugLaunch = debugLaunch;
    }

    public static boolean isDevEnabled() {
        return devEnabled || isIDE();
    }

    public static void setDevEnabled(boolean devEnabled) {
        CoreEngine.devEnabled = devEnabled;
    }

    public static boolean isReverseExit() {
        return reverseExit;
    }

    public static void setReverseExit(boolean reverseExit) {
        CoreEngine.reverseExit = reverseExit;
    }

    public static boolean isRamEconomy() {
        return ramEconomy;
    }

    public static void setRamEconomy(boolean ramEconomy) {
        CoreEngine.ramEconomy = ramEconomy;
    }

    public static boolean isKeyCheat() {
        return keyCheat;
    }

    public static void setKeyCheat(boolean keyCheat) {
        CoreEngine.keyCheat = keyCheat;
    }

    public static void setMainGame(boolean mainGame) {
        CoreEngine.mainGame = mainGame;
    }

    public static boolean isMainGame() {
        return mainGame;
    }

    public static boolean isAutoFixOn() {
        return autoFixOn;
    }

    public static void setAutoFixOn(boolean autoFixOn) {
        CoreEngine.autoFixOn = autoFixOn;
    }

    public static boolean isDungeonTool() {
        return dungeonTool;
    }

    public static void setDungeonTool(boolean dungeonTool) {
        CoreEngine.dungeonTool = dungeonTool;
    }

    public static boolean isTesterVersion() {
        return testerVersion;
    }

    public static void setTesterVersion(boolean testerVersion) {
        CoreEngine.testerVersion = testerVersion;
    }

    public static boolean isSuperLite() {
        return superLite;
    }

    public static void setSuperLite(boolean superLite) {
        CoreEngine.superLite = superLite;
    }

    public static boolean isVfxOff() {
        return vfxOff || isSuperLite();
    }
    public static void setVfxOff(boolean vfxOff) {
        CoreEngine.vfxOff = vfxOff;
    }


    public static void setFlag(String field, Boolean val) {
        try {
            CoreEngine.class.getField(field).set(null, val);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            try {
                CoreEngine.class.getField(field.toUpperCase()).set(null, val);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
