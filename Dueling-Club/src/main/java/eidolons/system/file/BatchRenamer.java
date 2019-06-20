package eidolons.system.file;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class BatchRenamer {
    private static String ANIM_ROOT;

    private static void moveFilesToPartFolders(String folder, int maxFilesPerFolder) {
        int i = 0;
        int suffix = 1;
//        C:\drive\[2019]\Team\AE\tests\netherflame\misc\icons\skill\Symbols 128 square
        for (File file : FileManager.getFilesFromDirectory(folder, false)) {

            String folderName = "part_" + (suffix + 1);
            String pathname = folder + "\\" + folderName + "\\" +
                    new File(file.getName());
            File newFile = new File(
                    pathname);
            new File(folder + "\\" + folderName + "\\").mkdirs();
            if (!file.renameTo(newFile)) {
                continue;
            }
            if (i++ >= maxFilesPerFolder) {
                suffix++;
                i = 0;
//                appendFrameNumbersToFiles(folder + "\\" + folderName+ "\\");
            }
        }
    }

    private static void appendFrameNumbersToFiles(String folder) {
        int i = 0;
        for (File file : FileManager.getFilesFromDirectory(folder, false)) {
            String frame = "_" + NumberUtils.getFormattedTimeString(i++, 5);
            String pathname = folder + "\\" +
                    new File(folder).getName() + frame + StringMaster.getFormat(file.getName());
            if (!file.renameTo(new File(
                    pathname))) {
                continue;
            }

        }
    }

    public static void main(String[] a) {
        ANIM_ROOT = "C:\\drive\\[2019]\\Team\\AE\\" +
                "tests\\netherflame\\misc\\weapons\\Battle Spear\\";
        for (File folder : FileManager.getFilesFromDirectory(ANIM_ROOT+ "to rename", true, false)) {
            if (folder.isDirectory()) {
                try {
                    renameAnimsInFolder(folder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (DialogMaster.confirm("Move into subfolders?")) {
            moveFilesToPartFolders(DialogMaster.inputText("Root?"), 5000);
            return;
        }
        if (DialogMaster.confirm("Append Frames?")) {
            appendFrameNumbersToFiles(DialogMaster.inputText("Root?"));
            return;
        }
        processAE_Dir();

        if (!DialogMaster.confirm("Anims?")) {
            return;
        }
        CoreEngine.systemInit();
        DC_Engine.dataInit();
        String root = DialogMaster.inputText("Root?");
        String type = ListChooser.chooseType(DC_TYPE.WEAPONS);
        generateAnimFolders(root, DataManager.getType(type, DC_TYPE.WEAPONS));
/**
 *
 */
        DialogMaster.confirm("Manual folders?");


    }

    private static void processAE_Dir() {
        String root = DialogMaster.inputText("Root?");
        String rawFolder = root + "/raw";
        String outputFolder = root;
        processAE_Dir(rawFolder, outputFolder); //String... frameNames

    }

    private static void processAE_Dir(String rawFolder, String outputFolder) {
        int i = 0;
        List<File> rawFiles = FileManager.getFilesFromDirectory(rawFolder, false);
        List<File> processFiles = FileManager.getFilesFromDirectory(outputFolder, false);

        for (File file : processFiles) {
            File newFile = new File(outputFolder + "/renamed/" + rawFiles.get(i++).getName());
            if (!file.renameTo(newFile)) {
                break;
            }
            continue;
        }
    }

    //could use same AE rename!
    private static void generateAnimFolders(String root, ObjType weaponType) {
        for (String s : ContainerUtils.openContainer(weaponType.getProperty(PROPS.WEAPON_ATTACKS))) {
            for (AnimMaster3d.PROJECTION projection : AnimMaster3d.PROJECTION.values()) {
                FileManager.getFile(root + "/" + s + "/" + projection).mkdirs();
            }
        }
    }

    private static void renameAnimsInFolder(File folder, String... animNames) {
        int frameNumber;

        // we can at least do same Fps for projections


    }

    private static void renameAnimsInFolder(File folder) throws IOException {
        for (File proj : FileManager.getFilesFromDirectory(folder.getPath(), true))
        {
            int i=0;
            if (proj.isDirectory())
                for (File file : FileManager.getFilesFromDirectory(proj.getPath(), false)) {

                    String name = ANIM_ROOT
                            + "/renamed/";
                    String weapon = PathUtils.getLastPathSegment(ANIM_ROOT);
                    String action = folder.getName();
                    new File(name).mkdirs();

                    name += ContainerUtils.construct("_", weapon, action, proj.getName());

                    name += NumberUtils.getFormattedTimeString(i++, 4)+ ".png";
                    File newFile = new File(name);

                    if (!file.renameTo(newFile)) {
                        break;
                    }
                    continue;
//            Files.setAttribute()

                }
        }
    }
}