package main.level_editor.gui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisTree;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import main.data.tree.DataNode;
import main.system.graphics.FontMaster;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static main.system.auxiliary.log.LogMaster.log;

public abstract class TreeX<T extends DataNode> extends VisTree {
    private static final boolean LOGGED = false;
    protected Node root;
    protected Set<Node> nodes = new LinkedHashSet<>();

    @Override
    public void setUserObject(Object userObject) {
        clearChildren();
        nodes.clear();
        if (userObject instanceof Collection) {
            for (Object o : ((Collection) userObject)) {
                initRoot(o);
            }
        } else {
            initRoot(userObject);
        }

        super.setUserObject(userObject);
        expandAll();
        for (Node node : nodes) {
            if (node.getChildren().size == 0) {
                node.getParent().collapseAll();
            }

        }
    }

    protected void initRoot(Object userObject) {
        T rootNode = (T) userObject;
        if (isShowRoot()) {
            add(root = createRootNode(rootNode));
            recursiveAdd(root, rootNode);
        } else {
            recursiveAdd(null, rootNode);
        }
    }

    protected boolean isShowRoot() {
        return false;
    }

    protected Node createRootNode(T rootNode) {
        return (createNode(rootNode));
    }

    protected void recursiveAdd(Node root, T node) {
        if (root == null) {
            add(root = createNode(node));
        } else {
            root.add(root = createNode(node));
        }
        nodes.add(root);
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
        n.setObject(node);
        n.getActor().addListener(new ClickListener(-1) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                click(getTapCount(), event, n, node);
            }

        });
        return n;
    }

    protected void click(int tapCount, InputEvent event, Node n, T node) {
        int button = event.getButton();
        Eidolons.onNonGdxThread(() -> {
            clicked(tapCount, event, n, node, button);
        });
    }

    protected void clicked(int tapCount, InputEvent event, Node n, T node, int button) {
        if (tapCount > 1) {
            doubleClick(node);
        } else if (event != null && button == 1) {
            rightClick(node);
        } else {
            selected(node, n);
        }
        for (Node node1 : nodes) {
            if (node1.getActor() instanceof Table) {
                ((Table) node1.getActor()).setBackground((Drawable) null);
            }
        }
        if (n.getActor() instanceof Table) {
            Eidolons.onGdxThread(() ->
                    ((Table) n.getActor()).setBackground(NinePatchFactory.getHighlightSmallDrawable()));
        }
    }

    protected abstract void rightClick(T node);

    protected abstract void doubleClick(T node);

    protected abstract void selected(T node, Node n);

    protected Actor createNodeComp(T node) {
        Label.LabelStyle style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 20);
        String name = node.toString();
        ValueContainer actor = new ValueContainer(style, null, name, null);
        return actor;
    }

    public void reselect() {
    }

    public void select(Object o) {

    }
}
