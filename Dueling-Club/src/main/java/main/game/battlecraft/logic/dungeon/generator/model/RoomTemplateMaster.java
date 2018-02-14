package main.game.battlecraft.logic.dungeon.generator.model;

import main.data.filesys.PathFinder;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TEMPLATE;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.battlecraft.logic.dungeon.generator.GeneratorEnums;
import main.game.battlecraft.logic.dungeon.generator.GeneratorEnums.EXIT_TEMPLATE;
import main.game.battlecraft.logic.dungeon.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import main.game.battlecraft.logic.dungeon.generator.LevelData;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/13/2018.
 * <p>
 * uses custom data format!
 * <p>
 * convert from existing levels?
 * <p>
 * how to use these?
 * <p>
 * perhaps the whole map will be modeled via this logic before real filling?
 */
public class RoomTemplateMaster {

    private static final String MODEL_SPLITTER = "=";
    private final LevelData levelData;
    private ROOM_TEMPLATE_GROUP group;
    private Map<ROOM_TEMPLATE, List<RoomModel>> templateMap;

    public RoomTemplateMaster(LevelData data, LevelModel model) {
        levelData = data;
//        group = levelData.getTemplateGroup();
        generateTemplateMap();
    }

    public void generateTemplateMap() {
        templateMap = new HashMap<ROOM_TEMPLATE, List<RoomModel>>();
        for (ROOM_TEMPLATE sub : ROOM_TEMPLATE.values()) {
            List<RoomModel> roomModels = loadModels(group, sub);
            templateMap.put(sub, roomModels);
        }
    }


    protected List<RoomModel> loadModels(ROOM_TEMPLATE_GROUP group,
                                         ROOM_TYPE type,
                                         EXIT_TEMPLATE exitTemplate) {
        //room namespace
        String path =
         PathFinder.getXML_PATH() + "Level Editor//" +
          "room templates//" + group + "//" + type+ "//" + exitTemplate;
        List<RoomModel> list = new ArrayList<>();
        for (File sub : FileManager.getFilesFromDirectory(path, false)) {
            String text = FileManager.readFile(sub);
            //hor/vert ?
            for (String string : text.split(MODEL_SPLITTER))
                list.add(createRoomModel(string, exitTemplate, type));
        }
        return list;
    }

    private RoomModel createMirrorRoomModel(RoomModel roomModel ) {

        return roomModel;
    }
    private RoomModel createRoomModel(String data,
                                      EXIT_TEMPLATE exit,
                                      ROOM_TYPE template) {
        String[] array = data.split(StringMaster.NEW_LINE);
        String[][] cells = new String[array[0].length()+1][array.length+1];

        int i = 0;
        for (String row : array) {
            cells[i] = row.split(""); //TODO won't work?
            i++;
        }
        ArrayMaster.rotateMatrix_(cells);
        wrapInWalls(cells);
        RoomModel model = new RoomModel(cells, template, exit);
        return model;
    }


    public void getTemplates(EXIT_TEMPLATE template, ROOM_TYPE roomType) {
        list = templateMap.get(roomType);
    }

    private RoomModel chooseTemplate(ROOM_TYPE roomType, FACING_DIRECTION... exits) {
        List<RoomModel> models = templateMap.get(roomType);

        return null;
    }

    public String generate(int x, int y, float irregularity) {
        String result = "";
        int col = 0;
        int row = 0;
        while (row < y) {
            while (col < x) {
                result += GeneratorEnums.ROOM_CELL.WALL.getSymbol();
                col++;
            }
            result += StringMaster.NEW_LINE;
            row++;
        }


        return result;
    }
    //TODO multiple objects on cell?

}
