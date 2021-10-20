package libgdx.utils;

import eidolons.game.core.Core;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.SortMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 11/12/2018.
 * <p>
 * copy from dirs with filters?
 * <p>
 * IDEA: why not work in a clean space? and keep the junk elsewhere?! of course some filtering will be required...
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
            "sprites",
            "vfx",
    };
    public static final String CORE_UPLOAD = "Y:\\[Eidolons demos]\\upload\\";
    public static final String CORE_TEST = "Y:\\[Eidolons demos]\\packer test\\";
    private static final String RES_TREE_OUTPUT = "output res tree.txt";
    private static final String RES_TREE = "res tree.txt";
    private static final String RES_TREE_FULL = "res tree full.txt";
    boolean test;
    int maxTreeDepth = 5;
    private final String outputRoot;
    private StrPathBuilder pathBuilder;
    private StrPathBuilder lastBuilder;
    private boolean delayed;
    private List<File> toCopy;
    // private List<File> failedFiles;

    public Packer(String outputRoot, boolean test) {
        if (test) {
            outputRoot = CORE_TEST + outputRoot;
        } else {
            outputRoot = CORE_UPLOAD + outputRoot;
        }
        this.outputRoot = outputRoot;
        this.test = test;
    }

    public static void main(String[] args) {
        new Packer(
                ContainerUtils.join("-", Core.NAME, Core.EXTENSION, Core.SUFFIX,
                        CoreEngine.filesVersion) + "/", true).pack();
    }

    public String createDirectoryTree() {
        int treeDepth = 0;
        StringBuilder data = new StringBuilder();
        appendToTree(treeDepth, data, (PathFinder.getResPath()));

        return data.toString();

    }

    private void appendToTree(int treeDepth, StringBuilder data, String path) {
        if (treeDepth > maxTreeDepth)
            return;
        List<File> files = FileManager.getFilesFromDirectory(path, true).stream().filter(
                File::isDirectory).sorted(new SortMaster<File>().getSorterByExpression_(
                file -> -(FileManager.getFilesFromDirectory(file.getPath(), true, false).size())))
                .collect(Collectors.toList());

        for (File dir : files) {
            //            if (full.contains(dir.getName().toLowerCase()))
            //                continue;
            //            if (exceptions.contains(dir.getName().toLowerCase()))
            //                continue;
            if (dir.isDirectory()) {
                if (treeDepth < 1)
                    data.append("\n").append(StringMaster.getStringXTimes(10, "__")).append("\n");
                data.append(StringMaster.getStringXTimes(treeDepth, "--")).append(">  ").append(dir.getName()).append("\n");
                if (treeDepth < 2)
                    data.append("\n");
                appendToTree(treeDepth + 1, data, dir.getPath());
            }

        }
    }

    public void pack() {
        toCopy = new ArrayList<>();
        // failedFiles = new ArrayList<>();

        boolean updateTree = false;
        if (updateTree)
            FileManager.write(createDirectoryTree(), PathFinder.getResPath() + "res tree.txt");
        boolean readTree = true;
        if (readTree)
            copyResourceTree();

        if (test) {
            return;
        }
        copyXml();
        copyText();
        copyOther();
        copySounds();
        copyMusic();
        copyImages();
        copyVfx();
        copyJar();

    }

    private void copyResourceTree() {
        delayed = true;
        String resTree = FileManager.readFile(PathFinder.getResPath() + RES_TREE_OUTPUT);
        resTree = formatResTree(resTree);
        String[] lines = StringMaster.splitLines(resTree);
        int result = 0;
        pathBuilder = new StrPathBuilder(PathFinder.getResPath());
        while (result >= 0) {
            result = crawlTreeLines(result, lines);
        }
    }
