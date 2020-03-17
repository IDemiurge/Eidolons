package main.level_editor.gui.palette.tree;

import com.kotcrab.vis.ui.widget.VisTree;
import main.level_editor.gui.tree.data.DataNode;

import java.util.Set;

public abstract class TreeX<T extends DataNode > extends VisTree {
    private Node root;

    @Override
    public void setUserObject(Object userObject) {
        T rootNode = (T) userObject;
        add(root = createRootNode(rootNode));
        recursiveAdd(root, rootNode);
        super.setUserObject(userObject);
    }

    private Node createRootNode(T rootNode) {
        return (createNodeComp(rootNode));
    }

    private void recursiveAdd(Node root, T node) {
        root.add(root = createNodeComp(node));

        Set<? extends T> set = node.getChildren();
        if (set != null)
        for (T child : set) {
            if (child.isLeaf()) {
                root.add(createNodeComp(child));
            } else
                recursiveAdd(root, child);
        }
    }

    protected abstract Node createNodeComp(T node);
}
