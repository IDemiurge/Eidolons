package eidolons.libgdx.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.libgdx.bf.Fluctuating;
import main.content.enums.GenericEnums.ALPHA_TEMPLATE;
import eidolons.libgdx.shaders.ShaderMaster.SHADER;

/**
 * Created by JustMe on 12/5/2018.
 */
public abstract class FluctuatingShader {
    Fluctuating fluctuating;
    ShaderProgram shader;
    private float baseFluctuatingValue;

    public FluctuatingShader() {
        fluctuating = new Fluctuating(getAlphaTemplate());
        shader = ShaderMaster.getShader(getShaderType());
        baseFluctuatingValue = getDefaultBaseFluctuatingValue();
    }

    public ShaderProgram getShader() {
        return shader;
    }

    protected abstract ALPHA_TEMPLATE getAlphaTemplate();

    protected abstract SHADER getShaderType();

    public void act(float delta) {
        fluctuating.fluctuate(delta);
        shader.setUniformf(getValueName(), baseFluctuatingValue * fluctuating.getColor().a);

    }

    protected  abstract float getDefaultBaseFluctuatingValue();



    protected  String getValueName(){
        return "coef";
    }
}
