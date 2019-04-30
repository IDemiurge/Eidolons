package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.content.DC_TYPE;
import main.data.DataManager;

public class IGG_MetaDataManager extends MetaDataManager<IGG_Meta> {
    public IGG_MetaDataManager(MetaGameMaster master) {
        super(master);
    }

    public void initData() {
        //path?
        String missionName = getPartyManager().getParty().getProperty(PROPS.PARTY_MISSION);
        IGG_Demo.IGG_MISSION mission;
        if (missionName.isEmpty()) {
            mission  = IGG_Demo.IGG_MISSION.ACT_I_MISSION_I ;
        } else {
            mission  = IGG_Demo.getMissionByName(missionName);
        }
        getMetaGame().setMission(mission);
    }

    @Override
    public String getMissionName() {
        return getMetaGame().getMissionType().getName();
    }

    @Override
    public String getMissionPath() {
        return getMetaGame().getMissionType().getProperty(PROPS.MISSION_FILE_PATH);
    }
}
