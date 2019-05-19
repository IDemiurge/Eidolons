package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import main.content.DC_TYPE;
import main.data.DataManager;

public class IGG_MetaDataManager extends MetaDataManager<IGG_Meta> {
    private final String startingAct;

    public IGG_MetaDataManager(MetaGameMaster master) {
        super(master);
        startingAct = master.getData();
    }

    public void initData() {
        //path?
        String missionName = getPartyManager().getParty().getProperty(PROPS.PARTY_MISSION);
        IGG_Demo.IGG_MISSION mission;
        if (missionName.isEmpty())
            missionName = startingAct;
        mission = IGG_Demo.getMissionByName(missionName);
        getGame().setBossFight(mission.isBossFight());
        Eidolons.BOSS_FIGHT = (mission.isBossFight());
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
