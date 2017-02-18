package main.ability.conditions;

import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref.KEYS;
import main.entity.item.DC_HeroSlotItem;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.entity.obj.attach.DC_HeroAttachedObj;
import main.entity.obj.unit.DC_UnitModel;
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
        if (!(ref.getSourceObj() instanceof DC_UnitModel)) {
            return false;
        }
        BattleFieldObject obj1 = (BattleFieldObject) ref.getSourceObj();
        Obj obj2;

        if (!(ref.getObj(KEYS.MATCH) instanceof BfObj)) {
            if (!(ref.getObj(KEYS.MATCH) instanceof DC_HeroSlotItem)) {
                return false;
            }
            obj2 = (Obj) ((DC_HeroAttachedObj) ref.getObj(KEYS.MATCH)).getOwnerObj();

        } else {
            obj2 = (Obj) ref.getObj(KEYS.MATCH);
        }

        boolean result = false;

        if (getTemplate() != null) {
            FACING_SINGLE facing = FacingMaster.getSingleFacing(obj1, (BfObj) obj2);
            result = Arrays.asList(templates).contains(facing);
            if (facing == UnitEnums.FACING_SINGLE.TO_THE_SIDE) {
                if (result) {
                    if (left_right == null) {
                        left_right = obj1.checkBool(GenericEnums.STD_BOOLS.LEFT_RIGHT_REACH);
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
