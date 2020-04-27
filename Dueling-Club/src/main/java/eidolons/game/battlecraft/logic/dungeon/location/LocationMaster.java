package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.logic.dungeon.location.layer.LayerManager;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.module.ModuleMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.*;
import eidolons.game.core.game.DC_Game;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 5/8/2017.
 */
public class LocationMaster extends DungeonMaster {
    protected ModuleMaster moduleMaster;

    public LocationMaster(DC_Game game) {
        super(game);
        moduleMaster = new ModuleMaster(this);
    }

    @Override
    public void init() {
        super.init();
        if (getFloorWrapper().getMainEntrance() != null) {
            getGame().getObjMaster().tryAddUnit(getFloorWrapper().getMainEntrance());
        }
        if (getFloorWrapper().getMainExit() != null) {
            getGame().getObjMaster().tryAddUnit(getFloorWrapper().getMainExit());
        }
    }

    @Override
    public Module getModule() {
        return moduleMaster.getCurrent();
    }

    public ModuleMaster getModuleMaster() {
        return moduleMaster;
    }

    @Override
    protected LayerManager createLayerManager() {
        return new LayerManager(this);
    }

    protected LocationBuilder createBuilder() {
        return new LocationBuilder(this);
    }

    private boolean isRngDungeon() {
        if (CoreEngine.isjUnit())
            return false;
        return getGame().getMetaMaster().isRngDungeon();
    }

    @Override
    protected FacingAdjuster createFacingAdjuster() {
        return new FacingAdjuster(this);
    }

    @Override
    protected Positioner createPositioner() {
        return new LocationPositioner(this);
    }

    @Override
    protected Spawner createSpawner() {
        return new LocationSpawner(this);
    }

    @Override
    protected DungeonInitializer createInitializer() {
        return new LocationInitializer(this);
    }

    @Override
    public LocationInitializer getInitializer() {
        return (LocationInitializer) super.getInitializer();
    }


}
