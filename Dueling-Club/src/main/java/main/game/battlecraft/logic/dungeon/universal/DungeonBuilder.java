package main.game.battlecraft.logic.dungeon.universal;

import main.content.CONTENT_CONSTS.FLIP;
import main.content.DC_TYPE;
import main.content.values.parameters.G_PARAMS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import main.game.battlecraft.logic.dungeon.location.Location;
import main.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import main.game.battlecraft.logic.dungeon.location.building.MapBlock;
import main.game.battlecraft.logic.dungeon.location.building.MapZone;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.TypeBuilder;
import main.system.text.NameMaster;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 */
public class DungeonBuilder<E extends DungeonWrapper> extends DungeonHandler<E> {
    public static final String DUNGEON_TYPE_NODE = "Dungeon_Type";
    public static final String CUSTOM_PARAMS_NODE = "Custom Params";
    public static final String CUSTOM_PROPS_NODE = "Custom Props";
    public static final String DIRECTION_MAP_NODE = "Direction Map";
    public static final String WALL_OBJ_DATA_NODE = "Wall Objects";
    protected static final String FLIP_MAP_NODE = "Flipping";

    public static final int BASE_WIDTH = 21;
    public static final int BASE_HEIGHT = 15;


    public int getDefaultHeight() {
        return BASE_HEIGHT;
    }
    public int getDefaultWidth() {
        return BASE_WIDTH;
    }
    public DungeonBuilder(DungeonMaster master) {
        super(master);
    }


public E buildDungeon(String path ) {
        String data = FileManager.readFile(path);

        if (data.isEmpty()) {
            data = FileManager.readFile(
             path.contains(PathFinder.getDungeonLevelFolder()) ? path
              : PathFinder.getDungeonLevelFolder() + path);
        }
        if (data.isEmpty()) {
            data = path;
        }
        Document levelDocument = XML_Converter.getDoc(data, true);
        Node levelNode = XML_Converter.getChildAt(levelDocument, 0);
        List<Node> nodeList = XML_Converter.getNodeList(levelNode);
        Node planNode = XML_Converter.getChildByName(levelNode, "Plan");
         nodeList .addAll(XML_Converter.getNodeList(planNode));
        return buildDungeon(data, nodeList);
    }
        public E buildDungeon(String path, List<Node> nodeList) {
        Node typeNode =XML_Converter.getNodeByName( nodeList, DUNGEON_TYPE_NODE);
        ObjType type = null;

        if (StringMaster.compareByChar(typeNode.getNodeName(), (DUNGEON_TYPE_NODE))) {
            String name = typeNode.getTextContent();
            if (name.contains(NameMaster.VERSION)) {
                name = name.split(NameMaster.VERSION)[0];
            }
            type = DataManager.getType(name, DC_TYPE.DUNGEONS);
        } else {
            type = TypeBuilder.buildType(typeNode, type); // custom base type
        }
        E dungeon = getInitializer().createDungeon(type);
        dungeon.setLevelFilePath(path.replace(PathFinder.getDungeonLevelFolder(), ""));
        // getDungeon().setName(name)
        DUNGEON_TEMPLATES template = null;
        DungeonPlan plan = null;
        if (getDungeon() instanceof Location) {
            plan = new DungeonPlan(template, ((Location) getDungeon()));
            plan.setLoaded(true);

        for (Node n : XML_Converter.getNodeList(levelNode)) {
processNode(n, dungeon, plan);

        }
            plan.setMap(getMapGenerator().generateMap((Location) getDungeon()));
            plan.setStringData(data);
            if (!CoreEngine.isLevelEditor()) {
                initDynamicObjData(plan);
            }
        }


        return dungeon;

    }

    protected void processNode(Node n, E dungeon, DungeonPlan plan) {
        if (StringMaster.compareByChar(n.getNodeName(), (WALL_OBJ_DATA_NODE))) {

            String wallObjData = n.getTextContent();

            if (!StringMaster.isEmpty(wallObjData)) {
                plan.setWallObjects(DC_ObjInitializer.createUnits(DC_Player.NEUTRAL,

                        wallObjData));
            }

        }

        if (StringMaster.compareByChar(n.getNodeName(), (FLIP_MAP_NODE))) {
            plan.setFlipMap(new RandomWizard<FLIP>().constructStringWeightMapInversed(n

                    .getTextContent(), FLIP.class));

        } else if (StringMaster.compareByChar(n.getNodeName(), (DIRECTION_MAP_NODE))) {
            plan.setDirectionMap(new RandomWizard<DIRECTION>()
             .constructStringWeightMapInversed(n.getTextContent(), DIRECTION.class));

        } else if (StringMaster.compareByChar(n.getNodeName(), (CUSTOM_PARAMS_NODE))) {
            TypeBuilder.setParams(dungeon.getDungeon(), n);
//                getDungeon().getGame().getDungeonMaster().setDungeon(getDungeon());
            //wtf?

            // TypeBuilder.setParams(type, n); // toBase()? TODO new type?
        } else if (StringMaster.compareByChar(n.getNodeName(), (CUSTOM_PROPS_NODE))) {
            TypeBuilder.setProps(dungeon.getDungeon(), n);
            // TypeBuilder.setProps(type, n);
        }
    }

    protected void initDynamicObjData(Location location, DungeonPlan plan) {
        List<BattleFieldObject> fullObjectList = new LinkedList<>();
        int z = location.getIntParam(G_PARAMS.Z_LEVEL);
        for (MapBlock b : plan.getBlocks()) {
            LinkedList<Obj> objects = new LinkedList<>(b.getObjects());
            for (Obj obj : objects) {
                fullObjectList.add((BattleFieldObject) obj);
                // TODO of course - the issue was that I added an object to
                // block too! ... init?
                BattleFieldObject unit = (BattleFieldObject) obj;

                if (z != 0) {
                    unit.setZ(z);
                }

            }
        }
        for (MapZone zone : plan.getZones()) {
            ObjType type1 = DataManager.getType(zone.getFillerType(), DC_TYPE.BF_OBJ);
            List<Coordinates> list = zone.getCoordinates();
            for (MapBlock b : zone.getBlocks()) {
                list.removeAll(b.getCoordinates());
            }

        }

        for (Obj obj : plan.getWallObjects()) {
            Unit unit = (Unit) obj;
            fullObjectList.add(unit);

            if (z != 0) {
                unit.setZ(z);
            }
        }
        if (plan.getDirectionMap() != null) {
            try {
                DC_ObjInitializer.initDirectionMap(z, plan.getDirectionMap());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (plan.getFlipMap() != null) {
            try {
                DC_ObjInitializer.initFlipMap(z, plan.getFlipMap());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
