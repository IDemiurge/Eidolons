package main.gui.builders;

import main.gui.components.tree.AV_Tree;
import main.swing.generic.components.G_Panel;
import main.v2_0.AV2;

import java.awt.*;

public class AvInfoTree extends G_Panel {
/*
mode should be static!

 */
   private static MainBuilder.TREE_MODE mode;
    private AV_Tree tree;
    private TreeViewBuilder treeViewBuilder;

    public AvInfoTree() {
    }

    @Override
    public void refresh() {
        this.treeViewBuilder = AV2.getMainBuilder().getPrevTreeBuilder();

        if (mode != null) {
        setMode(mode);
        }
    }

    //TODO WORKSPACE!
    public void setMode(MainBuilder.TREE_MODE mode) {
        this.mode = mode;
        removeAll();
        switch (mode) {
            case hierarchy -> {

            }
            case related -> {

            }
            case second -> {
                add(getTree());
            }
        }
    }

    private Component getTree() {
        if (tree==null){
            tree = treeViewBuilder.createTypeTree();
             treeViewBuilder.initTypeTree(tree.getTree());
        }
        return tree;
    }
}
