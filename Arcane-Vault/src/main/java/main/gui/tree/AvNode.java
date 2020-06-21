package main.gui.tree;

import java.util.Set;

public class AvNode {

    Object userObject;
    Set<AvNode> children;

    public AvNode(Object userObject, Set<AvNode> children) {
        this.userObject = userObject;
        this.children = children;
    }
}
