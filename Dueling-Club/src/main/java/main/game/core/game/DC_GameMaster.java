package main.game.core.game;

import main.content.PARAMS;
import main.data.XList;
import main.entity.Ref;
import main.entity.obj.DC_Cell;
import main.entity.obj.Obj;
import main.entity.obj.Structure;
import main.entity.obj.unit.Unit;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game.GAME_TYPE;
import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;
import main.system.math.PositionMaster;

import java.util.*;


/**
 * Created by JustMe on 2/15/2017.
 */
public class DC_GameMaster extends GameMaster {

    protected DequeImpl<Unit> units;
    protected DequeImpl<Structure> structures;
    private Map<Coordinates, List<Unit>> unitMap;
    private Map<Coordinates, List<Unit>> unitCache = new HashMap<>();


    public DC_GameMaster(DC_Game game) {
        super(game);
        structures = new DequeImpl<>();

        units = new DequeImpl<Unit>() {
            public boolean add(Unit e) {
                if (e.isHidden()) {
                    return false;
                }
                return super.add(e);
            }
        };
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    public boolean isSkirmishOrScenario() {
        return getGame().getGameType() == GAME_TYPE.SKIRMISH || getGame().getGameType() == GAME_TYPE.SCENARIO;
    }


    public Obj getObjectVisibleByCoordinate(Coordinates c) {
        return getObjectByCoordinate(null, c, true);
    }

    public Obj getObjectVisibleByCoordinate(Integer z, Coordinates c) {
        return getObjectByCoordinate(z, c, true);
    }

    public Obj getObjectByCoordinate(Coordinates c, boolean cellsIncluded) {
        return getObjectByCoordinate(null, c, cellsIncluded, true, true);
    }

    public Obj getObjectByCoordinate(Integer z, Coordinates c, boolean cellsIncluded) {
        return getObjectByCoordinate(z, c, cellsIncluded, true, true);
    }

    public Obj getObjectByCoordinate(Integer z, Coordinates c, boolean cellsIncluded,
                                     boolean passableIncluded, boolean overlayingIncluded) {
        if (c == null) {
            return null;
        }
        List<Unit> list = getObjectsOnCoordinate(c);
        if (list.isEmpty()) {
            if (cellsIncluded) {
                return getCellByCoordinate(c);
            }
            return null;
        }
        return list.get(0);
    }


    public List<Unit> getObjectsOnCoordinate(Coordinates c) {
        // [QUICK FIX] - consider no-reset coordinate changes for AI etc
        List<Unit> units = getUnitCache().get(c);
        if (units != null) {
            return units;
        }
        units = getObjectsOnCoordinate(null, c, null, true, false);
        getUnitCache().put(c, units);
        return units;
    }

    public List<Unit> getOverlayingObjects(Coordinates c) {
        return getObjectsOnCoordinate(null, c, true, true, false);

    }

    public List<Unit> getObjectsOnCoordinate(Integer z, Coordinates c,
                                             Boolean overlayingIncluded, boolean passableIncluded, boolean cellsIncluded) {
        // TODO auto adding cells won't work!
        if (c == null) {
            return null;
        }

        if (z == null) {
            z = getGame().getDungeon().getZ();
        }
        XList<Unit> list = new XList<>();

        for (Unit unit : getUnits()) {
            if (overlayingIncluded != null) {
                if (overlayingIncluded) {
                    if (!unit.isOverlaying()) {
                        continue;
                    }
                } else {
                    if (unit.isOverlaying()) {
                        continue;
                    }
                }
            }

            if (!passableIncluded) {
                if (unit.isPassable()) {
                    continue;
                }
            }
            if (unit.getZ() != z) {
                continue;
            }
            if (unit.getCoordinates().equals(c)) {
                list.add(unit);
            }
        }

        return list;
    }

    public void remove(Obj obj) {
        game.getState().removeObject(obj.getId());
        obj.removed();
        if (obj instanceof Unit) {
            getUnits().remove(obj);
        }
        if (obj instanceof Structure) {
            getStructures().remove(obj);
        }
    }

    public DC_Cell getCellByCoordinate(Coordinates coordinates) {
        return (DC_Cell) getGame().getBattleField().getCell(coordinates);
    }

    public List<DC_Cell> getCellsForCoordinates(List<Coordinates> coordinates) {
        List<DC_Cell> list = new LinkedList<>();
        for (Coordinates c : coordinates) {
            list.add(getCellByCoordinate(c));
        }
        return list;
    }

    public Unit getUnitByCoordinate(Coordinates coordinates) {
        Collection<Unit> list = getUnitsOnCoordinates(coordinates);
        //sort?? TODO
        if (list.isEmpty()) {
            return null;
        }
        return (Unit) list.iterator().next();
//         getGame().getBattleField().getObj(coordinates);
    }

    public Collection<Unit> getUnitsOnCoordinates(Coordinates... coordinates) {
        return getUnitsForCoordinates(new HashSet<>(Arrays.asList(coordinates)));
    }

    public Collection<Unit> getUnitsForCoordinates(Set<Coordinates> coordinates) {
        Collection<Unit> list = new LinkedList<>();
        for (Coordinates c : coordinates) {
            for (Unit unit : getUnits()) {
                if (unit.getCoordinates().equals(c)) {
                    list.add(unit);
                }
            }
        }
        return list; // TODO z-coordinate?
    }

    public Collection<? extends Obj> getCellsForCoordinates(Set<Coordinates> coordinates) {
        Collection<Obj> list = new LinkedList<>();
        for (Coordinates c : coordinates) {
            for (Obj cell : getCells()) {
                if (cell.getCoordinates().equals(c)) {
                    list.add(cell);
                }
            }
        }
        return list;
    }

    public void removeUnit(Unit unit) {
        getUnits().remove(unit);
    }

    public void clear() {
        getUnits().clear();
        getStructures().clear();
    }

    public Set<Obj> getCells() {
        return new HashSet<>(getGame().getBattleField().getGrid().getCells());
    }

    public DequeImpl<Unit> getUnits() {
        return units;
    }

    public DequeImpl<Structure> getStructures() {
        return structures;
    }

    public Map<Coordinates, List<Unit>> getUnitMap() {
        if (unitMap == null) {
            unitMap = new HashMap<>();
        }
        return unitMap;
    }

    public Map<Coordinates, List<Unit>> getUnitCache() {
        return unitCache;
    }

    public void checkAddUnit(Obj obj) {
        if (obj instanceof Unit) {
            if (!getUnits().contains(obj)) {
                getUnits().add((Unit) obj);
            }
        }
        if (obj instanceof Structure) {
            if (!getStructures().contains(obj)) {
                getStructures().add((Structure) obj);
            }
        }
    }
    public Unit getUnitByName(String name, Ref ref){
        return getUnitByName(name, ref, null, null, null);
    }
    public Unit getUnitByName(String name, Ref ref
     , Boolean ally_or_enemy_only, Boolean distanceSort, Boolean powerSort
    ) {
        List<Unit> matched = new LinkedList<>();
        for (Unit unit : getUnits()) {
            if (ally_or_enemy_only != null)
                if (unit.getOwner() == ref.getSourceObj().getOwner())
                    if (ally_or_enemy_only)
                        continue;
            if (unit.getOwner() != ref.getSourceObj().getOwner())
                if (!ally_or_enemy_only)
                    continue;
            if (StringMaster.isEmpty(name) || unit.getName().equalsIgnoreCase(name)) {
                matched.add(unit);
            }
            //TODO
        }

        if (matched.size() == 1)
            return matched.get(0);
if (distanceSort!=null )
        if (distanceSort) {
            SortMaster.sortByExpression(matched,
             unit1 -> -PositionMaster.getDistance((Obj) unit1, ref.getSourceObj()));
            return matched.get(0);
        }
        if (powerSort!=null )
        if (powerSort) {
            SortMaster.sortByExpression(matched,
             unit1 -> unit1.getIntParam(PARAMS.POWER));
            return matched.get(0);
        }

        return new RandomWizard<Unit>().getRandomListItem(matched);

    }
}
