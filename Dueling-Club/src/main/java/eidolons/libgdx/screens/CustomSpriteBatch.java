package eidolons.libgdx.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import eidolons.libgdx.bf.SuperActor.BLENDING;

/**
 * Created by JustMe on 10/12/2018.
 */
public class CustomSpriteBatch extends SpriteBatch {

    private static CustomSpriteBatch instance;
    private BLENDING blending;

    private CustomSpriteBatch() {
    }

    public static CustomSpriteBatch getInstance() {
        if (instance == null) {
            instance = new CustomSpriteBatch();
        }
        return instance;
    }

    public BLENDING getBlending() {
        return blending;
    }

    public void setBlending(BLENDING blending) {
        switch (blending) {
            case SCREEN:
                setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
//                setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                break;
        }
        this.blending = blending;
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
