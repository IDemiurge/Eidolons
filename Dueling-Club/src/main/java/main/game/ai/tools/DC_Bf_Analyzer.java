package main.game.ai.tools;

import main.content.PARAMS;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_UnitModel;
import main.game.ai.AI;
import main.game.ai.logic.BfAnalyzer;
import main.game.ai.logic.DC_AI;
import main.game.battlefield.pathing.Path;
import main.game.core.game.DC_Game;
import main.game.core.game.MicroGame;
import main.system.auxiliary.log.LogMaster;

// DECOUPLE FROM AI!!! 
public class DC_Bf_Analyzer extends BfAnalyzer {

    public DC_Bf_Analyzer(MicroGame game, AI ai) {
        super(game, ai);

    }

    public DC_Bf_Analyzer(MicroGame game) {
        super(game, new DC_AI((DC_Game) game, game.getPlayer(false)));
    }

    @Override
    public boolean checkFreeKill() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean checkHitToKill() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Obj getEnemyWithinMovingRange() {
        return getEnemyWithinMovingRange(getAi().getUnit());
    }

    public Obj getEnemyWithinMovingRange(Obj obj) {

        for (Obj enemyObj : getEnemy().getControlledUnits()) { // getPrioritizedEnemyList!
            DC_UnitModel enemyUnit = (DC_UnitModel) enemyObj;
            for (Obj cell : getAi().getGame().getMovementManager()
                    .getAdjacentObjs(enemyUnit, true)) {
                Path path = getAi().getGame().getMovementManager()
                        .getPath(obj, cell);
                if (path != null) {
                    if (checkAttackAfterMove(obj, path)) {

                        return enemyObj;
                    }
                }
            }
        }
        return null;

    }

    private boolean checkAttackAfterMove(Obj obj, Path path) {
        int cost = movementManager.getIntegerCost(path.getCost());
        if (obj.getIntParam(PARAMS.C_N_OF_ACTIONS) >= cost + 1
            // game.getActionManager().getAttackCost(obj)
                ) {
        }
        return false;
    }

    @Override
    public boolean checkFreeHits() {
        return checkCanHit(getAi().getUnit());
    }

    @Override
    public boolean checkCanHit(Obj obj) {
        DC_UnitModel unit = (DC_UnitModel) obj;
        if (!unit.canAttack()) {
            return false;
        }
        // loop thru enemy units?
        if (checkAdjacentAttacks(unit)) {
            return true;
        }

        return false;
    }

    // should return top priority target
    private boolean checkAdjacentAttacks(DC_UnitModel unit) {
        for (Obj obj : getEnemy().getControlledUnits()) {
            DC_UnitModel enemyUnit = (DC_UnitModel) obj;
            if (unit.canAttack(enemyUnit)) {
                if (getAi().getLogic() != null) {
                    getAi().getLogic().setTarget(enemyUnit.getId());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Obj getClosestAttackTarget() {
        DC_UnitModel unit = (DC_UnitModel) ai.getLogic().getUnit();
        Obj enemy_unit = null;
        int cost = Integer.MAX_VALUE;
        for (Obj obj : getEnemy().getControlledUnits()) {
            DC_UnitModel enemyUnit = (DC_UnitModel) obj;
            Obj cell = getClosestCell(enemyUnit, false);
            if (cell == null) {
                continue;
            }
            Path path = movementManager.getPath(unit, cell);
            if (path == null) {
                continue;
            }
            if (cost > path.getIntegerCost()) {
                enemy_unit = enemyUnit;
            }

        }
        if (enemy_unit == null) {
            enemy_unit = enemy.getHeroObj();
        }

        LogMaster.log(LogMaster.AI_DEBUG, enemy_unit
                + " has been picked as closest target for "
                + ai.getLogic().getUnit());
        return enemy_unit;
    }


}
