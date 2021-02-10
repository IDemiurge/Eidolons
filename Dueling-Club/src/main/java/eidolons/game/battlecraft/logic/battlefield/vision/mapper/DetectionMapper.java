package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;

/**
 * Created by JustMe on 4/1/2018.
 */
public class DetectionMapper extends PlayerMapper<Boolean> {

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Boolean get(DC_Player source, DC_Obj object) {
        Boolean result = getMap(source).get(object);
        if (result==null )
            return false;
        return result;
    }
}
