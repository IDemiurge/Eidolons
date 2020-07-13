package eidolons.ability.effects.containers.customtarget;

import eidolons.entity.obj.BattleFieldObject;
import main.ability.effects.Effect;
import main.content.OBJ_TYPE;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.math.Formula;
import main.system.math.PositionMaster.SHAPE;

public class WaveEffect extends ShapeEffect {
    private final Boolean expanding; // wave

    public WaveEffect(Effect effects, Formula radius, Formula distance,
                      Boolean allyOrEnemyOnly, Boolean notSelf, Boolean expanding,
                      OBJ_TYPE targetType) {
        super(effects, radius, distance, allyOrEnemyOnly, notSelf, targetType);
        this.expanding = expanding;

    }

    protected Coordinates getBaseCoordinate() {
        return (expanding) ? ref.getSourceObj().getCoordinates() : ref
         .getSourceObj().getCoordinates()
         .getAdjacentCoordinate(getFacing().getDirection());
    }

    @Override
    protected FACING_DIRECTION getFacing() {
        return ((BattleFieldObject) ref.getSourceObj()).getFacing();
    }

    @Override
    public SHAPE getShape() {
        return (expanding) ? SHAPE.CONE : SHAPE.RECTANGLE;
    }

}
