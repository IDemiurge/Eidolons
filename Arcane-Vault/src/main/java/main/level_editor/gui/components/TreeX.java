package main.level_editor.gui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisTree;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import main.data.tree.DataNode;
import main.system.graphics.FontMaster;

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
        T rootNode = (T) userObject;
        nodes.clear();
        if (isShowRoot()){
            add(root = createRootNode(rootNode));
            recursiveAdd(root, rootNode);
        } else {
            recursiveAdd(null, rootNode);
        }
        super.setUserObject(userObject);
        expandAll();
        for (Node node : nodes) {
            if (node.getChildren().size==0) {
                node.getParent().collapseAll();
            }

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
        n.getActor().addListener(new ClickListener(-1) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (getTapCount()>1) {
                    doubleClick(node);
                } else if (event.getButton()==1) {
                    rightClick(node);
                } else
                    selected(node);
                for (Node node1 : nodes) {
                    if (node1.getActor() instanceof Table) {
                        main.system.auxiliary.log.LogMaster.log(1,"node bg removed: " +node1.getActor());
                        ((Table) node1.getActor()).setBackground((Drawable) null);
                    }
                }
                if (n.getActor() instanceof Table) {
                    ((Table) n.getActor()).setBackground(NinePatchFactory.getHighlightSmallDrawable());
                }
            }

        });
        return n;
    }

    protected abstract void rightClick(T node);

    protected abstract void doubleClick(T node);

    protected abstract void selected(T node);

    protected Actor createNodeComp(T node) {
        Label.LabelStyle style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 20);
        String name = node.toString();
        ValueContainer actor = new ValueContainer(style, null , name, null );
        return actor;
    }

}
