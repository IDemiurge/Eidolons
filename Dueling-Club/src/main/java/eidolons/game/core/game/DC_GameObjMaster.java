package eidolons.game.core.game;

import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import io.vertx.core.impl.ConcurrentHashSet;
import main.data.XList;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.core.game.GameObjMaster;
import main.game.logic.battle.player.Player;
import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
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
    private Boolean unitFindSequential = true;
    public DC_GameObjMaster(DC_Game game, boolean paleAspect) {
        this(game);
        this.paleAspect = paleAspect;
    }

    public DC_GameObjMaster(DC_Game game) {
        super(game);
        structures = new ConcurrentHashSet<>();
        units = new ConcurrentHashSet<>();
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    public Obj getObjectVisibleByCoordinate(Coordinates c) {
        return getObjectByCoordinate(c, true);
    }

    public Obj getObjectByCoordinate(Coordinates c, Boolean overlaying) {
        Set<BattleFieldObject> objectsOnCoordinate = getObjectsOnCoordinate(c, overlaying);
        if (objectsOnCoordinate.isEmpty()) {
            return null;
        }
        return objectsOnCoordinate.iterator().next();
    }


    public Set<BattleFieldObject> getOverlayingObjects(Coordinates c) {
        return getObjectsOnCoordinate(c, null);
    }

    public void clearCache(Coordinates c) {
        getCache(true).remove(c);
        getCache(false).remove(c);
        getCache(null).remove(c);
        //TODO also remove if dead
    }

    public BattleFieldObject[] getObjects(int x_, int y_) {
        return getObjects(x_, y_, true);
    }

    public BattleFieldObject[] getObjects(int x_, int y_, Boolean overlayingIncluded_Not_Only) {
        return getGame().getGrid().getObjects(x_, y_, overlayingIncluded_Not_Only);
    }

    public Set<BattleFieldObject> getObjectsOnCoordinate(Coordinates c,
                                                         Boolean overlayingIncluded_Not_Only) {

        Set<BattleFieldObject> set = getCache(overlayingIncluded_Not_Only).get(c);

        if (set != null) {
            if (isCacheForStructures())
                return set;
            set = new HashSet<>(set);
        }

        if (!isCacheForStructures() || set == null) {
            set = new HashSet<>();

            for (BattleFieldObject object : getGame().getStructures()) {
//                if (object.isPale() != paleAspect) {
//                    continue;
//                }
                if (overlayingIncluded_Not_Only != null) {
                    if (!overlayingIncluded_Not_Only)
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
            game.getState().manager.removeObject(obj.getId(), obj.getOBJ_TYPE_ENUM());
        obj.removed();
        game.removed(obj);
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
        list.removeIf(c -> c == null);
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
        getGame().getGrid().getWallCache()[structure.getX()][structure.getY()] = null;
    }

    public void clearCaches() {
        clearCaches(true);
    }

    public void clearCaches(boolean structures) {
        if (!isCacheForStructures() || structures) {
            getCache(false).clear();
            getCache(true).clear();
            getCache(null).clear();
            getGame().getGrid().resetObjCells();
            structuresArray = null;
        }
        unitsArray = null;
    }

    protected boolean isCacheForStructures() {
        return !CoreEngine.isLevelEditor();
    }


    public void clear() {
        getUnits().clear();
        getStructures().clear();
    }

    public DC_Cell[][] getCells() {
        return (getGame().getGrid().getCells());
    }

    public Set<DC_Cell> getCellsSet() {
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

    public void objAdded(Obj obj) {
        if (obj instanceof Unit) {
            getUnits().add((Unit) obj);
        } else if (obj instanceof Structure) {
            getStructures().add((Structure) obj);
        }
    }

    public BattleFieldObject getByName(String name, Ref ref) {
        return getByName(name, ref, null, null, null);
    }

    public BattleFieldObject getByName(String name, Ref ref
            , Boolean ally_or_enemy_only, Boolean distanceSort, Boolean powerSort
    ) {
        return getByName(null, name, ally_or_enemy_only, distanceSort, powerSort,
                ref.getSourceObj().getOwner(), ref.getSourceObj());
    }

    public BattleFieldObject getByName(Boolean unit_struct_both, String name
            , Boolean ally_or_enemy_only, Boolean distanceSort, Boolean powerSort
            , Player owner, Obj source) {
        List<BattleFieldObject> matched = new XList<>();
        DequeImpl<BattleFieldObject> all = getBfObjects();
        if (unit_struct_both != null) {
            //TODO
        }
        for (BattleFieldObject unit : all) {
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
        if (distanceSort != null) {
            SortMaster.sortEntitiesByExpression(matched,
                    unit1 -> (distanceSort ? -1 : 1) * PositionMaster.getDistance((Obj) unit1, source));
        }
        if (powerSort != null) {
            SortMaster.sortEntitiesByExpression(matched,
                    unit1 -> (powerSort ? 1 : -1) * unit1.getIntParam(PARAMS.POWER));
        }
        if (matched.size() == 2) {
            if (Math.abs(PositionMaster.getDistance(source, matched.get(0)) - PositionMaster.getDistance(source, matched.get(1)))
                    <= 1
            ) {
                return getUnitSequentially(matched);
            }
        }
        return matched.get(0);

    }

    private BattleFieldObject getUnitSequentially(List<BattleFieldObject> matched) {
        if (unitFindSequential != null) {
            unitFindSequential = !unitFindSequential;
            if (unitFindSequential) {
                return matched.get(matched.size() - 1); //TODO try to iterate instead
            }
            return matched.get(0);
        }
        return new RandomWizard<BattleFieldObject>().getRandomListItem(matched);
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
            unitsArray = getUnits().toArray(new Unit[0]);
        return unitsArray;
    }

    public Structure[] getStructuresArray() {
        if (structuresArray == null)
            structuresArray = getStructures().toArray(new Structure[0]);
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


    public Set<Structure> getWalls() {
        HashSet<Structure> list = new HashSet<>(getStructures());
        list.removeIf(obj -> !obj.isWall());
        return list;
    }

    public DequeImpl<BattleFieldObject> getBfObjects() {
        DequeImpl<BattleFieldObject> list = new DequeImpl(getUnits());
        list.addAll(getStructures());
        return list;
    }

}
