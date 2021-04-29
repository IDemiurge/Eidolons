package main.gui.builders;

import eidolons.game.Simulation;
import main.ability.utilities.NodeMaster;
import main.content.ContentValsManager;
import main.content.OBJ_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.entity.type.TypeBuilder;
import main.gui.components.tree.AV_Tree;
import main.gui.components.tree.AV_TreeSelectionListener;
import main.launch.ArcaneVault;
import main.swing.generic.components.Builder;
import main.system.auxiliary.CloneMaster;
import main.system.graphics.ColorManager;
import main.utilities.workspace.Workspace;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.util.List;

// a tree for each tab 
// search result as a tree or a list? 
public class TreeViewBuilder extends Builder {

    private static final int VERTICAL_SCROLL_BAR_UNIT_INCREMENT = 15;

    private String sub;

    private List<ObjType> types;

    private OBJ_TYPE TYPE;

    private AV_Tree tree;

    private Workspace workspace;

    public TreeViewBuilder(List<ObjType> typesDoc, OBJ_TYPE TYPE, String sub) {
        this.TYPE = TYPE;
        this.types = typesDoc;
        this.sub = sub;

    }

    public TreeViewBuilder(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void init() {

        if (workspace == null) {
            tree = new AV_Tree(types, TYPE, sub, ArcaneVault.isColorsInverted());
        } else {
            tree = new AV_Tree(workspace);
        }
        JTree jTree = getTree();
        jTree.expandRow(0);
        // G_Panel container = new G_Panel();

        if (comp == null) {
            // Dimension dimension = new Dimension(AvConsts.TREE_WIDTH, AvConsts.TREE_HEIGHT);
            JScrollPane scrollPane = new JScrollPane(jTree);
            comp = scrollPane;
            scrollPane.getVerticalScrollBar().setUnitIncrement(VERTICAL_SCROLL_BAR_UNIT_INCREMENT);
        }
        jTree.addTreeSelectionListener(new AV_TreeSelectionListener(this.tree.getTree()));
        jTree.addMouseListener(new NodeMaster(jTree, false)); // add
        // workspace
        // listener?

        if (ArcaneVault.isColorsInverted()) {
            jTree.setBackground(ColorManager.BACKGROUND); // getOrCreate ws
        }
        // background
    }

    // TODO
    public void reload() {
        tree.reload();
        // types = DataManager.getTypesGroupNames(OBJ_TYPES.getType(type), generic);
        // init();
        // ((JScrollPane) comp).setViewportView(getTree());

        // comp.removeAll(); // JScrollPane scrollPane = new
        // JScrollPane(getTree());
        // scrollPane.getVerticalScrollBar().setUnitIncrement(VERTICAL_SCROLL_BAR_UNIT_INCREMENT);
        // comp.add(scrollPane, "pos 0 0");
        // comp.revalidate();
        // comp.add(new AV_Tree(types, generic, type,
        // ArcaneVault.isColorsInverted()));
    }

    public void reload(TreeNode node) {
        TreeModel model = new DefaultTreeModel(node);
        tree.getTree().setModel(model);
        tree.getTree().setSelectionRow(1);
        refresh();
    }

    @Override
    public JComponent build() {
        super.build();
        return comp;
    }

    @Override
    public JComponent getComp() {
        return tree;
    }

    public JTree getTree() {
        return tree.getTree();
    }

    @Override
    public synchronized void refresh() {

        // ((DefaultTreeModel) getTree().getModel()).reload();
        //
        // TreePath path = getTree().getSelectionPath();
        // getTree().collapsePath(path);
        // getTree().expandPath(path);
        // getTree().setSelectionPath(path);
        // getTree().setModel(model);
        getTree().revalidate();
        getTree().updateUI();
        tree.refresh();

        tree.setColorsInverted(ArcaneVault.isColorsInverted());
        if (ArcaneVault.isColorsInverted()) {
            getTree().setBackground(ColorManager.BACKGROUND);
        } else {
            getTree().setBackground(ColorManager.WHITE);
        }
    }

    private DefaultTreeModel getTreeModel() {
        return (DefaultTreeModel) tree.getTree().getModel();
    }


    public ObjType newType(ObjType newType, DefaultMutableTreeNode node ) {
        DefaultMutableTreeNode newNode;
        boolean empty = false;
        if (node == null) {
            empty = true;
        }
        String newName = newType.getName();
        if (empty) {
            newNode = new DefaultMutableTreeNode(newName, true);
            newNode.setUserObject(newType);
        } else {
            newNode = (DefaultMutableTreeNode) CloneMaster.deepCopy(node);
            newNode.setUserObject(newType);
        }

        DefaultMutableTreeNode parent = ((DefaultMutableTreeNode) getTree().getSelectionPath()
                .getLastPathComponent());
        if (empty ) { //|| !upgrade
            parent = (DefaultMutableTreeNode) parent.getParent();
        }
        newNode.removeAllChildren();
        getTreeModel().insertNodeInto(newNode, parent, 0);

        tree.getTree().setSelectionPath(new TreePath(newNode.getPath()));

        refresh();
        return newType;
    }


    public void remove() {
        if (ArcaneVault.getMainBuilder().getSelectedNode() == null) {
            return;
        }
        for (DefaultMutableTreeNode selectedNode : ArcaneVault.getMainBuilder().getSelectedNodes()) {
            selectedNode.removeFromParent();
        }
        refresh();

        int n = ArcaneVault.getMainBuilder().getTree().getRowCount();
        ArcaneVault.getMainBuilder().getTree().setSelectionRow(Math.min(1, n));
        ArcaneVault.getMainBuilder().getTree().getListeners(TreeSelectionListener.class)[0]
                .valueChanged(new TreeSelectionEvent(tree.getTree(), null, null, null, null));
        ArcaneVault.getMainBuilder().getEditViewPanel().refresh();

        // getTree().updateUI();
    }

    public void update() {
        getTree().updateUI();
    }

}
