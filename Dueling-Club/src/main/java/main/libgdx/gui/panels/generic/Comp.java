package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import main.libgdx.texture.TextureManager;
import main.system.images.ImageManager;

import java.util.function.Supplier;

/**
 * Created by JustMe on 1/6/2017.
 */
public class Comp extends WidgetGroup {
    protected String imagePath;
    protected Image image;
    protected boolean dirty = true;
    //tooltips,
    Supplier<String> supplier;

    public Comp(Supplier<String> supplier) {
        this.supplier = supplier;
    }

    public Comp() {
    }

    public Comp(String imagePath) {
        this.imagePath = imagePath;
    }

    public void update() {

        if (supplier != null) {
            imagePath = supplier.get();
        }
        if (ImageManager.isImage(imagePath)) {
            removeActor(image);
            image = new Image(TextureManager.getOrCreate(imagePath));
            addActorAt(0, image);
        }
        if (image == null) {
            return;
        }
        setWidth(image.getWidth());
        setHeight(image.getHeight());
        dirty = false;
    }

    public Image getImage() {
        if (image == null || dirty) {
            image = new Image(TextureManager.getOrCreate(imagePath));
        }
        return image;
    }

    public void setImagePath(String path) {
        this.imagePath = path;
        dirty = true;

    }

    @Override
    public float getWidth() {
        if (getImage() != null) {
            return getImage().getWidth();
        }
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        if (getImage() != null) {
            return getImage().getHeight();
        }
        return super.getHeight();
    }

    @Override
    public String toString() {
        return
                getClass().getSimpleName() + " " + getWidth() + " by " + getHeight()
                        + " at " + getX() + ":" + getY()
                        + " with " + getChildren().size + " children: " + getChildren()
                ;

    }
}
