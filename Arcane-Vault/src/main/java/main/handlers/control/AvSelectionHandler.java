package main.handlers.control;

import main.content.DC_TYPE;
import main.entity.type.ObjType;
import main.gui.builders.TabBuilder;
import main.launch.ArcaneVault;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.TreeMaster;
import main.system.auxiliary.data.ListMaster;

import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class AvSelectionHandler {

    public static void adjustTreeTabSelection(ObjType type) {
        adjustTreeTabSelection(type, true);
    }

    public static void adjustTreeTabSelection(ObjType type, boolean select) {
        int code =
                ListMaster.getIndexString(new ArrayList<>(ArcaneVault.getMainBuilder().getTabBuilder()
                        .getTabNames()), type.getOBJ_TYPE(), true);

        ArcaneVault.getMainBuilder().getTabBuilder().getTabbedPane().setSelectedIndex(code);
        List<String> list = EnumMaster.findEnumConstantNames(type.getOBJ_TYPE_ENUM()
                .getGroupingKey().getName());
        int index = ListMaster.getIndexString(list, type.getGroupingKey(), true);
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS) {
            if (index > 5) {
                index--;
            }
        }
        TabBuilder tb = ArcaneVault.getMainBuilder().getTabBuilder().getSubTabs(code);
        tb.getTabbedPane().setSelectedIndex(index);

        // getOrCreate path for node? I could keep some map from type to path...
        if (!select) {
            if (tb.getTree().getTreeSelectionListeners().length != 1) {
                return;
            }
            TreeSelectionListener listener = tb.getTree().getTreeSelectionListeners()[0];
            tb.getTree().removeTreeSelectionListener(listener);
            try {
                TreeMaster.collapseTree(tb.getTree());
                DefaultMutableTreeNode node = TreeMaster.findNode(tb.getTree(), type.getName());
                if (node == null) {
                    //                    tb.getTabbedPane().setSelectedIndex(prevIndex);
                    return;
                }
                tb.getTree().setSelectionPath(new TreePath(node.getPath()));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            } finally {
                tb.getTree().addTreeSelectionListener(listener);
            }
            return;
        }

        TreeMaster.collapseTree(tb.getTree());
        DefaultMutableTreeNode node = TreeMaster.findNode(tb.getTree(), type.getName());
        TreePath path = new TreePath(node.getPath());
        tb.getTree().setSelectionPath(path);
        tb.getTree().scrollPathToVisible(path);
    }
}
