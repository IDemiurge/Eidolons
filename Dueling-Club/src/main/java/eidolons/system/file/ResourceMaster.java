package eidolons.system.file;

import eidolons.content.PROPS;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ResourceMaster {
    private static final String ignoredTypes = "chars;";
    private static final String ignoreGroupTypes = "encounters;dungeons;units;deities;";
    private static final String ignoreSubGroupTypes =
     "abils;actions;buffs;weapons;armor;actions;";

    //    ignore {} img path
    //    add item variants (generate if missing?)

    private static final PROPERTY[] props = {PROPS.MAP_BACKGROUND, G_PROPS.IMAGE,};
    private static final String UNUSED_FOLDER = "\\unused\\";
    private static final String USED_FOLDER = "entity";
    static boolean setProperty = true;
    static boolean useFirstEntityIfOverlap = true;
    private static String folderName = "gen";
    private static Map<String, ObjType> map = new XLinkedMap<>();
    private static boolean writeUnused = false;

    public static void updateImagePaths() {

        for (ObjType type : DataManager.getTypes()) {
            updateImagePath(type);
        }

    }

    private static void updateImagePath(ObjType type) {
        for (PROPERTY prop : props) {
            if (!type.checkProperty(prop)) continue;
            String path = getNewImagePath(prop, type);
            type.setProperty(prop, path);
        }
    }

    public static void createUpdatedArtDirectory() {
        //        Map<String, ObjType> map = new XLinkedMap<>();
        // String path;
        for (PROPERTY prop : props) {
            for (DC_TYPE t : DC_TYPE.values()) {
                if (ignoredTypes.contains(t.getName()))
                    continue;
                for (ObjType type : DataManager.getTypes(t)) {
                    if (!type.checkProperty(prop))
                        continue;
                    try {
                        writeAndUpdateImage(type, setProperty, prop, map);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                }
            }
        }
        // unused
        if (!writeUnused)
            return;
        for (File f : FileManager.getFilesFromDirectory
         (ImageManager.getImageFolderPath() + "mini\\", false,
          true)) {
            try {
                if (!ImageManager.isImageFile(f.getName())) {
                    continue;
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                continue;
            }
            if (map.get(f.getPath().replace(ImageManager.getImageFolderPath(), "")) == null) {
                try {
                    writeToUnused(f, getImage(f));
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
    }

    private static void writeToUnused(File f, Image image) {
        List<String> segments = PathUtils.getPathSegments(f.getPath().replace(
         ImageManager.getImageFolderPath(), ""));
        String pathPart = segments.get(1) + "\\";
        if (!segments.get(2).contains(".")) {
            pathPart += segments.get(2) + "\\";
        }
        File outputfile = FileManager.getFile(ImageManager.getImageFolderPath() + folderName +
         UNUSED_FOLDER + pathPart
         + "\\" + f.getName());

        BufferedImage bufferedImage = ImageManager.getBufferedImage(image);
        try {
            if (!outputfile.isFile()) {
                outputfile.mkdirs();
                outputfile.createNewFile();
            }
            ImageIO.write(bufferedImage, getNewImageFormat(), outputfile);
        } catch (IOException e) {
            main.system.ExceptionMaster.printStackTrace(e);
            LogMaster.log(1, "Unused image failed to write: " + f.getPath());
        }

    }

    private static Image getImage(File f) {
        return ImageManager.getImage(f.getPath());
    }


    public static void writeAndUpdateImage(ObjType type, boolean update, PROPERTY imgProp,
                                           Map<String, ObjType> map) {


        String oldPath = type.getProperty(imgProp);
        if (oldPath.contains(StringMaster.FORMULA_REF_OPEN_CHAR))
            return; // variable value!

        String path = getNewImagePath(imgProp, type);
        path = checkDuplicate(path, map);
        Entity cached = map.get(oldPath);
        if (cached != null) {
            if (useFirstEntityIfOverlap)
                path = getNewImagePath(imgProp, cached);
            //else  ???
        } else {
            try {
                writeImage(oldPath, path);
                map.put(oldPath, type);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                return;
            }
        }
        if (update) {
            type.setProperty(imgProp, path);
        }
    }

    private static String checkDuplicate(String oldPath, Map<String, ObjType> map) {
        for (String path : map.keySet()) {
            if (PathUtils.getLastPathSegment(oldPath).equals(
             PathUtils.getLastPathSegment(path))) {
                File f1 = FileManager.getFile(ImageManager.getImageFolderPath() + oldPath);
                File f2 = FileManager.getFile(ImageManager.getImageFolderPath() + path);
                if (f1.length() == f2.length()) {
                    oldPath = path;
                    break;
                }
            }
        }
        return oldPath;
    }

    public static void writeImage(String oldPath, String path) {
        writeImage(oldPath, path, false);
    }
    public static void writeImage(String oldPath, String path  , boolean overwrite) {
        File outputfile = FileManager.getFile(ImageManager.getImageFolderPath() + path);
        BufferedImage bufferedImage = ImageManager.getBufferedImage(oldPath);
        if (!ImageManager.isValidImage(bufferedImage)) {
            throw new RuntimeException();
        }
        try {
            if (!outputfile.isFile() || overwrite)
            // {}
            // if (!outputfile.exists())
            {
                outputfile.mkdirs();
                outputfile.createNewFile();
                ImageIO.write(bufferedImage, getNewImageFormat(), outputfile);
            }
        } catch (IOException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    private static String getNewImagePath(PROPERTY imgProp, Entity type) {
        String group = type.getGroupingKey() + "\\";
        if (ignoreGroupTypes.contains(type.getOBJ_TYPE()))
            group = "";
        String subgroup = type.getSubGroupingKey() + "\\";
        if (ignoreSubGroupTypes.contains(type.getOBJ_TYPE()))
            subgroup = "";

        String propString = imgProp.getName() + "\\";
        if (imgProp == G_PROPS.IMAGE)
            propString = "";

        String typeName = type.getName().replace(":", "-");
        //        for (String s: XML_Writer.)

        return folderName + "\\"
         + USED_FOLDER + "\\"
         + propString
         + type.getOBJ_TYPE() + "\\" + group + subgroup + "\\" + typeName + "."
         + getNewImageFormat()
         .toLowerCase();
    }

    private static String getNewImageFormat() {
        return "png";
    }

}
