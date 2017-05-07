package main.game.battlecraft.ai.logic.types.brute;

import main.entity.obj.Obj;
import main.entity.obj.unit.DC_UnitModel;
import main.game.ai.AI_Logic;
import main.game.ai.TargetingManager;

public class BruteTargeter extends TargetingManager {

    public BruteTargeter(AI_Logic logic) {
        super(logic);
    }

    @Override
    public int initTarget() {
        int id = -1;

        switch (logic.getAction()) {

            case APPROACH:
                Obj cell = logic.getAnalyzer().getClosestCell(logic.getAnalyzer()
                        .getClosestAttackTarget(), true);
                if (cell == null) {
                    return id;
                } else {
                    id = cell.getId();
                }
                break;
            case ATTACK:
                id = getAttackTarget();
                break;
            case CLOSE_IN:
                // target already set when checking
                // analyzer.getEnemyWithinMovingRange();
                break;
            case ESCAPE:
                break;
            case SPELL:
                break;
            case ABILITY:
                break;

        }
        // log
        return id;
    }

    private int getAttackTarget() {
        DC_UnitModel unit = (DC_UnitModel) logic.getUnit();
        for (Obj target : logic.getAnalyzer().getAdjacentEnemies(unit)) {
            DC_UnitModel attacked = (DC_UnitModel) target;
            if (unit.canAttack(attacked)) {
                return target.getId();
            }
        }
        return -1;
    }

}
