package eidolons.ability.effects.oneshot.dungeon;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.obj.BattleFieldObject;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums;
import main.content.values.properties.G_PROPS;
import main.game.bf.Coordinates;

public class RevealEffect extends DC_Effect {
    @Override
    public boolean applyThis() {
        Coordinates c = getRef().getTargetObj().getCoordinates();
        for (BattleFieldObject object : getGame().getObjectsOnCoordinateNoOverlaying(c)) {
                object.setDetectedByPlayer(true);
                object.setDetectedByPlayer(true);
            object.setVisibilityLevel(VisionEnums.VISIBILITY_LEVEL.CLEAR_SIGHT);
            object.setVisibilityLevelForPlayer(VisionEnums.VISIBILITY_LEVEL.CLEAR_SIGHT);
            object.setUnitVisionStatus(VisionEnums.UNIT_VISION.IN_PLAIN_SIGHT);
            object.setPlayerVisionStatus(VisionEnums.PLAYER_VISION.DETECTED);
            object.addProperty(G_PROPS.STATUS, UnitEnums.STATUS.REVEALED.toString());
        }

        return true;
    }
}
