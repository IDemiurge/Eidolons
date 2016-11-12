package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.io.File;

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

    private static final String backgroundPath = "UI/custom/grid/GRID_BG_WIDE.png";
    private static final String emptyCellPath = "UI/cells/Empty Cell v3.png";
    private static final String hiddenCellPath = "UI/cells/Hidden Cell v2.png";
    private static final String highlightCellPath = "UI/cells/Highlight Green Cell v3.png";
    private static final String unknownCellPath = "UI/cells/Unknown Cell v2.png";

    private String imagePath;
    private int lines;
    private int rows;

    public DC_GDX_GridPanel(String imagePath, int lines, int rows) {
        this.imagePath = imagePath;
        this.lines = lines;
        this.rows = rows;
    }

    public DC_GDX_GridPanel init() {
        emptyImage = new Texture(imagePath + File.separator + emptyCellPath);
        hiddenImage = new Texture(imagePath + File.separator + hiddenCellPath);
        highlightImage = new Texture(imagePath + File.separator + highlightCellPath);
        unknownImage = new Texture(imagePath + File.separator + unknownCellPath);
        cells = new GridCell[lines][rows];
        for (int i = 0; i < lines; i++) {
            for (int i1 = 0; i1 < rows; i1++) {
                cells[i][i1] = new GridCell(imagePath, emptyImage);
                cells[i][i1].setX(i * emptyImage.getWidth());
                cells[i][i1].setY(i1 * emptyImage.getHeight());
            }
        }

        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (int i = 0; i < lines; i++) {
            for (int i1 = 0; i1 < rows; i1++) {
                cells[i][i1].draw(batch, parentAlpha);
            }
        }
    }
}
