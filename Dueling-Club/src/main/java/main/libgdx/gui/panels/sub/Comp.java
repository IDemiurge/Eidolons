package main.libgdx.gui.panels.sub;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.libgdx.texture.TextureManager;

/**
 * Created by JustMe on 1/6/2017.
 */
public class Comp extends Group {
    //tooltips,
    Texture texture;

    public Comp(String imagePath) {
        texture = TextureManager.getOrCreate(imagePath);
    }
}
