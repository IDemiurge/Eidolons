package main.game.core.game;

import main.game.battlecraft.logic.battle.BattleMaster;
import main.game.battlecraft.logic.battle.arena.ArenaBattleMaster;
import main.game.battlecraft.logic.dungeon.DungeonMaster;
import main.game.battlecraft.logic.dungeon.arena.ArenaDungeonMaster;
import main.game.battlecraft.logic.meta.MetaGameMaster;

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

    @Override
    protected MetaGameMaster createMetaMaster() {
        return super.createMetaMaster();
    }
}
