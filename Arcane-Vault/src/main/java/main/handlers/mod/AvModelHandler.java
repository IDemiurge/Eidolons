package main.handlers.mod;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PROPS;
import eidolons.game.Simulation;
import main.AV_DataManager;
import main.ability.AE_Manager;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.ItemEnums.ITEM_RARITY;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.entity.type.TypeBuilder;
import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.handlers.control.AvSelectionHandler;
import main.system.auxiliary.CloneMaster;
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
import javax.swing.tree.TreeNode;

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
        SwingUtilities.invokeLater(() -> {
            DefaultMutableTreeNode node = secondTable
                    ? ArcaneVault.getMainBuilder().getPreviousSelectedNode()
                    : ArcaneVault.getMainBuilder().getSelectedNode();
            ObjType type = addType(upgrade, secondTable, true);
            ArcaneVault.getMainBuilder().getTreeBuilder().newType(type, node );
        });
    }

    public ObjType addType(Boolean upgrade, boolean secondTable, boolean defaultParams) {
        DefaultMutableTreeNode node = secondTable
                ? ArcaneVault.getMainBuilder().getPreviousSelectedNode()
                : ArcaneVault.getMainBuilder().getSelectedNode();
        String selected = ArcaneVault.getMainBuilder().getSelectedTabName();

        if (ArcaneVault.getSelectedType().getOBJ_TYPE_ENUM().isTreeEditType()) {
            AE_Manager.saveTreeIntoXML(ArcaneVault.getSelectedType());
        }
        String newName = DialogMaster
                .inputText("New type's name:", node.getUserObject().toString());
        if (newName == null) {
            return null;
        }
        DefaultMutableTreeNode parentNode=node==null? null: (DefaultMutableTreeNode) node.getParent();
        if (upgrade == null) {
            parentNode= (DefaultMutableTreeNode) node.getParent();
            node = null;
            upgrade = false;
        }
        ObjType parentType=node==null? null:(ObjType) node.getUserObject();
        ObjType type = newType(newName, selected, upgrade, node==null,
                defaultParams, parentType,  parentNode);

        SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.CLOSE);
        ArcaneVault.setDirty(true);
        return type;
    }

    public ObjType newType(String newName, String TYPE, boolean upgrade, boolean empty,
                           boolean defaultParams, ObjType objType, DefaultMutableTreeNode parent) {
        ObjType newType = null;
        if (empty) {
            newType = getEmptyType(TYPE, newName, true, defaultParams);
        } else {
            newType = CloneMaster.getTypeCopy(objType, newName, ArcaneVault.getGame(), TYPE);
            newType.setGenerated(false);
        }
        newType.setProperty(G_PROPS.NAME, newName);
        newType.setProperty(G_PROPS.DISPLAYED_NAME, newName);
        if (empty) {
            newType.setProperty(DataManager.getSubGroupingKey(TYPE), parent.getUserObject()
                    .toString());
            newType.setProperty(DataManager.getGroupingKey(TYPE), ArcaneVault.getMainBuilder()
                    .getSelectedSubTabName());
        }
        if (upgrade) {
            newType.setProperty(G_PROPS.BASE_TYPE, objType.getName());
        }

        DataManager.addType(newName, TYPE, newType);
        Simulation.getGame().initType(newType);

        return newType;
    }

    private ObjType getEmptyType(String TYPE, String newName, boolean setDefaultProps, boolean setDefaultParams) {
        ObjType type = TypeBuilder.getTypeInitializer().getOrCreateDefault(
                ContentValsManager.getOBJ_TYPE(TYPE), setDefaultProps, setDefaultParams);
        type.setName(newName);
        Simulation.getGame().initType(type);
        return type;
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

    public   void addDialog() {
        // int option = DialogMaster.optionChoice("Choose one: ",
        //         "Etalon", "Empty", "Upgrade " + type, "Clone " + type);
        // if (option==0){
        //     String etalon= getDialogHandler().etalonChoice();
        //     //select ?
        //     // getAssembler().createComboType();
        //     // newType(name, TYPE, false , false, false, )
        // }

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
