package eidolons.libgdx.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by JustMe on 11/17/2018.
 */
public class DarkGrayscaleShader  {
    public static final String vertexShader = "attribute vec4 a_position;\n" +
 "attribute vec4 a_color;\n" +
 "attribute vec2 a_texCoord0;\n" +
 "\n" +
 "uniform mat4 u_projTrans;\n" +
 "\n" +
 "varying vec4 v_color;\n" +
 "varying vec2 v_texCoords;\n" +
 "\n" +
 "void main() {\n" +
 "    v_color = a_color;\n" +
 "    v_texCoords = a_texCoord0;\n" +
 "    gl_Position = u_projTrans * a_position;\n" +
 "}";

 public static final String fragmentShader = "#ifdef GL_ES\n" +
  "    precision mediump float;\n" +
  "#endif\n" +
  "\n" +
  "varying vec4 v_color;\n" +
  "varying vec2 v_texCoords;\n" +
  "uniform sampler2D u_texture;\n" +
  "\n" +
  "void main() {\n" +
  "  vec4 c = v_color * texture2D(u_texture, v_texCoords);\n" +
           "  float grey = (c.r + c.g + c.b) / 3.0;\n" +
  "  gl_FragColor = vec4(grey/1.5, grey/1.5, grey/1.5, c.a);\n" +
  "}";

 private static final ShaderProgram shader = new ShaderProgram(vertexShader,
  fragmentShader);

 public static ShaderProgram getShader() {
     return shader;
 }

}

