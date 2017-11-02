package main.ability.conditions.special;

import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums;
import main.content.mode.STD_MODES;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;

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
        if (result){
            main.system.auxiliary.log.LogMaster.log(1,"ya sneaky, ser!" );
        }
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

        if (attacked.checkStatus(UnitEnums.STATUS.IMMOBILE)) {
            return true;
        }
        if (attacked.checkStatus(UnitEnums.STATUS.CHARMED)) {
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

        if (!attacker.checkInSightForUnit(attacked)) {
            if (attacked.getMode().equals(STD_MODES.ALERT)) // TODO wake up?
            {
                return false;
            }
            // if (attacked.checkPassive(STANDARD_PASSIVES.VIGILANCE))
            // return false;

            if (!action.isRanged()) {
                if (attacker.getActivePlayerVisionStatus() == VisionEnums.UNIT_TO_PLAYER_VISION.UNKNOWN
                        || !VisionManager.checkVisible(attacker)) {
                    return true;
                } else { //TODO allow sneak in front for specialists
                    return FacingMaster.getSingleFacing(attacked, attacker) == UnitEnums.FACING_SINGLE.BEHIND;
                }
            }
        }
        return false;
    }

}
