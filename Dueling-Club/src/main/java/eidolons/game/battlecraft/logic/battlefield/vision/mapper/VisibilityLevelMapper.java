package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;

/**
 * Created by JustMe on 3/30/2018.
 */
public class VisibilityLevelMapper extends ObjVisionMapper<VISIBILITY_LEVEL> {

    @Override
    public void set(BattleFieldObject source, DC_Obj object, VISIBILITY_LEVEL visibility_level) {
        if (object.isVisibilityFrozen()) {
            return;
        }
        super.set(source, object, visibility_level);
    }

    @Override
    protected VISIBILITY_LEVEL getNullEquivalent() {
        return VISIBILITY_LEVEL.UNSEEN;
    }
    @Override
    protected boolean isClearRequired() {
        return true;
    }
    @Override
    public String toString() {
        return super.toString();
    }
}
