package main.client.cc.gui.neo.tree.logic;

import main.client.cc.gui.neo.tree.HT_Node;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.entity.type.ObjType;
import main.swing.PointX;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TreeMap {
    // Map<Point, TREE_LINK> linkMap = new XLinkedMap<>();
    private Map<Point, HT_Node> nodeMap = new XLinkedMap<>();
    private Map<Rectangle, HT_Node> mouseMap = new XLinkedMap<>();
    private Dimension dimension;
    private List<ObjType> types;
    private Map<Point, LINK_VARIANT> staticLinks = new XLinkedMap<>();

    private Map<StaticTreeLink, Point> staticLinkMap = new HashMap<>();
    private Map<StaticTreeLink, ObjType> altLinks = new HashMap<>();
    private int nodeSize = 64;

    public TreeMap(List<ObjType> types, int width, int height) {
        dimension = new Dimension(width, height);
        this.types = types;
    }

    public static int getXOffsetForLink(LINK_VARIANT variant) {
        if (variant.getImage() == null) {
            return 0;
        }
        if (variant.isVertical()) {
            return 32 - variant.getImage().getWidth(null) / 2;
        }

        int rankPoolWidth = STD_IMAGES.RANK_COMP.getWidth();
        if (variant.isToLeft()) {
            // right edge at rc's end
            // now left edge at comp's edge

            return -variant.getWidth() + (64 - rankPoolWidth) / 2 + rankPoolWidth;
        } else {
            return (64 - rankPoolWidth) / 2;
        }

    }

    @Override
    public String toString() {
        return "TreeMap: " + nodeMap.toString();
    }

    public Map<Point, HT_Node> getNodeMap() {
        return nodeMap;
    }

    // public Map<Point, TREE_LINK> getLinkMap() {
    // return linkMap;
    // }

    public HT_Node getNodeForType(ObjType targetType) {
        for (Point p : getNodeMap().keySet()) {
            ObjType type = getNodeMap().get(p).getValue();
            if (type == targetType) {
                return getNodeMap().get(p);
            }
        }
        return null;

    }

    public StaticTreeLink getLinkForChildType(ObjType targetType) {
        for (StaticTreeLink p : getStaticLinkMap().keySet()) {
            if (p.getChildren().contains(targetType)) {
                return p;
            }
        }
        return null;
    }

    public StaticTreeLink getLinkForParentType(ObjType targetType) {
        return getLinkForParentType(targetType, false);
    }

    public StaticTreeLink getLinkForParentType(ObjType targetType, boolean searchMultiLink) {
        for (StaticTreeLink link : getStaticLinkMap().keySet()) {
            if (searchMultiLink) {
                if (link.isManualSet()) {
                    continue;
                }
            }
            if (link.getSource() == targetType) {
                return link;
            }
        }
        if (searchMultiLink) {
            for (StaticTreeLink link : getStaticLinkMap().keySet()) {
                if (link.getSource() == targetType) {
                    return link;
                }
            }
        }
        return null;
    }

    public Point getPointForType(ObjType targetType) {
        for (Point p : getNodeMap().keySet()) {
            ObjType type = getNodeMap().get(p).getValue();
            if (type == targetType) {
                return p;
            }
        }
        return null;
    }

    public void addNode(Point point, HT_Node node, int size) {
        HT_Node replaced = getNodeMap().put(new PointX(point.x, point.y), node);
        if (replaced != null) {
            LogMaster.log(1, " !!! " + node + " replaces " + replaced
                    + " !!! ");
        }
        getMouseMap().put(new Rectangle(point.x, point.y, size, size), node);
    }

    public Dimension getDimension() {
        return dimension;
    }

    public Map<Rectangle, HT_Node> getMouseMap() {
        return mouseMap;
    }

    public void setMouseMap(Map<Rectangle, HT_Node> mouseMap) {
        this.mouseMap = mouseMap;
    }

    public List<ObjType> getTypes() {
        return types;
    }

    public Map<Point, LINK_VARIANT> getStaticLinks() {
        return staticLinks;
    }

    public Map<StaticTreeLink, Point> getStaticLinkMap() {
        return staticLinkMap;
    }

    public void setStaticLinkMap(Map<StaticTreeLink, Point> staticLinkMap) {
        this.staticLinkMap = staticLinkMap;
    }

    public Map<StaticTreeLink, ObjType> getAltLinks() {
        return altLinks;
    }

    public List<ObjType> getTypesWithinRange(ObjType type, int i, int sublingCount, int x1, int y1,
                                             int xRange, int yRange) {
        return getTypesWithinRange(type, i, sublingCount, x1, y1, xRange, yRange, null, null, true);
    }

    public List<ObjType> getTypesWithinRange(ObjType type, int i, int sublingCount, int x1, int y1,
                                             int xRange, int yRange, Boolean left_right_both, Boolean up_down_both,
                                             Boolean and_or_xor) {

        List<ObjType> typesOnRow = new LinkedList<>();

        ObjType parent = DataManager.getParent(type);
        for (Point p : getNodeMap().keySet()) {
            ObjType t = getNodeMap().get(p).getType();
            if (t == parent) {
                continue;
            }
            // Point point = map.getPointForType(parent);
            int x = getPointForType(t).x; // ?
            int xDiff = Math.abs(x1 - x);
            boolean xOverlap = xDiff < xRange;
            if (xOverlap) {
                if (BooleanMaster.isFalse(and_or_xor)) {
                    typesOnRow.add(t);
                    continue;
                } else {
                    int y = getPointForType(t).y;
                    int yDiff = Math.abs(y1 - y);

                    // if (up_down_both != null) {
                    // if (up_down_both)
                    // yDiff = y1 - y;
                    // else
                    // yDiff = y - y1; // ??
                    // }

                    boolean yOverlap = yDiff < yRange;
                    if (yOverlap) {
                        if (BooleanMaster.isFalse(and_or_xor)) {
                            typesOnRow.add(t);
                            continue;
                        }
                    }

                    if (BooleanMaster.isTrue(and_or_xor)) {
                        if (yOverlap && xOverlap) {
                            typesOnRow.add(t);
                        }
                    }
                }
            }

            // alternatively, use SD to make different versions of links...
            // shorter/longer
        }
        return typesOnRow;
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public enum LINK_VARIANT {
        ANGLE_TO_LEFT_2,
        ANGLE_TO_LEFT_1,
        ANGLE_TO_LEFT_0,
        ANGLE_TO_LEFT,
        ANGLE_TO_LEFT2,
        ANGLE_TO_LEFT3,
        VERTICAL(true),
        ANGLE_TO_RIGHT3,
        ANGLE_TO_RIGHT2,
        ANGLE_TO_RIGHT,
        ANGLE_TO_RIGHT_0,
        ANGLE_TO_RIGHT_1,
        ANGLE_TO_RIGHT_2,

        HORIZONTAL(false),
        VERTICAL_LONG(true),
        VERTICAL_SHORT(true),

        ANGLE_TO_LEFT_SHORT,
        ANGLE_TO_RIGHT_SHORT,
        VERTICAL_XL(true),
        VERTICAL_XXL(true),

        VERTICAL_THIN(true),
        VERTICAL_LONG_THIN(true),
        VERTICAL_XL_THIN(true),
        VERTICAL_XXL_THIN(true),

        HORIZONTAL_LONG(false),
        HORIZONTAL_SHORT(false);

        Boolean vertical_horizontal;
        int offsetY;
        int offsetX;
        int nodeOffsetY;
        int nodeOffsetX;
        private Image image;
        private Image imageSelected;
        private Image imageBlocked;
        private Image imageDarkened;

        LINK_VARIANT(Boolean vertical_horizontal) {
            this.vertical_horizontal = vertical_horizontal;
        }
        LINK_VARIANT() {

            // this.offsetY = getYOffsetForLink(this);
            // this.offsetX = getXOffsetForLink(this);
            // this.nodeOffsetY = getYOffsetForLink(this);
            // this.nodeOffsetX = getXOffsetForLink(this);
        }
        LINK_VARIANT(int offsetY, int offsetX, int nodeOffsetY, int nodeOffsetX) {
            this.offsetY = offsetY;
            this.offsetX = offsetX;
            this.nodeOffsetY = nodeOffsetY;
            this.nodeOffsetX = nodeOffsetX;
        }

        public int getWidth() {
            return getImage().getWidth(null);
        }

        public int getHeight() {
            return getImage().getHeight(null);
        }

        public Image getImage() {
            if (image == null) {
                image = ImageManager.getImage(getImageFileName());
            }
            return image;

        }

        public Image getSelectedImage() {
            if (imageSelected == null) {
                imageSelected = ImageManager.getImage("UI\\components\\ht\\selected\\" + "LINK_"
                        + name() + " s.png");
            }
            return imageSelected;
        }

        public Image getDarkenedImage() {
            if (imageDarkened == null) {
                imageDarkened = ImageManager.getImage("UI\\components\\ht\\darkened\\" + "LINK_"
                        + name() + " d.png");
            }
            return imageDarkened;
        }

        public Image getAvailableImage() {
            if (imageSelected == null) {
                imageSelected = ImageManager.getImage("UI\\components\\ht\\selected\\" + "LINK_"
                        + name() + " s.png");
            }
            return imageSelected;
        }

        public Image getImage(boolean selected) {
            return selected ? getSelectedImage() : getImage();
        }

        ;

        public String getImageFileName() {
            return "UI\\components\\ht\\" + "LINK_" + name() + ".png";
        }

        public int getXOffset() {
            return (getOffsetMultiplier()) * getImage().getWidth(null);
        }

        public int getOffsetMultiplier() {
            if (isToLeft()) {
                return -1;
            }
            if (isToRight()) {
                return 1;
            }
            return 0;
        }

        public boolean isToLeft() {
            return name().contains("LEFT");
        }

        ;

        public boolean isToRight() {
            return name().contains("RIGHT");
        }

        public int getOffsetY() {
            if (getImage() == null) {
                return 0;
            }
            return -getImage().getHeight(null);
            // return offsetY;
        }

        public int getOffsetX() {
            return getXOffsetForLink(this);
            // return offsetX;
        }

        public int getNodeOffsetY() {
            return -getImage().getHeight(null);
            // return nodeOffsetY;
        }

        public boolean isVertical() {
            if (vertical_horizontal == null) {
                return false;
            }
            return vertical_horizontal;
        }

        public boolean isHorizontal() {
            if (vertical_horizontal == null) {
                return false;
            }
            return vertical_horizontal;
        }

        public int getNodeOffsetX() {
            if (isVertical()) {
                return -32 + getImage().getWidth(null) / 2;
            }

            if (isToLeft()) {
                return -32; // -getImage().getWidth(null) / 2 + 32;
            }
            return getImage().getWidth(null) - 32;
            // getXOffsetForLink(this);
            // return nodeOffsetX;
        }

        public boolean isAutoPos() {
            // TODO Auto-generated method stub
            return false;
        }

    }

}
