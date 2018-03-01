package main.libgdx.bf.generic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.entity.Entity;
import main.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 2/10/2018.
 */
public class ImageContainer extends  SuperContainer {
    private   Sprite sprite;
    private   String path;

    public ImageContainer(Image content) {
        super(content);
    }
    public ImageContainer(Entity entity) {
        this(new Image(TextureCache.getOrCreateR(entity.getImagePath())));
    }

    public ImageContainer(String path) {
        super( );
       content = new Image(sprite=new Sprite(TextureCache.getOrCreateR(path)));
        addActor(content);
        this.path = path;
    }

    public ImageContainer() {
        super( );
    }


    boolean flipX, flipY;

    @Override
    public Image getContent() {
        return (Image) super.getContent();
    }

    @Override
    public Color getColor() {
        return getContent().getColor();
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
        if (sprite!=null )
        sprite.setFlip(flipX, flipY);
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
        if (sprite!=null )
            sprite.setFlip(flipX, flipY);
    }

    @Override
    public float getRotation() {
        return getContent().getRotation();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
//        getContent().setPosition(x, y);
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
