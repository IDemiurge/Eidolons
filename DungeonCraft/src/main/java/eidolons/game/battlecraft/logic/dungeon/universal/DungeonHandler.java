package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.location.TransitHandler;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.location.struct.PlaceholderResolver;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructMaster;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureBuilder;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.module.ModuleLoader;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.mission.universal.*;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager;
import eidolons.game.core.game.DC_Game;

import java.util.Set;

/**
 * Created by JustMe on 5/8/2017.
 */
public class DungeonHandler {

    protected DC_Game game;
    protected DungeonMaster  master;

    public DungeonHandler(DungeonMaster master) {
        this.master = master;
        this.game = master.getGame();
    }

    protected Module getModule() {
        return master.getModule();
    }

    protected Set<Module> getModules() {
        if (master instanceof LocationMaster) {
            return ((LocationMaster) master).getModuleMaster().getModules();
        }
        return null;
    }

    protected FloorLoader getFloorLoader() {
        return master.getFloorLoader();
    }

    public StructMaster getStructureMaster() {
        return master.getStructMaster();
    }

    public DC_ObjInitializer getObjInitializer() {
        return master.getObjInitializer();
    }
    public StructureBuilder getStructureBuilder() {
        return master.getStructureBuilder();
    }

    public DC_Game getGame() {
        return master.getGame();
    }

    public DungeonMaster getMaster() {
        return master;
    }

    public MetaGameMaster getMetaMaster() {
        return getGame().getMetaMaster();
    }

    public DungeonInitializer getInitializer() {
        return master.getInitializer();
    }

    public DungeonBuilder getBuilder() {
        return master.getBuilder();
    }

    public Positioner getPositioner() {
        return master.getPositioner();
    }

    public PlayerManager getPlayerManager() {
        return master.getPlayerManager();
    }
    public PlaceholderResolver getPlaceholderResolver() {
        return master.getPlaceholderResolver();
    }

    public MissionMaster getBattleMaster() {
        return master.getMissionMaster();
    }


    public MissionOptionManager getOptionManager() {
        return master.getOptionManager();
    }

    public MissionStatManager getStatManager() {
        return master.getStatManager();
    }

    public MissionConstructor getConstructor() {
        return master.getConstructor();
    }

    public DungeonSequence getBattle() {
        return master.getMission();
    }

    public Spawner getSpawner() {
        return master.getSpawner();
    }

    public ModuleLoader getModuleLoader() {
        return master.getModuleLoader();
    }

    public TransitHandler getTransitHandler() {
        return master.getTransitHandler();
    }

    public Location getFloorWrapper() {
        return master.getFloorWrapper();
    }
}
