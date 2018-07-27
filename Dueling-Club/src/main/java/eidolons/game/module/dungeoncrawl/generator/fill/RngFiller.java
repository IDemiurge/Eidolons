package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.ModelMaster;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TilesMaster;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.system.SortMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/25/2018.
 */
public abstract class RngFiller implements RngFillerInterface {
    public static final float BLOCK_FILL_COEF = 3;
    protected LevelModel model;
    protected float fillRequired;
    protected float currentFill;

    protected WeightMap<ROOM_CELL> fillerMap;

    public RngFiller(LevelModel model, WeightMap<ROOM_CELL> fillerMap) {
        this.model = model;
        this.fillerMap = fillerMap;
    }

    public void fill(LevelModel model) {
        this.model = model;
        fillRequired = getRequiredFillDefault();
        List<Room> mandatoryFillRooms = getMandatoryFillRooms();
        for (Room room : mandatoryFillRooms) {
            fillMandatory(room);
        }
        //additional fill 
        for (LevelZone zone : model.getZones()) {
            fill(zone);
        }
    }


    private List<Room> getMandatoryFillRooms() {
        return model.getRoomMap().values().stream().filter(room -> getMandatoryTypes().contains(room.getType())).collect(Collectors.toList());
    }

    public void fill(LevelZone zone) {

        currentFill = calculateFill();
        float dif = 0;


        while (!checkDone() && ((dif = fillRequired - currentFill) > 0.05f)) {
            Stack<Room> prioritySpawnLocations = createPrioritySpawnLocations(zone);
            fill(prioritySpawnLocations, dif);
            currentFill = calculateFill();
        }
    }

    private Stack<Room> createPrioritySpawnLocations(LevelZone zone) {
        Stack<Room> stack = new Stack<>();
        stack.addAll(
         model.getRoomMap().values().stream().filter(
          room -> room.getZone() == zone).filter(
          room -> !getMandatoryTypes().contains(room.getType())).filter
          (room -> checkTypeIsFilled(room.getType())).sorted(
          new SortMaster<Room>().getSorterByExpression_(room -> getSortValue(room))
         ).collect(Collectors.toList()));
        return stack;
    }

    protected boolean checkTypeIsFilled(ROOM_TYPE type) {
        switch (type) {
            case CORRIDOR:

        }
        return true;
    }

    protected Integer getSortValue(Room room) {
        return 0;
    }

    protected List<Coordinates> getPointsToFill(TileMap map, Room room, float max) {
        List<Coordinates> fullList = ModelMaster.getCoordinateList(room);

        fullList.removeIf(c -> map.getMap().get(c) != getFilledRoomCellType());

        if (isCornersOnly()) {
            fullList.removeIf(c -> TilesMaster.isCornerCell(c, map));
        }
        if (isNeverBlock()) {
            fullList.removeIf(c -> TilesMaster.isEntranceCell(c, room));
        }

        int limit = Math.round(TilesMaster.getCellsOfType(getFilledRoomCellType(), map) * max);

        Collections.shuffle(fullList);

        return fullList.subList(0, limit);
    }


    private ROOM_CELL getFilledRoomCellType() {
        return isFloorOrWallFiller()
         ? ROOM_CELL.FLOOR : ROOM_CELL.WALL;
    }


    private void fillMandatory(Room room) {
        LevelBlock block = model.getBlocks().get(room);
        List<Coordinates> toFill =
         getPointsToFill(block.getTileMap(), room, getMaxMandatoryFill() * getFillCoef(room.getType()));

        fillCells(toFill, block, getMinMandatoryFill() * getFillCoef(room.getType()));
    }


    protected void fill(Stack<Room> prioritySpawnLocations, float dif) {
        Room room = prioritySpawnLocations.pop();
        LevelBlock block = model.getBlocks().get(room);

        int max = 0;

        List<Coordinates> toFill =
         getPointsToFill(block.getTileMap(), room, max);
        /*
        choose spots or filler first?
        could choose spots for maximum fill...
         */
        fillCells(toFill, block, getMinAdditionalFill());

    }


    private void fillCells(List<Coordinates> toFill, LevelBlock block, float minimumPerc) {
        int min = Math.round(toFill.size() * minimumPerc);
        int i = 0;
        for (Coordinates coordinates : toFill) {
            if (i++ > min)
                if (RandomWizard.random())
                    continue;
            ROOM_CELL filler = null;
            Loop loop = new Loop(fillerMap.size() * 5);
            while (loop.continues()) {
                filler = fillerMap.getRandomByWeight();
                if (!isNoAdjacencyLimits())
                    if (getMaxAdjacency(filler) > getAdjacent(coordinates, filler, block))
                        continue;
                break;
            }
            placeFiller(coordinates, block, filler);
        }
    }

    private void placeFiller(Coordinates coordinates, LevelBlock block, ROOM_CELL filler) {
        block.getTileMap().getMap().put(coordinates, filler);
        //        block.getTileMap().update();
    }

    private int getAdjacent(Coordinates coordinates,
                            ROOM_CELL filler,
                            LevelBlock block) {
        TileMap map = block.getTileMap();
        return (int) map.getMap().keySet().stream().filter(c ->
         c.isAdjacent(coordinates)).map(c -> map.getMap().get(c)).filter(cell -> cell == filler).count();

    }

    private boolean checkAdjacent(Coordinates coordinates, ROOM_CELL filler, LevelBlock block) {
        return getAdjacent(coordinates, filler, block) > 0;
    }

    protected boolean checkDone() {
        return false;
    }

    protected abstract float getRequiredFillDefault();

    protected float calculateFill() {
        int n = 0;
        for (LevelZone levelZone : model.getZones()) {
            n += calculateFill(levelZone);
        }
        n = n / model.getZones().size();
        return n;
    }

    protected float calculateFill(LevelZone zone) {
        int n = 0;
        for (LevelBlock block : zone.getSubParts()) {
            n += calculateFill(block);
        }
        n = n / zone.getSubParts().size();
        return n;
    }

    protected float calculateFill(LevelBlock block) {
        int square = block.getWidth() * block.getHeight();
        List<ObjAtCoordinate> units = block.getUnits();
        return BLOCK_FILL_COEF * units.size() / square;
    }

}
