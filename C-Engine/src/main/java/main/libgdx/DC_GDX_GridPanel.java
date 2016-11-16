package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_GridPanel extends Group {

//    protected Sprite[][] background;
    protected Image[][] background;
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
        background = new Image[lines][rows];
//        Texture backTexture = new Texture(imagePath + File.separator + backgroundPath);
        for (int i = 0; i < lines; i++) {
            for (int i1 = 0; i1 < rows; i1++) {
//                background[i][i1] = new Sprite(emptyImage);
//                background[i][i1].setX(i * emptyImage.getWidth());
//                background[i][i1].setY(i1 * emptyImage.getHeight());
                background[i][i1] = new Image(emptyImage);
                background[i][i1].setBounds(i * 5,i1 * 5,5,5);
                addActor(background[i][i1]);
//                background[i][i1].setX(i * emptyImage.getWidth());
//                background[i][i1].setY(i1 * emptyImage.getHeight());
            }
        }

        setBounds(0,0,5*rows,5*lines);

        return this;
    }

//    public void draw(SpriteBatch batch, float alpha) {
//        for (int i = 0; i < lines; i++) {
//            for (int i1 = 0; i1 < rows; i1++) {
//                background[i][i1].draw(batch, alpha);
//
//
//            }
//        }
//    }

//    @Override
//    public void draw(Batch batch, float parentAlpha) {
////        for (int i = 0; i < lines; i++) {
////            for (int i1 = 0; i1 < rows; i1++) {
////                background[i][i1].draw(batch,parentAlpha);
////            }
////        }
//        super.draw(batch, parentAlpha);
//    }
}
