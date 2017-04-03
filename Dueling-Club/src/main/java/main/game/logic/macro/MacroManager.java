package main.game.logic.macro;

import main.client.dc.Launcher;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.logic.macro.global.Campaign;
import main.game.logic.macro.global.TimeMaster;
import main.game.logic.macro.gui.WorldEditorInterface;
import main.game.logic.macro.gui.map.MapView;
import main.game.logic.macro.map.Region;
import main.game.logic.macro.travel.MacroParty;
import main.system.auxiliary.data.FileManager;
import main.system.datatypes.DequeImpl;
import main.system.entity.FilterMaster;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MacroManager {
    private final static String defaultWorldName = "Test World";
    // "Ersidris";
    private final static String defaultCampaignName = "Introduction";
    /*
     * Party Gold - each hero has a 'share' Perhaps heroes without enough
     * Influence should act on their own - buy stuff, sell stuff, play with AI
     * in battle
     */
    private static MapView mapView;
    private static JComponent mapComponent;
    private static MacroGame game;
    private static String campaignName;
    private static String worldName;
    private static Unit selectedPartyMember;
    private static boolean editMode;
    private static WorldEditorInterface editorView;
    private static DequeImpl<OBJ_TYPE> custom_OBJ_TYPES;
    private static DequeImpl<ObjType> customTypes;

    private static DequeImpl<ObjType> copyTypes;
    private static DequeImpl<ObjType> removedTypes;
    private static boolean save;
    private static boolean turnProcessing;
    private static boolean load;

    public static void loadGame(String path) {

    }

    public static void newGame() {
        load = false;
        game = new MacroGame();
        MacroGame.setGame(game);
        game.init();
        MacroEngine.init();
        game.start(true);
    }

    public static void exitGame() {
        if (game == null) {
            return;
        }
        // try { save();
        game = null;
        mapView = null;
        mapComponent = null;
        campaignName = null;
        worldName = null;
        MacroGame.setGame(null);
    }

    public static void endTurn() {
        // TODO preCheck orders are given or prompt
        turnProcessing = true;
        try {
            game.getManager().newTurn();
            TimeMaster.turnEnded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        turnProcessing = false;
    }

    private static void initCustomTypes() {
        customTypes = new DequeImpl<>();
        custom_OBJ_TYPES = new DequeImpl<>();
        for (OBJ_TYPE t : MACRO_OBJ_TYPES.values()) {
            File file = FileManager.getFile(getTypeDataPath() + t.getName() + ".xml");
            if (!file.isFile()) {
                continue;
            }
            custom_OBJ_TYPES.add(t);
            String xml = FileManager.readFile(file);
            customTypes.addAll(XML_Reader.createCustomTypeList(xml, t, MacroGame.getGame(),
                    !isEditMode(), true, false));

            // full type data or on top of base type?
        }
        for (ObjType type : customTypes) {
            type.setGenerated(true);
            DataManager.addType(type);
        }
    }

    public static ObjType getCustomType(String typeName) {
        if (typeName == null) {
            return null;
        }
        for (ObjType t : MacroManager.getCustomTypes()) {
            if (typeName.equals(t.getName())) {
                return t;
            }
        }

        return null;
    }

    public static void saveCustomTypes() {
        // saveCopyTypes();
        for (OBJ_TYPE t : custom_OBJ_TYPES) {
            String content = XML_Converter.openXmlFormatted(t.getName());
            for (ObjType type : customTypes) {

                if (type.getOBJ_TYPE_ENUM() == t) {
                    content += XML_Writer.getIncompleteTypeXML(type, type.getType());
                }
            }
            content += XML_Converter.closeXmlFormatted(t.getName());

            String path = getTypeDataPath();
            String fileName = t.getName() + ".xml";
            XML_Writer.write(content, path, fileName);
        }
    }

    private static String getCopyTypePath() {
        return PathFinder.getTYPES_PATH() + "\\campaign\\" + campaignName + "\\";
    }

    private static String getTypeDataPath() {
        return PathFinder.getTYPES_PATH() + (!isSave() ? "\\campaign\\" : "\\save\\")
                + getCampaignName() + "\\";
    }

    public static boolean isSave() {
        return save;
    }

    public static DequeImpl<OBJ_TYPE> getCustom_OBJ_TYPES() {
        return custom_OBJ_TYPES;
    }

    public static DequeImpl<ObjType> getCustomTypes() {
        return customTypes;
    }

    public static DequeImpl<ObjType> getRemovedTypes() {
        if (removedTypes == null) {
            removedTypes = new DequeImpl<>();
        }
        return removedTypes;

    }

    public static DequeImpl<ObjType> getCopyTypes() {
        return copyTypes;
    }

    public static JComponent getMacroViewComponent() {
        if (mapView == null) {
            mapView = new MapView();
            mapView.init();
        }
        if (mapComponent == null) {
            mapComponent = mapView.build();
        }
        return mapComponent;
    }

    public static String getWorldName() {
        if (worldName == null) {
            return defaultWorldName;
        }
        return worldName;
    }

    public static void setWorldName(String worldName) {
        MacroManager.worldName = worldName;
    }

    public static MapView getMapView() {
        return mapView;
    }

    public static void refreshGui() {
        mapView.refresh();

    }

    public static Unit getSelectedPartyMember() {
        if (selectedPartyMember == null) {
            return game.getPlayerParty().getLeader();
        }
        return selectedPartyMember;
    }

    public static void setSelectedPartyMember(Unit selectedPartyMember) {
        MacroManager.selectedPartyMember = selectedPartyMember;
    }

    public static MacroParty getActiveParty() {
        return game.getPlayerParty();
    }

    public static Campaign getCampaign() {
        return game.getCampaign();
    }

    public static String getCampaignName() {
        if (campaignName == null) {
            return defaultCampaignName;
        }
        return campaignName;
    }

    public static void setCampaignName(String campaignName) {
        MacroManager.campaignName = campaignName;
    }

    public static boolean isMacroGame() {
        // return game!=null;
        if (Launcher.getMainManager() == null) {
            return false;
        }
        return Launcher.getMainManager().isMacroMode();
    }

    public static JComponent getMapComponent() {
        return mapComponent;
    }

    public static MacroGame getGame() {
        return game;
    }

    public static boolean isEditMode() {
        return editMode;
    }

    public static void setEditMode(boolean b) {
        editMode = b;

    }

    public static String getDefaultworldname() {
        return defaultWorldName;
    }

    public static String getDefaultcampaignname() {
        return defaultCampaignName;
    }

    public static WorldEditorInterface getEditorView() {
        return editorView;
    }

    public static void setEditorView(WorldEditorInterface view) {
        editorView = view;
    }

    public static void initTypes() {
        initCustomTypes();
        // initCopyTypes();
    }

    public static List<ObjType> getCustomTypes(MACRO_OBJ_TYPES TYPE) {
        LinkedList<ObjType> list = new LinkedList<>(customTypes);
        FilterMaster.filterByProp(list, "Type", TYPE.getName());
        return list;
    }

    public static ObjType addCustomType(ObjType parent, String name) {
        ObjType type = new ObjType(parent, true);
        type.initType();
        type.setName(name);
        type.setProperty(G_PROPS.PARENT_TYPE, parent.getName());
        getCustomTypes().add(type);
        if (!getCustom_OBJ_TYPES().contains(type.getOBJ_TYPE_ENUM())) {
            getCustom_OBJ_TYPES().add(type.getOBJ_TYPE_ENUM());
        }
        type.setGenerated(true);
        DataManager.addType(type);
        return type;
    }

    // private static void saveCopyTypes() {
    // String typeMapContent = "";
    // // parentTypeName(child;child;...)]|[...
    // // only add custom type if non-dynamic value is changed, otherwise keep
    // // separate!
    // // find types for Tree - ? sure, let the copied type be passed, then
    // // moved to Custom is altered...
    // Map<ObjType, List<ObjType>> map = new XLinkedMap<>();
    // for (ObjType t : copyTypes) {
    // List<ObjType> list = map.getOrCreate(t.getType());
    // if (list == null) {
    // list = new LinkedList<>();
    // map.put(t.getType(), list);
    // }
    // list.add(t);
    // }
    // for (ObjType t : map.keySet()) {
    // String typeString = "";
    // for (ObjType t2 : map.getOrCreate(t)) {
    // typeString += t2.getName() + ";";
    // }
    // typeMapContent += t.getName()
    // + StringMaster.wrapInParenthesis(typeString)
    // + StringMaster.AND_PROPERTY_SEPARATOR;
    // }
    // XML_Writer.write(typeMapContent, getCopyTypePath(), "copyTypes.txt");
    // }
    //
    // private static void initCopyTypes() {
    // copyTypes = new DequeImpl<>();
    // String typeMapContent = null;
    // for (String part : StringMaster.openContainer(typeMapContent,
    // StringMaster.AND_PROPERTY_SEPARATOR)) {
    // String parentTypeName = VariableManager.removeVarPart(part);
    // ObjType parentType = DataManager.getType(parentTypeName);
    // if (parentType == null)
    // parentType = getCustomType(parentTypeName);
    // for (String name : StringMaster.openContainer(VariableManager
    // .getVars(part))) {
    // // TODO perhaps value-mods could be added to name?
    // ObjType copyType = new ObjType(parentType);
    // copyType.setName(name);
    // copyType.initType();
    // DataManager.addType(copyType);
    // }
    //
    // }
    // }
    // public static void save() {
    //
    // String data = "";
    // Map<OBJ_TYPE, StringBuilder> parts = new XLinkedMap<>();
    // for (MACRO_OBJ_TYPES m : MACRO_OBJ_TYPES.values())
    // parts.put(m, new StringBuilder());
    //
    // for (Obj obj : game.getState().getObjects().values()) {
    //
    // }
    // for (Region r : game.getRegions()) {
    // for (Place p : r.getPlaces()) {
    // String node = p.getSaveData();
    // node = XML_Converter.wrap(p.getName(), node);
    // data += node;
    // // TODO IS IT BETTER TO JUST WRITE FOR *ALL* MACRO_OBJECTS?
    // // just separate by TYPE at least... and don't forget custom
    // // heroes/items!
    // if (p instanceof Town) {
    // Town town = (Town) p;
    //
    // parts.getOrCreate(MACRO_OBJ_TYPES.TOWN).append("");
    //
    // for (Shop s : town.getShops()) {
    // s.getSaveData();
    // for (ObjType t : s.getItems()) {
    // parts.getOrCreate(t.getOBJ_TYPE_ENUM()).append(string);
    // if (t.isCustom()) { // with traits or so
    // customTypes += XML_Writer.getTypeXML(t, null);
    // }
    // }
    // }
    // }
    //
    // // a special function to write/read each kind of Macro Object?
    // // E.g. Shops would write their gold and items...
    // // basically, we just need to set some values on top of the Base
    // // Type, right?
    //
    // }
    //
    // }
    //
    // }
    // File file = new File(filePath);
    // String fullXml = FileManager.readFile(file);
    // // OR parse into Doc and use nodes ... but would this xml be a proper
    // // one? it should be...
    // Document doc = XML_Converter.getDoc(fullXml);
    // for (Node typesNode : XML_Converter.getNodeList(doc)) {
    // OBJ_TYPE TYPE = MACRO_OBJ_TYPES.getType(typesNode.getNodeName());
    // // if (TYPE==null)
    // // TYPE =OBJ_TYPES.getType(typesNode.getNodeName()) ;
    // for (Node typeNode : XML_Converter.getNodeList(typesNode)) {
    //
    // // TYPE = ;
    // // xml = xml.re
    // //
    // // for (MACRO_OBJ_TYPES t : MACRO_OBJ_TYPES.values()){
    // // if (FileManager.getFile(filePath)!=null)
    // // XML_Reader.readCustomTypeFile(file, t, game);
    // // }
    // String typeName = typeNode.getNodeName();
    // ObjType type = DataManager.getType(typeName, TYPE);
    // // TODO add cloned type? or overwrite is ok?
    // for (Node valNode : XML_Converter.getNodeList(typeNode)) {
    // type.setValue(valNode.getNodeName(),
    // valNode.getTextContent());
    // }
    // }
    //
    // }
    // newGame();
    /* SAVE
         * create a joint xml file with all necessary types...
		 * 
		 * some meta-data in the header perhaps...
		 * 
		 * a somewhat special format perhaps
		 */

    public static List<Region> getRegions() {
        return getGame().getWorld().getRegions();
    }

    public static boolean isTurnProcessing() {
        return turnProcessing;
    }

    public static boolean isLoad() {
        return load;
    }

    // each object must maintain its values dynamically, always...

    // consider shops... surely each must have its own overwritten type
    // list?
    //

    // the need for unique names is too obvious to ignore! but in most
    // places, we only store real names...
}
