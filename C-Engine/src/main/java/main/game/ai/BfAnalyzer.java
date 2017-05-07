package main.game.ai;

import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.MovementManager;
import main.game.bf.pathing.Path;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.log.LogMaster;
import main.system.math.PositionMaster;

import java.util.Collection;
import java.util.List;

public abstract class BfAnalyzer {

    protected MicroGame game;
    protected Player enemy;
    protected Player player;
    protected AI ai;
    protected MovementManager movementManager;

    public BfAnalyzer(MicroGame game, AI ai) {
        this.game = game;
        this.setAi(ai);
        this.movementManager = game.getMovementManager();
        setPlayer(game.getPlayer(!ai.getPlayer().isMe()));
        setEnemy(ai.getPlayer());
    }
//    public abstract  int getMeleeDangerFactor(Unit unit);
//
//    public abstract  int getMeleeDangerFactor(Unit unit, boolean adjacentOnly, boolean now);
//
//    public abstract  int getMeleeThreat(Unit enemy);
//
//    public abstract   int getMeleeThreat(Unit enemy, boolean now);
//
//    public abstract  int getCostFactor(Costs cost, Unit unit);
//
//    public abstract  int getCastingPriority(Unit unit);


    public abstract boolean checkFreeHits();

    public abstract boolean checkFreeKill();

    public abstract boolean checkHitToKill();

    public abstract boolean checkCanHit(Obj unit);

    public abstract Obj getEnemyWithinMovingRange();

    public abstract Obj getClosestAttackTarget();

    // public case[] getCases(){

    public boolean checkActionCase(ACTION_CASES CASE) {
        switch (CASE) {

            case MOVE_TO_HIT:
                Obj target = getEnemyWithinMovingRange();
                if (target != null) {
                    ai.getLogic().setTarget(target.getId());
                    return true;
                }
            case HIT:
                return checkFreeHits();
            case MOVE_TO_ESCAPE:
                // return checkEscape();
                break;

            case HIT_TO_KILL:
                return checkHitToKill();
            default:
                break;

        }
        return false;
    }

    public Obj getClosestCell(Obj targetUnit, boolean canMove) {
        Obj cell = checkAdjacentCell(targetUnit, canMove);

        if (cell != null) {
            return cell;
        }
        for (int i = 2; i < getMaxDistance(); i++) {
            cell = checkCells(targetUnit, movementManager.getCellsInRadius(targetUnit, i), canMove);
            if (cell != null) {
                break;
            }
        }
        if (cell == null) {
            LogMaster
                    .log(LogMaster.AI_DEBUG, "failed to find a cell for : "
                            + getAi().getUnit() + " to attack " + targetUnit);
            // throw new RuntimeException();
        }

        LogMaster.log(LogMaster.AI_DEBUG, cell
                + " is the closest cell to " + targetUnit);
        return cell;

    }

    public int getMaxDistance() {
        return PositionMaster
                .getDistance(new Coordinates(0, 0), new Coordinates(game
                        .getBF_Width(), game.getBF_Height()));
    }

    public Obj checkCells(Obj targetUnit, Collection<Obj> set, boolean canMove) {
        double cost = Double.MAX_VALUE;
        Obj cell = null;
        for (Obj adjCell : set) {
            if (canMove) {
                if (!movementManager.canMove(getAi().getUnit(), adjCell)) {
                    LogMaster
                            .log(LogMaster.AI_DEBUG, ai.getLogic().getUnit()
                                    + " can't move to " + adjCell);
                    continue;
                }
            }
            Path path = movementManager.getPath(targetUnit, adjCell);
            if (path == null) {
                LogMaster.log(LogMaster.AI_DEBUG, ai
                        .getLogic().getUnit() + " has no path to " + adjCell);

                continue;
            }

            if (cost > path.getCost()) {
                cost = path.getCost();
                cell = adjCell;
            }
        }
        return cell;
    }

    public Obj checkAdjacentCell(Obj targetUnit, boolean canMove) {
        return checkCells(targetUnit, getAdjacentCells(targetUnit), canMove);
    }

    public boolean canMove(Obj obj, Obj cell) {
        return movementManager.canMove(obj, cell);
    }

    public boolean noObstacles(Coordinates objCoordinates, Coordinates cellCoordinates) {
        return movementManager.noObstacles(objCoordinates, cellCoordinates);
    }

    public boolean isAdjacent(Obj obj1, Obj obj2) {
        return movementManager.isAdjacent(obj1, obj2);
    }

    public int getDistance(Obj obj1, Obj obj2) {
        return movementManager.getDistance(obj1, obj2);
    }

    public List<Obj> getAdjacentCells(Obj unit) {
        return movementManager.getAdjacentObjs(unit, true);
    }

    public Obj getCell(Coordinates c1) {
        try {
            return movementManager.getCell(c1);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Obj> getAdjacentEnemies(Obj unit) {
        return movementManager.getAdjacentEnemies(unit);

    }

    public AI getAi() {
        return ai;
    }

    public void setAi(AI ai) {
        this.ai = ai;
    }

    public Player getEnemy() {
        return enemy;
    }

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Obj getClosestUnit(Obj source, boolean friendlyFire) {
        return null;
    }

    public boolean checkNoEnemiesLeft() {
        return checkPlayerHasNoUnits(getEnemy());

    }

    public boolean checkNoPlayerUnitsLeft() {
        return checkPlayerHasNoUnits(getPlayer());

    }

    private boolean checkPlayerHasNoUnits(Player player) {
        for (Obj d : player.getControlledUnits()) {
            if (!d.isDead()) {
                return false;
            }
            // panicked? preCheck ownership change?
        }
        return true;
    }

    public enum PRIORITY_CASES implements GameLogicCase {
        // TODO when something needs to be done before anything else...
    }

    public enum ACTION_CASES implements GameLogicCase {
        // TODO when something is obviously a good idea...
        HIT, HIT_TO_KILL, MOVE_TO_CLAIM, MOVE_TO_KILL, MOVE_TO_ESCAPE,
        MOVE_TO_HIT, MOVE_TO_DECLAIM,

    }

    // CASES
    public interface GameLogicCase {

    }

}
