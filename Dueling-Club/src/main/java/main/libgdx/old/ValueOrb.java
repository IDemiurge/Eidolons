package main.libgdx.old;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * Date: 22.10.2016
 * Time: 23:50
 * To change this template use File | Settings | File Templates.
 */
public class ValueOrb extends Group {
    private final static String topImagePath = "UI\\components\\new\\orb 64.png";
    private Texture backTexture;
    private Image topTexture;
    private Color backColor;
    private Texture newBackTexture;
    private String imagePath;

    public ValueOrb(Color backColor, String imagePath) {
        this.backColor = backColor;
        this.imagePath = imagePath;

    }

    public ValueOrb init() {
        topTexture = new Image(new Texture(imagePath + File.separator + topImagePath));
        addActor(topTexture);
        this.setWidth(topTexture.getWidth());
        this.setHeight(topTexture.getHeight());
        return this;
    }

    public ValueOrb setValue(int val) {

        return this;
    }

    public ValueOrb setMaxValue(int val) {


        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    /*    @Override
    public void act(float delta) {
        if (newBackTexture != null) {
            backTexture = newBackTexture;
            newBackTexture = null;
        }
        super.act(delta);
    }*/
}
