package main.game.battlefield;

import main.content.OBJ_TYPES;
import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates.DIRECTION;

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
    private Map<Coordinates, List<DIRECTION>> wallMap = new HashMap<>();
    private Map<Coordinates, List<DIRECTION>> diagonalJoints = new HashMap<>();

    public DC_BattleFieldManager(DC_Game game, SwingBattleField battlefield) {
        super(game, battlefield);
        this.game = game;
    }

    public DC_BattleField getBattlefield() {
        return (DC_BattleField) super.getBattlefield();
    }

    public Coordinates pickCoordinate() {
        Integer id = game.getManager().select(game.getCells());

        if (id == null)
            return null;

        return game.getObjectById(id).getCoordinates();
    }

    @Override
    public boolean isCellVisiblyFree(Coordinates c) {
        for (DC_HeroObj obj : getBattlefield().getGrid().getCellCompMap().get(c).getObjects()) {
            boolean free = false;
            // getBattlefield().getGrid().getObjCompMap().getOrCreate(c) == null;
            if (!free)
                free = !VisionManager.checkVisible(obj);
            if (!free)
                return false;
        }
        return true;
    }


    public boolean canMoveOnto(Entity unit, Coordinates c) {
        return getBattlefield().canMoveOnto(unit, c);
    }

    @Override
    public boolean placeUnit(MicroObj unit, int x, int y) {
        game.getBattleField().createObj(unit);
        return true;
    }


    public void resetWallMap() {
        Map<Coordinates, DC_HeroObj> wallMap = new HashMap<Coordinates, DC_HeroObj>();
        for (Obj obj : game.getObjects(OBJ_TYPES.BF_OBJ)) {
            DC_HeroObj bfObj = (DC_HeroObj) obj;
            if (bfObj.getZ() == game.getDungeonMaster().getZ())
                if (bfObj.isWall()) {
                    wallMap.put(obj.getCoordinates(), bfObj);
                }
        }
        resetWallMap(wallMap);
    }

    public void resetWallMap(Map<Coordinates, DC_HeroObj> wallObjects) {
        wallMap.clear();
        LinkedList<Coordinates> coordinates = new LinkedList<>(wallObjects.keySet());
        for (Coordinates coordinate : coordinates) {
            DC_HeroObj wall = wallObjects.get(coordinate);
            if (wall.isDead())
                continue;
            if (!VisionManager.checkVisible(wall))
                continue;
            List<DIRECTION> list = new LinkedList<>();

            for (Coordinates c : coordinate.getAdjacent(false)) {
                DC_HeroObj adjWall = wallObjects.get(c);
                if (adjWall != null)
                    if (adjWall.isWall() && !adjWall.isDead()) {
                        DIRECTION side = DirectionMaster.getRelativeDirection(coordinate, c);
                        list.add(side);
                    }
            }
            adjacent:
            for (Coordinates c : coordinate.getAdjacent(true)) {
                DC_HeroObj adjWall = wallObjects.get(c);
                if (adjWall != null)
                    if (adjWall.isWall() && !adjWall.isDead()) {
                        DIRECTION side = DirectionMaster.getRelativeDirection(coordinate, c);
                        if (!side.isDiagonal())
                            continue;
                        for (DIRECTION s : list) {
                            if (s.isDiagonal())
                                continue;
                            if (side.getXDirection() == s)
                                continue adjacent;
                            if (side.getYDirection() == s)
                                continue adjacent;
                        }

                        list.add(side);
                    }
            }
            if (!list.isEmpty())
                wallMap.put(coordinate, list);
        }
        diagonalJoints.clear();
        loop:
        for (Coordinates c : wallMap.keySet()) {
            for (DIRECTION s : wallMap.get(c)) {
                if (s.isDiagonal()) {
                    // for (Coordinates c :
                    // o.getCoordinates().getAdjacentCoordinates(null)) {
                    // if (wallObjects.getOrCreate(c) != null) {
                    // if (containsAdjacentDiagonal in X direction
                    // }
                    // }
                    List<DIRECTION> list = diagonalJoints.get(c);
                    if (list == null)
                        list = new LinkedList<>();
                    diagonalJoints.put(c, list);
                    // if (list.size() == 1) {
                    // DIRECTION d = list.getOrCreate(0);
                    // if (s.isGrowX())
                    // if (!d.isGrowX())
                    // continue;
                    // else if (d.isGrowX())
                    // continue;
                    // if (s.isGrowY())
                    // if (!d.isGrowY())
                    // continue;
                    // else if (d.isGrowY())
                    // continue;
                    // }
                    list.add(s);
                    // continue loop;
                }
            }
        }
    }

    public Map<Coordinates, List<DIRECTION>> getWallMap() {
        return wallMap;
    }

    public Map<Coordinates, List<DIRECTION>> getDiagonalJoints() {
        return diagonalJoints;
    }

}
