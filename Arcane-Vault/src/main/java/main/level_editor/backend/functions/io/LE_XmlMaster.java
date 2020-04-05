package main.level_editor.backend.functions.io;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.struct.BlockData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ZoneData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Block;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Module;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Zone;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.data.xml.XML_Converter;
import main.data.xml.XmlStringBuilder;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.struct.boss.BossDungeon;
import main.level_editor.backend.struct.level.Floor;
import main.system.auxiliary.data.MapMaster;

import java.util.LinkedHashMap;
import java.util.List;
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

        xmlBuilder.open(FloorLoader.MODULES);

        for (Module module : floor.getModules()) {
            //recursive? maybe better just plain loops - modules>zones>blocks with common methods
            String contents = toXml(module, false);
            xmlBuilder.append(contents);
        }
        xmlBuilder.close(FloorLoader.MODULES);


        xmlBuilder.append("\n").append(buildIdMap(floor));
        xmlBuilder.append("\n").append(buildCoordinateMap(floor));

        xmlBuilder.append("\n").open(FloorLoader.DATA_MAPS);
        for (LE_Handler handler : floor.getManager().getHandlers()) {
            String xml = handler.getDataMapString();
            if (xml.isEmpty()) {
                continue;
            }
            xmlBuilder.append(xml).append("\n");
        }
        xmlBuilder.close(FloorLoader.DATA_MAPS).append("\n");

//        xmlBuilder.append("\n").open(FloorLoader.COORDINATE_DATA);
        xmlBuilder.append("\n").open(FloorLoader.COORDINATES_VOID);
        for (DC_Cell cell : floor.getGame().getCells()) {
            if (cell.isVOID()) {
                xmlBuilder.append(cell.getCoordinates().toString()).append(";");
            }
        }
        xmlBuilder.close(FloorLoader.COORDINATES_VOID).append("\n");
//        xmlBuilder.close(FloorLoader.COORDINATE_DATA).append("\n");

        for (LE_Handler handler : floor.getManager().getHandlers()) {
            String xml = handler.getXml();
            if (xml.isEmpty()) {
                continue;
            }
            xmlBuilder.append(xml).append("\n");
        }
        xmlBuilder.close("Plan");
        return XML_Converter.wrap("Floor", xmlBuilder.toString()); //name?
    }

    private static String buildCoordinateMap(Floor floor) {
        StringBuilder builder = new StringBuilder();
        Map<Integer, BattleFieldObject> map = floor.getGame().getSimIdManager().getObjMap();
        for (Coordinates c : floor.getGame().getCoordinates()) {
            Set<BattleFieldObject> set = floor.getGame().getObjectsOnCoordinate(c);
            if (set.isEmpty()) {
                continue;
            }
            builder.append(c).append("=");
            for (BattleFieldObject obj : set) {
                Integer id = (Integer) MapMaster.getKeyForValue_(map, obj);
                if (id == null) {
                    continue;
                }
                builder.append(id);
            }
            builder.append(";");

        }
        return XML_Converter.wrap(FloorLoader.OBJ_NODE_NEW, builder.toString());
    }

    //TODO DC_TYPE ?
    private static String buildIdMap(Floor floor) {
        StringBuilder builder = new StringBuilder();
        Map<Integer, BattleFieldObject> map = floor.getGame().getSimIdManager().getObjMap();
        Map<ObjType, List<Integer>> nestedMap = new LinkedHashMap<>();
        for (Integer integer : map.keySet()) {
            try {
                Integer id =
                        floor.getGame().getSimIdManager().getId(map.get(integer));
                ObjType type = map.get(integer).getType();
                MapMaster.addToListMap(nestedMap, type, id);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

        for (ObjType type : nestedMap.keySet()) {
            builder.append(type).append("=");
            for (Integer integer : nestedMap.get(type)) {
                builder.append(integer).append(",");
            }
            builder.append(";");
        }

        return XML_Converter.wrap(FloorLoader.ID_MAP, builder.toString());
    }

    //must be a valid floor in itself?! No global id's then? So we can re-use modules, mix them up...
    //interesting. So maybe we can have ... duplicate id maps?
    public static String toXml(Module module, boolean standalone) {
        if (standalone) {
            //TODO
        }
        XmlStringBuilder xmlBuilder = new XmlStringBuilder();
        xmlBuilder.append("\n").open(module.getName());
        xmlBuilder.appendNode(new ModuleData(new LE_Module(module)).toString(),
                FloorLoader.DATA);
        xmlBuilder.append("\n").open("Zones");
        for (LevelZone zone : module.getZones()) {
            xmlBuilder.appendNode(toXml(zone), "Zone");
        }
        xmlBuilder.close("Zones").append("\n");
        xmlBuilder.close(module.getName()).append("\n");

        return xmlBuilder.toString();
    }

    private static String toXml(LevelZone zone) {
        XmlStringBuilder xmlBuilder = new XmlStringBuilder();
        xmlBuilder.appendNode(new ZoneData(new LE_Zone(zone)).toString(),
                FloorLoader.DATA);
        xmlBuilder.open("Blocks");
        for (LevelBlock block : zone.getSubParts()) {
            xmlBuilder.appendNode(toXml(block), "Block");
        }
        xmlBuilder.close("Blocks");

        return xmlBuilder.toString();
    }

    private static String toXml(LevelBlock block) {

        XmlStringBuilder xmlBuilder = new XmlStringBuilder();
        xmlBuilder.appendNode(new BlockData(new LE_Block(block)).toString(),
                FloorLoader.DATA);
        int w = block.getWidth();
        int h = block.getHeight();
        Coordinates c = block.getOrigin();
        Set<Coordinates> missing = CoordinatesMaster.getMissingCoordinatesFromRect(c, w, h, block.getCoordinatesSet());

        xmlBuilder.open("Missing");
        for (Coordinates coordinate : missing) {
            xmlBuilder.append(coordinate + ";");
        }
        xmlBuilder.close("Missing");

        return xmlBuilder.toString();
    }
}
