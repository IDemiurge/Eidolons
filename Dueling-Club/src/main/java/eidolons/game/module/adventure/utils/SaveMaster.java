package eidolons.game.module.adventure.utils;

import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.adventure.MacroManager;
import eidolons.game.module.adventure.entity.party.MacroParty;
import eidolons.game.module.adventure.map.Place;
import eidolons.game.module.adventure.map.Region;
import main.content.ContentValsManager;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.entity.obj.Obj;
import main.system.auxiliary.data.FileManager;
import main.system.threading.Weaver;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SaveMaster {

    private static Map<OBJ_TYPE, StringBuilder> builders;

    public static boolean isPropSaved(PROPERTY p, OBJ_TYPE TYPE) {
        return false;
    }

    public static void load(String fileName) {
        /*
         * load types... init world from save -
		 * dynamic values - can they be set for Type? 
		 */
        String content = FileManager.readFile(new File(getSavePath() + fileName + ".xml"));
        for (Node node : XML_Converter.getNodeList(XML_Converter.getDoc(content))) {
            OBJ_TYPE TYPE = ContentValsManager.getOBJ_TYPE(node.getNodeName());
            XML_Reader.createCustomTypeList(XML_Converter.getStringFromXML(node), TYPE, MacroGame
             .getGame(), true, true);
        }


    }

    public static void saveInNewThread() {
        Weaver.inNewThread(new Runnable() {
            public void run() {
                save();
            }
        });

    }

    public static boolean save() {
        // BACKUP
        // TODO SAVE DYNAMIC VALUES - DISCOVERY STATUS, TIME, COORDINATES,
        // PROGRESS, ...
        builders = new HashMap<>();
        for (Obj obj : MacroGame.getGame().getObjects()) {
            // same format as WE?
            // every object?
            StringBuilder builder = builders.get(obj.getOBJ_TYPE_ENUM());
            if (builder == null) {
                continue;
            }
            XML_Writer.getTypeXML_Builder(obj, builder, obj.getType());
            builders.put(obj.getOBJ_TYPE_ENUM(), builder);
            // both macro and micro types...
            // dynamics should be initialized
            // if (?!)
            // return false;
        }
        String content = "";

        for (OBJ_TYPE TYPE : builders.keySet()) {
            content += XML_Converter.wrap(TYPE.getName(), builders.get(TYPE).toString());
        }

        XML_Writer.write(content, getSavePath(), getSaveFileName());

        return true;

    }

    private static String getSaveFileName() {
        // ++ unique code generated per Macro Game and stored in Campaign obj
        return MacroManager.getActiveParty().getName();
    }

    private static String getSavePath() {
        return PathFinder.getXML_PATH() + "macro\\saves\\";
    }

    public static void saveTheWorld() {
        //all locations to regions, etc
        for (Region region : MacroManager.getGame().getState().getRegions()) {
            String places = "";
            for (Place sub : MacroManager.getGame().getPlaces()) {
                if (sub.getRegion() != region)
                    continue;
                places +=
                 sub.getNameAndCoordinate() + ";";
            }
            region.setProperty(MACRO_PROPS.PLACES, places, true);

            String parties = "";
            for (MacroParty sub : MacroManager.getGame().getParties()) {
                if (sub.getRegion() != region)
                    continue;
                parties +=
                 sub.getNameAndCoordinate() + ";";
            }
            region.setProperty(MACRO_PROPS.PARTIES, parties, true);

        }
        XML_Writer.writeXML_ForTypeGroup(MACRO_OBJ_TYPES.REGION);

    }
}
