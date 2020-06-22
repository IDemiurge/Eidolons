package main.handlers.control;

import eidolons.content.PARAMS;
import eidolons.game.core.Eidolons;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.AV_DataManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.gui.builders.EditViewPanel;
import main.gui.components.table.TableMouseListener;
import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.handlers.mod.AvModelHandler;
import main.handlers.mod.AvSaveHandler;
import main.handlers.types.SimulationHandler;
import main.launch.ArcaneVault;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.ContainerUtils;
import main.utilities.xml.XML_Transformer;

import javax.swing.*;

public class AvButtonHandler extends AvHandler {

    public final static String[] commands = new String[]{
            "New", "Clone",
            "Remove", "Upgrade",
            "Preview", "Undo",
            "Save all", "Save",
            "Add Tab", "Toggle",
            "Copy", "Paste",
            "Backup", "Transform"
    };

    public AvButtonHandler(AvManager manager) {
        super(manager);
    }


    public void handleButtonClick(boolean alt, String command) {
        if (SwingUtilities.isEventDispatchThread()) {
            Eidolons.onNonGdxThread(() -> handle(alt, command));
        } else {
            handle(alt, command);
        }
    }
        public void handle(boolean alt, String command) {
        switch (command) {
            case "Info":
                //
            case "Preview":
                SimulationHandler.refreshType(ArcaneVault.getSelectedType());
                break;
            case "Apply":
                ArcaneVault.getManager().getAssembler().applyType();
                break;
            case "Level Up":
            case "Clone":
                AvModelHandler.add(false);
                break;
            case "New":
                AvModelHandler.add(null);
                break;
            case "Upgrade": {
                AvModelHandler.add(true);
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
                        ListChooser.SELECTION_MODE.MULTIPLE);

                for (String sub : ContainerUtils.open(toAdd)) {
                    ArcaneVault.getMainBuilder().getTabBuilder().addTab(
                            ENUM_CLASS, sub);
                }
                break;
            }

            case "Copy To": {
                final boolean alt_ = alt;
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
                break;
            }
            case "WS Add": {
                AvModelHandler.addToWorkspace(alt);
                break;
            }
            case "Toggle": {
                //?
                if (alt) {
                    int result = DialogMaster.optionChoice("What do I toggle?", "Inversion");
                    if (result == 0) {
                        ArcaneVault.setColorsInverted(!ArcaneVault.isColorsInverted());
                    }
                    break;
                }
                EditViewPanel panel = ArcaneVault.getMainBuilder().getEditViewPanel();
                AvManager.toggle();
                break;
            }

            case "Backup": {
                AvSaveHandler.fullBackUp();
                break;
            }

            case "Transform": {
                XML_Transformer.showTransformDialog();
                break;
            }
            case "Undo": {
                if (alt) {
                    // while()
                }
                AvModelHandler.undo();
                break;
            }

            case "Remove": {
                AvModelHandler.remove();
                break;
            }
            case "Reload": {
                AvSaveHandler.saveAll();
                AvModelHandler.reload();
                TableMouseListener.configureEditors();
                break;
            }
            case "Save": {
                AvSaveHandler.save();
                break;
            }
            case "Save all": {
                AvSaveHandler.saveAll();
                return;
            }
        }
    }

    protected VALUE[] getCopyVals() {
        return new VALUE[]{PARAMS.FORCE, PARAMS.FORCE_SPELLPOWER_MOD, PARAMS.FORCE_DAMAGE_MOD,
                PARAMS.FORCE_KNOCK_MOD, PARAMS.FORCE_PUSH_MOD,};
    }
}
