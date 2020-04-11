package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.battlefield.vision.GammaMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.system.text.NameMaster;
import main.content.DC_TYPE;
import main.content.values.parameters.G_PARAMS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XmlNodeMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DirectionMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.GuiManager;
import main.system.launch.TypeBuilder;
import main.system.math.PositionMaster;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 */
public class DungeonBuilder<E extends DungeonWrapper> extends DungeonHandler<E> {
    public static final String DUNGEON_TYPE_NODE = "Dungeon_Type";
    public static final String CUSTOM_PARAMS_NODE = "Custom Params";
    public static final String CUSTOM_PROPS_NODE = "Custom Props";
    public static final String DIRECTION_MAP_NODE = "Direction Map";
    public static final int BASE_WIDTH = 15;
    public static final int BASE_HEIGHT = 11;
    protected static final String FLIP_MAP_NODE = "Flipping";

    public DungeonBuilder(DungeonMaster master) {
        super(master);
    }

    public int getDefaultHeight() {
        return BASE_HEIGHT;
    }

    public int getDefaultWidth() {
        return BASE_WIDTH;
    }

    public E buildDungeon(String path) {
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
        List<Node> nodeList = XmlNodeMaster.getNodeList(levelNode);
        Node planNode = XmlNodeMaster.getChildByName(levelNode, "Plan");
        nodeList.addAll(XmlNodeMaster.getNodeList(planNode));
        E dungeonWrapper = buildDungeon(path, data, nodeList);

        dungeonWrapper.setLevelFilePath(path.replace(PathFinder.getDungeonLevelFolder(), ""));

        initWidthAndHeight(dungeonWrapper);
        return getDungeon();
    }

    protected void initWidthAndHeight(E dungeonWrapper) {
        GuiManager.setBattleFieldCellsX(dungeonWrapper.getDungeon().getCellsX());
        GuiManager.setBattleFieldCellsY(dungeonWrapper.getDungeon().getCellsY());
        GuiManager.setCurrentLevelCellsX(dungeonWrapper.getWidth());
        GuiManager.setCurrentLevelCellsY(dungeonWrapper.getHeight());
        //TODO clean up this shit!

        PositionMaster.initDistancesCache();
        DirectionMaster.initCache(dungeonWrapper.getDungeon().getCellsX(),
         dungeonWrapper.getDungeon().getCellsY());
        Coordinates.resetCaches();
        GammaMaster.resetCaches();
    }

    public E buildDungeon(String s, String path, List<Node> nodeList) {
        Node typeNode = XmlNodeMaster.getNodeByName(nodeList, DUNGEON_TYPE_NODE);
        ObjType type = null;
        if (typeNode == null) {
            type = DataManager.getRandomType(DC_TYPE.DUNGEONS, null);
        } else if (StringMaster.compareByChar(typeNode.getNodeName(), (DUNGEON_TYPE_NODE))) {
            String name = typeNode.getTextContent();
            if (name.contains(NameMaster.VERSION)) {
                name = name.split(NameMaster.VERSION)[0];
            }
            type = DataManager.getType(name, DC_TYPE.DUNGEONS);
        } else {
            type = TypeBuilder.buildType(typeNode, type); // custom base type
        }
        E dungeon = getInitializer().createDungeon(type);
        // getDungeon().setName(name)


        return dungeon;

    }

    protected void initDynamicObjData(Location location) {
        int z = location.getIntParam(G_PARAMS.Z_LEVEL);
        if (location.getDirectionMap() != null) {
            try {
                DC_ObjInitializer.initDirectionMap(z, location.getDirectionMap());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (location.getFlipMap() != null) {
            try {
                DC_ObjInitializer.initFlipMap(z, location.getFlipMap());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }


    public void initLevel() {
    }
}
