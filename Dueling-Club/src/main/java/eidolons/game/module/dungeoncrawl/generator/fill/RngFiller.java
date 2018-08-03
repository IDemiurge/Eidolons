package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.ModelMaster;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TilesMaster;
import main.game.bf.Coordinates;
import main.system.SortMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;
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
    protected LevelZone zone;

    public RngFiller(WeightMap<ROOM_CELL> fillerMap) {
        this.fillerMap = fillerMap;
    }

    @Override
    public LevelData getData() {
        return model.getData();
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
        model.setCells(new TileMapper(model, model.getData()).build(model));
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

    protected boolean checkAdjacent(Coordinates coordinates, ROOM_CELL filler, LevelBlock block) {
        return getAdjacent(coordinates, filler, block) > 0;
    }

    protected boolean checkDone() {
        return false;
    }


    protected List<Room> getMandatoryFillRooms() {
        return model.getRoomMap().values().stream().filter(room -> getMandatoryTypes().contains(room.getType())).collect(Collectors.toList());
    }

    public void fill(LevelZone zone) {
        if (zone.getSubParts().isEmpty())
            return;
        this.zone = zone;
        currentFill = calculateFill();
        float dif = 0;


        while (true) {
            if (!(!checkDone() && ((dif = fillRequired - currentFill) > 0.05f)))
                break;
            Stack<Room> prioritySpawnLocations = createPrioritySpawnLocations(zone);
            if (prioritySpawnLocations.isEmpty())
                break;
            fill(prioritySpawnLocations, dif);
            currentFill = calculateFill();
        }
    }

    protected Stack<Room> createPrioritySpawnLocations(LevelZone zone) {
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


    protected List<Coordinates> getPointsToFill(TileMap map, Room room, float max) {
        List<Coordinates> fullList = ModelMaster.getCoordinateList(room);

        fullList.removeIf(c -> map.getMap().get(c) != getFilledRoomCellType());

        if (isCornersOnly()) {
            fullList.removeIf(c -> !TilesMaster.isCornerCell(c, map));
        }
        if (isNeverBlock()) {
            fullList.removeIf(c -> TilesMaster.isEntranceCell(c, room));
            fullList.removeIf(c -> TilesMaster.isEntranceCell(c, room));
        }
        if (getMaxDistanceFromEdge() >= 0) {
            if (isAlternativeCenterDistance()) {
                fullList.removeIf(c ->
                 TilesMaster.getDistanceFromEdge(c, room.getWidth(), room.getHeight()) >
                  getMaxDistanceFromEdge() &&
                  TilesMaster.getDistanceFromCenter(c, room.getWidth(), room.getHeight()) >
                   getMaxDistanceFromCenter()
                );
            } else {
                fullList.removeIf(c -> TilesMaster.getDistanceFromEdge(c, room.getWidth(), room.getHeight()) >
                 getMaxDistanceFromEdge());
                if (getMaxDistanceFromCenter() >= 0)
                    fullList.removeIf(c -> TilesMaster.getDistanceFromCenter(c, room.getWidth(), room.getHeight()) >
                     getMaxDistanceFromCenter());
            }

        }

        Collections.shuffle(fullList);

        int limit = Math.round(fullList.size() * max);
        if (limit >= fullList.size())
            return fullList;
        return fullList.subList(0, limit);
    }


    protected ROOM_CELL getFilledRoomCellType() {
        return isFloorOrWallFiller()
         ? ROOM_CELL.FLOOR : ROOM_CELL.WALL;
    }


    protected void fillMandatory(Room room) {
        LevelBlock block = model.getBlocks().get(room);
        List<Coordinates> toFill =
         getPointsToFill(block.getTileMap(), room, getMaxMandatoryFill() * getFillCoef(room.getType()));

        fillCells(toFill, block, getMinMandatoryFill() * getFillCoef(room.getType()));
    }


    protected void fill(Stack<Room> prioritySpawnLocations, float dif) {
        Room room = prioritySpawnLocations.pop();
        LevelBlock block = model.getBlocks().get(room);

        List<Coordinates> toFill =
         getPointsToFill(block.getTileMap(), room, BLOCK_FILL_COEF * dif);
        /*
        choose spots or filler first?
        could choose spots for maximum fill...
         */
        fillCells(toFill, block, getMinAdditionalFill());

    }


    protected void fillCells(List<Coordinates> toFill, LevelBlock block, float minimumPerc) {
        int min = Math.round(toFill.size() * minimumPerc);
        int i = 0;
        for (Coordinates coordinates : toFill) {
            if (i++ > min) //avrg result = min+(max-min)/2
                if (RandomWizard.random())
                    continue;
            ROOM_CELL filler = null;
            Loop loop = new Loop(fillerMap.size() * 5);
            while (loop.continues()) {
                filler = fillerMap.getRandomByWeight();
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

    protected void placeFiller(Coordinates coordinates, LevelBlock block, ROOM_CELL filler) {
        block.getTileMap().getMap().put(coordinates, filler);
        Room room = (Room) MapMaster.getKeyForValue_(model.getBlocks(), block);
        room.getCells()[coordinates.x][coordinates.y] = filler.getSymbol();
//        if (isOverlaying()) {
//            DIRECTION direction = model.getBuilder().
//             getOverlayManager().getDirection(coordinates, block, filler);
//            //when populating only?
//            //offset coords
//            model.getBuilder().getOverlayManager().saveDirection(direction, coordinates, filler);
//        }
        //        block.getTileMap().update();
    }

    protected boolean isOverlaying() {
        return false;
    }

    protected int getAdjacent(Coordinates coordinates,
                              ROOM_CELL filler,
                              LevelBlock block) {
        TileMap map = block.getTileMap();
        return (int) map.getMap().keySet().stream().filter(c ->
         c.isAdjacent(coordinates)).map(c -> map.getMap().get(c)).filter(cell -> cell == filler).count();

    }

    protected float calculateFill() {
        int n = 0;
        for (LevelZone levelZone : model.getZones()) {
            n += calculateFill(levelZone);
        }
        n = n / model.getZones().size();
        return n;
    }

    protected float calculateFill(LevelZone zone) {
        if (zone.getSubParts().isEmpty())
            return 0;
        int n = 0;
        for (LevelBlock block : zone.getSubParts()) {
            n += calculateFill(block);
        }
        n = n / zone.getSubParts().size();
        return n;
    }

    protected float calculateFill(LevelBlock block) {
        int fillable =
         getPointsToFill(block.getTileMap(), model.getRoom(block), Integer.MAX_VALUE).size();
        if (fillable == 0)
            return 0;
        int filled =
         (int) block.getTileMap().getMap().values().stream().filter(cell ->
          fillerMap.values().contains(cell)).count();
        return filled / fillable;
    }

    @Override
    public LEVEL_VALUES getFillCoefConst() {
        return null;
    }
}
