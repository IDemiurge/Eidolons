package eidolons.libgdx.assets.utils;

import eidolons.libgdx.launch.GpuTester;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.secondary.Bools;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class SteamPacker {

    public static final String steam_build_path = "C:/steamworks/sdk/tools/ContentBuilder/content/windows/";
    public static final String out = steam_build_path.toLowerCase();
    public static final String root = "C:/code/eidolons/Dueling-Club/target/";
    public static final String jar_path = root + "eidolons-1.0-SNAPSHOT.jar";
    public static final String jar_name_out = "Eidolons.jar";

    static SteamDataModel dataModel;
    private static final Set<String> newFiles = new LinkedHashSet<>();
    private static final Set<String> updatedFiles = new LinkedHashSet<>();
    PackingReport report;

    public static void main(String[] args) {
        boolean full = args.length > 0;
        PathFinder.init();
        // if (read){
        //     createDataModel();
        //     return;
        // }
        if (full || DialogMaster.confirm("Rebuild model?"))
            dataModel = new SteamDataReader(out).readDataModel();
        if (full || DialogMaster.confirm("Rebuild atlases?"))
            AtlasGen.main("", "", "");

        copyJar(); //what about exe gen? probably cmd compatible !
        clearLogs();
        if (full || DialogMaster.confirm("Copy all?")) {
            copyResources();
            copyXml();
        }
        main.system.auxiliary.log.LogMaster.log(1, newFiles.size() + " new files: \n" + newFiles);
        main.system.auxiliary.log.LogMaster.log(1, updatedFiles.size() + " updated Files: \n" + newFiles);
        if (full || DialogMaster.confirm("Run build?")) {
            runBuildScript();
        }
    }

    private static void runBuildScript() {

    }

    private static void clearLogs() {
        FileManager.cleanDir(out + PathFinder.getLogPath());
        FileManager.delete(out + GpuTester.FILE_NAME);
        for (File file : FileManager.getFilesFromDirectory(out, false)) {
            if (file.getPath().contains("hs_err_pid")){
                FileManager.delete(file);
            }
        }
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
        if (result==null ) {
            updatedFiles.add(path);
        }
    }
}
