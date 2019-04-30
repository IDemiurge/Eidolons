package eidolons.game.battlecraft.logic.meta.igg;

public class IGG_Mission {
    IGG_Demo.IGG_MISSION mission;
    private String name;

    public IGG_Mission(IGG_Demo.IGG_MISSION mission) {
        this.mission = mission;
        name = mission.getMissionName();
//        name = mission.getMissionIndex();
//        name = mission.getAct();
    }

    public boolean isTown() {
        if (mission.getMissionIndex() == 3) {
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }


    //3 levels
    // only final exit counts
    //

}
