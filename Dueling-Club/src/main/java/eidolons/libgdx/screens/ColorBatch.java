package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.github.tommyettinger.colorful.ColorfulBatch;
import main.content.enums.GenericEnums;

public class ColorBatch extends ColorfulBatch implements CustomSpriteBatch{

    @Override
    public void drawBlack(float alpha, boolean whiteout) {

    }

    @Override
    public GenericEnums.BLENDING getBlending() {
        return null;
    }

    public void setColorful(boolean b){
        // setShader();

}
    @Override
    public void shaderReset() {

    }

    @Override
    public void setColor(Color tint) {
        super.setColor(tint.sub(1,1,1,0));
    }

    @Override
    public void setColor(float color) {
        super.setColor(color);
    }

    @Override
    public void setPackedColor(float color) {
        super.setPackedColor(color);
    }

    @Override
    public void setBlending(GenericEnums.BLENDING blending) {
        switch (blending) {
            case SUBTRACT:
            case INVERT_SCREEN:
                Gdx.gl.glBlendEquation(GL20.GL_FUNC_REVERSE_SUBTRACT);
            case SCREEN:
                setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                break;
        }
    }

    @Override
    public void resetBlending() {
        setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
    }
}
