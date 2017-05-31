package main.game.battlecraft.logic.battle.mission;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.meta.scenario.hq.Place;

/**
 * Created by JustMe on 5/17/2017.
 */
public class Mission extends LightweightEntity {
    Place place;
    public Mission(ObjType type, BattleMaster<MissionBattle> master) {
        super(type);
    }

    public Place getPlace() {
        if (place==null ){
            place = new Place(DataManager.getType(getProperty(PROPS.MISSION_PLACE),
             DC_TYPE.PLACES)) ;
        }
        return place;
    }
}
