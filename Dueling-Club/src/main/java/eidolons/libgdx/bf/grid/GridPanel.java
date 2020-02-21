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
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.battlefield.vision.LastSeenMaster;
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
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;

import java.awt.*;
import java.util.*;
import java.util.List;

import static main.system.GuiEventType.*;

public class GridPanel extends Group {
    protected final int square;
    protected GridCellContainer[][] cells;
    protected int cols;
    protected int rows;
    protected Map<BattleFieldObject, BaseView> viewMap;
    protected GridUnitView hoverObj;
    protected static boolean gridEmitters;
    protected boolean resetVisibleRequired;

    protected ShadowMap shadowMap;
    protected WallMap wallMap;
    protected ShardVisuals shards;
    protected Pillars pillars;

    protected List<Manipulator> manipulators = new ArrayList<>();
    protected List<GridObject> gridObjects = new ArrayList<>();
    protected List<GroupX> customOverlayingObjects = new ArrayList<>();
    protected List<GroupX> customOverlayingObjectsTop = new ArrayList<>();
    protected List<GroupX> customOverlayingObjectsUnder = new ArrayList<>(100);
    protected List<GroupWithEmitters> emitterGroups = new ArrayList<>(125);
    protected List<OverlayView> overlays = new ArrayList<>();

    protected GridManager manager;
    protected GridOverlaysManager overlayManager;

    public GridPanel(int rows, int cols) {
        this.square = rows * cols;
        this.cols = cols;
        this.rows = rows;
    }

    public static boolean isGridEmitters() {
        return gridEmitters;
    }

    public static void setGridEmitters(boolean gridEmitters) {
        GridPanel.gridEmitters = gridEmitters;
    }

    public static  boolean isDrawEmitters() {
        return GridPanel.isGridEmitters();
    }

    public static  boolean isDrawEmittersOnTop() {
        if (EidolonsGame.FOOTAGE) {
            return false;
        }
        if (EidolonsGame.BOSS_FIGHT) {
            return false;
        }
        return true;
    }

