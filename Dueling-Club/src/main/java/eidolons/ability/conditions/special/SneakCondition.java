package eidolons.ability.conditions.special;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.mode.STD_MODES;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class SneakCondition extends MicroCondition {

    private KEYS key;

    public SneakCondition() {
        key = KEYS.TARGET;
    }

    public SneakCondition(Boolean match) {
        if (match) {
            key = KEYS.MATCH;
        } else {
            key = KEYS.TARGET;
        }
    }

    public SneakCondition(KEYS key) {
        this.key = key;
    }

    @Override
    public boolean check(Ref ref) {
        boolean result = checkSneak(ref);
        return result;
    }

    public boolean checkSneak(Ref ref) {
        if (!(ref.getObj(key) instanceof Unit)) {
            return false;
        }
        Unit attacked = (Unit) ref.getObj(key);
        if (attacked.isBfObj()) {
            return false;
        }
        if (attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.SNEAK_IMMUNE)) {
            return false;
        }

        if (attacked.checkStatus(STATUS.UNCONSCIOUS)) {
            return true;
        }
        if (attacked.checkStatus(STATUS.CHARMED)) {
            return true;
        }

        Unit attacker = (Unit) ref.getSourceObj();

        DC_ActiveObj action = (DC_ActiveObj) ref.getObj(KEYS.ACTIVE);

        // if (attacked.checkPassive(STANDARD_PASSIVES.VIGILANCE))
        // return false;

        // if (FacingMaster.getSingleFacing(attacked, attacker) ==
        // FACING_SINGLE.BEHIND) {
        // // if (!attacked.checkPassive(STANDARD_PASSIVES.CORVIDAE))
        // return true;
        // }
        VisionEnums.VISIBILITY_LEVEL v = attacker.getGame().getVisionMaster().getVisionController().getVisibilityLevelMapper().get(attacked, attacker);
        switch (v) {
            case CLEAR_SIGHT:
            case OUTLINE:
                return false;
            case VAGUE_OUTLINE:
            case CONCEALED:
            case BLOCKED:
            case UNSEEN:
                break;
        }
        if (!attacker.checkInSightForUnit(attacked)) {
            if (attacked.getMode().equals(STD_MODES.ALERT)) // TODO wake up?
            {
                if (!attacker.isSneaking())
                    return false;
            }
            // if (attacked.checkPassive(STANDARD_PASSIVES.VIGILANCE))
            // return false;

            if (!action.isRanged()) {
                if (attacker.getActivePlayerVisionStatus() == PLAYER_VISION.UNKNOWN
                 || !VisionHelper.checkVisible(attacker)) {
                    return true;
                } else { //TODO allow sneak in front for specialists
                    return FacingMaster.getSingleFacing(attacked, attacker) == UnitEnums.FACING_SINGLE.BEHIND;
                }
            }
        }
        return false;
    }

}
