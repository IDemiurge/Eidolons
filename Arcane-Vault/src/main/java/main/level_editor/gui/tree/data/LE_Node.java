package main.level_editor.gui.tree.data;

import java.util.Set;

public   class LE_Node< N extends LE_Node> {

    private boolean leaf;
    private LayeredData data;
    private Set<N> children;

    public LE_Node(LayeredData data) {
        this.data = data;
        leaf=true;
    }

    public LE_Node(LayeredData data, Set<N> children) {
        this.data = data;
        this.children = children;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public LayeredData getData() {
        return data;
    }

    public Set<? extends LE_Node> getChildren() {
        return children;
    }
}


