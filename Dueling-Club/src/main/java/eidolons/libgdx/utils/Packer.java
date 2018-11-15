package eidolons.libgdx.utils;

import eidolons.game.core.Eidolons;
import main.data.filesys.PathFinder;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by JustMe on 11/12/2018.
 * <p>
 * copy from dirs with filters?
 * <p>
 * IDEA:
 * why not work in a clean space? and keep the junk elsewhere?!
 * of course some filtering will be required...
 */
public class Packer {
    public static final String[] filteredAll = {
     ".psd",
     "desktop.ini",
     " copy",
     "(1)",
     "pregenerated",
    };
    public static final String[] imageFiltered = {
     "generator",
     "workshop",
    };

    public static final String[] xmlFolders = {
     "duel-club\\types",
     "dungeons\\levels\\generated"
    };
    public static final String[] vfxFolders = {
     "atlas",
     "atlases"
    };
    public static final String[] imageFolders = {
     "main",
     "desktop.ini",
     "sprites",
     "vfx",
    };
    private String outputRoot;

    public Packer(String outputRoot) {
        this.outputRoot = outputRoot;
    }

    public static void main(String[] args) {
        new Packer("Y:\\[Eidolons demos]\\upload\\" +
         ContainerUtils.join("-", Eidolons.NAME + Eidolons.EXTENSION + Eidolons.SUFFIX +
          CoreEngine.filesVersion)).pack();
    }

    public void pack() {
        copyXml();
        copyText();
        copyOther();
        copySounds();
        copyMusic();
        copyImages();
        copyVfx();
        copyJar();

    }

    private void copyVfx() {
    }

    private void copyMusic() {
    }

    private void copyOther() {
        String path = PathFinder.getFontPath();
    }

    private void copyJar() {
        String jarName = "Eidolons " + CoreEngine.VERSION;
    }

    private void copySounds() {
    }

    private void copyText() {
    }

    private void copyImages() {
    }

    private void copyXml() {
        String root = PathFinder.getXML_PATH();
        String dest = "xml/";
        copyFromRoot(root, dest, xmlFolders, filteredAll, "");
    }

    private void copyFromRoot(String root, String dest, String[] imageFolders,
                                String[] filteredAll, String... customExceptions) {


        for (File file : FileManager.getFilesFromDirectory(PathFinder.getXML_PATH(), false)) {

            String suffix = FileManager.formatPath(file.getPath(), true).replace(dest, "");
            File output = new File(StrPathBuilder.build(outputRoot + dest + suffix));
            Path src = Paths.get(file.toURI());
            Path target = Paths.get(output.toURI());
            try {
                Files.copy(src, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }


    }


}
