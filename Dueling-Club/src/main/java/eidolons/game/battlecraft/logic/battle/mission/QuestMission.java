package eidolons.game.battlecraft.logic.battle.mission;

import eidolons.game.battlecraft.logic.battle.universal.Mission;

/**
 * Created by JustMe on 5/8/2017.
 */
public class QuestMission extends Mission {
    eidolons.game.battlecraft.logic.battle.mission.Mission mission;

    public QuestMission(MissionBattleMaster missionBattleMaster) {
        super();
    }

    public eidolons.game.battlecraft.logic.battle.mission.Mission getMission() {
        return mission;
    }

    public void setMission(eidolons.game.battlecraft.logic.battle.mission.Mission mission) {
        this.mission = mission;
    }
}
