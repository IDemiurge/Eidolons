package eidolons.ability.effects.containers.customtarget;

import eidolons.game.core.game.DC_Game;
import eidolons.system.math.DC_PositionMaster;
import main.ability.effects.Effect;
import main.ability.effects.container.SpecialTargetingEffect;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.elements.Filter;
import main.elements.Filter.FILTERS;
import main.elements.conditions.Conditions;
import main.elements.targeting.AutoTargeting;
import main.entity.group.GroupImpl;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.datatypes.DequeImpl;
import main.system.entity.FilterMaster;
import main.system.math.Formula;
import main.system.math.PositionMaster.SHAPES;

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
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    @Override
    public void initTargeting() {
        // init unit group

        Coordinates baseCoordinate = getBaseCoordinate();
        int base_width = radius.getInt(ref);
        int distance = this.distance.getInt(ref);
        coordinates = DC_PositionMaster.getShapedCoordinates(baseCoordinate,
         getFacing(), base_width, distance, getShape());

        DequeImpl<Obj> objects = new DequeImpl<>();
        objects.addAllCast(
         getGame().getObjMaster()
          .getBfObjectsForCoordinates(coordinates));

        Filter.filter(objects, targetType);
        if (allyOrEnemyOnly != null) {
            if (allyOrEnemyOnly) {
                FilterMaster.applyFilter(objects, FILTERS.ALLY, ref, false);
            } else {
                FilterMaster.applyFilter(objects, FILTERS.ENEMY, ref, false);
            }
        }
        if (notSelf) {
            FilterMaster.applyFilter(objects, FILTERS.NOT_SELF, ref, false);
        }
        if (targetType.equals(DC_TYPE.TERRAIN)) { // C_TYPE equals if contains() !
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
