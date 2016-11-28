package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created with IntelliJ IDEA.
 * Date: 30.10.2016
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_PagedPriorityPanel extends DC_GDX_PagedPanel {

    private String clockImagePath = "\\UI\\custom\\Time.JPG";
    private Image clockImage;

    public DC_GDX_PagedPriorityPanel(String imagePath, int col, int row) {
        super(imagePath, col, row);
    }

    @Override
    protected float getCellScale() {
        return 0.5f;
    }

    @Override
    protected boolean isHorizontal() {
        return false;
    }

    public DC_GDX_PagedPriorityPanel init() {
        super.init();
        clockImage = new Image(new Texture(imagePath + clockImagePath));
        clockImage.setY(pager2.getY());
        addActor(clockImage);
        pager2.setY(pager2.getY() + clockImage.getHeight());
        return this;
    }
}
