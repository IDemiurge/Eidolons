
package eidolons.game.battlecraft.rules.action;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.VoidMaze;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.round.WaterRule;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.struct.Entrance;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.system.libgdx.GdxAdapter;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.entity.EntityCheckMaster;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.Flags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StackingRule implements ActionRule {

    public static final int DEFAULT_SPACE_PER_CELL = 500;
    private static final int MAX_OVERLAYING_ON_CELL = 4;
    private static final Integer CORPSE_GIRTH_FACTOR = 3;
    private static StackingRule instance;
    private final DC_Game game;
    private final HashMap<Entity, HashMap<Coordinates, Boolean>> cache = new HashMap();

    public StackingRule(DC_Game game2) {
        game = game2;
        instance = this;
    }


    public static boolean checkCanPlace(Coordinates c, Entity unit,
                                        List<? extends Entity> otherUnits) {
        if (EntityCheckMaster.isOverlaying(unit)) {
            boolean result = DC_Game.game.getOverlayingObjects(c).size() < MAX_OVERLAYING_ON_CELL;
            if (!result) {
                LogMaster.log(1, c
                        + "******* Cell already has max number of overlaying Objects!");
            }
            return result;
        }
        if (unit instanceof Entrance)
            return true;

        return instance.canBeMovedOnto(unit, c, otherUnits, true);
    }


    public static void actionMissed(DC_ActiveObj action) {
        if (RuleKeeper.isRuleOn(RuleEnums.RULE.MISSED_ATTACK_REDIRECTION))
            return;
        Ref ref = action.getRef();
        Obj target = ref.getTargetObj();
        Set<BattleFieldObject> units = action.getGame().getObjectsNoOverlaying(
                action.getOwnerUnit().getCoordinates());
        units.addAll(action.getGame().getObjectsNoOverlaying(
                target.getCoordinates()));
        units.remove(action.getOwnerUnit());
        units.remove(target);
        if (units.isEmpty()) {
            return;
        }
        Map<BattleFieldObject, Integer> map = new HashMap<>();
        for (BattleFieldObject unit : units) {
            //TODO NF Rules Review - Girth?
            map.put(unit, 1);
        }
        BattleFieldObject randomTarget = new RandomWizard<BattleFieldObject>().getObjectByWeight(map);
        ref.setTarget(randomTarget.getId());

        action.getGame().getLogManager().log(action.getName() + " has missed " +
                target.getNameIfKnown() +
                " and hit " +
                randomTarget +
                " instead!");

        action.activatedOn(ref);

    }

    public boolean isStackingSupported() {
        return ExplorationMaster.isExplorationOn();
    }


    public boolean canBeMovedOnto(Entity unit, Coordinates c) {
        return canBeMovedOnto(unit, c, true);
    }
        public boolean canBeMovedOnto(Entity unit, Coordinates c, boolean cache) {
        return canBeMovedOnto(unit, c, null, cache);
    }

    private boolean canBeMovedOnto(Entity unit, Coordinates c,
                                      List<? extends Entity> otherUnits, boolean useCache) {
        Boolean result = checkPlatform(unit, c);
        if (result != null) {
            return result;
        }
        result = false;
        HashMap<Coordinates, Boolean> bools = cache.get(unit);
        if (useCache ) {
            if (bools != null) {
                if (bools.containsKey(c)) {
                    return bools.get(c);
                }
            } else {
                bools = new HashMap<>();
                cache.put(unit, bools);
            }
        }

        DequeImpl<? extends Entity> units = new DequeImpl<>(otherUnits); //already placed ones?
        for (BattleFieldObject u : game.getObjMaster().getObjects(c.x, c.y, false)) {
            if (!units.contains(u)) {
                if (u == unit || u.isDead())
                    continue;
                if (!isStackingSupported()) {
                    if (!isStackUnit(u)) {
                        return false;
                    }
                }
                if (u.isWater())
                    return WaterRule.checkPassable(u, unit);
                // if (!u.isNeutral() && !u.isMine())
                //     if (game.getVisionMaster().checkInvisible(u)) {
                //         continue;
                //     }
                if (u instanceof Unit)
                    if (!u.isAnnihilated())
                    units.addCast(u);
            }
        }
        if (unit != null) {
                DC_Cell cell;
                if (!game.isSimulation()) {
                    cell = game.getCell(c);
                } else {
                    cell = new DC_Cell(c, game); //TODO this is a hack...
                }
                if (cell.isVOID()) {
                    if (unit != Eidolons.getMainHero() || !Flags.isIDE() || game.getDungeonMaster().getPuzzleMaster().getCurrent() instanceof VoidMaze)// || !VoidHandler.TEST_MODE)
                        if (!unit.checkProperty(G_PROPS.STANDARD_PASSIVES, UnitEnums.STANDARD_PASSIVES.VOIDWALKER.getName())) {
                            return false;
                        }
                }
        }
        if (units.isEmpty()) {
            result = true;
        }
        bools.put(c, result);
        return result;
    }

    private boolean isStackUnit(BattleFieldObject u) {
        if (u instanceof Entrance) {
            return true;
        }
        if (u instanceof Unit) {
            return !((Unit) u).isUnconscious();
        }
        return false;
    }

    private Boolean checkPlatform(Entity unit, Coordinates c) {
        return GdxAdapter.getInstance().getGridManager().checkPlatform(unit, c);
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
