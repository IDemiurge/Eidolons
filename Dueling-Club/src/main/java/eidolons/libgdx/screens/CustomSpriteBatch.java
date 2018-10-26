package eidolons.libgdx.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by JustMe on 10/12/2018.
 */
public class CustomSpriteBatch extends SpriteBatch {

    private static CustomSpriteBatch instance;

    private CustomSpriteBatch() {
    }

    public static CustomSpriteBatch getInstance() {
        if (instance == null) {
            instance = new CustomSpriteBatch();
        }
        return instance;
    }

    public void resetBlending() {
        setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public Color getColor() {
        return super.getColor();
        //        return new Color(super.getColor()).mul(GdxMaster.getBrightness());
    }
}
