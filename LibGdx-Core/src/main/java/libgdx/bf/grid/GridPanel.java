package libgdx.bf.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.consts.DecorData;
import eidolons.content.consts.libgdx.GdxColorMaster;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.cinematic.Cinematics;
import eidolons.puzzle.gridobj.GridObject;
import eidolons.puzzle.gridobj.LinkedGridObject;
import libgdx.bf.grid.moving.flight.FlightHandler;
import eidolons.game.module.dungeoncrawl.struct.Entrance;
import libgdx.anims.actions.ActionMaster;
import libgdx.anims.actions.FadeOutAction;
import libgdx.bf.GridMaster;
import libgdx.bf.decor.CellDecor;
import libgdx.bf.decor.CellDecorLayer;
import libgdx.bf.decor.shard.ShardVisuals;
import libgdx.bf.grid.handlers.ColorMap;
import libgdx.bf.grid.handlers.GridManager;
import libgdx.bf.grid.moving.PlatformCell;
import libgdx.bf.grid.moving.PlatformData;
import libgdx.bf.grid.moving.PlatformDecor;
import libgdx.bf.grid.moving.PlatformHandler;
import libgdx.bf.grid.sub.GridElement;
import libgdx.bf.light.ShadowMap;
import libgdx.bf.mouse.BattleClickListener;
import libgdx.bf.mouse.InputController;
import libgdx.bf.overlays.GridOverlaysManager;
import libgdx.bf.overlays.OverlayingMaster;
import libgdx.bf.overlays.map.BorderMap;
import libgdx.bf.overlays.map.PillarMap;
import libgdx.bf.overlays.map.WallMap;
import libgdx.gui.generic.GroupWithEmitters;
import libgdx.gui.generic.GroupX;
import libgdx.gui.panels.headquarters.HqPanel;
import libgdx.particles.ambi.ParticleManager;
import libgdx.screens.CustomSpriteBatch;
import libgdx.screens.ScreenMaster;
import libgdx.texture.TextureCache;
import libgdx.texture.TextureManager;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import libgdx.bf.grid.cell.*;
import main.content.enums.GenericEnums;
import main.data.ability.construct.VariableManager;
import main.entity.EntityCheckMaster;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;
import main.system.threading.WaitMaster;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;

import static main.system.GuiEventType.*;

public abstract class GridPanel extends GroupX {
    protected final int square;
    protected final int full_rows;
    protected final int full_cols;
    protected final GridManager gridManager;
    public int drawX1, drawX2, drawY1, drawY2;
    protected int moduleCols;
    protected int moduleRows;
    protected int x1, x2, y1, y2;
    protected float offsetX, offsetY;
    protected UnitGridView hoverObj;
    protected static boolean showGridEmitters;
    protected boolean resetVisibleRequired;

    protected ShadowMap shadowMap;
    protected WallMap wallMap;
    protected BorderMap borderMap;
    protected ShardVisuals shards;
    protected PillarMap pillars;
    protected PillarMap wallPillars;
    protected ParticleManager particleManager;

    protected GridCellContainer[][] cells;
    protected ObjectMap<Obj, BaseView> viewMap;

    protected Set<GridObject>[][] gridObjects;
    protected GroupX[][] customOverlayingObjects;
    protected GroupX[][] customOverlayingObjectsTop;
    protected GroupX[][] customOverlayingObjectsUnder;
    protected Array<GroupWithEmitters> emitterGroups;

    protected Array<Actor> actList = new Array<>(425);

    protected List<OverlayView> overlays = new ArrayList<>();
    //TODO modules
    // protected Set<BossVisual> bossVisuals = new LinkedHashSet<>();

    protected GridOverlaysManager overlayManager;
    FlightHandler flightHandler;

    protected Coordinates offset;
    protected Map<Module, GridSubParts> containerMap = new HashMap<>();
    protected List<PlatformCell> platforms = new LinkedList<>();
    protected List<UnitGridView> detached = new LinkedList<>();
    protected Set<PlatformDecor> platformDecor = new LinkedHashSet();
    protected final ObjectMap<DecorData.DECOR_LEVEL, CellDecorLayer> decorMap = new ObjectMap<>(4);

    List<GridCellContainer> topCells = new ArrayList<>(5);
    List<GridCellContainer> overCells = new ArrayList<>(25);
    List<GridCellContainer> screenCells = new ArrayList<>(65);
    protected boolean decorInitialized;
    protected boolean init;

    public GridPanel(int cols, int rows, int moduleCols, int moduleRows) {
        this.square = rows * cols;
        this.moduleCols = moduleCols;
        this.moduleRows = moduleRows;
        this.full_rows = rows;
        this.full_cols = cols;
        initFullGrid();
        flightHandler = new FlightHandler();
        addActor(flightHandler.getObjsOver());
        addActor(flightHandler.getObjsUnder());
        addActor(flightHandler.getObjsVfx());

        gridManager = new GridManager(this);

        for (DecorData.DECOR_LEVEL level : DecorData.DECOR_LEVEL.values()) {
            CellDecorLayer cellDecorLayer;
            addActor(cellDecorLayer = new CellDecorLayer(this));
            decorMap.put(level, cellDecorLayer);
        }
        //TODO have over and under layers!
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
/*
platforms...
it sort of broke at some point - need to investigate!
 */
        if (!isCustomHit())
            return super.hit(x, y, touchable);
        int gridX = (int) (x / 128);
        int gridY = getGdxY_ForModule((int) (y / 128));
        if (gridX < 0 || gridY < 0) {
            return null;
        }
        Actor view = hit(x, y, touchable, overlays);
        if (view == null) {
            view = getGridCell(gridX, gridY);
        }
        if (view == null) {
            return super.hit(x, y, touchable);
        }
        Vector2 v = view.parentToLocalCoordinates(new Vector2(x, y));
        return view.hit(v.x, v.y, touchable);
    }


