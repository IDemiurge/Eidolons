package main.game.battlecraft.logic.battle.arena;

import main.game.battlecraft.logic.battle.*;
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
    protected PlayerManager<ArenaBattle> createPlayerManager() {
        return null;
    }

    @Override
    protected ArenaBattle createBattle() {
        return null;
    }

    @Override
    protected BattleOutcomeManager createOutcomeManager() {
        return null;
    }

    @Override
    protected BattleConstructor createConstructor() {
        return null;
    }

    @Override
    protected BattleStatManager createStatManager() {
        return null;
    }
}
