package main.game.battlecraft.logic.battle.test;

import main.game.battlecraft.logic.battle.universal.*;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestBattleMaster extends BattleMaster<TestBattle> {

    public TestBattleMaster(DC_Game game) {
        super(game);
    }

    @Override
    protected TestBattle createBattle() {
        return null;
    }

    @Override
    protected PlayerManager<TestBattle> createPlayerManager() {
        return new PlayerManager<>(this);
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

    @Override
    protected BattleOptionManager createOptionManager() {
        return null;
    }
}
