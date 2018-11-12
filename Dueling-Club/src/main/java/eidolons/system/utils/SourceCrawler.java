package eidolons.system.utils;

import main.data.filesys.PathFinder;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.List;

/**
 * Created by JustMe on 8/4/2017.
 */
public class SourceCrawler {

    public SourceCrawler() {
        List<File> files = FileManager.getFilesFromDirectory(getDirPath(), false, true);
        files.forEach(file -> {
            if (file.getName().contains(".java")) {
                String text = modifyFile(StringMaster.cropFormat(file.getName()), FileManager.readFile(file));
                if (text != null)
                    FileManager.write(text, file.getAbsolutePath());
            }
        });
//    dir = FileManager.getFile(getDirPath());
    }

    public static void main(String[] args) {
        new SourceCrawler();
    }

    private String getDirPath() {
        return PathFinder.getXML_PATH() + "testing presets/src crawl/";
    }

    public String modifyFile(String className, String text) {

        String separator = className + "(";
        List<String> replacedParts = ContainerUtils.openContainer(text, separator);
        if (replacedParts.size() < 2) return null;
        replacedParts = replacedParts.subList(1, replacedParts.size());


        for (String sub : replacedParts) {
            String replacement = getReplacement(sub);
            text = text.replace(sub, replacement);
        }

        return text;
    }

    private String getReplacement(String sub) {
        String args = sub.substring(0, sub.indexOf(')'));
        int insertIndex = sub.lastIndexOf('}') - 1;
        String parameters = "";
        int i = 0;
        for (String var : args.split(" ")) {
            if (i % 2 == 1)
                parameters += var;
            i++;
        }

        String insertion = getInsertion() + parameters + ");\n";
        String replacement = sub.substring(0, insertIndex) + insertion + "\n}";
        return replacement;
    }

    private String getInsertion() {
        return "mapThisToConstrParams(";
    }
}
