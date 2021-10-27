package libgdx.shaders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import libgdx.GdxMaster;
import libgdx.TiledNinePatchGenerator;
import libgdx.bf.generic.SuperContainer;
import libgdx.assets.texture.TextureCache;
import main.content.enums.GenericEnums;
import org.lwjgl.opengl.Display;

/**
 * Created by JustMe on 2/7/2018.
 */
public class VignetteShader {

    private static boolean used;
    private static ShaderProgram shader;
    private static final String vignettePath = "ui/macro/vignette.png";

    public static boolean isUsed() {
        return used;
    }

    public static void setUsed(boolean used) {
        VignetteShader.used = used;
    }

    public static ShaderProgram getShader() {
//        ShaderProgram.pedantic = false;
//
        if (shader == null) {
            String vertexShader = "//combined projection and view matrix\n" +
                    "uniform mat4 u_projView;\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "\n" +
                    "//\"in\" attributes from our SpriteBatch\n" +
                    "attribute vec2 Position;\n" +
                    "attribute vec2 TexCoord;\n" +
                    "attribute vec4 Color;\n" +
                    "\n" +
                    "//\"out\" varyings to our fragment shader\n" +
                    "varying vec4 vColor;\n" +
                    "varying vec2 vTexCoord;\n" +
                    " \n" +
                    "void main() {\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "vColor = Color;\n" +
                    "vTexCoord = TexCoord;\n" +
                    "gl_Position = u_projTrans * a_position;\n" +
                    "gl_Position = u_projView * vec4(Position, 0.0, 1.0);\n" +
                    "}";//     "texColor.rgb *= vignette;\n" +
            String fragmentShader = "//texture 0\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "\n" +
                    "uniform vec2 resolution;\n" +
                    "\n" +
                    "varying vec4 vColor;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "\n" +
                    "//RADIUS of our vignette, where 0.5 results in a circle fitting the screen\n" +
                    "const float RADIUS = 0.75;\n" +
                    "\n" +
                    "//softness of our vignette, between 0.0 and 1.0\n" +
                    "const float SOFTNESS = 0.45;\n" +
                    "\n" +
                    "void main() {\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "//sample our texture\n" +
                    "vec4 texColor = texture2D(u_texture, vTexCoord);\n" +
                    "\n" +
                    "//determine center\n" +
                    "vec2 position = (gl_FragCoord.xy / resolution.xy) - vec2(0.5);\n" +
                    "\n" +
                    "//OPTIONAL: correct for aspect ratio\n" +
                    "//position.x *= resolution.x / resolution.y;\n" +
                    "\n" +
                    "//determine the vector length from center\n" +
                    "float len = length(position);\n" +
                    "\n" +
                    "//our vignette effect, using smoothstep\n" +
                    "float vignette = smoothstep(RADIUS, RADIUS-SOFTNESS, len);\n" +
                    "\n" +
                    "//apply our vignette\n" +
                    //     "texColor.rgb *= vignette;\n" +
                    "texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, 0.5);" +
                    "\n" +
                    "gl_FragColor = texColor;\n" +
                    "}";
            shader = new ShaderProgram(vertexShader,
                    fragmentShader);
            shader.setUniformf("resolution", Display.getWidth(), Display.getHeight());
        }
        return shader;
    }

    public static SuperContainer createVignetteActor() {
        TextureRegion texture;
        if (isTiledVignette()){
            texture = new TextureRegion(TiledNinePatchGenerator.getOrCreateNinePatch(TiledNinePatchGenerator.NINE_PATCH.VIGNETTE,
                    TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.TRANSPARENT, GdxMaster.getWidth(), GdxMaster.getHeight()));
        } else
            texture = TextureCache.getOrCreateR(vignettePath);

        SuperContainer vignette = new SuperContainer(
                new Image(texture),
                true);


        vignette.getContent().setWidth(GdxMaster.getWidth());
        vignette.getContent().setHeight(GdxMaster.getHeight());
        vignette.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.VIGNETTE);

        vignette.setTouchable(Touchable.disabled);
        return vignette;
    }
    private static boolean isTiledVignette() {
        return true;
    }
}
