package eidolons.ability.effects.oneshot.move;

import eidolons.ability.conditions.FacingCondition;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.system.DC_ConditionMaster;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.Conditions;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.bf.directions.UNIT_DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.MovementManager.MOVE_MODIFIER;
import main.game.bf.MovementManager.MOVE_TEMPLATES;
import main.system.math.Formula;

public class SelfMoveEffect extends MoveEffect {

    private UNIT_DIRECTION direction;
    private MOVE_MODIFIER mod;
    private MOVE_TEMPLATES template;

    public SelfMoveEffect() {
    }

    // TODO AE multi-list choosing for <...> Arrays!
    public SelfMoveEffect(MOVE_MODIFIER mods) {
        this.mod = mods;
    }

    public SelfMoveEffect(UNIT_DIRECTION direction, MOVE_MODIFIER mods, Formula range) {
        this.direction = direction;
    }

    public SelfMoveEffect(UNIT_DIRECTION direction, MOVE_MODIFIER mods) {
        this.direction = direction;
        this.mod = mods;
    }

    // special variables argument?
    public SelfMoveEffect(MOVE_TEMPLATES template, MOVE_MODIFIER mods) {
        this.template = template;
        this.mod = mods;
    }

    @Override
    public boolean applyThis() {
        BattleFieldObject obj = getObjToMove();
        origin = Coordinates.get(obj.getCoordinates().getX(), obj.getCoordinates().getY());
        destination = getCoordinates();
        if (destination == null) // if selective?
        {
            return false;
        }
        game.getMovementManager().move(obj, destination, free, mod, ref);
        return true;
    }

    protected FACING_DIRECTION getFacing() {
        return getObjToMove().getFacing();
    }
    @Override
    public Coordinates getCoordinates() {
        BattleFieldObject obj = getObjToMove();
        FACING_DIRECTION facing =  getFacing();
        if (template != null) {
            // ++ variables
            destination = game.getMovementManager().getTemplateMoveCoordinate(template, facing, obj,
             ref);
        } else if (direction != null) {
            if (origin == null) {
                origin = obj.getCoordinates();
            }
            destination = origin.getAdjacentCoordinate(DirectionMaster.getDirectionByFacing(facing,
             direction));
        } else {
            if (formula == null) {
                try {
                    return ref.getActive().getTargetObj().getCoordinates();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            Conditions conditions = new Conditions();
            if (mod != MOVE_MODIFIER.FLYING) {
                // conditions.add(new VisibilityCondition(
                // UNIT_TO_UNIT_VISION.IN_SIGHT));
                if (mod != MOVE_MODIFIER.TELEPORT) {
                    conditions.add(DC_ConditionMaster.getClearShotFilterCondition());
                    conditions.add(new FacingCondition(UnitEnums.FACING_SINGLE.IN_FRONT));

                }
            }
//            if (!new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.CELL, conditions)
//                    .select(ref)) {
//                return null;
//            }
            destination = ref.getTargetObj().getCoordinates();
        }
        return destination;
    }

    protected BattleFieldObject getObjToMove() {
        return (BattleFieldObject) ref.getSourceObj();
    }

    public UNIT_DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(UNIT_DIRECTION direction) {
        this.direction = direction;
    }

    public MOVE_MODIFIER getMod() {
        return mod;
    }

    public void setMod(MOVE_MODIFIER mod) {
        this.mod = mod;
    }

    public MOVE_TEMPLATES getTemplate() {
        return template;
    }

    public void setTemplate(MOVE_TEMPLATES template) {
        this.template = template;
    }

}
