package eidolons.libgdx.bf.generic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.texture.TextureCache;
import main.entity.Entity;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/10/2018.
 */
public class ImageContainer extends SuperContainer {
    protected boolean flipX, flipY;
    protected Sprite sprite;
    protected String path;
    protected Map<TextureRegion, Image> imageCache = new HashMap<>();

    public ImageContainer(Image content) {
        super(content);
    }

    public ImageContainer(Entity entity) {
        this(new Image(TextureCache.getOrCreateR(entity.getImagePath())));
    }

    public ImageContainer(String path) {
        super();
        if (!StringMaster.isEmpty(path)) {
            content = new Image(sprite = new Sprite(TextureCache.getOrCreateR(path)));
            addActor(content);
        }
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
    public void setImage(TextureRegion region) {
        Image image = imageCache.get(region);
        if (image==null ){
            image = new Image(sprite = new Sprite(region));
            imageCache.put(region, image);
        }
        setImage(image);
    }
    public void setImage(String path) {
        if (ImageManager.isImage(path)) {
            this.path = path;
            Texture r = TextureCache.getOrCreate(path);
            if (sprite != null)
                if (sprite.getTexture().equals(r)) {
                    if (getContent() == null || isResetImageAlways())
                        //                     if (!getContent().getDrawable().equals(new TextureRegionDrawable(sprite)))
                        setImage(new Image(sprite));
                    return;
                }
            setImage(new Image(sprite = new Sprite(TextureCache.getOrCreate(path))));
        } else {
            setEmpty();
        }
    }

    protected boolean isResetImageAlways() {
        return false;
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


}
