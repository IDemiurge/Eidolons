package main.game.battlecraft.logic.battle.mission;

import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.battlecraft.logic.battle.universal.PlayerManager;

/**
 * Created by JustMe on 11/20/2017.
 */
public class MissionPlayerManager extends PlayerManager<MissionBattle> {
    public MissionPlayerManager(BattleMaster<MissionBattle> master) {
        super(master);
    }

    @Override
    protected void initUnitData(DC_Player player, int i) {

    }
}
