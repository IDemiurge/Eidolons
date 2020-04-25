package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.battlefield.vision.GammaMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.system.text.NameMaster;
import main.content.DC_TYPE;
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
public class DungeonBuilder  extends DungeonHandler  {
    public static final String DUNGEON_TYPE_NODE = "Dungeon_Type";

    public DungeonBuilder(DungeonMaster master) {
        super(master);
    }

    public Location buildDungeon(String path) {
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

        Node planNode =
                levelNode.getNodeName().equalsIgnoreCase("Floor")
                        ? levelNode
                        : XmlNodeMaster.getChildByName(levelNode, "Floor");
        List<Node> nodeList = XmlNodeMaster.getNodeList(planNode);
        Location dungeonWrapper = buildDungeon(path, data, nodeList);
        master.setDungeonWrapper(dungeonWrapper);
        initLevel(nodeList);
        dungeonWrapper.setLevelFilePath(path.replace(PathFinder.getDungeonLevelFolder(), ""));

//        initWidthAndHeight(dungeonWrapper);
        return getDungeon();
    }

    public void initModuleSize(Module module) {
        initWidthAndHeight(module.getEffectiveWidth(true),
                module.getEffectiveHeight(true));
    }

    public void initLocationSize(Location dungeonWrapper) {
        int w = dungeonWrapper.getWidth();
        int h = dungeonWrapper.getHeight();
        initWidthAndHeight(w, h);
        Coordinates.initCache(w, h);
    }

    protected void initWidthAndHeight(int w, int h) {
        GuiManager.setBattleFieldCellsX(w);
        GuiManager.setBattleFieldCellsY(h);
        GuiManager.setCurrentLevelCellsX(w);
        GuiManager.setCurrentLevelCellsY(h);
        //TODO clean up this shit!

        PositionMaster.initDistancesCache(w, h);
        DirectionMaster.initCache(w, h);
        GammaMaster.resetCaches(w, h);
    }

    public Location buildDungeon(String s, String path, List<Node> nodeList) {
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
        Location dungeon = getInitializer().createDungeon(type);
        // getDungeon().setName(name)


        return dungeon;

    }


    public void initLevel(List<Node> nodeList) {
    }
}
