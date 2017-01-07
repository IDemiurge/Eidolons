package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.PointX;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.GameScreen;
import main.libgdx.bf.mouse.GridMouseListener;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.texture.TextureManager;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.libgdx.prototype.Lightmap;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
    protected DequeImpl<DC_HeroObj> units;
    protected GridCell[][] cells;
    private Lightmap lightmap;
    private CellBorderManager cellBorderManager;
    private Map<DC_HeroObj, UnitView> unitMap;
    private int cols;
    private int rows;

    public GridPanel(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public int getCellWidth() {
        return cellBorderTexture.getWidth();
    }

    public int getCellHeight() {
        return cellBorderTexture.getHeight();
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public Point getPointForCoordinateWithOffset(Coordinates sourceCoordinates) {
        InputController controller = GameScreen.getInstance().getController();
        int x = (int) (
//         controller.getX_cam_pos()/2
                +sourceCoordinates.getX() * getCellWidth() / controller.getZoom());
        int y = (int) (
//         controller.getY_cam_pos()/2
                +(rows - sourceCoordinates.getY()) * getCellHeight() / controller.getZoom());
        return new PointX(x, y);
    }

    public GridPanel init() {
        setUnitMap(new HashMap<>());
        emptyImage = TextureManager.getOrCreate(emptyCellPath);
        hiddenImage = TextureManager.getOrCreate(hiddenCellPath);
        highlightImage = TextureManager.getOrCreate(highlightCellPath);
        unknownImage = TextureManager.getOrCreate(unknownCellPath);
        cellBorderTexture = TextureManager.getOrCreate(cellBorderPath);

        cells=(new GridCell[cols][rows]);

        setCellBorderManager(new CellBorderManager(emptyImage.getWidth(),
                emptyImage.getHeight()));
        int rows1 = rows - 1;
        int cols1 = cols - 1;
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y] = new GridCell(emptyImage, x, rows1 - y);
                cells[x][y].setX(x * emptyImage.getWidth());
                cells[x][y].setY(y * emptyImage.getHeight());
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


        addListener(new GridMouseListener(this,cells ));
        return this;
    }

    private void bindEvents() {
        GuiEventManager.bind(SELECT_MULTI_OBJECTS, obj -> {
            Pair<Set<DC_Obj>, TargetRunnable> p =
                    (Pair<Set<DC_Obj>, TargetRunnable>) obj.get();
            Map<Borderable, Runnable> map = new HashMap<>();
            for (DC_Obj obj1 : p.getLeft()) {
                Borderable b = getUnitMap().get(obj1);
                if (b == null)
                    b = cells[obj1.getX()][rows - 1 - obj1.getY()];
                map.put(b, () -> p.getRight().run(obj1));
            }
            GuiEventManager.trigger(SHOW_BLUE_BORDERS, new EventCallbackParam(map));
        });

        GuiEventManager.bind(INGAME_EVENT_TRIGGERED, param -> {
            main.game.event.Event event = (main.game.event.Event) param.get();
            Ref r = event.getRef();
            boolean caught = false;

            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED
                    || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_CLOCKWISE
                    || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_ANTICLOCKWISE)
//                (r.getEffect() instanceof ChangeFacingEffect) nice try
            {
                DC_HeroObj hero = (DC_HeroObj) r.getObj(KEYS.TARGET
                );
                UnitView unitView = getUnitMap().get(hero);
                unitView.updateRotation(hero.getFacing().getDirection().getDegrees());
                caught = true;
            }

            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED
                    || event.getType() == STANDARD_EVENT_TYPE.UNIT_BEING_MOVED) {
                removeUnitView(r.getSourceObj());
                caught = true;
            }

            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING) {
                addUnitView(r.getSourceObj());
                caught = true;
            }
            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_SUMMONED) {
                addUnitView(r.getObj(KEYS.SUMMONED));
                caught = true;
            }

            if (event.getType().name().startsWith("PARAM_BEING_MODIFIED")) {
                caught = true;
                /*switch (event.getType().getArg()) {
                    case "Spellpower":
                        int a = 10;
                        break;
                }*/
            }
            if (event.getType().name().startsWith("PROP_"))
                caught = true;
            if (event.getType().name().startsWith("ABILITY_"))
                caught = true;
            if (event.getType().name().startsWith("EFFECT_"))
                caught = true;

            if (event.getType().name().startsWith("PARAM_MODIFIED")) {
                switch (event.getType().getArg()) {
                    case "Illumination":
                        if (getLightmap() != null) {
                            Obj o = event.getRef().getTargetObj();
                            if (o instanceof DC_HeroObj) {
                                getLightmap().updateObject((DC_HeroObj) event.getRef().getTargetObj());
                            }
                        }
                        caught = true;
                        break;
                }

                caught = true;
            }

            if (!caught) {
                System.out.println("catch ingame event: " + event.getType() + " in " + event.getRef());
            }
        });

        GuiEventManager.bind(ACTIVE_UNIT_SELECTED, obj -> {
            DC_HeroObj hero = (DC_HeroObj) obj.get();
            UnitView view = getUnitMap().get(hero);
            if (view.getParent() != null) {
                ((GridCellContainer) view.getParent()).popupUnitView(view);
            }
            if (hero.isMine()) {
                GuiEventManager.trigger(SHOW_GREEN_BORDER, new EventCallbackParam(view));
            } else {
                GuiEventManager.trigger(SHOW_RED_BORDER, new EventCallbackParam(view));
            }
        });

        GuiEventManager.bind(CREATE_UNITS_MODEL, param -> {
            units = (DequeImpl<DC_HeroObj>) param.get();

            setLightmap(new Lightmap(units, cells[0][0].getWidth(), cells[0][0].getHeight(), rows));

            Map<Coordinates, List<DC_HeroObj>> map = new HashMap<>();
            for (DC_HeroObj object : units) {
                Coordinates c = object.getCoordinates();
                if (!map.containsKey(c)) {
                    map.put(c, new ArrayList<>());
                }
                List<DC_HeroObj> list = map.get(c);
                list.add(object);
            }

            for (Coordinates coordinates : map.keySet()) {
                List<UnitViewOptions> options = new ArrayList<>();

                for (DC_HeroObj object : map.get(coordinates)) {
                    options.add(new UnitViewOptions(object, getUnitMap()));
                }

                GridCellContainer cellContainer = new GridCellContainer(emptyImage, coordinates.getX(), coordinates.getY()).init();
                cellContainer.setObjects(options);

                cells[coordinates.getX()][rows - 1 - coordinates.getY()].addInnerDrawable(cellContainer);
            }
        });

        GuiEventManager.bind(CELL_UPDATE, param -> {
            Coordinates cords = (Coordinates) param.get();

            List<DC_HeroObj> objList = units.stream()
                    .filter(microObj -> microObj.getCoordinates().equals(cords))
                    .collect(Collectors.toList());

            List<UnitViewOptions> options = new ArrayList<>();
            for (DC_HeroObj microObj : objList) {
                options.add(new UnitViewOptions(microObj, getUnitMap()));
            }

            if (options.size() == 0) {
                cells[cords.getX()][cords.getY()].addInnerDrawable(null);
            } else {
                GridCellContainer cellContainer = new GridCellContainer(cellBorderTexture, cords.getX(), cords.getY()).init();
                cellContainer.setObjects(options);

                if (cells[cords.getX()][cords.getY()].getInnerDrawable() != null) {
                    cells[cords.getX()][cords.getY()].addInnerDrawable(cellContainer);
                } else {
                    cells[cords.getX()][cords.getY()].updateInnerDrawable(cellContainer);
                }
            }

        });


    }

    private void addUnitView(Obj heroObj) {
        int rows1 = rows - 1;
        UnitView uv = getUnitMap().get(heroObj);
        if (uv == null) {
//            main.system.auxiliary.LogMaster.log(1," " );
            return;
        }
        Coordinates c = heroObj.getCoordinates();
        if (cells[c.x][rows1 - c.y].getInnerDrawable() == null) {
            GridCellContainer cellContainer = new GridCellContainer(cells[c.x][rows1 - c.y]).init();
            cells[c.x][rows1 - c.y].addInnerDrawable(cellContainer);
        }
        uv.setVisible(true);
        cells[c.x][rows1 - c.y].getInnerDrawable().addActor(uv);

        getCellBorderManager().updateBorderSize();

        if (getLightmap() != null) {
            getLightmap().updatePos((MicroObj) heroObj);
        }
    }

    private void removeUnitView(Obj obj) {
        UnitView uv = getUnitMap().get(obj);
        uv.getParent().removeActor(uv);
        uv.setVisible(false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //physx.getUnit(unit).setTransform(getX(),getY());
    }

    public Actor hitChildren(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() != Touchable.enabled) return null;
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y].draw(batch, parentAlpha);
            }
        }

        getCellBorderManager().draw(batch, parentAlpha);

        if (getLightmap() != null) {
            getLightmap().updateLight();
        }
    }


    public Lightmap getLightmap() {
        return lightmap;
    }

    public void setLightmap(Lightmap lightmap) {
        this.lightmap = lightmap;
    }

    public CellBorderManager getCellBorderManager() {
        return cellBorderManager;
    }

    public void setCellBorderManager(CellBorderManager cellBorderManager) {
        this.cellBorderManager = cellBorderManager;
    }

    public Map<DC_HeroObj, UnitView> getUnitMap() {
        return unitMap;
    }

    public void setUnitMap(Map<DC_HeroObj, UnitView> unitMap) {
        this.unitMap = unitMap;
    }
}
