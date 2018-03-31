package eidolons.game.battlecraft.logic.battle.test;

import eidolons.game.battlecraft.logic.battle.universal.*;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStatManager;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestBattleMaster extends BattleMaster<TestBattle> {


    public TestBattleMaster(DC_Game game) {
        super(game);
    }

    @Override
    protected TestBattle createBattle() {
        return new TestBattle();
    }

    @Override
    protected PlayerManager<TestBattle> createPlayerManager() {
        return new PlayerManager<>(this);
    }

    @Override
    protected BattleOutcomeManager createOutcomeManager() {
        return new BattleOutcomeManager(this);
    }

    @Override
    protected BattleConstructor createConstructor() {
        return new TestBattleConstructor(this);
    }

    @Override
    protected BattleStatManager createStatManager() {
        return new BattleStatManager(this);
    }

    @Override
    protected BattleOptionManager createOptionManager() {
        return new BattleOptionManager(this);
    }
}