    protected boolean isCustomHit() {
        return true;
    }

    public void setModule(Module module) {
        init = false;
        offset = module.getOrigin();
        x1 = offset.x;
        y1 = offset.y;
        offsetX = x1 * GridMaster.CELL_W;
        offsetY = y1 * GridMaster.CELL_H;

        moduleCols = module.getEffectiveWidth();
        moduleRows = module.getEffectiveHeight();
        x2 = moduleCols + offset.x;
        y2 = moduleRows + offset.y;
        GridSubParts container = containerMap.get(module);
        if (container == null) {
            container = new GridSubParts(moduleCols, moduleRows);
            containerMap.put(module, container);
        }
        if (viewMap != null)
            for (BaseView value : viewMap.values()) {
                if (value.isWithinCameraCheck()) {
                    value.fadeOut();
                } else {
                    value.remove(); //TODO  st add back on return
                }
            }

        viewMap = container.viewMap;
        emitterGroups = container.emitterGroups;
        customOverlayingObjectsUnder = container.customOverlayingObjectsUnder;
        customOverlayingObjectsTop = container.customOverlayingObjectsTop;
        customOverlayingObjects = container.customOverlayingObjects;
        gridObjects = container.gridObjects;
        gridObjects = container.gridObjects;

        //for others too?

        initModuleGrid();
        resetMaps();
        for (GridElement gridElement : getGridElements()) {
            if (gridElement != null) {
                gridElement.setModule(module);
            }
        }
        init = true;
    }

    protected GridElement[] getGridElements() {
        return new GridElement[]{
                shadowMap, shards, overlayManager
        };
    }

