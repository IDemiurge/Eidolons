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

    public final static String[] classFolderPaths = {"main.elements", "main.ability",
            "eidolons.elements", "eidolons.ability"};

    public static final UPLOAD_PACKAGE uploadPackage = UPLOAD_PACKAGE.Backer;
    public static final String VERSION = "1.0.0";
    public static final String VERSION_NAME = "Backer Demo (Basic Version)"; //StringMaster.getWellFormattedString(uploadPackage.toString());
    public static String filesVersion = "v" + VERSION.replace(".", "-");
    private static String selectivelyReadTypes;
    public static final boolean DEV_MODE = true;
    public static boolean TEST_LAUNCH;

    public static boolean swingOn = true;
    private static boolean arcaneVault;
    private static final boolean menuScope = true;
    private static boolean levelEditor;
    private static boolean graphicsOff;
    static boolean reflectionMapDisabled;

    //core Review - good idea, but how to use it?
    public enum UPLOAD_PACKAGE {
        Aphotic, igg, Backer,
    }

    public static boolean isMyLiteLaunch() {
        return Flags.isIDE() && Flags.isLiteLaunch();
    }

    public static void systemInit() {
        Chronos.mark("SYSTEM INIT");
        System.out.println("Eidolons " + VERSION);
        System.out.println("Core Engine Init... ");

        if (isDiagOn()){
        System.out.println("Heap size:  " +
                ( Runtime.getRuntime().maxMemory()));
        System.out.println("CPU's available:  " +
                ( Runtime.getRuntime().availableProcessors()));
               System.out.println("Total Memory:  " +
                (         Runtime.getRuntime().totalMemory()));
        }

        if (System.getProperty("user.home").equalsIgnoreCase("C:\\Users\\Alexa")) {
            Flags.me = true;
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

    public static   boolean isArcaneVault() {
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
                    StringMaster.cropFormat(StringMaster.cropLast(name, "-")),
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
        //         if (dialogueDataRequired){
        //             classFolders.add( "main.data.dialogue" );
        //             classFolders.add(  "main.game.battlecraft.logic.meta.scenario.dialogue.speech" );
        //         }

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
        if ( reflectionMapDisabled)
            return false;
        return !isLevelEditor();
    }

    public static void setEngineObject(CoreEngine engineObject) {
    }


    public static void setWeakGpu(boolean weakGpu) {
        // CoreEngine.weakGpu = weakGpu;
        ////TODO use it in assets?
        // LogMaster.important("Setting Weak GPU to " + weakGpu);
    }
}
