package eidolons.game.exploration.dungeon.generator.fill;

import eidolons.game.exploration.dungeon.generator.GeneratorEnums;
import eidolons.game.exploration.dungeon.generator.LevelData;
import eidolons.game.exploration.dungeon.generator.model.AbstractCoordinates;
import eidolons.game.exploration.dungeon.generator.model.LevelModel;
import eidolons.game.exploration.dungeon.generator.model.ModelMaster;
import eidolons.game.exploration.dungeon.generator.model.Room;
import eidolons.game.exploration.dungeon.generator.tilemap.TileMap;
import eidolons.game.exploration.dungeon.generator.tilemap.TileMapper;
import eidolons.game.exploration.dungeon.generator.tilemap.TilesMaster;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.exploration.dungeon.struct.LevelBlock;
import eidolons.game.exploration.dungeon.struct.LevelZone;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.SortMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;

import java.util.ArrayList;
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

    protected WeightMap<GeneratorEnums.ROOM_CELL> fillerMap;
    protected LevelZone zone;
    protected String originalTileMap;

    public RngFiller(WeightMap<GeneratorEnums.ROOM_CELL> fillerMap) {
        this.fillerMap = fillerMap;
    }

    @Override
    public LevelData getData() {
        return model.getData();
    }

    public void fill(LevelModel model) {
        this.model = model;
        fillRequired = getRequiredFillDefault();
        originalTileMap = model.toASCII_Map();

        main.system.auxiliary.log.LogMaster.log(1,
         getClass().getSimpleName() + " is filling... Model before:\n " + model);
        manualFill();
        List<Room> mandatoryFillRooms = getMandatoryFillRooms();
        for (Room room : mandatoryFillRooms) {
            fillMandatory(room);
        }
        //additional fill 
        for (LevelZone zone : model.getZones()) {
            fill(zone);
        }
        model.setCells(new TileMapper(model, model.getData()).build(model));
        main.system.auxiliary.log.LogMaster.log(1, getClass().getSimpleName() + " is done filling, Model after:\n " + model);
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

    protected boolean checkAdjacent(Coordinates coordinates, GeneratorEnums.ROOM_CELL filler, LevelBlock block) {
        return getAdjacent(coordinates, filler, block) > 0;
    }

    protected boolean checkDone() {
        return false;
    }

    public final float getFillCoef_() {
        if (getFillCoefConst() != null)
            return getFillCoef() * getData().getFloatValue(getFillCoefConst()) / 100;
        return getFillCoef();
    }

    protected List<Room> getMandatoryFillRooms() {
        return model.getRoomMap().values().stream().filter(room -> getMandatoryTypes().contains(room.getType())).collect(Collectors.toList());
    }

    public void fill(LevelZone zone) {
        if (zone.getSubParts().isEmpty())
            return;
        this.zone = zone;
        currentFill = calculateFill();
        float dif;

        main.system.auxiliary.log.LogMaster.log(1, zone + " being filled; now there's fill of %" + currentFill);
        Loop loop = new Loop(zone.getSubParts().size() * 100);
        while (loop.continues()) {
            if (!(!checkDone() && ((dif = fillRequired - currentFill) > 0.05f)))
                break;
            Stack<Room> prioritySpawnLocations = createPrioritySpawnLocations(zone);
            if (prioritySpawnLocations.isEmpty())
                break;
            fill(prioritySpawnLocations, dif);
            currentFill = calculateFill();
        }
        main.system.auxiliary.log.LogMaster.log(1, zone + " filled up to %" + currentFill);
    }

    protected Stack<Room> createPrioritySpawnLocations(LevelZone zone) {
        Stack<Room> stack = new Stack<>();
        stack.addAll(
         model.getRoomMap().values().stream().filter(
          room -> room.getZone() == zone).filter(
          room -> !getMandatoryTypes().contains(room.getType())).filter
          (room -> checkTypeIsFilled(room.getType())).sorted(
          new SortMaster<Room>().getSorterByExpression_(this::getSortValue)
         ).collect(Collectors.toList()));
        return stack;
    }


    protected List<Coordinates> getPointsToFill(TileMap map, Room room, float maxCoef, int min) {
        List<Coordinates> fullList = filterByCellType(map, ModelMaster.getCoordinateList(room));
        List<Coordinates> filterCoordinates =
         filterCoordinates(map, room, ModelMaster.getCoordinateList(room));

        int limit = Math.max(min, Math.round(fullList.size() * maxCoef));

        return selectPointsToFill(filterCoordinates, limit);

    }

    private List<Coordinates> selectPointsToFill(List<Coordinates> fullList, int limit) {
        if (isSymmetricFill()) {
            //bound ?
            // odd/even
            List<Coordinates> list = new ArrayList<>();
            while (true) {
                List<Coordinates> shape = findShape(fullList, limit);
                if (shape == null)
                    break;
                limit -= shape.size();
                list.addAll(shape);

            }
            if (!list.isEmpty())
                return list;
        }
        Collections.shuffle(fullList);

        if (limit >= fullList.size())
            return fullList;
        return fullList.subList(0, limit);
    }

    protected boolean isSymmetricFill() {
        return false;
    }

    private List<Coordinates> findShape(List<Coordinates> fullList, int limit) {
        DIRECTION d = DIRECTION.UP_LEFT; //TODO cycle thru?
        Coordinates seed = CoordinatesMaster.getFarmostCoordinateInDirection(d, fullList);
        ShapeFillMaster.FILL_SHAPE shape = ShapeFillMaster.getRandomShape(limit);
        int arg = 0;
        List<Coordinates> coords;
        while (new Loop(100 * limit).continues()) {
            coords = ShapeFillMaster.getCoordinatesForShape(shape, seed, arg, fullList, limit);
            if (ShapeFillMaster.checkShape(shape, coords, arg))
                return coords;
        }
        return new ArrayList<>();
    }

    private List<Coordinates> filterByCellType(TileMap map, List<Coordinates> coordinateList) {
        coordinateList.removeIf(c -> map.getMap().get(c) != getFilledRoomCellType());
        return coordinateList;
    }

    private List<Coordinates> filterCoordinates(TileMap map, Room room, List<Coordinates> coordinateList) {

        coordinateList.removeIf(c -> map.getMap().get(c) != getFilledRoomCellType());

        coordinateList.removeIf(c -> TilesMaster.isWallWrappedCell(room.relative(c), room));

        if (isCornersOnly()) {
            coordinateList.removeIf(c -> !TilesMaster.isCornerCell(c, map));
        }
        if (isNeverBlock()) {
            coordinateList.removeIf(c -> TilesMaster.isEntranceCell(room.relative(c), room));
            coordinateList.removeIf(c -> TilesMaster.isPassageCell(room.relative(c), room));
            coordinateList.removeIf(c -> TilesMaster.isCellAdjacentTo(
             room.relative(c), room, false, GeneratorEnums.ROOM_CELL.DOOR));
            coordinateList.removeIf(c -> TilesMaster.isCellAdjacentTo(
             room.relative(c), room, false, GeneratorEnums.ROOM_CELL.ROOM_EXIT));
        }
        if (getMaxDistanceFromEdge() >= 0) {
            if (isAlternativeCenterDistance()) {
                coordinateList.removeIf(c ->
                 TilesMaster.getDistanceFromEdge(room.relative(c), room.getWidth(), room.getHeight()) >
                  getMaxDistanceFromEdge() &&
                  TilesMaster.getDistanceFromCenter(room.relative(c), room.getWidth(), room.getHeight()) >
                   getMaxDistanceFromCenter()
                );
            } else {
                coordinateList.removeIf(c -> TilesMaster.getDistanceFromEdge(room.relative(c), room.getWidth(), room.getHeight()) >
                 getMaxDistanceFromEdge());
                if (getMaxDistanceFromCenter() >= 0)
                    coordinateList.removeIf(c -> TilesMaster.getDistanceFromCenter(room.relative(c), room.getWidth(), room.getHeight()) >
                     getMaxDistanceFromCenter());
            }

        }
        return coordinateList;
    }

    protected GeneratorEnums.ROOM_CELL getFilledRoomCellType() {
        return isFloorOrWallFiller()
         ? GeneratorEnums.ROOM_CELL.FLOOR : GeneratorEnums.ROOM_CELL.WALL;
    }

    protected void fillMandatory(Room room) {
        LevelBlock block = model.getBlocks().get(room);
        int min = getMinFilledCells(block.getRoomType());
        // TODO getMinMandatoryFill() *
        List<Coordinates> toFill =
         getPointsToFill(block.getTileMap(), room, getMaxMandatoryFill() * getFillCoef(room.getType()), min);

        fillCells(toFill, block, min);
    }

    protected void fill(Stack<Room> prioritySpawnLocations, float dif) {
        Room room = prioritySpawnLocations.pop();
        LevelBlock block = model.getBlocks().get(room);

        int min = getMinFilledCells(block.getRoomType());

        List<Coordinates> toFill =
         getPointsToFill(block.getTileMap(), room, BLOCK_FILL_COEF * dif, min);
        /*
        choose spots or filler first?
        could choose spots for maximum fill...
         */
        fillCells(toFill, block, min);

    }

    protected void fillCells(List<Coordinates> toFill, LevelBlock block, int min) {
        int i = 0;
        for (Coordinates coordinates : toFill) {
            if (i++ > min) //avrg result = min+(max-min)/2
                if (RandomWizard.random())
                    continue;
            GeneratorEnums.ROOM_CELL filler = null;
            Loop loop = new Loop(fillerMap.size() * 5);
            while (loop.continues()) {
                filler = getFillerRandomized();
                if (!isNoAdjacencyLimits())
                    if (getMaxAdjacency(filler) <= getAdjacent(coordinates, filler, block)) {
                        filler = null;
                        continue;
                    }
                break;
            }
            if (filler != null)
                placeFiller(coordinates, block, filler);
        }
    }

    protected int getMinFilledCells(ROOM_TYPE roomType) {
        return 0;
    }

    private GeneratorEnums.ROOM_CELL getFillerRandomized() {
        return fillerMap.getRandomByWeight();
    }

    protected void placeFiller(Coordinates coordinates, LevelBlock block, GeneratorEnums.ROOM_CELL filler) {
        if (isCornersOnly()) {
            if (!TilesMaster.isCornerCell(coordinates, block.getTileMap()))
                return;
        }
        if (RandomWizard.chance(getJustDontChance(block ,filler))){
            return;
        }
        model.placeCell(coordinates, block, filler);

    }

    protected int getJustDontChance(LevelBlock block, GeneratorEnums.ROOM_CELL filler) {
        return 0;
    }


    protected boolean isOverlaying() {
        return false;
    }

    protected int getAdjacent(Coordinates coordinates,
                              GeneratorEnums.ROOM_CELL filler,
                              LevelBlock block) {
        TileMap map = block.getTileMap();
        return (int) map.getMap().keySet().stream().filter(c ->
         c.isAdjacent(coordinates)).map(c -> map.getMap().get(c)).filter(cell -> cell == filler).count();

    }

    protected float calculateFill() {
        float n = 0;
        for (LevelZone levelZone : model.getZones()) {
            n += calculateFill(levelZone);
        }
        n = n / model.getZones().size();
        return n;
    }

    protected float calculateFill(LevelZone zone) {
        if (zone.getSubParts().isEmpty())
            return 0;
        float n = 0;
        for (LevelBlock block : zone.getSubParts()) {
            n += calculateFill(block);
        }
        n = n / zone.getSubParts().size();
        return n;
    }

    protected float calculateFill(LevelBlock block) {
        float fillable =
         getPointsToFill(block.getTileMap(), model.getRoom(block), 1, 0).size();
        if (fillable == 0)
            return 1;
        float filled =
         (int) block.getTileMap().getMap().values().stream().filter(cell ->
          fillerMap.containsKey(cell)).count();
        return filled / fillable;
    }

    @Override
    public GeneratorEnums.LEVEL_VALUES getFillCoefConst() {

        return LevelData.getFillCoefValue(getFillCellType());
    }

    protected abstract GeneratorEnums.ROOM_CELL getFillCellType();

    public void manualFill() {
        //before auto fill?
        wrapExits();
        //        wrapDoors();
        fillSymmetry();

    }

    protected void fillSymmetry() {
        //symmetry, ...
        fillCorners();
        //around center?
    }

    private void fillCorners() {
        for (Room room : model.getBlocks().keySet()) {
            if (!tryFillCorners(room, 0, 0))
                tryFillCorners(room, 1, 1);
        }
    }

    private boolean tryFillCorners(Room room, int x, int y) {
        Coordinates[] corners = new Coordinates[]{
         new AbstractCoordinates(x, y),
         new AbstractCoordinates(room.getWidth() - x - 1, y),
         new AbstractCoordinates(x, room.getHeight() - 1 - y),
         new AbstractCoordinates(room.getWidth() - 1 - x, room.getHeight() - 1 - y),
        };
        if (!RandomWizard.chance(getFillCornersChance(room))) {
            return false;
        }
        for (Coordinates corner : corners) {
            if (!room.getCells()[corner.x][corner.y].equals(getFilledRoomCellType().getSymbol())) {
                //                    if (!RandomWizard.chance(getFillCornersChance(room))){
                return false;
            }
        }
        LevelBlock block = model.getBlocks().get(room);
        tryFillWithBound(block, corners);
        return true;
    }

    protected int getFillCornersChance(Room room) {
        return 0;
    }

    protected void wrapExits() {
        for (Room room : model.getBlocks().keySet()) {
            int i = 0;
            for (Coordinates exit : room.getExitCoordinates()) {
                FACING_DIRECTION side = room.getUsedExits().get(i++);
                LevelBlock block = model.getBlocks().get(room);
                if (RandomWizard.chance(getWrapPreExitChance(room))) {
                    int degrees = side.flip().getDirection().getDegrees();
                    DIRECTION d = DirectionMaster.getDirectionByDegree(degrees - 135);
                    Coordinates c1 = exit.getAdjacentCoordinate(d);
                    d = DirectionMaster.getDirectionByDegree(degrees + 135);
                    Coordinates c2 = exit.getAdjacentCoordinate(d);
                    tryFillWithBound(block, c1, c2);
                } else if (RandomWizard.chance(getWrapByExitChance(room))) {
                    int degrees = side.getDirection().getDegrees();
                    DIRECTION d = DirectionMaster.getDirectionByDegree(degrees - 90);
                    Coordinates c1 = exit.getAdjacentCoordinate(d);
                    d = DirectionMaster.getDirectionByDegree(degrees + 90);
                    Coordinates c2 = exit.getAdjacentCoordinate(d);
                    tryFillWithBound(block, c1, c2);
                }
            }
        }
    }

    protected int getWrapByExitChance(Room room) {
        return 0;
    }

    protected int getWrapPreExitChance(Room room) {
        return 0;
    }

    private void tryFillWithBound(LevelBlock block, Coordinates... c) {
        for (Coordinates coordinates : c) {
            coordinates.offset(block.getOrigin());
        }
        for (Coordinates coordinates : c) {
            if (block.getTileMap().getMap().get(coordinates)
             != getFilledRoomCellType())
                return;
        }

        GeneratorEnums.ROOM_CELL filler = getFillerRandomized();
        for (Coordinates coordinates : c) {
            placeFiller(coordinates, block, filler);
        }

        for (Coordinates c1 : c) {
            for (Coordinates c2 : c) {
                if (c1 != c2) {
                    RngFillMaster.bindCoordinates(block, c1, c2);
                }
            }
        }
    }
}
