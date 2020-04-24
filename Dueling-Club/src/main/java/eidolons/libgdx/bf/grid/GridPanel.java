package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.battlefield.vision.LastSeenMaster;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.LinkedGridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Manipulator;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.actions.FadeOutAction;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.decor.Pillars;
import eidolons.libgdx.bf.decor.ShardVisuals;
import eidolons.libgdx.bf.grid.cell.*;
import eidolons.libgdx.bf.grid.sub.GridElement;
import eidolons.libgdx.bf.light.ShadowMap;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.bf.overlays.GridOverlaysManager;
import eidolons.libgdx.bf.overlays.OverlayingMaster;
import eidolons.libgdx.bf.overlays.WallMap;
import eidolons.libgdx.gui.generic.GroupWithEmitters;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.TextureManager;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import main.data.ability.construct.VariableManager;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;

import java.awt.*;
import java.util.List;
import java.util.*;

import static main.system.GuiEventType.*;

public abstract class GridPanel extends Group {
    protected final int square;
    private final int full_rows;
    private final int full_cols;
    protected int cols;
    protected int rows;
    protected int x1, x2, y1, y2;
    protected GridUnitView hoverObj;
    protected static boolean showGridEmitters;
    protected boolean resetVisibleRequired;

    protected ShadowMap shadowMap;
    protected WallMap wallMap;
    protected ShardVisuals shards;
    protected Pillars pillars;

    protected GridCellContainer[][] cells;
    protected GridCellContainer[][] removedCells;
    protected Map<BattleFieldObject, BaseView> viewMap;
    protected List<Manipulator> manipulators = new ArrayList<>();
    protected List<GridObject> gridObjects = new ArrayList<>();
    protected List<GroupX> customOverlayingObjects = new ArrayList<>();
    protected List<GroupX> customOverlayingObjectsTop = new ArrayList<>();
    protected List<GroupX> customOverlayingObjectsUnder = new ArrayList<>(100);
    protected List<GroupWithEmitters> emitterGroups = new ArrayList<>(125);
    protected List<OverlayView> overlays = new ArrayList<>();

    protected GridManager manager;
    protected GridOverlaysManager overlayManager;

    private Coordinates offset;
    private Map<Module, GridSubParts> containerMap = new HashMap<>();

    public GridPanel(int cols, int rows) {
        this.square = rows * cols;
        this.cols = cols;
        this.rows = rows;
        this.full_rows = rows;
        this.full_cols = cols;
        initFullGrid();
    }

    public void setModule(Module module) {
        offset = module.getOrigin();
        x1 = offset.x;
        y1 = offset.y;
        cols = module.getEffectiveWidth();
        rows = module.getEffectiveHeight();
        x2 = cols + offset.x;
        y2 = rows + offset.y;
        GridSubParts container = containerMap.get(module);
        if (container == null) {
            container = new GridSubParts();
            containerMap.put(module, container);
        }
        if (viewMap != null)
            for (BaseView value : viewMap.values()) {
                if (value.isWithinCameraCheck()) {
                    value.fadeOut();
                } else {
                    value.remove();
                }
            }

        viewMap = container.viewMap;
        customOverlayingObjectsUnder = container.customOverlayingObjects;
        customOverlayingObjectsTop = container.customOverlayingObjectsTop;
        customOverlayingObjectsUnder = container.customOverlayingObjectsUnder;
        emitterGroups = container.emitterGroups;
        gridObjects = container.gridObjects;
        manipulators = container.manipulators;
        overlays = container.overlays;
        //for others too?

        initModuleGrid();
        for (GridElement gridElement : getGridElements()) {
            if (gridElement != null) {
                gridElement.setModule(module);
            }
        }
    }

    private GridElement[] getGridElements() {
        return new GridElement[]{
                shadowMap, shards, overlayManager
        };
    }

