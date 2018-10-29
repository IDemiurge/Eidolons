package eidolons.libgdx.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by JustMe on 10/27/2018.
 */
public class FishEyeShader  {



    public static final String vertexShader =
     "uniform mat4 u_projTrans;\n" +
     "varying vec4 Vertex_UV;\n" +
     "uniform mat4 gxl3d_ModelViewProjectionMatrix;\n" +
     "void main()\n" +
     "{\n" +
     "  gl_Position = gxl3d_ModelViewProjectionMatrix * gl_Vertex;\n" +
     "  Vertex_UV = gl_MultiTexCoord0;\n" +
     "}";


    public static final String fragmentShader = "uniform sampler2D tex0;\n" +
     "varying vec4 Vertex_UV;\n" +
     "const float PI = 3.1415926535;\n" +
     "\n" +
     "void main()\n" +
     "{\n" +
     "  float aperture = 178.0;\n" +
     "  float apertureHalf = 0.5 * aperture * (PI / 180.0);\n" +
     "  float maxFactor = sin(apertureHalf);\n" +
     "\n" +
     "  vec2 uv;\n" +
     "  vec2 xy = 2.0 * Vertex_UV.xy - 1.0;\n" +
     "  float d = length(xy);\n" +
     "  if (d < (2.0-maxFactor))\n" +
     "  {\n" +
     "    d = length(xy * maxFactor);\n" +
     "    float z = sqrt(1.0 - d * d);\n" +
     "    float r = atan(d, z) / PI;\n" +
     "    float phi = atan(xy.y, xy.x);\n" +
     "\n" +
     "    uv.x = r * cos(phi) + 0.5;\n" +
     "    uv.y = r * sin(phi) + 0.5;\n" +
     "  }\n" +
     "  else\n" +
     "  {\n" +
     "    uv = Vertex_UV.xy;\n" +
     "  }\n" +
     "  vec4 c = texture2D(tex0, uv);\n" +
     "  gl_FragColor = c;\n" +
     "}";

    private static   ShaderProgram shader;

    public static ShaderProgram getShader() {
        if (shader == null) {
            shader = new ShaderProgram(vertexShader,
             fragmentShader);
        }
        return shader;
    }
    /*



     */
}
