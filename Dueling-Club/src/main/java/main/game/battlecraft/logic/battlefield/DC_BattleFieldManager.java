package main.game.battlecraft.logic.battlefield;

import main.content.DC_TYPE;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.bf.BattleFieldManager;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import main.game.core.game.DC_Game;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.MapMaster;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Supposed to provide all Grid-relevant data and methods for changing it
 * TODO extract gameManager into here!
 */
public class DC_BattleFieldManager extends BattleFieldManager {

    private DC_Game game;
    private Map<Coordinates, List<DIRECTION>> wallMap;
    private Map<Coordinates, List<DIRECTION>> diagonalJoints;
    private Map<Coordinates, List<DIRECTION>> visibleWallMap;
    private Map<Coordinates, List<DIRECTION>> visibleDiagonalJoints;
    private boolean wallResetRequired=true;

    public DC_BattleFieldManager(DC_Game game) {
        super(game);
        this.game = game;
    }


    public Coordinates pickCoordinate() {
        Integer id = game.getManager().select(game.getCells(), new Ref(game));

        if (id == null) {
            return null;
        }

        return game.getObjectById(id).getCoordinates();
    }

    @Override
    public boolean isCellVisiblyFree(Coordinates c) {
        for (Obj obj : game.getUnitsForCoordinates(c)) {
            boolean free = false;
            // getBattlefield().getGrid().getObjCompMap().getOrCreate(c) == null;
            if (!free) {
                free = !VisionManager.checkVisible((DC_Obj) obj, false);
            }
            if (!free) {
                return false;
            }
        }
        return true;
    }


    public boolean canMoveOnto(Entity unit, Coordinates c) {
        return game.getMovementManager().getPathingManager().isGroundPassable(unit, c);
    }


    public void resetWallMap() {

        if (wallResetRequired)
        {
            resetWalls();
        }
        resetVisibleWallMap();
        GuiEventManager.trigger(GuiEventType.UPDATE_WALL_MAP, this.visibleWallMap);
        GuiEventManager.trigger(GuiEventType.UPDATE_DIAGONAL_WALL_MAP, this.visibleDiagonalJoints);
        wallResetRequired = true;
    }

    private void resetVisibleWallMap() {
        visibleWallMap= new MapMaster().cloneHashMap(wallMap);
        visibleWallMap.keySet().removeIf((sub)->{
            DC_Obj obj = (DC_Obj) game.getObjectByCoordinate(sub);
            if (obj==null ){
                return false;
            } //TODO detected, actually
//            return !VisionManager.checkVisible(obj, false);
            return !VisionManager.getMaster().getDetectionMaster(). checkKnownForPlayer(obj );
        });
        visibleDiagonalJoints= new MapMaster().cloneHashMap(diagonalJoints);
        visibleDiagonalJoints.keySet().removeIf((sub)-> !visibleWallMap.containsKey(sub));
//        for (Coordinates sub: wallMap.keySet()){
//           DC_Obj obj = (DC_Obj) game.getObjectByCoordinate(sub);
//            if (VisionManager.checkVisible(obj, false)){
//                visibleWallMap.put(sub, wallMap.get(sub));
//            }
//        }
    }

    public void resetWalls() {
        Map<Coordinates, BattleFieldObject> wallObjects = new HashMap<>();
        for (Obj obj : game.getObjects(DC_TYPE.BF_OBJ)) {
            BattleFieldObject bfObj = (BattleFieldObject) obj;
            if (bfObj.getZ() == game.getDungeon().getZ()) {
                if (bfObj.isWall()) {
                    wallObjects.put(obj.getCoordinates(), bfObj);
                }
            }
        }
        if (wallMap == null) {
            wallMap = new HashMap<>();
        }
        wallMap.clear();
        LinkedList<Coordinates> coordinates = new LinkedList<>(wallObjects.keySet());
        for (Coordinates coordinate : coordinates) {
            BattleFieldObject wall = wallObjects.get(coordinate);
            if (wall.isDead()) {
                continue;
            }

            List<DIRECTION> list = new LinkedList<>();

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
                    // if (wallObjects.get(c) != null) {
                    // if (containsAdjacentDiagonal in X direction
                    // }
                    // }
                    List<DIRECTION> list = diagonalJoints.get(c);
                    if (list == null) {
                        list = new LinkedList<>();
                    }
                    diagonalJoints.put(c, list);
                    if (list.size() == 1) {
                        DIRECTION d = list.get(0);
                        if (s.isGrowX())
                            if (!d.isGrowX())
                                continue;
                            else if (d.isGrowX())
                                continue;
                        if (s.isGrowY())
                            if (!d.isGrowY())
                                continue;
                            else if (d.isGrowY())
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

    public Map<Coordinates, List<DIRECTION>> getDiagonalJoints() {
        if (diagonalJoints == null) {
            resetWalls();
        }
        return diagonalJoints;
    }

}
