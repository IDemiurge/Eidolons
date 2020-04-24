package main.system.auxiliary.data;

import main.data.filesys.PathFinder;
import main.data.xml.XmlNodeMaster;
import main.system.PathUtils;
import main.system.auxiliary.*;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Node;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileManager {
    private static List<String> missing = new ArrayList<>();

    public static String readFile(String filePath) {
        File file = getFile(filePath, true, false);
        return readFile(file);
    }

    public static List<String> readFileLines(String filePath) {
        File file = getFile(filePath);
        return readFileLines(file);
    }

    public static List<String> readFileLines(File file) {
        String string = readFile(file, "\n");
        return ContainerUtils.openContainer(string, "\n");
    }

    public static String readFile(File file) {
        return readFile(file, "");
    }

    public static String readFile(File file, String lineSeparator) {
        if (!isFile(file)) {
            if (CoreEngine.isJar()) {
                if (!CoreEngine.isWindows()) {
                    String lowerCase = file.getPath().toLowerCase();
                    if (!lowerCase.equals(file.getPath()))
                        return readFile(FileManager.getFile(file.getPath().toLowerCase()));

                }
                main.system.auxiliary.log.LogMaster.verbose("Failed to read " + file.getPath());
//              TODO wtf  try {
//                    throw new RuntimeException();
//                } catch (Exception e) {
//                    main.system.ExceptionMaster.printStackTrace(e);
//                }
                return "";
            }
            if (!PathUtils.fixSlashes(file.getPath()).toLowerCase().contains(PathFinder.getRootPath().toLowerCase()))
                return readFile(FileManager.getFile(PathFinder.getRootPath() + file.getPath()),
                        lineSeparator);
            return "";
        }

        String result = "";

        //        Charset charset= Charset.availableCharsets().get("Windows-1251");
        //        if (charset==null )
        //            charset= Charset.defaultCharset();
        try {
            result = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        return result;

        /*
        FileReader fr = null;
        String s = "";
        try {
            if (StringMaster.getFormat(file.getName()).equalsIgnoreCase(".Odt")) {
                return readOdtFile(file);
            }

            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String s1 = "";

            while (s1 != null) {
                s = s + lineSeparator + s1;
                s1 = br.readLine();

                // Consumer<? super String> action = new Consumer<String>() {
                // @Override
                // public void accept(String t) {
                // s +=t;
                //
                // }
                // @Override
                // public Consumer<String> andThen(Consumer<? super String>
                // after) {
                // // TODO Auto-generated method stub
                // return null;
                // }
                // };
                // br.lines().parallel().forEachOrdered(action);

            }

        } catch (IOException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            try {
                if (fr != null)
                    fr.close();
            } catch (IOException e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

        return s;*/
    }

    // private OutputStream out;
    // out = new FileOutputStream("/home/arthur/unzipped/" + outFilename);
    //
    //
    // // Transfer bytes from the ZIP file to the output file
    // byte[] buf = new byte[1024];
    // int len;
    // while ((len = zipInputStream.read(buf)) > 0)
    // {
    // out.write(buf, 0, len);
    // }
    // TextDocument document = TextDocument.loadDocument("test.odt");
    // String texts = document.getContentDom().getDocument().;
    // Get the first entry
    private static String readOdtFile(File file) throws FileNotFoundException {
        ZipInputStream in = new ZipInputStream(new FileInputStream(file));

        while (true) {
            try {
                ZipEntry entry = in.getNextEntry();
                if (entry == null) {
                    break;
                }
                if (entry.getName().equals("content.xml")) {
                    StringBuilder xmlString = new StringBuilder();
                    while (true) {

                        byte[] array = new byte[3000000]; // big enough for
                        // sure, or is this
                        // wrong?
                        int n = in.read(array);
                        if (n <= 0) {
                            break;
                        }
                        new Inflater().inflate(array);
                        String string = new String(array);
                        xmlString.append(string, 0, n);
                    }

                    Node node = XmlNodeMaster.findAndBuildNode(xmlString.toString(), "office:text");
                    StringBuilder string = new StringBuilder();
                    for (Node child : XmlNodeMaster.getNodeList(node)) {
                        string.append(child.getTextContent());
                    }
                    return string.toString();
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                break;
            }

        }
        return null;
    }

    public static boolean isFile(String file) {
        return isFile(FileManager.getFile(file));
    }

    public static boolean isFile(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            return false;
        }
        return file.isFile();
    }

    public static boolean isDirectory(String file) {
        return isDirectory(FileManager.getFile(file));
    }

    public static boolean isDirectory(File file) {
        if (file == null) {
            return false;
        }
        return file.isDirectory();
    }

    public static File getFile(String path) {
        return getFile(path, true);
    }

    public static File getFile(String path, boolean first) {
        return getFile(path, first, true);
    }

    public static File getFile(String path, boolean first, boolean allowInvalid) {
        File file = new File(path);
        if (file.isFile() || file.isDirectory()) {
            return file;
        }
        if (first) {
            path = formatPath(path);
            file = getFile((path), false);
            if (file.isFile() || file.isDirectory()) {
                return file;
            }
//            if (!allowInvalid)
//            file = getFile(PathFinder.getRootPath() + path, false);
//            if (file.isFile() || file.isDirectory()) {
//                return file;
//            }
//            file = new File(path);
            if (!CoreEngine.isActiveTestMode())
                if (!CoreEngine.isFullFastMode()) {
                    if (!missing.contains(file.getPath())) {
                        main.system.auxiliary.log.LogMaster.verbose("FILE NOT FOUND: " + file);
                        missing.add(file.getPath());
                    }
                }
        }

        return file;
    }

    public static String formatPath(String path) {
        return formatPath(path, false);
    }

    public static String formatPath(String path, boolean force, boolean removeLastSlash) {
        String v = formatPath(path, force);
        if (removeLastSlash)
            return v.substring(0, v.length() - 1);
        return v;
    }


    public static String formatPath(String path, boolean force) {
        StringBuilder formatted = new StringBuilder();
        int index = path.lastIndexOf(PathFinder.getRootPath(), PathFinder.getRootPath().length() - 1);
        if (index == -1 && !force) {
            return path.toLowerCase();
        }
        if (index == 0 && !force) {
            index += PathFinder.getRootPath().length() - 1;
        }
        String afterClass = force ? path : path.substring(
                index);

        //fix slashes
        if (!afterClass.isEmpty()) {
            for (String sub : PathUtils.splitPath(afterClass)) {
                formatted.append(StringMaster.replace(true, sub, "/", "")).append("/");
            }
        }
        if (force) {
            return formatted.toString().toLowerCase();
        }
        if (!CoreEngine.isWindows()) {

            return (PathFinder.getRootPath() + formatted.toString().toLowerCase())
                    .replace("%20", " ");
        }
        //fix case
        return PathFinder.getRootPath() + formatted.toString().toLowerCase();
    }

    public static String getRandomFilePathVariant(String corePath, String format) {
        return getRandomFilePathVariant(corePath, format, false);
    }

    public static String getRandomFilePathVariant(String corePath,
                                                  String format, boolean underslash) {
        return getRandomFilePathVariant(corePath, format, underslash, false);
    }

    public static String getRandomFilePathVariant(String corePath,
                                                  String format,
                                                  boolean underslash,
                                                  boolean recursion) {
        return getRandomFilePathVariant("", corePath, format, underslash, recursion);
    }

    public static String getRandomFilePathVariant(String prefixPath, String corePath,
                                                  String format, boolean underslash, boolean recursion) {
        corePath = StringMaster.cropFormat(corePath);
        File file = getFile(prefixPath + corePath + format);
        if (!file.isFile()) {
            LogMaster.verbose("no  file available for " + file.getPath());
            return null;
        }
        int i = 2;
        List<String> filesPaths = new ArrayList<>();
        while (file.isFile()) {

            String newPath = prefixPath + corePath + ((underslash) ? "_" : "") + i + format;
            file = FileManager.getFile(newPath);
            if (!file.isFile()) {
                newPath = prefixPath + corePath + (" ") + i + format;
                file = FileManager.getFile(newPath);
            }
            if (!file.isFile()) {
                break;
            }
            filesPaths.add(newPath);
            i++;
        }

        if (i == 2) {
            if (!recursion) {
                return getRandomFilePathVariant(prefixPath, corePath, format, underslash, true);
            }
            if (!filesPaths.isEmpty()) {
                return new RandomWizard<String>().getRandomListItem(filesPaths).replace(
                        prefixPath, "");
            }
            return corePath + format;
        }
        if (!filesPaths.isEmpty()) {
            return new RandomWizard<String>().getRandomListItem(filesPaths).replace(
                    prefixPath, "");
        }
        int number = (new Random().nextInt(i));
        if (number == 1) {
            number++;
        }
        String key = String.valueOf(number);
        if (number == 0) {
            key = "";
        }

        return corePath + key + format;
    }

    public static List<String> getFileNames(List<File> files) {

        List<String> list = new ArrayList<>();

        for (File file : files) {
            list.add(file.getName());
        }
        return list;

    }

    public static List<String> getFilePaths(List<File> files) {

        List<String> list = new ArrayList<>();

        for (File file : files) {
            list.add(file.getAbsolutePath());
        }
        return list;

    }

    public static String getUniqueVersion(File file) {
        String fileName = file.getName();
        String folder = file.getParent();
        return getUniqueFileVersion(fileName, folder);
    }

    public static String getUniqueFileVersion(String fileName, String folder) {
        String format = StringMaster.getFormat(fileName);
        String name = StringMaster.cropFormat(fileName);
        if (name.lastIndexOf(getFileVersionSeparator()) > 0) {
            if (NumberUtils.isInteger(name.substring(
                    name.lastIndexOf(getFileVersionSeparator()), name.length() - 1
            ))) {
                name = name.substring(0, name.lastIndexOf(getFileVersionSeparator()));
            }
        }
        String originalName = name;

        List<String> siblings = getFileNames(getFilesFromDirectory(folder, true));
        int i = 2;
        while (siblings.contains(name + format)) {

            name = originalName + getFileVersionSeparator() + i;
            i++;
        }
        return name + format;
    }

    private static String getFileVersionSeparator() {
        return "-";
    }

    public static List<File> findFiles(String path, String regex) {
        File folder = getFile(path);
        if (!folder.isDirectory()) {
            return new ArrayList<>();
        }
        return findFiles(folder, regex, false, true);
    }

    public static String findFirstFile(String folder, String regex, boolean closest) {
        List<File> files = getFilesFromDirectory(folder, false);
        if (files.isEmpty()) {
            return null;
        }
        if (closest) {
            return SearchMaster.findClosest(regex, getFileNames(files).toArray()).toString();
        }
        return new SearchMaster<String>().find(regex, getFileNames(files));
    }

    public static List<File> findFiles(File folder, String regex) {
        return findFiles(folder, regex, false, true);
    }

    public static List<File> findFiles(File folder, String regex, boolean cropFormat, boolean strict) {
        List<File> files = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                if (regex.isEmpty()) {
                    files.add(file);
                } else {
                    String fileName = cropFormat ? StringMaster.cropFormat(file.getName()) : file
                            .getName();
                    if (strict) {
                        if (StringMaster.compareByChar(StringMaster.cropFileVariant(fileName),
                                StringMaster.cropFormat(regex), false)) {
                            files.add(file);
                        }
                    } else {
                        if (StringMaster.compare(StringMaster.cropFileVariant(fileName),
                                // StringMaster.cropFormat
                                (regex), false)) {
                            files.add(file);
                        }
                    }
                }

            }
        }
        return files;
    }

    public static void appendToTextFile(String content, String path,
                                        String fileName) {
        String fullPath = path + "/" + fileName;
        appendToTextFile(content, fullPath);
    }

    public static void appendToTextFile(String content, String fullPath) {
        String prevContent = readFile(fullPath);
        write(prevContent + content, fullPath);
    }

    public static boolean write(String content, String filepath) {
        return write(content, filepath, false);
    }

    public static boolean write(String content, String filepath, boolean formatPath) {
        if (CoreEngine.isWindows())
            if (!filepath.contains(":")) {
                filepath = PathFinder.getRootPath() + "/" + filepath;
            }
        if (formatPath) {
            filepath = formatPath(filepath);
        } else
            filepath = filepath.trim();

        try {
            File file = FileManager.getFile(filepath);
            if (!file.exists()) {
                File parent = FileManager.getFile(file.getParent());
                if (!parent.isDirectory()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            } else {
                if (content.length() == 0) {
                    if (FileManager.readFile(file).length() > content.length()) {
                        LogMaster.log(1, "*Not writing empty file! "
                                + filepath);
                        return false;
                    }
                }
            }
            Writer writer = null;
            try {
                writer = new FileWriter(file);
            } catch (Exception e) {
                writer = new PrintWriter(filepath, "UTF-8");
            }


            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }

        return true;
    }

    //    public static List<String> listFiles(File file, boolean allowDirs) {
    //        return getFileNames(getFilesFromDirectory(file.getPath(), allowDirs));
    //    }
    public static List<String> listFiles(String path) {
        return getFileNames((getFilesFromDirectory(path, true)));
    }

    public static List<String> listFiles(File file) {
        return getFileNames(getFilesFromDirectory(file.getPath(), true));
    }

    public static List<File> getFilesFromDirectory(String path, boolean allowDirectories) {
        return getFilesFromDirectory(path, allowDirectories, false);
    }

    public static boolean isMusicFile(File sub) {
        String format = StringMaster.getFormat(sub.getName()).replace(".", "");
        for (String f : ContainerUtils.open(SoundMaster.STD_FORMATS)) {
            if (format.equalsIgnoreCase(f)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isImageFile(String name) {
        if (name == null)
            return false;
        String format = StringMaster.getFormat(name);
        for (String f : ImageManager.STD_FORMATS) {
            if (format.replaceFirst(".", "").equalsIgnoreCase(f)) {
                return true;
            }
        }
        return false;
    }

    public static List<File> getFilesFromDirectory(String path, boolean allowDirectories,
                                                   boolean subDirectories) {
        File folder = FileManager.getFile(path);
        return getFilesFromDirectory(folder, allowDirectories, subDirectories);
    }

    public static List<File> getFilesFromDirectory(File folder, boolean allowDirectories,
                                                   boolean subDirectories) {
        if (!folder.isDirectory()) {
            return new ArrayList<>();
        }
        List<File> list = new ArrayList<>();
        for (File f : folder.listFiles()) {
            if (subDirectories) {
                list.addAll(getFilesFromDirectory(f.getPath(), allowDirectories, true));
            }
            if (f.isDirectory()) {
                if (!allowDirectories) {
                    continue;
                }

            } else if (!(f.isFile())) {
                continue;
            }

            if (f.getName().equalsIgnoreCase("desktop.ini")) {
                continue;
            }
            list.add(f);
        }
        return list;
    }

    public static List<File> getFiles(String path, String name, String format) {
        return getFiles(path, name, format, false, false);
    }

    public static List<File> getFiles(String path, String name, String format, boolean dirs, boolean subdirs) {
        List<File> files = getFilesFromDirectory(path, dirs, subdirs);
        files.removeIf(file -> {
            if (!StringMaster.getFormat(file.getName()).equalsIgnoreCase(format))
                return true;
            return !StringMaster.contains(file.getName(), name);
        });
        return files;
    }

    public static File getRandomFile(List<File> files) {
        int randomListIndex = RandomWizard.getRandomIndex(files);
        if (randomListIndex == -1) {
            return null;
        }
        return files.get(randomListIndex);
    }

    public static File getRandomFile(String path, boolean recursive) {
        return getRandomFile(getFilesFromDirectory(path, false, recursive));
    }

    public static File getRandomFile(String path) {
        return getRandomFile(getFilesFromDirectory(path, false));
    }

    public static String getRandomFilePath(String path) {
        return getRandomFile(path).getPath();
    }

    public static String getRandomFileName(String path) {
        File file = getRandomFile(path);
        if (file == null)
            return null;
        return file.getName();
    }

    public static Path getPath(File file) {
        return Paths.get(file.toURI());
    }

    public static boolean isSameFile(File file, File current) {
        try {
            return FileUtils.contentEquals((file), (current));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void copy(String from, String to) {
        Path src = Paths.get(getFile(from).toURI());
        Path target = Paths.get(getFile(to).toURI());
        try {
            Files.copy(src, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public static String getFileName(String template) {
        return StringMaster.cropFormat(getFile(template).getName());
    }
}
