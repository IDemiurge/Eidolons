package eidolons.game.battlecraft.logic.battle.mission;

import eidolons.game.battlecraft.logic.battle.universal.PlayerManager;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;

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
