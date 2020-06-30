package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.libgdx.shaders.FluctuatingShader;
import eidolons.libgdx.shaders.post.PostProcessController;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.utils.ShaderBatch;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.BLENDING;
import main.system.launch.CoreEngine;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by JustMe on 10/12/2018.
 */
public class CustomSpriteBatchImpl extends ShaderBatch implements CustomSpriteBatch {

    PostProcessController controller;
    private BLENDING blending;
    private final Set<FluctuatingShader> shaders = new HashSet<>();
    private ShaderProgram bufferedShader;

    public CustomSpriteBatchImpl() {
    }


    BlackSprite blackSprite = new BlackSprite();
    @Override
    public void drawBlack(float alpha, boolean whiteout) {
        //        draw();
        begin();
        blackSprite.resetColor(alpha);
        if (whiteout)
            setBlending(GenericEnums.BLENDING.SCREEN);
        else
            setBlending(GenericEnums.BLENDING.INVERT_SCREEN);
        blackSprite.draw(this);
        resetBlending();
        end();

    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        super.setColor(r, g, b, a);
    }

    @Override
    public void setColor(Color tint) {
        super.setColor(tint);
    }

    @Override
    public void setColor(float color) {
        super.setColor(color);
    }

    public static class BlackSprite extends Sprite {
        public BlackSprite() {
            setRegion(TextureCache.getOrCreateR("ui/white.png"));
        }

        public void resetColor(float alpha) {
            setColor(1, 1, 1, alpha);
        }
    }


    public static class GradientSprite extends Sprite {

        public GradientSprite(TextureRegion white) {
            setRegion(white);
        }

        public void setGradientColor(Color a, Color b, boolean horizontal) {
            float[] vertices = getVertices();
            float ca = a.toFloatBits();
            float cb = b.toFloatBits();
            vertices[SpriteBatch.C1] = horizontal ? ca : cb; //bottom left
            vertices[SpriteBatch.C2] = ca; //top left
            vertices[SpriteBatch.C3] = horizontal ? cb : ca; //top right
            vertices[SpriteBatch.C4] = cb; //bottom right
        }
    }

    public BLENDING getBlending() {
        return blending;
    }

    @Override
    public void setBlending(BLENDING blending) {
        //                if (premultipliedAlpha) { emitters ..
        //                    batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //                } else if (additive) {
        //                    batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        //                } else {
        //                    batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //                }
        switch (blending) {
            case SUBTRACT:
            case INVERT_SCREEN:
                //lots of experimentation there..
                //                setBlendFunction(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_DST_COLOR);
                //                setBlendFunction(GL20.GL_ONE, GL20.GL_DST_COLOR);
                //                setBlendFunction(GL20.GL_ONE, GL20.GL_DST_ALPHA);
                //                setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_DST_COLOR);
                //                setBlendFunction(GL20.GL_ONE_MINUS_DST_COLOR, GL20.GL_ONE);
                //                setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_COLOR);
                //                setBlendFunction(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_ONE);
                //                setBlendFunction(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE);
                //                setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
                //                setBlendFunctionSeparate(GL20.GL_SRC_COLOR, GL20.GL_DST_COLOR,
                //                        GL20.GL_SRC_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);

                //                setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE,
                //                        GL20.GL_ONE_MINUS_CONSTANT_ALPHA, GL20.GL_ONE);
                //                     setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
                Gdx.gl.glBlendEquation(GL20.GL_FUNC_REVERSE_SUBTRACT);
            case SCREEN:
                setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                break;
        }
        this.blending = blending;
    }

    @Override
    public void resetBlending() {
        setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        blending = null;
    }

    @Override
    public Color getColor() {
        return super.getColor();
        //        return new Color(super.getColor()).mul(GdxMaster.getBrightness());
    }

    @Override
    public void setShader(ShaderProgram shader) {
        if (CoreEngine.TEST_LAUNCH) {
            return;
        }
        if (shader != getShader()) {
            bufferedShader = getShader();
        }
        super.setShader(shader);
    }

    public void setFluctuatingShader(FluctuatingShader shader) {
        setShader(shader.getShader());
        shaders.add(shader);
    }

    public void shaderFluctuation(float delta) {
        shaders.forEach(shader -> shader.act(delta));
    }


    public void shaderReset() {
        setShader(bufferedShader);
    }

}
