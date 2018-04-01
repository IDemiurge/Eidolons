package main.utilities.sorting;

import main.launch.ArcaneVault;
import main.system.auxiliary.TreeMaster;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class TypeSorter {

    private SORT_BY last_sort;

    public void sort(SORT_BY sort_by, boolean ascend, boolean customList) {

        last_sort = sort_by;
        JTree tree = ArcaneVault.getMainBuilder().getTree();

        if (!customList) {
            for (DefaultMutableTreeNode node : TreeMaster
                    .getChildren((DefaultMutableTreeNode) tree.getModel()
                            .getRoot())) {

            }

//			sortTreeNode();
        }
//		ArcaneVault.reloadTree(node);
    }

    public enum SORT_BY {
        NAME,
        ID,
        LEVEL,
        CUSTOM_PROPERTY
    }

    public enum CUSTOM_LIST_TYPE {
        ALL_TYPES,
        MATCHING_TYPES,

    }
}
