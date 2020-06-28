package eidolons.libgdx.bf.grid.handlers;

import com.badlogic.gdx.graphics.Color;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.bf.decor.pillar.PillarManager;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.HpBarView;
import eidolons.libgdx.bf.grid.moving.PlatformHandler;
import eidolons.libgdx.bf.light.ShadeLightCell;
import eidolons.libgdx.bf.light.ShadowMap;
import eidolons.libgdx.bf.overlays.bar.HpBarManager;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.LinkedHashSet;
import java.util.Set;

import static main.system.GuiEventType.UPDATE_MAIN_HERO;

public class GridManager {
    private final PillarManager pillarManager;
    private final GridCommentHandler commentHandler;
    private final GridEventHandler eventHandler;
    private final GridPanel gridPanel;
    private static final boolean customDraw = true;
    protected GridAnimHandler animHandler;
    protected PlatformHandler platformHandler;
    Set<GridHandler> handlers = new LinkedHashSet<>();

    public Float getLightness(Coordinates c) {
        if (gridPanel.getShadowMap().getCells(ShadowMap.SHADE_CELL.GAMMA_SHADOW) == null) {
            return 1f;
        }
        ShadeLightCell cell = gridPanel.getShadowMap().getCells(ShadowMap.SHADE_CELL.GAMMA_SHADOW)[c.x][c.y];
        if (cell == null) {
            return 0f;
        }
        return 1-cell.getColor().a;
    }

    public Color getColor(Coordinates c) {
        return ShadowMap.getLightColor(gridPanel.getCell(c.x,c.y));
        // return cell.getColor();
    }


    public GridManager(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
        // customDraw = !CoreEngine.isLevelEditor();
        handlers.add(pillarManager = new PillarManager(gridPanel));
        handlers.add(commentHandler = new GridCommentHandler(gridPanel));
        handlers.add(animHandler = new GridAnimHandler(gridPanel));
        handlers.add(eventHandler = new GridEventHandler(gridPanel));
        handlers.add(platformHandler = new PlatformHandler(gridPanel));

        GuiEventManager.bind(GuiEventType.GRID_RESET, p -> reset());
        // GuiEventManager.bind(GuiEventType.UPDATE_WALL_MAP , p-> reset());
        //wall map seems too often updated
    }

    public static boolean isCustomDraw() {
        return customDraw;
    }

    public void reset() {
        //TODO move various crap-functions into these handlers!
        /*
        what cases?
        voidHandler toggles a cell

        when do we update walls?
         */
        pillarManager.reset();
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
}
