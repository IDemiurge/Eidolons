package main.game.battlecraft.logic.dungeon;

import main.content.C_OBJ_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.arena.ArenaPositioner;
import main.game.battlecraft.logic.meta.PartyManager;
import main.game.battlecraft.rules.action.StackingRule;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.bf.DirectionMaster;
import main.game.core.game.DC_Game;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.graphics.GuiManager;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 5/7/2017.
 */
public class Positioner<E extends DungeonWrapper> extends DungeonHandler<E> {
    protected Map<Coordinates, List<ObjType>> unitCache;
    private Integer maxSpacePercentageTaken = 100;

    public Positioner(DungeonMaster<E> master) {
        super(master);
    }

    public boolean isAutoOptimalFacing() {
        return true;
    }
    public List<Coordinates> initPartyCoordinates(List<String> partyTypes,
                                                  Boolean mine_enemy_third) {

                String partyData = "";
//        getPositioner().setMaxSpacePercentageTaken(MAX_SPACE_PERC_PARTY);
        List<Coordinates> coordinates = null;

        if (PartyManager.getParty() != null) {
            if (MapMaster.isNotEmpty(PartyManager.getParty().getPartyCoordinates())) {
                coordinates = new LinkedList<>(PartyManager.getParty().getPartyCoordinates()
                 .values());
                partyTypes = ListMaster.toNameList(PartyManager.getParty().getPartyCoordinates()
                 .keySet());
            }

        }
        if (coordinates == null) {
            coordinates =  getPartyCoordinates(null, BooleanMaster
             .isTrue(mine_enemy_third), partyTypes);
        }

        int i = 0;

        for (String subString : partyTypes) {
            Coordinates c = coordinates.get(i);
            if (c == null) {
                LogMaster.log(1, subString + " coordinate BLAST!!!");
            }
            i++;
            subString = c + DC_ObjInitializer.COORDINATES_OBJ_SEPARATOR + subString;
            partyData += subString + DC_ObjInitializer.OBJ_SEPARATOR;
            //TODO string not needed?
        }


        return coordinates;
    }

    public static Coordinates adjustCoordinate(Coordinates c, FACING_DIRECTION facing) {
        return adjustCoordinate(null, c, facing);
    }

    public static Coordinates adjustCoordinate(Entity entity, Coordinates c, FACING_DIRECTION facing) {
        if (c == null) {
            return null;
        }
        Loop loop = new Loop(50);
        Coordinates coordinate = new Coordinates(c.x, c.y);
        while (loop.continues()) { // TODO remove from adj. list to limit
            // iterations to 8!
            DIRECTION direction = ArenaPositioner.getRandomSpawnAdjustDirection();
            coordinate = c.getAdjacentCoordinate(direction);
            if (coordinate != null) {
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
             .getAdjacentCoordinate(ArenaPositioner.getRandomSpawnAdjustDirection());
            coordinate = adjustCoordinate(adjacentCoordinate, facing);
        }
        if (coordinate.isInvalid()) {
            return null;
        }
        return coordinate;
    }

    public Map<Unit, Coordinates> getPartyCoordinates(List<Unit> members) {
        List<String> list = new LinkedList<>();
        for (Unit h : members) {
            list.add(h.getName());
        }
        List<Coordinates> c = getPlayerPartyCoordinates(list);

        return new MapMaster<Unit, Coordinates>().constructMap(members, c);
    }

    public List<Coordinates> getPlayerPartyCoordinates(List<String> partyTypes) {
        return getPartyCoordinates(null, true, partyTypes);
    }

    public List<Coordinates> getPartyCoordinates(Coordinates origin, Boolean me,
                                                 List<String> partyTypes) {

        List<Coordinates> list = new LinkedList<>();
        // TODO
        if (CoreEngine.isArcaneVault() || CoreEngine.isLevelEditor()) {
            origin = new Coordinates(PositionMaster.getMiddleIndex(false), PositionMaster
                    .getMiddleIndex(true));
        } else {
            FACING_DIRECTION side = ArenaPositioner.DEFAULT_PLAYER_SIDE;
//            if (getSpawningSide() != null) {
//                side = getSpawningSide();
//            }
//            if (getGame().getGameType() == GAME_TYPE.SCENARIO) {
//                if (getGame().getBattleManager().getEncounter() != null) {
//                    if (!getSpawner().getGame().getBattleManager().getEncounter().isSurrounded()) {
//                        side = ArenaPositioner.SECOND_PLAYER_SIDE;
//                    }
//                }
//            }
//            unitGroups.put(side, new LinkedHashMap<>());

            if (me != null) {
                if (me) {
                    if (PartyManager.getParty() != null) {
                        Unit mh = PartyManager.getParty().getMiddleHero();
                        if (mh != null) {
                            int index = partyTypes.indexOf(mh.getName());
                            if (index > -1) {
                                String s = partyTypes.remove(index);
                                partyTypes.add(0, s);
                            }
                        }
                    }
                    origin = getPlayerSpawnCoordinates(); // ??
                    if (origin == null) {
                        origin = Coordinates.getMiddleCoordinate(side);
                    }

                    if (getPlayerSpawnCoordinates() != null) {
                        origin =  getPlayerSpawnCoordinates();
                    }

                } else {
 
                    origin =  getEnemySpawningCoordinates();
                    if (origin == null) {
                        // getGame().getDungeon().getDefaultEnemyCoordinates();
                        // for
                        // more control... TODO
                        origin = getEnemyTestPartyCoordinates();
                    }
                }
            }
        }
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
            list.add(c);
        }

        return list;
    }

    public Coordinates getEnemySpawningCoordinates() {
        return null;
    }
    public Coordinates getPlayerSpawnCoordinates() {
        return null;
    }

    public Coordinates getEnemyTestPartyCoordinates() {
        // TODO encounter?

        // default - getOrCreate a random point in some range from player start

        Coordinates playerC =  getPlayerSpawnCoordinates();
        if (playerC == null) {
            playerC = Coordinates.getMiddleCoordinate(ArenaPositioner.DEFAULT_PLAYER_SIDE);
        }
        Loop.startLoop(100);
        while (Loop.loopContinues()) {
            int x = playerC.x + RandomWizard.getRandomIntBetween(-4, 4);
            int y = playerC.y + RandomWizard.getRandomIntBetween(-4, 4);
            if (y >= GuiManager.getBattleFieldHeight() - 1) {
                continue;
            }
            if (x >= GuiManager.getBattleFieldWidth() - 1) {
                continue;
            }
            if (y <= 0) {
                continue;
            }
            if (x <= 0) {
                continue;
            }
            return new Coordinates(x, y);
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
        DIRECTION spawnSide = ArenaPositioner.DEFAULT_CENTER_SPAWN_SIDE;
        if (randomPrefSide) {
            spawnSide = DirectionMaster.getRandomDirection();
        }
        Coordinates adjacentCoordinate = c.getAdjacentCoordinate(spawnSide);
        if (checkCanPlaceUnitOnCoordinate(adjacentCoordinate, objType)) {
           getFacingAdjuster().unitPlaced(adjacentCoordinate, 
            FacingMaster.getFacingFromDirection(
                    ArenaPositioner.DEFAULT_CENTER_SPAWN_SIDE, false, false));
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
            otherUnits = new LinkedList<>();
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
}
