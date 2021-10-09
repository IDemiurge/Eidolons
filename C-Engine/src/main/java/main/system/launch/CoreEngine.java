package main.system.launch;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.ability.ARGS;
import main.data.ability.Mapper;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.system.ExceptionMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.FileLogManager;
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

import static main.system.auxiliary.log.LogMaster.important;

public class CoreEngine {

    public final static String[] classFolderPaths = {"main.elements", "main.ability",
            "eidolons.elements", "eidolons.ability"};

    public static final UPLOAD_PACKAGE uploadPackage = UPLOAD_PACKAGE.Backer;
    public static final String VERSION_NAME = "Demo Version"; //StringMaster.getWellFormattedString(uploadPackage.toString());
    public static final boolean RAM_OPTIMIZATION = true;
    public static int resBuildId = readBuildId();
    public static int xmlBuildId = readXmlBuildId();
    public static int prevXmlBuildId = readXmlBuildId();
    public static String BUILD = NumberUtils.prependZeroes(resBuildId, 3);
    public static String XML_BUILD = NumberUtils.prependZeroes(xmlBuildId, 4);
    public static String PREV_XML_BUILD = NumberUtils.prependZeroes(prevXmlBuildId, 4);
    public static String CORE_VERSION = "0.5";
    public static String VERSION = CORE_VERSION + "." + BUILD + "."+XML_BUILD;
    public static String filesVersion = VERSION.replace(".", "-");

    private static void version() {
        resBuildId = readBuildId();
        xmlBuildId = readXmlBuildId();
        XML_BUILD = NumberUtils.prependZeroes(xmlBuildId, 4);
        prevXmlBuildId = readXmlBuildId();
        PREV_XML_BUILD = NumberUtils.prependZeroes(prevXmlBuildId, 4);

        BUILD = NumberUtils.prependZeroes(resBuildId, 3);
        CORE_VERSION = "0.5";
        VERSION = CORE_VERSION + "." + BUILD+ "."+ XML_BUILD;
        filesVersion = VERSION.replace(".", "-");
    }

    public static boolean FULL_LAUNCH; ////TODO with audio and all - real xp!
    private static String selectivelyReadTypes;
    public static boolean TEST_LAUNCH;
    public static boolean SELECT_LEVEL = false;

    public static boolean swingOn = true;
    private static boolean arcaneVault;
    private static final boolean menuScope = true;
    private static boolean levelEditor;
    private static boolean graphicsOff;
    static boolean reflectionMapDisabled;
    private static boolean weakGpu;
    private static boolean weakCpu;

    public static boolean isFullLaunch() {
        return FULL_LAUNCH || Flags.isJar();
    }

    public static void incrementResBuild() {
        resBuildId++;
        FileManager.write(resBuildId + "", PathFinder.getBuildsIdPath());
        version();
    }
    public static void incrementXmlBuild() {
        xmlBuildId++;
        FileManager.write(xmlBuildId + "", PathFinder.getXmlBuildsIdPath());
        version();
    }

    //core Review - good idea, but how to use it?
    public enum UPLOAD_PACKAGE {
        Backer, Tester,
    }

    private static Integer readBuildId() {
        return NumberUtils.getInt(FileManager.readFile(PathFinder.getBuildsIdPath()));
    }

    private static Integer readXmlBuildId() {
        return NumberUtils.getInt(FileManager.readFile(PathFinder.getXmlBuildsIdPath()));
    }
    private static Integer readPrevXmlBuildId() {
        return NumberUtils.getInt(FileManager.readFile(PathFinder.getXmlBuildsIdPath()));
    }

    public static boolean isMyLiteLaunch() {
        return Flags.isIDE() && Flags.isLiteLaunch();
    }

