package main.level_editor.gui.tree.data;

import java.util.Set;

public   class LE_DataNode< N extends LE_DataNode> extends DataNode<LayeredData, N>{

    public LE_DataNode(LayeredData data) {
        this.data = data;
        leaf=true;
    }

    public LE_DataNode(LayeredData data, Set<N> children) {
        this.data = data;
        this.children = children;
    }

}


