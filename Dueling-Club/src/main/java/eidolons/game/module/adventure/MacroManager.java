package eidolons.game.module.adventure;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.macro.MacroPartyManager;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.module.adventure.entity.party.MacroParty;
import eidolons.game.module.adventure.global.Campaign;
import eidolons.game.module.adventure.map.Place;
import eidolons.game.module.adventure.map.Region;
import main.system.GuiEventManager;
import main.system.launch.CoreEngine;

import java.util.List;

import static main.system.GuiEventType.SCREEN_LOADED;
import static main.system.MapEvent.*;

public class MacroManager {
    private final static String defaultWorldName = "Test World";
    private final static String defaultCampaignName = "Introduction";
    private static MacroGame game;
    private static String campaignName;
    private static String worldName;
    private static Unit selectedPartyMember;
    private static boolean save;
    private static boolean load;
    private static String scenario = "Mistfall";
    private static MetaGameMaster metaMaster;


    public static void setScenario(String scenario) {
        MacroManager.scenario = scenario;
    }

    public static void newGame() {
        newGame(scenario);
    }

    public static void newGame(String scenario) {
        load = false;
        metaMaster = new ScenarioMetaMaster(scenario) {
            @Override
            protected PartyManager createPartyManager() {
                return new MacroPartyManager(this);
            }
        };
        if (!CoreEngine.isMapEditor()) {
            metaMaster.init();
            if (metaMaster.getPartyManager().getParty() == null)
                return;
        }
        game = new MacroGame();
        if (!CoreEngine.isMapEditor()) {
            MacroEngine.init();
            game.start(true);
        }

        GuiEventManager.bind(MAP_READY, p -> {
            initComponents();
        });
        GuiEventManager.trigger(SCREEN_LOADED);

    }

    private static void initComponents() {
        GuiEventManager.trigger(CREATE_PARTY,
         game.getPlayerParty());
        for (Region sub : game.getWorld().getRegions()) {
            for (Place sub1 : sub.getPlaces()) {
                GuiEventManager.trigger(CREATE_PLACE,
                 sub1);
            }
            for (MacroParty sub1 : sub.getParties()) {
                if (game.getPlayerParty() == sub1)
                    continue;
                GuiEventManager.trigger(CREATE_PARTY,
                 sub1);
            }
        }
    }

    public static void exitGame() {
        if (game == null) {
            return;
        }
        // try { save();
        game = null;
        campaignName = null;
        worldName = null;
        MacroGame.setGame(null);
    }


    public static boolean isSave() {
        return save;
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


    public static MacroGame getGame() {
        return game;
    }

    public static boolean isEditMode() {
        return CoreEngine.isMapEditor();
    }


    public static List<Region> getRegions() {
        return getGame().getWorld().getRegions();
    }

    public static boolean isLoad() {
        return load;
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
    // list = new ArrayList<>();
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
    // for (String part : StringMaster.open(typeMapContent,
    // StringMaster.AND_PROPERTY_SEPARATOR)) {
    // String parentTypeName = VariableManager.removeVarPart(part);
    // ObjType parentType = DataManager.getType(parentTypeName);
    // if (parentType == null)
    // parentType = getCustomType(parentTypeName);
    // for (String name : StringMaster.open(VariableManager
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
    // for (Place portrait : r.getPlaces()) {
    // String node = portrait.getSaveData();
    // node = XML_Converter.wrap(portrait.getName(), node);
    // data += node;
    // // TODO IS IT BETTER TO JUST WRITE FOR *ALL* MACRO_OBJECTS?
    // // just separate by TYPE at least... and don't forget custom
    // // heroes/items!
    // if (portrait instanceof Town) {
    // Town town = (Town) portrait;
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


    // each object must maintain its values dynamically, always...

    // consider shops... surely each must have its own overwritten type
    // list?
    //

    // the need for unique names is too obvious to ignore! but in most
    // places, we only store real names...
}
