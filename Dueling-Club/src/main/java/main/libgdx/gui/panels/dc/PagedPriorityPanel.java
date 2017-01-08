package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.gui.panels.generic.PagedListPanel;
import main.libgdx.texture.TextureManager;

/**
 * Created with IntelliJ IDEA.
 * Date: 30.10.2016
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
public class PagedPriorityPanel extends PagedListPanel {

    private String clockImagePath = "\\UI\\custom\\Time.JPG";
    private Image clockImage;

    public PagedPriorityPanel(String imagePath, int col, int row) {
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

    public PagedListPanel init() {
        super.init();
        clockImage = new Image(TextureManager.getOrCreate(clockImagePath));
        clockImage.setY(pager2.getY());
        addActor(clockImage);
        pager2.setY(pager2.getY() + clockImage.getHeight());
        return this;
    }
}
