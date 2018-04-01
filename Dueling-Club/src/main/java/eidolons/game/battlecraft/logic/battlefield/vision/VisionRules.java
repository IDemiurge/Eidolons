package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.mechanics.ConcealmentRule;
import eidolons.game.battlecraft.rules.mechanics.IlluminationRule;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.UNIT_TO_UNIT_VISION;

/**
 * Created by JustMe on 4/1/2018.
 */
public class VisionRules {
    UnitVisionMapper sightMapper;
    OutlineMapper outlineMapper;

    public OUTLINE_TYPE outline(Unit source, BattleFieldObject object) {
        if (sightMapper.get(source, object) == UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT) {
            if (ConcealmentRule.isConcealed(source, object)){
                return OUTLINE_TYPE.DEEPER_DARKNESS;
            }
            if (IlluminationRule.isConcealed(source, object)){
                return OUTLINE_TYPE.BLINDING_LIGHT;
            }
            return OUTLINE_TYPE.CLEAR;
        }
        if (detectMapper.get(source, object)) {
            return OUTLINE_TYPE.CLEAR;
        }
        return null;
    }
}
