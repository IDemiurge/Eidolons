package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;

public class ModuleLoader extends DungeonHandler<Location> {

    private Module loading;
    private Module last;

    public ModuleLoader(DungeonMaster<Location> master) {
        super(master);
    }

    /*
    dispose of textures/...

    visual fx - blackout or so
    show loadscreen or whatnot
    fade music
    init ambi
    init music

    load assets
    create units/objects
    spawn encounters

    fade in
    zoom/cam
    step out
    init vfx
     */
    public void loadModule(Module module){
        last = getMetaMaster().getModuleMaster()
                .getCurrent();
        loading = module;
        freeResources();
//        initTransitFx();
//        showLoadScreen();
        initMusic();

        spawnEncounters();
        loadAssets(module);
        loadGrid(module);
    }

    private void spawnEncounters() {
//        getSpawner().getEncounterSpawner
    }

    private void initMusic() {
        AmbienceDataSource.AMBIENCE_TEMPLATE template = loading.getVfx();
        GuiEventManager.trigger(GuiEventType.UPDATE_AMBIENCE, template);
    }

    private void loadGrid(Module module) {
        //cache the prev. grid?
        //presently, LE uses same grid. Is it the easier way?
        //performance..

        //use other pipelines - spawner, ...

        GuiEventManager.trigger(GuiEventType.GRID_RESET, module);


//cannot do afterLoad()!

//        BFDataCreatedEvent data = new BFDataCreatedEvent(w, h, objects);
//        GuiEventManager.trigger(GuiEventType.CREATE_UNITS_MODEL, )
    }

    private void loadAssets(Module module) {
        String descriptors = module.getData().getValue(LevelStructure.MODULE_VALUE.assets);
        for (String path : ContainerUtils.openContainer(descriptors, ",")) {
//            BfObjEnums.SPRITES.valueOf()
            boolean ktx=false;
            Assets.loadSprite(path, false, ktx);
        }
    }

    private void freeResources() {
        //can we really just dispose of all textures?
        /*
        perhaps we can create a descriptor map from objects, then cross it with current one to figure out
        what we can dispose

        To be fair, such fervor is only needed for either low-end pcs or fat levels!
        Maybe we should dispose only when the module is '2 steps behind'?
        Do we support 'return to module'?
        Not always!

        IDEA: trigger addAsset when init() objects for grid
         */
        GuiEventManager.trigger(GuiEventType.DISPOSE_SCOPE);
    }
}
