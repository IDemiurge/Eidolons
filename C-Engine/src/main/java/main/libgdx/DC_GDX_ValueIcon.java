package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created with IntelliJ IDEA.
 * Date: 30.10.2016
 * Time: 0:03
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_ValueIcon extends Group {

    private String imagePath;
    private Texture backTexture;
    private Image backImage;
    private Image iconImage;

    public DC_GDX_ValueIcon(String valueIconImagePath, Texture backTexture) {
        this.imagePath = valueIconImagePath;
        this.backTexture = backTexture;
    }

    public DC_GDX_ValueIcon init() {
        backImage = new Image(backTexture);
        iconImage = new Image(new Texture(imagePath));

        iconImage.setY(15);

        addActor(backImage);
        addActor(iconImage);

        setHeight(backImage.getHeight());
        setWidth(backImage.getWidth());
        return this;
    }
}
