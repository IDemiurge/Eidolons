package main.gui.components.controls;

import eidolons.content.PARAMS;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.system.audio.DC_SoundMaster;
import main.AV_DataManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.gui.builders.EditViewPanel;
import main.gui.components.table.TableMouseListener;
import main.launch.ArcaneVault;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.components.panels.G_ButtonPanel;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.threading.Weaver;
import main.utilities.xml.XML_Transformer;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;


public class AV_ButtonPanel extends G_ButtonPanel {
    public static final String RENAME_TYPE = ("Rename Type");
    public static final String RENAME_SELECTED_TYPE = ("Rename Selected Type");
    public static final String RENAME_VALUE = ("Rename Value");
    public static final String REMOVE_VALUE = ("Remove Value");
    public static final String CLEAN_UP = ("Clean Up");
    public static final String DEFAULTS = "Defaults";
    static String[] commands = new String[]{
            "New",
            "Clone",
            "Remove",
            "Upgrade",
     "Undo", "Save all"
     , "WS Add", "Save",
     "Add Tab", "Toggle",
            "Copy","Paste",
            "Backup",
     // "Group",
     // CLEAN_UP,
//     "Edit",
//     "Defaults",
//     "Add WS", "Test"
            // "Reload", doesn't work yet!
            // "New Hero",
            // "Edit",
            // RENAME_TYPE, RENAME_VALUE, REMOVE_VALUE
    };
    XmlTransformMenu xmlTransformMenu;

    public AV_ButtonPanel() {
        super(commands);
        setPanelSize(new Dimension(ArcaneVault.WIDTH, 47));
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        new Thread(() -> handleAction(e), e.getActionCommand()+" thread").start();
    }

    public void handleAction(ActionEvent e) {
        boolean alt = ActionEvent.ALT_MASK == (e.getModifiers() & ActionEvent.ALT_MASK);
        String command = ((JButton) e.getSource()).getActionCommand();
        handleButtonClick(alt, command);
    }

    public void handleButtonClick(boolean alt, String command) {
        switch (command) {
            case "Info":
                //
            case "Preview":
            case "Apply":
            case "Level Up":
            case "New":
                {
                ModelManager.add();
                break;
            }
            case "Copy":
                AV_DataManager.copy(ArcaneVault.getSelectedType());
                break;
            case "Paste":
                AV_DataManager.paste(ArcaneVault.getSelectedType());
                break;
            case "Add WS":
                ArcaneVault.getWorkspaceManager().newWorkspaceForParty();
                break;
            case "Add Tab": {
                Class<?> ENUM_CLASS = DC_TYPE.class;
                String toAdd = ListChooser.chooseEnum(ENUM_CLASS,
                 SELECTION_MODE.MULTIPLE);

                for (String sub : ContainerUtils.open(toAdd)) {
                    ArcaneVault.getMainBuilder().getTabBuilder().addTab(
                     ENUM_CLASS, sub);
                }
                break;
            }

            case "Clone": {
                final boolean alt_ = alt;
                new Thread(new Runnable() {
                    public void run() {
                        if (alt_) {
                            ArcaneVault.getSelectedType().copyValues(
                             ArcaneVault.getPreviousSelectedType(), getCopyVals());
                        } else {
                            ArcaneVault.getSelectedType().cloneMapsWithExceptions(
                             ArcaneVault.getPreviousSelectedType(),
                             G_PROPS.NAME,
                             G_PROPS.DISPLAYED_NAME,
                             G_PROPS.IMAGE,
                             G_PROPS.GROUP,
                             ArcaneVault.getSelectedType().getOBJ_TYPE_ENUM()
                              .getGroupingKey(),
                             ArcaneVault.getSelectedType().getOBJ_TYPE_ENUM()
                              .getSubGroupingKey());
                        }
                        refresh();
                    }
                }, "Clone thread").start();
                break;
            }
            case "WS Add": {

                ModelManager.addToWorkspace(alt);
                break;
            }
            case "Toggle": {
                if (alt) {
                    int result = DialogMaster.optionChoice("What do I toggle?", "Inversion");
                    if (result == 0) {
                        ArcaneVault.setColorsInverted(!ArcaneVault.isColorsInverted());
                    }
                    break;
                }
                EditViewPanel panel = ArcaneVault.getMainBuilder().getEditViewPanel();
              ModelManager.toggle();
                break;
            }

            case "Backup": {
                ModelManager.fullBackUp();
                break;
            }
            case RENAME_SELECTED_TYPE: {
                renameType(ArcaneVault.getSelectedType());
                break;
            }
            case RENAME_TYPE: {
                ObjType type = DataManager.getType(JOptionPane.showInputDialog("Enter type name"));
                renameType(type);
                break;
            }
            case CLEAN_UP: {
                // cleanUp();
                break;

            }
            case DEFAULTS:
                ModelManager.addDefaultValues(alt);
                break;
            case "Test":
                if (alt)
                    DC_SoundMaster.preconstructEffectSounds();
                else
                    AnimConstructor.preconstructAllForAV();
                break;

            case "Transform": {
                if (xmlTransformMenu == null) {
                    xmlTransformMenu = new XmlTransformMenu();
                } else {
                    xmlTransformMenu.setVisible(xmlTransformMenu.isVisible());
                }
            }
            case "Undo": {
                if (alt) {
                    // while()
                }
                ModelManager.undo();
                break;
            }

            case "Upgrade": {
                if (alt) {
                    ModelManager.addParent();
                } else {
                    ModelManager.addUpgrade();
                }
                break;
            }
            case "Remove": {
                ModelManager.remove();
                break;
            }
            case "Reload": {
                Weaver.inNewThread(new Runnable() {
                    public void run() {
                        ModelManager.saveAll();
                        ModelManager.reload();
                        TableMouseListener.configureEditors();
                    }
                });
            }
            case "Save": {
                ModelManager.save();
                break;
            }

            case "Save all": {

                ModelManager.saveAll();
                return;
            }
        }
    }

