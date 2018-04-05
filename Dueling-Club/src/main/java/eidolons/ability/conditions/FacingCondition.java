package eidolons.ability.conditions;

import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.attach.DC_HeroAttachedObj;
import eidolons.entity.obj.unit.DC_UnitModel;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BfObj;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;

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
    public boolean check(Ref ref) {
        if (!(ref.getSourceObj() instanceof DC_UnitModel)) {
            return false;
        }
        BattleFieldObject obj1 = (BattleFieldObject) ref.getSourceObj();
        DC_Obj obj2;

        if (!(ref.getObj(KEYS.MATCH) instanceof BfObj)) {
            if (!(ref.getObj(KEYS.MATCH) instanceof DC_HeroSlotItem)) {
                return false;
            }
            obj2 = ((DC_HeroAttachedObj) ref.getObj(KEYS.MATCH)).getOwnerObj();

        } else {
            obj2 = (DC_Obj) ref.getObj(KEYS.MATCH);
        }

        boolean result = false;

        if (getTemplate() != null) {
            Coordinates c = obj2.getCoordinates();
            if (obj2.isOverlaying()) if (obj2 instanceof BattleFieldObject) {
                DIRECTION d = ((BattleFieldObject) obj2).getDirection();
                if (d != null) {
                    c = c.getAdjacentCoordinate(d.rotate180(), 2);
                }
//the coordinate to which unit must be facing in order to face the overlaying obj on the other side
            }
            if (obj1 == null)
                return false;
            if (c == null)
                return false;
            FACING_SINGLE facing = FacingMaster.getSingleFacing(obj1.getFacing(),
             obj1.getCoordinates(), c);
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
                            return left_right;
                        }
                        return !left_right;
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
