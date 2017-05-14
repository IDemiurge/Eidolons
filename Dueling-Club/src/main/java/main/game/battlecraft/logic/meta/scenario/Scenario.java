package main.game.battlecraft.logic.meta.scenario;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.DC_Player;
import main.game.core.game.DC_Game;
import main.game.battlecraft.logic.battle.DC_Player;
import main.game.battlecraft.logic.meta.scenario.ScenarioMaster.SCENARIO_MODES;
import main.game.core.game.DC_Game;

import java.util.List;

public class Scenario extends Entity {

    public Scenario(ObjType type) {
        super(type, DC_Player.NEUTRAL, DC_Game.game, new Ref());

    }

    public void init() {
        toBase();
    }


    public ObjType getPartyType() {
      return   DataManager.getType(
        getProperty(PROPS.SCENARIO_PARTY) ,
         DC_TYPE.PARTY);
    }

    public void next() {

    }
}
