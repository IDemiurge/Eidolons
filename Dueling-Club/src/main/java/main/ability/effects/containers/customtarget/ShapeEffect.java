package main.ability.effects.containers.customtarget;

import main.ability.effects.Effect;
import main.ability.effects.oneshot.common.SpecialTargetingEffect;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.elements.Filter;
import main.elements.Filter.FILTERS;
import main.elements.conditions.Conditions;
import main.elements.targeting.AutoTargeting;
import main.entity.group.GroupImpl;
import main.entity.obj.Obj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.system.FilterMaster;
import main.system.math.DC_PositionMaster;
import main.system.math.Formula;
import main.system.math.PositionMaster.SHAPES;

import java.util.Collection;
import java.util.Set;

public abstract class ShapeEffect extends SpecialTargetingEffect {

    private Formula radius;
    private Formula distance;
    private OBJ_TYPE targetType;

    public ShapeEffect(Effect effects, Formula width, Formula height, Boolean allyOrEnemyOnly,
                       Boolean notSelf, OBJ_TYPE targetType) {
        this.radius = width;
        this.distance = height;
        this.effects = effects;
        this.allyOrEnemyOnly = allyOrEnemyOnly;
        this.notSelf = notSelf;
        this.targetType = targetType;
        effects.setReconstruct(true);
    }

    @Override
    public Effect getEffect() {
        return effects;
    }

    @Override
    public void initTargeting() {
        // init unit group

        Coordinates baseCoordinate = getBaseCoordinate();
        int base_width = radius.getInt(ref);
        int distance = this.distance.getInt(ref);
 coordinates = DC_PositionMaster.getShapedCoordinates(baseCoordinate,
                getFacing(), base_width, distance, getShape());

        Collection<Obj> objects = game.getUnitsForCoordinates(coordinates);

        Filter.filter(objects, targetType);
        if (allyOrEnemyOnly != null)
            if (allyOrEnemyOnly) {
                FilterMaster.applyFilter(objects, FILTERS.ALLY, ref, false);
            } else {
                FilterMaster.applyFilter(objects, FILTERS.ENEMY, ref, false);
            }
        if (notSelf) {
            FilterMaster.applyFilter(objects, FILTERS.NOT_SELF, ref, false);
        }
        if (targetType.equals(OBJ_TYPES.TERRAIN)) {
            objects.addAll(game.getCellsForCoordinates(coordinates));
        }
        targeting = new AutoTargeting(new GroupImpl(objects));
        setFilteringConditions(new Conditions());
        targeting.setConditions(getFilteringConditions());
    }


    protected abstract SHAPES getShape();

    protected abstract Coordinates getBaseCoordinate();

    protected abstract FACING_DIRECTION getFacing();
}
