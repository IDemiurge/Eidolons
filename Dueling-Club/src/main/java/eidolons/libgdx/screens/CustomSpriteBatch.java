package eidolons.libgdx.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by JustMe on 10/12/2018.
 */
public class CustomSpriteBatch extends SpriteBatch{

    private CustomSpriteBatch() {
    }

    private static Batch instance;

    public static Batch getInstance() {
        if (instance == null) {
            instance = new CustomSpriteBatch();
        }
        return instance;
    }

    @Override
    public Color getColor() {
        return  super.getColor();
//        return new Color(super.getColor()).mul(GdxMaster.getBrightness());
    }
}
