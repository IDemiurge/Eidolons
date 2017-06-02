package main.game.battlecraft.logic.battle.mission;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.meta.scenario.Scenario;
import main.game.battlecraft.logic.meta.scenario.hq.Place;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/17/2017.
 */
public class Mission extends LightweightEntity {
    Scenario scenario;
    Place place;
    public Mission(ObjType type, Scenario scenario) {
        super(type);
       this.scenario=(scenario);
    }

    public Place getPlace() {
        if (place==null ){
            place = new Place(DataManager.getType(getProperty(PROPS.MISSION_PLACE),
             DC_TYPE.PLACES)) ;
        }
        return place;
    }

    public String getMissionResourceFolderPath() {
        return StringMaster.buildPath(PathFinder.getScenariosPath()  ,
          getScenario().getName(),
          getName());

    }

    public Scenario getScenario() {
        return scenario;
    }


    @Override
    public String getToolTip() {
        return "Travel to " + getPlace().getName();
    }
}
