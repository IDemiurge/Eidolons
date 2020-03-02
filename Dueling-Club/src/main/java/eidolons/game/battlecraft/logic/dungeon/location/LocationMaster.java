package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.logic.dungeon.location.layer.LayerManager;
import eidolons.game.battlecraft.logic.dungeon.universal.*;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.netherflame.boss.cinematic.BossLocationInitializer;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 5/8/2017.
 */
public class LocationMaster extends DungeonMaster<Location> {
    public LocationMaster(DC_Game game) {
        super(game);
    }

    @Override
    public void init() {
        super.init();
        if (getDungeonWrapper().getMainEntrance() != null) {
            getGame().getMaster().tryAddUnit(getDungeonWrapper().getMainEntrance());
        }
        if (getDungeonWrapper().getMainExit() != null) {
            getGame().getMaster().tryAddUnit(getDungeonWrapper().getMainExit());
        }
    }

    @Override
    protected LayerManager createLayerManager() {
        return  new LayerManager(this);
    }

    protected LocationBuilder createBuilder() {
        return isRngDungeon() ? new RngLocationBuilder(this):
        new LocationBuilder(this);
    }

    private boolean isRngDungeon() {
        if (CoreEngine.isjUnit())
            return false;
        return getGame().getMetaMaster().isRngDungeon();
    }

    @Override
    protected FacingAdjuster<Location> createFacingAdjuster() {
        return new FacingAdjuster(this);
    }

    @Override
    protected Positioner<Location> createPositioner() {
        return new LocationPositioner(this);
    }

    @Override
    protected Spawner<Location> createSpawner() {
        return new LocationSpawner(this);
    }

    @Override
    protected DungeonInitializer<Location> createInitializer() {
        if (getGame().isBossFight()) {
            return new BossLocationInitializer(this);
        }
        return new LocationInitializer(this);
    }

    @Override
    public LocationInitializer getInitializer() {
        return (LocationInitializer) super.getInitializer();
    }


}
