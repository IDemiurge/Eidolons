package main.system.util;

import main.content.OBJ_TYPES;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.StringMaster;
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
    private static Map<String, ObjType> map = new XLinkedMap<>();
    private static String folderName = "Ordered";

    public static void createArtFolders(String arg) {
        List<OBJ_TYPES> types = new EnumMaster<OBJ_TYPES>().getEnumList(OBJ_TYPES.class, arg);
        for (OBJ_TYPES t : types) {
            createArtFolder(t);
        }
        boolean unused;
    }

    public static void createUpdatedArtDirectory() {
        Map<String, ObjType> map = new XLinkedMap<>();
        // String path;
        for (PROPERTY prop : props) {
            for (OBJ_TYPES t : OBJ_TYPES.values()) {
                try {
                    createArtFolder(t, true, prop, map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (File f : FileManager.getFilesFromDirectory(ImageManager.getPATH() + "mini\\", false,
                true)) {
            if (!ImageManager.isImageFile(f.getName())) {
                continue;
            }
            if (map.get(f.getPath().replace(ImageManager.getPATH(), "")) == null) {
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
                ImageManager.getPATH(), ""));
        String pathPart = segments.get(1) + "\\";
        if (!segments.get(2).contains(".")) {
            pathPart += segments.get(2) + "\\";
        }
        File outputfile = new File(ImageManager.getPATH() + folderName + "\\unused\\" + pathPart
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
            main.system.auxiliary.LogMaster.log(1, "Unused image failed to write: " + f.getPath());
        }

    }

    private static Image getImage(File f) {
        return ImageManager.getImage(f.getPath());
    }

    public static void createArtFolder(OBJ_TYPES TYPE) {
        createArtFolder(TYPE, false, G_PROPS.IMAGE, map);
    }

    public static void createArtFolder(OBJ_TYPES TYPE, boolean update, PROPERTY imgProp,
                                       Map<String, ObjType> map) {

        for (ObjType type : DataManager.getTypes(TYPE)) {
            String path = getNewImagePath(imgProp, type);
            File outputfile = new File(path);

            String oldPath = type.getProperty(imgProp);
            Entity cached = map.get(oldPath);
            if (cached != null) {
                // ???
            } else {
                BufferedImage bufferedImage = ImageManager.getBufferedImage(oldPath);
                if (!ImageManager.isValidImage(bufferedImage)) {
                    continue;
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
                map.put(oldPath, type);
            }
            if (update) {
                type.setProperty(imgProp, path);
            }
        }
    }

    private static String getNewImagePath(PROPERTY imgProp, Entity type) {
        String group = type.getGroupingKey() + "\\";
        String subgroup = type.getSubGroupingKey() + "\\";
        return PathFinder.getImagePath() + folderName + "\\" + imgProp.getName() + "\\"
                + type.getOBJ_TYPE() + "\\" + group + subgroup + "\\" + type.getName() + "."
                + getNewImageFormat();
    }

    private static String getNewImageFormat() {
        return "png";
    }

}
