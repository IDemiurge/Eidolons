package main.level_editor.backend.functions.io;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
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

        xmlBuilder.open(LocationBuilder.MODULES_NODE);

        for (Module module : floor.getModules()) {
            //recursive? maybe better just plain loops - modules>zones>blocks with common methods
            String contents = toXml(module, false);
            xmlBuilder.appendNode(contents, module.getName());
        }
        xmlBuilder.close(LocationBuilder.MODULES_NODE);

        xmlBuilder.close("Plan");

        xmlBuilder.append(buildIdMap(floor));
        xmlBuilder.append(buildCoordinateMap(floor));

        for (LE_Handler handler : floor.getManager().getHandlers()) {
            String xml = handler.getXml();
            if (xml.isEmpty()) {
                continue;
            }
            xmlBuilder.append(xml).append("\n");
        }

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
        return XML_Converter.wrap(LocationBuilder.OBJ_NODE_NEW, builder.toString());
    }

    //TODO DC_TYPE ?
    private static String buildIdMap(Floor floor) {
        StringBuilder builder = new StringBuilder();
        Map<Integer, BattleFieldObject> map = floor.getGame().getSimIdManager().getObjMap();
        for (Integer integer : map.keySet()) {
            Integer id = map.get(integer).getId();
            ObjType type = map.get(integer).getType();
            builder.append(id).append("=").append(type.getName()).append(";");

        }
        return XML_Converter.wrap(LocationBuilder.ID_MAP, builder.toString());
    }

    //must be a valid floor in itself?! No global id's then? So we can re-use modules, mix them up...
    //interesting. So maybe we can have ... duplicate id maps?
    public static String toXml(Module module, boolean standalone) {
        if (standalone) {
        }
        XmlStringBuilder xmlBuilder = new XmlStringBuilder();
        xmlBuilder.appendNode(new ModuleData(new LE_Module(module)).toString(),
                FloorLoader.DATA);
        xmlBuilder.open("Zones");
        for (LevelZone zone : module.getZones()) {
            xmlBuilder.appendNode(toXml(zone), "Zone");
        }
        xmlBuilder.close("Zones");

        return xmlBuilder.toString();
    }

    private static String toXml(LevelZone zone) {
        XmlStringBuilder xmlBuilder = new XmlStringBuilder();
        xmlBuilder.appendNode(new ZoneData(new LE_Zone(zone)).toString(),
                FloorLoader.DATA);
        xmlBuilder.open("Blocks");
        for (LevelBlock block :   zone.getSubParts()) {
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
      Set<Coordinates>  missing = CoordinatesMaster.getMissingCoordinatesFromRect(c, w, h, block.getCoordinatesList());

        xmlBuilder.open("Missing");
        for (Coordinates coordinate :  missing) {
            xmlBuilder.append (coordinate+";");
        }
        xmlBuilder.close("Missing");

        return xmlBuilder.toString();
    }
}
