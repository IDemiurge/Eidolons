package main.gui.components.controls;

import main.content.PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.xml.XML_Transformer;
import main.entity.type.ObjType;
import main.launch.ArcaneVault;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.components.panels.G_ButtonPanel;
import main.system.auxiliary.ListMaster;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class XmlTransformMenu extends G_ButtonPanel {
    private static final String RENAME_TYPE = ("Rename Type");
    private static final String RENAME_SELECTED_TYPE = ("Rename Selected Type");
    private static final String RENAME_VALUE = ("Rename Value");
    private static final String REMOVE_VALUE = ("Remvoe Value");
    final static String[] commands = {RENAME_TYPE, RENAME_VALUE, REMOVE_VALUE,};

    public XmlTransformMenu() {
        super(commands);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        switch (arg0.getActionCommand()) {
            case RENAME_SELECTED_TYPE: {
                renameType(ArcaneVault.getSelectedType());
            }
            case RENAME_TYPE: {
                ObjType type = DataManager.getType(JOptionPane
                        .showInputDialog("Enter type name"));
                renameType(type);
                break;
            }
            case RENAME_VALUE: {
                break;
            }
            case REMOVE_VALUE: {
                break;
            }
        }

    }

    private void renameType(ObjType type) {
        String input = ListChooser
                .chooseEnum(PROPS.class, SELECTION_MODE.MULTIPLE);
        List<PROPERTY> propList = new ListMaster<>(PROPERTY.class)
                .toList(input);

        XML_Transformer.renameType(type, JOptionPane
                .showInputDialog("Enter new name"), propList
                .toArray(new PROPERTY[propList.size()]));
    }
}
