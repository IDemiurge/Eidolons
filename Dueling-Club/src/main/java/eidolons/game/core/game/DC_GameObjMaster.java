package eidolons.game.core.game;

import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game.GAME_TYPE;
import main.data.XList;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.core.game.Game;
import main.game.core.game.GameObjMaster;
import main.game.logic.battle.player.Player;
import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by JustMe on 2/15/2017.
 */
public class DC_GameObjMaster extends GameObjMaster {

    protected Set<Unit> units;
    protected Set<Structure> structures;
    private Map<Coordinates, Set<Unit>> unitMap;
    private Map<Coordinates, Set<Unit>> unitCache = new HashMap<>();
    private Map<Coordinates, Set<BattleFieldObject>> objCache = new HashMap<>();
    private Map<Coordinates, Set<BattleFieldObject>> noOverlayingCache = new HashMap<>();
    private Map<Coordinates, Set<BattleFieldObject>> overlayingCache = new HashMap<>();
    private Unit[] unitsArray;
    private Structure[] structuresArray;

    boolean paleAspect;

    public DC_GameObjMaster(DC_Game game, boolean paleAspect) {
        this(game);
        this.paleAspect = paleAspect;
    }

    public DC_GameObjMaster(DC_Game game) {
        super(game);
        structures = new HashSet<>();
        units = new HashSet<>();
        //         new DequeImpl<Unit>() {
        //            public boolean add(Unit e) {
        //                if (e.isHidden()) {
        //                    return false;
        //                }
        //                return super.add(e);
        //            }
        //        };
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
        return getObjectByCoordinate(null, c, cellsIncluded, true, null);
    }

    public Obj getObjectByCoordinate(Integer z, Coordinates c, boolean cellsIncluded) {
        return getObjectByCoordinate(z, c, cellsIncluded, true, true);
    }

    public Obj getObjectByCoordinate(Integer z, Coordinates c, boolean cellsIncluded,
                                     boolean passableIncluded, Boolean overlayingIncluded) {
        if (c == null) {
            return null;
        }
        Set<BattleFieldObject> list = getObjectsOnCoordinate(z, c, overlayingIncluded, passableIncluded, cellsIncluded);
        if (list.isEmpty()) {
            if (cellsIncluded) {
                return getCellByCoordinate(c);
            }
            return null;
        }
        return list.iterator().next();
    }

    //    @Deprecated
    //    public Set<Unit> getObjectsOnCoordinate(Coordinates c) {
    //        // [QUICK FIX] - consider no-reset coordinate changes for AI etc
    //        //        Set<Unit> units = getUnitCache().get(c);
    //        //        if (units != null) {
    //        //            return units;
    //        //        }
    //        //        units = getObjectsOnCoordinate(null, c, null, true, false);
    //        //        getUnitCache().put(c, units);
    //        //        return units;
    //        Set<Unit> set = new HashSet<>();
    //        for (BattleFieldObject object : getObjectsOnCoordinate(c, null)) {
    //            if (object instanceof Unit)
    //                set.add((Unit) object);
    //        }
    //        return set;
    //        return
    //         getUnits().stream().filter(u -> u.getCoordinates().equals(c)).collect(Collectors.toSet());
    //    }

    public Set<BattleFieldObject> getOverlayingObjects(Coordinates c) {
        return getObjectsOnCoordinate(null, c, null , true, false);

    }

    public Set<BattleFieldObject> getObjectsOnCoordinate(Coordinates c,
                                                         Boolean overlayingIncluded) {
        return getObjectsOnCoordinate(null, c, overlayingIncluded, true, false);
    }
    public void clearCache(Coordinates c){
        getCache(true).remove(c);
        getCache(false).remove(c);
        getCache(null ).remove(c);
        //TODO also remove if dead
    }

