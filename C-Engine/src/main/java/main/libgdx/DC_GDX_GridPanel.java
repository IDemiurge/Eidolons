package main.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.entity.Entity;
import main.entity.obj.ActiveObj;
import main.entity.obj.MicroObj;
import main.game.Game;
import main.game.battlefield.Coordinates;
import main.system.datatypes.DequeImpl;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.*;

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
    protected Image greenBorder;
    protected Image redBorder;
    protected DequeImpl<MicroObj> units;
    private Map<String, Texture> textureMap = new HashMap<>();

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

    private Texture getOreCreate(String path) {
        if (!textureMap.containsKey(path)) {
            textureMap.put(path, new Texture(path));
        }
        return textureMap.get(path);
    }

    public DC_GDX_GridPanel init() {
        emptyImage = new Texture(imagePath + File.separator + emptyCellPath);
        hiddenImage = new Texture(imagePath + File.separator + hiddenCellPath);
        highlightImage = new Texture(imagePath + File.separator + highlightCellPath);
        unknownImage = new Texture(imagePath + File.separator + unknownCellPath);
        cellBorderTexture = new Texture(imagePath + File.separator + cellBorderPath);
        cells = new GridCell[cols][rows];

        for (int x = 0; x < cols; x++) {
            for (int y = rows - 1; y >= 0; y--) {
                cells[x][y] = new GridCell(emptyImage, imagePath, x, y);
                cells[x][y].setX(x * emptyImage.getWidth());
                cells[x][y].setY(y * emptyImage.getHeight());
                addActor(cells[x][y].init());
            }
        }

        TempEventManager.bind("create-units-model", new EventCallback() {
            @Override
            public void call(final Object obj) {
                units = (DequeImpl<MicroObj>) obj;

                Map<Coordinates, List<MicroObj>> map = new HashMap<>();
                for (MicroObj object : units) {
                    Coordinates c = object.getCoordinates();
                    if (!map.containsKey(c)) {
                        map.put(c, new ArrayList<MicroObj>());
                    }
                    List<MicroObj> list = map.get(c);
                    list.add(object);
                }

                for (Coordinates coordinates : map.keySet()) {
                    List<Texture> textures = new ArrayList<>();

                    for (Entity object : map.get(coordinates)) {
                        String path = imagePath + File.separator + object.getImagePath();
                        textures.add(getOreCreate(path));
                    }

                    GridCellContainer cellContainer = new GridCellContainer(cellBorderTexture, imagePath, coordinates.getX(), coordinates.getY()).init();
                    cellContainer.setObjects(textures);

                    cells[coordinates.getX()][coordinates.getY()].addInnerDrawable(cellContainer);
                }
            }
        });

        TempEventManager.bind("cell-update", new EventCallback() {
            @Override
            public void call(Object obj) {
                Coordinates cords = (Coordinates) obj;

                List<MicroObj> objList = new ArrayList<>();
                for (MicroObj unit : units) {
                    if (unit.getCoordinates().equals(cords)) {
                        objList.add(unit);
                    }
                }

                List<Texture> textures = new ArrayList<>();
                for (MicroObj microObj : objList) {
                    String path = imagePath + File.separator + microObj.getImagePath();
                    textures.add(getOreCreate(path));
                }
                if (textures.size() == 0) {
                    cells[cords.getX()][cords.getY()].addInnerDrawable(null);
                } else {
                    GridCellContainer cellContainer = new GridCellContainer(cellBorderTexture, imagePath, cords.getX(), cords.getY()).init();
                    cellContainer.setObjects(textures);

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
            }
        });
        /*
        LIGHT_EMISSION
         ILLUMINATION
           CONCEALMENT*/

        setHeight(cells[0][0].getHeight() * rows);
        setWidth(cells[0][0].getWidth() * cols);

        greenBorder = new Image(getColoredBorderTexture(Color.GREEN));
        greenBorder.setVisible(false);
        redBorder = new Image(getColoredBorderTexture(Color.RED));
        redBorder.setVisible(false);

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
                    if (event.getButton() == 0) {
                        greenBorder.setX(cell.getX() - 5);
                        greenBorder.setY(cell.getY() - 5);
                        greenBorder.setVisible(true);
                    } else if (event.getButton() == 1) {
                        createRadialMenu(x, y);
                    }
                }

                return false;
            }
        });


        return this;
    }

    private static List<DC_GDX_RadialMenu.CreatorNode> creatorNodes(final String name, Texture t) {
        List<DC_GDX_RadialMenu.CreatorNode> nn1 = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            DC_GDX_RadialMenu.CreatorNode inn1 = new DC_GDX_RadialMenu.CreatorNode();
            inn1.texture = t;
            final int finalI = i;
            inn1.action = new Runnable() {
                @Override
                public void run() {
                    System.out.println(name + finalI);
                }
            };
            nn1.add(inn1);
        }
        return nn1;
    }

    private static List<DC_GDX_RadialMenu.CreatorNode> creatorNodes(List<Pair<ActiveObj, Texture>> pairs) {
        List<DC_GDX_RadialMenu.CreatorNode> nn1 = new ArrayList<>();
        for (final Pair<ActiveObj, Texture> pair : pairs) {
            DC_GDX_RadialMenu.CreatorNode inn1 = new DC_GDX_RadialMenu.CreatorNode();
            inn1.texture = pair.getRight();
            inn1.action = new Runnable() {
                @Override
                public void run() {
                    ((Entity) pair.getLeft()).invokeClicked();
                }
            };
            nn1.add(inn1);
        }
        return nn1;
    }

    private void createRadialMenu(float x, float y) {
        if (radialMenu != null) {
            radialMenu = null;//dispose if required;
        }

        MicroObj activeObj = (MicroObj) Game.game.getManager().getActiveObj();

        List<ActiveObj> activeObjs = activeObj.getActives();

        List<Pair<ActiveObj, Texture>> moves = new ArrayList<>();
        List<Pair<ActiveObj, Texture>> turns = new ArrayList<>();

        for (ActiveObj obj : activeObjs) {
            if (obj.isMove()) {
                moves.add(new ImmutablePair<>(obj, new Texture(imagePath + File.separator + ((Entity) obj).getImagePath())));
                //add this filter later
                //obj.getTargeting().getFilter().getObjects().contains(Game.game.getCellByCoordinate(new Coordinates(0, 0)));
            }
            if (obj.isTurn()) {
                turns.add(new ImmutablePair<>(obj, new Texture(imagePath + File.separator + ((Entity) obj).getImagePath())));

            }

/*
            Entity e = ((Entity) obj);
            obj.isAttack();
            obj.getTargeting() instanceof SelectiveTargeting;
            obj.getTargeting().getFilter().getObjects().contains(Game.game.getCellByCoordinate(new Coordinates(0, 0)));
            obj.isMove();
            obj.isTurn();
            ((Entity) obj).getImagePath();*/

        }


        Texture moveAction = new Texture(imagePath + "\\UI\\actions\\Move gold.jpg");
        Texture turnAction = new Texture(imagePath + "\\UI\\actions\\turn anticlockwise quick2 - Copy.jpg");
        Texture yellow = new Texture(DC_GDX_GridPanel.class.getResource("/data/marble_yellow.png").getPath());
        Texture red = new Texture(DC_GDX_GridPanel.class.getResource("/data/marble_red.png").getPath());
        Texture green = new Texture(DC_GDX_GridPanel.class.getResource("/data/marble_green.png").getPath());

        DC_GDX_RadialMenu.CreatorNode n1 = new DC_GDX_RadialMenu.CreatorNode();
        n1.texture = moveAction;
        n1.childNodes = creatorNodes(moves);

        DC_GDX_RadialMenu.CreatorNode n2 = new DC_GDX_RadialMenu.CreatorNode();
        n2.texture = turnAction;
        n2.childNodes = creatorNodes(turns);

        DC_GDX_RadialMenu.CreatorNode n3 = new DC_GDX_RadialMenu.CreatorNode();
        n3.texture = yellow;
        n3.childNodes = creatorNodes("nn3:", red);

        DC_GDX_RadialMenu.CreatorNode n4 = new DC_GDX_RadialMenu.CreatorNode();
        n4.texture = yellow;
        n4.action = new Runnable() {
            @Override
            public void run() {

//                activeObj.invokeClicked();
            }
        };
        n4.childNodes = creatorNodes("nn4:", red);

        radialMenu = new DC_GDX_RadialMenu(green, Arrays.asList(n1, n2, n3, n4));

        radialMenu.setX(x - radialMenu.getWidth() / 2);
        radialMenu.setY(y - radialMenu.getHeight() / 2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //physx.getUnit(unit).setTransform(getX(),getY());
    }

    private Texture getColoredBorderTexture(Color c) {
        Pixmap p = new Pixmap(emptyImage.getWidth() + 10, emptyImage.getHeight() + 10, Pixmap.Format.RGBA8888);
        p.setColor(c);
        p.drawRectangle(5, 5, p.getWidth() - 10, p.getHeight() - 10);
        p.drawRectangle(6, 6, p.getWidth() - 12, p.getHeight() - 12);
        p = BlurUtils.blur(p, 3, 1, true);
        p.setColor(c);
        p.drawRectangle(5, 5, p.getWidth() - 10, p.getHeight() - 10);
        p.drawRectangle(6, 6, p.getWidth() - 12, p.getHeight() - 12);
        p = BlurUtils.blur(p, 2, 2, true);
        p.setColor(c);
        p.drawRectangle(5, 5, p.getWidth() - 10, p.getHeight() - 10);
        p = BlurUtils.blur(p, 1, 1, true);
        Texture t = new Texture(p);
        p.dispose();
        return t;
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
        if (redBorder.isVisible()) {
            redBorder.draw(batch, parentAlpha);
        }
        if (greenBorder.isVisible()) {
            greenBorder.draw(batch, parentAlpha);
        }

        if (radialMenu != null && radialMenu.isVisible()) {
            radialMenu.draw(batch, parentAlpha);
        }
    }
}
