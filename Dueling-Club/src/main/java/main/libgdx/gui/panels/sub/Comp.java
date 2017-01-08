package main.libgdx.gui.panels.sub;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.texture.TextureManager;

/**
 * Created by JustMe on 1/6/2017.
 */
public class Comp extends Group {
    private String imagePath;
    //tooltips,

    public Comp() {
    }

    public Comp(String imagePath) {
        this.imagePath = imagePath;
    }

    public void update() {
        clearChildren();
        addActor(getImage());
    }

    protected Actor getImage() {
        return new Image(TextureManager.getOrCreate(imagePath));
    }
}
