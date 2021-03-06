package eidolons.game.core.game;

import eidolons.game.battlecraft.logic.meta.tutorial.TutorialMetaMaster;
import eidolons.game.core.master.combat.CombatMaster;
import eidolons.game.core.master.combat.DummyCombatMaster;

/**
 * Created by JustMe on 5/13/2017.
 */
public class TestGame extends DC_Game {

    public TestGame(TutorialMetaMaster testMetaMaster) {
        super(true);
        metaMaster = testMetaMaster;
    }

    @Override
    public DummyCombatMaster getCombatMaster() {
        return (DummyCombatMaster) super.getCombatMaster();
    }

    @Override
    protected CombatMaster createCombatMaster() {
        return new DummyCombatMaster(this);
    }
}