    public Set<BattleFieldObject> getObjectsOnCoordinate(Integer z, Coordinates c,
                                                         Boolean overlayingIncluded_Not_Only, boolean passableIncluded, boolean cellsIncluded) {
        // TODO auto adding cells won't work!
        //        if (c == null) {
        //            return null;
        //        }
        //        = null;
        //        if (getCache(overlayingIncluded) != null)
        //            set =
        Set<BattleFieldObject> set = getCache(overlayingIncluded_Not_Only).get(c);

        if (set != null) {
            if (!isCacheForStructures())
                return set;
            set = new HashSet<>(set);
        }


        if (!isCacheForStructures() || set == null) {
            set = new HashSet<>();
            for (BattleFieldObject object : getGame().getStructures()) {
                if (object.isPale()!=paleAspect) {
                    continue;
                }
                if (overlayingIncluded_Not_Only != null) {
                    if (overlayingIncluded_Not_Only)
                        if (object.isOverlaying())
                            continue;
                } else {
                    if (!object.isOverlaying())
                        continue;
                }

                if (object.getCoordinates().equals(c))
                    set.add(object);
            }
            if (isCacheForStructures())
                getCache(overlayingIncluded_Not_Only).put(c, set);
        }
        if (set == null) {
            set = new HashSet<>();
        }
        if (overlayingIncluded_Not_Only != null)
            for (BattleFieldObject object : getGame().getUnits()) {
                if (object.getCoordinates().equals(c)) {
                    set.add(object);
                }
            }
        //        if (overlayingIncluded == null)
        //        if (z == 0)
        if (!isCacheForStructures())
            if (getCache(overlayingIncluded_Not_Only) != null) {
                getCache(overlayingIncluded_Not_Only).put(c, set);
            }
        return set;
    }

    private boolean isStreamImpl() {
        return getGame().isDebugMode();
        //        if (isStreamImpl()) {
        //            set = getGame().getBfObjects().stream().filter(object -> {
        //                if (object instanceof Structure) {
        //                    if (overlayingIncluded != null) {
        //                        if (overlayingIncluded) {
        //                            if (!object.isOverlaying()) {
        //                                return false;
        //                            }
        //                        } else {
        //                            if (object.isOverlaying()) {
        //                                return false;
        //                            }
        //                        }
        //                    }
        //                }
        //                return object.getCoordinates().equals(c);
        //            }).collect(Collectors.toSet());
        //
        //        } else {
    }


    public void remove(Obj obj) {
        remove(obj, false);
    }

    public void remove(Obj obj, boolean soft) {
        if (!soft)
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
        return getGame().getGrid().getCell(coordinates);
    }


    public Unit getUnitByCoordinate(Coordinates coordinates) {
        Collection<Unit> list = getUnitsOnCoordinates(coordinates);
        //sort?? TODO
        if (list.isEmpty()) {
            return null;
        }
        return list.iterator().next();
        //         getGame().getBattleField().getObj(coordinates);
    }

    public Collection<Unit> getUnitsOnCoordinates(Coordinates... coordinates) {
        return getUnitsForCoordinates(new HashSet<>(Arrays.asList(coordinates)));
    }

    public Collection<BattleFieldObject> getBfObjectsForCoordinates(Set<Coordinates> coordinates) {
        List<BattleFieldObject> list = getUnits().stream().filter(unit -> coordinates.contains(unit.getCoordinates())).collect(Collectors.toList());
        list.addAll(getStructures().stream().filter(unit -> coordinates.contains(unit.getCoordinates())).collect(Collectors.toList()));
        return list;
    }

    public Collection<Unit> getUnitsForCoordinates(Set<Coordinates> coordinates) {
        return getUnits().stream().filter(unit -> coordinates.contains(unit.getCoordinates())).collect(Collectors.toList());
//        Collection<Unit> list = new HashSet<>();
//        for (Coordinates c : coordinates) {
//            for (Unit unit : getUnits()) {
//                if (unit.getCoordinates().equals(c)) {
//                    list.add(unit);
//                }
//            }
//        }
//        return list; // TODO z-coordinate?
    }

    public Set<DC_Cell> getCellsForCoordinates(Set<Coordinates> coordinates) {
        Set<DC_Cell> list = new HashSet<>();
        for (Coordinates c : coordinates) {
            list.add(getCellByCoordinate(c));
        }
        return list;
    }

    @Override
    public Obj getObjectById(Integer id) {
        Obj obj = super.getObjectById(id);
        if (obj == null) {
//            if (Eidolons.getMainHero() != null) { this is madness... main hero must be added to state!
//                if (Eidolons.getMainHero().getId().equals(id))
//                    return Eidolons.getMainHero();
//            }
            return obj;
        }
        return obj;
    }

    public void removeUnit(Unit unit) {
        getUnits().remove(unit);
    }

    public void removeStructure(Structure structure) {
        getStructures().remove(structure);
    }

    public void clearCaches() {
        if (!isCacheForStructures()) {
            getCache(false).clear();
            getCache(true).clear();
            getCache(null).clear();
            getGame().getGrid().resetObjCells();
            structuresArray = null;
        }
        unitsArray = null;
    }

