package main.data.tree;

import java.util.Set;

public   class StructNode  extends DataNode<LayeredData, StructNode>{

    public StructNode(LayeredData data) {
        this.data = data;
        leaf=true;
    }

    public StructNode(LayeredData data, Set<StructNode> children) {
        this.data = data;
        this.children = children;
    }

}


