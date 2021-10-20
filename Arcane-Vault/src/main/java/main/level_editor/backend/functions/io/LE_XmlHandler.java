package main.level_editor.backend.functions.io;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.struct.Entrance;
import eidolons.game.module.dungeoncrawl.struct.LevelBlock;
import eidolons.game.module.dungeoncrawl.struct.LevelZone;
import eidolons.game.netherflame.dungeons.model.assembly.Transform;
import main.data.xml.XML_Converter;
import main.data.xml.XmlStringBuilder;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LOG_CHANNEL;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static main.system.auxiliary.log.LogMaster.log;

public class LE_XmlHandler extends LE_Handler {

    public LE_XmlHandler(LE_Manager manager) {
        super(manager);
    }


    public String toXml(Location floor) {
        return toXml(floor, null);
    }

    public String toXml(Location floor, Module standalone) {
        return toXml(floor, standalone, null);
    }

    public String toXml(Location floor, Module standalone, Transform transform) {
        XmlStringBuilder xmlBuilder = new XmlStringBuilder();

        //from old - dungeon params props etc

        xmlBuilder.appendNode((floor).getData().toString(),
                FloorLoader.DATA);
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

    private String getPreObjXml(Module module) {
        Function<Integer, Boolean> idFilter = getIdFilter(module);
        Function<Coordinates, Boolean> coordinateFilter = getCoordinateFilter(module);
        return getHandlersXml(null, handler -> handler.getPreObjXml(idFilter, coordinateFilter));
    }

    public String getMetaXml(Module module) {
        Function<Integer, Boolean> idFilter = getIdFilter(module);
        Function<Coordinates, Boolean> coordinateFilter = getCoordinateFilter(module);
        return getHandlersXml(null, handler -> handler.getXml(idFilter, coordinateFilter));
    }

    public String getDataMapsXml(Module module) {
        Function<Integer, Boolean> idFilter = getIdFilter(module);
        Function<Coordinates, Boolean> coordinateFilter = getCoordinateFilter(module);
        return getHandlersXml(FloorLoader.DATA_MAPS, handler -> handler.getDataMapString(idFilter, coordinateFilter));
    }

    public String getHandlersXml(String nodeName,
                                 Function<LE_Handler, String> xmlSupplier) {
        XmlStringBuilder xmlBuilder = new XmlStringBuilder();
        if (nodeName == null) {
            xmlBuilder.append("\n");
        } else
            xmlBuilder.append("\n").open(nodeName);
        for (LE_Handler handler : LevelEditor.getManager().getHandlers()) {
            String xml = xmlSupplier.apply(handler);
            if (xml.isEmpty()) {
                continue;
            }
            xmlBuilder.append(xml).append("\n");
        }
        if (nodeName == null) {
            xmlBuilder.append("\n");
        } else
            xmlBuilder.close(nodeName).append("\n");

        return xmlBuilder.toString();
    }

    private Function<Coordinates, Boolean> getCoordinateFilter(Module module) {
        return c -> module.getCoordinatesSet().contains(c);
    }

    private Function<Integer, Boolean> getIdFilter(Module standalone) {
        return id ->
        {
            BattleFieldObject object = LevelEditor.getManager().getIdManager().getObjectById(id);
            if (object == null) {
                return false;
            }
            return standalone == null || standalone.getCoordinatesSet().
                    contains(object.getCoordinates());
        };
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
        int i = 0;
        for (Coordinates c : module.initCoordinateSet(true)) {
            Set<BattleFieldObject> set = LevelEditor.getGame().getObjectsOnCoordinateAll(c);
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
                i++;
                continue; //just the coordinate
            }
            builder.append("=");
            for (BattleFieldObject obj : set) {
                Integer id = (Integer) MapMaster.getKeyForValue_(map, obj);
                if (id == null) {
                    continue;
                }
                i++;
                builder.append(id).append(",");
            }
            builder.append(";");

        }
        log(LOG_CHANNEL.SAVE, module.getName() + " has " + i +
                (borders ? "border objects"
                        : "objects"));
        return XML_Converter.wrap(
                borders
                        ? FloorLoader.BORDERS
                        : FloorLoader.OBJ_NODE_NEW
                , builder.toString());
    }

    //TODO DC_TYPE ?
    private String buildIdMap(Module module) {
        StringBuilder builder = new StringBuilder();
        Map<Integer, BattleFieldObject> map = LevelEditor.getGame().getSimIdManager().getObjMap();

        Map<String, List<Integer>> nestedMap = new LinkedHashMap<>();
        for (Integer integer : map.keySet()) {
            try {
                BattleFieldObject object = map.get(integer);
                if (module != null) {
                    if (!module.getCoordinatesSet().contains(object.getCoordinates())) {
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

        xmlBuilder.append("\n").append(getPreObjXml(module));
        xmlBuilder.append("\n").append(buildIdMap(module));
        xmlBuilder.append("\n").append(buildCoordinateMap(module));

        String s = getDataMapsXml(module);
        xmlBuilder.append("\n").append(s);
        s = getMetaXml(module);
        xmlBuilder.append("\n").append(s);

        xmlBuilder.append("\n").open("Zones");
        for (LevelZone zone : module.getZones()) {
            if ((zone).getData() == null) {
               continue;
            }
            xmlBuilder.appendNode(toXml(zone), "Zone");
        }
        xmlBuilder.close("Zones").append("\n");

        xmlBuilder.append("\n").append(buildBorderMap(module));

        // ScreenMaster.getDungeonGrid().getPlatformHandler()


        xmlBuilder.appendNode(getPlatformHandler().getPlatformData(module) ,  FloorLoader.PLATFORM_DATA);

        xmlBuilder.append("\n").open(FloorLoader.COORDINATES_VOID);
        for (Coordinates coordinates : module.initCoordinateSet(false)) {
            DC_Cell cell = DC_Game.game.getCell(coordinates);
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
            try {
                xmlBuilder.appendNode(toXml(block), "Block");
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }   }
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

        xmlBuilder.open(FloorLoader.MISSING);
        for (Coordinates coordinate : missing) {
            xmlBuilder.append(coordinate + ";");
        }
        xmlBuilder.close(FloorLoader.MISSING);

        return xmlBuilder.toString();
    }

    public String wrapSingleModule(Location floorWrapper, Module module, String contents) {
        return null;
    }
}
