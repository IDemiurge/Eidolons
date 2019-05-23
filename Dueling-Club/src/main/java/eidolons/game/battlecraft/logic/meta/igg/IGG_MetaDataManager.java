package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;

public class IGG_MetaDataManager extends MetaDataManager<IGG_Meta> {
    private String scenarioName;

    public IGG_MetaDataManager(MetaGameMaster master) {
        super(master);
    }

    @Override
    public String getData() {
        return scenarioName;
    }

    public String nextMission() {
        IGG_Demo.IGG_MISSION next = getMetaGame().getMission().getNext();
        if (next==null) {
            return null;
        }
        getPartyManager().getParty().setProperty(PROPS.PARTY_MISSION, next.getMissionName(), true);
//        next.missionIndex;
//        getMaster().getSaveMaster().autoSave();
        scenarioName=  next.getScenarioName();
        return next.getMissionName();
    }


    public void initData() {
        //path?
        String missionName = getPartyManager().getParty().getProperty(PROPS.PARTY_MISSION);
        IGG_Demo.IGG_MISSION mission;
        if (missionName.isEmpty())
            missionName = getMaster().getData(); //starting mission
        mission = IGG_Demo.getMissionByName(missionName);
        getGame().setBossFight(mission.isBossFight());
        Eidolons.BOSS_FIGHT = (mission.isBossFight());
        Eidolons.TUTORIAL = (mission.isTutorial());
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
