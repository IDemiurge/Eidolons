package main.client.cc.gui.neo.tree.logic;

import main.client.cc.gui.neo.tree.logic.TreeMap.LINK_VARIANT;
import main.entity.type.ObjType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StaticTreeLink {
    Point point;
    // determined node pos
    ObjType source;
    List<ObjType> children;
    LINK_VARIANT variant;
    int z;
    private boolean manualSet;

    public StaticTreeLink(LINK_VARIANT variant, Point point, ObjType parent, ObjType type) {
        children = new ArrayList<>();
        children.add(type);
        this.variant = variant;
        // auto-init position?
        this.point = point;
        this.source = parent;

        // initOffsets();
    }

    // int offsetY;
    // int offsetX;
    // int nodeOffsetY;
    // int nodeOffsetX;
    @Override
    public String toString() {
        return variant + " from " + source.getName() + " to " + children;

    }

    // manual vs automatic? some links just don't require additional offset, but
    // let's
    // calc pos in one place with one method!
    //
    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public ObjType getSource() {
        return source;
    }

    public void setSource(ObjType source) {
        this.source = source;
    }

    public List<ObjType> getChildren() {
        return children;
    }

    public void setChildren(List<ObjType> children) {
        this.children = children;
    }

    public LINK_VARIANT getVariant() {
        return variant;
    }

    public void setVariant(LINK_VARIANT variant) {
        this.variant = variant;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean isManualSet() {
        return manualSet;
    }

    public void setManualSet(boolean manualSet) {
        this.manualSet = manualSet;
    }
}
