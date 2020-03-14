package main.level_editor.gui.tree.data;

import java.util.Set;

public   class LE_DataNode< N extends LE_DataNode> {

    private boolean leaf;
    private LayeredData data;
    private Set<N> children;

    public LE_DataNode(LayeredData data) {
        this.data = data;
        leaf=true;
    }

    public LE_DataNode(LayeredData data, Set<N> children) {
        this.data = data;
        this.children = children;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public LayeredData getData() {
        return data;
    }

    public Set<? extends LE_DataNode> getChildren() {
        return children;
    }
}


