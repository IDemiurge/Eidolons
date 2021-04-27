package main.handlers.mod;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PROPS;
import main.AV_DataManager;
import main.ability.AE_Manager;
import main.content.DC_TYPE;
import main.content.enums.entity.ItemEnums.ITEM_RARITY;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.handlers.control.AvSelectionHandler;
import main.v2_0.AV2;
import main.launch.ArcaneVault;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.sound.AudioEnums;
import main.system.sound.SoundMaster;
import main.system.util.DialogMaster;
import main.utilities.search.TypeFinder;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;

public class AvModelHandler extends AvHandler {

    private static AV_DataManager manager;

    public AvModelHandler(AvManager manager) {
        super(manager);
    }

    public void upgrade(boolean secondTable) {
        add(true, secondTable);
    }

    public void findType() {
        ObjType type = TypeFinder.findType(false);
        if (type == null) {
            return;
        }
        AvSelectionHandler.adjustTreeTabSelection(type);
    }

    public static void back(ObjType type) {
        getAV_Manager().back(type);
    }

    public void add(final Boolean upgrade, boolean secondTable) {
        SwingUtilities.invokeLater(() -> addType(upgrade, secondTable));
    }

    private void addType(Boolean upgrade, boolean secondTable) {
        DefaultMutableTreeNode node = secondTable ? ArcaneVault.getMainBuilder().getPreviousSelectedNode() : ArcaneVault.getMainBuilder().getSelectedNode();
        String selected = ArcaneVault.getMainBuilder().getSelectedTabName();

        if (ArcaneVault.getSelectedType().getOBJ_TYPE_ENUM().isTreeEditType()) {
            AE_Manager.saveTreeIntoXML(ArcaneVault.getSelectedType());
        }
        String newName = DialogMaster
                .inputText("New type's name:", node.getUserObject().toString());
        if (newName == null) {
            return;
        }
        if (upgrade == null) {
            node = null;
            upgrade = false;
        }
        ArcaneVault.getMainBuilder().getTreeBuilder().newType(newName, node, selected,
                upgrade);

        SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.CLOSE);
        ArcaneVault.setDirty(true);
    }


    private static void initRarity(ObjType type) {
        if (type.getProperty(PROPS.ITEM_RARITY).isEmpty()) {
            type.setProperty(PROPS.ITEM_RARITY, StringMaster.format(ITEM_RARITY.COMMON.name()));
        }
    }

    private static void setDefaults(ObjType type) {
        // TODO Auto-generated method stub
    }


    public void remove(boolean secondTable) {
        SwingUtilities.invokeLater(() -> removeType(secondTable));
    }

    private void removeType(boolean secondTable) {
    }

    private void removeSelectedTypes() {
        for (ObjType selectedType : ArcaneVault.getSelectedTypes()) {
            if (selectedType.getOBJ_TYPE_ENUM() == DC_TYPE.ABILS) {
                AE_Manager.typeRemoved(ArcaneVault.getSelectedType());
            }
            DataManager.removeType(selectedType);
        }
        ArcaneVault.getMainBuilder().getTreeBuilder().remove();
        ArcaneVault.setDirty(true);

        SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.ERASE);

    }


    public static void checkReload() {
        if (ArcaneVault.isDirty()) {
            return;
        }
        reload();
    }

    public static void reload() {
        XML_Reader.readTypes(ArcaneVault.isMacroMode());
        ArcaneVault.getGame().initObjTypes();
    }

    // public   void startReloading() {
    //     TimerTaskMaster.newTimer(this, "checkReload", null, null, AvSaveHandler.RELOAD_PERIOD);
    // }

    public static AV_DataManager getAV_Manager() {
        if (manager == null) {
            manager = new AV_DataManager();
        }
        return manager;
    }

    public static void setManager(AV_DataManager manager) {
        AvModelHandler.manager = manager;
    }

    public static void addToWorkspace() {
        addToWorkspace(false);
    }

    public static void addToWorkspace(boolean alt) {
        for (ObjType objType : ArcaneVault.getSelectedTypes()) {
            if (alt) {
                objType.setWorkspaceGroup(WORKSPACE_GROUP.DEMO);
            }
            boolean result = ArcaneVault.getWorkspaceManager().addTypeToActiveWorkspace(
                    objType);
            if (!result) {
                ChangeEvent sc = new ChangeEvent(ArcaneVault.getMainBuilder().getTabBuilder()
                        .getWorkspaceTab().getTabs());
                ArcaneVault.getMainBuilder().getTabBuilder().stateChanged(sc);
            }
        }

    }

    public static void undo() {
        ObjType selectedType = ArcaneVault.getSelectedType();
        if (selectedType != null) {
            back(selectedType);
        }
        AvManager.refresh();

    }

    public static void addDefaultValues(boolean alt) {
        for (ObjType sub : DataManager.getTypes()) {
            DC_ContentValsManager.addDefaultValues(sub);
        }
        if (!alt)
            AvSaveHandler.saveAll();
    }

    public void clear(ObjType type) {
    }

    public boolean modified(ObjType type, String valName, String newValue) {
        if (valName.equalsIgnoreCase(type.getOBJ_TYPE_ENUM().getSubGroupingKey().getName())
                || valName.equalsIgnoreCase(type.getOBJ_TYPE_ENUM().getGroupingKey().getName())
                || (valName.equals(G_PROPS.BASE_TYPE.getName()))) {
            AV2.getMainBuilder().getEditViewPanel().setReload(true);
        }
        if (valName.equals(G_PROPS.NAME.getName())) {
            String oldName = type.getName();
            rename(type, oldName, newValue);

        } else if (valName.equalsIgnoreCase(G_PROPS.WORKSPACE_GROUP.getName())) {
            if (ArcaneVault.getWorkspaceManager().isDefaultTypeWorkspacesOn()) {
                if (ArcaneVault.getWorkspaceManager().getActiveWorkspace() != null) {
                    ArcaneVault.getWorkspaceManager().getActiveWorkspace().setDirty(true);
                }
            }
        } else {
            AV2.getVersionHandler().modified(type, valName, newValue);
        }
        if (valName.equals(G_PROPS.IMAGE.getName())) {
            ArcaneVault.getMainBuilder().getTreeBuilder().update();
        }
        String timestamp = TimeMaster.getTimeStamp() + StringMaster.wrapInParenthesis(TimeMaster.getTime() + "");
        type.setProperty(G_PROPS.TIMESTAMP, timestamp);

        return true;
    }

    public void rename(ObjType type, String oldName, String newValue) {
        DataManager.renameType(type, newValue);
        if (type.getOBJ_TYPE_ENUM().isTreeEditType()) {
            AE_Manager.typeRename(newValue, oldName);
        }

        ArcaneVault.getMainBuilder().getTreeBuilder().update();

        AV2.getVersionHandler().renamed(type, newValue);
    }
}
