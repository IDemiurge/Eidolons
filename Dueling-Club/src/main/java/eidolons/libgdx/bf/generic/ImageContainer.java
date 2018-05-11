package eidolons.libgdx.bf.generic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.texture.TextureCache;
import main.entity.Entity;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 2/10/2018.
 */
public class ImageContainer extends SuperContainer {
    boolean flipX, flipY;
    private Sprite sprite;
    private String path;

    public ImageContainer(Image content) {
        super(content);
    }

    public ImageContainer(Entity entity) {
        this(new Image(TextureCache.getOrCreateR(entity.getImagePath())));
    }

    public ImageContainer(String path) {
        super();
        content = new Image(sprite = new Sprite(TextureCache.getOrCreateR(path)));
        addActor(content);
        this.path = path;
    }

    public ImageContainer() {
        super();
    }

    @Override
    public void clearActions() {
        getContent().clearActions();
    }

    @Override
    public void addAction(Action action) {
        getContent().addAction(action);
    }

    @Override
    public void removeAction(Action action) {
        getContent().removeAction(action);
    }

    public void setImage(Image image) {
        setContents(image);
    }

    public void setImage(String path) {
        if (ImageManager.isImage(path))
        { this.path =path;
            Texture r = TextureCache.getOrCreate(path);
            if (sprite!=null )
                if (sprite.getTexture().equals(r) )
                {
                    setImage(new Image(sprite));
                    return;
                }
            setImage(new Image(sprite = new Sprite(TextureCache.getOrCreate(path))));
        }
        else
        {
            setEmpty();
        }
    }

    public void setEmpty() {
    }

    @Override
    public Array<Action> getActions() {
        if (getContent() == null)
            return new Array<>();
        return getContent().getActions();
    }

    @Override
    public boolean hasActions() {
        return getContent().hasActions();
    }

    @Override
    public void moveBy(float x, float y) {
        getContent().moveBy(x, y);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        getContent().setBounds(x, y, width, height);
    }

    @Override
    public float getOriginX() {
        return getContent().getOriginX();
    }

    @Override
    public float getOriginY() {
        return getContent().getOriginY();
    }

    @Override
    public void scaleBy(float scale) {
        getContent().scaleBy(scale);
    }

    @Override
    public void scaleBy(float scaleX, float scaleY) {
        getContent().scaleBy(scaleX, scaleY);
    }

    @Override
    public void rotateBy(float amountInDegrees) {
        getContent().rotateBy(amountInDegrees);
    }

    @Override
    public String toString() {
        return path + " container";
    }

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
        if (sprite != null)
            sprite.setFlip(flipX, flipY);
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
        if (sprite != null)
            sprite.setFlip(flipX, flipY);
    }

    @Override
    public float getRotation() {
        return getContent().getRotation();
    }

    @Override
    public void setRotation(float degrees) {
        getContent().setRotation(degrees);
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
    public void setScale(float scaleXY) {
        getContent().setScale(scaleXY);
    }

    @Override
    public void setOrigin(int alignment) {
        getContent().setOrigin(alignment);
    }

    @Override
    public void setScaleX(float scaleX) {
        getContent().setScaleX(scaleX);
    }

    @Override
    public void setScaleY(float scaleY) {
        getContent().setScaleY(scaleY);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        getContent().setScale(scaleX, scaleY);
    }
}
