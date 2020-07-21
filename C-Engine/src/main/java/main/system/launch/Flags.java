package main.system.launch;

public class Flags {
    public static final boolean DEV_MODE = true;
    public static final boolean FAST_TEST = false;
    public static final boolean ONE_FRAME_SPRITES = false;
    public static boolean animationTestMode;
    static boolean concurrentLaunch;
    static boolean writingLogFilesOn;
    static boolean graphicTestMode = false;
    static boolean guiTestMode;
    static boolean vfxOff;
    static boolean actionTargetingFiltersOff;
    static boolean phaseAnimsOn;
    private static boolean logicTest;
    static boolean itemGenerationOff;
    static boolean jar;
    static boolean exe;
    static boolean mapEditor;
    static boolean macro;
    static boolean jarlike;
    static boolean fastMode;
    static boolean jUnit;
    static boolean initializing;
    static boolean initialized;
    static boolean crashSafeMode = true;
    static boolean utility;
    static float memoryLevel;
    static boolean fullFastMode;
    static Boolean windows;
    static boolean me;
    static boolean cinematicMode;
    static boolean mapPreview;
    static boolean safeMode;
    static boolean iggDemo;
    static boolean iggDemoRunning;
    static boolean toolIsRunning;
    static boolean activeTestMode;
    static boolean liteLaunch;
    static boolean dialogueTest;
    static boolean skillTestMode;
    static boolean contentTestMode;
    static boolean ruleTestMode;
    static boolean levelTestMode;
    static boolean devEnabled;
    static boolean reverseExit;
    static boolean ramEconomy;
    static boolean keyCheat;
    static boolean mainGame;
    static boolean autoFixOn;
    static boolean dungeonTool;
    static boolean testerVersion;
    static boolean superLite;
    private static boolean fullVersion;

    public static boolean isLogicTest() {
        return logicTest;
    }

    public static boolean isMinimizeLogging() {
        return true;
    }

    public static boolean isWritingLogFilesOn() {
        return writingLogFilesOn;
    }

    public static boolean isGraphicTestMode() {
        return graphicTestMode;
    }

    public static boolean isGuiTestMode() {
        return guiTestMode;
    }

    public static boolean isAnimationTestMode() {
        return animationTestMode;
    }

    public static boolean isExceptionTraceLogged() {
        return true;
    }

    public static boolean isPhaseAnimsOn() {
        return phaseAnimsOn;
    }

    public static boolean isItemGenerationOff() {
        return itemGenerationOff;
    }

    public static boolean isTargetingResultCachingOn() {
        return !isLogicTest();
    }


    public static boolean isJar() {
        return jar;
    }

    public static void setJar(boolean jar) {
        Flags.jar = jar;
    }

    public static boolean isCombatGame() {
        return isMainGame();
        //        return !toolIsRunning && !isArcaneTower() && !isArcaneVault() && !isLevelEditor() && !isjUnit();
    }

    public static boolean isIDE() {
        return !exe && !jar;// && !isJarlike();
    }

    public static boolean isExe() {
        return exe;
    }

    public static void setExe(boolean exe) {
        Flags.exe = exe;
    }

    public static boolean isDefaultValuesAddedDynamically() {
        return false;
    }

    public static boolean isMapEditor() {
        return mapEditor;
    }

    public static void setMapEditor(boolean mapEditor) {
        Flags.mapEditor = mapEditor;
    }

    public static boolean isMacro() {
        return macro;
    }

    public static void setMacro(boolean macro) {
        Flags.macro = macro;
    }

    public static boolean isJarlike() {
        return  jarlike || isJar();
    }

    public static void setJarlike(boolean jarlike) {
        Flags.jarlike = jarlike;
    }

    public static boolean isFastMode() {
        return fastMode;
    }

    public static void setFastMode(boolean fastMode) {
        Flags.fastMode = fastMode;
    }

    public static boolean isJUnit() {
        return jUnit;
    }

    public static boolean isjUnit() {
        return jUnit;
    }

    public static void setjUnit(boolean jUnit) {
        Flags.jUnit = jUnit;
    }

    public static boolean isInitializing() {
        return initializing;
    }

