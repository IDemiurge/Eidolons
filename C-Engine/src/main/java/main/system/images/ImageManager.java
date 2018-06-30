package main.system.images;

import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.ASPECT;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.directions.FACING_DIRECTION;
import main.swing.SwingMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.ColorManager.FLAG_COLOR;
import main.system.graphics.GuiManager;
import main.system.graphics.MigMaster;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageManager {

    public final static String[] STD_FORMATS = {"jpg", "png",};
    public static final String DEFAULT_EMBLEM = "UI\\deities.png";
    public static final String DEFAULT_ENTITY_IMAGE_FORMAT = ".jpg";
    public static final int CENTERED = -999;
    public static final String VALUE_ICONS_PATH = "UI\\value icons\\";
    public static final String DEAD_ICON = "UI\\dead.png";
    private static final String EMPTY_LIST_ITEM = "UI\\EMPTY_ITEM.jpg";
    private static final String EMPTY_LIST_ITEM_ALT = "UI\\EMPTY_ITEM_ALT.jpg";
    private static final String EMPTY_LIST_ITEM_SMALL = "UI\\EMPTY_ITEM_SMALL.jpg";
    private static final String HL_CELL = "UI\\HIGHLIGHTED_CELL.png";
    private static final String CONCEALED_CELL = "UI\\HIDDEN_CELL.png";
    private static final String UNSEEN_CELL = "UI\\UNDETECTED_CELL.png";
    private static final String CELL = "UI\\EMPTY CELL2.png";
    private static final String DEFAULT_IMAGE_PATH = "UI\\Empty1.jpg";
    private static final int MAX_TYPE_ICON_SIZE = 256;
    public static final int LARGE_ICON = MAX_TYPE_ICON_SIZE;
    private static final String DEFAULT_CURSOR = "UI\\cursor.png";
    private static final String PORTRAIT_ROOT_PATH = "\\mini\\char\\std\\";
    private static final String EMBLEM_PATH = "UI\\emblems\\std\\";
    private static String PATH;
    private static String DEFAULT;
    private static ImageObserver observer = new ImageObserver() {
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            return false;
        }
    };
    private static Map<String, CustomImageIcon> iconCache = new HashMap<>();
    private static Map<String, ImageIcon> sizedImageStringCache = new HashMap<>();
    private static Map<String, Image> sizedImageCache = new HashMap<>();
    private static Image DEFAULT_IMG;
    private static HashMap<BACKGROUND, List<String>> portraitMap;
    private static Map<COLOR_THEME, Map<String, String>> colorImgCache;
    private static List<String> noColorTheme = new ArrayList<>();
    private static HashMap<BACKGROUND, List<String>> extendedPortraitMap;
    private static Map<Obj, Map<String, ImageIcon>> customIconCache;
    private static Map<String, JLabel> labelCache = new HashMap<>();

    public static void init() {
        setPATH(PathFinder.getImagePath());
        DEFAULT = "chars\\";
        colorImgCache = new HashMap<>();
        COLOR_THEME[] values = COLOR_THEME.values();
        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            COLOR_THEME c = values[i];
            colorImgCache.put(c, new HashMap<>());
        }
    }

    public static CustomImageIcon getIcon(String imgName) {
        // Toolkit.getDefaultToolkit().createImage(filename)
        if (imgName == null) {
            return null;
        }
        if (iconCache.get(imgName) != null) {
            return iconCache.get(imgName);
        }
        // DEFAULT + f.list()[new Random().nextInt(f.list().length)]

        CustomImageIcon img;
        if (!imgName.toLowerCase().contains(getImageFolderPath().toLowerCase())) {
            img = new CustomImageIcon(getImageFolderPath() + imgName);
        } else {
            img = new CustomImageIcon(imgName);
        }

        if (img.getImage().getHeight(null) == -1) {
            if (!imgName.equals(imgName.toLowerCase())) {
                return getIcon(imgName.toLowerCase());
            }
            // return "broken skull" image! at least for units and list items!
            // if (imgName.contains(".")) {
            if (!imgName.contains(getImageFolderPath())) {
                imgName = getImageFolderPath() + imgName;
            }
            if (StringMaster.getFormat(imgName).length() < 2) {
                for (String format : STD_FORMATS) {
                    img = new CustomImageIcon(imgName + format);
                    if (isValidIcon(img)) {
                        return img;
                    }
                    img = new CustomImageIcon(imgName + "." + format);
                    if (isValidIcon(img)) {
                        return img;
                    }
                }
            }
            // }
            return null;
        }
        iconCache.put(imgName, img);
        return img;
    }

    public static ImageIcon getEmptyIcon(int obj_size) {
        return new ImageIcon(getImageFolderPath() + "UI\\empty" + obj_size + ".jpg");
    }


    public static String getEmptyItemIconPath(boolean alt) {
        if (alt) {
            return "UI\\EMPTY_ITEM_ALT.jpg";
        }
        return "UI\\EMPTY_ITEM.jpg";

    }

    public static String getEmptyUnitIconPath() {
        return "UI\\Empty.jpg";
    }

    public static String getEmptyUnitIconFullSizePath() {
        return "UI\\Empty Full.jpg";
    }

    public static ImageIcon getEmptyUnitIcon() {
        return new ImageIcon(getImageFolderPath() + getEmptyUnitIconPath());
    }

    public static String getDEFAULT() {
        return DEFAULT;
    }

    public static void setDEFAULT(String dEFAULT) {
        DEFAULT = dEFAULT;
    }

    public static String getDefaultImageLocation() {
        return getImageFolderPath();
    }

    public static Image applyBorder(Image img, BORDER border) {
        return applyBorder(img, border, true);
    }

    public static Image applyBorder(Image img, BORDER border, Boolean resize) {
        return applyBorder(img, border, true, null);
    }

    public static Image applyBorder(Image img, BORDER border, Boolean resize, Dimension size) {
        Image bImg = border.getImage();
        if (bImg == null) {
            return img;
        }
        if (img.getWidth(observer) > border.getImage().getWidth(observer)) {
            border = border.getBiggerVersion();
            if (border != null) {
                bImg = border.getImage();
            }
        }
        try {
            return applyImage(img, bImg, 0, 0, resize, false, size);
        } catch (Exception e) {
            return img;
        }
    }

    public static Image applyImage(Image target, Image applied, int x, int y) {
        return applyImage(target, applied, x, y, true);
    }

    public static ImageIcon getOffsetImage(Image img, int size, ALIGNMENT al) {
        BufferedImage canvas = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        int x = 0;
        int y = 0;
        switch (al) {
            case SOUTH_EAST:
                x = size - img.getWidth(null);
                y = size - img.getHeight(null);
                break;
            case CENTER:
                x = size / 2 - img.getWidth(null) / 2;
                y = size / 2 - img.getHeight(null) / 2;
                break;
            case NORTH_WEST:
                break;

        }
        return new ImageIcon(applyImage(canvas, img, x, y, false));

    }

    public static Image applyImage(Image target, Image applied, int x, int y, boolean resize) {
        return applyImage(target, applied, x, y, resize, true);
    }

    public static Image applyImage(Image target, Image applied, int x, int y, Boolean resize,
                                   boolean retry) {
        return applyImage(target, applied, x, y, resize, retry, null);
    }

    public static Image applyImage(Image target, Image applied, int x, int y, Boolean resize,
                                   boolean retry, Dimension size) {

        if (x == CENTERED) {
            x = MigMaster
             .getCenteredPosition(applied.getWidth(observer), target.getWidth(observer));
        }
        if (y == CENTERED) {
            y = MigMaster.getCenteredPosition(applied.getHeight(observer), target
             .getHeight(observer));
        }
        // return applied;
        if (target == null || applied == null) {
            throw new RuntimeException();
        }
        try {
            BufferedImage IMG;
            if (resize == null && size != null) {
                IMG = new BufferedImage((int) size.getWidth(), (int) size.getHeight(),
                 BufferedImage.TYPE_INT_ARGB);
                IMG.getGraphics().drawImage(target, 0, 0, (int) size.getWidth(),
                 (int) size.getHeight(), observer);
                IMG.getGraphics().drawImage(applied, x, y, (int) size.getHeight(),
                 (int) size.getWidth(), observer);
                return IMG;
            }
            IMG = new BufferedImage(target.getWidth(observer), target.getHeight(observer),
             BufferedImage.TYPE_INT_ARGB);
            IMG.getGraphics().drawImage(target, 0, 0, observer);
            if (resize) {
                if (target.getHeight(observer) != applied.getHeight(observer)) {
                    IMG.getGraphics().drawImage(applied, x, y, target.getWidth(observer),
                     target.getHeight(observer), observer);
                    return IMG;
                }
            }
            IMG.getGraphics().drawImage(applied, x, y, observer);
            return IMG;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            LogMaster.log(1, " failed " + target.getWidth(observer) + " and "
             + applied.getWidth(observer));
            // if (retry)
            // return applyImage(target, applied, x, y, resize, false);
            return target;
        }
    }

    public static ImageIcon getSizedIcon(String imgName, int size) {
        return getSizedIcon(imgName, new Dimension(size, size), false);
    }

    public static ImageIcon getSizedIcon(String imgName, Dimension size) {
        return getSizedIcon(imgName, size, false);
    }

    public static ImageIcon getSizedIcon(String imgName, Dimension size, boolean forceCache) {
        ImageIcon icon;
        try {
            icon = sizedImageStringCache.get(imgName + size.getWidth() + size.getHeight());
            if (icon == null) {
                throw new Exception();
            }
            if (icon.getImage().getHeight(null) == size.getHeight()
             && icon.getImage().getWidth(null) == size.getWidth()) {
                return icon;
            }

        } catch (Exception e) {
            if (!isImage(imgName)) {
                return new ImageIcon(getNewBufferedImage(size.width, size.height));
            }
            icon = new ImageIcon(getSizedVersion(getImage(imgName), size));
            sizedImageStringCache.put(imgName + size.getWidth() + size.getHeight(), icon);

        }
        return icon;
    }

    public static Image getSizedVersion(Image img, Dimension tsize, boolean forceCache) {
        Image image = null;
        if (checkMemoryForSizedCache() || forceCache) {
            image = sizedImageCache.get(img.hashCode() + tsize.toString());
        }
        if (image != null) {
            return image;
        }
        if (SwingMaster.DEBUG_ON) {
            LogMaster.log(1, img + " to " + tsize);
        }
        if (img.getWidth(null) != tsize.width || img.getHeight(null) != tsize.height) {
            image = img.getScaledInstance((int) tsize.getWidth(), (int) tsize.getHeight(),
             Image.SCALE_SMOOTH);
            if (checkMemoryForSizedCache() || forceCache) {
                sizedImageCache.put(img.hashCode() + tsize.toString(), image);
            }
            return image;
        } else {
            return img;
        }
    }

    public static Image getSizedVersion(Image img, Dimension tsize) {
        return getSizedVersion(img, tsize, false);
    }

    private static boolean checkMemoryForSizedCache() {
        return !CoreEngine.isLevelEditor();
    }

    /**
     * @param img
     * @param percentage
     * @return
     */

    public static Image getSizedVersion(Image img, int percentage) {
        return getSizedVersion(img, percentage, false);
    }

    public static Image getSizedVersion(Image img, int percentage, boolean forceCache) {
        int w = img.getWidth(null) * percentage / 100;
        int h = img.getHeight(null) * percentage / 100;
        return getSizedVersion(img, new Dimension(w, h), true);
        // BufferedImage buffered = new BufferedImage(w, h,
        // BufferedImage.TYPE_INT_ARGB);
        // buffered.getGraphics().drawImage(img, 0, 0, w, h, null);
        // return buffered;
        // img.getScaledInstance((int) img.getWidth(null) * percentage / 100,
        // (int) img
        // .getHeight(null)
        // * percentage / 100, Image.SCALE_SMOOTH);

    }

    public static Image getArrowImage(boolean vertical, boolean forward, boolean blocked,
                                      int version) {
        Image img = getArrowImage(vertical, forward, version);
        if (blocked) {
            BufferedImage buffered = getBufferedImage(img);
            // buffered.getGraphics().fill
            // pixel filter!
        }
        return img;
    }

    public static String getArrowImagePath(boolean vertical, boolean forward, int version) {
        String compVersion = (version == 1) ? "" : "" + version;
        String path = "UI\\components\\";
        if (forward) {
            path += vertical ? "up" : "right";
        } else
        // TODO swap up/down?!
        {
            path += vertical ? "down" : "left";
        }
        path += compVersion + ".png";
        return path;
    }

    public static ImageIcon getCellBorderForBfObj(boolean huge, PLAYER_VISION detection,
                                                  UNIT_VISION visibility) {
        String suffix = (!huge) ? "96" : "HUGE";
        if (detection == PLAYER_VISION.UNKNOWN
         || detection == PLAYER_VISION.CONCEALED) {
            suffix += " unknown";
        } else if (detection == PLAYER_VISION.KNOWN
         || visibility == UNIT_VISION.BEYOND_SIGHT) {
            suffix += " hidden";
        }
        return getIcon("UI//CELL for " + suffix + ".png");

    }

    public static ImageIcon getEmptyCellIcon() {
        return getEmptyCellIcon(GuiManager.getBfCellsVersion());
    }

    public static ImageIcon getEmptyCellIcon(int version) {
        return getIcon(getEmptyCellPath(version));
    }

    public static String getEmptyCellPath(int version) {
        return "UI//cells//Empty Cell v" + version + ".png";
    }

    public static ImageIcon getHighlightedCellIcon() {
        return getHighlightedCellIcon(GuiManager.getBfCellsVersion());
    }

    public static ImageIcon getEmptyEmblem() {
        return getIcon("UI//emblems//empty emblem.jpg");
    }

    public static String getEmptyEmblemPath() {
        return ("UI//emblems//auto//Knights.png");
    }

    // public static ImageIcon getHighlightedVersion(ImageIcon pic, HIGHLIGHT
    // hl, boolean terrain) {
    // hl.getBorder();
    // if (terrain) {
    // hl.getCellVariant();
    // }
    // Image IMG = pic.getImage();
    // pic.setImage(IMG);
    // return pic;
    // }

    public static ImageIcon getHighlightedCellIcon(int version) {
        return getIcon("UI//cells//Highlight Green Cell v" + version + ".png");
    }

    public static ImageIcon getHiddenCellIcon() {
        return getHiddenCellIcon(GuiManager.getBfCellsVersion());
    }

    public static ImageIcon getHiddenCellIcon(int version) {
        return getIcon("UI//cells//Hidden Cell v" + version + ".png");
    }

    public static ImageIcon getUnknownCellIcon() {
        return getUnknownCellIcon(GuiManager.getBfCellsVersion());
    }

    public static ImageIcon getUnknownCellIcon(int version) {
        return getIcon("UI//cells//Unknown Cell v" + version + ".png");
    }

    public static String getEmptyCellIconNoBorder() {
        return "UI//EMPTY_CELL_NO_BORDER" + ".png";
    }

    public static ImageIcon getEmptyCellBfObjBackgroundIcon() {
        return getIcon("UI//EMPTY_CELL_BF_OBJ_BG" + ".png");

    }

    public static String getUnknownSmallItemIconPath() {
        return "UI//unknown buff" + ".jpg";
    }

    public static JLabel getLabel(String img, int w, int h) {
        JLabel label = labelCache.get(img + w + h);
        if (label != null) {
            return label;
        }
        ImageIcon image = new ImageIcon(ImageManager.getSizedVersion(
         ((ImageManager.getImage(img) != null) ? ImageManager.getImage(img)
          : getDefaultImage()), new Dimension(w, h)));
        label = new JLabel(image);
        labelCache.put(img + w + h, label);
        return label;
    }

    private static Image getDefaultImage() {
        if (DEFAULT_IMG == null) {
            DEFAULT_IMG = getImage(DEFAULT_IMAGE_PATH);
            LogMaster.log(1, DEFAULT_IMAGE_PATH);
        }
        return DEFAULT_IMG;
    }

    public static Image getDeadIcon() {
        return getIcon(DEAD_ICON).getImage();
    }

    public static Image getDeadIconBig() {
        return getImage("UI\\Empty1.jpg");
    }

    public static Image getEmblem(String property) {
        LogMaster.log(1, "emblem for " + property);

        return null;
    }

    public static Image getImage(String imgPath) {
        CustomImageIcon icon;
        try {
            icon = getIcon(imgPath);
        } catch (Exception e) {
            LogMaster.log(LogMaster.GUI_DEBUG, "no image for " + imgPath);

            return null;
        }
        if (icon != null) {
            if (CoreEngine.isSwingOn()) {
                return
                 icon.getImage();
            }

            return new CustomImage(icon.imgPath, icon.getImage());
        }
        return null;
    }

    public static ImageIcon getHighlightedVersion(ImageIcon pic, HIGHLIGHT hl, boolean terrain) {
        return new ImageIcon(applyBorder(pic.getImage(), hl.getBorder()));
    }

    public static String getThemedImagePath(String imagePath, COLOR_THEME colorTheme) {
        if (colorTheme == null) {
            return imagePath;
        }
        if (noColorTheme.contains(imagePath)) {
            return imagePath;
        }
        String imgPath = colorImgCache.get(colorTheme).get(imagePath);
        if (imgPath == null) {
            imgPath = StringMaster.cropFormat(imagePath) + colorTheme.getSuffix()
             + DEFAULT_ENTITY_IMAGE_FORMAT;
        }
        if (isImage(imgPath)) {
            colorImgCache.get(colorTheme).put(imagePath, imgPath);
            return imgPath;
        }
        noColorTheme.add(imagePath);
        return imagePath;
    }

    public static Image applyStdImage(Image img, STD_IMAGES std_image) {
        Image IMG = std_image.getImage();
        int x = (img.getWidth(null) + IMG.getWidth(null)) / 2;
        int y = (img.getHeight(null) + IMG.getHeight(null)) / 2;
        return applyImage(img, IMG, x, y);

    }

    public static String getImageFolderPath() {
        return PATH;
    }

    public static void setPATH(String pATH) {
        PATH = pATH;
    }

    public static String getValueIconsPath() {
        return VALUE_ICONS_PATH;
    }

    public static boolean isValidImage(Image img) {
        if (img == null) {
            return false;
        }
        return !(img.getWidth(null) <= 0 || img.getHeight(null) <= 0);
    }

    public static boolean isValidIcon(ImageIcon icon) {
        if (icon == null) {
            return false;
        }
        if (icon.getImage() == null) {
            return false;
        }

        return !(icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0);

    }

    public static int getMaxTypeIconSize() {
        return MAX_TYPE_ICON_SIZE;
    }

    public static Image getPrincipleImage(PRINCIPLES principle) {
        if (principle == null) {
            return getImage(VALUE_ICONS_PATH + "Principles\\principles.jpg");
        }
        return getImage(VALUE_ICONS_PATH + "Principles\\" + principle.toString() + ".jpg");
    }

    public static String getMasteryGroupPath(String title) {
        return VALUE_ICONS_PATH + "masteries\\groups\\" + title + ".png";
    }

    public static String getClassGroupPath(String title) {
        return VALUE_ICONS_PATH + "Class groups\\" + title + ".png";
    }
    public static ImageIcon getMasteryGroupIcon(String title) {
        String imgPath = getMasteryGroupPath(title);
        Image image = getNewBufferedImage(32, 32);
        Image masteryIcon = getImage(imgPath);
        if (masteryIcon == null) {
            return getEmptyEmblem();
        }
        // image = applyImage(image, masteryIcon, 4, 4, false);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.drawImage(masteryIcon, -4, -4, 32, 32, 0, 0, 36, 36, observer);
        return new ImageIcon(image);

    }

    public static Image getValueIcon(VALUE value) {
        return getValueIcon(value, false);
    }


    //    public static String getValueIconPath(VALUE value) {
    //        if (value == null) {
    //            return "";
    //        }
    //        Image img = getValueIcon(value);
    //        if (img == null) {
    //            return "";
    //        }
    //        return ((CustomImage) img).getImgPath();
    //
    //    }
    public static String getValueIconPath(VALUE value) {
        String imgPath = VALUE_ICONS_PATH;
        String name = value.getName().toLowerCase();

        boolean mastery = false;
        if (value instanceof MACRO_PARAMS) {
            imgPath = VALUE_ICONS_PATH + "macro\\";
        } else if (value instanceof PARAMETER) {
            PARAMETER parameter = (PARAMETER) value;
            if (parameter.getName().contains(" Durability Mod")) {
                return getDamageTypeImagePath(parameter.getName().replace(" Durability Mod", ""));
            }
            if (parameter.getName().contains(" Armor")) {
                return getDamageTypeImagePath(parameter.getName().replace(" Armor", ""));
            }

            if (parameter.isMastery()) {
                mastery = true;
                imgPath += "masteries\\";
                name = name.replace(" mastery", "");
            } else if (parameter.isAttribute()) {
                imgPath += "attributes\\";
            }

        }
        if (!mastery) {
            name = name.replaceFirst("c ", "");
        }
        String path = imgPath + name + ".png"; // free format
        if (!FileManager.isFile(getImageFolderPath() + path))
            path = imgPath + name + ".jpg";

        return path;

    }

    public static Image getValueIcon(VALUE value, boolean glowIconForDynamicIfAvailable) {
        Image icon;
        String imgPath = VALUE_ICONS_PATH;
        String name = value.getName().toLowerCase();

        boolean mastery = false;
        if (value instanceof MACRO_PARAMS) {
            imgPath = VALUE_ICONS_PATH + "macro\\";
        } else if (value instanceof PARAMETER) {
            PARAMETER parameter = (PARAMETER) value;
            if (parameter.getName().contains(" Durability Mod")) {
                return getDamageTypeImage(parameter.getName().replace(" Durability Mod", ""));
            }
            if (parameter.getName().contains(" Armor")) {
                return getDamageTypeImage(parameter.getName().replace(" Armor", ""));
            }

            if (parameter.isMastery()) {
                mastery = true;
                imgPath += "masteries\\";
                name = name.replace(" mastery", "");
            } else if (parameter.isAttribute()) {
                imgPath += "attributes\\";
            }
            if (glowIconForDynamicIfAvailable) {
                if (parameter.isDynamic()) {
                    if (!parameter.isWriteToType()) {
                        name += " s";
                    }
                }
            }
        }
        if (!mastery) {
            name = name.replaceFirst("c ", "");
        }
        imgPath += name + "."; // free format
        icon = getImage(imgPath);
        if (!isValidImage(icon)) {
            if (mastery) {
                return getImage(getEmptyListIconSmall());
            } else {
                return new CustomImage("", getNewBufferedImage(40, 40));
            }
        }
        return icon;

    }

    public static Image getDamageTypeImage(String enumConstName) {
        return getImage(getDamageTypeImagePath(enumConstName));
    }

    public static String getDamageTypeImagePath(String enumConstName) {
        return getDamageTypeImagePath(enumConstName, false);
    }

    public static String getDamageTypeImagePath(String enumConstName, boolean alpha) {
        if (alpha)
            return (VALUE_ICONS_PATH + "damage types\\" + enumConstName + " alpha.png");
        return (VALUE_ICONS_PATH + "damage types\\" + enumConstName + ".png");
    }

    public static Image getModeImage(String mode, Boolean on_off, boolean blocked) {
        Image image = getImage("ui\\actions\\modes\\" + mode + ".png");
        if (image == null) {
            ObjType type = DataManager.getType(mode, DC_TYPE.ACTIONS);
            if (type != null) {
                image = type.getIcon().getImage();
            }
        }
        if (image == null) {
            return null;
        }
        if (blocked) {
            image = applyBorder(image, BORDER.DARKENING_CIRCLE_50);
        }
        if (on_off) {
            // image = applyBorder(image, BORDER.CIRCLE_GLOW_50);
            image = applyBorder(image, BORDER.CIRCLE_BEVEL);
        }
        return image;
    }

    public static Dimension getImgSize(Image img) {
        if (img == null) {
            return null; // new Dimension(0, 0)
        }
        return new Dimension(img.getWidth(null), img.getHeight(null));
    }

    public static Image getFacingImage(FACING_DIRECTION facing) {
        return new EnumMaster<STD_IMAGES>().retrieveEnumConst(STD_IMAGES.class, facing.name())
         .getImage();
    }

    public static boolean isImageFile(String name) {
        String format = StringMaster.getFormat(name);
        for (String f : STD_FORMATS) {
            if (format.substring(1).equalsIgnoreCase(f)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isImage(String imgPath) {
        if (!FileManager.isImageFile(imgPath))
            return false;
        if (!FileManager.isFile(imgPath))
            if (!FileManager.isFile(getImageFolderPath() + imgPath))
                return false;
        return true;
        //        Image image = getImage(imgPath);
        //        if (image == null) {
        //            return false;
        //        }
        //        if (image.getWidth(null) < 1) {
        //            return false;
        //        }
        //        return image.getHeight(null) >= 1;
    }

    public static String getDefaultEmptyListIcon() {
        return EMPTY_LIST_ITEM;
    }

    public static String getAltEmptyListIcon() {
        return EMPTY_LIST_ITEM_ALT;
    }

    public static String getEmptyListIconSmall() {
        return EMPTY_LIST_ITEM_SMALL;
    }

    public static ImageIcon getSizedVersion(String path, int size) {
        return getSizedIcon(path, new Dimension(size, size));

    }

    public static Image getDefaultCursor() {
        return getImage(DEFAULT_CURSOR);
    }

    public static List<String> getPortraitsForBackground(String property) {
        return getPortraitsForBackground(property, true);
    }

    public static String getRandomHeroPortrait() {
        Loop loop = new Loop(10);
        while (loop.continues()) {
            try {
                String bg = HeroEnums.BACKGROUND.values()[RandomWizard.getRandomIntBetween(0, HeroEnums.BACKGROUND
                 .values().length - 1)].toString();
                List<String> portraitsForBackground = ImageManager.getPortraitsForBackground(bg);
                int index = RandomWizard.getRandomListIndex(portraitsForBackground);
                String image = portraitsForBackground.get(index);
                return image;
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return DEFAULT;
    }

    public static List<String> getPortraitsForBackground(String property, Boolean extended) {
        BACKGROUND bg = new EnumMaster<BACKGROUND>().retrieveEnumConst(BACKGROUND.class, property);
        List<String> list = getPortraitMap(extended).get(bg);
        if (list != null) {
            return list;
        }
        list = new ArrayList<>();
        File folder;
        List<String> paths = new ArrayList<>();

        switch (bg) {
            case DARK_ELF:
                paths.add(PORTRAIT_ROOT_PATH + "elf\\dark");
                paths.add(PORTRAIT_ROOT_PATH + "elf\\drow");
                if (!extended) {
                    break;
                }
                paths.add(PORTRAIT_ROOT_PATH + "elf\\grey");
                break;
            case FEY_ELF:
                paths.add(PORTRAIT_ROOT_PATH + "elf\\fey");
                if (!extended) {
                    break;
                }
                paths.add(PORTRAIT_ROOT_PATH + "elf\\wood");
                paths.add(PORTRAIT_ROOT_PATH + "elf\\ice");
                paths.add(PORTRAIT_ROOT_PATH + "elf\\grey");
                break;
            case GREY_ELF:
                paths.add(PORTRAIT_ROOT_PATH + "elf\\grey");
                if (!extended) {
                    break;
                }
                paths.add(PORTRAIT_ROOT_PATH + "elf\\ice");
                paths.add(PORTRAIT_ROOT_PATH + "elf\\fey");
                paths.add(PORTRAIT_ROOT_PATH + "elf\\dark");
                break;
            case HIGH_ELF:
                paths.add(PORTRAIT_ROOT_PATH + "elf\\high");
                paths.add(PORTRAIT_ROOT_PATH + "elf\\ice");
                break;
            case WOOD_ELF:
                paths.add(PORTRAIT_ROOT_PATH + "elf\\wood");
                // paths.add(PORTRAIT_ROOT_PATH + "elf\\grey");
                paths.add(PORTRAIT_ROOT_PATH + "elf\\fey");
                break;

            case ELF:
                paths.add(PORTRAIT_ROOT_PATH + "elf\\");
                break;
            case VAMPIRE:
                paths.add(PORTRAIT_ROOT_PATH + "vampire\\");
                break;

            case INFERI_WARPBORN:
                paths.add(PORTRAIT_ROOT_PATH + "demon\\succubus");
                break;
            case INFERI_HELLSPAWN:
                paths.add(PORTRAIT_ROOT_PATH + "demon\\SPAWN");
                break;
            case INFERI_CHAOSBORN:
                paths.add(PORTRAIT_ROOT_PATH + "demon\\guard");
                break;
            case NORDHEIMER:
                paths.add(PORTRAIT_ROOT_PATH + "human\\Nordheim");
                break;
            case EASTERLING:
                paths.add(PORTRAIT_ROOT_PATH + "human\\East");
                break;

            case WOMAN_OF_EAGLE_REALM:
            case MAN_OF_EAGLE_REALM:
                paths.add(PORTRAIT_ROOT_PATH + "human\\Eagle Realm");
                if (!extended) {
                    break;
                }
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\4arcane");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\1misc");
                break;
            case WOMAN_OF_GRIFF_REALM:
            case MAN_OF_GRIFF_REALM:
                paths.add(PORTRAIT_ROOT_PATH + "human\\Griff Realm");
                if (!extended) {
                    break;
                }
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\3warrior");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\1misc");
                break;
            case WOMAN_OF_KINGS_REALM:
            case MAN_OF_KINGS_REALM:
                paths.add(PORTRAIT_ROOT_PATH + "human\\King Realm");
                if (!extended) {
                    break;
                }
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\1misc");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\3warrior");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\4arcane");
                break;
            case WOMAN_OF_RAVEN_REALM:
            case MAN_OF_RAVEN_REALM:
                paths.add(PORTRAIT_ROOT_PATH + "human\\Raven Realm");
                if (!extended) {
                    break;
                }
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\4arcane");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\6dark");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\2rogue");
                break;
            case WOMAN_OF_WOLF_REALM:
            case MAN_OF_WOLF_REALM:
                paths.add(PORTRAIT_ROOT_PATH + "human\\Wolf Realm");
                if (!extended) {
                    break;
                }
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\6dark");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\2rogue");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic\\3warrior");
                break;
            case STRANGER:
                paths.add(PORTRAIT_ROOT_PATH + "human\\Generic");
                if (!extended) {
                    break;
                }
                paths.add(PORTRAIT_ROOT_PATH + "human\\King Realm");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Griff Realm");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Eagle Realm");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Raven Realm");
                paths.add(PORTRAIT_ROOT_PATH + "human\\Wolf Realm");
                break;

            case DWARF:
            case WILDAXE_DWARF:
            case STONESHIELD_DWARF:
            case IRONHELM_DWARF:
            case GRIMBART_DWARF:
            case REDBLAZE_DWARF:
            case FROSTBEARD_DWARF:
            case MOONSILVER_DWARF:
                paths.add(PORTRAIT_ROOT_PATH + "dwarf\\");
                break;
            case RED_ORC:
                paths.add(PORTRAIT_ROOT_PATH + "orc\\red");
                paths.add(PORTRAIT_ROOT_PATH + "orc\\orc");
                break;
            case BLACK_ORC:
                paths.add(PORTRAIT_ROOT_PATH + "orc\\black");
                paths.add(PORTRAIT_ROOT_PATH + "orc\\orc");
                break;
            case PALE_ORC:
                paths.add(PORTRAIT_ROOT_PATH + "orc\\pale");
                paths.add(PORTRAIT_ROOT_PATH + "orc\\orc");
                break;
            case GREEN_ORC:
                paths.add(PORTRAIT_ROOT_PATH + "orc\\green");
                paths.add(PORTRAIT_ROOT_PATH + "orc\\orc");
                break;

            default:
                break;

        }

        for (String path : paths) {
            folder = new File(getImageFolderPath() + path);
            path += "\\";
            for (String file : FileManager.listFiles(folder)) {
                if (new File(getImageFolderPath() + path + file).isFile()) {
                    if (!ListMaster.contains(list, file, false)) {
                        list.add(path + file);
                    }
                } else if (new File(getImageFolderPath() + path + file).isDirectory()) {
                    for (String subfile : new File(getImageFolderPath() + path + file).list()) {
                        if (!ListMaster.contains(list, subfile, false)) {
                            list.add(path + file + "\\" + subfile);
                        } else {
                            continue;
                        }
                    }
                }
            }
        }

        getPortraitMap(extended).put(bg, list);
        return list;
    }

    public static List<String> getEmblems(String property) {
        ASPECT A = new EnumMaster<ASPECT>().retrieveEnumConst(ASPECT.class, property);
        String path = EMBLEM_PATH;
        // if (A !=
        if (A == null) {
            A = GenericEnums.ASPECT.NEUTRAL;
        }

        ASPECT[] aspects = new ASPECT[]{A};
        if (A == GenericEnums.ASPECT.NEUTRAL) {
            aspects = new ASPECT[]{GenericEnums.ASPECT.NEUTRAL, GenericEnums.ASPECT.LIGHT, GenericEnums.ASPECT.ARCANUM, GenericEnums.ASPECT.LIFE,
             GenericEnums.ASPECT.DARKNESS, GenericEnums.ASPECT.CHAOS, GenericEnums.ASPECT.DEATH,

            };
        }
        List<String> list = new ArrayList<>();

        for (ASPECT a : aspects) {

            path = EMBLEM_PATH + (a.toString().toLowerCase());
            File folder = new File(PATH + path);

            path += "\\";
            for (String file : FileManager.listFiles(folder)) {
                list.add(path + file);
            }
        }
        return list;
    }

    public static HashMap<BACKGROUND, List<String>> getPortraitMap(Boolean extended) {
        if (extended) {
            if (extendedPortraitMap == null) {
                extendedPortraitMap = new HashMap<>();
            }
            return extendedPortraitMap;
        }
        if (portraitMap == null) {
            portraitMap = new HashMap<>();
        }
        return portraitMap;
    }

    public static Dimension getImageSize(Image image) {
        return getImgSize(image);
    }

    public static Image applyImageNew(Image image, Image image2) {
        try {
            image.getGraphics().drawImage(image2, 0, 0, null);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return image;
    }

    public static BufferedImage getBufferedImage(String imagePath) {
        Image image = getImage(imagePath);
        if (!isValidImage(image)) {
            return null;
        }
        return getBufferedImage(image);
    }

    public static Image getArrowImage(boolean vertical, boolean forward, int version) {
        return getImage(getArrowImagePath(vertical, forward, version));
    }

    public static Image getPreferredAoO_Image() {
        return STD_IMAGES.ATTACK_OF_OPPORTUNITY.getImage();
    }

    public static Image getPreferredCounter_Image() {
        return STD_IMAGES.COUNTER_ATTACK.getImage();
    }

    public static Image getPreferredInstant_Image() {
        return STD_IMAGES.INSTANT_ATTACK.getImage();
    }

    public static Image getGlowFrame(FLAG_COLOR flagColor) {
        return getGlowFrame(flagColor, 132);
    }

    public static Image getGlowFrame(FLAG_COLOR flagColor, int size) {
        return getImage("UI\\Borders\\neo\\color flag\\" + flagColor.toString() + " " + size);
    }

    public static Image applyGlowFrame(Image image, Image frameImage) {
        Image result = getNewBufferedImage(frameImage.getWidth(null), frameImage.getHeight(null));
        int x = (frameImage.getWidth(null) - image.getWidth(null)) / 2;
        int y = (frameImage.getHeight(null) - image.getHeight(null)) / 2;
        result = applyImage(result, image, x, y, false);
        return applyImage(result, frameImage, 0, 0);

    }

    public static Image getDiceIcon(Boolean luck, boolean glow) {
        String luckVariant = "normal";
        if (luck != null) {
            luckVariant = luck ? "good" : "bad";
        }
        String imgPath = "UI\\Components\\small\\dice " + luckVariant;
        if (glow) {
            imgPath += " glow";
        }
        return getImage(imgPath);
    }

    public static BufferedImage getBufferedImage8bit(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        if (!(cm instanceof IndexColorModel)) {
            return image; // sorry...
        }
        IndexColorModel icm = (IndexColorModel) cm;
        WritableRaster raster = image.getRaster();
        int pixel = raster.getSample(0, 0, 0); // pixel is offset in ICM's palette
        int size = icm.getMapSize();
        byte[] reds = new byte[size];
        byte[] greens = new byte[size];
        byte[] blues = new byte[size];
        icm.getReds(reds);
        icm.getGreens(greens);
        icm.getBlues(blues);
        IndexColorModel icm2 = new IndexColorModel(8, size, reds, greens, blues, pixel);
        return new BufferedImage(icm2, raster,
         image.isAlphaPremultiplied(),

         null);
    }

    public static BufferedImage getBufferedImage(Image image) {
        return getBufferedImage(image, 100);
    }

    public static BufferedImage getBufferedImage(Image image, int alpha) {
        if (image.getHeight(null) <= 0 || image.getWidth(null) <= 0) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
        BufferedImage buf = new BufferedImage(image.getWidth(null), image.getHeight(null),
         BufferedImage.TYPE_INT_ARGB);
        if (alpha != 100) {
            Graphics2D g2d = (Graphics2D) buf.getGraphics();

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
             new Float(alpha) / 100));
        }
        buf.getGraphics().drawImage(image, 0, 0, null);
        return buf;
    }

    public static int getGlowOffsetForSize(int size) {
        int factor = MathMaster.round(128 / size);
        return MathMaster.round(6 / factor);
    }

    public static ImageIcon getDefaultTypeIcon(Entity entity) {
        if (entity.getOBJ_TYPE_ENUM() instanceof DC_TYPE) {
            switch ((DC_TYPE) entity.getOBJ_TYPE_ENUM()) {
                case UNITS:
                case CHARS:
                    return getEmptyIcon(GuiManager.getObjSize());
                case BF_OBJ:
                case DUNGEONS:
                    return getEmptyIcon(GuiManager.getFullObjSize());
                case BUFFS:
                    return getEmptyIcon(GuiManager.getTinyObjSize());
            }
        }
        return getEmptyIcon(GuiManager.getSmallObjSize());
    }

    public static Map<Obj, Map<String, ImageIcon>> getCustomIconCache() {
        if (customIconCache == null) {
            customIconCache = new HashMap<>();
        }
        return customIconCache;
    }

    public static BufferedImage getNewBufferedImage(int i, int j) {
        return new BufferedImage(i, j, BufferedImage.TYPE_INT_ARGB);
    }

    public static Image getGlowOverlaidImage(Image icon, Image glow) {
        Image img = ImageManager.getNewBufferedImage(glow.getWidth(null), glow.getHeight(null));
        int x = MigMaster.getCenteredPosition(glow.getWidth(null), icon.getWidth(null)) + 1;
        int y = MigMaster.getCenteredPosition(glow.getHeight(null), icon.getHeight(null)) + 1;
        img = ImageManager.applyImage(img, icon, x, y, false);
        img = ImageManager.applyImage(img, glow, 0, 0, false);
        return img;
    }

    public static String getRadialSpellIconPath() {
        return "UI\\Spellbook.png";
    }

    public static String getFullSizeImage(Entity entity) {
        String format = StringMaster.getFormat(entity.getImagePath());
        String path = StringMaster.cropFormat(entity.getImagePath()) + " full" + format;
        if (isImage(path))
            return path;
        return entity.getImagePath();

    }

    public enum ALIGNMENT {
        NORTH, SOUTH, EAST, WEST, CENTER, NORTH_WEST, SOUTH_EAST, NORTH_EAST, SOUTH_WEST
    }

    public enum BORDER {
        MAP_PARTY_CIRCLE("MAP PARTY CIRCLE.png"),
        GOLDEN("128_golden.png"),

        DEFAULT("default.png"),
        HIGHLIGHTED_256("256 highlight.png"),
        HIGHLIGHTED("HIGHLIGHTED.png") {
            @Override
            public BORDER getBiggerVersion() {
                return HIGHLIGHTED_256;
            }
        },

        CONCEALED("DISABLED.png"),
        FROZEN("FROZEN_1.png"),
        PETRIFIED("PETRIFIED.png"),
        HIGHLIGHTED_96("HIGHLIGHTED_96.png"),
        HIGHLIGHTED_GREEN("HIGHLIGHTED_GREEN.png"),
        HIGHLIGHTED_RED("HIGHLIGHTED_RED.png"),

        HIGHLIGHTED_BLUE("HIGHLIGHTED_BLUE.png"),

        SPELL_BLOCKED("SPELL_BLOCKED.png"),
        SPELL_UNPREPARED("DISABLED.png"),
        SPELL_UNAVAILABLE("SPELL_UNAVAILABLE.png"),
        SPELL_SELECTING("SPELL_SELECTING.png"),
        SPELL_OBLIVIATED("SPELL_OBLIVIATED.png"),
        SPELL_NORMAL("SPELL_NORMAL.png"),
        SPELL_ACTIVATED("SPELL_ACTIVATED.png"),
        SPELL_HIGHLIGHTED("SPELL_HIGHLIGHTED.png"),

        CIRCLE_DEFAULT("CIRCLE_DEFAULT.png"),
        CIRCLE_HIGHLIGHT("CIRCLE_HIGHLIGHT_NEW.png"),
        CIRCLE_HIGHLIGHT_GREEN("CIRCLE_HIGHLIGHT_GREEN.png"),
        CIRCLE_HIGHLIGHT_96("CIRCLE_HIGHLIGHT_NEW_96.png"),
        HIDDEN("HIDDEN.png"),
        SILVER_64("64_SILVER.png"),
        GOLDEN_64("64_GOLDEN.png"),
        IRON_64("64_IRON.png"),
        STEEL_64("64_STEEL.png"),
        PLATINUM_64("64_PLATINUM.png"),
        GOLDEN_PLUS_64("64_GOLDEN_PLUS.png"),
        SPEC_LOCK("1b.png"),
        SPEC_DEAD("1d.png"),
        SPEC_DEAD2("1d2.png"),
        SPEC_Q("1q.png"),
        SPEC_SEARCH("1s.png"),
        BACKGROUND_HIGHLIGHT_32("small\\BACKGROUND_HIGHLIGHT_32.png"),
        DARKENING_32("small\\DARKENING_32.png"),
        DARKENING_CIRCLE_50("circle\\DARKENING_50.png"),
        NEO_INFO_SELECT_HIGHLIGHT_SQUARE_64("neo\\info select 64.png"),
        NEO_INFO_SELECT_HIGHLIGHT("neo\\info select.png"),
        NEO_INFO_SELECT_HIGHLIGHT_SQUARE_96("neo\\info select square 96.png"),
        NEO_INFO_SELECT_HIGHLIGHT_SQUARE_128("neo\\info select square.png"),

        NEO_ACTIVE_SELECT_HIGHLIGHT("neo\\active select.png"),
        NEO_ACTIVE_SELECT_HIGHLIGHT_SQUARE_96("neo\\active select 96.png"),
        NEO_ACTIVE_SELECT_HIGHLIGHT_SQUARE_64("neo\\active select 64.png"),

        NEO_ACTIVE_ENEMY_SELECT_HIGHLIGHT("neo\\active enemy select.png"),
        NEO_ACTIVE_ENEMY_SELECT_HIGHLIGHT_SQUARE_96("neo\\active enemy select 96.png"),
        NEO_ACTIVE_ENEMY_SELECT_HIGHLIGHT_SQUARE_64("neo\\active enemy select 64.png"),

        NEO_PATH_HIGHLIGHT_SQUARE_64("neo\\semihighlight 64.png"),
        NEO_BLUE_HIGHLIGHT_SQUARE_64("neo\\blue 64.png"),
        NEO_CYAN_HIGHLIGHT_SQUARE_64("neo\\cyan 64.png"),
        NEO_PURPLE_HIGHLIGHT_SQUARE_64("neo\\purple 64.png"),
        NEO_WHITE_HIGHLIGHT_SQUARE_64("neo\\white 64.png"),
        NEO_GREEN_HIGHLIGHT_SQUARE_64("neo\\green 64.png"),
        NEO_RED_HIGHLIGHT_SQUARE_64("neo\\red 64.png"),
        RANK_II("neo\\RANK_II.png"),
        RANK_III("neo\\RANK_III.png"),
        RANK_IV("neo\\RANK_IV.png"),
        RANK_V("neo\\RANK_V.png"),

        SHADOW_64("neo\\shadow 64.png"),
        CIRCLE_GLOW_50("circle\\circle glow 50.png"),
        CIRCLE_GLOW_64("circle\\circle glow 64.png"),
        CIRCLE_GLOW_40("circle\\circle glow 40.png"),
        CIRCLE_GLOW_32("circle\\circle glow 32.png"),
        CIRCLE_BEVEL("circle\\circle frame selected.png"),

        // SILVER("silver.png")
        ;
        private String filename;
        private Image img;

        BORDER(String filename) {
            this.setFilename("UI\\Borders\\" + filename);

        }

        public BORDER getBiggerVersion() {
            return null;
        }

        public void initImg() {
            try {
                img = ImageManager.getIcon(filename).getImage();
            } catch (Exception e) {
                img = getNewBufferedImage(1, 1);
                LogMaster.log(1, "Failed to init border! - " + filename);
            }
        }

        public Image getImage() {
            if (img == null) {
                initImg();
            }
            return img;
        }

        public String getImagePath() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }

    public enum HIGHLIGHT {
        TRAVERSED_PATH(BORDER.HIGHLIGHTED),
        DONE(BORDER.CONCEALED),
        AVAILABLE(BORDER.HIGHLIGHTED_GREEN),
        PATH_ORIGIN(BORDER.HIGHLIGHTED),
        TRAVERSED_PATH_FLYING,

        // MACRO

        DESTINATION_AVAILABLE(BORDER.CIRCLE_HIGHLIGHT_GREEN),
        INFO(BORDER.CIRCLE_HIGHLIGHT),
        DEFAULT(BORDER.HIGHLIGHTED),;
        private BORDER border;

        HIGHLIGHT() {

        }

        HIGHLIGHT(BORDER border) {
            this.setBorder(border);
        }

        public BORDER getBorder() {
            return border;
        }

        public void setBorder(BORDER border) {
            this.border = border;
        }
    }

    public enum STD_IMAGES {
        LIGHT("UI\\outlines\\shadows\\light emitter.png"),
        SCROLL_ATTACK_CHOICE("UI\\components\\neo\\attack choice scroll.png"),
        SCROLL_ATTACK_TEXT("UI\\components\\neo\\choose attack.png"),
        SCROLL_END_HORIZONTAL_DOUBLE("UI\\components\\neo\\scroll.png"),

        EYE("UI\\bf\\eye.png"),
        THROW("UI\\components\\small\\throw.png"),
        ATTACK_OF_OPPORTUNITY("ui\\actions\\modes\\misc\\ATTACK OF OPPORTUNITY.png"),
        COUNTER_ATTACK("ui\\actions\\modes\\misc\\COUNTER ATTACK.png"),
        INSTANT_ATTACK("ui\\actions\\modes\\misc\\INSTANT ATTACK.png"),
        WATCHER("ui\\bf\\WATCHER.png"),
        WATCHED("ui\\bf\\WATCHED.png"),
        GUARDIAN("ui\\components\\HC\\GUARDIAN.png"),
        ALIGNMENT("ui\\value icons\\alignment.png"),
        BLOCKED_SIGHT("ui\\bf\\blocked sight.png"),
        COIN("ui\\components\\small\\coin.png"),
        GUARD("ui\\components\\level editor\\guard.png"),
        HAND("ui\\components\\level editor\\hand.png"),
        DEATH("ui\\components\\level editor\\dead.png"),
        SECRET("ui\\components\\level editor\\secret.png"),

        SEARCH("ui\\macro\\filter.png"),
        FOOT("ui\\macro\\foot.png"),
        MOVES("ui\\special\\moves.png"),
        ACTIONS("ui\\special\\actions.png"),
        ATTACKS("ui\\special\\attacks.png"),
        COUNTERS("ui\\special\\counter2.png"),
        TRAP("ui\\special\\trap.jpg"),
        WING("ui\\special\\WING.png"),
        FLAG("ui\\special\\FLAG.png"),
        NORTH("ui\\DIRECTION POINTER NORTH.png"),
        WEST("ui\\DIRECTION POINTER WEST.png"),
        EAST("ui\\DIRECTION POINTER EAST.png"),
        NONE("ui\\DIRECTION POINTER NONE.png"),
        DIRECTION_POINTER("ui\\DIRECTION POINTER.png"),
        SOUTH("ui\\DIRECTION POINTER SOUTH.png"),
        UNKNOWN_UNIT("ui\\Empty.jpg"),
        UNKNOWN_CELL(UNSEEN_CELL),
        KNOWN_CELL(CELL),
        HIGHLIGHTED_CELL(HL_CELL),
        HIDDEN_CELL(CONCEALED_CELL),
        BAG("ui\\components\\small\\bag2.png"),
        MAP_PLACE("ui\\macro\\place3.png"),
        MAP_PLACE_HIGHLIGHTED("ui\\macro\\place3 highlight.png"),
        MAP_PLACE_ALT("ui\\macro\\place2.png"),
        MAP_PLACE_HIGHLIGHTED_ALT("ui\\macro\\place2 highlight.png"),

        BF_BORDER_UP("ui\\custom\\border up.png"),
        BF_BORDER_UP_DARKENED("ui\\custom\\border up.png"),

        BF_BORDER_DOWN("ui\\custom\\border down.png"),
        BF_BORDER_DOWN_DARKENED("ui\\custom\\border down.png"),

        BF_BORDER_DOWN_LEFT("ui\\custom\\border DOWN_LEFT.png"),
        BF_BORDER_DOWN_LEFT_DARKENED("ui\\custom\\border DOWN_LEFT.png"),

        BF_BORDER_DOWN_RIGHT("ui\\custom\\border DOWN_RIGHT.png"),
        BF_BORDER_DOWN_RIGHT_DARKENED("ui\\custom\\border DOWN_RIGHT.png"),

        BF_BORDER_LEFT("ui\\custom\\border LEFT.png"),
        BF_BORDER_LEFT_DARKENED("ui\\custom\\border LEFT.png"),

        BF_BORDER_RIGHT("ui\\custom\\border RIGHT.png"),
        BF_BORDER_RIGHT_DARKENED("ui\\custom\\border RIGHT.png"),

        BF_BORDER_UP_RIGHT("ui\\custom\\border UP_RIGHT.png"),
        BF_BORDER_UP_RIGHT_DARKENED("ui\\custom\\border UP_RIGHT.png"),

        BF_BORDER_UP_LEFT("ui\\custom\\border UP_LEFT.png"),
        BF_BORDER_UP_LEFT_DARKENED("ui\\custom\\border UP_LEFT.png"),
        ZONE_NODE("ui\\components\\new\\zone.jpg"),
        BLINDING_LIGHT("ui\\outlines\\BLINDING_LIGHT.jpg"),
        THICK_DARKNESS("ui\\outlines\\DEEPER_DARKNESS.jpg"),
        ENGAGER("ui\\components\\new\\exclam2.png"),
        ENGAGEMENT_TARGET("ui\\components\\new\\swords.png"),
        HT_LINK_VERTICAL("ui\\components\\ht\\LINK_VERTICAL.png"),
        RANK_COMP("ui\\components\\ht\\rank comp.png"),
        RANK_COMP_DARKENED("ui\\components\\ht\\rank comp dark.png"),
        CROSS("ui\\components\\small\\cross.png"),
        REQ_BLOCKED("ui\\Borders\\neo\\blocked mid.png"),
        REQ_MASTERY("ui\\Borders\\neo\\no rank mid.png"),
        REQ_XP("ui\\Borders\\neo\\no xp mid.png"),
        WALL_DIAGONAL_DOWN_RIGHT("ui\\BF\\WALL_DOWN_RIGHT.png"),
        WALL_DIAGONAL_DOWN_LEFT("ui\\BF\\WALL_DOWN_LEFT.png"),
        WALL_DIAGONAL_UP_RIGHT("ui\\BF\\WALL_UP_RIGHT.png"),
        WALL_DIAGONAL_UP_LEFT("ui\\BF\\WALL_UP_LEFT.png"),
        WALL_HORIZONTAL_LEFT("ui\\BF\\WALL_LEFT.png"),
        WALL_HORIZONTAL_RIGHT("ui\\BF\\WALL_RIGHT.png"),
        WALL_VERTICAL_UP("ui\\BF\\WALL_UP.png"),
        WALL_VERTICAL_DOWN("ui\\BF\\WALL_DOWN.png"),
        WALL_CORNER("ui\\BF\\CORNER SQUARE.png"),
        WALL_CORNER_ROUND("ui\\BF\\CORNER ROUND.png"),
        WALL_CORNER_ALMOND("ui\\BF\\CORNER ALMOND.png"),
        WALL_CORNER_ALMOND_H("ui\\BF\\CORNER ALMOND h.png"),
        WALL_CORNER_ALMOND_V("ui\\BF\\CORNER ALMOND v.png"),
        WALL_CORNER_MESH("ui\\BF\\CORNER MESH.png"),
        WALL_CORNER_DIAMOND("ui\\BF\\CORNER diamond.png"),;

        private String path;
        private Image image;
        private int height;
        private int width;

        STD_IMAGES(String path) {
            this.setPath(path);
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public ImageIcon getIcon() {
            return new ImageIcon(getImage());
        }

        public Image getPathPrefixedImage(String pathPrefix) {
            if (pathPrefix == null) {
                return getImage();
            }
            String name = StringMaster.getLastPathSegment(path);
            return ImageManager.getImage(path.replace(name, "") + pathPrefix + "\\" + name);
        }

        public Image getSuffixedImage(String suffix) {
            if (suffix == null) {
                return getImage();
            }
            String f = StringMaster.getFormat(path);
            return ImageManager.getImage(StringMaster.cropFormat(path) + suffix + f);
        }

        public Image getImage() {
            if (image == null) {
                image = ImageManager.getImage(path);
                if (image == null) {
                    return null;
                }
                height = (image.getHeight(null));
                width = (image.getWidth(null));
            }
            return image;
        }

        public int getWidth() {
            if (width == 0) {
                getImage();
            }
            return width;
        }

        public int getHeight() {
            if (height == 0) {
                getImage();
            }
            return height;
        }


    }

    // public static Image generateValueIcon(Object arg, boolean selected,
    // boolean locked,
    // boolean skill) {
    // // return ImageManager.getValueIcon(param);
    // if (CoreEngine.isArcaneVault())
    // locked = false;
    // Map<Object, Image> cache = valueImgCache;
    // if (locked) {
    // if (selected) {
    // cache = valueImgCacheLockedSelected;
    // } else
    // cache = valueImgCacheLocked;
    // } else if (selected)
    // cache = valueImgCacheSelected;
    //
    // Image valueIcon = skill ? ImageManager.getValueIcon((VALUE) arg) :
    // getClassIcon(
    // (CLASS_GROUP) arg, null);
    // Image img = cache.get(arg);
    // img = getGlowOverlaidImage(selected, locked, valueIcon);
    // // STD_IMAGES.background_highlight_32;
    //
    // cache.put(arg, img);
    // return img;
    // }

}
