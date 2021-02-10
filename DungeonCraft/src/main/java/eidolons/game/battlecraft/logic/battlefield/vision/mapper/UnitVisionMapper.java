package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import main.content.enums.rules.VisionEnums.UNIT_VISION;

/**
 * Created by JustMe on 3/30/2018.
 */
public class UnitVisionMapper extends ObjVisionMapper<UNIT_VISION> {


    @Override
    public void set(BattleFieldObject source, DC_Obj object, UNIT_VISION unit_vision) {
        if (object.isVisibilityFrozen()) {
            return;
        }
        super.set(source, object, unit_vision);
    }

    @Override
    protected UNIT_VISION getNullEquivalent() {
        return UNIT_VISION.BEYOND_SIGHT;
    }

    @Override
    protected boolean isClearRequired() {
        return false;
    }

    @Override
    public String toString() {
        return "Unit Vision Map";
    }
}
