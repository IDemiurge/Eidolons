package main.level_editor.sim.impl;

import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.location.LocationInitializer;
import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationSpawner;
import eidolons.game.battlecraft.logic.dungeon.universal.*;
import eidolons.game.core.game.DC_Game;
import org.mockito.Mockito;

public class LE_DungeonMaster extends LocationMaster {
    public LE_DungeonMaster(DC_Game game) {
        super(game);
    }

    protected boolean isPuzzlesOn() {
        return false;
    }
//    @Override
//    protected FacingAdjuster createFacingAdjuster() {
//        return Mockito.mock(FacingAdjuster.class);
//    }
//
//    @Override
//    protected Positioner createPositioner() {
//        return Mockito.mock(Positioner.class);
//    }

    @Override
    protected Spawner createSpawner() {
        return new LocationSpawner(this){
            @Override
            public void spawn() {
//                UnitData data=new UnitData();
//                DC_Player player;
//                spawn(data, player, SPAWN_MODE.DUNGEON);
//                super.spawn();
//                spawn(new UnitData())
                main.system.auxiliary.log.LogMaster.log(1,"LE: Units present - \n " +game.getBfObjects());
                spawnDone();
            }

            @Override
            protected void spawnDone() {
                super.spawnDone();
            }
        };
    }

    @Override
    public String getDefaultEntranceType() {
        return "Dark Portal";
    }

    @Override
    public String getDefaultExitType() {
        return "Dark Portal";
    }

    @Override
    public LE_DungeonInitializer getInitializer() {
        return (LE_DungeonInitializer) super.getInitializer();
    }

    @Override
    protected DungeonInitializer createInitializer() {
        return new LE_DungeonInitializer(this);
    }
}
