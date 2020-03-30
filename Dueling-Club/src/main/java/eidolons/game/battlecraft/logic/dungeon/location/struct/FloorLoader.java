package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Block;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.data.DataUnit;
import main.system.launch.TypeBuilder;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static eidolons.game.battlecraft.logic.dungeon.universal.DungeonBuilder.CUSTOM_PARAMS_NODE;
import static eidolons.game.battlecraft.logic.dungeon.universal.DungeonBuilder.CUSTOM_PROPS_NODE;

public class FloorLoader extends DungeonHandler<Location> {
    //nodes
    public static final String TRANSITS = "TRANSITS";
    public static final String OVERLAY_DIRECTIONS = "OVERLAY_DIRECTIONS";
    public static final String ENCOUNTER_AI_GROUPS = "ENCOUNTER_AI_GROUPS";
    public static final String CUSTOM_AI_GROUPS = "CUSTOM_AI_GROUPS";
    public static final String ID_MAP = "ID_MAP";
    public static final String MODULES = "MODULES";
    //subnodes
    public static final String TRANSIT_IDS = "TRANSIT_IDS";
    public static final String TRANSIT_ONE_END = "TRANSIT_ONE_END";
    public static final String DATA = "DATA";
    public static final String FACING = "FACING";
    public static final String LAYERS = "LAYERS";
    public static final String MISSING = "Missing";

    public FloorLoader(DungeonMaster<Location> master) {
        super(master);
    }

    public void processSubnode(Node node, LevelBlock block) {
        BlockData data = new BlockData(new LE_Block(block));
        data.setData(node.getTextContent());
        data.apply();

    }

    public void processNode(Node node, Location location) {

        switch (node.getNodeName().toUpperCase()) {
            case MODULES:
               new StructureBuilder(master).build(node, location);
                break;
            case TRANSIT_IDS:

                break;
            case CUSTOM_PARAMS_NODE:
                TypeBuilder.setParams(location.getDungeon(), node);
                break;
            case CUSTOM_PROPS_NODE:
                TypeBuilder.setProps(location.getDungeon(), node);
                break;
//                case FLIP_MAP_NODE
            case OVERLAY_DIRECTIONS:
                break;
            case ENCOUNTER_AI_GROUPS:
                break;
            case CUSTOM_AI_GROUPS:
                break;
            case ID_MAP:
                getMaster().setIdTypeMap(processIdTypesMap(node.getTextContent()));
            case LAYERS:
                processLayers(node);
                break;
            case FACING:
                location.setUnitFacingMap(createUnitFacingMap(node.getTextContent()));
                break;
        }

    }

    protected void processLayers(Node node) {
        game.getMetaMaster().getDungeonMaster().getLayerManager().initLayers(node);
    }

    private static Map<Integer, ObjType> processIdTypesMap(String textContent) {
        /*
        id=objType(x, y);
        OR just map ids to types, then proceed with the obj map as usual
         */
        Map<Integer, ObjType> idMap = new LinkedHashMap<>();
        textContent = textContent.trim();
        for (String content : ContainerUtils.openContainer(textContent)) {
            Integer id = Integer.valueOf(content.split("=")[0]);
            String typeName = content.split("=")[1];
            ObjType type = DataManager.getType(typeName, DC_TYPE.BF_OBJ);
            if (type == null) {
                type = DataManager.getType(typeName, DC_TYPE.UNITS);
            }
            idMap.put(id, type);
        }
        return idMap;
    }

    private Map<Coordinates, FACING_DIRECTION> createUnitFacingMap(String textContent) {
        Map<Coordinates, FACING_DIRECTION> map = new HashMap<>();

        DataUnit<FACING_DIRECTION> data = new DataUnit<>(textContent);
        for (String s : data.getValues().keySet()) {
            map.put(new Coordinates(s), FacingMaster.getFacing(data.getValues().get(s)));
        }
        return map;
    }
}
