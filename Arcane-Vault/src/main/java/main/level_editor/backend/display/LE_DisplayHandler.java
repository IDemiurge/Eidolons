package main.level_editor.backend.display;

import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.VisualEnums.FULLSCREEN_ANIM;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.Eidolons;
import libgdx.screens.ScreenMaster;
import libgdx.screens.dungeon.GenericDungeonScreen;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import main.content.enums.macro.MACRO_CONTENT_CONSTS;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.MapEvent;


public class LE_DisplayHandler extends LE_Handler implements IDisplayHandler {
    MACRO_CONTENT_CONSTS.DAY_TIME time = MACRO_CONTENT_CONSTS.DAY_TIME.DAWN;

    public LE_DisplayHandler(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void vfxTimeNext() {
        GuiEventManager.trigger(MapEvent.PREPARE_TIME_CHANGED, time = time.getNext());
    }

    public void fullscreenAnim() {
        FULLSCREEN_ANIM fullscreen_anim = getEditHandler().chooseEnum(FULLSCREEN_ANIM.class);
        GuiEventManager.trigger(GuiEventType.SHOW_FULLSCREEN_ANIM, fullscreen_anim);
    }

    @Override
    public void toggleShadows() {
        getDisplayMode().toggleShadows();
        updateOptions();
    }

    @Override
    public void toggleGamma() {
        getDisplayMode().toggleGamma();
    }

    // @Override
    // public void toggleShards() {
    //     getDisplayMode().toggleShards();
    //     updateOptions();
    // }
    @Override
    public void toggleVfx() {
        getDisplayMode().toggleVfx();
        updateOptions();
    }

    public void updateOptions() {
        GraphicsOptions options = OptionsMaster.getGraphicsOptions();
        options.setValue(GraphicsOptions.GRAPHIC_OPTION.AMBIENCE_VFX, getDisplayMode().isShowVfx());
        options.setValue(GraphicsOptions.GRAPHIC_OPTION.GRID_VFX, getDisplayMode().isShowVfx());
        options.setValue(GraphicsOptions.GRAPHIC_OPTION.SHADOW_MAP_OFF, !getDisplayMode().isShowShadowMap());
        //TODO shards
        OptionsMaster.applyGraphicsOptions();
    }

    @Override
    public void gameView() {
        boolean b = !getModel().getDisplayMode().isGameView();
        getModel().getDisplayMode().setGameView(b);
        updateOptions();
        if (b) {
            Module m = null;

            if (getModel().getLastSelectedStruct() instanceof Module) {
                m = (Module) getModel().getLastSelectedStruct();
            } else {
                if (getModel().getLastSelectedStruct() == null) {
                    m = getGame().getModule();
                } else
                    m = getModule((Coordinates) getModel().getLastSelectedStruct().getCoordinatesSet().iterator().next());
            }
            Module finalM = m;

            getGame().getVisionMaster().getIllumination().resetIllumination(true);
            for (DC_Cell cell : getGame().getCells()) {
                int gamma = getGame().getVisionMaster()
                        .getGammaMaster().getGamma(getGame().getManager().getActiveObj(), cell);
                if (gamma > 0) {
                    main.system.auxiliary.log.LogMaster.log(1, cell + "gamma for cell: " + gamma);
                }
                cell.setGamma(getGame().getManager().getActiveObj(),
                        gamma);
            }
            Eidolons.onGdxThread(() ->
            {
                try {
                    ScreenMaster.getGrid().resetZIndices();
                    ScreenMaster.getGrid().getShadowMap().setModule(finalM);
                    ScreenMaster.getGrid().getShadowMap().update();
                    //TODO
                    // ScreenMaster.getDungeonGrid().getShards().setModule(finalM);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
                if (ScreenMaster.getScreen() instanceof GenericDungeonScreen) {
                    ((GenericDungeonScreen) ScreenMaster.getScreen()).getParticleManager().initModule(finalM);
                }
                //don't re-init?
                // ScreenMaster.getDungeonGrid().getShards().setModule(m);
            });

            getGame().getManager().resetWallMap();
        } else {
            getDisplayMode().setShowVfx(false);
            getDisplayMode().setShowShadowMap(false);
            getDisplayMode().setShowShards(false);
        }

    }

    public LE_DisplayMode getDisplayMode() {
        return getModel().getDisplayMode();
    }

}
