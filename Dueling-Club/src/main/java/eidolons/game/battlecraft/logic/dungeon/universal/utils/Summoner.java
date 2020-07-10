package eidolons.game.battlecraft.logic.dungeon.universal.utils;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.core.game.DC_Game;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

public class Summoner {


    public static BattleFieldObject bfObj(Coordinates c, String name) {
        ObjType type = DataManager.getType(name, DC_TYPE.BF_OBJ);
        return DC_Game.game.createObject(type, c, DC_Player.NEUTRAL);
    }
}
