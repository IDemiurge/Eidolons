package main.system.util;

import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
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

    // private static final OBJ_TYPES[] types = { OBJ_TYPES.BUFFS, };
    private static final PROPERTY[] props = {G_PROPS.IMAGE, G_PROPS.BACKGROUND,};
    private static final String UNUSED_FOLDER = "\\unused\\";
    private static final String USED_FOLDER = "entity";
    static boolean setProperty = true;
    static boolean useFirstEntityIfOverlap = true;
    private static Map<String, ObjType> map = new XLinkedMap<>();
    private static String folderName = "gen";

    public static void updateImagePaths() {

        for (ObjType type : DataManager.getTypes()) {
            updateImagePath(type);
        }

    }

    private static void updateImagePath(ObjType type) {
        String path = getNewImagePath(G_PROPS.IMAGE, type);
        type.setProperty(G_PROPS.IMAGE, path);
    }

    public static void createUpdatedArtDirectory() {
        Map<String, ObjType> map = new XLinkedMap<>();
        // String path;
        for (PROPERTY prop : props) {
            for (DC_TYPE t : DC_TYPE.values()) {
                try {
                    createArtFolder(t, setProperty, prop, map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
// unused
        for (File f : FileManager.getFilesFromDirectory(ImageManager.getImageFolderPath() + "mini\\", false,
         true)) {
            if (!ImageManager.isImageFile(f.getName())) {
                continue;
            }
            if (map.get(f.getPath().replace(ImageManager.getImageFolderPath(), "")) == null) {
                try {
                    writeToUnused(f, getImage(f));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeToUnused(File f, Image image) {
        List<String> segments = StringMaster.getPathSegments(f.getPath().replace(
         ImageManager.getImageFolderPath(), ""));
        String pathPart = segments.get(1) + "\\";
        if (!segments.get(2).contains(".")) {
            pathPart += segments.get(2) + "\\";
        }
        File outputfile = new File(ImageManager.getImageFolderPath() + folderName +
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
            e.printStackTrace();
            LogMaster.log(1, "Unused image failed to write: " + f.getPath());
        }

    }

    private static Image getImage(File f) {
        return ImageManager.getImage(f.getPath());
    }


    public static void createArtFolder(DC_TYPE TYPE, boolean update, PROPERTY imgProp,
                                       Map<String, ObjType> map) {

        for (ObjType type : DataManager.getTypes(TYPE)) {
            String path = getNewImagePath(imgProp, type);
            String oldPath = type.getProperty(imgProp);
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
                    e.printStackTrace();
                    continue;
                }
            }
            if (update) {
                type.setProperty(imgProp, path);
            }
        }
    }

    private static void writeImage(String oldPath, String path) {
        File outputfile = new File(path);
        BufferedImage bufferedImage = ImageManager.getBufferedImage(oldPath);
        if (!ImageManager.isValidImage(bufferedImage)) {
            throw new RuntimeException() ;
        }
        try {
            if (!outputfile.isFile())
            // {}
            // if (!outputfile.exists())
            {
                outputfile.mkdirs();
                outputfile.createNewFile();
                ImageIO.write(bufferedImage, getNewImageFormat(), outputfile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getNewImagePath(PROPERTY imgProp, Entity type) {
        String group = type.getGroupingKey() + "\\";
        String subgroup = type.getSubGroupingKey() + "\\";
        return PathFinder.getImagePath() + folderName + "\\"
         + USED_FOLDER + "\\"
         + imgProp.getName() + "\\"
         + type.getOBJ_TYPE() + "\\" + group + subgroup + "\\" + type.getName() + "."
         + getNewImageFormat();
    }

    private static String getNewImageFormat() {
        return "png";
    }

    public static void createArtFolders(String arg) {
        List<DC_TYPE> types = new EnumMaster<DC_TYPE>().getEnumList(DC_TYPE.class, arg);
        for (DC_TYPE t : types) {
            createArtFolder(t);
        }
    }

    public static void createArtFolder(DC_TYPE TYPE) {
        createArtFolder(TYPE, false, G_PROPS.IMAGE, map);
    }
}
