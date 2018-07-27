package eidolons.game.battlecraft.logic.battle.mission;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.scenario.Scenario;
import eidolons.game.battlecraft.logic.meta.scenario.hq.MissionLocation;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.system.PathUtils;

/**
 * Created by JustMe on 5/17/2017.
 */
public class Mission extends LightweightEntity {
    Scenario scenario;
    MissionLocation missionLocation;

    public Mission(ObjType type, Scenario scenario) {
        super(type);
        this.scenario = (scenario);
    }

    public MissionLocation getMissionLocation() {
        if (missionLocation == null) {
            missionLocation = new MissionLocation(DataManager.getType(getProperty(PROPS.MISSION_PLACE),
             DC_TYPE.PLACES));
        }
        return missionLocation;
    }

    public String getMissionResourceFolderPath() {
        return PathUtils.buildPath(PathFinder.getScenariosPath(),
         getScenario().getName(),
         getName());

    }

    public Scenario getScenario() {
        return scenario;
    }


    @Override
    public String getToolTip() {
        return "Travel to " + getMissionLocation().getName();
    }
}
