package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.data.xml.XML_Formatter;
import main.data.xml.XmlNodeMaster;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.util.Refactor;
import org.w3c.dom.Node;

import java.util.*;

import static main.system.auxiliary.log.LogMaster.log;

public class StructureBuilder extends DungeonHandler<Location> {

    private int ZONE_ID = 0;
    private int BLOCK_ID = 0;

    public StructureBuilder(DungeonMaster master) {
        super(master);
    }

    public void build(Node node, Location location) {
        Set<Module> modules = new LinkedHashSet<>();
        log(LOG_CHANNEL.BUILDING, "Main floor node");
        for (Node sub : XmlNodeMaster.getNodeList(node)) {
            modules.add(createModule(sub, location));
        }

        log(LOG_CHANNEL.BUILDING, "Location has modules: " +
                modules.size());
        getMetaMaster().getModuleMaster().setModules(modules);
    }

    private Module createModule(Node node, Location location) {
        Module module = new Module();
//    TODO explicit order!    XmlNodeMaster.findNode()
        for (Node sub : XmlNodeMaster.getNodeList(node)) {
            if (sub.getNodeName().equalsIgnoreCase(FloorLoader.DATA)) {
                ModuleData data = new ModuleData(module);
                data.setData(sub.getTextContent());
                log(LOG_CHANNEL.BUILDING, "Module data: " + data);
                data.apply();
                log(LOG_CHANNEL.BUILDING, "Module after data applies: " +
                        module);

            } else if (sub.getNodeName().equalsIgnoreCase(FloorLoader.ZONES)) {
                List<LevelZone> zones = createZones(module, sub, location);
                module.setZones(zones);
                log(LOG_CHANNEL.BUILDING, "Module has zones: " +
                        zones.size());
            } else {
                getFloorLoader().processModuleSubNode(sub, location, module);
            }
        }
        return module;
    }

    private List<LevelZone> createZones(Module module, Node sub, Location location) {
        List<LevelZone> zones = new LinkedList<>();
        for (Node node : XmlNodeMaster.getNodeList(sub)) {
            zones.add(buildZone(module, location.getDungeon(), ZONE_ID++, node));
        }
        return zones;
    }


    private LevelZone buildZone(Module module, Dungeon dungeon, Integer id, Node zoneNode) {
        LevelZone zone = new LevelZone(id);
        String dataString = "";
        for (Node node : XmlNodeMaster.getNodeList(zoneNode)) {
            if (node.getNodeName().equalsIgnoreCase(FloorLoader.DATA)) {
                dataString = node.getTextContent();
                log(LOG_CHANNEL.BUILDING, "Zone data read: " + dataString);
            } else {
                for (Node subNode : XmlNodeMaster.getNodeList(node)) {
                    LevelBlock block = constructBlock(subNode, BLOCK_ID++, zone, dungeon);
                    zone.addBlock(block);
                }
                log(LOG_CHANNEL.BUILDING, "Module has zones: " +
                        zone.getSubParts().size());
            }
        }

        ZoneData data = new ZoneData((zone));
        data.setData(dataString);
        zone.setData(data);
        zone.setName(XML_Formatter.restoreXmlNodeName(zoneNode.getNodeName()));
        zone.setModule(module);
        log(LOG_CHANNEL.BUILDING, "Zone data: " + data);
        data.apply();
        log(LOG_CHANNEL.BUILDING, "Zone after data applies: " +
                module);
        return zone;
    }

    @Refactor
    //TODO the way it's done, we can't have Structures in non-Location dungeons!!!
    public LevelBlock constructBlock(Node node, int id, LevelZone zone,
                                     Dungeon dungeon) {
        List<Coordinates> coordinates = new ArrayList<>();
        Map<Coordinates, ? extends Obj> objectMap = new LinkedHashMap<>();
        LevelBlock b = new LevelBlock(zone);
        Coordinates c = null;
        // TODO b-data, coordinates, objects
        for (Node subNode : XmlNodeMaster.getNodeList(node)) {
            if (StringMaster.compareByChar(subNode.getNodeName(), FloorLoader.DATA)) {
                new BlockData((b)).setData(subNode.getTextContent()).apply();
                c = b.getOrigin();
            } else if (StringMaster.compareByChar(subNode.getNodeName(), FloorLoader.MISSING)) {

                coordinates = CoordinatesMaster.getCoordinatesBetween(b.getOrigin(),
                        Coordinates.get(c.x + b.getWidth(), c.y + b.getHeight()));
                List<Coordinates> missing = CoordinatesMaster.getCoordinatesFromString(subNode.getTextContent());
                coordinates.removeAll(missing);

            }
        }

        b.setCoordinates(coordinates);
        if (objectMap == null) {
            return b;
        }

        return b;
    }


//    public void initModuleZoneLazily(Module module) {
//        int id = 0;
//        for (Node lazyInitZone : lazyInitZones) {
//            if (lazyInitZone.getNodeName().equalsIgnoreCase(module.getName())) {
//                buildZone(getDungeon().getDungeon(), id++, lazyInitZone);
//                return;
//            }
//
//        }
//    }
//
//    private void initZones(Node zonesNode, Dungeon dungeon) {
//        int id = 0;
//        int zoneId = 0;
//        List<LevelZone> zones = new ArrayList<>();
//        //        Node zonesNode = XML_Converter.getChildAt(planNode, (1));
//        for (Node zoneNode : XmlNodeMaster.getNodeList(zonesNode)) {
//
//
//            if (isZoneModulesLazy())
//                if (!checkZoneModule(zoneNode)) {
//                    lazyInitZones.add(zoneNode);
//                    continue;
//                }
//            LevelZone zone = buildZone(dungeon, zoneId, zoneNode);
//            zoneId++;
//            zones.add(zone);
//        }
//    }

}
