package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates;
import main.game.core.Eidolons;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.anims.particles.lighting.LightingManager;
import main.libgdx.anims.std.DeathAnim;
import main.libgdx.bf.mouse.GridMouseListener;
import main.libgdx.gui.panels.dc.InitiativePanelParam;
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
    protected Texture emptyImage;
    protected Texture hiddenImage;
    protected Texture highlightImage;
    protected Texture unknownImage;
    protected Texture cellBorderTexture;
    protected DequeImpl<BattleFieldObject> units;
    protected GridCell[][] cells;
    private CellBorderManager cellBorderManager;
    private Map<BattleFieldObject, BaseView> unitMap;
    private int cols;
    private int rows;
    private LightingManager lightingManager;
    private boolean muteEventLog = true;

    public GridPanel(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public void updateOutlines() {
        unitMap.keySet().forEach(obj -> {
            OUTLINE_TYPE outline = obj.getOutlineType();
            UnitView uv = (UnitView) unitMap.get(obj);
            Texture texture = null;
            if (outline != null) {
                texture = TextureCache.getOrCreate(
                        Eidolons.game.getVisionMaster().getVisibilityMaster().getImagePath(outline, obj));
            }

            uv.setOutlineTexture(texture);
        });
    }

    public void updateGamma() {
        if (Eidolons.game.getManager().getActiveObj() == null) {
            return;
        }
        if (!Eidolons.game.getManager().getActiveObj().isMine()) {
            return;
        }
        int x = 0;
        int y = 0;
        for (GridCell[] cellRow : (cells)) {
            for (GridCell cell : cellRow) {
                x++;
                float gamma =
                        Eidolons.game.getVisionMaster().getGammaMaster().getGammaForCell(x, y);
                cell.setGamma(gamma);
            }
            y++;
        }

    }

    public void updateGraves() {

        unitMap.keySet().forEach(obj -> {
            UnitView uv = (UnitView) unitMap.get(obj);
            uv.setGraveIndex(Eidolons.game.getGraveyardManager().getGraveIndex(obj));
        });
    }

    public GridPanel init() {
        setUnitMap(new HashMap<>());
        emptyImage = TextureCache.getOrCreate(emptyCellPath);
        hiddenImage = TextureCache.getOrCreate(hiddenCellPath);
        highlightImage = TextureCache.getOrCreate(highlightCellPath);
        unknownImage = TextureCache.getOrCreate(unknownCellPath);
        cellBorderTexture = TextureCache.getOrCreate(cellBorderPath);

        cells = (new GridCell[cols][rows]);

        setCellBorderManager(new CellBorderManager());
        int rows1 = rows - 1;
        int cols1 = cols - 1;
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y] = new GridCell(emptyImage, x, rows1 - y);
                cells[x][y].setX(x * GridConst.CELL_W);
                cells[x][y].setY(y * GridConst.CELL_H);
                addActor(cells[x][y].init());
            }
        }
        bindEvents();

        /*
        LIGHT_EMISSION
         ILLUMINATION
           CONCEALMENT*/

        setHeight(cells[0][0].getHeight() * rows);
        setWidth(cells[0][0].getWidth() * cols);


        addListener(new GridMouseListener(this, cells, unitMap));
        return this;
    }

    private void bindEvents() {


        GuiEventManager.bind(UPDATE_GUI, obj -> {
            updateOutlines();
            updateGamma();
//            updateGraves();
        });
        GuiEventManager.bind(SELECT_MULTI_OBJECTS, obj -> {
            Pair<Set<DC_Obj>, TargetRunnable> p =
                    (Pair<Set<DC_Obj>, TargetRunnable>) obj.get();
            Map<Borderable, Runnable> map = new HashMap<>();
            for (DC_Obj obj1 : p.getLeft()) {
                Borderable b = unitMap.get(obj1);
                if (b == null) {
                    b = cells[obj1.getX()][rows - 1 - obj1.getY()];
                }
                map.put(b, () -> p.getRight().run(obj1));
            }
            GuiEventManager.trigger(SHOW_BLUE_BORDERS, new EventCallbackParam(map));
        });
        GuiEventManager.bind(UNIT_MOVED, param -> {
            moveUnitView((BattleFieldObject) param.get());
        });
        GuiEventManager.bind(DESTROY_UNIT_MODEL, param -> {
            BattleFieldObject unit = (BattleFieldObject) param.get();
            UnitView view = (UnitView) unitMap.get(unit);
            view.setVisibleVal(0);//set this val to zero remove unit from initiative queue
            GuiEventManager.trigger(REMOVE_FROM_INITIATIVE_PANEL,
                    new EventCallbackParam(new InitiativePanelParam(null, view.getId(), 0)));
            removeUnitView(unit);
        });


        GuiEventManager.bind(INGAME_EVENT_TRIGGERED, param -> {
            main.game.logic.event.Event event = (main.game.logic.event.Event) param.get();
            Ref ref = event.getRef();

            boolean caught = false;
            if (event.getType() == STANDARD_EVENT_TYPE.EFFECT_HAS_BEEN_APPLIED) {
                GuiEventManager.trigger(GuiEventType.EFFECT_APPLIED,
                        new EventCallbackParam<>(event.getRef().getEffect()));
                caught = true;
            }


            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_CHANGED_FACING
                    || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_CLOCKWISE
                    || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_ANTICLOCKWISE)
//                (r.getEffect() instanceof ChangeFacingEffect) nice try
            {
                BattleFieldObject hero = (BattleFieldObject) ref.getObj(KEYS.TARGET
                );
                BaseView view = unitMap.get(hero);
                if (view instanceof UnitView) {
                    UnitView unitView = ((UnitView) view);
                    unitView.updateRotation(hero.getFacing().getDirection().getDegrees());
                }
                caught = true;
            }


            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED) {

                if (!DeathAnim.isOn() || ref.isDebug()) {
                    GuiEventManager.trigger(DESTROY_UNIT_MODEL,
                            new EventCallbackParam(ref.getTargetObj()));
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
            if (!muteEventLog) {
                if (!caught) {
                    System.out.println("catch ingame event: " + event.getType() + " in " + event.getRef());
                }
            }
        });


        GuiEventManager.bind(ACTIVE_UNIT_SELECTED, obj -> {
            BattleFieldObject hero = (BattleFieldObject) obj.get();
            BaseView view = unitMap.get(hero);
            if (view == null) {
                System.out.println("unitMap not initiatilized at ACTIVE_UNIT_SELECTED! "
                );
                return;
            }
            if (view.getParent() instanceof GridCellContainer) {
                ((GridCellContainer) view.getParent()).popupUnitView(view);
            }
            if (hero.isMine()) {
                GuiEventManager.trigger(SHOW_GREEN_BORDER, new EventCallbackParam(view));
            } else {
                GuiEventManager.trigger(SHOW_RED_BORDER, new EventCallbackParam(view));
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
                List<UnitViewOptions> options = new ArrayList<>();
                List<UnitViewOptions> overlays = new ArrayList<>();
// TODO how can it throw NullPointer? (21.02)
                if (map.get(coordinates) == null) {
                    continue;
                }
                for (BattleFieldObject object : map.get(coordinates)) {
                    if (!object.isOverlaying()) {
                        options.add(new UnitViewOptions(object, unitMap));
                    } else {
                        overlays.add(new UnitViewOptions(object, unitMap));
                    }
                }

                GridCellContainer cellContainer = new GridCellContainer(emptyImage, coordinates.getX(), coordinates.getY()).init();
                cellContainer.setObjects(options);
                cellContainer.setOverlays(overlays);

                cells[coordinates.getX()][rows - 1 - coordinates.getY()].addInnerDrawable(cellContainer);
            }

            GuiEventManager.bind(INITIATIVE_CHANGED, obj -> {
                Pair<Unit, Integer> p = (Pair<Unit, Integer>) obj.get();
                UnitView uv = (UnitView) unitMap.get(p.getLeft());
                uv.updateInitiative(p.getRight());
            });
            GuiEventManager.bind(UNIT_CREATED, p -> {
                addUnitView((BattleFieldObject) p.get());
            });

            WaitMaster.receiveInput(WAIT_OPERATIONS.GUI_READY, true);
            WaitMaster.markAsComplete(WAIT_OPERATIONS.GUI_READY);
        });
    }

    public Actor hitChildren(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }

    private void moveUnitView(BattleFieldObject heroObj) {
        int rows1 = rows - 1;
        BaseView uv = unitMap.get(heroObj);
        Coordinates c = heroObj.getCoordinates();
        if (cells[c.x][rows1 - c.y].getInnerDrawable() == null) {
            GridCellContainer cellContainer = new GridCellContainer(cells[c.x][rows1 - c.y]).init();
            cells[c.x][rows1 - c.y].addInnerDrawable(cellContainer);
        }
        uv.setVisible(true);
        cells[c.x][rows1 - c.y].getInnerDrawable().addActor(uv);

        getCellBorderManager().updateBorderSize();

        if (lightingManager != null) {
            lightingManager.updatePos(heroObj);
            lightingManager.updateAll();
        }
    }

    private void addUnitView(BattleFieldObject heroObj) {
        UnitViewOptions uvo = new UnitViewOptions(heroObj, unitMap);
        new UnitView(uvo);
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
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() != Touchable.enabled) {
            return null;
        }
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (BaseView view : unitMap.values()) {
            if (view.getActions().size > 0) {
                view.act(delta);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y].draw(batch, parentAlpha);
            }
        }

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
