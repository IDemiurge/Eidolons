package main.game.core.game;

import main.game.core.master.combat.CombatMaster;
import main.game.core.master.combat.DummyCombatMaster;

/**
 * Created by JustMe on 5/13/2017.
 */
public class TestGame extends DC_Game {
    @Override
    public DummyCombatMaster getCombatMaster() {
        return (DummyCombatMaster) super.getCombatMaster();
    }

    @Override
    protected CombatMaster createCombatMaster() {
        return new DummyCombatMaster(this);
    }
}
