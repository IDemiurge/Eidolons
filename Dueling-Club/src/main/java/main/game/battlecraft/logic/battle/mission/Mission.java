package main.game.battlecraft.logic.battle.mission;

import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.universal.BattleMaster;

/**
 * Created by JustMe on 5/17/2017.
 */
public class Mission extends LightweightEntity {

    public Mission(ObjType type, BattleMaster<MissionBattle> master) {
        super(type);
    }


}
