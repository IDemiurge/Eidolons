package main.ability.utilities;

import main.ability.gui.AE_Element;
import main.content.ContentManager;
import main.content.VALUE;
import main.content.parameters.PARAMETER;
import main.content.parameters.Param;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.content.properties.Prop;
import main.data.ability.AE_Item;
import main.data.ability.ARGS;
import main.data.ability.Argument;
import main.data.ability.Mapper;
import main.data.ability.construct.VariableManager;
import main.launch.ArcaneVault;
import main.system.auxiliary.*;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

public class NodeMaster implements ActionListener, ItemListener, MouseListener {
	private static final int COMBO_BOX_MOUSE_MODIFIER = 16;
	AE_Item selectedItem;
	private JTree tree;
	private boolean autoSelect = true;
	private boolean ability;
	private DefaultMutableTreeNode copyBuffer;

	public NodeMaster(JTree tree, boolean ability) {
		this.tree = tree;
		this.ability = ability;
	}

	public NodeMaster(JTree tree) {
		this(tree, true);
	}

	public static JTree initTree(Node document) {
		JTree result = new JTree(build(document));
		// result.setRootVisible(false); //now clean documents!
		return result;

	}

	public static AE_Item getEnumItem(ARGS arg, VALUE v) {
		return Mapper.getItem(v.getName());
	}

	public static AE_Item getEnumItem(Argument arg, String string) {

		if (!arg.getCoreClass().isEnum()) {
			if (arg.getCoreClass() == Param.class || arg.getCoreClass() == PARAMETER.class)
				string = ContentManager.getPARAM(string).getName();
			if (arg.getCoreClass() == Prop.class || arg.getCoreClass() == PROPERTY.class)
				string = ContentManager.getPROP(string).getName();
		}
		if (Mapper.getItem(string) != null)
			return Mapper.getItem(string);
		AE_Item item = new AE_Item(string, arg, null, arg.getCoreClass(), false);
		Mapper.addEnumConstItem(item);
		return item;
	}

	public static AE_Item getEnumItem(Argument arg, int index) {

		String name = null;

		if (!arg.getCoreClass().isEnum()) {
			if (arg.getCoreClass() == Param.class || arg.getCoreClass() == PARAMETER.class)
				name = ContentManager.getParamList().get(index).getName();
			if (arg.getCoreClass() == Prop.class || arg.getCoreClass() == PROPERTY.class)
				name = ContentManager.getPropList().get(index).getName();
		} else {
			name = Arrays.asList(arg.getCoreClass().getEnumConstants()).get(index).toString();
			// collection = CollectionsMaster.getSortedCollection(collection);
            // name =collection.getOrCreate(index).toString()
        }
        if (Mapper.getItem(name) != null)
            return Mapper.getItem(name);
        AE_Item item = new AE_Item(name, arg, null, arg.getCoreClass(), false);
        Mapper.addEnumConstItem(item);
        return item;
    }

	public static DefaultMutableTreeNode build(Node e) {
		if (e == null)
			return new DefaultMutableTreeNode();
		String nodeName = e.getNodeName();

		if (nodeName.contains("#"))
			return new DefaultMutableTreeNode();

		if (nodeName.contains(AE_Item.SEPARATOR))
			nodeName = nodeName.substring(0, nodeName.indexOf(AE_Item.SEPARATOR));

		AE_Item rootItem = Mapper.getItem(e);
		DefaultMutableTreeNode result = new DefaultMutableTreeNode(rootItem);

		if (rootItem.isENUM()) {
			if (e.getTextContent() == null)
				return new DefaultMutableTreeNode(rootItem.getArg().getEmptyName());

			AE_Item childItem = getEnumItem(rootItem.getArg(), EnumMaster.getEnumConstIndex(
					rootItem.getArg().getCoreClass(), e.getTextContent()));

			DefaultMutableTreeNode child = new DefaultMutableTreeNode(childItem.getName());
			result.add(child);
			return result;
		}
		if (rootItem.isPrimitive()) {

			if (e.getTextContent() == null)
				return new DefaultMutableTreeNode(rootItem.getArg().getEmptyName());

			DefaultMutableTreeNode child = new DefaultMutableTreeNode(rootItem.getArg().name()
					+ e.getTextContent());
			result.add(child);
			return result;
		}

		int x = e.getChildNodes().getLength();

		for (int i = 0; i < x; i++) {
			Node child = e.getChildNodes().item(i);
			String itemName = child.getNodeName();
			if (itemName.contains("#text"))
				continue;
			DefaultMutableTreeNode node = new DefaultMutableTreeNode();
			node.setUserObject(Mapper.getItem(child));
			result.add(build(child));
			main.system.auxiliary.LogMaster.log(LogMaster.AV_AE, "node added: "
					+ child.getNodeName());
		}

		return result;
	}

	private static int getDropBoxIndex(Object selectedItem, AE_Element element) {
		String text = selectedItem.toString();
		int index = new SearchMaster<AE_Item>().getIndex(text, element.getItemList());
		main.system.auxiliary.LogMaster.log(1, element.getItem().getName() + selectedItem + " is "
				+ index + "th in " + element.getItemList());
		return index;
	}