    protected void initModuleGrid() {

        int width = cells.length;
        int height = cells[0].length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                DC_Cell cell = DC_Game.game.getCell(Coordinates.get(x,
                        y));
                if (y < y2 && y >= y1
                        && x < x2 && x >= x1) {
                    if (cells[x][y] != null) {
                        resetCellForModule(cells[x][y]);
                        continue;
                    }
                    TextureRegion image = TextureCache.getRegionUV(cell.getImagePath());
                    GridCellContainer gridCell;
                    cells[x][y] = gridCell = createGridCell(image, x, y);

                    //setVoid(x, y, false);
                    addActor(gridCell.init());
                    gridCell.setUserObject(cell);
                    if (cell.isVOID()) {
                        setVoid(x, y, false);
                    } else if (DC_Game.game.getMetaMaster().getModuleMaster().getAllVoidCells().contains(Coordinates.get(x, y))) {
                        setVoid(x, y, false);
                    }

                    gridCell.setY(getDrawY(y) * GridMaster.CELL_H);
                    gridCell.setX(x * GridMaster.CELL_W);
                } else {
                    //OR do it via distance from main hero?
                    //compare pos of modules, choose axis and direction, increase delay
                    //add fade in waves
                    if (cells[x][y] != null)
                        ActionMaster.addFadeOutAction(cells[x][y], 1.5f, true);
                }
            }
        }

    }

    protected void resetCellForModule(GridCellContainer container) {
        ActionMaster.addFadeInAction(container, 1.5f);
        addActor(container);
    }


    public GridPanel initObjects(DequeImpl<BattleFieldObject> objects) {
        createUnitsViews(objects);
        return this;
    }

    public GridPanel initFullGrid() {
        //entire dungeon?
        cells = new GridCellContainer[full_cols][full_rows];
        setHeight(GridMaster.CELL_W * full_rows);
        setWidth(GridMaster.CELL_H * full_cols);
        overlayManager = createOverlays();

        addVoidDecorators(true);

        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.SPRITE_CACHE_ON))
            TextureManager.addCellsToCache(moduleCols, moduleRows);

        addActor(new CellBorderManager());
        if (isShadowMapOn()) {
            addActor(shadowMap = new ShadowMap(this));
        }
        addActor(wallMap = new WallMap());
        addActor(borderMap = new BorderMap());

        bindEvents();

        addListener(new BattleClickListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                //TODO ? 
                GridPanel.this.getStage().setScrollFocus(GridPanel.this);
                return true;
            }

            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                return super.touchDown(e, x, y, pointer, button);
            }
        });

        return this;
    }

    protected void addVoidDecorators(boolean hasVoid) {
        if (hasVoid) {
            if (isShardsOn())
                try {
                    addActor(shards = new ShardVisuals(this));
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
            if (isPillarsOn()) {
                addActor(pillars = new PillarMap(false));
                addActor(wallPillars = new PillarMap(true));
            }

        }
    }

    protected abstract GridOverlaysManager createOverlays();

    protected String getCellImagePath() {
        if (Eidolons.getGame().getDungeon() != null) {
            if (Eidolons.getGame().getDungeon().isSurface())
                return GridMaster.emptyCellPath;
        }
        return GridMaster.emptyCellPathFloor;
    }


    @Override
    public void act(float delta) {
        gridManager.act(delta);
        if (HqPanel.getActiveInstance() != null) {
            if (HqPanel.getActiveInstance().getColor().a == 1)
                return;
        }
        if (resetVisibleRequired) {
            resetVisible();
        }
        if (isCustomDraw()) {
            customAct(delta);
        } else
            super.act(delta);
    }

    protected void customAct(float delta) {
        InputController controller = ScreenMaster.getScreen().getController();
        drawX1 = controller.getGridDrawX1(getWidth());
        drawY1 = getDrawY(controller.getGridDrawY1(0));
        drawX2 = Math.min(getModuleCols(), controller.getGridDrawX2(getWidth()));
        drawY2 = getDrawY(controller.getGridDrawY2(0));
        for (int x = drawX1; x < drawX2; x++) {
            for (int y = drawY1; y < drawY2; y++) {
                cells[x][y].act(delta);
            }
        } //TODO filter these to optimize!
        for (Actor actor : actList) {
            actor.act(delta);
        }
        if (particleManager != null) {
            particleManager.act(delta);
        }
    }

    protected int getDrawY(int y) {
        return MathMaster.getMinMax(getGdxY_ForModule(y), 0, moduleRows - 1);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isCustomDraw()) {
            customDraw(batch);
        } else
            super.draw(batch, 1);
        if (isShowGridEmitters())
            if (isDrawEmittersOnTop()) {
                drawEmitters(batch);
                ((CustomSpriteBatch) batch).resetBlending();
            }
    }


    protected boolean isPillarsOn() {
        return true;
    }

    protected boolean isShardsOn() {
        return true;
    }

    protected void drawEmitters(Batch batch) {
        for (int i = 0; i < emitterGroups.size; i++) {
            emitterGroups.get(i).draw(batch, 1f, true);
        }
    }

    @Override
    public void addActor(Actor actor) {
        if (actor instanceof GroupWithEmitters) {
            emitterGroups.add((GroupWithEmitters) actor);
        }
        if (isCustomDraw()) {
            if ((actor instanceof UnitGridView)) {
                detached.add((UnitGridView) actor);
            }
            if (!(actor instanceof GridCellContainer)) {
                actList.add(actor);
            }
        }
        // if (!isCustomHit())
        super.addActor(actor);

    }

    @Override
    public boolean removeActor(Actor actor) {
        if (isCustomDraw()) {
            actList.removeValue(actor, true);
            if (detached.remove(actor)) {
                isCustomDraw();
            }
        }
        return super.removeActor(actor);
    }

    @Override
    public boolean removeActor(Actor actor, boolean unfocus) {
        if (isCustomDraw()) {
            actList.removeValue(actor, true);
            detached.remove(actor);
        }

        return super.removeActor(actor, unfocus);
    }

    protected UnitView getUnitView(BattleFieldObject battleFieldObject) {
        return (UnitView) viewMap.get(battleFieldObject);
    }

    public GridObject findGridObj(String key, Coordinates c) {
        if (c != null) {
            try {
                return gridObjects[c.x][c.y].iterator().next();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                return null;
            }
        }
        for (Set<GridObject>[] array : gridObjects)
            for (Set<GridObject> set : array)
                for (GridObject g : set) {
                    if (g.getKey().equalsIgnoreCase(key)) {
                        return g;
                    }
                }
        return null;
    }


    protected GridCellContainer createGridCell(TextureRegion emptyImage, int x, int y) {
        return new GridCellContainer(emptyImage, x, y, coord -> {
            // if (DC_Game.game.isWall(coord)) {
            //TODO ??? different colorFunc?
            //             }
            return getGridManager().getColor(coord);
        });
    }

    public void restoreVoid(int x, int y, boolean animated) {
        GridCellContainer cell =
                cells[x][(y)];
        cell.setVoid(false, animated);
        cell.getUserObject().setVOID(false);

        resetMaps();
    }

    public void resetMaps() {
        if (gridManager.isResetting())
            return;
        Eidolons.onNonGdxThread(GridManager::reset);
    }

    public void setVoid(int x, int y, boolean animated) {
        GridCellContainer cell = cells[x][(y)];
        cell.setVoid(true, animated);
        cell.getUserObject().setVOID(true);
        if (animated && init) {
            resetMaps();
        }
    }

    protected void setVisible(BattleFieldObject sub, boolean visible) {
        BaseView view = viewMap.get(sub);
        //TODO refactor this quick fix!
        if (view == null)
            return;
        if (sub.isWall()) {
            if (!visible) {
                if (view.isVisible()) {
                    return;
                }
            }
        }
        if (view.getParent() instanceof GridCellContainer) {
            ((GridCellContainer) view.getParent()).setDirty(true);
            if (((GridCellContainer) view.getParent()).getUserObject().isVOID()) {
                if (visible) {
                    if (sub instanceof Structure) {
                        return;
                    }

                }
            }

        }

        setVisible(view, visible);
    }

    protected void setVisible(BaseView view, boolean visible) {

        if (view == null)
            return;
        if (view.getParent() == null)
            addActor(view);
        if (visible) {
            if (view.isVisible())
                return;
        } else {
            if (!view.isVisible())
                return;
            if (view.getColor().a > 0 && view.getColor().a < 1)//            if (view.getActionsOfClass(FadeOutAction.class, false).size > 0)
                return;
        }
        if (!isFadeAnimForUnitsOn()) {
            view.setVisible(visible);
        } else {
            if (visible) {
                view.getColor().a = 0;
                view.setVisible(true);
                ActionMaster.addFadeInAction(view, getFadeDuration(view));
            } else {
                //                ActionMaster.checkHasAction(view, AlphaAction.class).if
                if (view.getActionsOfClass(FadeOutAction.class).size > 0) {
                    return;
                }
                view.clearActions();
                ActionMaster.addFadeOutAction(view, getFadeDuration(view));
                ActionMaster.addSetVisibleAfter(view, false);

                if (overlayManager != null)
                    overlayManager.clearTooltip(view.getUserObject());
            }
            if (view instanceof UnitGridView)
                if (((UnitGridView) view).getLastSeenView() != null) {
                    BattleFieldObject obj = ((UnitGridView) view).getUserObject();

                    GridMaster.resetLastSeen((UnitGridView) view,
                            obj, !visible);
                    if (obj.getLastSeenOutline() == null) {
                        (((UnitGridView) view).getLastSeenView()).setOutlinePathSupplier(
                                () -> null);
                    } else {
                        (((UnitGridView) view).getLastSeenView()).setOutlinePathSupplier(
                                () -> {
                                    try {
                                        return obj.getLastSeenOutline().getImagePath();
                                    } catch (Exception e) {
                                        ExceptionMaster.printStackTrace(e);
                                    }
                                    return null;
                                });
                    }
                }
        }
    }

    protected float getFadeDuration(BaseView view) {
        if (Cinematics.ON) {
            return 2f;
        }
        return 0.33f;
    }

    protected boolean isFadeAnimForUnitsOn() {
        return true;
    }

    protected UnitGridView doCreateUnitView(BattleFieldObject battleFieldObject) {
        return UnitViewFactory.doCreate(battleFieldObject);
    }

    protected OverlayView doCreateOverlay(BattleFieldObject battleFieldObject) {
        return UnitViewFactory.doCreateOverlay(battleFieldObject, getColorFunction());
    }

    protected Function<Coordinates, Color> getColorFunction() {
        return coord -> getGridManager().getColor(coord);
    }

    protected void createUnitsViews(DequeImpl<BattleFieldObject> units) {
        Map<Coordinates, List<BattleFieldObject>> map = new HashMap<>();
        LogMaster.log(1, " " + units);
        for (BattleFieldObject object : units) {
            Coordinates c = object.getCoordinates();
            if (c == null)
                continue;
            if (!map.containsKey(c)) {
                map.put(c, new ArrayList<>());
            }
            List<BattleFieldObject> list = map.get(c);
            list.add(object);
        }

        for (Coordinates coordinates : map.keySet()) {
            List<BaseView> views = new ArrayList<>();

            if (map.get(coordinates) == null) {
                continue;
            }
            for (BattleFieldObject object : map.get(coordinates)) {
                if (EntityCheckMaster.isBoss(object.getType())) {
                    continue;
                }
                if (!object.isOverlaying()) {
                    if (viewMap.get(object) != null) {
                        return;
                    }
                    final BaseView baseView = createUnitView(object);
                    views.add(baseView);
                } else {
                    final OverlayView overlay = doCreateOverlay(object);
                    if (!isVisibleByDefault(object))
                        overlay.setVisible(false);
                    viewMap.put(object, overlay);
                    addOverlay(overlay);
                }
            }
            try {
                final GridCellContainer gridCellContainer = cells[coordinates.getX()]
                        [(coordinates.getY())];

                if (gridCellContainer == null) {
                    LogMaster.log(1, "Grid cell is null at " + coordinates);
                    continue;
                }
                views.forEach(gridCellContainer::addActor);
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
            }

        }
    }

    public void afterInit() {

        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.GUI_READY, true);
        WaitMaster.markAsComplete(WaitMaster.WAIT_OPERATIONS.GUI_READY);
        gridManager.afterLoaded();
    }

    public FlightHandler getFlightHandler() {
        return flightHandler;
    }

    protected boolean isShadowMapOn() {
        return true;
    }

    protected boolean isVisibleByDefault(BattleFieldObject battleFieldObject) {
        if (battleFieldObject.isMine())
            return true;
        return battleFieldObject instanceof Entrance;
    }

    public void unitViewMoved(BaseView view) {
        unitMoved((BattleFieldObject) (view).getUserObject());
    }

    public void unitMoved(BattleFieldObject object) {
        UnitGridView uv = (UnitGridView) viewMap.get(object);
        if (uv == null) {
            return;
        }
        Coordinates c = object.getCoordinates();
        GridCellContainer cell = getGridCell(c.x, c.y);
        if (cell == null) {
            LogMaster.devLog("No cell for view: " + object.getNameAndCoordinate());
            return;
        }
        cell.addActor(uv);
        GuiEventManager.trigger(UNIT_VIEW_MOVED, uv);
        if (uv.getLastSeenView() != null) {
            if (GridMaster.isUpdateRequired(object))
                cell.addActor(uv.getLastSeenView());
        }
        if (overlayManager != null)
            overlayManager.clearTooltip(object);
    }

    protected BaseView createUnitView(BattleFieldObject battleFieldObject) {
        if (viewMap.get(battleFieldObject) != null) {
            LogMaster.log(1, ">>>>>> UnitView already created!!! " + battleFieldObject);
            return viewMap.get(battleFieldObject);
        }
        UnitGridView view = doCreateUnitView(battleFieldObject);
        viewMap.put(battleFieldObject, view);


        unitMoved(battleFieldObject);
        if (!isVisibleByDefault(battleFieldObject))
            view.setVisible(false);
        GuiEventManager.trigger(UNIT_VIEW_CREATED, view);
        return view;
    }

    protected void addUnitView(BattleFieldObject object) {
        if (viewMap.get(object) != null) {
            return;
        }
        if (object.isOverlaying()) {
            addOverlay(doCreateOverlay(object));
            return;
        }
        BaseView uv = createUnitView(object);
        unitMoved(object);
        if (!isVisibleByDefault(object))
            uv.setVisible(false);
    }

    public BaseView removeUnitView(BattleFieldObject obj) {
        BaseView uv = viewMap.get(obj);
        if (obj.isOverlaying()) {
            return removeOverlay(obj);

        }

        if (uv == null) {
            LogMaster.log(1, obj + " IS NOT ON UNIT MAP!");
            return null;
        }
        //        gridCellContainer.removeActor(uv);
        uv.remove(); //if it was detached...
        uv.setVisible(false);

        if (overlayManager != null)
            overlayManager.clearTooltip(obj);
        LogMaster.log(1, obj + " unit view REMOVED!");
        return uv;
    }

    protected BaseView removeOverlay(BattleFieldObject obj) {
        BaseView overlay = getOverlay(obj);
        overlays.remove(overlay);
        overlay.fadeOut();
        LogMaster.log(1, obj + " overlay view REMOVED!");
        return overlay;
    }

    protected void resetVisible() {
        //        if (DeathAnim.isOn() && animMaster.getDrawer().isDrawing()) {
        //            return;
        //        }

        for (Obj sub : viewMap.keys()) {
            BaseView view = viewMap.get(sub);
            if (view.getActions().size == 0) {
                if (sub.isDead()) {
                    view.setVisible(false);
                    view.remove();
                }
            }
        }
        resetVisibleRequired = false;
    }

    public UnitGridView getHoverObj() {
        return hoverObj;
    }

    public void setHoverObj(UnitGridView hoverObj) {
        this.hoverObj = hoverObj;
    }

    public void resetZIndices() {
        if (hoverObj != null) {
            if (hoverObj.getParent() instanceof GridCellContainer) {
                ((GridCellContainer) hoverObj.getParent()).setHovered(false);
            }
        }
        for (int x = drawX1; x < drawX2; x++) {
            for (int y = drawY1; y < drawY2; y++) {
                GridCellContainer cell = cells[x][y];
                cell.setHovered(false);
                List<GenericGridView> views = cell.getUnitViewsVisible();
                for (GenericGridView sub : views) {
                    if (sub.isHovered() && sub instanceof UnitGridView
                    ) {
                        setHoverObj((UnitGridView) sub);
                        cell.setHovered(true);
                    }
                }
            }
        }
    }

    protected void customDraw(Batch batch) {

        // ************* Step 1
        flightHandler.getObjsUnder().draw(batch, 1f);
        ((CustomSpriteBatch) batch).resetBlending();
        decorMap.get(DecorData.DECOR_LEVEL.LOWEST).draw(batch, 1);
        pillars.draw(batch, 1f);
        pillars.drawScreen(batch);
        for (PlatformDecor platform : platformDecor) {
            platform.draw(batch, 1);
        }
        decorMap.get(DecorData.DECOR_LEVEL.BOTTOM).draw(batch, 1);


        // ************* Step 2
        topCells.clear();
        overCells.clear();
        screenCells.clear();
        for (int x = drawX1; x < drawX2; x++)
            for (int y = drawY1; y < drawY2; y++) {
                GridCellContainer container = cells[x][y];
                if (isTopCell(container)) {
                    topCells.add(container);
                } else if (isOverCell(container)) {
                    container.drawCell(batch);
                    overCells.add(container);
                } else {
                    container.draw(batch, 1);
                    if (isScreenCell(container))
                        screenCells.add(container);
                }
            }
        //wtf will happen with stacking?..
        ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
        for (GridCellContainer cell : screenCells)
            cell.drawScreen(batch);
        ((CustomSpriteBatch) batch).resetBlending();


        // ************* Step 3
        draw(customOverlayingObjectsUnder, batch);
        decorMap.get(DecorData.DECOR_LEVEL.OVER_CELLS).draw(batch, 1f);
        if (particleManager != null) {
            if (!flightHandler.isOn())
                particleManager.draw(batch, 1f);
        }
        wallPillars.draw(batch, 1f);
        wallPillars.drawScreen(batch);


        // ************* Step 4
        screenCells.clear();
        for (GridCellContainer cell : overCells) {
            cell.drawWithoutCell(batch);
            if (isScreenCell(cell))
                screenCells.add(cell);
        }
        ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
        for (GridCellContainer cell : screenCells)
            cell.drawScreen(batch);
        ((CustomSpriteBatch) batch).resetBlending();


        // ************* Step 5
        if (shards != null) {
            shards.draw(batch, 1f);
        }
        if (isDrawShadowMap())
            if (shadowMap != null) {
                shadowMap.draw(batch, 1f);
            }
        for (PlatformCell platform : platforms) {
            platform.draw(batch, 1f);
        }
        draw(customOverlayingObjects, batch);

        ((CustomSpriteBatch) batch).resetBlending();

        decorMap.get(DecorData.DECOR_LEVEL.OVER_MAPS).draw(batch, 1f);

        // ************* Step 6
        screenCells.clear();
        for (GridCellContainer cell : topCells) {
            cell.draw(batch, 1f);
            // if (isScreenCell(cell) && cell.getUnitViewsVisible().size()<=1)
            //     screenCells.add(cell);
        }
        //TODO gdx Review - hover + screen is ugly now, but must be done

        // ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
        // for (GridCellContainer cell : screenCells) {
        //     cell.drawScreen(batch);
        // }
        // ((CustomSpriteBatch) batch).resetBlending();

        for (UnitGridView unitGridView : detached) {
            unitGridView.draw(batch, 1);
            unitGridView.drawScreen(batch);
        }

        // ************* Step 7
        if (gridManager.getPlatformHandler().isActive()) {
            UnitView baseView = (UnitView) viewMap.get(Eidolons.getMainHero());
            float x = baseView.getX();
            float y = baseView.getY();
            if (baseView.getParent() instanceof PlatformCell) {
                baseView.setPosition(baseView.getParent().getX(), baseView.getParent().getY());
            }
            baseView.getColor().a = 1f;
            baseView.draw(batch, 1);
            baseView.setPosition(x, y);
            baseView.drawScreen(batch);
        }
        /////////////////
        draw(customOverlayingObjectsTop, batch);


        // ************* Step 8
        overlays.forEach(overlayView -> {
            if (isDrawn(overlayView.getUserObject().getCoordinates()))
                if (!overlayView.isScreen()) overlayView.draw(batch, 1f);
        });

        ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
        overlays.forEach(overlayView -> {
            if (overlayView.isScreen())
                if (isDrawn(overlayView.getUserObject().getCoordinates())) {
                    overlayView.draw(batch, 1f);
                    overlayView.drawScreen(batch);
                }
        });
        ((CustomSpriteBatch) batch).resetBlending();


        // ************* Step 9
        overlayManager.draw(batch, 1f);
        flightHandler.getObjsOver().draw(batch, 1f);
        flightHandler.getObjsVfx().draw(batch, 1f);
        decorMap.get(DecorData.DECOR_LEVEL.TOP).draw(batch, 1f);


        // ************* Step 10
        // for (BossVisual visual : bossVisuals)
        //     visual.draw(batch, 1f);

        ((CustomSpriteBatch) batch).resetBlending();
    }

    protected boolean isDrawShadowMap() {
        return !flightHandler.isOn();
    }

    protected boolean isScreenCell(GridCellContainer container) {
        return container.isScreen();
    }

    protected boolean isOverCell(GridCellContainer container) {
        return container.isWall() || container.isMainHero();
    }

    protected boolean isTopCell(GridCellContainer gridCellContainer) {
        return gridCellContainer.isHovered();
    }

    Array<Actor> screenObjs = new Array<>(50);

    protected void drawScreen(Batch batch) {
        // ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
        for (Actor actor : screenObjs) {
            actor.draw(batch, 1);
        }
        screenObjs.clear();
    }

    protected void draw(Actor[][] array, Batch batch) {
        for (int x = 0; x < moduleCols; x++)
            for (int y = 0; y < moduleRows; y++) {
                Actor actor = array[x][y];
                if (actor instanceof GridObject) {
                    if (((GridObject) actor).isScreen()) {
                        screenObjs.add(actor);
                        continue;
                    }
                }
                if (actor != null)
                    actor.draw(batch, 1);
            }
        drawScreen(batch);
    }

    protected void resetZ(Actor[][] array) {
        for (int x = 0; x < moduleCols; x++)
            for (int y = 0; y < moduleRows; y++)
                if (array[x][y] != null)
                    array[x][y].setZIndex(Integer.MAX_VALUE);

    }

    public void addOverlay(OverlayView view) {
        Vector2 v = GridMaster.getVectorForCoordinate(
                view.getUserObject().getCoordinates(), false, false, this);
        view.setPosition(v.x, v.y);
        int width = (int) (GridMaster.CELL_W * view.getScale());
        int height = (int) (GridMaster.CELL_H * view.getScale());
        Dimension dimension = OverlayingMaster.getOffsetsForOverlaying(view.getDirection(), width, height, view);
        float calcXOffset = view.getX();
        float calcYOffset = view.getY();

        view.setBounds((float) dimension.getWidth() + calcXOffset
                , (float) dimension.getHeight() + calcYOffset
                , width, height);
        view.setOffsetX(dimension.getWidth());
        view.setOffsetY(dimension.getHeight());
        addActor(view);
        overlays.add(view);
    }

    public ObjectMap<Obj, BaseView> getViewMap() {
        return viewMap;
    }

    public int getModuleCols() {
        return moduleCols;
    }

    public GridCellContainer[][] getCells() {
        return cells;
    }

    public int getModuleRows() {
        return moduleRows;
    }

    public GroupX[][] getCustomOverlayingObjects() {
        return customOverlayingObjects;
    }

    public GridManager getGridManager() {
        return gridManager;
    }

    public DC_Cell getCell(Coordinates c) {
        return getCell(c.x, c.y);
    }

    public DC_Cell getCell(int i, int i1) {
        if (cells[i][i1] == null) {
            return null;
        }
        return cells[i][i1].getUserObject();
    }

    protected void bindEvents() {
        boolean removePrevious = !CoreEngine.isLevelEditor();
        //TODO boss Review
        // GuiEventManager.bind(ADD_BOSS_VIEW, obj -> {
        //     BossVisual visual = (BossVisual) obj.get();
        //     ////TODO how to manage it z?
        //     addActor(visual);
        //     bossVisuals.add(visual);
        //     visual.setPosition(visual.getCoordinates().getX() * 128,
        //             getGdxY_ForModule(visual.getCoordinates().getY()) * 128);
        //     //centering?
        //     // CellDecorLayer layer = decorMap.get(level);
        // });

        GuiEventManager.bind(removePrevious, CELL_RESET_VOID, obj -> {
            Coordinates c = (Coordinates) obj.get();
            restoreVoid(c.x, c.y, true);
        });
        GuiEventManager.bind(removePrevious, CELL_DECOR_RESET, obj -> {
            createDecor(obj.get());
        });

        GuiEventManager.bind(removePrevious, CELL_DECOR_INIT, obj -> {
            Map<Coordinates, DecorData> decorMap = (Map<Coordinates, DecorData>) obj.get();
            initDecor(decorMap);
        });
        GuiEventManager.bind(removePrevious, CELL_SET_VOID, obj -> {
            Coordinates c = (Coordinates) obj.get();
            LogMaster.log(1, "CELL_SET_VOID " + c);
            setVoid(c.x, c.y, true);
        });
        GuiEventManager.bind(removePrevious, CELLS_MASS_RESET_VOID, obj -> {
            Collection<Coordinates> c = (Collection<Coordinates>) obj.get();
            for (Coordinates coordinates : c) {
                restoreVoid(coordinates.x, coordinates.y, true);
            }
        });
        GuiEventManager.bind(removePrevious, GRID_RESET_MAPS, obj -> {
            resetMaps();
        });
            GuiEventManager.bind(removePrevious, CELLS_MASS_SET_VOID, obj -> {
            Collection<Coordinates> c = (Collection<Coordinates>) obj.get();
            for (Coordinates coordinates : c) {
                setVoid(coordinates.x, coordinates.y, false);
            }
            resetMaps();
        });

        GuiEventManager.bind(removePrevious, MOVE_OVERLAYING, obj -> {
            for (OverlayView overlay : overlays) {
                if (overlay.getUserObject() == obj.get()) {
                    Vector2 v = OverlayingMaster.getOffset(overlay.getDirection(),
                            overlay.getUserObject().getDirection());

                    overlay.setDirection(overlay.getUserObject().getDirection());

                    MoveToAction a = ActionMaster.addMoveByAction(overlay, v.x, v.y, 0.5f);
                    a.setInterpolation(Interpolation.circleOut);
                }
            }

        });
        GuiEventManager.bind(removePrevious, UNIT_CREATED, p -> {
            addUnitView((BattleFieldObject) p.get());
        });
        GuiEventManager.bind(removePrevious, REMOVE_GRID_OBJ, p -> {
            GridObject gridObj;
            Coordinates c;
            if (p.get() instanceof GridObject) {
                gridObj = ((GridObject) p.get());
                c = gridObj.getCoordinates();
            } else {
                List list = (List) p.get();
                String key = (String) list.get(0);
                c = (Coordinates) list.get(1);
                gridObj = findGridObj(key, c);
                if (gridObj == null) {
                    LogMaster.devLog("No grid obj to remove: " + key + c);
                    return;
                }
            }
            gridObj.fadeOut(true);
            customOverlayingObjectsUnder[c.x][c.y] = null;
            customOverlayingObjects[c.x][c.y] = null;
            customOverlayingObjectsTop[c.x][c.y] = null;

            Set<GridObject> objs = gridObjects[c.x][c.y];
            if (objs != null) {
                objs.remove(gridObj);
            }
        });

        GuiEventManager.bind(removePrevious, ADD_GRID_OBJ, p -> {
            GridObject object = (GridObject) p.get();
            addActor(object);
            if (object.isInitRequired()) {
                object.init();
            }
            Boolean under = object.isUnder();
            Coordinates c = object.getCoordinates();
            Set<GridObject> objs = gridObjects[c.x][c.y];
            if (objs == null) {
                gridObjects[c.x][c.y] = objs = new LinkedHashSet<>();
            }
            objs.add(object);
            if (under != null) {
                (under ? customOverlayingObjectsUnder : customOverlayingObjectsTop)[c.x][c.y] = (object);
                return;
            }
            if (object instanceof LinkedGridObject) {
                if (((LinkedGridObject) object).getLinked() instanceof OverlayView) {
                    customOverlayingObjectsTop[c.x][c.y] = (object);
                    return;
                } else {
                    if (((LinkedGridObject) object).getLinked().getUserObject() instanceof Structure) {
                        if (((Structure) ((LinkedGridObject) object).getLinked().getUserObject()).isWater()) {
                            customOverlayingObjectsUnder[c.x][c.y] = (object);
                            return;
                        }
                    }
                }
            }
            getCustomOverlayingObjects()[c.x][c.y] = (object);
        });
        GuiEventManager.bind(removePrevious, INIT_CELL_OVERLAY, (obj) -> {
            DC_Cell cell;
            if (obj.get() instanceof Coordinates) {
                cell = getCell(((Coordinates) obj.get()));
            } else
                cell = (DC_Cell) obj.get();
            GridCellContainer container = cells[cell.getX()][(cell.getY())];
            String overlayData = cell.getOverlayData();
            if (StringMaster.isEmpty(overlayData)) {
                container.setOverlayTexture(null);
                return;
            }
            String path = VariableManager.removeVarPart(overlayData);
            Texture tex = TextureCache.getOrCreate(path);
            TextureRegion r;
            if (!VariableManager.getVars(overlayData).isEmpty()) {
                Coordinates c = new Coordinates(VariableManager.getVars(overlayData));
                r = new TextureRegion(tex, c.x * 128, c.y * 128, 128, 128);
            } else {
                r = new TextureRegion(tex);
            }
            container.setOverlayTexture(r);
            container.setOverlayRotation(cell.getOverlayRotation());
            // container.getCellImage().setDrawable(new TextureRegionDrawable(region));
        });
        GuiEventManager.bind(removePrevious, CELL_RESET, obj -> {
            try {
                DC_Cell cell = (DC_Cell) obj.get();
                GridCellContainer container = cells[cell.getX()][(cell.getY())];
                //overlays
                container.getCellImage().setDrawable(
                        new TextureRegionDrawable(TextureCache.getRegionUV(cell.getImagePath())));

                container.setOverlayRotation(cell.getOverlayRotation());
                container.setTeamColor(GdxColorMaster.getColorForTheme(cell.getColorTheme()));
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
            }

        });
        GuiEventManager.bind(removePrevious, UNIT_GREYED_OUT_ON, obj -> {
            BattleFieldObject bfObj = (BattleFieldObject) obj.get();
            if (bfObj.isOverlaying())
                return;
            UnitView unitView = getUnitView(bfObj);
            unitView.setFlickering(true);
            unitView.setGreyedOut(true);
            //            unitView.setVisible(true);
        });
        GuiEventManager.bind(removePrevious, UNIT_FADE_OUT_AND_BACK, obj -> {
            UnitView unitView = getUnitView((BattleFieldObject) obj.get());
            if (unitView != null) {
                unitView.fadeOut();
            }
        });
        GuiEventManager.bind(removePrevious, UNIT_GREYED_OUT_OFF, obj -> {
            BattleFieldObject bfObj = (BattleFieldObject) obj.get();
            if (bfObj.isOverlaying())
                return;
            UnitView unitView = getUnitView(bfObj);
            unitView.setGreyedOut(false);
            unitView.setFlickering(false);
            //            ActorMaster.getActionsOfClass(unitView, AlphaAction.class);
            unitView.getActions().clear();
            //            unitView.setVisible(true);
        });

        GuiEventManager.bind(removePrevious, UNIT_MOVED, obj -> {
            unitMoved((BattleFieldObject) obj.get());
        });

        GuiEventManager.bind(removePrevious, DESTROY_UNIT_MODEL, param -> {
            BattleFieldObject unit = (BattleFieldObject) param.get();
            removeUnitView(unit);
        });

        GuiEventManager.bind(removePrevious, REMOVE_OVERLAY_VIEW, param -> {
            BattleFieldObject obj = (BattleFieldObject) param.get();
            removeOverlay(obj);
        });

        GuiEventManager.bind(removePrevious, UNIT_VISIBLE_ON, p -> {
            if (p.get() instanceof Collection) {
                for (Object sub : ((Collection) p.get())) {
                    setVisible((BattleFieldObject) sub, true);
                }
                return;
            }
            setVisible((BattleFieldObject) p.get(), true);
        });
        GuiEventManager.bind(removePrevious, UNIT_VISIBLE_OFF, p -> {
            if (p.get() instanceof Collection) {
                for (Object sub : ((Collection) p.get())) {
                    setVisible((BattleFieldObject) sub, false);
                }
                return;
            }
            setVisible((BattleFieldObject) p.get(), false);
        });


    }

    public void initDecor(Map<Coordinates, DecorData> decorMap) {
        for (Coordinates coordinates : decorMap.keySet()) {
            createDecor(coordinates, decorMap.get(coordinates));
        }
        decorInitialized = true;
    }

    protected void createDecor(Object o) {

        List list = (List) o;
        Coordinates c = (Coordinates) list.get(0);
        DecorData data = (DecorData) list.get(1);
        createDecor(c, data);

    }

    public BaseView getOverlay(Obj object) {
        for (OverlayView overlay : overlays) {
            if (overlay.getUserObject() == object) {
                return overlay;
            }
        }
        return null;
    }

    public static void setShowGridEmitters(boolean showGridEmitters) {
        GridPanel.showGridEmitters = showGridEmitters;
    }

    public static boolean isShowGridEmitters() {
        return true;// GridPanel.showGridEmitters;
    }

    public static boolean isDrawEmittersOnTop() {
        return true;
    }

    public void setUpdateRequired(boolean b) {
    }

    public void clearSelection() {
    }

    public boolean detachUnitView(BattleFieldObject unit) {
        return false;
    }

    public boolean isVoid(Coordinates c) {
        return isVoid(c.x, c.y);
    }

    public boolean isVoid(int x, int y) {
        DC_Cell c = getCell(x, y);
        if (c != null) {
            return c.isVOID();
        }
        return true;
    }


    public int getFullCols() {
        return full_cols;
    }

    public int getFullRows() {
        return full_rows;
    }

    public void resetCell(Coordinates c) {
        GridCellContainer cellContainer = cells[c.x][(c.y)];

        cellContainer.fadeOutOverlay();

        // LogMaster.log(1, "fadeOutOverlay " + cellContainer);
    }

    public void showMoveGhostOnCell(Unit unit) {
        Coordinates c = unit.getCoordinates();
        GridCellContainer cellContainer = cells[c.x][(c.y)];
        TextureRegion texture = null;
        if (getUnitView(unit).getPortrait().getContent().getDrawable() instanceof TextureRegionDrawable) {
            texture = ((TextureRegionDrawable) getUnitView(unit).getPortrait().getContent().getDrawable()).getRegion();
        }
        cellContainer.fadeInOverlay(texture);

        // LogMaster.log(1, "showMoveGhostOnCell " + cellContainer);
    }

    public int getGdxY_ForModule(int y) {
        return moduleRows - y;
    }

    // public float getModuleY(int y) {
    //     return y - (full_rows - moduleRows);
    // }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public GridCellContainer getGridCell(int x, int y) {
        if (x >= cells.length || y < 0 || y >= cells[0].length ) {
            return null;
        }
        return cells[x][y];
    }

    public PlatformDecor addPlatform(List<PlatformCell> cells, PlatformData data, PlatformDecor visuals) {
        for (PlatformCell cell : cells) {
            int x = cell.getGridX();
            int y = cell.getGridY();
            addActor(cell.init());
            cell.setY(getGdxY_ForModule(y) * GridMaster.CELL_H);
            cell.setX(x * GridMaster.CELL_W);
            platforms.add(cell);
        }
        platformDecor.add(visuals);
        addActor(visuals);
        return visuals;
    }

    public PlatformHandler getPlatformHandler() {
        return getGridManager().getPlatformHandler();
    }

    public GridCellContainer getGridCell(Coordinates coordinate) {
        return getGridCell(coordinate.x, coordinate.y);
    }

    public ShadowMap getShadowMap() {
        return shadowMap;
    }

    public ShardVisuals getShards() {
        return shards;
    }

    public boolean isDecorInitialized() {
        return decorInitialized;
    }

    public void setDecorInitialized(boolean decorInitialized) {
        this.decorInitialized = decorInitialized;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public List<GroupX> getCommentSprites() {
        return null;
    }

    public List<GroupX> getActiveCommentSprites() {
        return null;
    }

    public boolean isCustomDraw() {
        return GridManager.isCustomDraw();
    }

    public PillarMap getPillars() {
        return pillars;
    }

    public PillarMap getWallPillars() {
        return wallPillars;
    }

    public boolean isDrawn(Coordinates c) {
        if (c.x > drawX2)
            return false;
        if (c.x < drawX1)
            return false;
        if (c.y > drawY2)
            return false;
        return c.y >= drawY1;
    }

    public void setParticleManager(ParticleManager particleManager) {
        this.particleManager = particleManager;
    }


    protected void createDecor(Coordinates c,
                               DecorData data) {
        for (DecorData.DECOR_LEVEL level : decorMap.keys()) {
            CellDecorLayer cellDecorLayer = decorMap.get(level);
            if (data == null) {
                cellDecorLayer.remove(c);
            } else
                cellDecorLayer.add(c, data.getGraphicData(level), createDecorListener(c));
        }
    }

    protected EventListener createDecorListener(Coordinates c) {
        return null;
    }

    public void addDecor(Coordinates c, CellDecor decor, DecorData.DECOR_LEVEL overMaps) {
        decorMap.get(overMaps).add(c, decor, 0, 0);
    }

    public void firstDraw() {
        for (BaseView value : viewMap.values()) {
            if (value instanceof HpBarView) {
                ((HpBarView) value).resetHpBar();
            }

        }
    }

    public ColorMap getColorMap() {
        return gridManager.getColorHandler().getColorMap();
    }
}


