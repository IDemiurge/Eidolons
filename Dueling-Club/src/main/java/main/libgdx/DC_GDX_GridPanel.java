package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import main.ability.effects.ChangeFacingEffect;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.MicroObj;
import main.game.Game;
import main.game.battlefield.Coordinates;
import main.game.event.Event;
import main.system.EventCallbackParam;
import main.system.TempEventManager;
import main.system.datatypes.DequeImpl;
import main.test.libgdx.prototype.LightmapTest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_GridPanel extends Group {
    protected GridCell[][] cells;
    protected Texture emptyImage;
    protected Texture hiddenImage;
    protected Texture highlightImage;
    protected Texture unknownImage;
    protected Texture cellBorderTexture;
    protected LightmapTest lightmap;
    protected DequeImpl<MicroObj> units;

    protected CellBorderManager cellBorderManager;
    protected TextureCache textureCache;
    protected Map<DC_HeroObj, UnitView> unitMap;

    private static final String backgroundPath = "UI/custom/grid/GRID_BG_WIDE.png";
    private static final String emptyCellPath = "UI/cells/Empty Cell v3.png";
    private static final String hiddenCellPath = "UI/cells/Hidden Cell v2.png";
    private static final String highlightCellPath = "UI/cells/Highlight Green Cell v3.png";
    private static final String unknownCellPath = "UI/cells/Unknown Cell v2.png";
    private static final String cellBorderPath = "UI\\CELL for 96.png";

    private DC_GDX_RadialMenu radialMenu = null;

    private String imagePath;
    private int cols;
    private int rows;

    public DC_GDX_GridPanel(String imagePath, int cols, int rows) {
        this.imagePath = imagePath;
        this.cols = cols;
        this.rows = rows;
    }

    public DC_GDX_GridPanel init() {
        emptyImage = new Texture(imagePath + File.separator + emptyCellPath);
        hiddenImage = new Texture(imagePath + File.separator + hiddenCellPath);
        highlightImage = new Texture(imagePath + File.separator + highlightCellPath);
        unknownImage = new Texture(imagePath + File.separator + unknownCellPath);
        cellBorderTexture = new Texture(imagePath + File.separator + cellBorderPath);
        textureCache = new TextureCache(imagePath);

        cellBorderManager = new CellBorderManager(emptyImage.getWidth(), emptyImage.getHeight(), textureCache);

        unitMap = new HashMap<>();

        cells = new GridCell[cols][rows];

        for (int x = 0; x < cols; x++) {
            for (int y = rows - 1; y >= 0; y--) {
                cells[x][y] = new GridCell(emptyImage, imagePath, x, y);
                cells[x][y].setX(x * emptyImage.getWidth());
                cells[x][y].setY(y * emptyImage.getHeight());
                addActor(cells[x][y].init());
            }
        }

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
                if (cells[c.x][c.y].getInnerDrawable() == null) {
                    GridCellContainer cellContainer = new GridCellContainer(cells[c.x][c.y]).init();
                    cells[c.x][c.y].addInnerDrawable(cellContainer);
                }
                uv.setVisible(true);
                cells[c.x][c.y].getInnerDrawable().addActor(uv);

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

            if (event.getType().name().startsWith("PARAM_MODIFIED")) {
                switch (event.getType().getArg()) {
                    case "Illumination":
                        lightmap.updateObject((DC_HeroObj) event.getRef().getTargetObj());
                        catched = true;
                        break;
                }
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
            units = (DequeImpl<MicroObj>) param.get();
            //lightmap = new LightmapTest(units, cells[0][0].getWidth(), cells[0][0].getHeight());
            Map<Coordinates, List<MicroObj>> map = new HashMap<>();
            for (MicroObj object : units) {
                Coordinates c = object.getCoordinates();
                if (!map.containsKey(c)) {
                    map.put(c, new ArrayList<>());
                }
                List<MicroObj> list = map.get(c);
                list.add(object);
            }

            for (Coordinates coordinates : map.keySet()) {
                List<UnitViewOptions> options = new ArrayList<>();

                for (MicroObj object : map.get(coordinates)) {
                    options.add(new UnitViewOptions(object, textureCache, unitMap));
                }

                GridCellContainer cellContainer = new GridCellContainer(emptyImage, imagePath, coordinates.getX(), coordinates.getY()).init();
                cellContainer.setObjects(options);

                cells[coordinates.getX()][coordinates.getY()].addInnerDrawable(cellContainer);
            }
        });

        TempEventManager.bind("cell-update", param -> {
            Coordinates cords = (Coordinates) param.get();

            List<MicroObj> objList = units.stream()
                    .filter(microObj -> microObj.getCoordinates().equals(cords))
                    .collect(Collectors.toList());

            List<UnitViewOptions> options = new ArrayList<>();
            for (MicroObj microObj : objList) {
                options.add(new UnitViewOptions(microObj, textureCache, unitMap));
            }

            if (options.size() == 0) {
                cells[cords.getX()][cords.getY()].addInnerDrawable(null);
            } else {
                GridCellContainer cellContainer = new GridCellContainer(cellBorderTexture, imagePath, cords.getX(), cords.getY()).init();
                cellContainer.setObjects(options);

                if (cells[cords.getX()][cords.getY()].getInnerDrawable() != null) {
                    cells[cords.getX()][cords.getY()].addInnerDrawable(cellContainer);
                } else {
                    cells[cords.getX()][cords.getY()].updateInnerDrawable(cellContainer);
                }
            }

/*                physx.getUnit(unit).addXY(x,y);
            MoveToAction moveToAction = Actions.moveTo(1,1,1);
            moveToAction.
            addAction(Actions.moveTo());*/
        });
        /*
        LIGHT_EMISSION
         ILLUMINATION
           CONCEALMENT*/

        setHeight(cells[0][0].getHeight() * rows);
        setWidth(cells[0][0].getWidth() * cols);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor a;
                if (radialMenu != null) {
                    Vector2 v = new Vector2(x, y);
                    v = parentToLocalCoordinates(v);
//                    a = radialMenu.hit(v.x - radialMenu.getX(), v.y - radialMenu.getY(), true);
                    a = radialMenu.hit(x, y, true);
                    if (a != null && a instanceof DC_GDX_RadialMenu.MenuNode) {
                        DC_GDX_RadialMenu.MenuNode node = (DC_GDX_RadialMenu.MenuNode) a;
                        node.action.run();
                        return true;
                    }
                }

                a = DC_GDX_GridPanel.super.hit(x, y, true);
                if (a != null && a instanceof GridCell) {
                    GridCell cell = (GridCell) a;
                   /* if (cell.getInnerDrawable() != null) {
                        Actor unit = cell.getInnerDrawable().hit(x, y, true);
                        if (unit != null && unit instanceof UnitView) {
                            ((GridCellContainer) cell.getInnerDrawable()).onClick(unit, event);
                        }
                    }*/
                    if (event.getButton() == 0) {
/*                        greenBorder.setX(cell.getX() - 5);
                        greenBorder.setY(cell.getY() - 5);
                        greenBorder.setVisible(true);*/
                    } else if (event.getButton() == 1) {
                        createRadialMenu(x, y);
                    }
                }

                return false;
            }
        });


        return this;
    }

    private void createRadialMenu(float x, float y) {
        if (radialMenu != null) {
            radialMenu = null;//dispose if required;
        }

        radialMenu = DC_GDX_RadialMenu.create(x, y, textureCache);
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

        if (radialMenu != null) {
            radialMenu.draw(batch, parentAlpha);
        }

        if (lightmap != null) {
            lightmap.updateLight();
        }
    }
}
