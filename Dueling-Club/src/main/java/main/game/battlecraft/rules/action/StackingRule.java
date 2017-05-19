package main.game.battlecraft.rules.action;

import main.client.cc.logic.HeroCreator;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.EntityCheckMaster;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_Cell;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.UnitAnalyzer;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.special.DoorMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackingRule implements ActionRule {

    private static final int MAX_OVERLAYING_ON_CELL = 4;
    private static StackingRule instance;
    private DC_Game game;
    private HashMap<Entity, HashMap<Coordinates, Boolean>> cache = new HashMap();

    public StackingRule(DC_Game game2) {
        game = game2;
        instance = this;
    }

    public static void applyStackingModifiers(List<Unit> units) {
        for (Unit unit : units) {
            for (Unit otherUnit : units) {
                if (unit == otherUnit) {
                    continue;
                }

            }
        }
        // friend or foe? grapple

    }

    public static boolean checkCanPlace(Coordinates c, Entity unit,
                                        List<? extends Entity> otherUnits) {
        return checkCanPlace(100, c, unit, otherUnits);
    }

    public static boolean checkCanPlace(Integer maxSpaceTakenPercentage, Coordinates c, Entity unit,
                                        List<? extends Entity> otherUnits) {
        if (EntityCheckMaster.isOverlaying(unit)) {
            boolean result = DC_Game.game.getOverlayingObjects(c).size() < MAX_OVERLAYING_ON_CELL;
            if (!result) {
                LogMaster.log(1, c
                        + "******* Cell already has max number of overlaying Objects!");
            }

            return result;
            // TODO limit number of overlays?
        }
        try {
            return instance.canBeMovedOnto(maxSpaceTakenPercentage, unit, c, 0, otherUnits);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void actionMissed(DC_ActiveObj action) {
        Ref ref = action.getRef();
        Obj target = ref.getTargetObj();
        List<Unit> units = action.getGame().getObjectsOnCoordinate(
                action.getOwnerObj().getCoordinates());
        units.remove(action.getOwnerObj());
        units.remove(target);
        if (units.isEmpty()) {
            return;
        }
        Map<Unit, Integer> map = new HashMap<>();
        for (Unit unit : units) {
            map.put(unit, unit.getIntParam(PARAMS.GIRTH));
        }
        Unit randomTarget = new RandomWizard<Unit>().getObjectByWeight(map);
        ref.setTarget(randomTarget.getId());
        // action.addProperty(G_PROPS.DYNAMIC_BOOLS,
        // DYNAMIC_BOOLS.MISSED_ALREADY); //NO RESTRAINTS! :)
        action.activatedOn(ref);

    }

    public boolean canBeMovedOnto(Entity unit, Coordinates c) {
        return canBeMovedOnto(unit, c, null, null);
    }

    public boolean canBeMovedOnto(Entity unit, Coordinates c, Integer z,
                                  List<? extends Entity> otherUnits) {
        return canBeMovedOnto(100, unit, c, z, otherUnits);
    }

    private boolean canBeMovedOnto(Integer maxSpaceTakenPercentage, Entity unit, Coordinates c, Integer z,
                                   List<? extends Entity> otherUnits) {
        HashMap<Coordinates, Boolean> bools = cache.get(unit);
        boolean result = false;
        if (maxSpaceTakenPercentage == 100) {
            if (bools != null) {
                if (bools.containsKey(c)) {
                    return bools.get(c);
                }
            } else {
                bools = new HashMap<>();
                cache.put(unit, bools);
            }
        }

        //get all units on the cell
        DequeImpl<? extends Entity> units = new DequeImpl<>(otherUnits);
        for (Unit u : game.getObjectsOnCoordinate(z, c, false, false, false)) {
            if (!units.contains(u)) {
                units.addCast(u.getType());
            }
        }
        //check if '1 unit per cell' is on
        if (maxSpaceTakenPercentage <= 0) {
            if (!units.isEmpty()) {
                return false;
            }
        }


        if (unit == null) {
            unit = DataManager.getType(HeroCreator.BASE_HERO, DC_TYPE.CHARS);
        }
        Obj cell;
        if (!game.isSimulation()) {
            cell = game.getCellByCoordinate(c);
        } else {
            cell = new DC_Cell(c, game);
        }
        if (cell == null) {
            return false;
        }

        if (z == null) {
            if (unit instanceof Unit) {
                Unit heroObj = (Unit) unit;
                z = heroObj.getZ();
            }
        }

        //TODO ???
        if (game.isSimulation()) {
            if (units.size() > 1) {
                return false;
            }
        }
        // no passable/overlaying!
        int space = StringMaster.getInteger(PARAMS.SPACE.getDefaultValue());
        if (c != null) {
            if (!game.isSimulation()) {
                space = cell.getIntParam(PARAMS.SPACE);
            }
        }

        int girth = 0;
        for (Entity u : units) {
            if (u == unit) {
                continue;
            }
            if (UnitAnalyzer.isDoor(u)) {
                if (!u.checkProperty(G_PROPS.STATUS, "" + UnitEnums.STATUS.UNLOCKED)) {
                    return false;
                }
            }
            if (UnitAnalyzer.isWall(u)) {
                // not flying
                if (!UnitAnalyzer.isFlying(unit)) {
                    return false;
                }
            }
            girth += u.getIntParam(PARAMS.GIRTH);
            if (DoorMaster.isDoor(u)) {

            }
            // main.system.auxiliary.LogMaster.log(1, "****************** " +
            // u.getName()
            // + "'s Girth " + u.getIntParam(PARAMS.GIRTH));
        }
        // [QUICK FIX]
        if (unit.getIntParam(PARAMS.GIRTH) == 0) {
            girth += StringMaster.getInteger(PARAMS.GIRTH.getDefaultValue());
        } else {
            girth += unit.getIntParam(PARAMS.GIRTH);
        }
        // main.system.auxiliary.LogMaster.log(1, "****************** " + space
        // + " Space vs " + girth
        // + " Girth on " + c + " for " + unit);
        space = space * maxSpaceTakenPercentage / 100;
        if (space >= girth) {
            result = true;
        } else {
            if (unit.getIntParam(PARAMS.GIRTH) > space) {
                if (units.isEmpty()) {
                    result = true;
                }
            }
        }
        if (maxSpaceTakenPercentage == 100) //only cache for default cases!
        {
            bools.put(c, result);
        }
        return result;
    }

    @Override
    public void actionComplete(ActiveObj activeObj) {
        clearCache();

    }

    public void clearCache() {
        cache.clear();
    }

    @Override
    public boolean unitBecomesActive(Unit unit) {
        return true;
    }

    // TODO
    // doors
    // would
    // set
    // this
    // for
    // the
    // underlying
    // cell?
    // Perhaps
    // some
    // walls
    // would
    // reduce
    // it
    // to
    // all
    // adjacent
    // ones
    // too...
    // unless
    // we
    // do
    // terrain
    // types
    // like
    // 'corridor'
}
