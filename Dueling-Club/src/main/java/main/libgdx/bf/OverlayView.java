package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class OverlayView extends Group {
    private Image image;

    public OverlayView(Texture texture) {
        image = new Image(texture);
        addActor(image);
    }
}
