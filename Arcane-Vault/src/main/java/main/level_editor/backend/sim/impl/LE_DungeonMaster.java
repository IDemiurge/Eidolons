package main.level_editor.backend.sim.impl;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationSpawner;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.FacingAdjuster;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.core.game.DC_Game;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;

import java.util.HashMap;
import java.util.Map;

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
    protected FloorLoader createFloorLoader() {
        return new LE_FloorLoader(this);
    }

    @Override
    protected Spawner createSpawner() {
        return new LocationSpawner(this) {
            @Override
            public void spawn() {
//                UnitData data=new UnitData();
//                DC_Player player;
//                spawn(data, player, SPAWN_MODE.DUNGEON);
//                super.spawn();
//                spawn(new UnitData())
                main.system.auxiliary.log.LogMaster.log(1, "LE: Units present - \n " + game.getBfObjects());
                spawnDone();
            }

            @Override
            protected void spawnDone() {
                super.spawnDone();
            }
        };
    }

    @Override
    protected LocationBuilder createBuilder() {
        return new LE_DungeonBuilder(this);
    }

    @Override
    protected FacingAdjuster createFacingAdjuster() {
        return new FacingAdjuster(this) {
            @Override
            public FACING_DIRECTION getFacingForUnit(Coordinates c, String typeName) {
                return FACING_DIRECTION.SOUTH; //TODO
            }

            protected Map<Coordinates, FACING_DIRECTION> getUnitFacingMap() {
                return new HashMap<>(); //TODO
            }
        };
    }

    public boolean isModuleSizeBased() {
        return false;
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
