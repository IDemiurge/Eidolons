package main.game.battlecraft.logic.battle.arena;

import main.game.battlecraft.logic.battle.universal.BattleConstructor;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.universal.BattleOutcomeManager;
import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/7/2017.
 */
public class ArenaBattleMaster extends BattleMaster<ArenaBattle> {

    private WaveAssembler waveAssembler;

    public ArenaBattleMaster(DC_Game game) {
        super(game);
    }


    @Override
    protected ArenaOptionsMaster createOptionManager() {
        return new ArenaOptionsMaster(this);
    }

    @Override
    public void startGame() {
        waveAssembler = new WaveAssembler(this);
        super.startGame();
    }

    public WaveAssembler getWaveAssembler() {
        return waveAssembler;
    }


    @Override
    protected ArenaBattle createBattle() {
        return new ArenaBattle();
    }

    @Override
    protected BattleOutcomeManager createOutcomeManager() {
        return new ArenaOutcomeManager(this);
    }

    @Override
    protected BattleConstructor createConstructor() {
        return new ArenaBattleConstructor(this);
    }

    @Override
    protected BattleStatManager createStatManager() {
        return new ArenaStatManager(this);
    }
}
