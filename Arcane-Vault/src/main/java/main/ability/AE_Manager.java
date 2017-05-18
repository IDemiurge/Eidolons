package main.ability;

import main.ability.gui.AE_EditPanel;
import main.ability.gui.AE_Element;
import main.ability.gui.AE_MainPanel;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.PROPS;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.AE_Item;
import main.data.ability.ARGS;
import main.data.ability.Mapper;
import main.data.ability.construct.VariableManager;
import main.data.ability.construct.XmlDocHolder;
import main.data.xml.XML_Converter;
import main.entity.type.ObjType;
import main.launch.ArcaneVault;
import main.system.auxiliary.log.LogMaster;
import org.w3c.dom.Node;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AE_Manager {
    public static Map<String, AE_MainPanel> cacheMap = new ConcurrentHashMap<>();

    private static HashMap<AE_Item, AE_EditPanel> smallCache = new HashMap<>();

    private static Map<AE_MainPanel, HashMap<AE_Item, AE_EditPanel>> smallCaches = new HashMap<>();

    private static boolean manualVars = true;

    Map<ARGS, AE_Item> ARG_Map; // ??

    Map<ARGS, AE_Element> AE_Element_Cache;

    public static AE_Item getAE_Item(String selectedItem) {
        return Mapper.getItem(selectedItem);
    }

    public static void typeRename(String newValue, String oldName) {
        cacheMap.put(newValue, cacheMap.get(oldName));
        cacheMap.remove(oldName);
    }

    public static void typeRemoved(ObjType t) {
        cacheMap.remove(t.getName());

    }

    public static AE_MainPanel getAE_View(String typeName) {
        AE_MainPanel panel = cacheMap.get(typeName);
        boolean valid=panel != null;
        if (valid){
            valid = panel.getEditPanel()!=null ;
        }
        if (!valid) {
            LogMaster.log(0, "creating AE view ..."
                    + typeName);
            panel = new AE_MainPanel((typeName));
            cacheMap.put(typeName, panel);
            smallCache = new HashMap<>();
            smallCaches.put(panel, smallCache);
        } else {
            LogMaster.log(0, "AE view FOUND! - "
                    + typeName);
            smallCache = smallCaches.get(panel);
            if (smallCache == null) {
                LogMaster.log(0,
                        "*** smallCache NOT FOUND! - " + typeName);
            }
        }

        return panel;
    }

    public static Node getDoc(String typeName) {
        OBJ_TYPE TYPE = null;
        if (!ArcaneVault.isDialogueMode()) {
            TYPE = DC_TYPE.ABILS;
        } else {
            TYPE = DC_TYPE.DIALOGUE;
        }


        ObjType type;
        if (ArcaneVault.isMacroMode()) {
            type = DataManager.getType(typeName);
//			DialogueType diagType = (DialogueType) type;
//			return diagType.getDoc();
        }
        type = DataManager.getType(typeName, TYPE.getName());
        XmlDocHolder abilType = (XmlDocHolder) type;
        // return XML_Converter.getDoc(abilType.getProperty(G_PROPS.ABILITIES));
        return abilType.getDoc();

    }

    public static void saveTreesIntoXML() {
        OBJ_TYPE TYPE = null;
        PROPERTY XML_PROP = null;
        if (!ArcaneVault.isDialogueMode()) {
            TYPE = DC_TYPE.ABILS;
            XML_PROP = G_PROPS.ABILITIES;
        } else {
             TYPE = DC_TYPE.DIALOGUE;
             XML_PROP = PROPS.DIALOGUE_DATA;
        }
        for (String typeName : cacheMap.keySet()) {
            ObjType type = DataManager.getType(typeName, TYPE);
            if (type == null) {
                continue;
            }
            saveTreeIntoXML(TYPE, XML_PROP, type);
            // dirty flag can help
            // XML_Writer.writeXML_ForType(type, TYPE);
        }
    }

    public static void saveTreeIntoXML(ObjType type) {
        if (type == null) {
            cacheMap.remove(type.getName());
        }
        PROPERTY XML_PROP;
        if (!ArcaneVault.isMacroMode()) {
            XML_PROP = G_PROPS.ABILITIES;
        } else {
            XML_PROP = MACRO_PROPS.DIALOGUE_TREE;
        }
        saveTreeIntoXML(type.getOBJ_TYPE_ENUM(), XML_PROP, type);
    }

    public static void saveTreeIntoXML(OBJ_TYPE TYPE, PROPERTY XML_PROP,
                                       ObjType type) {
        String newXml;
        try {

            JTree tree = cacheMap.get(type.getName()).getTree();
            if (tree == null) {
                return;
            }
            newXml = XML_Converter.getXMLfromTree(tree);
        } catch (Exception e) {
            e.printStackTrace();
            LogMaster.log(2, type.getName()
                    + " is not ready to be saved!");
            return;
        }

        type.setProperty(XML_PROP, newXml);

        VariableManager.setAbilityVars(type, newXml);
    }

    public static boolean isManualVars() {
        return manualVars;
    }

    public static void setManualVars(boolean manualVars) {
        AE_Manager.manualVars = manualVars;
    }

    public static AE_EditPanel getAE_EditPanel(AE_MainPanel mainPanel,
                                               AE_Item item, int index) {
        if (item == null) {
            return null;
        }
        // AE_EditPanel panel = smallCache.getOrCreate(item);
        // if (panel == null) {
        // main.system.auxiliary.LogMaster
        // .log(0, "*** ae_edit panel NOT FOUND! - " + item.getName()
        // + index);
        // panel = new AE_EditPanel(item, mainPanel, index);
        //
        // smallCache.put(item, panel);
        // }
        // else {
        // main.system.auxiliary.LogMaster
        // .log(0, ">>> ae_edit panel  FOUND! - " + item.getName()
        // + index);
        // // smallCache = new HashMap<AE_Item, AE_EditPanel>();
        // panel = new AE_EditPanel(item, mainPanel, index);
        // if (panel.checkContainerExpansionRequired())
        // panel.expandContainer();
        // }
        AE_EditPanel panel = new AE_EditPanel(item, mainPanel, index);
        if (item.isContainer()) {
            if (panel.checkContainerExpansionRequired()) {
                panel.expandContainer();
            }
        }
        return panel;
    }
}
