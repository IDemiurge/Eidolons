package eidolons.libgdx.bf.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.game.battlecraft.logic.battlefield.vision.OutlineMaster;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.rules.VisionEnums;

/**
 * Created by JustMe on 3/30/2018.
 */
public class FadeImageContainer extends ImageContainer {

    protected float fadePercentage;
    protected Image previousImage;
    protected float fadeDuration = 2f;
    protected String imagePath;
    protected String previousPath;

    public FadeImageContainer(String path, float fadeDuration) {
        super(path);
        this.fadeDuration = fadeDuration;
    }

    public FadeImageContainer(Image image) {
        super(image);
    }

    public FadeImageContainer(String path) {
        super(path);
    }

    public FadeImageContainer() {

    }

    @Override
    public void setImage(String path) {
        previousPath = imagePath;
        super.setImage(path);
        imagePath = path;
    }

    public Image getPreviousImage() {
        return previousImage;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getPreviousPath() {
        return previousPath;
    }

    public void resetPreviousImage() {
        if (previousImage == null)
            return;
        setContents(previousImage);
        main.system.auxiliary.log.LogMaster.log(1, this + " resetPreviousImage to " + previousImage);
    }

    @Override
    public Image getContent() {
        if (content == null)
            content = new Image();
        return super.getContent();
    }

    @Override
    public void setEmpty() {
        if (getContent() != null)
            fadeOut();
    }

    @Override
    public float getHeight() {
        if (getContent() == null)
            return 0;
        return super.getHeight();
    }

    @Override
    public float getWidth() {
        if (getContent() == null)
            return 0;
        return super.getWidth();
    }

    public void setImageImmediately(String image) {
        setContentsImmediately(new Image(TextureCache.getOrCreateR(image)));
    }

    public void setContentsImmediately(Actor contents) {
        super.setContents(contents);
    }

    @Override
    public void setContents(Actor contents) {
        if (previousImage == contents)
            return;

        if (previousImage != null)
            previousImage.remove();
        previousImage = getContent();

        this.content = contents;
        addActor(contents);
        if (previousImage != null) {
            previousImage.getColor().a = 1;
            getContent().getColor().a = 0;
            fadePercentage = 1f;
        }
    }

    @Override
    public void act(float delta) {

//        if (getContent().getDrawable().equals(
//                TextureCache.getOrCreateR(VisionEnums.OUTLINE_TYPE.UNKNOWN.getImagePath()))) {
//            return;
//        }
//        if (getContent().getDrawable().equals(
//                TextureCache.getOrCreate(VisionEnums.OUTLINE_TYPE.UNKNOWN.getImagePath()))) {
//            return;
//        }
        if (fadePercentage > 0) {
            fadePercentage = Math.max(0, fadePercentage - delta / getFadeDuration());
            previousImage.getColor().a = fadePercentage;
            getContent().getColor().a = 1 - fadePercentage;
        }
        super.act(delta);
    }

    public float getFadeDuration() {
        return fadeDuration;
    }

    public void setFadeDuration(float fadeDuration) {
        this.fadeDuration = fadeDuration;
    }

    public void setTexture(Drawable drawable) {
        if (getContent().getDrawable() != drawable) {
            if (previousImage != null && previousImage.getDrawable() == drawable) {
                setContents(previousImage);
            } else
                setContents(new Image(drawable));
        }
    }

}
