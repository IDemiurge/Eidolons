package main.libgdx.bf.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.entity.Entity;
import main.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 2/10/2018.
 */
public class ImageContainer extends  SuperContainer {
    public ImageContainer(Actor content) {
        super(content);
    }
    public ImageContainer(Entity entity) {
        this(new Image(TextureCache.getOrCreateR(entity.getImagePath())));
    }

    public ImageContainer(String path) {
        this(new Image(TextureCache.getOrCreateR(path)));
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height) {
        getContent().setSize(width, height);
    }

    @Override
    public void setOrigin(float originX, float originY) {
        getContent().setOrigin(originX, originY);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        getContent().setScale(scaleX, scaleY);
    }

    @Override
    public void setRotation(float degrees) {
        getContent().setRotation(degrees);
    }
}
