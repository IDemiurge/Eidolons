package eidolons.libgdx.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.libgdx.bf.Fluctuating.ALPHA_TEMPLATE;
import eidolons.libgdx.shaders.ShaderMaster.SHADER;

/**
 * Created by JustMe on 11/17/2017.
 */
public class DarkShader extends FluctuatingShader{

    private static final ShaderProgram shader = getInstance().getShader();
    private static DarkShader instance;

    public static ShaderProgram getDarkShader() {
        return shader;
    }

    public static DarkShader getInstance() {
        if (instance == null) {
            instance = new DarkShader();
        }
        return instance;
    }

    @Override
    protected ALPHA_TEMPLATE getAlphaTemplate() {
        return ALPHA_TEMPLATE.BLOOM;
    }

    @Override
    protected SHADER getShaderType() {
        return SHADER.DARKEN;
    }

    @Override
    protected float getDefaultBaseFluctuatingValue() {
        return 1;
    }

}
