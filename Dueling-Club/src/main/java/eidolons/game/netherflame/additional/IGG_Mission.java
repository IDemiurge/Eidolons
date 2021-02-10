package eidolons.game.netherflame.additional;

public class IGG_Mission {
    IGG_Demo.IGG_MISSION mission;
    private String name;

    public IGG_Mission(IGG_Demo.IGG_MISSION mission) {
        this.mission = mission;
        name = mission.getMissionName();
//        name = mission.getMissionIndex();
//        name = mission.getAct();
    }

    public int getAct() {
        return mission.getAct();
    }

    public IGG_Demo.IGG_MISSION getNext() {
        return mission.getNext();
    }

    public int getMissionIndex() {
        return mission.getMissionIndex();
    }

    public String getMissionName() {
        return mission.getMissionName();
    }
    public String getXmlLevelName() {
        return mission.getXmlLevelName();
    }

    public boolean isBossFight() {
        return mission.isBossFight();
    }

    public boolean isTown() {
//        if (mission.getMissionIndex() == 3) { TODO change of plans
//            return true;
//        }
        return false;
    }

    public String getName() {
        return name;
    }


    //3 levels
    // only final exit counts
    //

}
