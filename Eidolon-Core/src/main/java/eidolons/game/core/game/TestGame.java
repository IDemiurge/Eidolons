package eidolons.game.core.game;

import eidolons.game.battlecraft.logic.battlefield.vision.VisionMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;
import eidolons.game.core.master.combat.CombatMaster;
import eidolons.system.test.TestMasterContent;

public class TestGame extends DC_Game {
    public TestGame() {
        super(false);
    }

    @Override
    protected CombatMaster createCombatMaster() {
        return super.createCombatMaster();
    }

    @Override
    protected TestMasterContent createTestMaster() {
        return super.createTestMaster();
    }

    @Override
    protected VisionMaster createVisionMaster() {
        return super.createVisionMaster();
    }

    @Override
    protected MissionMaster createBattleMaster() {
        return super.createBattleMaster();
    }

    @Override
    protected DungeonMaster createDungeonMaster() {
        return super.createDungeonMaster();
    }
}
