package libgdx.bf.grid.handlers;

import com.badlogic.gdx.graphics.Color;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMapDataSource;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import libgdx.bf.decor.pillar.PillarManager;
import libgdx.bf.decor.woods.Woods;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.grid.cell.BaseView;
import libgdx.bf.grid.cell.HpBarView;
import libgdx.bf.grid.moving.PlatformHandler;
import libgdx.bf.overlays.bar.HpBarManager;
import libgdx.screens.ScreenMaster;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;

import java.util.LinkedHashSet;
import java.util.Set;

import static main.system.GuiEventType.UPDATE_MAIN_HERO;

public class GridManager {
    private final PillarManager pillarManager;
    private final GridCommentHandler commentHandler;
    private final GridEventHandler eventHandler;
    private final ColorHandler colorHandler;
    private final GridPanel gridPanel;
    private static final boolean customDraw = true;
    protected GridAnimHandler animHandler;
    protected PlatformHandler platformHandler;
    Set<GridHandler> handlers = new LinkedHashSet<>();
    private boolean resetting;

    public Float getLightness(Coordinates c) {
        return colorHandler.getLightness(c);
    }

    public Color getBaseColor(Coordinates c) {
        return colorHandler.getBaseColor(c);
    }

    public Color getOrigColor(Coordinates c) {
        return colorHandler.getOrigColor(c);
    }

    public Color getColor(Coordinates c) {
        return colorHandler.getColor(c);
    }

    public GridManager(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
        // customDraw = !CoreEngine.isLevelEditor();
        handlers.add(pillarManager = new PillarManager(gridPanel));
        handlers.add(colorHandler = new ColorHandler(gridPanel));
        Woods woods;
        handlers.add(woods = new Woods(gridPanel));
        handlers.add(commentHandler = new GridCommentHandler(gridPanel));
        handlers.add(animHandler = new GridAnimHandler(gridPanel));
        handlers.add(eventHandler = new GridEventHandler(gridPanel));
        handlers.add(platformHandler = new PlatformHandler(gridPanel));


        GuiEventManager.bind(GuiEventType.BF_OBJ_RESET, p -> reset((BattleFieldObject) p.get()));
        // GuiEventManager.bind(GuiEventType.UPDATE_WALL_MAP , p-> reset());
        //wall map seems too often updated
    }

    private void reset(BattleFieldObject object) {
        BaseView baseView = gridPanel.getViewMap().get(object);
        baseView.getPortrait().setImage(object.getImagePath());
    }

    public static boolean isCustomDraw() {
        return customDraw;
    }

    public static void reset() {
        if (isGridInitialized()) {
            getInstance().resetMaps();
        }
        //TODO move various crap-functions into these handlers!
        /*
        what cases?
        voidHandler toggles a cell
        when do we update walls?
         */
    }

    private static boolean isGridInitialized() {
        return ScreenMaster.getGrid() != null;
    }

    public void afterLoaded() {
        if (CoreEngine.isLevelEditor())
            return;
        for (GridHandler handler : handlers) {
            handler.afterLoaded();
        }
        resetMaps();
    }

    private void resetMaps() {
        try {
            setResetting(true);
            pillarManager.reset();
            setResetting(false);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    private static GridManager getInstance() {
        return ScreenMaster.getGrid().getGridManager();
    }


    // @Override
    // protected PlatformHandler createPlatformHandler() {
    //     return new LE_PlatformHandler(this);
    // }
    protected PlatformHandler createPlatformHandler() {
        return new PlatformHandler(gridPanel);
    }

    private void checkBodyBarReset(BattleFieldObject object) {
        GuiEventManager.trigger(UPDATE_MAIN_HERO);
    }

    private void checkSoulBarReset(BattleFieldObject object) {
        GuiEventManager.trigger(UPDATE_MAIN_HERO);
    }

    public void checkHpBarReset(Obj obj) {
        HpBarView view = (HpBarView) gridPanel.getViewMap().get(obj);
        if (view != null)
            if (view.getActor().isVisible())
                if (view.getHpBar() != null)
                    if (
                            !ExplorationMaster.isExplorationOn()
                                    || HpBarManager.canHpBarBeVisible((BattleFieldObject) view.getActor().getUserObject()))
                        view.resetHpBar();
    }

    public ColorHandler getColorHandler() {
        return colorHandler;
    }

    public PillarManager getPillarManager() {
        return pillarManager;
    }

    public GridCommentHandler getCommentHandler() {
        return commentHandler;
    }

    public GridEventHandler getEventHandler() {
        return eventHandler;
    }

    public GridPanel getGridPanel() {
        return gridPanel;
    }

    public GridAnimHandler getAnimHandler() {
        return animHandler;
    }

    public PlatformHandler getPlatformHandler() {
        return platformHandler;
    }

    public Set<GridHandler> getHandlers() {
        return handlers;
    }

    public boolean isResetting() {
        return resetting;
    }

    public void setResetting(boolean resetting) {
        this.resetting = resetting;
    }

    public void act(float delta) {
        colorHandler.act(delta);
    }
}
