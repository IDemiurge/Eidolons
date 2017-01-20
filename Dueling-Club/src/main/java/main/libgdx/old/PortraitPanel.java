package main.libgdx.old;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created with IntelliJ IDEA.
 * Date: 24.10.2016
 * Time: 23:24
 * To change this template use File | Settings | File Templates.
 */
public class PortraitPanel extends Group {

    private static final String borderImagePath = "\\UI\\components\\Border New.png";
    private Image portrait;
    private Image border;
    private String imagePath;

    public PortraitPanel(String imagePath) {
        this.imagePath = imagePath;
    }

    public PortraitPanel init() {
        border = new Image(new Texture(imagePath + borderImagePath));
        addActor(border);
        setHeight(border.getHeight());
        setWidth(border.getWidth());
        return this;
    }
}
