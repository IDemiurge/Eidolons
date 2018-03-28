package main.game.battlecraft.logic.battle.mission;

import main.game.battlecraft.logic.battle.universal.Battle;

/**
 * Created by JustMe on 5/8/2017.
 */
public class MissionBattle extends Battle {
    Mission mission;

    public MissionBattle(MissionBattleMaster missionBattleMaster) {
        super();
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }
}
