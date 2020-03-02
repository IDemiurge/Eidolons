package main.level_editor.functions.io;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.data.xml.XML_Converter;
import main.data.xml.XmlStringBuilder;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.level_editor.struct.boss.BossDungeon;
import main.level_editor.struct.level.Floor;
import main.system.auxiliary.data.MapMaster;

import java.util.Map;
import java.util.Set;

public class LE_XmlMaster {

    public static String toXml(BossDungeon dungeon) {

        StringBuilder xmlBuilder = new StringBuilder();
        for (Floor floor : dungeon.getFloors()) {
            xmlBuilder.append(toXml(floor));

        }


        return xmlBuilder.toString();
    }

    public static String toXml(Floor floor) {
        XmlStringBuilder xmlBuilder = new XmlStringBuilder();

        //from old - dungeon params props etc

        xmlBuilder.open("Plan");

        xmlBuilder.append(buildIdMap(floor));
        xmlBuilder.append(buildCoordinateMap(floor));
        String planXml = (floor.getGame().getDungeonMaster().getDungeonWrapper()).getPlan().getXml();

        xmlBuilder.append(planXml );

        xmlBuilder.close("Plan");

        xmlBuilder.open(LocationBuilder.MODULES_NODE);
        for (Module module : floor.getModules()) {
            String contents = toXml(module, false);
            xmlBuilder.appendNode(contents, module.getName());
        }
        xmlBuilder.close(LocationBuilder.MODULES_NODE);
        return XML_Converter.wrap("Floor", xmlBuilder.toString() ); //name?
    }

    private static String buildCoordinateMap(Floor floor) {
        StringBuilder builder = new StringBuilder();
        Map<Integer, Obj> map = floor.getGame().getSimIdManager().getObjMap();
        for (Coordinates c : floor.getGame().getCoordinates()) {
            Set<BattleFieldObject> set = floor.getGame().getObjectsOnCoordinate(c);
            if (set.isEmpty()) {
                continue;
            }
            builder.append(c).append("=");
            for (BattleFieldObject obj : set) {
                Integer id = (Integer) MapMaster.getKeyForValue_(map, obj);
                builder.append(id);
            }
            builder.append(";");

        }
        return XML_Converter.wrap(LocationBuilder.OBJ_NODE_NEW, builder.toString());
    }

    //TODO DC_TYPE ?
    private static String buildIdMap(Floor floor) {
        StringBuilder builder = new StringBuilder();
        Map<Integer, Obj> map = floor.getGame().getSimIdManager().getObjMap();
        for (Integer integer : map.keySet()) {
            Integer id = map.get(integer).getId();
            ObjType type = map.get(integer).getType();
            builder.append(id).append("=").append(type.getName()).append(";");

        }
        return XML_Converter.wrap(LocationBuilder.ID_MAP, builder.toString());
    }

    public static String toXml(Module module, boolean standalone) {
        StringBuilder xmlBuilder = new StringBuilder();
        //must be a valid floor in itself?! No global id's then? So we can re-use modules, mix them up...
        //interesting. So maybe we can have ... duplicate id maps?
        /*
        we need to have single id_map and coordinate->ids for FLOOR
        we can build it here too,
         */

        if (standalone) {
            /*
            LAYERS

            common id and coordinate map!

             */

            //transform id maps!
            //ignore origin
        }
//        Set<BattleFieldObject> objects =
//        LE_GameSim.getGame().getMetaMaster().getModuleMaster().getObjectsForModule(module);
//        Set<Integer> ids =
//        LE_GameSim.getGame().toFloorIds(objects);

        //in fact splitting objects on WRITE may not be necessary.

        return xmlBuilder.toString();
    }
}
