package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.content.consts.VisualEnums;
import eidolons.entity.obj.BattleFieldObject;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.Core;
import eidolons.system.libgdx.GdxAdapter;

import main.game.bf.BattleFieldManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ModuleLoader extends DungeonHandler {

    private Module loading;


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
    public ModuleLoader(DungeonMaster master) {
        super(master);
        //ToDo-Cleanup
        // GuiEventManager.bind(GuiEventType.GRID_RESET, p -> {
        //     loadGdxGrid((Module) p.get());
        // });
    }

    public void loadGdxGrid(Module module) {
        GdxAdapter.getInstance().getDungeonScreen().moduleEntered(module, getObjects(module));
    }

    private DequeImpl<BattleFieldObject> getObjects(Module module) {
        Set<BattleFieldObject> set = game.getBfObjects().stream().filter(obj -> module.getCoordinatesSet().
                contains(obj.getCoordinates())).collect(Collectors.toCollection(LinkedHashSet::new));
        return new DequeImpl<>(set);
    }

    public void loadModuleFull(Module module) {
        Module last = getMetaMaster().getModuleMaster()
                .getCurrent();
        loading = module;
        // checkBossModuleInit(module);

        initLogicalGrid(module);
        //        freeResources();
        //        initTransitFx();
        //        showLoadScreen();
        //        initMusic();
        //        loadAssets(module);
        adjustTransitHero(module);
        if (module.isFirstInit()) {
            module.initBorderObjects();
            module.initObjects();
            if (module.getPlatformData() != null)
                GuiEventManager.trigger(GuiEventType.INIT_PLATFORMS, module.getPlatformData());
        }
        module.setFirstInit(false);

        GuiEventManager.trigger(GuiEventType.GRID_RESET );
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, Core.getMainHero());
    }

    private void adjustTransitHero(Module module) {
        Core.getMainHero().setModule(module);
    }

    public void loadInitial() {
        initLogicalGrid(getModule());
    }

    private void initLogicalGrid(Module module) {
        game.enterModule(module);
        game.getDungeonMaster().getBuilder().initModuleSize(module);
        BattleFieldManager.entered(module.getId());
        if (!CoreEngine.isLevelEditor()) {
            spawnEncounters(module);
        }
        // if (module.getData().getValue(LevelStructure.MODULE_VALUE.type).equalsIgnoreCase("boss")) {
        //     EidolonsGame.BOSS_FIGHT = true;
        //     try {
        //         getMetaMaster().initBossModule(module);
        //     } catch (Exception e) {
        //         main.system.ExceptionMaster.printStackTrace(e);
        //         return ;
        //     }
        //     // GuiEventManager.trigger(GuiEventType.LOAD_SCOPE, );
        // }
        //TODO
        //        PositionMaster.initDistancesCache(loading.getId(),
        //                getModule().getEffectiveWidth(),
        //                getModule().getEffectiveHeight());
    }

    private void spawnEncounters(Module module) {
        getBattleMaster().getEncounterSpawner().spawnEncounters(
                module.getEncounters());
    }

    private void initMusic() {
         VisualEnums.VFX_TEMPLATE template = loading.getVfx();
    }


    private void loadAssets(Module module) {
        String descriptors = module.getData().getValue(LevelStructure.MODULE_VALUE.assets);
        for (String path : ContainerUtils.openContainer(descriptors, ",")) {
            //TODO gdx Review 2021
            //            BfObjEnums.SPRITES.valueOf()
            // boolean ktx = false;
            // Assets.loadSprite(path );
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
