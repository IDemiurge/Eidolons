package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import main.ability.effects.ChangeFacingEffect;
import main.content.PARAMS;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.DC_Game;
import main.game.Game;
import main.game.battlefield.Coordinates;
import main.game.event.Event;
import main.libgdx.texture.TextureCache;
import main.system.EventCallbackParam;
import main.system.TempEventManager;
import main.system.datatypes.DequeImpl;
import main.test.libgdx.prototype.Lightmap;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public class GridPanel extends Group {
    protected GridCell[][] cells;
    protected Texture emptyImage;
    protected Texture hiddenImage;
    protected Texture highlightImage;
    protected Texture unknownImage;
    protected Texture cellBorderTexture;
    protected Lightmap lightmap;
    protected DequeImpl<DC_HeroObj> units;
    protected TextureCache textureCache;
    protected CellBorderManager cellBorderManager;


    protected Map<DC_HeroObj, UnitView> unitMap;

    private static final String backgroundPath = "UI/custom/grid/GRID_BG_WIDE.png";
    private static final String emptyCellPath = "UI/cells/Empty Cell v3.png";
    private static final String hiddenCellPath = "UI/cells/Hidden Cell v2.png";
    private static final String highlightCellPath = "UI/cells/Highlight Green Cell v3.png";
    private static final String unknownCellPath = "UI/cells/Unknown Cell v2.png";
    private static final String cellBorderPath = "UI\\CELL for 96.png";

    private int cols;
    private int rows;

    public GridPanel(TextureCache textureCache, int cols, int rows) {
        this.textureCache = textureCache;
        this.cols = cols;
        this.rows = rows;
    }

    public GridPanel init() {
        unitMap = new HashMap<>();
        emptyImage = textureCache.getOrCreate(emptyCellPath);
        hiddenImage = textureCache.getOrCreate(hiddenCellPath);
        highlightImage = textureCache.getOrCreate(highlightCellPath);
        unknownImage = textureCache.getOrCreate(unknownCellPath);
        cellBorderTexture = textureCache.getOrCreate(cellBorderPath);

        cells = new GridCell[cols][rows];

        cellBorderManager = new CellBorderManager(emptyImage.getWidth(), emptyImage.getHeight(), textureCache);
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

        TempEventManager.bind("select-multi-objects", obj -> {
            Pair<Set<DC_Obj>, TargetRunnable> p =
             (Pair<Set<DC_Obj>, TargetRunnable>) obj.get();
            Map<Borderable, Runnable> map = new HashMap<>();
            for (DC_Obj obj1 : p.getLeft()) {
                Borderable   b=unitMap.get(obj1);
                if (b==null )
                b=cells[obj1.getX()][rows1-obj1.getY()];
                map.put(b, () -> p.getRight().run(obj1));
            }
            TempEventManager.trigger("show-blue-borders", new EventCallbackParam(map));
        });

        TempEventManager.bind("ingame-event-triggered", param -> {
            main.game.event.Event event = (main.game.event.Event) param.get();
            Ref r = event.getRef();
            boolean catched = false;
            if (r.getEffect() instanceof ChangeFacingEffect) {
                ChangeFacingEffect ef = (ChangeFacingEffect) r.getEffect();
                DC_HeroObj hero = ((DC_HeroObj) ef.getActiveObj().getOwnerObj());
                UnitView unitView = unitMap.get(hero);
                unitView.updateRotation(hero.getFacing().getDirection().getDegrees());
                catched = true;
            }

            if (event.getType() == Event.STANDARD_EVENT_TYPE.UNIT_BEING_MOVED) {
                String id = event.getRef().getValue("SOURCE");
                DC_HeroObj heroObj = (DC_HeroObj) Game.game.getObjectById(Integer.valueOf(id));
                UnitView uv = unitMap.get(heroObj);
                uv.getParent().removeActor(uv);
                uv.setVisible(false);
                catched = true;
            }

            if (event.getType() == Event.STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING) {
                String id = event.getRef().getValue("SOURCE");
                DC_HeroObj heroObj = (DC_HeroObj) Game.game.getObjectById(Integer.valueOf(id));
                UnitView uv = unitMap.get(heroObj);
                Coordinates c = heroObj.getCoordinates();
                if (cells[c.x][rows1 - c.y].getInnerDrawable() == null) {
                    GridCellContainer cellContainer = new GridCellContainer(cells[c.x][rows1 - c.y]).init();
                    cells[c.x][rows1 - c.y].addInnerDrawable(cellContainer);
                }
                uv.setVisible(true);
                cells[c.x][rows1 - c.y].getInnerDrawable().addActor(uv);

                cellBorderManager.updateBorderSize();

                if (lightmap != null) {
                    lightmap.updatePos(heroObj);
                }
                catched = true;
            }

            if (event.getType().name().startsWith("PARAM_BEING_MODIFIED")) {
                catched = true;
                /*switch (event.getType().getArg()) {
                    case "Spellpower":
                        int a = 10;
                        break;
                }*/
            }
            if (event.getType().name().startsWith("PROP_")) {

                catched = true;
            }

            if (event.getType().name().startsWith("PARAM_MODIFIED")) {
                switch (event.getType().getArg()) {
                    case "Illumination":
                        if (lightmap != null) {
                            Obj o = event.getRef().getTargetObj();
                            if (o instanceof DC_HeroObj) {
                                lightmap.updateObject((DC_HeroObj) event.getRef().getTargetObj());
                            }
                        }
                        catched = true;
                        break;
                }

                catched = true;
            }

            if (!catched) {
                System.out.println("catch ingame event: " + event.getType() + " in " + event.getRef());
            }
        });

        TempEventManager.bind("active-unit-selected", obj -> {
            DC_HeroObj hero = (DC_HeroObj) obj.get();
            UnitView view = unitMap.get(hero);
            if (view.getParent() != null) {
                ((GridCellContainer) view.getParent()).popupUnitView(view);
            }
            if (hero.isMine()) {
                TempEventManager.trigger("show-green-border", new EventCallbackParam(view));
            } else {
                TempEventManager.trigger("show-red-border", new EventCallbackParam(view));
            }
        });

        TempEventManager.bind("create-units-model", param -> {
            units = (DequeImpl<DC_HeroObj>) param.get();

            lightmap = new Lightmap(units, cells[0][0].getWidth(), cells[0][0].getHeight());

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
                    options.add(new UnitViewOptions(object, textureCache, unitMap));
                }

                GridCellContainer cellContainer = new GridCellContainer(emptyImage, coordinates.getX(), coordinates.getY()).init();
                cellContainer.setObjects(options);

                cells[coordinates.getX()][rows1 - coordinates.getY()].addInnerDrawable(cellContainer);
            }
        });

        TempEventManager.bind("cell-update", param -> {
            Coordinates cords = (Coordinates) param.get();

            List<DC_HeroObj> objList = units.stream()
                    .filter(microObj -> microObj.getCoordinates().equals(cords))
                    .collect(Collectors.toList());

            List<UnitViewOptions> options = new ArrayList<>();
            for (DC_HeroObj microObj : objList) {
                options.add(new UnitViewOptions(microObj, textureCache, unitMap));
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
        /*
        LIGHT_EMISSION
         ILLUMINATION
           CONCEALMENT*/

        setHeight(cells[0][0].getHeight() * rows);
        setWidth(cells[0][0].getWidth() * cols);

        Map<String, String> tooltipStatMap = new HashMap<>();
        tooltipStatMap.put(PARAMS.C_TOUGHNESS.getName(), "Toughness");
        tooltipStatMap.put("C_Endurance", "Endurance");
        tooltipStatMap.put("C_N_Of_Actions", "N_Of_Actions");

        addListener(new InputListener() {

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                int cell = (int) (x / cells[0][0].getWidth());
                int row = (int) (y / cells[0][0].getHeight());
                GridCell gridCell = cells[cell][row];
                if (gridCell.getInnerDrawable() != null) {
                    GridCellContainer innerDrawable = (GridCellContainer) gridCell.getInnerDrawable();
                    Actor a = innerDrawable.hit(x, y, true);
                    if (a != null && a instanceof UnitView) {
                        UnitView uv = (UnitView) a;
                        DC_HeroObj hero = unitMap.entrySet().stream()
                                .filter(entry -> entry.getValue() == uv).findFirst()
                                .get().getKey();

                        List<ToolTipManager.ToolTipRecordOption> recordOptions = new ArrayList<>();

                        tooltipStatMap.entrySet().forEach(entry -> {
                            ToolTipManager.ToolTipRecordOption recordOption = new ToolTipManager.ToolTipRecordOption();
                            recordOption.curVal = hero.getIntParam(entry.getKey());
                            recordOption.maxVal = hero.getIntParam(entry.getValue());
                            recordOption.name = entry.getValue();
                            recordOption.recordImage = textureCache.getOrCreate("UI\\value icons\\" + entry.getValue().replaceAll("_", " ") + ".png");
                            recordOptions.add(recordOption);
                        });

                        ToolTipManager.ToolTipRecordOption recordOption = new ToolTipManager.ToolTipRecordOption();
                        recordOption.name = hero.getName();
                        recordOptions.add(0, recordOption);

                        recordOption = new ToolTipManager.ToolTipRecordOption();
                        recordOption.name = hero.getCoordinates().toString();
                        recordOptions.add(recordOption);

                        recordOption = new ToolTipManager.ToolTipRecordOption();
                        recordOption.name = "direction: " + hero.getFacing().getDirection();
                        recordOptions.add(recordOption);

                        if (hero.isOverlaying()){
                            recordOption = new ToolTipManager.ToolTipRecordOption();
                            recordOption.name = "direction O: " + hero.getDirection();
                            recordOptions.add(recordOption);
                        }

                        TempEventManager.trigger("show-tooltip", new EventCallbackParam(recordOptions));
                        return true;
                    }
                }
                TempEventManager.trigger("show-tooltip", new EventCallbackParam(null));
                return true;
            }

            /*
            Entity e = ((Entity) obj);
            obj.isAttack();
            obj.getTargeting() instanceof SelectiveTargeting;
            obj.getTargeting().getFilter().getObjects().contains(Game.game.getCellByCoordinate(new Coordinates(0, 0)));
            obj.isMove();
            obj.isTurn();
            ((Entity) obj).getImagePath();*/

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    return handleTouchDown(event, x, y, pointer, button);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            public boolean handleTouchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor a;
                a = GridPanel.super.hit(x, y, true);
                if (a != null && a instanceof GridCell) {
                    GridCell cell = (GridCell) a;
                    if (cellBorderManager.isBlueBorderActive() && event.getButton() == 0) {
                        Borderable b = cell;
                        if (cell.getInnerDrawable() != null) {
                            Actor unit = cell.getInnerDrawable().hit(x, y, true);
                            if (unit != null && unit instanceof Borderable) {
                                b = (Borderable) unit;
                            }
                        }
                        cellBorderManager.hitAndCall(b);
                    }


                    if (cell.getInnerDrawable() != null) {
                        Actor unit = cell.getInnerDrawable().hit(x, y, true);
                        if (unit != null && unit instanceof UnitView) {
                            if (event.getButton() == 1) {
                                DC_HeroObj heroObj = unitMap.entrySet()
                                        .stream().filter(entry -> entry.getValue() == unit).findFirst()
                                        .get().getKey();
                                Triple<DC_Obj, Float, Float> container = new ImmutableTriple<>(heroObj, x, y);
                                TempEventManager.trigger("create-radial-menu", new EventCallbackParam(container));
                            }
                        }
                    } else if (event.getButton() == 1) {
                        DC_Obj dc_cell = DC_Game.game.getCellByCoordinate(new Coordinates(cell.gridX, cell.gridY));
                        Triple<DC_Obj, Float, Float> container = new ImmutableTriple<>(dc_cell, x, y);
                        TempEventManager.trigger("create-radial-menu", new EventCallbackParam(container));
                    }
                }
                return false;
            }
        });


        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //physx.getUnit(unit).setTransform(getX(),getY());
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

        cellBorderManager.draw(batch, parentAlpha);

        if (lightmap != null) {
            lightmap.updateLight();
        }
    }
}
