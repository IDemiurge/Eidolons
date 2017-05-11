package main.game.battlecraft.logic.battle.mission;

import main.game.battlecraft.logic.battle.Battle;
import main.game.battlecraft.logic.battle.BattleConstructor;
import main.game.battlecraft.logic.battle.BattleMaster;

/**
 * Created by JustMe on 5/7/2017.
 */
public class MissionConstructor extends BattleConstructor {
    public MissionConstructor(BattleMaster<? extends Battle> master) {
        super(master);
    }
}
