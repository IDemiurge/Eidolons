package main.libgdx.old;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
public class TopPanel extends Image {

    private final static String backgroundImagePath = "UI\\components\\top.png";
    private static final String buttonImagePath = "UI\\components\\menu.png";
    private Image imageButton;
    private Label text;
//    public static final String


    public TopPanel(String imagePath) {
        super(new Texture(imagePath + backgroundImagePath));
        imageButton = new Image(new Texture(imagePath + buttonImagePath));
        // text = new Label("aaaaaaa", new Skin());
    }

    public TopPanel init() {
//        int h = Gdx.graphics.getHeight();
//        int w = Gdx.graphics.getWidth();

        int h = 900;
        int w = 1600;

        setAlign(Align.top);
        setX(w / 2 - (getWidth() / 2));
        setY(h - getHeight());
        imageButton.setX(w / 2 - (imageButton.getWidth() / 2));
        imageButton.setY(h - imageButton.getHeight());
        //text.setBounds(0, 0, 10, 10);
        return this;
    }

    public TopPanel setText(String text) {
        //this.text.setText(text);
        return this;
    }

    private TopPanel setOnButtonClicked(EventListener listener) {
        imageButton.addListener(listener);
        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        imageButton.draw(batch, parentAlpha);
        //text.draw(batch, parentAlpha);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor hitedActor = super.hit(x, y, touchable);
        if (hitedActor == null) {
            hitedActor = imageButton.hit(x, y, touchable);
            if (hitedActor == null) {
                hitedActor = text.hit(x, y, touchable);
            }
        }
        return hitedActor;
    }
}
