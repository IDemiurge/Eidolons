package eidolons.game.battlecraft.logic.battlefield;

import com.badlogic.gdx.utils.ObjectMap;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.objects.Door;
import eidolons.game.module.dungeoncrawl.objects.DoorMaster.DOOR_STATE;
import eidolons.libgdx.bf.decor.wall.WallMaster;
import eidolons.libgdx.bf.overlays.map.WallMap;
import main.content.enums.rules.VisionEnums;
import main.entity.Entity;
import main.entity.EntityCheckMaster;
import main.entity.obj.Obj;
import main.game.bf.BattleFieldManager;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

import java.util.*;

/**
 * Supposed to provide all Grid-relevant data and methods for changing it TODO extract gameManager into here!
 */
public class DC_BattleFieldManager extends BattleFieldManager {

    private final DC_Game game;
    private ObjectMap<Coordinates, List<DIRECTION>> wallDirectionMap;
    private List<BattleFieldObject> wallObjects;
    private ObjectMap<Coordinates, List<DIRECTION>> diagonalJoints;
    private ObjectMap<Coordinates, List<DIRECTION>> visibleWallMap = new ObjectMap<>();
    private ObjectMap<Coordinates, List<DIRECTION>> visibleDiagonalJoints = new ObjectMap<>();
    private final ObjectMap<Coordinates, DOOR_STATE> doorMap = new ObjectMap<>();

    public DC_BattleFieldManager(DC_Game game, Integer id, int w, int h) {
        super(game, id, w, h);
        this.game = game;
    }


    @Override
    public boolean isCellVisiblyFree(Coordinates c) {
        for (Obj obj : game.getUnitsForCoordinates(c)) {
            boolean free = false;
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
        return game.getMovementManager().canMove(unit, c);
    }


    public void resetWallMap() {
        if (!WallMap.on)
            return;
        resetWalls();
        resetVisibleWallMap();
        GuiEventManager.trigger(GuiEventType.UPDATE_DOOR_MAP, this.doorMap);
        GuiEventManager.trigger(GuiEventType.UPDATE_WALL_MAP, this.visibleWallMap);
        GuiEventManager.trigger(GuiEventType.UPDATE_DIAGONAL_WALL_MAP, this.visibleDiagonalJoints);
    }


    private void resetVisibleWallMap() {
        if (CoreEngine.isLevelEditor()) {
            visibleWallMap = wallDirectionMap;
            visibleDiagonalJoints = diagonalJoints;
            return;
        }
        visibleWallMap.clear();
        visibleDiagonalJoints.clear();
        for (BattleFieldObject wall : wallObjects) {
            if (wall.getPlayerVisionStatus()
                    != VisionEnums.PLAYER_VISION.INVISIBLE) {
                visibleWallMap.put(wall.getCoordinates(), wallDirectionMap.get(wall.getCoordinates()));
                visibleDiagonalJoints.put(wall.getCoordinates(), diagonalJoints.get(wall.getCoordinates()));
            }
        }
    }

    public void resetWalls(Set<Coordinates> coordinatesSet) {
        resetWalls(coordinatesSet, false);
    }

    public void resetWalls() {
        if (CoreEngine.isLevelEditor()) {
            game.getMetaMaster().getModuleMaster().getModules().forEach(m -> {
                resetWalls(m.getCoordinatesSet(), true);
            });
        } else
            resetWalls(game.getModule().getCoordinatesSet(), true);
    }

    public void resetWalls(Set<Coordinates> coordinatesSet, boolean full) {
        doorMap.clear();
        wallObjects = new ArrayList<>();
        Map<Coordinates, BattleFieldObject> wallMap = new HashMap<>(game.getStructures().size());

        for (Obj obj : game.getStructures()) {
            if (!coordinatesSet.contains(obj.getCoordinates()))
                continue;
            // if (obj.getModule() != module) {
            //     continue;
            // }
            BattleFieldObject bfObj = (BattleFieldObject) obj;

            if (EntityCheckMaster.isWall(bfObj)) {
                Coordinates c = bfObj.getCoordinates();
                game.getGrid().getWallCache()[c.x][c.y] = true;
                wallObjects.add(bfObj);
                wallMap.put(bfObj.getCoordinates(), bfObj);
            }
            if (bfObj instanceof Door) {
                doorMap.put(obj.getCoordinates(), ((Door) bfObj).getState());
            }
        }
        if (wallDirectionMap == null) {
            wallDirectionMap = new ObjectMap<>();
        }
        wallDirectionMap.clear();
        for (BattleFieldObject wall : wallObjects) {
            if (wall==null ) continue;
            if (wall.isDead()) continue;
            List<DIRECTION> list = new ArrayList<>();
            Coordinates coordinate = wall.getCoordinates();
            for (Coordinates c : coordinate.getAdjacent(false)) {
                BattleFieldObject adjWall = wallMap.get(c);
                if (adjWall != null && adjWall.isWall() && !adjWall.isDead()) {
                    DIRECTION side = DirectionMaster.getRelativeDirection(coordinate, c);
                    list.add(side);
                }
            }
            adjacent:
            for (Coordinates c : coordinate.getAdjacent(true)) {
                BattleFieldObject adjWall = wallMap.get(c);
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
            if (full)
                wallDirectionMap.put(coordinate, list);
            WallMaster.resetWall(wall, list);
        }
        GuiEventManager.trigger(GuiEventType.BF_OBJ_RESET, wallObjects);
        if (!full)
            return;
        if (diagonalJoints == null) {
            diagonalJoints = new ObjectMap<>();
        }
        diagonalJoints.clear();
        loop:
        for (Coordinates c : wallDirectionMap.keys()) {
            for (DIRECTION s : wallDirectionMap.get(c)) {
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

    }

    public ObjectMap<Coordinates, List<DIRECTION>> getWallMap() {
        if (wallDirectionMap == null) {
            resetWalls();
        }
        return wallDirectionMap;
    }

    public ObjectMap<Coordinates, DOOR_STATE> getDoorMap() {
        return doorMap;
    }

    public ObjectMap<Coordinates, List<DIRECTION>> getDiagonalJoints() {
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
