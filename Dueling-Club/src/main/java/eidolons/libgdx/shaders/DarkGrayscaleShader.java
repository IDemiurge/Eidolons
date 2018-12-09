package eidolons.libgdx.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.libgdx.bf.Fluctuating.ALPHA_TEMPLATE;
import eidolons.libgdx.shaders.ShaderMaster.SHADER;

/**
 * Created by JustMe on 11/17/2018.
 */
public class DarkGrayscaleShader extends FluctuatingShader {


    private static final ShaderProgram shader = getInstance().getShader();
    private static DarkGrayscaleShader instance;

    public static ShaderProgram getShader_() {
        return shader;
    }

    public static DarkGrayscaleShader getInstance() {
        if (instance == null) {
            instance = new DarkGrayscaleShader();
        }
        return instance;
    }

    @Override
    protected ALPHA_TEMPLATE getAlphaTemplate() {
        return ALPHA_TEMPLATE.BLOOM;
    }

    @Override
    protected SHADER getShaderType() {
        return SHADER.GREY_DARKEN;
    }

    @Override
    protected float getDefaultBaseFluctuatingValue() {
        return 1;
    }


    //    public static final String vertexShader = "attribute vec4 a_position;\n" +
    // "attribute vec4 a_color;\n" +
    // "attribute vec2 a_texCoord0;\n" +
    // "\n" +
    // "uniform mat4 u_projTrans;\n" +
    // "\n" +
    // "varying vec4 v_color;\n" +
    // "varying vec2 v_texCoords;\n" +
    // "\n" +
    // "void main() {\n" +
    // "    v_color = a_color;\n" +
    // "    v_texCoords = a_texCoord0;\n" +
    // "    gl_Position = u_projTrans * a_position;\n" +
    // "}";
    //
    // public static final String fragmentShader = "#ifdef GL_ES\n" +
    //  "    precision mediump float;\n" +
    //  "#endif\n" +
    //  "\n" +
    //  "varying vec4 v_color;\n" +
    //  "varying vec2 v_texCoords;\n" +
    //  "uniform sampler2D u_texture;\n" +
    //  "\n" +
    //  "void main() {\n" +
    //  "  vec4 c = v_color * texture2D(u_texture, v_texCoords);\n" +
    //           "  float grey = (c.r + c.g + c.b) / 3.0;\n" +
    //  "  gl_FragColor = vec4(grey/1.5, grey/1.5, grey/1.5, c.a);\n" +
    //  "}";
}

