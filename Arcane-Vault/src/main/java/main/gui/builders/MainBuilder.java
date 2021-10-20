package main.gui.builders;

import main.gui.components.controls.AV_IconStrip;
import main.gui.components.menu.AV_Menu;
import main.gui.components.table.AvColorHandler;
import main.launch.ArcaneVault;
import main.launch.AvConsts;
import main.swing.generic.components.Builder;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.system.ExceptionMaster;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

//Use G-Engine? Caching 
public class MainBuilder extends Builder {

    private final EditViewPanel tableBuilder = new EditViewPanel();
    private TabBuilder tabBuilder = new TabBuilder(null);
    private TabBuilder tabBuilderOrig = tabBuilder;
    // maybe it's time for a massive AV/Builder revamp that will support
    // rebuilding the gui?
    private boolean nodesDirty;
    private AV_Menu menu;
    private AV_Menu menu2;
    private AV_IconStrip icons;
    private DefaultMutableTreeNode selectedNode;
    private DefaultMutableTreeNode previousSelectedNode;

    JComboBox<VIEW_MODE> viewModes;
    JComboBox<TREE_MODE> treeModes;
//     JComboBox<TREE_MODE> treeModes;
// AvColorHandler.HIGHLIGHT_SCHEME
    public enum TREE_MODE {
        hierarchy,
        related,
        second,

    }

    public enum VIEW_MODE { //sync with color scheme?
        // two_types,
        compare,
        raw_type,
        parent_type,
    }

    public MainBuilder() {
        comp = new G_Panel();

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
            return;
        }
        super.add(newComp, info);
    }

    @Override
    public void init() {
        ActionListener viewModeListener = null;
        ActionListener treeModeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();

                getTreeBuilder().getInfoTree().setMode((TREE_MODE) ((JComboBox<TREE_MODE>) source).getSelectedItem());
            }
        };
        builderArray = new Builder[]{
                getTabBuilder(),
        };

        infoArray = new String[]{
                "id tree, y 0, x 0, w " + AvConsts.TREE_WIDTH + ", h " + AvConsts.TREE_HEIGHT,
        };
        menu = new AV_Menu(false);
        menu2 = new AV_Menu(true);
        viewModes = new JComboBox<>(VIEW_MODE.values());
        viewModes.addActionListener(viewModeListener);
        treeModes = new JComboBox<>(TREE_MODE.values());
        treeModes.addActionListener(treeModeListener);
        compArray = new JComponent[]{
                // viewModes,
                treeModes,
                menu.getBar(),
                icons = new AV_IconStrip(),
                menu2.getBar(), tableBuilder.getPanel(),
        };
        cInfoArray = new String[]{
                // "id bp, pos " + (ArcaneVault.selectiveInit ? AvConsts.TREE_WIDTH + " " + AvConsts.TREE_HEIGHT : "0 tree.y2-20"),

                // "id views, pos tree.x2 menu.y2+5 ",
                "id treeModes, pos tree.x2 0 ",
                "id menu, pos treeModes.x2 0, pad 3",
                "id icons, pos menu.x2 0, pad 3",
                "id menu2, pos icons.x2 0, pad 3",
                "id table, pos tree.x2 menu.y2+5 ",

        };

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
            ExceptionMaster.printStackTrace(e);
        }
        return "";
    }

    public String getSelectedTabName() {
        return getTabBuilder().getSelectedTabName();
    }

    public String getPreviousSelectedTabName() {
        return ArcaneVault.getPreviousSelectedType().getName();
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

    public void selectionChanged() {
        previousSelectedNode = selectedNode;
        selectedNode = (DefaultMutableTreeNode) getTree().getSelectionPath().getLastPathComponent();
    }

    public DefaultMutableTreeNode getSelectedNode() {
        return selectedNode;
    }

    public TreeViewBuilder getTreeBuilder() {
        return getTabBuilder().getTreeBuilder();
    }
    public TreeViewBuilder getPrevTreeBuilder() {
        return getTabBuilder().getPrevTreeBuilder();
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

    public DefaultMutableTreeNode getPreviousSelectedNode() {
        return previousSelectedNode;
    }

}
