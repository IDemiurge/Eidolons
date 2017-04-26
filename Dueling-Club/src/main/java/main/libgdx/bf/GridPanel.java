package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates;
import main.game.core.Eidolons;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.anims.particles.lighting.LightingManager;
import main.libgdx.anims.phased.PhaseAnimator;
import main.libgdx.anims.std.DeathAnim;
import main.libgdx.gui.panels.dc.actionpanel.datasource.PanelActionsDataSource;
import main.libgdx.texture.TextureCache;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static main.system.GuiEventType.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public class GridPanel extends Group {
    private static final String backgroundPath = "UI/custom/grid/GRID_BG_WIDE.png";
    private static final String emptyCellPath = "UI/cells/Empty Cell v3.png";
    private static final String hiddenCellPath = "UI/cells/Hidden Cell v2.png";
    private static final String highlightCellPath = "UI/cells/Highlight Green Cell v3.png";
    private static final String unknownCellPath = "UI/cells/Unknown Cell v2.png";
    private static final String cellBorderPath = "UI\\CELL for 96.png";
    protected TextureRegion emptyImage;
    protected TextureRegion hiddenImage;
    protected TextureRegion highlightImage;
    protected TextureRegion unknownImage;
    protected TextureRegion cellBorderTexture;
    protected DequeImpl<BattleFieldObject> units;
    protected GridCellContainer[][] cells;
    private CellBorderManager cellBorderManager;
    private Map<BattleFieldObject, BaseView> unitMap;
    private int cols;
    private int rows;
    private LightingManager lightingManager;

    public GridPanel(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public void updateOutlines() {
        unitMap.keySet().forEach(obj -> {
            if (!obj.isOverlaying()) {
                OUTLINE_TYPE outline = obj.getOutlineType();
                GridUnitView uv = (GridUnitView) unitMap.get(obj);

                TextureRegion texture = null;
                if (outline != null) {
                    texture = TextureCache.getOrCreateR(
                            Eidolons.game.getVisionMaster().getVisibilityMaster().getImagePath(outline, obj));
                }
            }
        });
    }

    public GridPanel init() {
        setUnitMap(new HashMap<>());
        emptyImage = TextureCache.getOrCreateR(emptyCellPath);
        hiddenImage = TextureCache.getOrCreateR(hiddenCellPath);
        highlightImage = TextureCache.getOrCreateR(highlightCellPath);
        unknownImage = TextureCache.getOrCreateR(unknownCellPath);
        cellBorderTexture = TextureCache.getOrCreateR(cellBorderPath);

        cells = new GridCellContainer[cols][rows];

        setCellBorderManager(new CellBorderManager());
        int rows1 = rows - 1;
        int cols1 = cols - 1;
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y] = new GridCellContainer(emptyImage, x, rows1 - y);
                cells[x][y].setX(x * GridConst.CELL_W);
                cells[x][y].setY(y * GridConst.CELL_H);
                addActor(cells[x][y].init());
            }
        }
        bindEvents();

        setHeight(cells[0][0].getHeight() * rows);
        setWidth(cells[0][0].getWidth() * cols);

        addListener(new ClickListener() {

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                GridPanel.this.getStage().setScrollFocus(GridPanel.this);
                return false;
            }

            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                if (PhaseAnimator.getInstance().checkAnimClicked(x, y, pointer, button)) {
                    return true;
                }
                return false;
            }
        });
        return this;
    }

    private void bindEvents() {

        GuiEventManager.bind(UPDATE_GUI, obj -> {
            if (Eidolons.game.getVisionMaster().getVisibilityMaster().isOutlinesOn()) {
                updateOutlines();
            }
        });

        GuiEventManager.bind(SELECT_MULTI_OBJECTS, obj -> {
            Pair<Set<DC_Obj>, TargetRunnable> p = (Pair<Set<DC_Obj>, TargetRunnable>) obj.get();
            Map<Borderable, Runnable> map = new HashMap<>();
            for (DC_Obj obj1 : p.getLeft()) {
                Borderable b = unitMap.get(obj1);
                if (b == null) {
                    b = cells[obj1.getX()][rows - 1 - obj1.getY()];
                }

                if (b instanceof GridUnitView) {
                    final GridUnitView gridView = (GridUnitView) b;
                    final UnitView unitView = gridView.getInitiativeQueueUnitView();
                    map.put(unitView, () -> p.getRight().run(obj1));
                }

                map.put(b, () -> p.getRight().run(obj1));
            }
            GuiEventManager.trigger(SHOW_BLUE_BORDERS, new EventCallbackParam(map));
        });

        GuiEventManager.bind(DESTROY_UNIT_MODEL, param -> {
            BattleFieldObject unit = (BattleFieldObject) param.get();
            GridUnitView view = (GridUnitView) unitMap.get(unit);
            removeUnitView(unit);
        });

        GuiEventManager.bind(INGAME_EVENT_TRIGGERED, param -> {
            Event event = (main.game.logic.event.Event) param.get();
            Ref ref = event.getRef();

            boolean caught = false;
            if (event.getType() == STANDARD_EVENT_TYPE.EFFECT_HAS_BEEN_APPLIED) {
                GuiEventManager.trigger(GuiEventType.EFFECT_APPLIED,
                        new EventCallbackParam<>(event.getRef().getEffect()));
                caught = true;
            }


            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_CHANGED_FACING
                    || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_CLOCKWISE
                    || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_ANTICLOCKWISE) {
                BattleFieldObject hero = (BattleFieldObject) ref.getObj(KEYS.TARGET);
                BaseView view = unitMap.get(hero);
                if (view != null && view instanceof GridUnitView) {
                    GridUnitView unitView = ((GridUnitView) view);
                    unitView.updateRotation(hero.getFacing().getDirection().getDegrees());
                }
                caught = true;
            }

            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED) {

                if (!DeathAnim.isOn() || ref.isDebug()) {
                    GuiEventManager.trigger(DESTROY_UNIT_MODEL, new EventCallbackParam(ref.getTargetObj()));
                }
//                else //TODO make it work instead of onFinishEvents!
//                AnimMaster.getInstance(). onDone(event,p ->
//                GuiEventManager.trigger(DESTROY_UNIT_MODEL,
//                 new EventCallbackParam(r.getTargetObj())
//                )
//                ,  new EventCallbackParam(r.getTargetObj())
//                );

                caught = true;
            }

            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_BEING_MOVED) {
                removeUnitView((BattleFieldObject) ref.getSourceObj());
                caught = true;
            }

            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING) {
                moveUnitView((BattleFieldObject) ref.getSourceObj());
                caught = true;
            }
