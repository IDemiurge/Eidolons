package main.libgdx.old;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */
public class ActivePanelUnitName extends Group {

    private static final String backgroundImagePath = "\\UI\\bf\\name comp.png";
    private Image background;
    //private Label text;
    private String imagePath;

    public ActivePanelUnitName(String imagePath) {
        this.imagePath = imagePath;
    }

    public ActivePanelUnitName init() {
        background = new Image(new Texture(imagePath + backgroundImagePath));
        setX(0);
        setY(0);
        addActor(background);
        setWidth(background.getWidth());
        setHeight(background.getHeight());
        return this;
    }
}
