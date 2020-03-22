package main.level_editor.gui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTree;
import main.level_editor.gui.tree.data.DataNode;

import java.util.Set;

import static main.system.auxiliary.log.LogMaster.log;

public abstract class TreeX<T extends DataNode> extends VisTree {
    private static final boolean LOGGED = false;
    protected Node root;

    @Override
    public void setUserObject(Object userObject) {
        T rootNode = (T) userObject;
        add(root = createRootNode(rootNode));
        recursiveAdd(root, rootNode);
        super.setUserObject(userObject);
        expandAll();
    }

    protected Node createRootNode(T rootNode) {
        return (createNode(rootNode));
    }

    protected void recursiveAdd(Node root, T node) {
        root.add(root = createNode(node));
        if (LOGGED)
            log(1, hashCode() + ": recursiveAdd " + node + " " + root);
        Set<? extends T> set = node.getChildren();
        if (set != null)
            for (T child : set) {
                if (child.isLeaf()) {
                    if (LOGGED)
                        log(1, hashCode() + ": add child " + child);
                    root.add(createNode(child));
                } else
                    recursiveAdd(root, child);
            }
    }

    protected Node createNode(T node) {
        Node n = new Node(createNodeComp(node));
        n.getActor().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                selected(node);
            }
        });
        return n;
    }

    protected abstract void selected(T node);

    protected abstract Actor createNodeComp(T node);
}