/*
__________________
>  Fonts
-->  hiero

---->  high
---->  perpetua
____________________
  img
-->  gen

---->  entity
------>  abils
-------->  Actives
-------->  Passives
------>  items
-------->  Keys
 */

    /*
    so the idea is to keep a kind of 'pom' of the file system
    but will it be maintainable?
    No, but I don't want some 'random new folders and files' to automatically sneak where I didn't specify
    recursion by hand!

     */
    private int crawlTreeLines(int index, String[] lines) {
        //runs until the tree goes up

        List<File> filesToCopy = new ArrayList<>();
        int depth = 0;
        for (int i = index; i < lines.length; i++) {
            String line = lines[i];
            boolean recursive = false;
            if (isRecursive(line)) {
                recursive = true; //do not stop until depth is again N
                line = line.replace("*", "");
            }
            boolean subfoldersOnly = false;
            if (isSubfoldersOnly(line)) {
                subfoldersOnly = true; //do not copy files, just subfolders
                line = line.replace("%", "");
            }

            String name = getNameFromLine(line);
            pathBuilder.append(name);
            //what if it's a file itself?!
            List<File> files = FileManager.getFilesFromDirectory(pathBuilder.toString(), false, recursive);
            if (!subfoldersOnly)
                filesToCopy.addAll(files);
            int newDepth = getDepth(line);
            if (newDepth > 0 || depth > 0)
                if (newDepth <= depth) {
                    //going up again
                    copyFiles(filesToCopy);
                    int goBack = depth - newDepth + 2;
                    String path = pathBuilder.toString();
                    while (goBack-- > 0) {
                        path = PathUtils.cropLastPathSegment(path);
                    }
                    pathBuilder = new StrPathBuilder(path);
                    return i; //resume crawl from this index
                }
            depth = newDepth; //we went down
        }
        delayed = false;
        copyFiles(filesToCopy);
        return -1; //crawl is finished
    }

    private boolean isSubfoldersOnly(String line) {
        return line.contains("%");
    }

    private boolean isRecursive(String line) {
        return line.contains("*");
    }

    private void copyFiles(List<File> filesToCopy) {
        if (isDelayed()) {
            toCopy.addAll(filesToCopy);
            return;
        }
        if (!toCopy.isEmpty()) {
            toCopy.addAll(filesToCopy);
            filesToCopy = toCopy;
            toCopy = new ArrayList<>();
        }
        //no dirs
        String src = PathFinder.getResPath();
        String dest = "resources/";
        copyFromRoot(false, src, dest,outputRoot, filesToCopy.toArray(new File[0]), null);
    }

    private int getDepth(String line) {
        String[] parts = line.split(">");
        if (parts.length == 0)
            return 0;
        return parts[0].length() / 2;
    }

    private String getNameFromLine(String line) {
        String[] parts = line.split("  ");
        return parts[1];
    }

    private void copyLines(String root, List<String> subFolders) {
        for (String subFolder : subFolders) {
            //            from = subFolder.replace(root, "");
            //            to =
            //            copyFromRoot(false, );
        }
    }

    private String formatResTree(String resTree) {
        return resTree.replace("__", "").trim();
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
        String root = PathFinder.getImagePath();
        String dest = "resources/img/";
        copyFromRoot(true, root, dest, imageFolders, filteredAll, imageFiltered);
    }

    private void copyXml() {
        String root = PathFinder.getXML_PATH();
        String dest = "xml/";
        copyFromRoot(false, root, dest, xmlFolders, filteredAll);
    }

    private void copyFromRoot(boolean recursive, String root, String dest, String[] folders,
                              String[] filteredAll, String... customExceptions) {
        copyFromRoot(recursive, root, outputRoot, dest,
                Arrays.stream(folders).map(name -> FileManager.getFile(root + "/" + name))
                        .collect(Collectors.toList()).toArray(new File[folders.length])
                , filteredAll, customExceptions);
    }

    public static List<File> copyFromRoot(boolean recursive, String root, String outputRoot, String dest, File[] folders,
                                          String[] filteredAll, String... customExceptions) {
        Path rootPath = null;
        String symbolicLinkRoot = "";
        List<File> failedFiles = new LinkedList<>();
        try {
            rootPath = Paths.get(new File(root).toURI());
            if (Files.isSymbolicLink(rootPath)) {
                rootPath = Files.readSymbolicLink(rootPath);
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (rootPath != null) {
            symbolicLinkRoot = FileManager.formatPath(root, true);
            root = rootPath.toString();
        }
        root = FileManager.formatPath(root, true);

        List<String> exceptions = new ArrayList<>();

        if (filteredAll != null)
            exceptions.addAll(Arrays.asList(filteredAll));
        exceptions.addAll(Arrays.asList(customExceptions));
        for (File folder : folders) {
            List<File> fileList = new ArrayList<>();
            if (folder.isFile()) {
                fileList.add(folder);
            } else {
                FileManager.getFilesFromDirectory(folder, false, recursive);
            }
            files:
            for (File file : fileList) {
                for (String exception : exceptions) {
                    if (StringMaster.contains(file.getPath(), exception, true, true)) {
                        log("Ignoring " + file);
                        continue files;
                    }
                }

                String suffix = FileManager.formatPath(file.getPath(), true)
                        .replace(symbolicLinkRoot, "")
                        .replace(root, "");
                File output = new File(StrPathBuilder.build(outputRoot + dest + suffix));
                output.mkdirs();
                try {
                    Path src = Paths.get(file.toURI());
                    Path target = Paths.get(output.toURI());

                    Files.copy(src, target, StandardCopyOption.REPLACE_EXISTING);
                    log(1, "Copied to " + output);
                } catch (InvalidPathException e1) {
                    e1.printStackTrace();
                    log(1, "Paths failed " + file + "\n" + output);
                    failedFiles.add(file);
                } catch (Exception e) {
                    log(1, "Copy failed from " + file);
                    failedFiles.add(file);
                }
            }

        }
        log(1, "failed files: " + failedFiles);
        return failedFiles;
    }


    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }
}
