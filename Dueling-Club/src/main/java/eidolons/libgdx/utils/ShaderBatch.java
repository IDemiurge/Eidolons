package eidolons.libgdx.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderBatch extends SpriteBatch {


    static final String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "uniform mat4 u_projTrans;\n" //
            + "varying vec4 v_color;\n" //
            + "varying vec2 v_texCoords;\n" //
            + "\n" //
            + "void main()\n" //
            + "{\n" //
            + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
            + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "}\n";

    static final String fragmentShader = "#ifdef GL_ES\n" //
            + "#define LOWP lowp\n" //
            + "precision mediump float;\n" //
            + "#else\n" //
            + "#define LOWP \n" //
            + "#endif\n" //
            + "varying LOWP vec4 v_color;\n" //
            + "varying vec2 v_texCoords;\n" //
            + "uniform sampler2D u_texture;\n" //
            + "uniform float brightness;\n" //
            + "uniform float contrast;\n" //
            + "void main()\n"//
            + "{\n" //
            + "  vec4 color = v_color * texture2D(u_texture, v_texCoords);\n"
            + "  color.rgb /= color.a;\n" //ignore alpha
            + "  color.rgb = ((color.rgb - 0.5) * max(contrast, 0.0)) + 0.5;\n" //apply contrast
            + "  color.rgb += brightness;\n" //apply brightness
            + "  color.rgb *= color.a;\n" //return alpha
            + "  gl_FragColor = color;\n"
            + "}";

    ShaderProgram shader;
    public final boolean isCompiled;
    public final String log;

    private int brightnessLoc = -1;
    private int contrastLoc = -1;

    //ideally use getters/setters here...
    private float brightness = 0f;
    private float contrast = 1f;

    private static float globalBrightness = 0f;
    private static float globalContrast = 1f;

    public ShaderBatch() {
        super();
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(vertexShader, fragmentShader);
        isCompiled = shader.isCompiled();
        log = shader.getLog();
        if (isCompiled) {
            setShader(shader);
            shader.begin();
            setBrightnessLoc(shader.getUniformLocation("brightness"));
            setContrastLoc(shader.getUniformLocation("contrast"));
            shader.end();
        }

    }

    @Override
    public void end() {
        try {
            super.end();
        } catch (IllegalStateException e) {
            super.begin();
            super.end();
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public void begin() {
        try {
            super.begin();
        } catch (IllegalStateException e) {
            super.end();
            super.begin();
            main.system.ExceptionMaster.printStackTrace(e);
        }

        if (getBrightnessLoc() != -1 && shader != null)
            shader.setUniformf(getBrightnessLoc(), getBrightness() * globalBrightness);
        if (getContrastLoc() != -1 && shader != null)
            shader.setUniformf(getContrastLoc(), getContrast() * globalContrast);
    }

    public static void setGlobalBrightness(float globalBrightness) {
        ShaderBatch.globalBrightness = globalBrightness;
    }

    public static void setGlobalContrast(float globalContrast) {
        ShaderBatch.globalContrast = globalContrast;
    }

    public int getBrightnessLoc() {
        return brightnessLoc;
    }

    public void setBrightnessLoc(int brightnessLoc) {
        this.brightnessLoc = brightnessLoc;
    }

    public int getContrastLoc() {
        return contrastLoc;
    }

    public void setContrastLoc(int contrastLoc) {
        this.contrastLoc = contrastLoc;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }
}
