package main.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.entity.Entity;
import main.entity.obj.MicroObj;
import main.game.battlefield.Coordinates;
import main.system.datatypes.DequeImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private static final String backgroundPath = "UI/custom/grid/GRID_BG_WIDE.png";
    private static final String emptyCellPath = "UI/cells/Empty Cell v3.png";
    private static final String hiddenCellPath = "UI/cells/Hidden Cell v2.png";
    private static final String highlightCellPath = "UI/cells/Highlight Green Cell v3.png";
    private static final String unknownCellPath = "UI/cells/Unknown Cell v2.png";
    private static final String cellBorderPath = "UI\\CELL for 96.png";

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
        cells = new GridCell[cols][rows];

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y] = new GridCell(emptyImage, imagePath, x, y);
                cells[x][y].setX(x * emptyImage.getWidth());
                cells[x][y].setY(y * emptyImage.getHeight());
                addActor(cells[x][y].init());
            }
        }

        TempEventManager.bind("create-units-model", new EventCallback() {
            @Override
            public void call(final Object obj) {
                DequeImpl<MicroObj> list = (DequeImpl<MicroObj>) obj;
                Map<Coordinates, List<MicroObj>> map = new HashMap<>();
                for (MicroObj object : list) {
                    Coordinates c = object.getCoordinates();
                    if (!map.containsKey(c)) {
                        map.put(c, new ArrayList<MicroObj>());
                    }
                    List<MicroObj> l = map.get(c);
                    l.add(object);
                }

                for (Coordinates coordinates : map.keySet()) {
                    List<Texture> textures = new ArrayList<>();
                    for (Entity object : map.get(coordinates)) {
                        String path = imagePath + File.separator + object.getImagePath();
                        textures.add(new Texture(path));
                    }
                    GridCellContainer cellContainer = new GridCellContainer(cellBorderTexture, imagePath, coordinates.getX(), coordinates.getY()).init();
                    cellContainer.setObjects(textures);

                    cells[coordinates.getX()][coordinates.getY()].addInnerDrawable(cellContainer);
                }
            }
        });

        setHeight(cells[0][0].getHeight() * rows);
        setWidth(cells[0][0].getWidth() * cols);

        greenBorder = new Image(getColoredBorderTexture(Color.GREEN));
        greenBorder.setVisible(false);
        redBorder = new Image(getColoredBorderTexture(Color.RED));
        redBorder.setVisible(false);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GridCell cell = (GridCell) DC_GDX_GridPanel.super.hit(x, y, true);
                if (cell != null) {
                    greenBorder.setX(cell.getX() - 5);
                    greenBorder.setY(cell.getY() - 5);
                    greenBorder.setVisible(true);
                } else {
                    System.out.println("bbbb");
                }
            }
        });


        return this;
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

    private Lock lock = new ReentrantLock();

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
    }
}
