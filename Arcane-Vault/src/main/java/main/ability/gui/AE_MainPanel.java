package main.ability.gui;

import main.ability.AE_Manager;
import main.ability.utilities.NodeMaster;
import main.ability.utilities.TemplateManager;
import main.data.ability.AE_Item;
import main.launch.AvConsts;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.TreeMaster;
import main.system.auxiliary.log.LogMaster;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AE_MainPanel extends G_Panel implements TreeSelectionListener,
        ActionListener {

    private JTree tree;
    private AE_EditPanel editPanel;
    private JSplitPane sp;

    private NodeMaster nodeMaster;
    private final Node doc;
    private final TemplateManager templateManager;

    public AE_MainPanel(String typeName) {
        Node document = AE_Manager.getDoc(typeName);
        this.templateManager = new TemplateManager(this);
        this.doc = document;
        try {
            tree = NodeMaster.initTree(document);
            tree.addTreeSelectionListener(this);
            this.setNodeMaster(new NodeMaster(tree));
            tree.addMouseListener(nodeMaster);
            initComponents();
            addComponents();
        } catch (Exception e) {
            LogMaster.log(1,
                    "AE tree failed to build for type: " + typeName);
             main.system.ExceptionMaster.printStackTrace(e);
        }

    }

    public String toString() {
        return doc.getTextContent();
    }

    public AE_Item getSelectedItem() {
        if (tree.getSelectionPath() == null) {
            return null;
        }
        AE_Item item;
        try {
            DefaultMutableTreeNode node = getSelectedNode();

            item = (AE_Item) node.getUserObject();
        } catch (Exception e) {

            return null;
        }
        return item;
    }

    public DefaultMutableTreeNode getSelectedNode() {
        return (DefaultMutableTreeNode) tree.getSelectionPath()
                .getLastPathComponent();
    }

    // selected tree node - ITEM + cached values for each ARG; how to map???
    public void refresh() {
        if (getSelectedItem() == null) {
            return;
        }
        removeAll();
        initComponents();
        // remove(editPanel);
        addComponents();
        // addEditPanel();
        revalidate();
        repaint();
    }

    private void addComponents() {
        sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree),
                // new JScrollPane
                (editPanel));
        sp.setDividerLocation(0.5);
        sp.setResizeWeight(0.5);
        // tree.setBackground(ColorManager.BLACK);
        // editPanel.setBackground(ColorManager.BLACK);
        // sp.setBackground(ColorManager.BLACK);

        add(sp, "pos 0 0 visual.x2 " + AvConsts.AE_HEIGHT);

    }

    private void initComponents() {
        if (tree.getSelectionPath() == null) {
            editPanel = new AE_EditPanel();
            TreeMaster.expandTree(tree);
        } else {
            editPanel = AE_Manager.getAE_EditPanel(this, getSelectedItem(),
                    tree.getRowForPath(tree.getSelectionPath()));
        }

    }

    private AE_Item getItem(String selectedItem) {
        return AE_Manager.getAE_Item(selectedItem);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        try {
            if (e.getSource() == null) {
                return;
            }

            if (e.getPath() != null) {
                if (e.getOldLeadSelectionPath() != null) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            tree.updateUI();
                        }
                    });
                }
                refresh();
            }
        } catch (Exception ex) {
            main.system.ExceptionMaster.printStackTrace(ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        e.getSource();
    }

    public AE_EditPanel getEditPanel() {
        return editPanel;
    }

    public void setEditPanel(AE_EditPanel editPanel) {
        this.editPanel = editPanel;
    }

    public NodeMaster getNodeMaster() {
        return nodeMaster;
    }

    public void setNodeMaster(NodeMaster nodeMaster) {
        this.nodeMaster = nodeMaster;
    }

    public JTree getTree() {
        return tree;
    }

    public TemplateManager getTemplateManager() {
        return templateManager;
    }

    public void setTreeSelection(DefaultMutableTreeNode newNode) {
        TreePath path = new TreePath(newNode.getPath());
        tree.setSelectionPath(path);

    }

}