	public static int getDropBoxIndex(DefaultMutableTreeNode parentNode, AE_Element element, int i) {

		int elementIndex = (element == null) ? i : element.getIndex();
		AE_Item nodeItem = (AE_Item) parentNode.getUserObject();

		List<DefaultMutableTreeNode> children = TreeMaster.getChildren(parentNode);
		if (children.isEmpty())
			return 0;
		DefaultMutableTreeNode child = children.get(elementIndex);
		Object userObject = child.getUserObject();

		if (!(userObject instanceof AE_Item))
			return 0;
		AE_Item item = (AE_Item) userObject;

		if (item.isENUM()) {

			DefaultMutableTreeNode enumChild = child.getFirstLeaf();
			Object enumUserObject = enumChild.getUserObject();
			return getDropBoxIndex(enumUserObject, element);

		}

		Argument arg = null;
		if (nodeItem.isContainer()) {
			arg = item.getArg();
		} else
			arg = nodeItem.getArgList().get(elementIndex);

		List<AE_Item> itemList = Mapper.getItemList(arg);
		if (!itemList.contains(item)) {
			main.system.auxiliary.LogMaster.log(1, "indexing item not found: " + item.getName()
					+ ";  " + arg + "= " + itemList);
			return 0;
		}
		return itemList.indexOf(item);
        // return Mapper.getItemList(nodeItem.getArgList().getOrCreate(elementIndex))
        // .indexOf(child.getUserObject());
    }

    public static DefaultMutableTreeNode newNode(DefaultMutableTreeNode newNode, int index,
                                                 JTree tree) {
        return new NodeMaster(tree).newNode(newNode, index);

    }

    public static DefaultMutableTreeNode newNode(AE_Item item, int index, JTree tree) {
        return new NodeMaster(tree).newNode(item, index);
    }