    public static void setInitializing(boolean initializing) {
        Flags.initializing = initializing;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setInitialized(boolean initialized) {
        Flags.initialized = initialized;
    }

    public static boolean isCrashSafeMode() {
        return crashSafeMode;
    }

    public static void setCrashSafeMode(boolean crashSafeMode) {
        Flags.crashSafeMode = crashSafeMode;
    }

    public static boolean isUtility() {
        return utility;
    }

    public static void setUtility(boolean utility) {
        Flags.utility = utility;
    }

    public static float getMemoryLevel() {
        return memoryLevel;
    }

    public static void setMemoryLevel(float memoryLevel) {
        Flags.memoryLevel = memoryLevel;
    }

    public static boolean isFullFastMode() {
        return fullFastMode;
    }

    public static void setFullFastMode(boolean fullFastMode) {
        Flags.fullFastMode = fullFastMode;
    }

    public static boolean isWindows() {
        if (windows == null) {
            windows = System.getProperty("os.name").startsWith("Windows");
        }
        return windows;
    }

    public static void setWindows(boolean windows) {
        Flags.windows = windows;
    }

    public static boolean isMe() {
        return me;
    }

    public static void setMe(boolean me) {
        Flags.me = me;
    }

    public static boolean isFootageMode() {
        return cinematicMode;
    }

    public static void setCinematicMode(boolean cinematicMode) {
        Flags.cinematicMode = cinematicMode;
    }

    public static void setMapPreview(boolean mapPreview) {
        Flags.mapPreview = mapPreview;
    }

    public static boolean isMapPreview() {
        return mapPreview;
    }

    public static boolean isSafeMode() {
        return safeMode;
    }

    public static void setSafeMode(boolean safeMode) {
        Flags.safeMode = safeMode;
    }

    public static boolean isOutlinesFixed() {
        return true;
    }

    public static boolean isIggDemo() {
        return iggDemo;
    }

    public static void setIggDemo(boolean iggDemo) {
        Flags.iggDemo = iggDemo;
    }

    public static void setIggDemoRunning(boolean iggDemoRunning) {
        Flags.iggDemoRunning = iggDemoRunning;
    }

    public static boolean isIggDemoRunning() {
        return iggDemoRunning;
    }

    public static void setToolIsRunning(boolean toolIsRunning) {
        Flags.toolIsRunning = toolIsRunning;
    }

    public static boolean isActiveTestMode() {
        return activeTestMode;
    }

    public static void setActiveTestMode(boolean activeTestMode) {
        Flags.activeTestMode = activeTestMode;
    }

    public static boolean isLiteLaunch() {
        if (CoreEngine.TEST_LAUNCH) {
            return true;
        }
        if (isSuperLite())
            return true;
        return liteLaunch;
    }

    public static void setLiteLaunch(boolean liteLaunch) {
        Flags.liteLaunch = liteLaunch;
    }

    public static boolean isDialogueTest() {
        return dialogueTest;
    }

    public static void setDialogueTest(boolean dialogueTest) {
        Flags.dialogueTest = dialogueTest;
    }

    public static boolean isSkillTestMode() {
        return skillTestMode;
    }

    public static void setSkillTestMode(boolean skillTestMode) {
        Flags.skillTestMode = skillTestMode;
    }

    public static boolean isContentTestMode() {
        return contentTestMode;
    }

    public static void setContentTestMode(boolean contentTestMode) {
        Flags.contentTestMode = contentTestMode;
    }

    public static boolean isRuleTestMode() {
        return ruleTestMode;
    }

    public static void setRuleTestMode(boolean ruleTestMode) {
        Flags.ruleTestMode = ruleTestMode;
    }

    public static boolean isLevelTestMode() {
        return levelTestMode;
    }

    public static void setLevelTestMode(boolean levelTestMode) {
        Flags.levelTestMode = levelTestMode;
    }

    public static boolean isDevEnabled() {
        return devEnabled || isIDE();
    }

    public static void setDevEnabled(boolean devEnabled) {
        Flags.devEnabled = devEnabled;
    }

    public static boolean isReverseExit() {
        return reverseExit;
    }

    public static void setReverseExit(boolean reverseExit) {
        Flags.reverseExit = reverseExit;
    }

    public static boolean isRamEconomy() {
        return ramEconomy;
    }

    public static void setRamEconomy(boolean ramEconomy) {
        Flags.ramEconomy = ramEconomy;
    }

    public static boolean isKeyCheat() {
        return keyCheat;
    }

    public static void setKeyCheat(boolean keyCheat) {
        Flags.keyCheat = keyCheat;
    }

    public static void setMainGame(boolean mainGame) {
        Flags.mainGame = mainGame;
    }

    public static boolean isMainGame() {
        return mainGame;
    }

    public static boolean isAutoFixOn() {
        return autoFixOn;
    }

    public static void setAutoFixOn(boolean autoFixOn) {
        Flags.autoFixOn = autoFixOn;
    }

    public static boolean isDungeonTool() {
        return dungeonTool;
    }

    public static void setDungeonTool(boolean dungeonTool) {
        Flags.dungeonTool = dungeonTool;
    }

    public static boolean isTesterVersion() {
        return testerVersion;
    }

    public static void setTesterVersion(boolean testerVersion) {
        Flags.testerVersion = testerVersion;
    }

    public static boolean isSuperLite() {
        return superLite;
    }

    public static void setSuperLite(boolean superLite) {
        Flags.superLite = superLite;
    }

    public static boolean isVfxOff() {
        return vfxOff ;
    }

    public static void setVfxOff(boolean vfxOff) {
        Flags.vfxOff = vfxOff;
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

    public static boolean isFullVersion() {
        return fullVersion;
    }

    public static void setFullVersion(boolean fullVersion) {
        Flags.fullVersion = fullVersion;
    }
}
