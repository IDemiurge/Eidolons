package eidolons.libgdx.shaders.blur;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.shaders.ShaderMaster;
import eidolons.libgdx.shaders.ShaderMaster.SHADER;
import eidolons.libgdx.shaders.post.Processor;
import main.content.enums.GenericEnums;

/**
 * Created by JustMe on 12/2/2018.
 *
 * This class applies dynamic blur to the screen
 *
 * solutions:
 * FBO+shader
 * downsampling and other tricks?
 *
 */
public class Blur implements Processor{
    private final FadeImageContainer fluctuate;
    float blur=2;
ShaderProgram shader;

    public void setBlur(float blur) {
        this.blur = blur;
    }

    public Blur() {
        shader = ShaderMaster.getShader(SHADER.BLUR);

        if (!shader.isCompiled()) {
            Gdx.app.error("Shader", shader.getLog());
            Gdx.app.exit();
        }

        shader.begin();
        shader.setUniformf("resolution", GdxMaster.getWidth());
        shader.end();

        fluctuate = new FadeImageContainer();
        fluctuate.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.LIGHT_EMITTER_RAYS);
    }

    @Override
    public void prepareForFBO(SpriteBatch batch, float delta) {
        fluctuate.act(delta);
        batch.setShader(shader);
        shader.setUniformf("dir", 1.0f, 0.0f);
        shader.setUniformf("radius",5* blur*getFluctuation());
    }

    private float getFluctuation() {
        return fluctuate.getColor().a;
    }

    @Override
    public void prepareForBatch(SpriteBatch batch) {
        // Vertical blur from FBO B to the screen
        batch.setShader(shader);
        shader.setUniformf("dir", 0.0f, 1.0f);
        shader.setUniformf("radius", blur*getFluctuation());
    }
}
