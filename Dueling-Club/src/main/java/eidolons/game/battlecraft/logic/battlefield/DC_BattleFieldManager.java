package eidolons.game.battlecraft.logic.battlefield;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.objects.Door;
import eidolons.game.module.dungeoncrawl.objects.DoorMaster.DOOR_STATE;
import main.content.DC_TYPE;
import main.content.enums.rules.VisionEnums;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.BattleFieldManager;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.MapMaster;
import main.system.math.PositionMaster;

import java.util.*;

/**
 * Supposed to provide all Grid-relevant data and methods for changing it
 * TODO extract gameManager into here!
 */
public class DC_BattleFieldManager extends BattleFieldManager   {

    private DC_Game game;
    private Map<Coordinates, List<DIRECTION>> wallMap;
    private Map<Coordinates, List<DIRECTION>> diagonalJoints;
    private Map<Coordinates, List<DIRECTION>> visibleWallMap;
    private Map<Coordinates, List<DIRECTION>> visibleDiagonalJoints;
    private boolean wallResetRequired = true;
    private Map<Coordinates, DOOR_STATE> doorMap = new HashMap<>();

//    DroppedItemManager droppedItemManager;
//    GraveyardManager graveyardManager;
//    CoordinatesMaster coordinatesMaster;

    public DC_BattleFieldManager(DC_Game game, Integer id, int w, int h) {
        super(game, id, w, h);
        this.game = game;
    }



    @Override
    public boolean isCellVisiblyFree(Coordinates c) {
        for (Obj obj : game.getUnitsForCoordinates(c)) {
            boolean free = false;
            // getBattlefield().getGrid().getObjCompMap().getOrCreate(c) == null;
            if (!free) {
                free = !VisionHelper.checkVisible((DC_Obj) obj, false);
            }
            if (!free) {
                return false;
            }
        }
        return true;
    }


    public boolean canMoveOnto(Entity unit, Coordinates c) {
        return game.getMovementManager().canMove( unit, c);
    }


    public void resetWallMap() {

            resetWalls();
        resetVisibleWallMap();
        GuiEventManager.trigger(GuiEventType.UPDATE_DOOR_MAP, this.doorMap);
        GuiEventManager.trigger(GuiEventType.UPDATE_WALL_MAP, this.visibleWallMap);
        GuiEventManager.trigger(GuiEventType.UPDATE_DIAGONAL_WALL_MAP, this.visibleDiagonalJoints);
        wallResetRequired = true;
    }


    private void resetVisibleWallMap() {
        visibleWallMap = new LinkedHashMap<>();
        for (Coordinates coordinates1 : wallMap.keySet()) {
            Obj objectByCoordinate = game.getObjectByCoordinate(coordinates1);
            if (objectByCoordinate != null) {
                if (objectByCoordinate instanceof Structure) {
                    if (((Structure) objectByCoordinate).getPlayerVisionStatus()
                            != VisionEnums.PLAYER_VISION.INVISIBLE) {
                        visibleWallMap.put(coordinates1, wallMap.get(coordinates1));
                    }
                }
            }
        }
        // visibleWallMap = new MapMaster().cloneHashMap(wallMap);
        // visibleWallMap.keySet().removeIf((sub) -> {
        //     Boolean seen = game.getCellByCoordinate(sub).isPlayerHasSeen();
        //     // game.getGrid().getWallCache() IDEA: put walls into a static array to access easily
        //     //try easy option for now
        //     // VisionHelper.getMaster().getVisionController().
        //     // VisionHelper.getMaster().getVisionController().getSeenMapper().
        //     //         get(game.getCellByCoordinate(sub));
        //     if (seen == null) {
        //         return true;
        //     }
        //     return !seen;
        // });
        visibleDiagonalJoints = new MapMaster().cloneHashMap(diagonalJoints);
        visibleDiagonalJoints.keySet().removeIf((sub) -> !visibleWallMap.containsKey(sub));
    }

