package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_Background extends Image {

    private final static String imagePath = "big\\dungeon.jpg";

    public DC_GDX_Background(String path) {
        super(new Texture(path+imagePath));
        setX(0);
        setY(0);
        setScale(1,1);
    }
}
