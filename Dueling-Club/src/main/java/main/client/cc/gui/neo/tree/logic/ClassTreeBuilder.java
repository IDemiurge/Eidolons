package main.client.cc.gui.neo.tree.logic;

import main.client.cc.gui.neo.tree.ClassNode;
import main.client.cc.gui.neo.tree.HT_Node;
import main.client.cc.gui.neo.tree.logic.TreeMap.LINK_VARIANT;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.MigMaster;

import java.awt.*;
import java.util.List;

public class ClassTreeBuilder extends HT_MapBuilder {

    protected boolean groupLinkAdded;

    public ClassTreeBuilder(Object arg) {
        super(false, arg);
        marginX = -15;
        defSize = 64;
        marginY = 60;
        maxRows = 4;
        rowGap = (maxHeight - baseLineY - marginY) / maxRows;
    }

    @Override
    protected HT_Node createNode(ObjType type, int nodeSize, ObjType parent) {
        ClassNode node = new ClassNode(type, nodeSize, parent);
        if (isGrouped(type, parent)) {
            node.setGrouped(true);
        }
        return node;
    }

    @Override
    protected void addNodeBranch(ObjType type, int parentX, int i, int sublingCount, ObjType parent) {
        super.addNodeBranch(type, parentX, i, sublingCount, parent);

        addMulticlassNodes(type);
    }

    private void addMulticlassNodes(ObjType type) {
        if (map.getPointForType(type) == null) {
            return;
        }
        boolean multiclass = DC_ContentManager.isMulticlass(type);
        List<ObjType> children = DataManager.getChildren(type, DC_ContentManager
                        .getMulticlassTypes(), (multiclass ? G_PROPS.BASE_TYPE : PROPS.BASE_CLASSES_ONE),
                G_PROPS.NAME);

        int i = 0;
        initGroups(children);
        for (ObjType c : children) {
            if (children.size() == 1) {
                if (!c.checkProperty(PROPS.LINK_VARIANT)) {
                    c.setProperty(PROPS.LINK_VARIANT, LINK_VARIANT.VERTICAL_LONG.toString());
                }
            }
            addNodeBranch(c, map.getPointForType(type).x, i, children.size(), type);
            i++;
            map.getTypes().add(c);
        }

    }

    protected LINK_VARIANT getDefaultLinkVariant() {
        return LINK_VARIANT.VERTICAL_LONG;
    }

    @Override
    protected int getX(int parentX, int i, int sublingCount, ObjType type, ObjType parent) {

        Integer circle = type.getIntParam(PARAMS.CIRCLE);
        if (circle == 0) {
            return MigMaster.getCenteredPosition(maxWidth, defSize);
        }
        int x = parentX;
        // need to preserve the same angle always, right?
        /*
		 * double/tripple nodes or class variants (chaos/dark cultist/monk/priest ... ?)
		 * otherwise, always 2 angles and optionally a middle... 
		 */

        if (isLeftNode(i, sublingCount)) {
            x = parentX - getGapWidth(circle);
            x -= defSize;
        } else if (isRightNode(i, sublingCount)) {
            x = parentX + getGapWidth(circle);

        }
        LogMaster.log(1, x + " X for " + i + "th of " + sublingCount + ""
                + " with size = " + getNodeSize(sublingCount) + "; from parent's " + parentX);
        return x;
    }

    protected int getY(ObjType child, ObjType parent) {

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
    }

    // @Override
    // protected int getY(ObjType child, ObjType parent) {
    //
    // Integer parentCircle = 0;
    // if (parent != null)
    // parentCircle = parent.getIntParam(PARAMS.CIRCLE) + 1;
    // Integer circle = Math.max(parentCircle + 1,
    // child.getIntParam(PARAMS.CIRCLE) + 1);
    // // TODO will overlap if 3 skills same circle...
    // int y = maxHeight - baseLineY - circle * rowGap;
    // main.system.auxiliary.LogMaster.log(1, y + " Y for " + circle +
    // " circle");
    //
    // return y;
    //
    // }

