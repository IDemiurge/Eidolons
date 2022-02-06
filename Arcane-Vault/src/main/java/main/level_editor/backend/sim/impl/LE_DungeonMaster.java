package main.level_editor.backend.sim.impl;

import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationSpawner;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.core.game.DC_Game;

public class LE_DungeonMaster extends LocationMaster {
    public LE_DungeonMaster(DC_Game game) {
        super(game);
    }

    protected boolean isPuzzlesOn() {
        return false;
    }

    @Override
    public void loadingDone() {
        resetColorMap(game.getCoordinates());
    }

    @Override
    protected FloorLoader createFloorLoader() {
        return new LE_FloorLoader(this);
    }

    @Override
    protected DC_ObjInitializer createObjInitializer() {
        return new LE_ObjInitializer(this);
    }

    @Override
    protected Spawner createSpawner() {
        return new LocationSpawner(this) {
            @Override
            public void spawn() {
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

    public boolean isModuleSizeBased() {
        return false;
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
