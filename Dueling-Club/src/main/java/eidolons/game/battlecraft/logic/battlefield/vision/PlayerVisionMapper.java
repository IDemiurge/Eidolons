package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import main.content.enums.rules.VisionEnums.UNIT_TO_PLAYER_VISION;
import main.data.XLinkedMap;
import org.apache.poi.ss.formula.functions.T;

import java.util.Map;

/**
 * Created by JustMe on 3/30/2018.
 */
public class PlayerVisionMapper extends VisionDataMapper<UNIT_TO_PLAYER_VISION> {

    Map<DC_Player, Map<BattleFieldObject, T>> map = new XLinkedMap<>();

    @Override
    public String toString() {
        return "Player Map";
    }
}
