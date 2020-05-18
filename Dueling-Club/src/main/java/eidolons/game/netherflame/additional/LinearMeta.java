package eidolons.game.netherflame.additional;

import eidolons.game.battlecraft.logic.meta.universal.MetaGame;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.netherflame.main.NF_MetaMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;

/**
 * this is the thing to be constructed from save data?
 *
 */
public class LinearMeta extends MetaGame {

    private int missionIndex;
    private IGG_Mission mission;
    private ObjType missionType;
    private int actIndex;

    public LinearMeta(MetaGameMaster master) {
        super(master);
    }

    @Override
    public NF_MetaMaster getMaster() {
        return (NF_MetaMaster) super.getMaster();
    }


    public void setMissionType(ObjType missionType) {
        this.missionType = missionType;
    }

    public ObjType getMissionType() {
        return missionType;
    }

    public void setMission(IGG_Demo.IGG_MISSION mission) {
        this.mission = new IGG_Mission(mission);
        missionIndex = mission.getMissionIndex();
        actIndex = mission.getAct();
        setMissionType(DataManager.getType(mission.getMissionName(), DC_TYPE.FLOORS));
    }

    public int getMissionIndex() {
        return missionIndex;
    }

    public int getActIndex() {
        return actIndex;
    }

    public IGG_Mission getMission() {
        return mission;
    }

    public boolean isFinalLevel() {
        return false;
    }
}