    public GridPanel init(DequeImpl<BattleFieldObject> objects) {
        objects.removeIf(unit ->
                {
                    if (unit.getCoordinates().x < 0)
                        return true;
                    if (unit.getCoordinates().y < 0)
                        return true;
                    if (unit.getCoordinates().x >= cols)
                        return true;
                    if (unit.getCoordinates().y >= rows)
                        return true;
                    return false;
                }
        );
        this.viewMap = new HashMap<>();
        cells = new GridCellContainer[cols][rows];

        int rows1 = rows - 1;
        boolean hasVoid = false;
        TextureRegion emptyImage = TextureCache.getOrCreateR(getCellImagePath());
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                DC_Cell cell = DC_Game.game.getCellByCoordinate(Coordinates.get(x, rows1 - y));
                if (cell == null) {
                    hasVoid = true;
                    continue;
                }
                emptyImage = TextureCache.getOrCreateR(cell.getImagePath());
                cells[x][y] = new GridCellContainer(emptyImage, x, rows1 - y);
                cells[x][y].setX(x * GridMaster.CELL_W);
                cells[x][y].setY(y * GridMaster.CELL_H);
                addActor(cells[x][y].init());
                cells[x][y].setUserObject(cell);
            }
        }
        addVoid(hasVoid);

        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.SPRITE_CACHE_ON))
            TextureManager.addCellsToCache(cols, rows);

        addActor(new CellBorderManager());

        bindEvents();

        createUnitsViews(objects);

        setHeight(GridMaster.CELL_W * rows);
        setWidth(GridMaster.CELL_H * cols);

        addListener(new BattleClickListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                GridPanel.this.getStage().setScrollFocus(GridPanel.this);
                return false;
            }

            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                //                return PhaseAnimator.getInstance().checkAnimClicked(x, y, pointer, button);
                return false;
            }
        });

        //        if (AnimConstructor.isPreconstructAllOnGameInit())
        //            units.forEach(unit ->
        //            {
        //                if (unit instanceof Unit)
        //                    animMaster.getConstructor().preconstructAll((Unit) unit);
        //            });


        return this;
    }

    protected void addVoid(boolean hasVoid) {
        if (hasVoid) {
            if (isShardsOn())
                addActor(shards = new ShardVisuals(this));
            if (isPillarsOn())
                addActor(pillars = new Pillars(this));
        }
    }

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
        if (isDrawEmitters())
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

    public int getGdxY(int y) {
        return getRows() - 1 - y;
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
                    Vector2 v = GridMaster.getVectorForCoordinate(
                            object.getCoordinates(), false, false, this);
                    overlay.setPosition(v.x, v.y - GridMaster.CELL_H);
                    addOverlay(overlay);
                }
            }

            final GridCellContainer gridCellContainer = cells[coordinates.getX()]
                    [rows - 1 - coordinates.getY()];
            if (gridCellContainer == null) {
                continue;
            }
            views.forEach(gridCellContainer::addActor);
        }

        shadowMap = new ShadowMap(this);
        addActor(shadowMap);
        wallMap = new WallMap();
        addActor(wallMap);

        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.GUI_READY, true);
        WaitMaster.markAsComplete(WaitMaster.WAIT_OPERATIONS.GUI_READY);
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
        int rows1 = rows - 1;
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
            cells[c.x][rows1 - c.y].addActor(uv);
            GuiEventManager.trigger(GuiEventType.UNIT_VIEW_MOVED, uv);
            if (uv.getLastSeenView() != null) {
                if (LastSeenMaster.isUpdateRequired(object))
                    cells[c.x][rows1 - c.y].addActor(uv.getLastSeenView());
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

    protected void addUnitView(BattleFieldObject heroObj) {
        if (viewMap.get(heroObj) != null) {
            return;
        }
        BaseView uv = createUnitView(heroObj);
        unitMoved(heroObj);
        if (!isVisibleByDefault(heroObj))
            uv.setVisible(false);
    }

    protected BaseView removeUnitView(BattleFieldObject obj) {
        BaseView uv = viewMap.get(obj);
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
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
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


        shadowMap.setZIndex(Integer.MAX_VALUE);

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
        try {
            return cells[i][i1].getUserObject();
        } catch (Exception e) {
        }
        return null;
    }


    protected void bindEvents() {
        GuiEventManager.bind(MOVE_OVERLAYING, obj -> {
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
        GuiEventManager.bind(UNIT_CREATED, p -> {
            addUnitView((BattleFieldObject) p.get());
        });
        GuiEventManager.bind(REMOVE_GRID_OBJ, p -> {
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

        GuiEventManager.bind(GuiEventType.ADD_GRID_OBJ, p -> {
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
        GuiEventManager.bind(INIT_MANIPULATOR, (obj) -> {
            Manipulator manipulator = (Manipulator) obj.get();
            addActor(manipulator);
            manipulators.add(manipulator);
            Coordinates c = manipulator.getCoordinates();
            manipulator.setPosition(c.x * 128,
                    (getGdxY(c.y)));
        });
        GuiEventManager.bind(INIT_CELL_OVERLAY, (obj) -> {
            DC_Cell cell = (DC_Cell) obj.get();
            GridCellContainer container = cells[cell.getX()][rows - 1 - cell.getY()];
            String overlayData = cell.getOverlayData();

            String path = VariableManager.removeVarPart(overlayData);
            Coordinates c = new Coordinates(VariableManager.getVars(overlayData));
            TextureRegion region = new TextureRegion(TextureCache.getOrCreateR(path),
                    c.x * 128, c.y * 128, 128, 128);
            container.setOverlayTexture(region);
            container.setOverlayRotation(cell.getOverlayRotation());


            container.getBackImage().setDrawable(new TextureRegionDrawable(region));
        });
        GuiEventManager.bind(CELL_RESET, obj -> {
            DC_Cell cell = (DC_Cell) obj.get();
            GridCellContainer container = cells[cell.getX()][getGdxY(cell.getY())];
            //overlays
            container.getBackImage().setDrawable(
                    new TextureRegionDrawable(TextureCache.getOrCreateR(cell.getImagePath())));

            container.setOverlayRotation(cell.getOverlayRotation());

        });
        GuiEventManager.bind(UNIT_GREYED_OUT_ON, obj -> {
            BattleFieldObject bfObj = (BattleFieldObject) obj.get();
            if (bfObj.isOverlaying())
                return;
            UnitView unitView = getUnitView(bfObj);
            unitView.setFlickering(true);
            unitView.setGreyedOut(true);
            //            unitView.setVisible(true);
        });
        GuiEventManager.bind(UNIT_FADE_OUT_AND_BACK, obj -> {
            UnitView unitView = getUnitView((BattleFieldObject) obj.get());
            if (unitView != null) {
                unitView.fadeOut();
            }
        });
        GuiEventManager.bind(UNIT_GREYED_OUT_OFF, obj -> {
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

        GuiEventManager.bind(UNIT_MOVED, obj -> {
            unitMoved((BattleFieldObject) obj.get());
        });

        GuiEventManager.bind(DESTROY_UNIT_MODEL, param -> {
            BattleFieldObject unit = (BattleFieldObject) param.get();
            removeUnitView(unit);
        });


        GuiEventManager.bind(UNIT_VISIBLE_ON, p -> {
            if (p.get() instanceof Collection) {
                for (Object sub : ((Collection) p.get())) {
                    setVisible((BattleFieldObject) sub, true);
                }
                return;
            }
            setVisible((BattleFieldObject) p.get(), true);
        });
        GuiEventManager.bind(REMOVE_UNIT_VIEW, p -> {

            getUnitView((BattleFieldObject) p.get()).setVisible(false);
            getUnitView((BattleFieldObject) p.get()).remove();
        });
        GuiEventManager.bind(UNIT_VISIBLE_OFF, p -> {
            if (p.get() instanceof Collection) {
                for (Object sub : ((Collection) p.get())) {
                    setVisible((BattleFieldObject) sub, false);
                }
                return;
            }
            setVisible((BattleFieldObject) p.get(), false);
        });


    }

}