    public static void moveNode(DefaultTreeModel model, DefaultMutableTreeNode node, boolean down) {
        TreeNode parent = node.getParent();
        if (parent == null)
            return;
        int index = model.getIndexOfChild(parent, node);
        if (down)
            index++;
        else
            index--;

        if (index < 0)
            return;
        if (index >= model.getChildCount(parent))
            return;

        model.removeNodeFromParent(node);
        model.insertNodeInto(node, (DefaultMutableTreeNode) parent, index);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JComboBox) {
            // toggle this how? perhaps based on n_of_items?
            if (e.getModifiers() == COMBO_BOX_MOUSE_MODIFIER)
                comboboxAction(source);
        } else if (source instanceof JTextField) {
            textBoxAction(source);
        }

    }

    @Override
    public void itemStateChanged(ItemEvent arg0) {
        // checkBox
        Checkbox cb = (Checkbox) arg0.getSource();
        AE_Item item = Mapper.getPrimitiveItem(ARGS.BOOLEAN);
        AE_Element element = (AE_Element) cb.getParent();
        int index = element.getIndex();

        DefaultMutableTreeNode node = newNode(item, index);
        DefaultMutableTreeNode textNode = new DefaultMutableTreeNode(item.getName() + cb.getState());

        node.add(textNode);

    }

	private void textBoxAction(Object source) {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		AE_Item parentItem = (AE_Item) parent.getUserObject();
		JTextField tb = (JTextField) source;
		AE_Element element = (AE_Element) tb.getParent();
		int index = element.getIndex();

		AE_Item item = element.getItem();

		DefaultMutableTreeNode node = newNode(item, index);
		DefaultMutableTreeNode textNode = new DefaultMutableTreeNode(item.getName() + tb.getText());

		node.add(textNode);
	}

	private void comboboxAction(Object source) {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		JComboBox<AE_Item> cb = (JComboBox<AE_Item>) source;

		// I could store previous selection of each combobox...
		AE_Element element = (AE_Element) cb.getParent();
		int index = element.getIndex();
		if (element.isENUM()) {
			DefaultMutableTreeNode node = newNode(element.getItem(), index);
			AE_Item item = getEnumItem(element.getArg(), cb.getSelectedItem().toString());
			if (item == null) {
				main.system.auxiliary.LogMaster.log(2, "NULL ITEM: "
						+ cb.getSelectedItem().toString());
			}
			DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(item.getName());
			node.add(newChild);
			return;
		} else {
			AE_Item parentItem = (AE_Item) parent.getUserObject();

		}
		AE_Item item = (AE_Item) cb.getSelectedItem();
		DefaultMutableTreeNode node = newNode(item, index);
		if (autoSelect) {
			// createEmptyNodes(node, item);
			tree.setSelectionPath(new TreePath(node.getPath()));

		}

	}

	private DefaultMutableTreeNode newNode(DefaultMutableTreeNode node, int index) {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
//		if (parent.isLeaf())
        createEmptyNodes(parent, (AE_Item) parent.getUserObject());

		if (parent.getChildCount() > index) {
			if (parent.getChildAt(index) != null)
				parent.remove(index);
		}

		((DefaultTreeModel) tree.getModel()).insertNodeInto(node, parent, index);
		tree.scrollPathToVisible(new TreePath(node.getPath()));
		tree.updateUI();
		return node;
	}

	private DefaultMutableTreeNode newNode(AE_Item item, int index) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
		return newNode(node, index);
	}

	private void createEmptyNodes(DefaultMutableTreeNode node, AE_Item item) {
		if (item.isContainer())
			return;
		for (Argument arg : item.getArgList()) {
            if (!arg.isPrimitive()) {
                DefaultMutableTreeNode newChild = new DefaultMutableTreeNode("<" +
                        arg.name() +
                        ">" + VariableManager.VARIABLE + "</" +
                        arg.name() +
                        ">");
                if (!Mapper.getItemList(arg).isEmpty())
                    newChild = new DefaultMutableTreeNode(Mapper.getItemList(arg).get(0));
                createEmptyNodes(newChild, Mapper.getItemList(arg).get(0));
                node.add(newChild);
                continue;

		}

            Object value = VariableManager.VARIABLE;
            if (arg.equals(ARGS.BOOLEAN)) {
                value = false;
            }
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(Mapper.getPrimitiveItem(arg));
            itemNode.add(new DefaultMutableTreeNode(value));


        }
    }

	public JTree getTree() {
		return tree;
	}

	public void setTree(JTree tree) {
		this.tree = tree;
	}

	public boolean isAutoSelect() {
		return autoSelect;
	}

    public void setAutoSelect(boolean b) {
        this.autoSelect = b;
    }

	public DefaultMutableTreeNode getSelectedNode() {
		try {
			return (DefaultMutableTreeNode) getTree().getSelectionPath().getLastPathComponent();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		boolean right = SwingUtilities.isRightMouseButton(arg0);

		if (arg0.isAltDown()) {
			if (arg0.getClickCount() > 1) {
				trySetBase();
				return;
			}
		}
		if (arg0.isShiftDown()) {
			if (right && ability)
				tryRemove();
			else
				tryClone();
			return;
		} else if (arg0.isControlDown()) {
			moveNode((DefaultTreeModel) tree.getModel(), getSelectedNode(), right);
		} else if (right) {
			JTree tree = (JTree) arg0.getSource();
			int row = 0;
			try {
				row = tree.getSelectionRows()[0];
			} catch (Exception e) {

			}
			// tree.getModel().getIndexOfChild(tree.getModel().getRoot(),
			// tree.getSelectionRows())
			for (int i = row; i < tree.getRowCount(); ++i) {
				if (arg0.getClickCount() > 1) {
					tree.expandRow(i);
				} else
					tree.collapseRow(i);
			}
		}
	}

	private DefaultMutableTreeNode cloneNodeTree(DefaultMutableTreeNode node) {
		DefaultMutableTreeNode nodeTree = cloneLeafNode(node);
		nodeTree.removeAllChildren();
		for (DefaultMutableTreeNode n : TreeMaster.getChildren(node)) {
			DefaultMutableTreeNode newNode = cloneNode(n);
			nodeTree.add(newNode);
		}
		return nodeTree;
	}

	private DefaultMutableTreeNode cloneNode(DefaultMutableTreeNode n) {
		if (n.children().hasMoreElements())
			return cloneNodeTree(n);
		else
			return cloneLeafNode(n);
	}

	private DefaultMutableTreeNode cloneLeafNode(DefaultMutableTreeNode n) {
		DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) CloneMaster.deepCopy(n);
		newNode.setUserObject(n.getUserObject());
		return newNode;
	}

	private void tryRemove() {
		try {
			DefaultMutableTreeNode node = getSelectedNode();
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

			if (!checkNodeClonable(parent.getUserObject().toString()))
				return;
			parent.remove(node);
		} catch (Exception e) {
			e.printStackTrace();
		}

		SoundMaster.playStandardSound(STD_SOUNDS.ERASE);
	}

	private void trySetBase() {
		new Thread(new Runnable() {
			public void run() {
				WaitMaster.WAIT(100);
				ArcaneVault.getSelectedType().setProperty(G_PROPS.BASE_TYPE,
						ArcaneVault.getPreviousSelectedType().getName());
				// main.system.auxiliary.LogMaster.log(1, )
			}
		}, " thread").start();
		// ArcaneVault.getPreviousSelectedType().setProperty(G_PROPS.BASE_TYPE,
		// ArcaneVault.getSelectedType().getName());
		// ((ObjType)getSelectedNode().getUserObject())
        // ArcaneVault.getMainBuilder().getOrCreate;
    }

	public void nodePasted() {

	}

	public void nodeCopied() {
		copyBuffer = getSelectedNode();
	}

	private void tryClone() {
		try {
			DefaultMutableTreeNode node = getSelectedNode();
			// int index = tree.getSelectionRows()[0];
			MutableTreeNode newNode = cloneNodeTree(node);

			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
			if (!checkNodeClonable(parent.getUserObject().toString()))
				return;
			((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, parent, node.getParent()
					.getChildCount() - 1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		SoundMaster.playStandardSound(STD_SOUNDS.DONE);
	}

	private boolean checkNodeClonable(String string) {
		return Mapper.getItem(string.split(":")[0]).isContainer();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
