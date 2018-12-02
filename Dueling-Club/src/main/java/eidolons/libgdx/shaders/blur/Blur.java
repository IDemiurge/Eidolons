package eidolons.libgdx.shaders.blur;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.libgdx.shaders.ShaderMaster;
import eidolons.libgdx.shaders.ShaderMaster.SHADER;

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
public class Blur {
    public void blur(SpriteBatch batch) {
        ShaderProgram blurShader = ShaderMaster.getShader(SHADER.BLUR);
        //always a good idea to set up default uniforms...
//        blurShader.setUniformf("dir", 0f, 0f); //direction of blur; nil for now
//        blurShader.setUniformf("resolution", FBO_SIZE); //size of FBO texture
//        blurShader.setUniformf("radius", radius); //radius of blur
//
//        fboA(batch);
    }

}
