package main.ability.gui;

import main.ability.utilities.NodeMaster;
import main.ability.utilities.TemplateManager;
import main.data.ability.AE_Item;
import main.data.ability.Argument;
import main.launch.AvConsts;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AE_EditPanel extends G_Panel {

    private AE_MainPanel mainPanel;
    private NodeMaster nodeMaster;
    private AE_Item item;
    private final List<AE_Element> elements = new ArrayList<>();
    private int pos = 0;
    private Argument expandArg;
    // private G_Panel scrollable;
    private JButton saveButton;
    private Dimension minSize;

    public AE_EditPanel(AE_Item item, AE_MainPanel mainPanel, int index) {
        this.item = item;
        this.nodeMaster = mainPanel.getNodeMaster();
        this.mainPanel = mainPanel;
        createGUI();
        panelSize = new Dimension(AvConsts.AE_WIDTH * 3 / 2,
                AvConsts.AE_HEIGHT);
        setBackground(ColorManager.OBSIDIAN);
        // item.getARG_List();
    }

    public AE_EditPanel() {
    }

    public Dimension getMinimumSize() {
        if (minSize == null) {
            minSize = new Dimension(100, AvConsts.AE_HEIGHT);
        }
        return minSize;
        // return super.getMinimumSize();
    }

    // @Override
    // public void add(Component comp, Object c) {
    // getScrollable().add(comp, c);
    // }

    public void addSaveButton() {
        saveButton = new JButton("Save as Template");
        saveButton.setFont(FontMaster.getFont(FONT.MAIN, 12, Font.PLAIN));
        saveButton.addActionListener(mainPanel.getTemplateManager());
        saveButton.setActionCommand(TemplateManager.getSaveTemplateAction());
        add(saveButton, "id saveButton, pos 0 " + AE_Element.ELEMENT_HEIGHT
                + "*" + pos);
    }

    private void addControls() {
        addSaveButton();
    }

    public boolean checkContainerExpansionRequired() {
        return (elements.size() >= pos);
    }

    public void expandContainer() {
        if (expandArg == null) {
            initExpandArg();
        }
        add(new AE_Element(pos, expandArg, nodeMaster, mainPanel), "x 0, y "
                + pos + "*" + AE_Element.ELEMENT_HEIGHT);
        pos++;
        addControls();
    }

    private void initExpandArg() {
//		if (item.getArg() == MACRO_ARGS.REPLIES) {
//			expandArg = MACRO_ARGS.REPLY;
//		} else {
//			expandArg = item.getArg();
//		}
        expandArg = item.getArg();
    }

    private void createGUI() {
        pos = -1;
        if (item.isContainer()) {
            for (int i = 0; i < mainPanel.getSelectedNode().getChildCount(); i++) {
                int db_index = NodeMaster.getDropBoxIndex(mainPanel
                        .getSelectedNode(), null, i);
                AE_Element element = new AE_Element(i,
                        (DefaultMutableTreeNode) mainPanel.getSelectedNode()
                                .getChildAt(i), db_index, nodeMaster, mainPanel);
                elements.add(element);
                add(element, "x 0, y " + i + "*" + AE_Element.ELEMENT_HEIGHT);
                LogMaster
                        .log(1, i + "CONTAINER ELEMENT ADDED with selection: "
                                + db_index);
                pos = i;
            }
            pos++;
            expandContainer();
            return;
        }

        int i = 0;
        if (item.getArgList() != null) {
            for (Argument arg : item.getArgList()) {
                AE_Element element = new AE_Element(i, arg, nodeMaster,
                        mainPanel);
                elements.add(element);
                int db_index = NodeMaster.getDropBoxIndex(mainPanel
                        .getSelectedNode(), element, i);

                element.setDropBoxIndexQuietly(db_index);
                add(element, "x 0, y " + i + "*" + AE_Element.ELEMENT_HEIGHT);
                LogMaster.log(1, "ELEMENT ADDED: "
                        + arg.name());
                i++;
            }
        }
        pos = i;
        addControls();

    }

    public AE_MainPanel getMainPanel() {
        return mainPanel;
    }

    public List<AE_Element> getElements() {
        return elements;
    }

}
