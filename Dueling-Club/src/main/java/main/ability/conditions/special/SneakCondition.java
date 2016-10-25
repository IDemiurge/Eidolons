package main.ability.conditions.special;

import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.CONTENT_CONSTS.UNIT_TO_PLAYER_VISION;
import main.content.enums.STD_MODES;
import main.elements.conditions.MicroCondition;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.FacingMaster;
import main.game.battlefield.VisionManager;

public class SneakCondition extends MicroCondition {

    private KEYS key;

    public SneakCondition() {
        key = KEYS.TARGET;
    }

    public SneakCondition(Boolean match) {
        if (match)
            key = KEYS.MATCH;
        else
            key = KEYS.TARGET;
    }

    public SneakCondition(KEYS key) {
        this.key = key;
    }

    @Override
    public boolean check() {
        if (!(ref.getObj(key) instanceof DC_HeroObj))
            return false;
        DC_HeroObj attacked = (DC_HeroObj) ref.getObj(key);
        if (attacked.isBfObj())
            return false;
        if (attacked.checkPassive(STANDARD_PASSIVES.SNEAK_IMMUNE))
            return false;

        if (attacked.checkStatus(STATUS.IMMOBILE))
            return true;
        if (attacked.checkStatus(STATUS.CHARMED))
            return true;

        DC_HeroObj attacker = (DC_HeroObj) ref.getSourceObj();
        DC_ActiveObj action = (DC_ActiveObj) ref.getObj(KEYS.ACTIVE);

        // if (attacked.checkPassive(STANDARD_PASSIVES.VIGILANCE))
        // return false;

        // if (FacingMaster.getSingleFacing(attacked, attacker) ==
        // FACING_SINGLE.BEHIND) {
        // // if (!attacked.checkPassive(STANDARD_PASSIVES.CORVIDAE))
        // return true;
        // }

        if (!attacker.checkInSightForUnit(attacked)) {
            if (attacked.getMode().equals(STD_MODES.ALERT)) // TODO wake up?
                return false;
            // if (attacked.checkPassive(STANDARD_PASSIVES.VIGILANCE))
            // return false;

            if (!action.isRanged())
                if (attacker.getActivePlayerVisionStatus() == UNIT_TO_PLAYER_VISION.UNKNOWN
                        || !VisionManager.checkVisible(attacker))
                    return true;
                else
                    return FacingMaster.getSingleFacing(attacker, attacked) == FACING_SINGLE.BEHIND;
        }
        return false;
    }

}
