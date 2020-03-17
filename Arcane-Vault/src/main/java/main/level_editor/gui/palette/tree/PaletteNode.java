package main.level_editor.gui.palette.tree;

import main.entity.type.ObjType;
import main.level_editor.gui.tree.data.DataNode;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class PaletteNode extends DataNode<Object, PaletteNode> {

    public PaletteNode(String data) {
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

}