    @Override
    public void initData() {
        super.initData();
    }

    protected boolean isAddLinkPerChild() {
        return true;
    }

    protected int getGapWidth(Integer circle) {
        return 30 + 120 / (circle + 1);
    }

    public boolean isGroupLinkAdded() {
        return groupLinkAdded;
    }

    public void setGroupLinkAdded(boolean groupLinkAdded) {
        this.groupLinkAdded = groupLinkAdded;
    }

    // protected void addLink(ObjType type, ObjType parent, int parentX, int i,
    // int sublingCount) {
    // boolean group = false;
    // if (isGrouped(type, parent)) {
    // if (isGroupLinkAdded()) {
    // setGroupLinkAdded(false);
    // return;
    // }
    // setGroupLinkAdded(true);
    // group = true;
    //
    // }
    // // if (!isMultiNode(type))
    // // check adjacent nodes are grouped and alter
    // // link pos and node pos!
    //
    // LINK_VARIANT variant = LINK_VARIANT.VERTICAL_LONG;
    // Integer circle = type.getIntParam(PARAMS.CIRCLE);
    // String linkName = type.getProperty(PROPS.LINK_VARIANT);
    // boolean linkPos = false;
    // if (linkName.isEmpty()) {
    // if (isLeftNode(i, sublingCount)) {
    // linkName += "ANGLE_TO_LEFT";
    // } else if (isRightNode(i, sublingCount)) {
    // linkName += "ANGLE_TO_RIGHT";
    // }
    // if (group) // more conditions
    // linkName += "-";
    // if (circle > 0 && circle < 4)
    // linkName += circle;
    // } else {
    // linkPos = true;
    // }
    // if (!linkName.isEmpty())
    // variant = new EnumMaster<LINK_VARIANT>()
    // .retrieveEnumConst(LINK_VARIANT.class, linkName);
    // if (variant == null) {
    // variant = LINK_VARIANT.VERTICAL_LONG;
    // }
    // boolean relativeToChild = variant == LINK_VARIANT.ANGLE_TO_LEFT
    // || variant == LINK_VARIANT.ANGLE_TO_LEFT2 || variant ==
    // LINK_VARIANT.ANGLE_TO_LEFT3;
    // // what does this mean? that I am adding a link from parent to child,
    // // and the reverse is needed
    //
    // int x = parentX;
    // if (relativeToChild) {
    // if (map.getPointForType(type) == null) {
    //
    // main.system.auxiliary.LogMaster.log(1, type + " ^^^ is not on map - " +
    // type);
    // } else
    // x = map.getPointForType(type).x;
    // } else {
    // if (map.getPointForType(parent) == null) {
    // main.system.auxiliary.LogMaster.log(1, type +
    // " ^^^ has no parent on map - "
    // + parent);
    // } else
    // x = map.getPointForType(parent).x;
    // }
    // // x = map.getPointForType(relativeToChild ? type : parent).x;
    // int y;
    // if (circle > 0) {
    // if (map.getPointForType(parent) == null) {
    // y = getY(type, parent);
    // main.system.auxiliary.LogMaster.log(1, type +
    // " ^^^ has no parent on map - "
    // + parent);
    // } else
    // y = map.getPointForType(parent).y;
    // } else
    // y = getY(type, parent);
    //
    // if (group) {
    // // alter Y?
    // }
    // // if (isLeftNode(i, sublingCount)) {
    // // x -= defSize;
    // // } not for link, for node!
    // y += HC_Tree.getYOffsetForLink(variant);
    // Point point = new Point(x, y);
    // if (linkPos)
    // linkPosMap.put(parent, point);
    // /*
    // * TODO
    // *
    // * lower corner -> standard offset!
    // *
    // * right/left - defSize offset
    // *
    // *
    // *
    // */
    //
    // // for child! currently, it seems
    // // we're
    // // adding
    // // links 'upwards', let's look into pos
    //
    // map.getStaticLinks().put(point, variant);
    // }
}
