package main.gui.components.tree;

import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.gui.builders.EditViewPanel;
import main.gui.builders.TabBuilder;
import main.launch.ArcaneVault;
import main.simulation.SimulationManager;
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

    private EditViewPanel panel;
    private JTree tree;

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

        String name = (String) node.getUserObject();
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

        // SoundManager.playEffectSound(SOUNDS.WHAT,
        // DataManager.getType(name)
        // .getProperty(PROPS.SOUNDSET));

        if (SimulationManager.isUnitType(tab) && ArcaneVault.isSimulationOn()) {
            try {
                SimulationManager.initUnitObj(name);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

        ObjType type = DataManager.getType(name, tab);
        if (type == null) {
            for (TabBuilder t : ArcaneVault.getAdditionalTrees()) {
                type = DataManager.getType(name, t.getSelectedTabName());
                if (type != null) {
                    break;
                }
            }
        }
        if (type == null) {
            type = DataManager.getType(name);
        }
        if (type == null) {
            return;
        }
        selectType(type, tab);
        List<ObjType> types = new ArrayList<>();
        int length = 1;
        try {
            length = e1.getPaths().length;
        } catch (Exception e) {

        }
        if (length > 2) { // TODO
            try {
                for (TreePath p : e1.getPaths()) {
                    node = (DefaultMutableTreeNode) p.getLastPathComponent();
                    if (node == null) {
                        continue;
                    }
                    name = (String) node.getUserObject();
                    type = DataManager.getType(name, tab);
                    if (type != null) {
                        types.add(type);
                    }
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        } else {
            types.add(type);
        }
        ArcaneVault.setSelectedTypes(types);
        ArcaneVault.setDirty(dtFlag);

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
