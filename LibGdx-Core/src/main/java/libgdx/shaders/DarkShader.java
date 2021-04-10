package libgdx.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.content.enums.GenericEnums.ALPHA_TEMPLATE;
import libgdx.shaders.ShaderMaster.SHADER;
import main.content.enums.GenericEnums;

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
        return GenericEnums.ALPHA_TEMPLATE.BLOOM;
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
