package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.logic.dungeon.universal.*;
import eidolons.game.core.game.DC_Game;

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
    }

    protected LocationBuilder createBuilder() {
        return new LocationBuilder(this);
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

        return new LocationInitializer(this);
    }

    @Override
    public LocationInitializer getInitializer() {
        return (LocationInitializer) super.getInitializer();
    }
}
