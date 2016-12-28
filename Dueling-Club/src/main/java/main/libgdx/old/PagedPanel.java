package main.libgdx.old;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created with IntelliJ IDEA.
 * Date: 24.10.2016
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public class PagedPanel extends Group {
    private Image pager1;
    protected Image pager2;

    protected Image[] slots;

    private Texture emptySlotTexture;

    protected String imagePath;
    protected final int col;
    protected final int row;

    private static final String pagerImagePath = "\\UI\\components\\left5.png";
    private static final String pagerImagePath2 = "\\UI\\components\\right5.png";
    private static final String pagerImagePath3 = "\\UI\\components\\down5.png";
    private static final String pagerImagePath4 = "\\UI\\components\\up5.png";

    public PagedPanel(String imagePath, int col, int row) {
        this.imagePath = imagePath;
        this.col = col;
        this.row = row;
        slots = new Image[col * row];
    }

    protected String getEmptySlotImagePath() {
        return imagePath + "\\UI\\EMPTY_ITEM.jpg";
    }

    protected float getCellScale() {
        return 0.5f;
    }

    protected boolean isHorizontal() {
        return true;
    }

    public PagedPanel init() {
        if (getCellScale() != 1) {
            //make empty image little smaller
            //if use image scale we must calc real w&h when we call it
            //example
            // Image i = new Image(new Texture(200x200.png));
            // i.scale(0.5,0.5);
            // i.w == 200, not 100!!!!
            //scale only apply when image drawed
            Pixmap pixmapOr = new Pixmap(new FileHandle(getEmptySlotImagePath()));
            Pixmap pixmapTar = new Pixmap((int) (pixmapOr.getWidth() * getCellScale()), (int) (pixmapOr.getHeight() * getCellScale()), pixmapOr.getFormat());
            pixmapTar.drawPixmap(pixmapOr, 0, 0, pixmapOr.getWidth(), pixmapOr.getHeight(), 0, 0, pixmapTar.getWidth(), pixmapTar.getHeight());
            emptySlotTexture = new Texture(pixmapTar);
            pixmapOr.dispose();
            pixmapTar.dispose();
        } else {
            emptySlotTexture = new Texture(getEmptySlotImagePath());
        }

        if (isHorizontal()) {
            pager1 = new Image(new Texture(imagePath + pagerImagePath));
            pager2 = new Image(new Texture(imagePath + pagerImagePath2));
        } else {
            pager1 = new Image(new Texture(imagePath + pagerImagePath3));
            pager2 = new Image(new Texture(imagePath + pagerImagePath4));
        }

        //pager2.rotateBy(180);
        addActor(pager1);
        addActor(pager2);

        for (int j = 0; j < row; j++) {
            for (int i = 0; i < col; i++) {
                Image im = new Image(emptySlotTexture);
                if (isHorizontal()) {
                    im.setX(pager1.getWidth() + i * im.getWidth());
                    im.setY(j * im.getHeight());
                } else {
                    im.setX(i * im.getWidth());
                    im.setY(pager2.getHeight() + j * im.getHeight());
                }
                slots[i] = im;
                addActor(im);
            }
        }

        if (isHorizontal()) {
            pager2.setX(pager1.getWidth() + slots[0].getWidth() * col);
            setWidth(pager1.getWidth() + slots[0].getWidth() * col + pager2.getWidth());
            setHeight(slots[0].getHeight() * row);
            pager1.setY(getHeight() / 2 - pager1.getHeight() / 2);
            pager2.setY(getHeight() / 2 - pager2.getHeight() / 2);
        } else {
            pager2.setY(pager1.getHeight() + slots[0].getHeight() * row);
            setHeight(pager1.getHeight() + slots[0].getHeight() * row + pager2.getHeight());
            setWidth(slots[0].getWidth() * col);
            pager1.setX(getWidth() / 2 - pager1.getWidth() / 2);
            pager2.setX(getWidth() / 2 - pager2.getWidth() / 2);
        }

        //drawRect(pager1);
//        drawRect(this);
        return this;
    }

    @Override
    public boolean addListener(EventListener listener) {
        return super.addListener(listener);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
