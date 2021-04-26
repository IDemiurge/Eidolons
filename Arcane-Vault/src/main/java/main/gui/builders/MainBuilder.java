package main.gui.builders;

import main.gui.components.controls.AV_ButtonPanel;
import main.launch.ArcaneVault;
import main.launch.AvConsts;
import main.swing.generic.components.Builder;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//Use G-Engine? Caching 
public class MainBuilder extends Builder {

    private final EditViewPanel tableBuilder = new EditViewPanel();
    private TabBuilder tabBuilder = new TabBuilder(null);
    // maybe it's time for a massive AV/Builder revamp that will support
    // rebuilding the gui?
    private final AV_ButtonPanel buttonPanel = new AV_ButtonPanel();
    private boolean nodesDirty;

    public MainBuilder() {
        if (isSplitPane()){
            comp = new JSplitPane();
            comp.setLayout(new GridLayout());
        } else {
            comp = new G_Panel();
        }
    }

    private boolean isSplitPane() {
        return false;
    }

    @Override
    protected void add(JComponent newComp, String info) {
        if (comp instanceof JSplitPane) {
            G_Panel container = new G_Panel();
            container.add(newComp, info);
            if (comp.getComponentCount() == 0) {
                ((JSplitPane) comp).setLeftComponent(container);
            } else
                ((JSplitPane) comp).setRightComponent(container);
            if (getKeyListener() != null) {
                if (newComp instanceof G_Component) {
                    ((G_Component) newComp).setKeyManager(getKeyListener());
                } else {
                    newComp.addKeyListener(getKeyListener());
                }
            }
            return ;
        }
        super.add(newComp, info);
    }

    @Override
    public void init() {

        builderArray = new Builder[]{

                getTabBuilder(),

        };

        infoArray = new String[]{

                "id tree, y 0, x 0, w " + AvConsts.TREE_WIDTH + ", h " + AvConsts.TREE_HEIGHT,

        };

        compArray = new JComponent[]{buttonPanel, tableBuilder.getPanel()};
        cInfoArray = new String[]{
                "id bp, pos "
                        + (ArcaneVault.selectiveInit ? AvConsts.TREE_WIDTH + " "
                        + AvConsts.TREE_HEIGHT : "0 tree.y2-20") ,
                // + " table.x2 " +
                //         AvConsts.HEIGHT,

                "id table, pos tree.x2 0 ",

        };

        // compHolderArray = new {sp};
        initMap();
    }

    public EditViewPanel getEditViewPanel() {
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
        List<DefaultMutableTreeNode> list = new ArrayList<>();
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
            // main.system.ExceptionMaster.printStackTrace(e);
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
