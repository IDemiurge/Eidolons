package main.ability.utilities;

import main.ability.gui.AE_Element;
import main.ability.gui.AE_MainPanel;
import main.data.ability.AE_Item;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Writer;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class TemplateManager implements ActionListener {

    private static final String USE_TEMPLATE = "Use template";

    private static final String SAVE_TEMPLATE = "Save as template";

    JFileChooser fc;

    private AE_MainPanel mainPanel;

    private File lastfile;

    private String path;

    private AE_Element element;

    public TemplateManager(AE_MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        fc = new JFileChooser();
        path = PathFinder.getTemplatesPath();
    }

    public static String getUseTemplateAction() {
        return USE_TEMPLATE;
    }

    public static String getSaveTemplateAction() {
        return SAVE_TEMPLATE;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (mainPanel.getSelectedNode() == null) {
            return;
        }
        switch (((JButton) e.getSource()).getActionCommand()) {
            case SAVE_TEMPLATE: {
                saveTemplate();
                return;
            }
            case USE_TEMPLATE: {
                element = (AE_Element) ((JButton) e.getSource()).getParent();
                String templateType = element.getArg().name();
                AE_Item item = element.getItem();

                // String itemName = "\\";
                // if (item != null)
                // itemName += item.getArg().name();
                File directory = FileManager.getFile(path + "\\" + templateType

                );
                if (!directory.isDirectory()) {
                    directory.mkdirs();
                }
                fc.setCurrentDirectory(directory);
                int returnVal = fc.showOpenDialog(mainPanel.getParent());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    useTemplate(file);
                }
                return;
            }

        }
    }

    private String getFilePath(AE_Item item) {
        if (item == null) {
            return path;
        }
        String templateType = item.getArg().name();

        return path + "\\" + templateType;
    }

    private void saveTemplate() throws java.lang.ClassCastException {

        DefaultMutableTreeNode node = mainPanel.getSelectedNode();
        if (!(node.getUserObject() instanceof AE_Item)) {
            return;
        }
        AE_Item item = (AE_Item) node.getUserObject();

        String xml = XML_Converter.getXmlFromNode(node);
        if (xml == null) {
            throw new ClassCastException();
        }

        String name = JOptionPane.showInputDialog("Type template's name...");
        if (name == null) {
            return;
        }
        String newPath = getFilePath(item) + "\\" + item.getName();
        XML_Writer.write(xml, newPath, name + ".xml");

    }

    private void useTemplate(File file) {
        DefaultMutableTreeNode node = mainPanel.getSelectedNode();
        String xml = FileManager.readFile(file);
        Node doc = XML_Converter.getDoc(xml);
        DefaultMutableTreeNode newNode = NodeMaster.build(doc.getFirstChild());
        LogMaster.log(1, XML_Converter
                .getStringFromXML(doc));
        NodeMaster.newNode(newNode, element.getIndex(), mainPanel.getTree());
        mainPanel.getEditPanel().checkContainerExpansionRequired();
    }

    public String getPath() {
        return path;
    }
}
