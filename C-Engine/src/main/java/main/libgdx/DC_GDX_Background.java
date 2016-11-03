package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_Background extends Group {

//    private final static String backImagePath = "big\\dungeon.jpg";
    private final static String backImagePath = "big\\big bf grid test2.jpg";

    public Image backImage;

    private String imagePath;

    public DC_GDX_Background(String path) {
        imagePath = path;
    }

    public DC_GDX_Background init() {
        backImage = new Image(new Texture(imagePath + backImagePath));
        addActor(backImage);

        return this;
    }
}
