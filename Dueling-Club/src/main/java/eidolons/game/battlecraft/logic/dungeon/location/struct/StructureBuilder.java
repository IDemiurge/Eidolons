package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Block;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Module;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.data.xml.XmlNodeMaster;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.util.Refactor;
import org.w3c.dom.Node;

import java.util.*;

public class StructureBuilder extends DungeonHandler<Location> {


    private static final String OBJ_NODE_NEW = null;
    private int ZONE_ID = 0;
    private int BLOCK_ID = 0;

    public StructureBuilder(DungeonMaster<Location> master) {
        super(master);
    }

    public void build(Node node, Location location) {
        for (Node sub : XmlNodeMaster.getNodeList(node)) {
            createModule(sub, location);
        }
    }

    private void createModule(Node node, Location location) {
        Set<Module> modules = new LinkedHashSet<>();
        for (Node sub : XmlNodeMaster.getNodeList(node)) {
            Module m = new Module();
            if (node.getNodeName().equalsIgnoreCase(FloorLoader.DATA)) {
                ModuleData data = new ModuleData(new LE_Module(m));
                data.setData(node.getTextContent());
                data.apply();
            } else {
                createZones(sub, location);
            }
            modules.add(m);
        }
        getMetaMaster().getModuleMaster().setModules(modules);
    }

    private Set<LevelZone> createZones(Node sub, Location location) {
        Set<LevelZone> zones = new LinkedHashSet<>();
        for (Node node : XmlNodeMaster.getNodeList(sub)) {
            createZone(location.getDungeon(), ZONE_ID++, node);

        }
        return zones;
    }


    private LevelZone createZone(Dungeon dungeon, Integer id, Node zoneNode) {
        LevelZone zone;
        try {
            zone = createZone(dungeon, id, zoneNode);
        } catch (Exception e) {
            //                main.system.ExceptionMaster.printStackTrace(e);
            zone = new LevelZone(id);
        } // ++ add coord exceptions
        id = 0;
        for (Node node : XmlNodeMaster.getNodeList(XmlNodeMaster.getNodeList(zoneNode).get(0))) {
            // if (node.getNodeName().equalsIgnoreCase(BLOCKS_NODE))
            // blocks = initBlocks(XML_Converter.getStringFromXML(node));

            LevelBlock block = constructBlock(node, BLOCK_ID++, zone, dungeon);
            zone.addBlock(block);
//            plan.getBlocks().add(block);
        }
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
                new BlockData(new LE_Block(b)).setData(subNode.getTextContent()).apply();
            } else if (StringMaster.compareByChar(subNode.getNodeName(), FloorLoader.MISSING)) {

                coordinates = CoordinatesMaster.getCoordinatesBetween(b.getOrigin(),
                        Coordinates.get(c.x + b.getWidth(), c.y + b.getHeight()));
                List<Coordinates> missing = CoordinatesMaster.getCoordinatesFromString(subNode.getTextContent());
                coordinates.removeAll(missing);

            } else if (StringMaster.compareByChar(subNode.getNodeName(), OBJ_NODE_NEW)) {
                objectMap = processObjects(master.getIdTypeMap(), dungeon, b, subNode);
            }

        }

        b.setCoordinatesList(coordinates);
        if (objectMap == null) {
            return b;
        }

        return b;
    }

    private Map<Coordinates, ? extends Obj> processObjects(Map<Integer, ObjType> idMap, Dungeon dungeon, LevelBlock b, Node subNode) {
        if (idMap == null) {
            throw new RuntimeException("No ID MAP FOR OBJECTS!");
        }
        Map<Integer, BattleFieldObject> objIdMap = master.getObjIdMap();
        // x-y=id,id,id;...
        //TODO   create player=> ids map and make multiple maps here!
        Map<Coordinates, Obj> fullMap = new HashMap<>();
        for (String substring : ContainerUtils.openContainer(
                subNode.getTextContent())) {
            String objectsString = "";
            Coordinates c = Coordinates.get(true, substring.split("=")[0]);
            List<String> ids = ContainerUtils.openContainer(substring.split("=")[1], ",");

            for (String id : ids) {
                try {
                    objectsString += c + DC_ObjInitializer.COORDINATES_OBJ_SEPARATOR
                            + idMap.get(Integer.valueOf(id)).getName()
                            + DC_ObjInitializer.OBJ_SEPARATOR;
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            Map<Coordinates, ? extends Obj> subMap = DC_ObjInitializer.initMapBlockObjects(dungeon, b, objectsString);
            int i = 0;
            for (Obj value : subMap.values()) {
                if (value instanceof BattleFieldObject) {
                    Integer id = Integer.valueOf(ids.get(i++));
                    objIdMap.put(id, (BattleFieldObject) value);
                }
            }
            fullMap.putAll(subMap);
        }

        return fullMap;
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

//    private LevelZone createZone(Dungeon dungeon, Integer zoneId, Node zoneNode) {
//        String name = zoneNode.getNodeName();
//        String nodeName = XML_Formatter.restoreXmlNodeName(name);
//        if (!nodeName.contains(",")) {
//            nodeName = XML_Formatter.restoreXmlNodeNameOld(name);
//        }
//        int[] c = CoordinatesMaster.getMinMaxCoordinates(nodeName.split(",")[1]);
//        LevelZone zone = new LevelZone(zoneId);
//        return zone;
//    }
}