    private void resetWalls() {
        doorMap.clear();
        Map<Coordinates, BattleFieldObject> wallObjects = new HashMap<>();
//   TODO optimization
//    game.getGrid().getWallCache()
        for (Obj obj : game.getObjects(DC_TYPE.BF_OBJ)) {
            BattleFieldObject bfObj = (BattleFieldObject) obj;
                if (bfObj.isWall()) {
                    wallObjects.put(obj.getCoordinates(), bfObj);
                }
                if (bfObj instanceof Door) {
                    doorMap.put(obj.getCoordinates(), ((Door) bfObj).getState());
            }
        }
        if (wallMap == null) {
            wallMap = new HashMap<>();
        }
        wallMap.clear();
        ArrayList<Coordinates> coordinates = new ArrayList<>(wallObjects.keySet());
        for (Coordinates coordinate : coordinates) {
            BattleFieldObject wall = wallObjects.get(coordinate);
            if (wall.isDead()) {
                continue;
            }

            List<DIRECTION> list = new ArrayList<>();

            for (Coordinates c : coordinate.getAdjacent(false)) {
                BattleFieldObject adjWall = wallObjects.get(c);
                if (adjWall != null) {
                    if (adjWall.isWall() && !adjWall.isDead()) {
                        DIRECTION side = DirectionMaster.getRelativeDirection(coordinate, c);
                        list.add(side);
                    }
                }
            }
            adjacent:
            for (Coordinates c : coordinate.getAdjacent(true)) {
                BattleFieldObject adjWall = wallObjects.get(c);
                if (adjWall != null) {
                    if (adjWall.isWall() && !adjWall.isDead()) {
                        DIRECTION side = DirectionMaster.getRelativeDirection(coordinate, c);
                        if (!side.isDiagonal()) {
                            continue;
                        }
                        for (DIRECTION s : list) {
                            if (s.isDiagonal()) {
                                continue;
                            }
                            if (side.getXDirection() == s) {
                                continue adjacent;
                            }
                            if (side.getYDirection() == s) {
                                continue adjacent;
                            }
                        }

                        list.add(side);
                    }
                }
            }
            if (!list.isEmpty()) {
                if (coordinate == null)
                    continue;
                wallMap.put(coordinate, list);
            }
        }

        if (diagonalJoints == null) {
            diagonalJoints = new HashMap<>();
        }
        diagonalJoints.clear();
        loop:
        for (Coordinates c : wallMap.keySet()) {
            for (DIRECTION s : wallMap.get(c)) {
                if (s.isDiagonal()) {
                    // for (Coordinates c :
                    // o.getCoordinates().getAdjacentCoordinates(null)) {
                    // if (wallObjects.getVar(c) != null) {
                    // if (containsAdjacentDiagonal in X direction
                    // }
                    // }
                    List<DIRECTION> list = diagonalJoints.get(c);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    diagonalJoints.put(c, list);
                    if (list.size() == 1) {
                        DIRECTION d = list.get(0);
                        if (s.growX)
                            if (!d.growX)
                                continue;
                            else if (d.growX)
                                continue;
                        if (s.growY)
                            if (!d.growY)
                                continue;
                            else if (d.growY)
                                continue;
                    }
                    list.add(s);
                    continue loop;
                }
            }
        }
        wallResetRequired = false;
    }

    public Map<Coordinates, List<DIRECTION>> getWallMap() {
        if (wallMap == null) {
            resetWalls();
        }
        return wallMap;
    }

    public Map<Coordinates, DOOR_STATE> getDoorMap() {
        return doorMap;
    }

    public Map<Coordinates, List<DIRECTION>> getDiagonalJoints() {
        if (diagonalJoints == null) {
            resetWalls();
        }
        return diagonalJoints;
    }

    public Set<DC_Cell> getCellsWithinRange(BattleFieldObject observer, int i) {
        HashSet<DC_Cell> set = new HashSet<>(game.getCells());
        set.removeIf(cell -> PositionMaster.getDistance(observer, cell) > i);
        return set;
    }

}
