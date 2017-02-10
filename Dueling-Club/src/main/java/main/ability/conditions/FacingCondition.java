package main.ability.conditions;

import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.content.CONTENT_CONSTS.STD_BOOLS;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref.KEYS;
import main.entity.obj.*;
import main.game.battlefield.DirectionMaster;
import main.game.battlefield.FacingMaster;

import java.util.Arrays;

public class FacingCondition extends ConditionImpl {

    private FACING_SINGLE[] templates;
    private Boolean left_right;

    public FacingCondition(Boolean left_right, FACING_SINGLE... template) {
        this.left_right = left_right;
        setTemplates(template);
    }

    public FacingCondition(FACING_SINGLE... templates) {
        this(null, templates);
    }

    @Override
    public boolean check() {
        if (!(ref.getSourceObj() instanceof DC_UnitObj)) {
            return false;
        }
        DC_UnitObj obj1 = (DC_UnitObj) ref.getSourceObj();
        Obj obj2;

        if (!(ref.getObj(KEYS.MATCH) instanceof BattlefieldObj)) {
            if (!(ref.getObj(KEYS.MATCH) instanceof DC_HeroSlotItem)) {
                return false;
            }
            obj2 = (Obj) ((DC_HeroAttachedObj) ref.getObj(KEYS.MATCH)).getOwnerObj();

        } else {
            obj2 = (Obj) ref.getObj(KEYS.MATCH);
        }

        boolean result = false;

        if (getTemplate() != null) {
            FACING_SINGLE facing = FacingMaster.getSingleFacing(obj1, (BattlefieldObj) obj2);
            result = Arrays.asList(templates).contains(facing);
            if (facing == FACING_SINGLE.TO_THE_SIDE) {
                if (result) {
                    if (left_right == null) {
                        left_right = obj1.checkBool(STD_BOOLS.LEFT_RIGHT_REACH);
                    }
                    if (left_right) {
                        int degrees = obj1.getFacing().getDirection().getDegrees();
                        int degrees2 = DirectionMaster.getRelativeDirection(obj1, obj2)
                                .getDegrees();
                        boolean left = degrees > degrees2;
                        if (left) {
                            return left_right ? true : false;
                        }
                        return !left_right ? true : false;
                    }
                }
            }
        }

        return result;
    }

    public FACING_SINGLE[] getTemplate() {
        return templates;
    }

    public void setTemplates(FACING_SINGLE[] template) {
        this.templates = template;
    }

}
