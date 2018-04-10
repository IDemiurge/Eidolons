package eidolons.game.battlecraft.rules.action;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.logic.HeroCreator;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.EntityCheckMaster;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackingRule implements ActionRule {

    private static final int MAX_OVERLAYING_ON_CELL = 4;
    private static final Integer CORPSE_GIRTH_FACTOR = 3;
    private static StackingRule instance;
    private DC_Game game;
    private HashMap<Entity, HashMap<Coordinates, Boolean>> cache = new HashMap();

    public StackingRule(DC_Game game2) {
        game = game2;
        instance = this;
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
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    public static void actionMissed(DC_ActiveObj action) {
        if (RuleKeeper.isRuleOn(RULE.MISSED_ATTACK_REDIRECTION))
            return;
        Ref ref = action.getRef();
        Obj target = ref.getTargetObj();
        List<BattleFieldObject> units = action.getGame().getObjectsAt(
         action.getOwnerObj().getCoordinates());
        units.addAll(action.getGame().getObjectsAt(
         target.getCoordinates()));
        units.remove(action.getOwnerObj());
        units.remove(target);
        if (units.isEmpty()) {
            return;
        }
        Map<BattleFieldObject, Integer> map = new HashMap<>();
        for (BattleFieldObject unit : units) {
            map.put(unit, unit.getIntParam(PARAMS.GIRTH));
        }
        BattleFieldObject randomTarget = new RandomWizard<BattleFieldObject>().getObjectByWeight(map);
        ref.setTarget(randomTarget.getId());
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
        for (BattleFieldObject u : game.getObjectsOnCoordinate(z, c, false, false, false)) {
            if (!units.contains(u)) {
                if (!u.isAnnihilated())
//                    continue; TODO why was Type necessary?
                    units.addCast(!u.isDead() ? u.getType() : u);
                if (u.isWall())
                    if (!u.isDead())
                        return false;
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
            if (UnitAnalyzer.isWall(u)) {
                return false;
            }
            if (u.isDead())
                girth += u.getIntParam(PARAMS.GIRTH) / CORPSE_GIRTH_FACTOR;
            else
                girth += u.getIntParam(PARAMS.GIRTH);
//           TODO  if (DoorMaster.isDoor((BattleFieldObject) u)) {
        }
        // [QUICK FIX]
        if (unit.getIntParam(PARAMS.GIRTH) == 0) {
            girth += StringMaster.getInteger(PARAMS.GIRTH.getDefaultValue());
        } else {
            girth += unit.getIntParam(PARAMS.GIRTH);
        }
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

    @Override
    public boolean isAppliedOnExploreAction(DC_ActiveObj action) {
        return true;
    }

    public void clearCache() {
        cache.clear();
    }

    @Override
    public boolean unitBecomesActive(Unit unit) {
        return true;
    }

}
