package main.level_editor.gui.panels.palette.tree;

import main.data.tree.DataNode;
import main.entity.type.ObjType;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class PaletteNode extends DataNode<Object, PaletteNode> {

    public PaletteNode(Object data) {
        this.data = data;
    }
    public PaletteNode(ObjType data) {
        this.data = data;
        leaf = true;
    }

    public PaletteNode(Set<PaletteNode> children, String title) {
        this.data=title;
        this.children = children;
    }

    public void setChildren(PaletteNode... children) {
        this.children = new LinkedHashSet<>(Arrays.asList(children));
    }
    public void setChildrenSet(Set<PaletteNode> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