//            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_SUMMONED) {
//                GuiEventManager.trigger(UNIT_CREATED, new EventCallbackParam(ref.getObj(KEYS.SUMMONED)));
//                caught = true; now in ObjCreator
//            }

            if (event.getType().name().startsWith("PARAM_BEING_MODIFIED")) {
                caught = true;
                /*switch (event.getType().getArg()) {
                    case "Spellpower":
                        int a = 10;
                        break;
                }*/
            }
            if (event.getType().name().startsWith("PROP_")) {
                caught = true;
            }
            if (event.getType().name().startsWith("ABILITY_")) {
                caught = true;
            }
            if (event.getType().name().startsWith("EFFECT_")) {
                caught = true;
            }

            if (event.getType().name().startsWith("PARAM_MODIFIED")) {
                switch (event.getType().getArg()) {
                    case "Illumination":
                        if (lightingManager != null) {
                            Obj o = event.getRef().getTargetObj();
                            if (o instanceof Unit) {
                                lightingManager.updateObject((BattleFieldObject) event.getRef().getTargetObj());
                            }
                        }
                        caught = true;
                        break;
                }

                caught = true;
            }

/*            if (!caught) {
                System.out.println("catch ingame event: " + event.getType() + " in " + event.getRef());
            }*/
        });


        GuiEventManager.bind(ACTIVE_UNIT_SELECTED, obj -> {
            BattleFieldObject hero = (BattleFieldObject) obj.get();
            BaseView view = unitMap.get(hero);
            if (view == null) {
                System.out.println("unitMap not initiatilized at ACTIVE_UNIT_SELECTED!");
                return;
            }

            if (view.getParent() instanceof GridCellContainer) {
                ((GridCellContainer) view.getParent()).popupUnitView(view);
            }

            if (hero.isMine()) {
                GuiEventManager.trigger(SHOW_GREEN_BORDER, view);

                GuiEventManager.trigger(UPDATE_QUICK_SLOT_PANEL, new PanelActionsDataSource((Unit) hero));
            } else {
                GuiEventManager.trigger(SHOW_RED_BORDER, view);
                GuiEventManager.trigger(UPDATE_QUICK_SLOT_PANEL, null);
            }
        });

        GuiEventManager.bind(CREATE_UNITS_MODEL, param -> {
            units = (DequeImpl<BattleFieldObject>) param.get();

            lightingManager = new LightingManager(units, rows, cols);

            Map<Coordinates, List<BattleFieldObject>> map = new HashMap<>();
            for (BattleFieldObject object : units) {
                Coordinates c = object.getCoordinates();
                if (!map.containsKey(c)) {
                    map.put(c, new ArrayList<>());
                }
                List<BattleFieldObject> list = map.get(c);
                list.add(object);
            }

            for (Coordinates coordinates : map.keySet()) {
                List<BaseView> views = new ArrayList<>();
                List<OverlayView> overlays = new ArrayList<>();

                if (map.get(coordinates) == null) {
                    continue;
                }
                for (BattleFieldObject object : map.get(coordinates)) {
                    if (!object.isOverlaying()) {
                        final BaseView baseView = UnitViewFactory.create(object);
                        unitMap.put(object, baseView);
                        views.add(baseView);
                    } else {
                        final OverlayView overlay = UnitViewFactory.createOverlay(object);
                        unitMap.put(object, overlay);
                        overlays.add(overlay);
                    }
                }

                cells[coordinates.getX()][rows - 1 - coordinates.getY()].setObjects(views);
                cells[coordinates.getX()][rows - 1 - coordinates.getY()].setOverlays(overlays);
            }

            GuiEventManager.bind(INITIATIVE_CHANGED, obj -> {
                Pair<Unit, Integer> p = (Pair<Unit, Integer>) obj.get();
                GridUnitView uv = (GridUnitView) unitMap.get(p.getLeft());
                uv.updateInitiative(p.getRight());
            });
            GuiEventManager.bind(UNIT_CREATED, p -> {
                addUnitView((BattleFieldObject) p.get());
            });

            WaitMaster.receiveInput(WAIT_OPERATIONS.GUI_READY, true);
            WaitMaster.markAsComplete(WAIT_OPERATIONS.GUI_READY);
        });

        GuiEventManager.bind(UPDATE_UNIT_VISIBLE, obj -> {
            final Pair<Unit, Boolean> pair = (Pair<Unit, Boolean>) obj.get();
            final BaseView baseView = unitMap.get(pair.getLeft());
            if (baseView instanceof GridUnitView) {
                final Boolean isVisible = pair.getRight();
                ((GridUnitView) baseView).setVisibleVal(isVisible ? 100 : 50);
            }
        });

        GuiEventManager.bind(UPDATE_UNIT_ACT_STATE, obj -> {
            final Pair<Unit, Boolean> pair = (Pair<Unit, Boolean>) obj.get();
            final BaseView baseView = unitMap.get(pair.getLeft());
            if (baseView instanceof GridUnitView) {
                final boolean mobilityState = pair.getRight();
                ((GridUnitView) baseView).setMobilityState(mobilityState);
            }
        });
    }

    private void moveUnitView(BattleFieldObject heroObj) {
        int rows1 = rows - 1;
        BaseView uv = unitMap.get(heroObj);
        Coordinates c = heroObj.getCoordinates();

        uv.setVisible(true);
        cells[c.x][rows1 - c.y].addActor(uv);

        if (lightingManager != null) {
            lightingManager.updatePos(heroObj);
            lightingManager.updateAll();
        }
    }

    private void addUnitView(BattleFieldObject heroObj) {
        BaseView uv = UnitViewFactory.create(heroObj);
        unitMap.put(heroObj, uv);
        moveUnitView(heroObj);
    }

    private BaseView removeUnitView(BattleFieldObject obj) {
        BaseView uv = unitMap.get(obj);
        GridCellContainer gridCellContainer = (GridCellContainer) uv.getParent();
        if (gridCellContainer == null) {
            LogMaster.log(1, obj + " IS ALREADY REMOVED!");
            return uv;
        }
        gridCellContainer.removeActor(uv);
        uv.setVisible(false);

        GuiEventManager.trigger(UPDATE_LIGHT, null);
        return uv;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        getCellBorderManager().draw(batch, parentAlpha);

        if (lightingManager != null) {
            lightingManager.updateLight();
        }
    }

    public CellBorderManager getCellBorderManager() {
        return cellBorderManager;
    }

    public void setCellBorderManager(CellBorderManager cellBorderManager) {
        this.cellBorderManager = cellBorderManager;
    }

    public Map<BattleFieldObject, BaseView> getUnitMap() {
        return unitMap;
    }

    public void setUnitMap(Map<BattleFieldObject, BaseView> unitMap) {
        this.unitMap = unitMap;
    }


    public int getRows() {
        return rows;
    }
}
