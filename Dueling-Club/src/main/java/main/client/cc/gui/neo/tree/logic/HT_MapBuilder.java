package main.client.cc.gui.neo.tree.logic;

import main.client.cc.gui.neo.tree.HT_Node;
import main.client.cc.gui.neo.tree.logic.TreeMap.LINK_VARIANT;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.ability.construct.VariableManager;
import main.entity.type.ObjType;
import main.game.battlefield.PointX;
import main.system.SortMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.MathMaster;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HT_MapBuilder {
    public static final int defTreeHeight = 665;
    public static final int defTreeWidth = 475;
    protected int columnLimit;
    protected int maxHeight;
    protected int maxWidth;
    // Map<Point, TREE_LINK> linkMap;

    protected boolean personal;
    protected boolean skill;
    protected TreeMap map;
    protected LinkedList<ObjType> data;
    protected OBJ_TYPE TYPE;
    protected int maxRows = 5;
    protected int[] columns = {1, 3, 2, 0, 4}; // equal split? or specify x?
    protected int marginX = -15; // account on add() instead?
    // protected int defSize = 64;
    protected int defSize = 64;
    protected int baseLineY; // = 265;
    protected int marginY = 60;
    protected int rowGap;
    protected Object arg;
    protected Map<String, List<ObjType>> groupsMap = new HashMap<>();
    protected int column = 0;
    protected Map<ObjType, Point> linkPosMap = new HashMap<>();
    Map<ObjType, Point> groupNodePosMap = new XLinkedMap<>();
    private Map<ObjType, ObjType> alteredTypeLinkMap = new HashMap<>();
    private boolean autoAdjustmentOn;
    private boolean customPos;

    // protected int[] xPoints = { 111, 2, 3, 32, 4};

    public HT_MapBuilder(boolean skill, Object arg) {
        this(0, defTreeHeight, defTreeWidth, skill, false, arg);
    }

    public HT_MapBuilder(int baseLineY, int maxHeight, int maxWidth, boolean skill,
                         boolean personal, Object arg) { // PARAM
        this.arg = arg;
        this.baseLineY = baseLineY;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        rowGap = (maxHeight - baseLineY - marginY) / maxRows;
        this.skill = skill;
        TYPE = getTYPE();
        this.personal = personal;
        column = 0;
    }

    public static LINK_VARIANT getMoreHorizontal(LINK_VARIANT variant) {
        return getShiftedLinkVariant(variant, false);

    }

    public static LINK_VARIANT getMoreVertical(LINK_VARIANT variant) {
        return getShiftedLinkVariant(variant, true);
    }

    public static LINK_VARIANT getShiftedLinkVariant(LINK_VARIANT variant, boolean moreVertical) {
        if (variant == LINK_VARIANT.HORIZONTAL) {
            if (moreVertical) {
                return LINK_VARIANT.ANGLE_TO_LEFT;
            }
            return LINK_VARIANT.ANGLE_TO_RIGHT;
        }
        if (variant == LINK_VARIANT.VERTICAL) {
            if (!moreVertical) {
                return LINK_VARIANT.ANGLE_TO_LEFT;
            }
            return LINK_VARIANT.ANGLE_TO_RIGHT;
        }
        int i = new EnumMaster<LINK_VARIANT>().getEnumConstIndex(variant);
        int n = moreVertical ? 1 : -1;
        if (variant.isToRight()) {
            i -= n;
        } else if (variant.isToLeft()) {
            i += n;
        }
        if (i < 0) {
            if (!moreVertical) {
                return LINK_VARIANT.HORIZONTAL;
            }
        }

        if (i >= LINK_VARIANT.values().length) {
            return null;
        }
        LINK_VARIANT link = LINK_VARIANT.values()[i];
        // if (link == LINK_VARIANT.VERTICAL || link ==
        // LINK_VARIANT.VERTICAL_LONG) {
        if (!(link.isToRight() || link.isToLeft())) {
            // check LONG/s TODO
            if (!moreVertical) {
                return LINK_VARIANT.HORIZONTAL;
            }
            return LINK_VARIANT.VERTICAL;
        }
        return link;
    }

    public static LINK_VARIANT getPresetLink(ObjType type) {
        return new EnumMaster<LINK_VARIANT>().retrieveEnumConst(LINK_VARIANT.class, type
                .getProperty(PROPS.LINK_VARIANT));
    }

    public static void sortDefault(List<ObjType> children) {
        // auto-init ids?

        // idea: have default id like 0, and set +/-x to go left/right
        // idea: init default ids per type#/circle ; leave space, e.g. to insert
        // between
        LogMaster.log(1, ">>>>>>> before: " + children);
        SortMaster.sortById(children);
        LogMaster.log(1, ">>>>>>> after: " + children);
    }

    private OBJ_TYPES getTYPE() {
        return (skill) ? OBJ_TYPES.SKILLS : OBJ_TYPES.CLASSES;
    }

    public TreeMap build() {
        initData();
        map = new TreeMap(data, maxWidth, maxHeight);
        // first, break the types into groups perhaps!
        List<ObjType> rootTypes = DataManager.getRootTypes(data);
        List<ObjType> customPosTypes = getCustomPosTypes(rootTypes);
        rootTypes.removeAll(customPosTypes);
        initGroups(data);
        sortDefault(rootTypes);
        LogMaster.log(1, "Building tree for " + arg + "... \n rootTypes== "
                + rootTypes);
        // TODO next row of root types!
        if (rootTypes.size() > 3) {
            if (rootTypes.size() > 4) {
                marginX = 38;
            } else {
                marginX = 28;
            }
        }
        for (ObjType rootType : rootTypes) {

            int x = marginX + columns[column] * defSize * 2 - defSize / 2;
            if (column == 3) {
                x += 30;
            } else if (column == 4) {
                x -= 30;
            }
            addNodeBranch(rootType, x, column, 0, null);
            column++;

            if (column >= maxRows) {
                column = 0;
                // TODO check row has enough free Y space for this skill-line
            }
            // y still determined by sd? perhaps not always, some *aligning*
            // would be good only non-basic roots?
        }

        for (ObjType customPosType : customPosTypes) {
            setCustomPos(true);
            int x = customPosType.getIntParam(PARAMS.HT_CUSTOM_POS_X);
            addNodeBranch(customPosType, x, -1, -1, null);
            // addNode(parentX, type, i, sublingCount, parent)
        }
        addAltBaseLinks();

        LogMaster.log(1, "Tree built for " + arg + ": \n " + map);
        return map;

		/*
         * let links have offset space within 64 pixels!
		 *
		 * re-build map with random root-sorting ?
		 * save best order...
		 *
		 * after-adjustment:
		 * >> changing links will affect children... unless I automatically balance with Short Link (and long for the 'neighbors')
		 *
		 *
		 *
		 *
		 *
		 */

    }

    private void setCustomPos(boolean b) {
        customPos = b;
    }

    private List<ObjType> getCustomPosTypes(List<ObjType> rootTypes) {
        List<ObjType> list = new LinkedList<>();
        for (ObjType t : rootTypes) {
            if (t.checkParam(PARAMS.HT_CUSTOM_POS_X) || t.checkParam(PARAMS.HT_CUSTOM_POS_Y)) {
                list.add(t);
            }
        }
        return list;
    }

    protected void addNodeBranch(ObjType root, int parentX, int i, int sublingCount, ObjType parent) { // int
        int newParentX = addNode(parentX, root, i, sublingCount, parent);
        List<ObjType> children = DataManager.getChildren(root, data);
        if (children.isEmpty()) {
            return;
        }
        i = 0;
        sortDefault(children);

        LogMaster.log(1, root.getName() + " has children: " + children);
        for (ObjType child : children) {
            sublingCount = getSublingCount(children);
            addNodeBranch(child, newParentX, i, sublingCount, root);
            if (sublingCount < children.size()) {
                if (isRowPosIgnored(child)) {
                    continue;
                }
            }
            i++;
        }
    }

    private int getSublingCount(List<ObjType> children) {
        int c = 0;
        List<Object> groups = new LinkedList<>();
        for (ObjType child : children) {
            if (isRowPosIgnored(child)) {
                continue;
            }

            String group = child.getProperty(PROPS.TREE_NODE_GROUP);
            if (!groups.contains(group)) {
                c++;
                if (!group.isEmpty()) {
                    groups.add(child);
                }
            }
        }
        return c;
    }

    private boolean isRowPosIgnored(ObjType child) {
        if (isAddLinkPerChild()) {
            return false;
        }
        LINK_VARIANT presetLink = getPresetLink(child);
        if (presetLink != null) {
            if (!presetLink.isVertical()) {
                return true;
            }
        }
        return false;
    }

    protected int addNode(int parentX, ObjType type, int i, int sublingCount, ObjType parent) {
        addLink(parentX, type, i, sublingCount, parent);
        Point point;
        if (parent == null) {
            // getBasePoint();
            point = new PointX(getX(parentX, i, sublingCount, type, parent), getY(type, parent));
            LogMaster.log(1, "Base Node added: " + "" + "" + type.getName()
                    + " at " + point);
        } else {
            StaticTreeLink link = getStaticLink(type);
            point = getNodePointFromLink(parentX, type, i, sublingCount, parent, link);

            LogMaster.log(1, "Node added: " + "" + type.getName() + " at "
                    + point + " with " + link);
        }
        HT_Node node = createNode(type, defSize, parent);
        map.addNode(point, node, defSize);

        addAltBaseLinks(point, node);

        return point.x;
    }

    private void addAltBaseLinks(Point point, HT_Node node) {
        ObjType type = node.getType();
        String prop = type.getProperty(PROPS.ALT_BASE_LINKS);
        if (prop.isEmpty()) {
            return;
        }
        StaticTreeLink link = getStaticLink(type);
        for (String s : StringMaster.openContainer(prop)) {
            addAltBaseLink(link, s, type, point);
        }

    }

    private void addAltBaseLinks() {
        for (HT_Node c : map.getNodeMap().values()) {
            String prop = c.getType().getProperty(PROPS.ALT_BASE_LINKS);
            if (prop.isEmpty()) {
                continue;
            }

            for (String s : StringMaster.openContainer(prop)) {
                String typeName = VariableManager.removeVarPart(s);
                String vars = VariableManager.getVar(s);
                LINK_VARIANT variant = new EnumMaster<LINK_VARIANT>().retrieveEnumConst(
                        LINK_VARIANT.class, vars.split("=")[0]);
                ObjType targetType = DataManager.getType(typeName, getTYPE());
                Point point = map.getPointForType(c.getType());

                // getPoint(variant, parent, type, parentX, i, sublingCount)
                // point = getNodePointFromLink(point2.x, targetType, 0, 0,
                // c.getType(), variant);

				/*
				 * use the variant.getOffset()
				 *
				 * what point is base?
				 *
				 * c.
				 *
				 */

                int offsetX = StringMaster.getInteger(vars.split("=")[1].split(",")[0]);
                int offsetY = StringMaster.getInteger(vars.split("=")[1].split(",")[1]);
                point = new PointX(point.x + offsetX, point.y + offsetY);

                StaticTreeLink link = new StaticTreeLink(variant, point, targetType, c.getType());
                // link.setAlt(true); // what will it do?
                map.getAltLinks().put(link, targetType);

            }
        }

    }

    private void addAltBaseLink(StaticTreeLink link, String s, ObjType type2, Point point) {
        // map.getStaticLinkMap().put(link, p);
    }

    private Point getNodePointFromLink(int parentX, ObjType type, int i, int sublingCount,
                                       ObjType parent, StaticTreeLink link) {
        Point point;
        LINK_VARIANT variant = link.getVariant();
        point = link.getPoint();
        Point originalPoint = new Point(point.x, point.y);
        boolean adjusted = false;
        if (isAutoAdjustmentOn()) {
            if (parent != null) {
                while (adjustLink(point, link, parent, type, parentX, i, sublingCount)) {
                    adjusted = true; // link point or node point?
                    point = new PointX(link.getPoint().x + variant.getNodeOffsetX(), link
                            .getPoint().y - 64);// getPoint(link.getVariant(),
                    // parent, type, parentX, i,
                    // sublingCount); //
                    // unnecessary?
                }
            }
        }
        if (adjusted) {
            LogMaster.log(1, "^v^ " + type.getName() + " adjusted to "
                    + variant + " from " + link.getVariant());
            LogMaster.log(1, "^v^ " + type.getName() + " adjusted to "
                    + point + " from " + originalPoint);
            variant = link.getVariant();
            map.getStaticLinkMap().put(link,
                    getPoint(variant, parent, type, parentX, i, sublingCount));
        }

        int x = point.x + variant.getNodeOffsetX();// +getGroupNodeOffsetX(type,
        // link.getChildren());
        if (!isAddLinkPerChild()) {
            if (link.getVariant().isVertical() || (!link.isManualSet())) {
                x = getX(parentX, i, sublingCount, type, parent);
            }
        }
        int y = point.y - 64; // + variant.getNodeOffsetY();

        Point groupNodeOffset = groupNodePosMap.get(type);
        if (groupNodeOffset != null) {
            x += groupNodeOffset.x;
            y += groupNodeOffset.y;
        }

        x += type.getIntParam(PARAMS.TREE_NODE_OFFSET_X);
        y += type.getIntParam(PARAMS.TREE_NODE_OFFSET_Y);

        int finalX = MathMaster.getMinMax(x, 0, defTreeWidth - 64);
        int finalY = MathMaster.getMinMax(y, 0, defTreeHeight - 64);
        // int diffY = finalY-y;
        int abs = Math.abs(finalX - x);
        if (!customPos) {
            if (abs > 0) {
                if (finalY == y) { // just limit the diff!

                    LogMaster.log(1, type.getName()
                            + " (x adjustment) -> Y decreased by " + abs);
                    finalY += abs; // increase or decrease Y for x-change?
                }
            }
        }

        point = new PointX(finalX, finalY);

        return point;
    }

    private boolean isAutoAdjustmentOn() {
        return autoAdjustmentOn;
    }

    private boolean adjustLink(Point point, StaticTreeLink link, ObjType parent, ObjType type,
                               int parentX, int i, int sublingCount) {
        int v = 0;
        int h = 0;
        if (point.x > defTreeWidth - defSize || point.x < 0) {
            if (point.x < 0) {
                v += Math.abs(point.x);
            } else {
                v += point.x - (defTreeWidth - defSize);
            }
        }
        if (point.y > defTreeHeight - defSize || point.y < 0) {
            // if (point.x < 0)
            // v += Math.abs(point.x);
            // else
            // v += point.x - (defTreeWidth - defSize);
        }
        List<ObjType> neighbors = map.getTypesWithinRange(type, i, sublingCount, point.x, point.y,
                defSize, 64);

        // offset for neighbors to determine optimal adjustment!
        if (neighbors.size() > 0) // make sure it's not cyclic -
        // "left - right - left - right"
        {
            List<ObjType> neighborsH = map.getTypesWithinRange(type, sublingCount, sublingCount,
                    point.x + 22, point.y - 22, 64, 64); // flexible numbers!!!
            List<ObjType> neighborsV = map.getTypesWithinRange(type, sublingCount, sublingCount,
                    point.x - 22, point.y + 22, 64, 64);
            // getMoreVertical(link.getVariant()).getNodeOffsetX() difference...

            LogMaster.log(1, neighborsH + " vs " + neighborsV);
            v += neighborsV.size() * 16;
            h += neighborsH.size() * 16;
            // if (neighborsH.size() > neighborsV.size()) {
            // return adjustLink(link, false);
            // }
            // return adjustLink(link, true);
        }
        // perhaps both? return |
        LogMaster.log(1, "h= " + h + "; v= " + v);
        if (((v - h) > 15)) {
            LogMaster.log(1, type.getName() + " adjusting vertically... "
                    + point);
            return adjustLink(link, true);
        }
        if (((v - h) < -15)) {
            LogMaster.log(1, type.getName() + " adjusting horizontally... "
                    + point);
            return adjustLink(link, false);
        }

        // List<ObjType> neighborsY =
        // getTypesWithinRange(type, sublingCount, sublingCount,
        // Integer.MAX_VALUE,defSize);

        return false;
    }

    public List<ObjType> getTypesWithinRange(ObjType type, int i, int sublingCount, int xMaxRange,
                                             int yMaxRange) {
        ObjType parent = DataManager.getParent(type);
        int y1 = getY(type, parent);
        Point pointForType = parent == null ? new PointX(getX(0, i, sublingCount, type, parent), y1) // TODO
                : map.getPointForType(parent); // ???
        int x1 = getX(pointForType.x, i, sublingCount, type, parent);

        return map.getTypesWithinRange(type, i, sublingCount, x1, y1, xMaxRange, yMaxRange);
    }

    private boolean adjustLink(StaticTreeLink link, boolean moreVertical) {
        LINK_VARIANT variant = getShiftedLinkVariant(link.getVariant(), moreVertical);
        LogMaster.log(1, "***** vertical= " + moreVertical
                + " => adjusted to " + variant + "  [" + link);
        if (variant == null) {
            return false;
        }
        if (variant == link.getVariant()) {
            return false;
        }
        link.setVariant(variant);
        return true;
    }

    // private Point adjustPoint(Point point, StaticTreeLink link, ObjType
    // parent, ObjType type,
    // int parentX, int i, int sublingCount) {
    // if (point.x > defTreeWidth - defSize || point.x < 0) {
    // LINK_VARIANT variant = getMoreVertical(link.getVariant());
    // getPoint(variant, parent, type, parentX, i, sublingCount);
    // link.setVariant(variant);
    // }
    // if (point.y > defTreeHeight - defSize || point.y < 0) {
    // }
    // int yDiff;
    // return point;
    // }
    protected void addLink(int parentX, ObjType type, int i, int sublingCount, ObjType parent) {
        // TODO multiple
        // if (groupNodePosMap.containsKey(type)){
        // }
        if (parent == null) {
            return;
        }

        LINK_VARIANT variant = getPresetLink(type);

        if (!isAddLinkPerChild()) {
            if (i > 0) {
                if (variant == null) {
                    StaticTreeLink link = map.getLinkForParentType(parent, true);
                    link.getChildren().add(type);
                    // TODO add child!
                    return;
                }
            }
        }

        if (isGrouped(type, parent)) {
            for (StaticTreeLink link : map.getStaticLinkMap().keySet()) {
                // if (link.getSource() == parent) {
                if (StringMaster.compareByChar(link.getChildren().get(0).getProperty(
                        PROPS.TREE_NODE_GROUP), type.getProperty(PROPS.TREE_NODE_GROUP))) {
                    link.getChildren().add(type);
                    LogMaster.log(1, "Group Type added: " + "" + ""
                            + type.getName() + " for " + link);
                    return;
                }
            }
            if (variant == null) {
                variant = getDefaultLinkVariant();
            }

        } else {
            variant = getLinkVariant(type, i, sublingCount);
        }
        Point point = getPoint(variant, parent, type, parentX, i, sublingCount);
        StaticTreeLink link = new StaticTreeLink(variant, point, parent, type);
        if (getPresetLink(type) == variant) {
            link.setManualSet(true);
        }
        map.getStaticLinkMap().put(link, point);

        LogMaster.log(1, "LINK added: " + variant + " FOR " + type.getName()
                + " at " + point);

    }

    protected Point getPoint(LINK_VARIANT variant, ObjType parent, ObjType type, int parentX,
                             int i, int sublingCount) {
        if ((variant.isAutoPos()) || parent == null) {
            return new PointX(getX(parentX, i, sublingCount, type, parent), getY(type, parent));
        }
        int x = parentX + variant.getOffsetX();
        int y = map.getPointForType(parent).y + variant.getOffsetY();
        x += type.getIntParam(PARAMS.TREE_LINK_OFFSET_X);
        y += type.getIntParam(PARAMS.TREE_LINK_OFFSET_Y);
        return new PointX(x, y);
    }

    protected boolean isLeftNode(int i, int sublingCount) {
        return sublingCount > 1 && i == 0;
    }

    protected boolean isRightNode(int i, int sublingCount) {
        return i == 2 || (sublingCount == 2 && i == 1);
    }

    protected LINK_VARIANT getLinkVariant(ObjType type, int i, int sublingCount) {
        LINK_VARIANT variant = getDefaultLinkVariant();
        Integer circle = type.getIntParam(PARAMS.CIRCLE);
        String linkName = type.getProperty(PROPS.LINK_VARIANT);
        if (linkName.isEmpty()) {
            if (!isAddLinkPerChild()) {
                List<ObjType> typesOnRow = getTypesWithinRange(type, sublingCount, sublingCount,
                        64, 64);
                ObjType parent = DataManager.getParent(type);
                if (typesOnRow.size() >= 1) {
                    int index = 0;
                    while (index < typesOnRow.size()) {
                        if (alteredTypeLinkMap.containsKey(DataManager.getParent(typesOnRow
                                .get(index)))) {
                            return LINK_VARIANT.VERTICAL; // TODO short? if any
                        }
                        // in
                        // line are long!
                        index++;
                    }

                    LogMaster.log(1, type.getName()
                            + "*!* Link made LONG! - " + typesOnRow);
                    alteredTypeLinkMap.put(parent, type);
                    return LINK_VARIANT.VERTICAL_LONG;
                }
            } else {
                if (sublingCount > 1) {
                    if (sublingCount % 2 == 1) {
                        if ((i == sublingCount / 2)) {
                            variant = getDefaultLinkVariant(); // getSpecialLink?
                        } else if (i < sublingCount / 2) {
                            linkName += "ANGLE_TO_LEFT";
                        } else {
                            linkName += "ANGLE_TO_RIGHT";
                        }
                    } else if (i % 2 == 0) {
                        linkName += "ANGLE_TO_LEFT";
                    } else {
                        linkName += "ANGLE_TO_RIGHT";
                    }
                }
                if (!linkName.isEmpty()) {
                    if (circle > 0 && circle < 4) {
                        linkName += circle;
                    }
                }
            }
        }
        if (!linkName.isEmpty()) {
            variant = new EnumMaster<LINK_VARIANT>()
                    .retrieveEnumConst(LINK_VARIANT.class, linkName);
        }
        if (variant == null) {
            variant = getDefaultLinkVariant();
        }
        return variant;
    }

    protected LINK_VARIANT getDefaultLinkVariant() {
        return LINK_VARIANT.VERTICAL;
    }

    protected void initGroupNodePos(List<ObjType> group) {
        int defaultOffsetX = Math.min(-4, -10 + group.size());
        int defaultOffsetY = Math.min(-8, -16 + 2 * group.size());
        int nodeOffsetX = Math.max(5, 12 - 2 * group.size());
        int nodeOffsetY = Math.max(9, 22 - 3 * group.size());
        int i = 0;
        for (ObjType t : group) {
            groupNodePosMap.put(t, new Point(defaultOffsetX + nodeOffsetX * i, defaultOffsetY
                    + nodeOffsetY * i));
            i++;
        }

    }

    protected StaticTreeLink getStaticLink(ObjType type) {
        for (StaticTreeLink s : map.getStaticLinkMap().keySet()) {
            if (s.getChildren().contains(type)) {
                return (s);
            }
        }

        if (!isAddLinkPerChild()) {
            ObjType parent = DataManager.getParent(type);
            ObjType typeAltered = alteredTypeLinkMap.get(parent);
            if (typeAltered != null)
                // return alteredLink;
            {
                for (StaticTreeLink s : map.getStaticLinkMap().keySet()) {
                    if (s.getChildren().contains(typeAltered)) {
                        return s;
                    }
                }
            }

            for (StaticTreeLink s : map.getStaticLinkMap().keySet()) {
                if (s.getSource() == parent) {
                    return s;
                }
            }
        }

        return null;
    }

    protected HT_Node createNode(ObjType type, int nodeSize, ObjType parent) {
        HT_Node node = new HT_Node(type, nodeSize, parent);
        if (isGrouped(type, parent)) {
            node.setGrouped(true);
        }
        return node;
    }

    protected int getX(int parentX, int i, int sublingCount, ObjType type, ObjType parent) {
        int x = parentX;
        if (sublingCount > 1) {
            x = x + i * getNodeSize(sublingCount) - defSize / 2;
        }
        LogMaster.log(1, x + " X for " + i + "th of " + sublingCount + ""
                + " with size = " + getNodeSize(sublingCount) + "; from parent's " + parentX);
        return x;
    }

    protected int getNodeSize(int sublingCount) {
        return defSize;
    }

    protected int getY(ObjType child, ObjType parent) {

        if (customPos) {
            return child.getIntParam(PARAMS.HT_CUSTOM_POS_Y);
        }
        Integer parentCircle = 0;
        Point parentPosSpecial = null;
        if (parent != null) {
            parentPosSpecial = linkPosMap.get(parent);
            if (parentPosSpecial != null) {
                parentCircle = 0;
            } else {
                parentCircle = parent.getIntParam(PARAMS.CIRCLE) + 1;
            }
        }

        Integer circle = Math.max(parentCircle + 1, child.getIntParam(PARAMS.CIRCLE) + 1);
        // TODO will overlap if 3 skills same circle...
        int y = maxHeight - baseLineY - circle * rowGap;
        LogMaster.log(1, y + " Y for " + circle + " circle");
        if (parentPosSpecial != null) {
            y = maxHeight - baseLineY - 1 * rowGap + parentPosSpecial.y;
            LogMaster.log(1, y + " replaces Y;  from parent: "
                    + parent.getName());
        }
        return y;

        // aren't margins already counted via coef? + marginY

        // return baseLineY+ circle * yCoef;
        // map Y per column
        // sd offset
    }

    protected void initGroups(List<ObjType> data) {
        // use for the whole of Data or for each Children Group?

        for (ObjType c : data) {
            // init groups
            String group = c.getProperty(PROPS.TREE_NODE_GROUP);
            if (group.isEmpty()) {
                continue;
            }
            List<ObjType> types = groupsMap.get(group);
            if (types == null) {
                types = new LinkedList<>();
                groupsMap.put(group, types);
            }
            types.add(c);
        }

        // List<ObjType> workList = new LinkedList<>(children);
        for (String group : groupsMap.keySet()) {
            initGroupNodePos(groupsMap.get(group));
            // sort within each group
            // go through groups and take first element, add by lowest id

            // int id = c.getIntParam(params.id);
            // prevIndex = children.indexOf(o);
            // children.set(index, element);

        }
        // append groups
    }

    protected boolean isGrouped(ObjType type, ObjType parent) {
        if (groupNodePosMap.containsKey(type)) {
            return true;
        }
        String group = type.getProperty(PROPS.TREE_NODE_GROUP);
        if (group.isEmpty()) {
            return false;
        }

        for (ObjType t : DataManager.getChildren(parent, data)) {
            if (t.checkProperty(PROPS.TREE_NODE_GROUP, group)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isAddLinkPerChild() {
        return false;
    }

    public void initData() {
        List<ObjType> list = null;
        String group = arg.toString();
        if (!personal) {

            list = DataManager.getTypesSubGroup(TYPE, group);

        } else {
            // TODO
        }
        data = new LinkedList<>(list);

        // HT_DataManager.getTypeGroups(data);
        // column per BASE_TYPE? TODO
        // for (){
        // getGroupConditions();
        // FilterMaster.getFilteredList(c, data);
        // }
        // estimate required space in each row? then either place as is or
        // shuffle
    }
    // protected void addLink(ObjType type, ObjType parent, int parentX, int i,
    // int sublingCount) {
    // LINK_VARIANT link = new
    // EnumMaster<LINK_VARIANT>().retrieveEnumConst(LINK_VARIANT.class,
    // type.getProperty(PROPS.LINK_VARIANT));
    // if (link == null)
    // link = LINK_VARIANT.VERTICAL;
    // map.getStaticLinks().put(new Point(
    // // getX(parentX, i, sublingCount, type, parent)
    // parentX, getY(type, null)), link);
    // }
    // protected int addNode(int parentX, ObjType type, int i, int sublingCount,
    // ObjType parent) {
    // int x = getX(parentX, i, sublingCount, type, parent);
    // int nodeSize = getNodeSize(sublingCount);
    // HT_Node node = createNode(type, nodeSize);
    // Point point = new PointX(x, getY(type, parent));
    // if (parent != null) {
    // if (groupNodePosMap.containsKey(type)) {
    // point = groupNodePosMap.getOrCreate(type);
    // main.system.auxiliary.LogMaster.log(1, "***groupNodePos: " + "" + ""
    // + node
    // + " at " + point);
    // } else {
    // LINK_VARIANT link = new EnumMaster<LINK_VARIANT>().retrieveEnumConst(
    // LINK_VARIANT.class, type.getProperty(PROPS.LINK_VARIANT));
    // if (link != null) {
    // Image img = ImageManager.getImage(link.getImageFileName());
    // point = new PointX(parentX + link.getXOffset()
    // + (link.isToRight() ? defSize : 0)
    // // from the other corner...
    // + link.getOffsetMultiplier() * HC_Tree.getXOffsetForLink(link)
    // // additional offset - not always from corner!!!
    // , map.getPointForType(parent).y - img.getHeight(null)
    // + HC_Tree.getYOffsetForLink(link));
    //
    // main.system.auxiliary.LogMaster.log(1, "***LINK_VARIANT pos: " + "" +
    // "" + node
    // + " at " + point);
    //
    // }
    // }
    //
    // x = point.x;
    // }
    //
    // // lastNode = node;
    // map.addNode(point, node, nodeSize);
    // main.system.auxiliary.LogMaster.log(1, "Node added: " + "" + "" +
    // node + " at " + point);
    // return x;
    // }

}
