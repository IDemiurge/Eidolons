package main.ability.gui;

import main.ability.utilities.NodeMaster;
import main.ability.utilities.TemplateManager;
import main.data.ability.AE_Item;
import main.data.ability.ARGS;
import main.data.ability.Argument;
import main.data.ability.Mapper;
import main.data.ability.construct.VariableManager;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.auxiliary.secondary.DefaultComparator;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

public class AE_Element extends G_Panel implements MouseListener {

    public static final int ELEMENT_HEIGHT = 128;
    private static final int COLUMNS = 125;
    private Argument arg;
    private JTree tree;
    private NodeMaster nodeMaster;
    private JComboBox<AE_Item> box;
    private int index;
    private int db_index = 0;
    private JTextField textBox;
    private Checkbox checkBox;
    private AE_Item item;
    private AE_MainPanel mainPanel;
    private boolean ENUM;
    private List<?> ENUMS;
    private int y = 0;
    private int h = 32;
    private Vector<AE_Item> itemList;
    private boolean formula = false;

    public AE_Element(int index, Argument argument, NodeMaster nodeMaster,
                      AE_MainPanel mainPanel) {
        nodeMaster.setAutoSelect(true);
        this.mainPanel = mainPanel;
        this.index = index;
        this.nodeMaster = nodeMaster;
        this.tree = nodeMaster.getTree();
        this.arg = argument;
        addToolTip();
        switch (argument.getElementType()) {
            case BOOLEAN:
                item = Mapper.getPrimitiveItem(argument);
                initCheckBoxElement();
                break;
            case ENUM_CHOOSING:
                this.ENUMS = Arrays.asList(argument.getEnumList());

                this.item = Mapper.getItem(argument.name());
                initDropBoxElement(true);
                break;
            case ITEM_CHOOSING:
                initDropBoxElement();
                initTemplateButton();
                break;
            case TEXT:
                initTextBoxElement();
                item = Mapper.getPrimitiveItem(argument);
                if (argument == ARGS.FORMULA) {
                    formula = true;
                }
                break;
            default:
                break;

        }
        panelSize = new Dimension(300, 200);
    }

    public AE_Element(int index, DefaultMutableTreeNode node, int i,
                      NodeMaster nodeMaster, AE_MainPanel mainPanel) {

        this(index, ((AE_Item) node.getUserObject()).getArg(), nodeMaster,
                mainPanel);
        this.db_index = i;
        setDropBoxIndexQuietly(i);
    }

    private void addToolTip() {
        if (arg == null) {
            return;
        }
        JTextArea toolTip = new JTextArea(arg.name());
        toolTip.setEditable(false);
        toolTip.setFont(FontMaster.getFont(FONT.MAIN, 12, Font.PLAIN));
        add(toolTip, "pos 0 " + y + ", h " + h);
        y += h;

    }

    private void initTemplateButton() {
        // TODO Auto-generated method stub
        JButton button = new JButton("Use Template");
        add(button, "pos 0 " + y + ", h " + h);
        y += h;

        button.setFont(FontMaster.getFont(FONT.MAIN, 12, Font.PLAIN));
        button.setActionCommand(TemplateManager.getUseTemplateAction());
        button.addActionListener(mainPanel.getTemplateManager());
    }

    public void setDropBoxIndex(int i) {
        if (item == null) {
            item = Mapper.getItemList(arg).get(i);
        }
        if (box == null) {
            return;
        }
        box.setSelectedIndex(i);
    }

    public void setDropBoxIndexQuietly(int i) {

        if (item == null) {
            item = Mapper.getItemList(arg).get(i);
        }

        if (box == null || (!nodeMaster.isAutoSelect())) {
            return;
        }

        box.removeActionListener(nodeMaster);
        box.setSelectedIndex(i);
        box.addActionListener(nodeMaster);
        // nodeMaster.setAutoSelect(true);

    }

    private void initTextBoxElement() {
        String text = arg.name();
        DefaultMutableTreeNode elementNode = getElementNode();
        if (elementNode != null) {
            if (!elementNode.isLeaf()) {
                text = getText();
            }
        }

        this.textBox = new JTextField(text, COLUMNS);
        textBox.addActionListener(nodeMaster);
        textBox.addMouseListener(this);
        LogMaster.log(0, "initTextBoxElement");
        add(textBox, "pos 0 " + y + ",  h " + h);
        y += h;

        // JTextArea toolTip = new JTextArea(arg.name());
        // toolTip.setEditable(false);
        // toolTip.setFont(FontMaster.getFont(FONT.MAIN, 12, Font.PLAIN));
        // add(toolTip, "pos 0 " + y + ", h " + h);
        // y += h;
    }

    private String getText() {
        TreeNode node = getElementNode().getFirstChild();
        if (node != null) {
            try {
                return (((DefaultMutableTreeNode) node).getUserObject()
                        .toString().replace(arg.name(), ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return arg.name();
    }

    private DefaultMutableTreeNode getElementNode() {
        DefaultMutableTreeNode parentNode = getParentNode();
        if (parentNode == null) {
            return null;
        }
        if (parentNode.isLeaf()) {
            return null;
        }
        return (DefaultMutableTreeNode) parentNode.getChildAt(index);
    }

    private DefaultMutableTreeNode getParentNode() {
        return mainPanel.getSelectedNode();

    }

    private void initCheckBoxElement() {
        this.checkBox = new Checkbox();
        checkBox.addItemListener(nodeMaster);
        LogMaster.log(1, "initCheckBoxElement");
        add(checkBox, "pos 0 " + y + "" + ", h " + h);
        y += h;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initDropBoxElement(boolean ENUM) {
        this.ENUM = ENUM;
        if (ENUM) {
            Vector itemList = new Vector(ENUMS);
            Collections.sort(itemList, new DefaultComparator<AE_Item>());
            itemList.add(VariableManager.VARIABLE);
            this.setItemList(itemList);

        } else {
            setItemList(new Vector<>(Mapper.getItemList(arg)));
        }
        this.box = new JComboBox<>(getItemList());
        box.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);

        box.addActionListener(nodeMaster);
        // box.setSelectedIndex(db_index);
        add(box, "pos 0 " + y +
//				", w container.x2/2"+
                ", h " + h);
        y += h;
        box.setFont(FontMaster.getFont(FONT.MAIN, 12, Font.PLAIN));

    }

    private void initDropBoxElement() {
        initDropBoxElement(false);

        // tree selection, node addition,
    }

    public Argument getArg() {
        return arg;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public AE_Item getItem() {
        return item;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // if (formula) {
        // FormulaBuilder fb = new FormulaBuilder(textBox);
        // return;
        // }
        String input = JOptionPane.showInputDialog(item.getName(),
                textBox.getText());
        if (input == null) {
            return;
        }
        if (Objects.equals(input, "")) {
            return;
        }
        if (input.equals(textBox.getText())) {
            return;
        }
        textBox.setText(input);
        textBox.getListeners(ActionListener.class)[0]
                .actionPerformed(new ActionEvent(textBox, 0, ""));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public boolean isENUM() {
        return ENUM;
    }

    public void setENUM(boolean eNUM) {
        ENUM = eNUM;
    }

    public List<?> getENUMS() {
        return ENUMS;
    }

    public void setENUMS(List<?> eNUMS) {
        ENUMS = eNUMS;
    }

    public Vector<AE_Item> getItemList() {
        return itemList;
    }

    public void setItemList(Vector<AE_Item> itemList) {
        this.itemList = itemList;
    }

}