    protected VALUE[] getCopyVals() {
        return new VALUE[]{PARAMS.FORCE, PARAMS.FORCE_SPELLPOWER_MOD, PARAMS.FORCE_DAMAGE_MOD,
         PARAMS.FORCE_KNOCK_MOD, PARAMS.FORCE_PUSH_MOD,};
    }

    private void cleanUp() {

        String string = "What do I clean up now?..";
        String TRUE = "Group";
        String FALSE = "Subgroup";
        String NULL = "XML";
        Boolean result = DialogMaster.askAndWait(string, TRUE, FALSE, NULL);
        if (result == null) {
            XML_Transformer.cleanUp();
            return;
        }
        OBJ_TYPE TYPE = ArcaneVault.getSelectedOBJ_TYPE();
        String subgroup = (result) ? ArcaneVault.getSelectedType().getGroupingKey() : ArcaneVault
         .getSelectedType().getSubGroupingKey();
        List<String> types = (result) ? DataManager.getTypesGroupNames(TYPE, subgroup)
         : DataManager.getTypesSubGroupNames(TYPE, subgroup);
        List<String> retained = ContainerUtils.openContainer(new ListChooser(SELECTION_MODE.MULTIPLE,
         types, TYPE).choose());
        for (String t : types) {
            if (retained.contains(t)) {
                continue;
            }
            DataManager.removeType(t, TYPE.getName());

        }

        ArcaneVault.getMainBuilder().getTreeBuilder().reload();

        int n = ArcaneVault.getMainBuilder().getTree().getRowCount();
        ArcaneVault.getMainBuilder().getTree().setSelectionRow(Math.min(1, n));
        ArcaneVault.getMainBuilder().getTree().getListeners(TreeSelectionListener.class)[0]
         .valueChanged(new TreeSelectionEvent(ArcaneVault.getMainBuilder().getTree(), null,
          null, null, null));
        ArcaneVault.getMainBuilder().getEditViewPanel().refresh();

        // reset tree

    }


    @Override
    public void refresh() {
        ArcaneVault.getMainBuilder().getTree().getTreeSelectionListeners()[0].valueChanged(null);
    }

    @Override
    public boolean isInitialized() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getColumns() {
        return 2;
    }

    private void renameType(ObjType type) {
        if (type == null) {
            return;
        }
        String input = ListChooser.chooseEnum(PROPERTY.class, SELECTION_MODE.MULTIPLE);

        if (StringMaster.isEmpty(input)) {
            return;
        }

        List<PROPERTY> propList = new ListMaster<>(PROPERTY.class).toList(input);

        String newName = JOptionPane.showInputDialog("Enter new name");
        if (StringMaster.isEmpty(newName)) {
            return;
        }
        XML_Transformer.renameType(type, newName, propList.toArray(new PROPERTY[propList.size()]));
    }

}