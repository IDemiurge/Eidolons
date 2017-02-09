package main.game.ai.brute;

import main.entity.obj.DC_UnitObj;
import main.entity.obj.Obj;
import main.game.ai.AI_Logic;
import main.game.ai.logic.TargetingManager;

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
        DC_UnitObj unit = (DC_UnitObj) logic.getUnit();
        for (Obj target : logic.getAnalyzer().getAdjacentEnemies(unit)) {
            DC_UnitObj attacked = (DC_UnitObj) target;
            if (unit.canAttack(attacked)) {
                return target.getId();
            }
        }
        return -1;
    }

}
