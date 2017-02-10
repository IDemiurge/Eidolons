package main.gui.builders;

import main.gui.components.controls.AV_ButtonPanel;
import main.launch.ArcaneVault;
import main.swing.generic.components.Builder;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.LinkedList;
import java.util.List;

//Use G-Engine? Caching 
public class MainBuilder extends Builder {

    private EditViewPanel tableBuilder = new EditViewPanel();
    private TabBuilder tabBuilder = new TabBuilder(null);
    // maybe it's time for a massive AV/Builder revamp that will support
    // rebuilding the gui?
    private AV_ButtonPanel buttonPanel = new AV_ButtonPanel();
    private boolean nodesDirty;

    public MainBuilder() {
        comp = new G_Panel();
    }

    @Override
    public void init() {

        builderArray = new Builder[]{

                getTabBuilder(),

        };

        infoArray = new String[]{

                "id tree, y 0, x 0, w " + ArcaneVault.TREE_WIDTH + ", h " + ArcaneVault.TREE_HEIGHT,

        };

        compArray = new G_Component[]{buttonPanel, tableBuilder.getPanel()};
        cInfoArray = new String[]{
                "id bp, pos "
                        + (ArcaneVault.selectiveInit ? ArcaneVault.TREE_WIDTH + " "
                        + ArcaneVault.TREE_HEIGHT : "0 tree.y2") + " table.x2 pref",

                "id table, pos tree.x2 0 ",

        };

        // compHolderArray = new {sp};
        initMap();
    }

    public EditViewPanel getEditViewPanel() {
        // TODO Auto-generated method stub
        return tableBuilder;
    }

    public String getSelectedSubTabName() {
        try {
            return ((TabBuilder) getTabBuilder().getBuilderArray()[getTabBuilder()
                    .getSelectedIndex()]).getSelectedTabName();
        } catch (Exception e) {
            return "";
        }
    }

    public String getSelectedTabName() {
        return getTabBuilder().getSelectedTabName();
    }

    public String getPreviousSelectedTabName() {
        return ArcaneVault.getPreviousSelectedType().getName();
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    public JTree getTree() {
        return getTabBuilder().getTree();

    }

    public List<DefaultMutableTreeNode> getSelectedNodes() {
        List<DefaultMutableTreeNode> list = new LinkedList<>();
        for (TreePath p : getTree().getSelectionPaths()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
            if (node != null) {
                list.add(node);
            }
        }
        return list;
    }

    public DefaultMutableTreeNode getSelectedNode() {
        try {
            return (DefaultMutableTreeNode) getTree().getSelectionPath().getLastPathComponent();
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }

    }

    public TreeViewBuilder getTreeBuilder() {
        return getTabBuilder().getTreeBuilder();
    }

    public TabBuilder getTabBuilder() {
        return tabBuilder;
    }

    public void setTabBuilder(TabBuilder tabBuilder) {
        this.tabBuilder = tabBuilder;
    }

    public boolean isNodesDirty() {
        return nodesDirty;
    }

    public void setNodesDirty(boolean nodesDirty) {
        this.nodesDirty = nodesDirty;
    }

    public AV_ButtonPanel getButtonPanel() {
        return buttonPanel;
    }
}
