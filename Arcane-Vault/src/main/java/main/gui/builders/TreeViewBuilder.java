package main.gui.builders;

import main.ability.utilities.NodeMaster;
import main.client.dc.Simulation;
import main.content.MACRO_OBJ_TYPES;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.gui.components.tree.AV_Tree;
import main.gui.components.tree.AV_TreeSelectionListener;
import main.launch.ArcaneVault;
import main.swing.generic.components.Builder;
import main.system.auxiliary.CloneMaster;
import main.system.auxiliary.ColorManager;
import main.utilities.workspace.Workspace;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

// a tree for each tab 
// search result as a tree or a list? 
public class TreeViewBuilder extends Builder {

	private static final int VERTICAL_SCROLL_BAR_UNIT_INCREMENT = 15;

	private String sub;

	private List<String> types;

	private String type = "units";

	private AV_Tree tree;

	private Workspace workspace;

	public TreeViewBuilder(List<String> typesDoc, String sub, String type) {
		this.type = type;
		this.types = typesDoc;
		this.sub = sub;

	}

	public TreeViewBuilder(Workspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public void init() {
		if (workspace == null)
			tree = new AV_Tree(types, sub, type, ArcaneVault.isColorsInverted());
		else {
			tree = new AV_Tree(workspace);
		}

		tree.getTree().expandRow(0);
		// List<DefaultMutableTreeNode> nodes = TreeMaster
		// .getChildren((DefaultMutableTreeNode) tree.getTree().getModel()
		// .getRoot());
		// nodes = TreeMaster.getChildren(nodes.getOrCreate(0));
		if (comp == null) {
			Dimension dimension = new Dimension(ArcaneVault.TREE_WIDTH, ArcaneVault.TREE_HEIGHT);
			JScrollPane scrollPane = new JScrollPane(getTree());
			// comp = new G_Panel();
			// scrollPane.setSize(dimension);
			// scrollPane.setPreferredSize(dimension);
			// scrollPane.setMaximumSize(dimension);
			comp = scrollPane;

			// getTree().setSize(dimension);
			// getTree().setPreferredSize(dimension);
			// getTree().setMaximumSize(dimension);
			// comp.add(scrollPane);
			scrollPane.getVerticalScrollBar().setUnitIncrement(VERTICAL_SCROLL_BAR_UNIT_INCREMENT);
		}
		getTree().addTreeSelectionListener(new AV_TreeSelectionListener(tree.getTree()));
		getTree().addMouseListener(new NodeMaster(getTree(), false)); // add
		// workspace
		// listener?

		if (ArcaneVault.isColorsInverted())
			getTree().setBackground(ColorManager.BACKGROUND); // getOrCreate ws
																// background
	}

	// TODO
	public void reload() {
		// types = DataManager.getTypesGroupNames(OBJ_TYPES.getType(type), sub);
		// init();
		// ((JScrollPane) comp).setViewportView(getTree());

		// comp.removeAll(); // JScrollPane scrollPane = new
		// JScrollPane(getTree());
		// scrollPane.getVerticalScrollBar().setUnitIncrement(VERTICAL_SCROLL_BAR_UNIT_INCREMENT);
		// comp.add(scrollPane, "pos 0 0");
		// comp.revalidate();
		// comp.add(new AV_Tree(types, sub, type,
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
		if (ArcaneVault.isColorsInverted())
			getTree().setBackground(ColorManager.BACKGROUND);
		else
			getTree().setBackground(ColorManager.WHITE);
	}

	private DefaultTreeModel getTreeModel() {
		return (DefaultTreeModel) tree.getTree().getModel();
	}

	public void newType(String newName, DefaultMutableTreeNode node, String TYPE, Boolean upgrade) {
		DefaultMutableTreeNode newNode = null;
		ObjType newType = null;
		boolean empty = false;
		if (node == null)
			empty = true;
		if (DataManager.getType(node.getUserObject().toString(), TYPE) == null)
			empty = true;
		if (empty) {
			newType = getEmptyType(TYPE, newName);
			newNode = new DefaultMutableTreeNode(newName, true);
		} else {
			ObjType type = DataManager.getType(node.getUserObject() + "", TYPE);
			newType = CloneMaster.getTypeCopy(type, newName, ArcaneVault.getGame(), TYPE);
			newNode = (DefaultMutableTreeNode) CloneMaster.deepCopy(node);

			newNode.setUserObject(newName);
		}

		DefaultMutableTreeNode parent = ((DefaultMutableTreeNode) getTree().getSelectionPath()
				.getLastPathComponent());
		if (!empty) // upgrade_parent_new
			if (upgrade == null) {
				parent = (DefaultMutableTreeNode) parent.getParent();
			} else {
				if (!upgrade) {
					parent.removeFromParent();
					newNode.add(parent);
					parent = (DefaultMutableTreeNode) parent.getParent();
					parent.add(newNode);
				}
			}
		newNode.removeAllChildren();

		// parent.add(newNode);
		getTreeModel().insertNodeInto(newNode, parent, 0);

		newType.setProperty(G_PROPS.NAME, newName);
		newType.setProperty(G_PROPS.DISPLAYED_NAME, newName);
		if (empty) {

			newType.setProperty(DataManager.getSubGroupingKey(TYPE), parent.getUserObject()
					.toString());

			newType.setProperty(DataManager.getGroupingKey(TYPE), ArcaneVault.getMainBuilder()
					.getSelectedSubTabName());
		}
		if (upgrade != null)
			if (upgrade) {
				newType.setProperty(G_PROPS.BASE_TYPE, node.getUserObject().toString());
			} else {
				// parent.getUserObject().toString()
				parent.getParent();

				ObjType selectedType = DataManager.getType(node.getUserObject().toString(), TYPE);

				selectedType.setProperty(G_PROPS.BASE_TYPE, newNode.getUserObject().toString());

				newType.setProperty(G_PROPS.BASE_TYPE, parent.getUserObject().toString());

			}

		DataManager.addType(newName, TYPE, newType);
		Simulation.getGame().initType(newType);

		tree.getTree().setSelectionPath(new TreePath(newNode.getPath()));

		refresh();
	}

	private ObjType getEmptyType(String TYPE, String newName) {
		ObjType type = new ObjType(newName);
		OBJ_TYPE t = OBJ_TYPES.getType(TYPE);
		if (ArcaneVault.isMacroMode())
			t = MACRO_OBJ_TYPES.getType(TYPE);
		// type.setObjType(t);
		Simulation.getGame().initType(type);
		return type;
	}

	public void remove() {
		if (ArcaneVault.getMainBuilder().getSelectedNode() == null)
			return;
		ArcaneVault.getMainBuilder().getSelectedNode().removeFromParent();
		String selected = ArcaneVault.getMainBuilder().getSelectedTabName();
		DataManager.removeType((String) ArcaneVault.getMainBuilder().getSelectedNode()
				.getUserObject(), selected);

		refresh();

		int n = ArcaneVault.getMainBuilder().getTree().getRowCount();
		ArcaneVault.getMainBuilder().getTree().setSelectionRow(Math.min(1, n));
		ArcaneVault.getMainBuilder().getTree().getListeners(TreeSelectionListener.class)[0]
				.valueChanged(new TreeSelectionEvent(tree.getTree(), null, null, null, null));
		ArcaneVault.getMainBuilder().getEditViewPanel().refresh();

		// getTree().updateUI();
	}

	public boolean nameChanged(String oldName, String newValue) {
		DefaultMutableTreeNode selectedNode = ArcaneVault.getMainBuilder().getSelectedNode();
		if (!selectedNode.getUserObject().toString().equals(oldName))
			return false;
		selectedNode.setUserObject(newValue);

		getTree().updateUI();
		return true;
	}

}
