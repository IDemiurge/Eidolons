package main.gui.components.tree;

import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.entity.type.ObjType;
import main.gui.builders.EditViewPanel;
import main.handlers.types.SimulationHandler;
import main.launch.ArcaneVault;
import main.swing.SwingMaster;
import main.swing.generic.components.G_Panel;
import main.utilities.workspace.Workspace;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class AV_TreeSelectionListener implements TreeSelectionListener {

    private final EditViewPanel panel;
    private final JTree tree;

    public AV_TreeSelectionListener(JTree tree) {
        panel = ArcaneVault.getMainBuilder().getEditViewPanel();
        this.tree = tree;
        // tree.setSelectionPath(path);
        // DefaultMutableTreeNode node = ArcaneVault.getMainBuilder()
        // .getSelectedNode();
        // node.getp

    }

    @Override
    public void valueChanged(TreeSelectionEvent e1) {
        if (e1 == null) {
            return;
        }
        tree.requestFocusInWindow();
        boolean dtFlag = ArcaneVault.isDirty();
        if (((JTree) e1.getSource()).getSelectionPath() == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (((JTree) e1.getSource())
                .getSelectionPath().getLastPathComponent());

        if (node == null) {
            return;
        }
        ObjType type = null;
        String name = null;
        if (node.getUserObject() instanceof ObjType) {
            type = (ObjType) node.getUserObject();
            name = type.getName();
        } else {
            name = node.getUserObject().toString();
        }

        String tab;

        tab = ArcaneVault.getMainBuilder().getSelectedTabName();
        if (tab == null) {
            Workspace workspace = ArcaneVault.getWorkspaceManager().getWorkspaceByTab(
                    (G_Panel) SwingMaster.getParentOfClass(tree, G_Panel.class));
            if (workspace != null) {
                try {
                    tab = workspace.getOBJ_TYPE(name, tab).getName();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
        //AV revamp - default preview?
        if (SimulationHandler.isUnitType(tab) && ArcaneVault.isSimulationOn()) {
            try {
                SimulationHandler.initUnitObj(name);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (type == null) {
            return;
        }
        selectType(type, tab);
        List<ObjType> types = new ArrayList<>();
                for (TreePath p :  tree.getSelectionPaths()) {
                    node = (DefaultMutableTreeNode) p.getLastPathComponent();
                    if (node == null) {
                        continue;
                    }
                        types.add((ObjType) node.getUserObject());
                }
        ArcaneVault.setSelectedTypes(types);
        ArcaneVault.setDirty(dtFlag);
        ArcaneVault.getMainBuilder().selectionChanged();

    }

    public void selectType(ObjType type, String tab) {
        panel.selectType(type);
    }

    public boolean isTreeEditType(String selected) {
        if (ArcaneVault.isMacroMode()) {
            return ContentValsManager.getOBJ_TYPE(selected).isTreeEditType();
        }
        return selected.equals(DC_TYPE.ABILS.getName());
    }
}
