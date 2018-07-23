package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

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
    private static final FACING_DIRECTION DEFAULT_ENTRANCE_SIDE = FACING_DIRECTION.EAST;
    private final LevelData levelData;
    private final ROOM_TEMPLATE_GROUP[] groups;
    Stack<List<RoomModel>> roomPoolStack = new Stack<>();
    private Map<ROOM_TEMPLATE_GROUP, Set<RoomModel>> models = new LinkedHashMap<>();
    private Map<ROOM_TEMPLATE_GROUP, Map<ROOM_TYPE, Map<EXIT_TEMPLATE, List<RoomModel>>>> templateMap;

    //    private List<RoomModel> lastPool;
    //    private ArrayList randomPool;

    public RoomTemplateMaster(LevelData data, LevelModel model) {
        levelData = data;
        groups = levelData.getTemplateGroups();
        templateMap = new XLinkedMap<>();
        for (ROOM_TEMPLATE_GROUP group : groups) {
            templateMap.put(group, new HashMap<>());
            models.put(group, new HashSet<>());
            generateTemplateMap(group);
        }
    }

    private void generateTemplateMap(ROOM_TEMPLATE_GROUP group) {
        for (ROOM_TYPE sub : ROOM_TYPE.values()) {
            Map<EXIT_TEMPLATE, List<RoomModel>> map = new HashMap<>();
            List<RoomModel> common = sub == ROOM_TYPE.CORRIDOR
             ? new ArrayList<>()
             : loadModels(group, sub, null);
            for (EXIT_TEMPLATE exitTemplate : EXIT_TEMPLATE.values()) {
                List<RoomModel> roomModels = loadModels(group, sub, exitTemplate);
                roomModels.addAll(common);
                map.put(exitTemplate, roomModels);
                models.get(group).addAll(roomModels);
            }
            templateMap.get(group).put(sub, map);
        }
    }


    protected List<RoomModel> loadModels(ROOM_TEMPLATE_GROUP group,
                                         ROOM_TYPE type,
                                         EXIT_TEMPLATE exitTemplate) {
        //room namespace
        String path =
         PathFinder.getXML_PATH() + "Level Editor//" +
          "room templates//" + group;
        if (exitTemplate != null)
            path += "//" + exitTemplate;
        //else => common templates
        List<RoomModel> list = new ArrayList<>();
        File sub = new File(path + "//" + getRoomTypePath(type) + ".txt");
        if (!sub.exists())
            return list;
        //        for (File sub : FileManager.getFilesFromDirectory(path, false)) {
        String text = FileManager.readFile(sub).trim();
        //hor/vert ?
        for (String string : text.split(MODEL_SPLITTER))
            list.add(createRoomModel(string, exitTemplate, type));
        //        }
        return list;
    }

    private String getRoomTypePath(ROOM_TYPE type) {
        if (type == ROOM_TYPE.THRONE_ROOM) {
            return "main";
        }
        if (type == ROOM_TYPE.CORRIDOR) {
            return "link";
        }
        return type.toString().split("_")[0];
    }

    private RoomModel createMirrorRoomModel(RoomModel roomModel) {

        return roomModel;
    }

    private RoomModel createRoomModel(String data,
                                      EXIT_TEMPLATE exit,
                                      ROOM_TYPE template) {
        String[] array = StringMaster.splitLines(data);
        int wrapWidth = 0;
        if (levelData.getBooleanValue(LEVEL_VALUES.WRAP_ROOMS))
            wrapWidth = 1;
        String[][] cells = new String[array[0].length() + wrapWidth * 2][array.length + wrapWidth * 2];
        boolean hor = array[0].length() + wrapWidth * 2 > array.length + wrapWidth * 2;
        if (hor)
            cells = new String[array.length + wrapWidth * 2][array[0].length() + wrapWidth * 2];

        int i = 0;
        for (String row : array) {
            if (cells.length <= i)
                continue;
            cells[i] = row.split(""); //TODO won't work?
            i++;
        }
        if (hor) {
            main.system.auxiliary.log.LogMaster.log(1, "TODO hor ");
            cells = ArrayMaster.rotateMatrixClockwise(cells);
        }

        if (levelData.getBooleanValue(LEVEL_VALUES.WRAP_ROOMS))
            RoomModelTransformer.wrapInWalls(cells); //TODO could wrap in empty cells for outdoors!
        RoomModel model = new RoomModel(cells, template, exit);
        return model;
    }


    public void getTemplates(EXIT_TEMPLATE template, ROOM_TYPE roomType) {
        //        list = templateMap.get(roomType);
    }

    //always returns something, but does not control its recursion, careful!
    public RoomModel getNextLargestRandomModel(ROOM_TYPE roomType, EXIT_TEMPLATE template,
                                               FACING_DIRECTION entrance, ROOM_TEMPLATE_GROUP templateGroup) {

        if (roomPoolStack.isEmpty())
            resetSizedRandomRoomPools(templateGroup);
        List<RoomModel> models = new ArrayList<>(roomPoolStack.pop());
        models.removeIf(model -> model.getType() != roomType);
        models.removeIf(model -> model.getExitTemplate() != template);

        if (models.isEmpty()) {
            if (roomPoolStack.isEmpty())
                return null;
            return getNextLargestRandomModel(roomType, template, entrance, templateGroup);
        }
        //        if (lastPool!=models){
        //            randomPool = new ArrayList<>(models);
        //            lastPool = models;
        //        } else {
        //            models = randomPool;
        //        }
        //        if (!ListMaster.isNotEmpty(models)) {
        //            models = templateMap.get(ROOM_TYPE.COMMON_ROOM).get(template);
        //        }
        Boolean[] rotations =
         getRotations(entrance, DEFAULT_ENTRANCE_SIDE);
        RoomModel model = new RandomWizard<RoomModel>().getRandomListItem(models);
        //        randomPool.remove(model);
        //        main.system.auxiliary.log.LogMaster.log(1, roomType + " Model chosen with exit "
        //         + template + ": " + model.getCellsString());
        model = clone(model);
        if (rotations != null) {
            model.setRotated(rotations);
            //            main.system.auxiliary.log.LogMaster.log(1, roomType + " after rotations: "
            //             + model.getCellsString());
        }
        return model;
    }

    private Boolean[] getRotations(FACING_DIRECTION from, FACING_DIRECTION to) {
        if (from == null)
            return new Boolean[0];
        if (to == null)
            return new Boolean[0];
        int dif = from.getDirection().getDegrees() - to.getDirection().getDegrees();
        int turns = dif / 90;
        boolean clockwise = true;
        if (turns < 0)
            clockwise = false;
        Boolean[] bools = new Boolean[Math.abs(turns)];
        Arrays.fill(bools, clockwise);
        return bools;
    }

    private RoomModel clone(RoomModel model) {
        return new RoomModel(model.getCells(), model.getType(), model.getExitTemplate());
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

    public void resetSizedRandomRoomPools(ROOM_TEMPLATE_GROUP templateGroup) {
        roomPoolStack.clear();
        Map<Dimension, List<RoomModel>> pools = new LinkedHashMap<>();
        List<Dimension> dimensions = new ArrayList<>();
        for (RoomModel model : models.get(templateGroup)) {
            Dimension dimension = new Dimension(model.getWidth(), model.getHeight());
            MapMaster.addToListMap(pools, dimension, model);
            if (!dimensions.contains(dimension))
                dimensions.add(dimension);
        }
        Collections.sort(dimensions,
         new SortMaster<Dimension>().getSorterByExpression_((Dimension dim)
          -> (int) -(dim.getHeight() * dim.getWidth())));

        for (Dimension dimension : dimensions) {
            roomPoolStack.add(pools.get(dimension));
        }
    }
    //TODO multiple objects on cell?

}
