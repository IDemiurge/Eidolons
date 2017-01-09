package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.texture.TextureManager;

import java.util.function.Supplier;

/**
 * Created by JustMe on 1/6/2017.
 */
public class Comp extends Group {
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
        clearChildren();
        if (supplier != null) {
            imagePath = supplier.get();
        }
            if (imagePath != null) {
                image = new Image(TextureManager.getOrCreate(imagePath));
            }
        if (image==null ){
            return ;
        }
        setWidth(image.getWidth());
        setHeight(image.getHeight());
        addActor(getImage());
        dirty = false;
    }

    public Image getImage() {
        if (image == null)
            image = new Image(TextureManager.getOrCreate(imagePath));
        return image;
    }

    public void setImagePath(String path) {
        this.imagePath = path;
        dirty = true;

    }

    @Override
    public float getWidth() {
        if (getImage() != null)
            return getImage().getWidth();
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        if (getImage() != null)
            return getImage().getHeight();
        return super.getHeight();
    }
}