    protected void initModuleGrid() {
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                DC_Cell cell = DC_Game.game.getCellByCoordinate(Coordinates.get(x,
                        getGdxY(y)));
                TextureRegion image = TextureCache.getOrCreateR(cell.getImagePath());

                cells[x][y] = createGridCell(image, x, getGdxY(y));
                cells[x][y].setX(x * GridMaster.CELL_W);
                cells[x][y].setY(y * GridMaster.CELL_H);

                addActor(cells[x][y].init());
                cells[x][y].setUserObject(cell);
            }
        }

    }

    public GridPanel initObjects(DequeImpl<BattleFieldObject> objects) {
        createUnitsViews(objects);
        return this;
    }

    public GridPanel initFullGrid() {
        //entire dungeon?
        cells = new GridCellContainer[cols][rows];
        removedCells = new GridCellContainer[cols][rows];
        setHeight(GridMaster.CELL_W * rows);
        setWidth(GridMaster.CELL_H * cols);

        addVoidDecorators(true);

        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.SPRITE_CACHE_ON))
            TextureManager.addCellsToCache(cols, rows);

        addActor(new CellBorderManager());
        if (isShadowMapOn()) {
            addActor(shadowMap = new ShadowMap(this));
        }
        addActor(wallMap = new WallMap());

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
                addActor(shards = new ShardVisuals(this));
            if (isPillarsOn())
                addActor(pillars = new Pillars(this));
        } else {

            for (int x = 0; x < cols; x++) {
                for (int y = 0; y < rows; y++) {
                    checkAddBorder(x, y);
                }
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
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, 1);
        if (isShowGridEmitters())
            if (isDrawEmittersOnTop())
                drawEmitters(batch);
    }


    @Override
    public void act(float delta) {
        if (HqPanel.getActiveInstance() != null) {
            if (HqPanel.getActiveInstance().getColor().a == 1)
                return;
        }
        if (resetVisibleRequired) {
            resetVisible();
        }
        super.act(delta);
    }


    protected boolean isPillarsOn() {
        return false;
    }

    protected boolean isShardsOn() {
        return false;
    }

    protected void drawEmitters(Batch batch) {
        for (int i = 0; i < emitterGroups.size(); i++) {
            emitterGroups.get(i).draw(batch, 1f, true);
        }
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
        if (actor instanceof GroupWithEmitters) {
            emitterGroups.add((GroupWithEmitters) actor);
        }
    }

    protected UnitView getUnitView(BattleFieldObject battleFieldObject) {
        return (UnitView) viewMap.get(battleFieldObject);
    }

    protected GridObject findGridObj(String key, Coordinates c) {
        for (GridObject gridObject : gridObjects) {
            if (gridObject.getKey().equalsIgnoreCase(key)) {
                if (c == null || gridObject.getCoordinates().equals(c)) {
                    return gridObject;
                }
            }
        }
        return null;
    }

    protected GridCellContainer createGridCell(TextureRegion emptyImage, int x, int y) {
        return new GridCellContainer(emptyImage, x, y);
    }

    public void restoreVoid(int x, int y, boolean animated) {
        GridCellContainer cell =
                removedCells[x][getGdxY(y)];
//        addActor(cell);
        if (animated) {
            ActionMaster.addFadeInAction(cell, 0.5f);
        } else {
            cell.setVisible(true);
        }
        cell.getUserObject().setVOID(false);
//        resetDecorators();
    }

    public void setVoid(int x, int y, boolean animated) {
        GridCellContainer cell = cells[x][getGdxY(y)];
//        cell.remove();
        if (animated) {
            ActionMaster.addFadeOutAction(cell, 0.5f, false);
        } else
            cell.setVisible(false);

        removedCells[x][getGdxY(y)] = cell;
        cell.getUserObject().setVOID(true);
    }

    public int getGdxY(int y) {
        return full_rows - 1 - y;
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
            if (view instanceof GridUnitView)
                if (((GridUnitView) view).getLastSeenView() != null) {
                    BattleFieldObject obj = getObjectForView(view);

                    LastSeenMaster.resetLastSeen((GridUnitView) view,
                            obj, !visible);
                    if (obj.getLastSeenOutline() == null) {
                        (((GridUnitView) view).getLastSeenView()).setOutlinePathSupplier(
                                () -> null);
                    } else {
                        (((GridUnitView) view).getLastSeenView()).setOutlinePathSupplier(
                                () -> {
                                    try {
                                        return obj.getLastSeenOutline().getImagePath();
                                    } catch (Exception e) {
                                        main.system.ExceptionMaster.printStackTrace(e);
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

    protected GridUnitView doCreateUnitView(BattleFieldObject battleFieldObject) {
        return UnitViewFactory.doCreate(battleFieldObject);
    }

    protected OverlayView doCreateOverlay(BattleFieldObject battleFieldObject) {
        return UnitViewFactory.doCreateOverlay(battleFieldObject);
    }

    protected void createUnitsViews(DequeImpl<BattleFieldObject> units) {

        Map<Coordinates, List<BattleFieldObject>> map = new HashMap<>();
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
                        [getGdxY(coordinates.getY())];
                if (gridCellContainer == null) {
                    continue;
                }
                views.forEach(gridCellContainer::addActor);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        }
    }

    public void afterInit() {

        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.GUI_READY, true);
        WaitMaster.markAsComplete(WaitMaster.WAIT_OPERATIONS.GUI_READY);
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
        unitMoved(getObjectForView(view));
    }

    public void unitMoved(BattleFieldObject object) {
        GridUnitView uv = (GridUnitView) viewMap.get(object);
        if (uv == null) {
            return;
        }
        Coordinates c = object.getCoordinates();
        //        if (!(object instanceof Entrance))
        //            if (c.equals(Eidolons.getMainHero().getCoordinates())) {
        //                if (object != Eidolons.getMainHero()) {
        //                    uv = uv;// it's a trap!!
        //                }
        //            }
        //        uv.setVisible(true);
        try {
            cells[c.x][getGdxY(c.y)].addActor(uv);
            GuiEventManager.trigger(GuiEventType.UNIT_VIEW_MOVED, uv);
            if (uv.getLastSeenView() != null) {
                if (LastSeenMaster.isUpdateRequired(object))
                    cells[c.x][getGdxY(c.y)].addActor(uv.getLastSeenView());
            }
        } catch (Exception e) {
            main.system.auxiliary.log.LogMaster.dev("No cell for view: " + object.getNameAndCoordinate());
        }
        if (overlayManager != null)
            overlayManager.clearTooltip(object);
    }

    protected BaseView createUnitView(BattleFieldObject battleFieldObject) {
        if (viewMap.get(battleFieldObject) != null) {
            main.system.auxiliary.log.LogMaster.log(1, ">>>>>> UnitView already created!!! " + battleFieldObject);
            return viewMap.get(battleFieldObject);
        }
        GridUnitView view = doCreateUnitView(battleFieldObject);
        viewMap.put(battleFieldObject, view);


        unitMoved(battleFieldObject);
        if (!isVisibleByDefault(battleFieldObject))
            view.setVisible(false);
        GuiEventManager.trigger(GuiEventType.UNIT_VIEW_CREATED, view);
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

    protected BaseView removeUnitView(BattleFieldObject obj) {
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

    private BaseView removeOverlay(BattleFieldObject obj) {
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

        for (BattleFieldObject sub : viewMap.keySet()) {
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

    public GridUnitView getHoverObj() {
        return hoverObj;
    }

    public void setHoverObj(GridUnitView hoverObj) {
        this.hoverObj = hoverObj;
    }

    public void resetZIndices() {
        List<GridCellContainer> topCells = new ArrayList<>();
        loop:
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                GridCellContainer cell = cells[x][y];
                if (cell == null) {
                    continue;
                }
                cell.setHovered(false);
                List<GenericGridView> views = cell.getUnitViewsVisible();
                if (views.isEmpty()) {
                    cell.setZIndex(0);
                } else {
                    if (!cell.getUserObject().isPlayerHasSeen()) {
                        cell.setZIndex(x + y);
                    } else {
                        cell.setZIndex(square + x + y);
                    }
                    //                TODO     cell.recalcUnitViewBounds();
                    for (GenericGridView sub : views) {
                        if (sub.isHovered() && sub instanceof GridUnitView
                        ) {
                            setHoverObj((GridUnitView) sub);
                            topCells.add(cell);
                            cell.setHovered(true);
                        } else if (
                                sub.getUserObject().isBoss() ||
                                        sub.getUserObject().isPlayerCharacter() || sub.isStackView()) {
                            topCells.add(cell);
                        }
                    }
                }
            }
        }
        customOverlayingObjectsUnder.forEach(obj -> {
            obj.setZIndex(Integer.MAX_VALUE);
        });

        wallMap.setVisible(WallMap.isOn());
        wallMap.setZIndex(Integer.MAX_VALUE);

        if (shadowMap != null) {
            shadowMap.setZIndex(Integer.MAX_VALUE);
        }

        customOverlayingObjects.forEach(obj -> {
            obj.setZIndex(Integer.MAX_VALUE);
        });
        for (GridCellContainer cell : topCells) {
            cell.setZIndex(Integer.MAX_VALUE);
        }
        customOverlayingObjectsTop.forEach(obj -> {
            obj.setZIndex(Integer.MAX_VALUE);
        });
        manipulators.forEach(manipulator -> {
            manipulator.setZIndex(Integer.MAX_VALUE);
        });
        overlays.forEach(overlayView -> overlayView.setZIndex(Integer.MAX_VALUE));

        overlayManager.setZIndex(Integer.MAX_VALUE);


    }

    public void addOverlay(OverlayView view) {
        Vector2 v = GridMaster.getVectorForCoordinate(
                view.getUserObject().getCoordinates(), false, false, this);
        view.setPosition(v.x, v.y - GridMaster.CELL_H);
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

    public Map<BattleFieldObject, BaseView> getViewMap() {
        return viewMap;
    }

    public int getCols() {
        return cols;
    }

    public GridCellContainer[][] getCells() {
        return cells;
    }

    public int getRows() {
        return rows;
    }

    public BattleFieldObject getObjectForView(BaseView source) {
        return (BattleFieldObject) source.getUserObject(); //TODO igg demo fix
//        return new MapMaster<BattleFieldObject, BaseView>()
//         .getKeyForValue(viewMap, source);
    }

    public List<GroupX> getCustomOverlayingObjects() {
        return customOverlayingObjects;
    }

    public GridManager getGridManager() {
        return manager;

    }

    public DC_Cell getCell(int i, int i1) {
        return cells[i][i1].getUserObject();
    }


    protected void bindEvents() {
        boolean removePrevious = true;

        GuiEventManager.bind(removePrevious, RESET_VIEW, obj -> {
            BattleFieldObject object = (BattleFieldObject) obj.get();
            UnitView unitView = getUnitView(object);
            unitView.setPortraitTexture(TextureCache.getOrCreateR(object.getImagePath()));
//            main.system.auxiliary.log.LogMaster.log(1,object.getNameAndCoordinate()+
//                    " RESET_VIEW: " +object.getImagePath());
        });

        GuiEventManager.bind(removePrevious, CELL_RESET_VOID, obj -> {
            Coordinates c = (Coordinates) obj.get();
            restoreVoid(c.x, c.y, true);
        });

        GuiEventManager.bind(removePrevious, CELL_SET_VOID, obj -> {
            Coordinates c = (Coordinates) obj.get();
            setVoid(c.x, c.y, true);
        });
        GuiEventManager.bind(removePrevious, CELLS_MASS_RESET_VOID, obj -> {
            Collection<Coordinates> c = (Collection<Coordinates>) obj.get();
            for (Coordinates coordinates : c) {
                restoreVoid(coordinates.x, coordinates.y, true);
            }
        });
        GuiEventManager.bind(removePrevious, CELLS_MASS_SET_VOID, obj -> {
            Collection<Coordinates> c = (Collection<Coordinates>) obj.get();
            for (Coordinates coordinates : c) {
                setVoid(coordinates.x, coordinates.y, true);
            }
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
            List list = (List) p.get();
            String key = (String) list.get(0);
            Coordinates c = (Coordinates) list.get(1);
            GridObject gridObj = findGridObj(key, c);
            if (gridObj == null) {
                main.system.auxiliary.log.LogMaster.dev("No grid obj to remove: " + key + c);
                return;
            }
            gridObj.fadeOut(true);
            customOverlayingObjectsUnder.remove(gridObj);
            customOverlayingObjects.remove(gridObj);
            customOverlayingObjectsTop.remove(gridObj);
            gridObjects.remove(gridObj);
        });

        GuiEventManager.bind(removePrevious, GuiEventType.ADD_GRID_OBJ, p -> {
            GridObject object = (GridObject) p.get();
            addActor(object);

            gridObjects.add(object);
            Boolean under = object.getUnder();
            if (under != null) {
                (under ? customOverlayingObjectsUnder : customOverlayingObjectsTop).add(object);
                return;
            }
            if (object instanceof LinkedGridObject) {
                if (((LinkedGridObject) object).getLinked() instanceof OverlayView) {
                    customOverlayingObjectsTop.add(object);
                    return;
                } else {
                    if (((LinkedGridObject) object).getLinked().getUserObject() instanceof Structure) {
                        if (((Structure) ((LinkedGridObject) object).getLinked().getUserObject()).isWater()) {
                            customOverlayingObjectsUnder.add(object);
                            return;
                        }
                    }
                }
            }
            getCustomOverlayingObjects().add(object);
        });
        GuiEventManager.bind(removePrevious, INIT_MANIPULATOR, (obj) -> {
            Manipulator manipulator = (Manipulator) obj.get();
            addActor(manipulator);
            manipulators.add(manipulator);
            Coordinates c = manipulator.getCoordinates();
            manipulator.setPosition(c.x * 128,
                    (getGdxY(c.y)));
        });
        GuiEventManager.bind(removePrevious, INIT_CELL_OVERLAY, (obj) -> {
            DC_Cell cell = (DC_Cell) obj.get();
            GridCellContainer container = cells[cell.getX()][getGdxY(cell.getY())];
            String overlayData = cell.getOverlayData();

            String path = VariableManager.removeVarPart(overlayData);
            Coordinates c = new Coordinates(VariableManager.getVars(overlayData));
            TextureRegion region = new TextureRegion(TextureCache.getOrCreateR(path),
                    c.x * 128, c.y * 128, 128, 128);
            container.setOverlayTexture(region);
            container.setOverlayRotation(cell.getOverlayRotation());


            container.getBackImage().setDrawable(new TextureRegionDrawable(region));
        });
        GuiEventManager.bind(removePrevious, CELL_RESET, obj -> {
            DC_Cell cell = (DC_Cell) obj.get();
            GridCellContainer container = cells[cell.getX()][getGdxY(cell.getY())];
            //overlays
            container.getBackImage().setDrawable(
                    new TextureRegionDrawable(TextureCache.getOrCreateR(cell.getImagePath())));

            container.setOverlayRotation(cell.getOverlayRotation());

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
        return GridPanel.showGridEmitters;
    }

    public static boolean isDrawEmittersOnTop() {
        if (EidolonsGame.FOOTAGE) {
            return false;
        }
        return !EidolonsGame.BOSS_FIGHT;
    }

    public void setUpdateRequired(boolean b) {
    }

    public void clearSelection() {
    }

    public boolean detachUnitView(Unit unit) {
        return false;
    }

    public boolean isVoid(Coordinates c) {
        return isVoid(c.x, c.y);
    }

    public boolean isVoid(int x, int y) {
        return getCell(x, y).isVOID();
    }

    protected void checkAddBorder(int x, int y) {
        Boolean hor = null;
        Boolean vert = null;
        if (x + 1 == cols)
            hor = true;
        if (x == 0)
            hor = false;
        if (y + 1 == rows)
            vert = true;
        if (y == 0)
            vert = false;

        float posX = x * GridMaster.CELL_W;
        float posY = y * GridMaster.CELL_H;
        String suffix = null;
        if (hor != null) {
            int i = hor ? 1 : -1;
            suffix = hor ? "right" : "left";
            com.badlogic.gdx.scenes.scene2d.ui.Image image = new com.badlogic.gdx.scenes.scene2d.ui.Image(TextureCache.getOrCreateR(
                    StrPathBuilder.build(
                            "ui", "cells", "bf", "gridBorder " +
                                    suffix +
                                    ".png")));
            addActor(image);
            image.setPosition(posX + i * GridMaster.CELL_W + (20 - 20 * i)//+40
                    , posY
                    //+ i * 35
            );
        }
        if (vert != null) {
            int i = vert ? 1 : -1;
            suffix = vert ? "up" : "down";
            com.badlogic.gdx.scenes.scene2d.ui.Image image = new com.badlogic.gdx.scenes.scene2d.ui.Image(TextureCache.getOrCreateR(StrPathBuilder.build(
                    "ui", "cells", "bf", "gridBorder " +
                            suffix +
                            ".png")));
            addActor(image);
            image.setPosition(posX //+ i * 35
                    , posY
                            + i * GridMaster.CELL_H + (20 - 20 * i));//+40
        }
        TextureRegion cornerRegion = TextureCache.getOrCreateR(GridMaster.gridCornerElementPath);
        if (hor != null)
            if (vert != null) {
                int i = vert ? 1 : -1;
                com.badlogic.gdx.scenes.scene2d.ui.Image image = new com.badlogic.gdx.scenes.scene2d.ui.Image(cornerRegion);
                image.setPosition(posX + i * 40 + i * GridMaster.CELL_W + i * -77, posY
                        + i * 40 + i * GridMaster.CELL_H + i * -77);

                if (!vert && hor) {
                    image.setX(image.getX() + 170);
                    image.setY(image.getY() + 12);
                }
                if (vert && !hor) {
                    image.setX(image.getX() - 180);
                    image.setY(image.getY() - 25);
                }
                if (vert && hor) {
                    image.setX(image.getX() - 15);
                    image.setY(image.getY() - 15);
                }
                addActor(image);
            }
    }

    public int getFullCols() {
        return full_cols;
    }

    public int getFullRows() {
        return full_rows;
    }

    public void resetCell(Coordinates c) {
        GridCellContainer cellContainer = cells[c.x][getGdxY(c.y)];

        cellContainer.fadeOutOverlay();

    }
    public void showMoveGhostOnCell(Unit unit) {
        Coordinates c = unit.getCoordinates();
        GridCellContainer cellContainer = cells[c.x][getGdxY(c.y)];
        TextureRegion texture = null;
        if (getUnitView(unit).getPortrait().getContent().getDrawable() instanceof TextureRegionDrawable) {
            texture =   ((TextureRegionDrawable) getUnitView(unit).getPortrait().getContent().getDrawable()).getRegion();
        }
        cellContainer.fadeInOverlay(texture);

    }
}