    public static void systemInit() {
        Chronos.mark("SYSTEM INIT");
        System.out.println("Core Engine Init... ");
        important("---- Eidolons " + VERSION);

        if (isDiagOn()) {
            important("Heap size:  " +
                    (Runtime.getRuntime().maxMemory()));
            important("CPU's available:  " +
                    (Runtime.getRuntime().availableProcessors()));
            important("Total Memory:  " +
                    (Runtime.getRuntime().totalMemory()));
        }

        Flags.setMe(PathFinder.getRootPath().contains("C:/code/Eidolons/"));
        // if (System.getProperty("user.home").equalsIgnoreCase("C:\\Users\\Alexa")) {
        //     Flags.me = true;
        // }

        ImageManager.init();
        if (!graphicsOff) {
            if (isSwingOn())
                FontMaster.init();
            GuiManager.init();
        }

        SoundMaster.initialize();
        DataManager.init();

        Chronos.logTimeElapsedForMark("SYSTEM INIT");
        important("...Core Engine Init finished");
        if (!Flags.me) {
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

    public static boolean isArcaneVault() {
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

    private static boolean isDiagOn() {
        return false;
    }

    public static boolean checkReadNecessary(String name) {

        if (selectivelyReadTypes != null) {
            return ContainerUtils.checkContainer(
                    selectivelyReadTypes,
                    StringMaster.cropFormat(StringMaster.cropAfter(name, "-")),
                    false);
        }

        // if (exceptionTypes != null) {
        //     if (ContainerUtils.checkContainer(
        //             exceptionTypes,
        //             StringMaster.cropFormat(StringMaster.cropLast(name, "-")),
        //             false
        //     )) {
        //         return false;
        //     }
        // }

        DC_TYPE TYPE = new EnumMaster<DC_TYPE>().retrieveEnumConst(DC_TYPE.class, name);

        boolean TEST_MODE = true;
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

    public static boolean isMenuScope() {
        return menuScope;
    }

    public static void setSelectivelyReadTypes(String types) {
        selectivelyReadTypes = types;
    }

    public static String getSelectivelyReadTypes() {
        return selectivelyReadTypes;
    }

    public static boolean isEnumCachingOn() {
        boolean enumCachingOn = true;
        return enumCachingOn;
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

    public static void dataInit(boolean macro) {
        Chronos.mark("TYPES INIT");

        XML_Reader.readTypes(macro);
        WaitMaster.receiveInput(WAIT_OPERATIONS.XML_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.XML_READY);

        Chronos.logTimeElapsedForMark("TYPES INIT");

        if (isCompileReflectionMap())
            compileReflectionMap();

    }

    public static void compileReflectionMap() {
        if (Mapper.isInitialized()) {
            return;
        }
        List<String> classFolders = new ArrayList<>(Arrays.asList(classFolderPaths));
        try {
            Chronos.mark("MAPPER INIT");
            Mapper.compileArgMap(Arrays.asList(ARGS.getArgs()),
                    classFolders);
            Chronos.logTimeElapsedForMark("MAPPER INIT");
        } catch (ClassNotFoundException | SecurityException | IOException e) {
            ExceptionMaster.printStackTrace(e);
        }
    }

    public static void setReflectionMapDisabled(boolean reflectionMapDisabled) {
        CoreEngine.reflectionMapDisabled = reflectionMapDisabled;
    }

    private static boolean isCompileReflectionMap() {
        if (reflectionMapDisabled)
            return false;
        return !isLevelEditor();
    }

    public static void setEngineObject(CoreEngine engineObject) {
    }

    public static void setWeakCpu(boolean weakCpu) {
        CoreEngine.weakCpu = weakCpu;
    }

    public static boolean isWeakCpu() {
        return weakCpu;
    }

    public static boolean isWeakGpu() {
        return weakGpu;
    }

    public static void setWeakGpu(boolean weakGpu) {
        CoreEngine.weakGpu = weakGpu;
        ////TODO use it in assets?
        important("Setting Weak GPU to " + weakGpu);
    }
}
