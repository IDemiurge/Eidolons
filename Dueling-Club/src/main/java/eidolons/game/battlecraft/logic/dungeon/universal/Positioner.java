package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.test.TestSpawner;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner.SPAWN_MODE;
import eidolons.game.battlecraft.rules.action.StackingRule;
import eidolons.game.core.game.DC_Game;
import main.content.C_OBJ_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by JustMe on 5/7/2017.
 */
public class Positioner<E extends DungeonWrapper> extends DungeonHandler<E> {
    protected Map<Coordinates, List<ObjType>> unitCache;
    private Integer maxSpacePercentageTaken = 100;

    public Positioner(DungeonMaster<E> master) {
        super(master);
    }

    public static Coordinates adjustCoordinate(Coordinates c, FACING_DIRECTION facing) {
        return adjustCoordinate(null, c, facing);
    }

    public static Coordinates adjustCoordinate(Entity entity,
                                               Coordinates c, FACING_DIRECTION facing

    ) {
        return adjustCoordinate(entity, c, facing, null);
    }

    public static Coordinates adjustCoordinate(Entity entity,
                                               Coordinates c, FACING_DIRECTION facing
     , Predicate<Coordinates> filterPredicate
    ) {
        if (c == null) {
            return null;
        }
        Loop loop = new Loop(50);
        Coordinates coordinate = Coordinates.get(c.x, c.y);
        while (loop.continues()) { // TODO remove from adj. list to limit
            // iterations to 8!
            DIRECTION direction = getRandomSpawnAdjustDirection();
            coordinate = c.getAdjacentCoordinate(direction);
            if (coordinate != null) {
                if (filterPredicate != null)
                    if (!filterPredicate.test(coordinate))
                        continue;

                if (!DC_Game.game.isSimulation()) {
                    if (DC_Game.game.getBattleFieldManager().canMoveOnto(entity, coordinate)) {
                        break;
                    }
                }
                if (new StackingRule(DC_Game.game).canBeMovedOnto(entity, coordinate)) {
                    break;
                }
            }
        }
        loop = new Loop(50); // second layer in case first one is fully
        // blocked
        while (!loop.continues() &&

         !DC_Game.game.getBattleFieldManager().canMoveOnto(entity, c)

         // (DC_Game.game.getBattleField().getGrid().isCoordinateObstructed(coordinate)
         || coordinate == null) {

            Coordinates adjacentCoordinate = c
             .getAdjacentCoordinate(getRandomSpawnAdjustDirection());
            coordinate = adjustCoordinate(adjacentCoordinate, facing);
        }
        if (coordinate.isInvalid()) {
            return null;
        }
        return coordinate;
    }

    public boolean isAutoOptimalFacing() {
        return true;
    }

    public Map<Unit, Coordinates> getPartyCoordinates(List<Unit> members) {
        List<String> list = new ArrayList<>();
        for (Unit h : members) {
            list.add(h.getName());
        }
        List<Coordinates> c = getPlayerPartyCoordinates(list);

        return new MapMaster<Unit, Coordinates>().constructMap(members, c);
    }

    public List<Coordinates> getPlayerPartyCoordinates(List<String> partyTypes) {
        return getPartyCoordinates(getDungeon().getPlayerSpawnCoordinates(), true, partyTypes);
    }

    public List<String> getCoordinates(List<String> types, DC_Player owner, SPAWN_MODE mode) {
        return
         ContainerUtils.convertToStringList(
          getPartyCoordinates(null, owner.isMe(), types));
    }


    public List<Coordinates> getPartyCoordinates(Coordinates origin, Boolean me,
                                                 List<String> partyTypes) {

        List<Coordinates> list = new ArrayList<>();
        // TODO
        if (CoreEngine.isArcaneVault() || CoreEngine.isLevelEditor()) {
            origin = Coordinates.get(PositionMaster.getMiddleIndex(false), PositionMaster
             .getMiddleIndex(true));
        } else {
            if (me != null) {
                if (me) {
//                    if (PartyHelper.getParty() != null) {
//                        Unit mh = PartyHelper.getParty().getMiddleHero();
//                        if (mh != null) {
//                            int index = partyTypes.indexOf(mh.getName());
//                            if (index > -1) {
//                                String s = partyTypes.remove(index);
//                                partyTypes.add(0, s);
//                            }
//                        }
//                    }
//TODO formation!
                    if (origin == null)
                        origin = getPlayerSpawnCoordinates();
                } else {

                    origin = getEnemySpawningCoordinates();
                    if (origin == null) {
                        // getGame().getDungeon().getDefaultEnemyCoordinates();
                        // for
                        // more control... TODO
                        origin = getEnemyTestPartyCoordinates();
                    }
                }
            }
        }

        unitCache = new HashMap<>(partyTypes.size(), 1f);
        for (String type : partyTypes) {
            ObjType objType = DataManager.getType(type, C_OBJ_TYPE.UNITS_CHARS);
            // DIRECTION[] prefs = {
            // DIRECTION.LEFT, DIRECTION.RIGHT,
            // };
            Coordinates c = getFirstLayerCenterCoordinate(origin, objType, false);
            if (!DC_Game.game.isSimulation()) {
//                if (unitGroups.get(side) != null) {
//                    unitGroups.get(side).put(c, objType); // facing? TODO
//                }
            }
            MapMaster.addToListMap(unitCache, c, objType);
            list.add(c);
        }
        unitCache.clear();
        return list;
    }

