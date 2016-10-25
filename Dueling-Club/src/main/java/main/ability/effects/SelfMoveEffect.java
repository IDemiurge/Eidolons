package main.ability.effects;

import main.ability.conditions.FacingCondition;
import main.ability.targeting.TemplateSelectiveTargeting;
import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.elements.conditions.Conditions;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.entity.obj.DC_UnitObj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.UNIT_DIRECTION;
import main.game.battlefield.DirectionMaster;
import main.game.battlefield.MovementManager.MOVE_MODIFIER;
import main.game.battlefield.MovementManager.MOVE_TEMPLATES;
import main.system.DC_ConditionMaster;
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
        DC_UnitObj obj = (DC_UnitObj) ref.getSourceObj();
        Coordinates c = null;
        c = getCoordinates();
        if (c == null) // if selective?
            return false;
        game.getMovementManager().move(obj, c, free, mod, ref);
        return true;
    }

    public Coordinates getCoordinates() {
        DC_UnitObj obj = (DC_UnitObj) ref.getSourceObj();
        Coordinates c = obj.getCoordinates();
        if (template != null) {
            // ++ variables
            c = game.getMovementManager().getTemplateMoveCoordinate(template, obj.getFacing(), obj,
                    ref);
        } else if (direction != null)
            c = c.getAdjacentCoordinate(DirectionMaster.getDirectionByFacing(obj.getFacing(),
                    direction));
        else {
            if (formula == null) {
                try {
                    return ref.getTargetObj().getCoordinates();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Conditions conditions = new Conditions();
            if (mod != MOVE_MODIFIER.FLYING) {
                // conditions.add(new VisibilityCondition(
                // UNIT_TO_UNIT_VISION.IN_SIGHT));
                if (mod != MOVE_MODIFIER.TELEPORT) {
                    conditions.add(DC_ConditionMaster.getClearShotFilterCondition());
                    conditions.add(new FacingCondition(FACING_SINGLE.IN_FRONT));

                }
            }
            if (!new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.CELL, conditions)
                    .select(ref))
                return null;
            c = ref.getTargetObj().getCoordinates();
        }
        return c;
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
