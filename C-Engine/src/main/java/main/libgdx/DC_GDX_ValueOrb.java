package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 22.10.2016
 * Time: 23:50
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_ValueOrb extends Actor {
    private Texture backTexture;
    private Texture topTexture;
    private Color backColor;
    private Texture newBackTexture;

    private int posX;
    private int posY;

    private final static String topImagePath = "UI\\components\\new\\orb";

    public DC_GDX_ValueOrb(Color backColor, int x, int y, String rootPath) {
        this.backColor = backColor;
        posX = x;
        posY = y;
        new Texture("");
    }

    public DC_GDX_ValueOrb init(){


        return this;
    }

    public DC_GDX_ValueOrb setValue(int val) {

        return this;
    }

    public DC_GDX_ValueOrb setMaxValue(int val) {


        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //super.draw(batch, parentAlpha);


        batch.draw(backTexture,1,1);
    }

    @Override
    public void act(float delta) {
        if (newBackTexture != null) {
            backTexture = newBackTexture;
            newBackTexture = null;
        }
        super.act(delta);
    }
}
