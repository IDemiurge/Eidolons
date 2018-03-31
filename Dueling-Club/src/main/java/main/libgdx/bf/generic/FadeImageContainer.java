package main.libgdx.bf.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by JustMe on 3/30/2018.
 */
public class FadeImageContainer extends ImageContainer {

    private float fadePercentage;
    private Image previousImage;
    private float fadeDuration=0.5f;

    public FadeImageContainer(Image image) {
        super(image);
    }

    public FadeImageContainer(String path) {
        super(path);
    }

    public FadeImageContainer() {

    }

    @Override
    public void setContents(Actor contents) {
        if (previousImage!=null )
            previousImage.remove();
        previousImage = getContent();

        this.content = contents;
        addActor(contents);

        previousImage.getColor().a = 1;
        getContent().getColor().a = 0;
        fadePercentage=1f;
    }

    @Override
    public void act(float delta) {
        if (fadePercentage > 0) {
            fadePercentage -= delta * getFadeDuration();
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
            if (previousImage!=null &&previousImage.getDrawable() == drawable) {
                setContents(previousImage);
            }else
            setContents(new Image(drawable));
        }
    }
}
