package main.libgdx.old;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public class QuickActionPagedPanel extends PagedPanel {

    private Image actionButton1;
    private Image actionButton2;


    public QuickActionPagedPanel(String imagePath, int col, int row) {
        super(imagePath, col, row);
    }

    protected String[] getActionButtonImagePaths() {
        return new String[]{imagePath + "\\UI\\components\\new\\inv.jpg", imagePath + "\\UI\\components\\new\\hammer.jpg"};
    }

    @Override
    protected float getCellScale() {
        return 0.8f;
    }

    @Override
    protected boolean isHorizontal() {
        return false;
    }

    @Override
    public QuickActionPagedPanel init() {
        super.init();

        actionButton1 = new Image(new Texture(getActionButtonImagePaths()[0]));
        actionButton2 = new Image(new Texture(getActionButtonImagePaths()[1]));

        actionButton1.setX(-actionButton2.getWidth() * .25f);
        actionButton1.setY(slots[0].getHeight() * row);

        actionButton2.setX(slots[0].getWidth() * col - actionButton2.getWidth() + actionButton2.getWidth() * .25f);
        actionButton2.setY(slots[0].getHeight() * row);

        addActor(actionButton1);
        addActor(actionButton2);
        return this;
    }
}
