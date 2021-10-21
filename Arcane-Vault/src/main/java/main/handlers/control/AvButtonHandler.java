package main.handlers.control;

import eidolons.content.PARAMS;
import main.AV_DataManager;
import main.content.VALUE;
import main.entity.type.ObjType;
import main.gui.components.controls.AV_TableButtons;
import main.gui.components.table.AvColorHandler;
import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.handlers.mod.AvSaveHandler;
import main.launch.ArcaneVault;
import main.system.auxiliary.EnumMaster;
import main.system.util.DialogMaster;

public class AvButtonHandler extends AvHandler   {

    public AvButtonHandler(AvManager manager) {
        super(manager);
    }

        public void handle(boolean secondTable,boolean alt, String command) {
            AvSaveHandler.saveAllIfDirty();
        AV_TableButtons.AV_TABLE_BUTTON btn = new EnumMaster<AV_TableButtons.AV_TABLE_BUTTON>().retrieveEnumConst(AV_TableButtons.AV_TABLE_BUTTON.class, command);
            ObjType type = secondTable ? ArcaneVault.getPreviousSelectedType() : ArcaneVault.getSelectedType();
            //     case "Apply":
            //           getAssembler().applyType();
            switch (btn) {
                case NEW -> {
                    getModelHandler().add(null, secondTable);
                }
                case CLONE -> {getModelHandler().add(null, secondTable);
                }
                case UPGRADE -> {getModelHandler().upgrade(secondTable);
                }
                case ETALON -> {getAssembler().createComboType(secondTable);
                }
                case OPEN -> {
                    getModelHandler().findType();
                }
                case CLEAR -> {getModelHandler().clear(type);
                }
                case ROLLBACK -> {getVersionHandler().rollback(type);
                }
                case COMPARE -> {getColorHandler().setScheme(AvColorHandler.HIGHLIGHT_SCHEME.compare);
                }
                case REMOVE -> {
                    getModelHandler().remove(secondTable);
                }
                case RENAME -> {
                    String name = DialogMaster.inputText("Enter new name for " + type.getName());
                    getModelHandler().rename(type, type.getName(), name);
                }
                case COPY -> {AV_DataManager.copy(type);
                }
                case PASTE -> {
                    AV_DataManager.paste(type);
                }
                case SET -> {
                    //if values are selected...
                    ArcaneVault.getGame().getValueHelper().promptSetValue();
                }
            }

        //
        //         break;
        //     case "Upgrade": {
        //         getModelHandler().add(true);
        //         break;
        //     }
        //     case "Copy":
        //
        //         break;
        //     case "Paste":
        //
        //         break;
        //     case "Add WS":
        //         ArcaneVault.getWorkspaceManager().newWorkspaceForParty();
        //         break;
        //     case "Add Tab": {
        //         Class<?> ENUM_CLASS = DC_TYPE.class;
        //         String toAdd = ListChooser.chooseEnum(ENUM_CLASS,
        //                 ListChooser.SELECTION_MODE.MULTIPLE);
        //
        //         for (String sub : ContainerUtils.open(toAdd)) {
        //             ArcaneVault.getMainBuilder().getTabBuilder().addTab(
        //                     ENUM_CLASS, sub);
        //         }
        //         break;
        //     }
        //
        //     case "Copy To": {
        //         final boolean alt_ = alt;
        //                 if (alt_) {
        //                     ArcaneVault.getSelectedType().copyValues(
        //                             ArcaneVault.getPreviousSelectedType(), getCopyVals());
        //                 } else {
        //                     ArcaneVault.getSelectedType().cloneMapsWithExceptions(
        //                             ArcaneVault.getPreviousSelectedType(),
        //                             G_PROPS.NAME,
        //                             G_PROPS.DISPLAYED_NAME,
        //                             G_PROPS.IMAGE,
        //                             G_PROPS.GROUP,
        //                             ArcaneVault.getSelectedType().getOBJ_TYPE_ENUM()
        //                                     .getGroupingKey(),
        //                             ArcaneVault.getSelectedType().getOBJ_TYPE_ENUM()
        //                                     .getSubGroupingKey());
        //                 }
        //         break;
        //     }
        //     case "WS Add": {
        //         AvModelHandler.addToWorkspace(alt);
        //         break;
        //     }
        //     case "Toggle": {
        //         //?
        //         if (alt) {
        //             int result = DialogMaster.optionChoice("What do I toggle?", "Inversion");
        //             if (result == 0) {
        //                 ArcaneVault.setColorsInverted(!ArcaneVault.isColorsInverted());
        //             }
        //             break;
        //         }
        //         EditViewPanel panel = ArcaneVault.getMainBuilder().getEditViewPanel();
        //         AvManager.toggle();
        //         break;
        //     }
        //
        //     case "Backup": {
        //         AvSaveHandler.fullBackUp();
        //         break;
        //     }
        //
        //     case "Transform": {
        //         XML_Transformer.showTransformDialog();
        //         break;
        //     }
        //     case "Undo": {
        //         if (alt) {
        //             // while()
        //         }
        //         AvModelHandler.undo();
        //         break;
        //     }
        //
        //     case "Remove": {
        //         AvModelHandler.remove();
        //         break;
        //     }
        //     case "Reload": {
        //         AvSaveHandler.saveAll();
        //         AvModelHandler.reload();
        //         TableMouseListener.configureEditors();
        //         break;
        //     }
        //     case "Save": {
        //         AvSaveHandler.save();
        //         break;
        //     }
        //     case "Save all": {
        //         AvSaveHandler.saveAll();
        //         return;
        //     }
        // }
    }

    protected VALUE[] getCopyVals() {
        return new VALUE[]{PARAMS.FORCE, PARAMS.FORCE_SPELLPOWER_MOD, PARAMS.FORCE_DAMAGE_MOD,
                PARAMS.FORCE_KNOCK_MOD, PARAMS.FORCE_PUSH_MOD,};
    }

}
