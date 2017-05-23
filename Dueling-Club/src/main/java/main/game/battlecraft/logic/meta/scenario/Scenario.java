package main.game.battlecraft.logic.meta.scenario;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;

public class Scenario extends LightweightEntity {

    public Scenario(ObjType type) {
        super(type);

    }

//    public void init() {
//        toBase();
//    }


    public ObjType getPartyType() {
      return   DataManager.getType(
        getProperty(PROPS.SCENARIO_PARTY) ,
         DC_TYPE.PARTY);
    }

    public void next() {

    }
}
