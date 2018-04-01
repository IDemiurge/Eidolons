package eidolons.game.core.game;

import eidolons.game.battlecraft.logic.battle.arena.ArenaBattleMaster;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.dungeon.arena.ArenaDungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;

/**
 * Created by JustMe on 5/10/2017.
 */
public class ArenaGame extends DC_Game {

    @Override
    protected BattleMaster createBattleMaster() {
        return new ArenaBattleMaster(this);
    }

    @Override
    protected DungeonMaster createDungeonMaster() {
        return new ArenaDungeonMaster(this);
    }


}
