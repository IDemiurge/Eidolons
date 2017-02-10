package main.system.auxiliary;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class TreeMaster {

    public static List<DefaultMutableTreeNode> getChildren(DefaultMutableTreeNode selectedNode) {
        return getChildren(selectedNode, false);
    }

    @SuppressWarnings("unchecked")
    public static List<DefaultMutableTreeNode> getChildren(DefaultMutableTreeNode selectedNode,
                                                           boolean recursive) {
        Enumeration<DefaultMutableTreeNode> e = selectedNode.children();
        List<DefaultMutableTreeNode> list = new LinkedList<>();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode nextElement = e.nextElement();
            if (recursive) {
                if (!nextElement.isLeaf()) {
                    list.addAll(getChildren(nextElement, true));
                }
            }
            list.add(nextElement);
        }
        return list;
    }

    public static List<DefaultMutableTreeNode> getAllNodes(JTree tree) {
        return getChildren((DefaultMutableTreeNode) tree.getModel().getRoot(), true);

    }

    public static List<DefaultMutableTreeNode> getChildren(JTree tree) {
        return getChildren((DefaultMutableTreeNode) tree.getModel().getRoot(), false);

    }

    public static DefaultMutableTreeNode findChildNode(DefaultMutableTreeNode subNode,
                                                       String property) {
        Enumeration<DefaultMutableTreeNode> e = subNode.children();
        List<DefaultMutableTreeNode> list = new LinkedList<>();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode child = e.nextElement();
            if (child.getUserObject().toString().equals(property)) {
                return child;
            }
        }
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode child = e.nextElement();
            if (StringMaster.compare(child.getUserObject().toString(), property)) {
                return child;
            }
        }
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode child = e.nextElement();
            if (StringMaster.compare(child.getUserObject().toString(), property, false)) {
                return child;
            }
        }
        return null;

    }

    public static void expandTree(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); ++i) {
            tree.expandRow(i);
        }
    }

    public static DefaultMutableTreeNode findNode(JTree tree, String name) {
        for (DefaultMutableTreeNode n : getAllNodes(tree)) {
            if (StringMaster.compareByChar(n.getUserObject().toString(), name, false)) {
                return n;
            }
        }
        return null;
    }

    public static void collapseTree(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); ++i) {
            tree.collapseRow(i);
        }
    }

}