    public Coordinates getEnemySpawningCoordinates() {
        return getEnemyTestPartyCoordinates();
    }

    public Coordinates getPlayerSpawnCoordinates() {
        return Coordinates.getMiddleCoordinate(main.game.bf.directions.FACING_DIRECTION.NONE);
    }

    public Coordinates getEnemyTestPartyCoordinates() {
        // TODO encounter?

        // default - getOrCreate a random point in some range from player start

        Coordinates playerC = getPlayerSpawnCoordinates();
        if (true) //TODO sometimes not?
            return Coordinates.get(playerC.x, playerC.y - (
             TestSpawner.isPlayerUnitGroupMode() ? 1 : 3));
        if (playerC == null) {
            playerC = getPlayerSpawnCoordinates();// Coordinates.getMiddleCoordinate(ArenaPositioner.DEFAULT_PLAYER_SIDE);
        }
        Loop.startLoop(100);
        while (Loop.loopContinues()) {
            int x = playerC.x + RandomWizard.getRandomIntBetween(-4, 4);
            int y = playerC.y + RandomWizard.getRandomIntBetween(-4, 4);
            if (y >= getDungeon().getCellsY() - 1) {
                continue;
            }
            if (x >= getDungeon().getCellsX() - 1) {
                continue;
            }
            if (y <= 0) {
                continue;
            }
            if (x <= 0) {
                continue;
            }
            return Coordinates.get(x, y);
        }
        return null;
    }

    protected Coordinates getFirstLayerCenterCoordinate(Coordinates c, ObjType objType) {
        return getFirstLayerCenterCoordinate(c, objType, true);
    }

    protected Coordinates getFirstLayerCenterCoordinate(Coordinates c, ObjType objType,
                                                        boolean randomPrefSide) {
        if (checkCoordinateNotUsed(c)) {
            if (checkCanPlaceUnitOnCoordinate(c, objType)) {
                return c;
            }
        }
        DIRECTION spawnSide = DIRECTION.LEFT;
        if (randomPrefSide) {
            spawnSide = DirectionMaster.getRandomDirection();
        }
        Coordinates adjacentCoordinate = c.getAdjacentCoordinate(spawnSide);
        if (checkCanPlaceUnitOnCoordinate(adjacentCoordinate, objType)) {
            getFacingAdjuster().unitPlaced(adjacentCoordinate,
             FacingMaster.getFacingFromDirection(
              DIRECTION.LEFT, false, false));
            return adjacentCoordinate;
        }
        DIRECTION direction = spawnSide;
        DirectionMaster.rotate90(spawnSide, false);
//         = c.getAdjacentCoordinate(direction);
        Coordinates nextCoordinate;// orthogonal
        while (true) {
            direction = DirectionMaster.rotate90(direction, false);
            nextCoordinate = c.getAdjacentCoordinate(direction);
            if (checkCanPlaceUnitOnCoordinate(nextCoordinate, objType)) {
                getFacingAdjuster().unitPlaced(nextCoordinate, FacingMaster.getFacingFromDirection(direction, true,
                 true));
                return nextCoordinate;
            }
            if (direction == spawnSide) {
                break;
            }
        }
        direction = DirectionMaster.rotate45(spawnSide, false);
        // diagonal
        nextCoordinate = adjustCoordinate(nextCoordinate, FacingMaster
         .getFacingFromDirection(direction));
        return nextCoordinate;
    }

    private boolean checkCoordinateNotUsed(Coordinates c) {
//        for (Map<Coordinates, ObjType> group : unitDungeonGroups.values()) {
//            if (group.keySet().contains(c)) {
//                return false;
//            }
//        }
        return true;
    }

    protected boolean checkCanPlaceUnitOnCoordinate(Coordinates c, Entity entityToPosition) {
        if (c == null) {
            return false;
        }

        // for (Map<Coordinates, ObjType> group : unitGroups.values())
        // if (group.keySet().contains(c)) // TODO
        // return false;
        List<ObjType> otherUnits = null;
        if (unitCache != null) {
            otherUnits = unitCache.get(c);
        }
        if (otherUnits == null) {
            otherUnits = new ArrayList<>();
        }

        try {
            return StackingRule.checkCanPlace(maxSpacePercentageTaken, c, entityToPosition, otherUnits);
        } catch (Exception e) {
            return true;
        }
    }

    public Integer getMaxSpacePercentageTaken() {
        return maxSpacePercentageTaken;
    }

    public void setMaxSpacePercentageTaken(Integer maxSpacePercentageTaken) {
        this.maxSpacePercentageTaken = maxSpacePercentageTaken;
    }

    public static DIRECTION getRandomSpawnAdjustDirection() {
        DIRECTION direction = FacingMaster.getRandomFacing().getDirection();
        if (RandomWizard.chance(20)) {
            direction = DirectionMaster.rotate45(direction, RandomWizard.random());
        }
        return direction;
    }


}
