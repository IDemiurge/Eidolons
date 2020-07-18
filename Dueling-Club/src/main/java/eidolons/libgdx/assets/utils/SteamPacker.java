package eidolons.libgdx.assets.utils;

import eidolons.libgdx.launch.GpuTester;
import eidolons.libgdx.texture.TexturePackerLaunch;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.data.filesys.PathFinder;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.CoreEngine;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class SteamPacker {

    public static final String steam_build_path = "C:/steamworks/sdk/tools/ContentBuilder/content/windows/";
    public static final String out = steam_build_path.toLowerCase();
    public static final String root = "C:/code/eidolons/Dueling-Club/target/";
    public static final String jar_path = root + "eidolons-0.5.jar";
    public static final String jar_name_out = "Eidolons.jar";

    static SteamDataModel dataModel;
    private static final Set<String> newFiles = new LinkedHashSet<>();
    private static final Set<String> updatedFiles = new LinkedHashSet<>();
    public static BuildReport report;
    private static String buildID;

    public static void main(String[] args) {
        boolean full = args.length > 0;
        boolean fast = args.length < 2;
        PathFinder.init();
        if (args.length > 3) {
            full = DialogMaster.confirm("Full build?");
            fast = DialogMaster.confirm("Fast?");
        }
        // if (read){
        //     createDataModel();
        //     return;
        // }
        // if (full || DialogMaster.confirm("Rebuild model?"))
        buildID = readID();
        dataModel = new SteamDataReader(out).readDataModel();
        report = new BuildReport();
        if (full || DialogMaster.confirm("Rebuild assets?")) {
            if (full || DialogMaster.confirm("Pack atlases?")) {
                AtlasGen.OVERWRITE = !fast;
                TexturePackerLaunch.FAST = fast;
                AtlasGen.main("", "", "");
            } else {
                AtlasGen.main();
            }
        }

        copyJar(); //what about exe gen? probably cmd compatible !

        if (full || DialogMaster.confirm("Copy all?")) {
            copyResources();
            copyXml();
        }
        try {
            clearLogs();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (newFiles.isEmpty() && updatedFiles.isEmpty()) {
        }

        log(newFiles.size() + " new files: \n" + newFiles);
        log(updatedFiles.size() + " updated Files: \n" + newFiles);
        if (full || DialogMaster.confirm("Run build?")) {
            runBuildScript();
        }
        incrementID();
        report.setBuildId(buildID);
        report.write();
    }

    private static String readID() {
        String s = FileManager.readFile(PathFinder.getBuildsIdPath() );
        if (s.isEmpty()) {
            return "0";
        }
        return s;
    }

    private static void incrementID() {
        buildID = (NumberUtils.getInt(buildID) + 1) + "";
        FileManager.write(buildID, PathFinder.getBuildsIdPath()  );
    }

    private static void log(String s) {
        main.system.auxiliary.log.LogMaster.log(1, " " + s);
        report.append(s);
    }

    private static void runBuildScript() {
        //         ProcessBuilder
    }

    private static void clearLogs() {
        FileManager.cleanDir(out + PathFinder.getLogPath());
        FileManager.delete(out + GpuTester.FILE_NAME);
        for (File file : FileManager.getFilesFromDirectory(out, false)) {
            if (file.getPath().contains("hs_err_pid")) {
                FileManager.delete(file);
            }
        }
    }

    public static String getVersionName() {
        return CoreEngine.VERSION_NAME + " " + CoreEngine.VERSION + buildID;
    }

    private static void copyJar() {
        FileManager.copy(jar_path, out + jar_name_out);
    }

    private static void copyXml() {
        for (File xmlFile : dataModel.filesXml) {
            copy(cropPath(xmlFile.getPath(), "xml"));
        }
    }

    private static void copyResources() {
        for (File xmlFile : dataModel.filesRes) {
            copy(cropPath(xmlFile.getPath(), "resources"));
        }
    }

    private static String cropPath(String path, String suffix) {
        return FileManager.formatPath(path, true, true).replace(out, "");
    }


    private static void copy(String path) {
        Boolean result = FileManager.copy(root + path, out + path);
        if (Bools.isTrue(result)) {
            newFiles.add(path);
        }
        if (result == null) {
            updatedFiles.add(path);
        }
    }

}
