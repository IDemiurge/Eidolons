package main.level_editor.backend.functions.io;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.data.xml.XML_Converter;
import main.data.xml.XmlStringBuilder;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.auxiliary.data.MapMaster;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class LE_XmlMaster extends LE_Handler {

    public LE_XmlMaster(LE_Manager manager) {
        super(manager);
    }


    public String toXml(Location floor) {
        return toXml(floor, null);
    }

    public String toXml(Location floor, Module standalone) {
        XmlStringBuilder xmlBuilder = new XmlStringBuilder();

        //from old - dungeon params props etc

        xmlBuilder.appendNode((floor).getData().toString(),
                FloorLoader.DATA);
        xmlBuilder.append("\n").append(buildIdMap(standalone)); //ideally after modules, but read order..
        xmlBuilder.open(FloorLoader.MODULES);

        for (Module module : floor.getModules()) {
            if (standalone != null) {
                if (module != standalone) {
                    continue;
                }
            }
            //recursive? maybe better just plain loops - modules>zones>blocks with common methods
            String contents = toXml(module, false);
            if (standalone != null) {
                return contents;
            }
            xmlBuilder.append(contents);
        }
        xmlBuilder.close(FloorLoader.MODULES);

        return XML_Converter.wrap("Floor", xmlBuilder.toString());
    }

    public String getMetaXml(Module standalone) {
        XmlStringBuilder xmlBuilder = new XmlStringBuilder();
        Function<Integer, Boolean> idFilter = getIdFilter(standalone);

        xmlBuilder.append("\n").open(FloorLoader.DATA_MAPS);
        for (LE_Handler handler : LevelEditor.getManager().getHandlers()) {
            String xml = handler.getDataMapString(idFilter);
            if (xml.isEmpty()) {
                continue;
            }
            xmlBuilder.append(xml).append("\n");
        }
        xmlBuilder.close(FloorLoader.DATA_MAPS).append("\n");

//        xmlBuilder.append("\n").open(FloorLoader.COORDINATE_DATA);
//        xmlBuilder.close(FloorLoader.COORDINATE_DATA).append("\n");

        for (LE_Handler handler : LevelEditor.getManager().getHandlers()) {
            String xml = handler.getXml(idFilter);
            if (xml.isEmpty()) {
                continue;
            }
            xmlBuilder.append(xml).append("\n");
        }
        return xmlBuilder.toString();
    }

    private Function<Integer, Boolean> getIdFilter(Module standalone) {
        return id ->
                standalone == null || standalone.getCoordinatesSet().
                        contains(LevelEditor.getManager().getIdManager().getObjectById(id).
                                getCoordinates());
    }

    private String buildBorderMap(Module module) {
        return buildCoordinateMap(module, true);
    }

    private String buildCoordinateMap(Module module) {
        return buildCoordinateMap(module, false);
    }

    private String buildCoordinateMap(Module module, boolean borders) {
        StringBuilder builder = new StringBuilder();
        Map<Integer, BattleFieldObject> map = LevelEditor.getGame().getSimIdManager().getObjMap();
        for (Coordinates c : module.initCoordinateSet(false)) {
            Set<BattleFieldObject> set = LevelEditor.getGame().getObjectsOnCoordinate(c);
            if (!borders)
                for (Entrance entrance : getTransitHandler().entrances) {
                    if (entrance.getCoordinates().equals(c)) {
                        set.add(entrance);
                    }
                }
            set.removeIf(obj -> obj.isModuleBorder() != borders);
            //TODO  save separately!!!!
            if (set.isEmpty()) {
                continue;
            }
            builder.append(c);
            if (borders) {
                builder.append(";");
                continue; //just the coordinate
            }
            builder.append("=");
            for (BattleFieldObject obj : set) {

                Integer id = (Integer) MapMaster.getKeyForValue_(map, obj);
                if (id == null) {
                    continue;
                }
                builder.append(id);
            }
            builder.append(";");

        }
        return XML_Converter.wrap(
                borders
                        ? FloorLoader.BORDERS
                        : FloorLoader.OBJ_NODE_NEW
                , builder.toString());
    }

    //TODO DC_TYPE ?
    private String buildIdMap(Module standalone) {
        StringBuilder builder = new StringBuilder();
        Map<Integer, BattleFieldObject> map = LevelEditor.getGame().getSimIdManager().getObjMap();

        Map<String, List<Integer>> nestedMap = new LinkedHashMap<>();
        for (Integer integer : map.keySet()) {
            try {
                BattleFieldObject object = map.get(integer);
                if (standalone != null) {
                    if (!standalone.getCoordinatesSet().contains(object.getCoordinates())) {
                        continue;
                    }
                }
                Integer id = LevelEditor.getGame().getSimIdManager().getId(object);
                String type = object.getType().getName();
                MapMaster.addToListMap(nestedMap, type, id);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

        for (String type : nestedMap.keySet()) {
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
    public String toXml(Module module, boolean standalone) {
        XmlStringBuilder xmlBuilder = new XmlStringBuilder();


        xmlBuilder.append("\n").open(module.getName());
        xmlBuilder.appendNode(new ModuleData(module).toString(),
                FloorLoader.DATA);

        if (standalone) {
            //TODO
            xmlBuilder.append("\n").append(buildIdMap(module));
        }
        String meta = getMetaXml(module);
        xmlBuilder.append("\n").append(meta);

        xmlBuilder.append("\n").open("Zones");
        for (LevelZone zone : module.getZones()) {
            xmlBuilder.appendNode(toXml(zone), "Zone");
        }
        xmlBuilder.close("Zones").append("\n");

        xmlBuilder.append("\n").append(buildCoordinateMap(module));
        xmlBuilder.append("\n").append(buildBorderMap(module));

        xmlBuilder.append("\n").open(FloorLoader.COORDINATES_VOID);
        for (Coordinates coordinates : module.initCoordinateSet(false)) {
            DC_Cell cell = DC_Game.game.getCellByCoordinate(coordinates);
            if (cell != null) //TODO buffer!
                if (cell.isVOID()) {
                    xmlBuilder.append(cell.getCoordinates().toString()).append(";");
                }
        }
        xmlBuilder.close(FloorLoader.COORDINATES_VOID).append("\n");

        xmlBuilder.close(module.getName()).append("\n");

        return xmlBuilder.toString();
    }


    private String toXml(LevelZone zone) {
        XmlStringBuilder xmlBuilder = new XmlStringBuilder();
        xmlBuilder.appendNode((zone).getData().toString(),
                FloorLoader.DATA);
        xmlBuilder.open("Blocks");
        for (LevelBlock block : zone.getSubParts()) {
            xmlBuilder.appendNode(toXml(block), "Block");
        }
        xmlBuilder.close("Blocks");

        return xmlBuilder.toString();
    }

    private String toXml(LevelBlock block) {

        XmlStringBuilder xmlBuilder = new XmlStringBuilder();
        xmlBuilder.appendNode(block.getData().toString(),
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
