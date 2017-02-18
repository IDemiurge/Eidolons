package main.system.auxiliary.data;

import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.SearchMaster;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.system.sound.SoundMaster;
import org.w3c.dom.Node;

import java.io.*;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileManager {
    public static String readFile(String filePath) {
        File file = getFile(filePath);
        return readFile(file);
    }

    public static List<String> readFileLines(String filePath) {
        File file = getFile(filePath);
        return readFileLines(file);
    }

    public static List<String> readFileLines(File file) {
        String string = readFile(file, "\n");
        return StringMaster.openContainer(string, "\n");
    }

    public static String readFile(File file) {
        return readFile(file, "");
    }

    public static String readFile(File file, String lineSeparator) {
        if (!isFile(file)) {
            return "";
        }

        String result = "";

        try {
            result = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        } finally {
            try {
                if (fr != null)
                    fr.close();
            } catch (IOException e) {
                e.printStackTrace();
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
                    String xmlString = "";
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
                        xmlString += string.substring(0, n);
                    }

                    Node node = XML_Converter.findAndBuildNode(xmlString, "office:text");
                    String string = "";
                    for (Node child : XML_Converter.getNodeList(node)) {
                        string += child.getTextContent();
                    }
                    return string;
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

        }
        return null;
    }

    public static boolean isFile(String file) {
        return isFile(new File(file));
    }

    public static boolean isFile(File file) {
        if (file == null) {
            return false;
        }
        return file.isFile();
    }

    public static File getFile(String path) {
        return getFile(path, true);
    }

    public static File getFile(String path, boolean first) {
        File file = new File(path);
        if (!first || file.isFile() || file.isDirectory()) {
            return file;
        }
        file = getFile(formatPath(path), false);
        return file;
    }

    private static String formatPath(String path) {
        String formatted = "";
        for (String sub : StringMaster.splitPath(path)) {
            formatted += StringMaster.replace(true, sub, "\\", "") + "\\";
        }
        return formatted;
    }

    public static String getRandomFilePathVariant(String corePath, String format) {
        return getRandomFilePathVariant(corePath, format, false);
    }

    public static String getRandomFilePathVariant(String corePath, String format, boolean underslash) {
        File file = new File(corePath + format);
        if (!file.isFile()) {
            LogMaster.log(1, "no  file available for " + corePath + " - " + format);
            return null;
        }
        int i = 2;
        while (file.isFile()) {

            String newPath = corePath + ((underslash) ? "_" : "") + i + format;
            file = new File(newPath);
            if (!file.isFile()) {

                break;
            }
            i++;
        }
        if (i == 2) {
            return corePath + format;
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

        List<String> list = new LinkedList<>();

        for (File file : files) {
            list.add(file.getName());
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
        if (name.lastIndexOf(getFileVersionSeparator()) > 0)
            if (StringMaster.isInteger(name.substring(
             name.lastIndexOf(getFileVersionSeparator()), name.length() - 1
            ))) {
                name = name.substring(0, name.lastIndexOf(getFileVersionSeparator()));
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
            return new LinkedList<>();
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
        List<File> files = new LinkedList<>();
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

    public static void appendToTextFile(String content, String path, String fileName) {
        String filePath = path + "\\" + fileName;
        String prevContent = readFile(filePath);
        write(prevContent + content, filePath);
    }

    public static boolean write(String content, String filepath) {
        if (!filepath.contains(":")) {
            filepath = PathFinder.getEnginePath() + "\\" + filepath;
        }
        filepath = filepath.trim();
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                if (!new File(file.getParent()).isDirectory()) {
                    file.mkdirs();
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
            FileWriter fw = new FileWriter(file);

            fw.write(content);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static List<File> getFilesFromDirectory(String path, boolean allowDirectories) {
        return getFilesFromDirectory(path, allowDirectories, false);
    }

    public static boolean isMusicFile(File sub) {
        String format = StringMaster.getFormat(sub.getName()).replace(".", "");
        for (String f : StringMaster.openContainer(SoundMaster.STD_FORMATS)) {
            if (format.equalsIgnoreCase(f)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isImageFile(String name) {
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
        List<File> list = new LinkedList<>();
        File folder = new File(path);
        if (!folder.isDirectory()) {
            return list;
        }
        for (File f : folder.listFiles()) {
            if (subDirectories) {
                list.addAll(getFilesFromDirectory(f.getPath(), allowDirectories, subDirectories));
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

    public static File getRandomFile(List<File> files) {
        int randomListIndex = RandomWizard.getRandomListIndex(files);
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
        return getRandomFile(path).getName();
    }

}