    private boolean isCacheForStructures() {
        return true;
    }


    public void clear() {
        getUnits().clear();
        getStructures().clear();
    }

    public Set<Obj> getCells() {
        return new LinkedHashSet<>(getGame().getGrid().getCellsSet());
    }

    public Set<Unit> getUnits() {
        return units;
    }

    public Set<Structure> getStructures() {
        return structures;
    }

    @Deprecated
    public Map<Coordinates, Set<Unit>> getUnitMap() {
        if (unitMap == null) {
            unitMap = new HashMap<>();
        }
        return unitMap;
    }

    @Deprecated
    public Map<Coordinates, Set<Unit>> getUnitCache() {
        return unitCache;
    }

    public void tryAddUnit(Obj obj) {
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

    public Unit getUnitByName(String name, Ref ref) {
        return getUnitByName(name, ref, null, null, null);
    }

    public Unit getUnitByName(String name, Ref ref
            , Boolean ally_or_enemy_only, Boolean distanceSort, Boolean powerSort
    ) {
        return getUnitByName(name, ally_or_enemy_only, distanceSort, powerSort,
                ref.getSourceObj().getOwner(), ref.getSourceObj());
    }

    public Unit getUnitByName(String name
            , Boolean ally_or_enemy_only, Boolean distanceSort, Boolean powerSort
            , Player owner, Obj source) {
        List<Unit> matched = new XList<>();
        for (Unit unit : getUnits()) {
            if (ally_or_enemy_only != null) {
                if (unit.getOwner() == owner)
                    if (!ally_or_enemy_only)
                        continue;
                if (unit.getOwner() != owner)
                    if (ally_or_enemy_only)
                        continue;
            }
            if (StringMaster.isEmpty(name) || unit.getName().equalsIgnoreCase(name)) {
                matched.add(unit);
            }
            //TODO
        }

        if (matched.size() == 0)
            return null;
        if (matched.size() == 1)
            return matched.get(0);
        if (distanceSort != null)
            if (distanceSort) {
                SortMaster.sortEntitiesByExpression(matched,
                        unit1 -> -PositionMaster.getDistance((Obj) unit1, source));
                return matched.get(0);
            }
        if (powerSort != null)
            if (powerSort) {
                SortMaster.sortEntitiesByExpression(matched,
                        unit1 -> unit1.getIntParam(PARAMS.POWER));
                return matched.get(0);
            }

        return new RandomWizard<Unit>().getRandomListItem(matched);

    }

    public Map<Coordinates, Set<BattleFieldObject>> getCache(Boolean overlaying) {
        if (overlaying == null)
            return objCache;
        return overlaying ? overlayingCache : noOverlayingCache;
    }

    public Map<Coordinates, Set<BattleFieldObject>> getNoOverlayingCache() {
        return noOverlayingCache;
    }

    public Map<Coordinates, Set<BattleFieldObject>> getOverlayingCache() {
        return overlayingCache;
    }

    public Map<Coordinates, Set<BattleFieldObject>> getObjCache() {
        return objCache;
    }

    public Unit[] getUnitsArray() {
        if (unitsArray == null)
            unitsArray = getUnits().toArray(new Unit[getUnits().size()]);
        return unitsArray;
    }

    public Structure[] getStructuresArray() {
        if (structuresArray == null)
            structuresArray = getStructures().toArray(new Structure[getStructures().size()]);
        return structuresArray;
    }

    public void nextLevel() {
        //        getGame().getGameLoop().setSkippingToNext(true);
        WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_INPUT,
                null);
        WaitMaster.WAIT(100);
        WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED,
                true);
        //pan camera to main hero
        // zoom?
    }

    public BattleFieldObject[][][] getObjCells() {
        return getGame().getGrid().getObjCells();
    }


    public BattleFieldObject[] getObjects(int x_, int y_) {
        return getObjects(x_, y_, true);
    }

    public BattleFieldObject[] getObjects(int x_, int y_, Boolean overlayingIncluded_Not_Only) {
        return getGame().getGrid().getObjects(x_, y_, overlayingIncluded_Not_Only);
    }

    public Set<Structure> getWalls() {
        HashSet<Structure> list = new HashSet<>(getStructures());
        list.removeIf(obj -> !obj.isWall());
        return list;
    }
}
