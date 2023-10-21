package elements.exec.condition.targeting;

import elements.content.enums.FieldConsts;
import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import elements.exec.targeting.area.MeleeTargeter;
import framework.entity.field.Unit;
import main.system.math.PositionMaster;

/**
 * Created by Alexander on 10/20/2023
 */
public class MeleeCondition implements Condition {
    boolean closeQuarters;
    boolean longReach;

    @Override
    public boolean check(EntityRef ref) {
        FieldConsts.Cell srcCell = ref.getSource().getPos().getCell();
        FieldConsts.Cell targetCell =((Unit)ref.getMatch()).getPos().getCell();
        // MeleeTargeter
        return MeleeTargeter.checkTarget(srcCell, targetCell, ref.getSource().isAlly(), closeQuarters, longReach);

        /*
        largely depends on unit's own position
        Q: Rear - special case

        only 1 row - either front or back
        if nobody occupies certain cells, melee can hit beyond its range
         */
    }

    public MeleeCondition setCloseQuarters(boolean closeQuarters) {
        this.closeQuarters = closeQuarters;
        return this;
    }

    public MeleeCondition setLongReach(boolean longReach) {
        this.longReach = longReach;
        return this;
    }
}
