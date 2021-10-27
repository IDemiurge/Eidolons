package libgdx.shaders.blur;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import libgdx.shaders.ShaderMaster;
import libgdx.shaders.ShaderMaster.SHADER;
import libgdx.assets.texture.TextureCache;
import org.lwjgl.opengl.GL11;

/**
 * Created by JustMe on 12/2/2018.
 */
public class BlurTest extends Game {
    //should be at least as large as our display
    public static final int FBO_SIZE = 1024;
    final static float MAX_BLUR = 3f;
    //our texture to blur
    Texture tex, tex2;
    //we'll use a single batch for everything
    SpriteBatch batch;
    //our blur shader
    ShaderProgram blurShader;
    //our offscreen buffers
    FrameBuffer blurTargetA, blurTargetB;
    float radius = 3f;

    public static void main(String[] args) {
        new LwjglApplication(new BlurTest(), "Blurry", 1920, 1080);
    }

    @Override
    public void dispose() {

    }

    public void create() {
        // 2D games generally won't require depth testing
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // Enable blending
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Set clear to transparent black
//        glClearColor(0f, 0f, 0f, 0f);

        tex =
         TextureCache.getOrCreate("slider.png");
        tex2 =
         TextureCache.getOrCreate("tiles.png");
        tex =
         TextureCache.getOrCreate("demo/heroes/averon full.png");
        tex2 =
         TextureCache.getOrCreate("demo/heroes/averon.png");

        //our simple demo won't support display resizing
//        Display.setResizable(false);

        //load our shader program and sprite batch
        try {
            //create our FBOs
            blurTargetA = FrameBuffer.createFrameBuffer(Format.RGBA4444, FBO_SIZE, FBO_SIZE, true);
            blurTargetB = FrameBuffer.createFrameBuffer(Format.RGBA4444, FBO_SIZE, FBO_SIZE, true);

            //our basic pass-through vertex shader
            //create our shader program
            blurShader = ShaderMaster.getShader(SHADER.BLUR);
            ShaderProgram.pedantic = false;
            //Good idea to log any warnings if they exist
            if (blurShader.getLog().length() != 0)
                System.out.println(blurShader.getLog());

            //always a good idea to set up default uniforms...
//            blurShader.begin();
//            blurShader.setUniformf("dir", 0f, 0f); //direction of blur; nil for now
//            blurShader.setUniformf("resolution", FBO_SIZE); //size of FBO texture
//            blurShader.setUniformf("radius", radius); //radius of blur

            batch = new SpriteBatch();

            //            blurShader = DarkShader.getShader();
//                        blurShader = FishEyeShader.getShader();
        } catch (Exception e) {
            //simple exception handling...
            e.printStackTrace();
            System.exit(0);
        }
    }


    public void drawEntities(SpriteBatch batch) {
        batch.draw(tex, 50, 50);
        batch.draw(tex2, tex.getWidth() + 20, 100);
    }

    void renderScene() {
//        fb.enableBlending();
//        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //Bind FBO target A
        blurTargetA.begin();

        //Clear FBO A with an opaque colour to minimize blending issues
//        glClearColor(0.5f, 0.5f, 0.5f, 1f);
//        glClear(GL_COLOR_BUFFER_BIT);
//        Gdx.gl.glClearColor(0, 0, 0, 0);
//        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        //Reset batch to default shader (without blur)
//        batch.setShader(GrayscaleShader.getGrayscaleShader());

        //send the new projection matrix (FBO size) to the default shader
//        batch.getProjectionMatrix().setToOrtho2D(0, 0,blurTargetA.getWidth(), blurTargetA.getHeight());
        //now we can start our batch
        batch.begin();

        //render our scene fully to FBO A
        drawEntities(batch);

        //flush the batch, i.e. render entities to GPU
        batch.flush();

        //After flushing, we can finish rendering to FBO target A
        blurTargetA.end();


    }

    void horizontalBlur() {
        //swap the shaders
        //this will send the batch's (FBO-sized) projection matrix to our blur shader
        batch.setShader(blurShader);

        //ensure the direction is along the X-axis only
//        blurShader.setUniformf("dir", 1f, 0f);

        //determine radius of blur based on mouse position
//        float mouseXAmt = Mouse.getX() / (float) Display.getWidth();
//        blurShader.setUniformf("radius", mouseXAmt * MAX_BLUR);

        //start rendering to target B
        blurTargetB.begin();

        //no need to clear since targetA has an opaque background
        //render target A (the scene) using our horizontal blur shader
        //it will be placed into target B
        TextureRegion r = new TextureRegion(blurTargetA.getColorBufferTexture());
        batch.draw(r, 0, 0, r.getRegionWidth(), r.getRegionHeight());
        //flush the batch before ending target B
        batch.flush();

        //finish rendering target B
        blurTargetB.end();
    }

    void verticalBlur() {
        //now we can render to the screen using the vertical blur shader

        //send the screen-size projection matrix to the blurShader
//        batch.resize(Display.getWidth(), Display.getHeight());
//        batch.getProjectionMatrix().setToOrtho2D(0, 0,Display.getWidth(), Display.getHeight());
        //apply the blur only along Y-axis
//        blurShader.setUniformf("dir", 0f, 1f);

        //update Y-axis blur radius based on mouse
//        float mouseYAmt = (Display.getHeight() - Mouse.getY() - 1) / (float) Display.getHeight();
//        blurShader.setUniformf("radius", mouseYAmt * MAX_BLUR);

        //draw the horizontally-blurred FBO B to the screen, applying the vertical blur as we go
        TextureRegion r = new TextureRegion(blurTargetB.getColorBufferTexture());
        batch.draw(r, 0, 0, r.getRegionWidth(), r.getRegionHeight());

    }

    public void render() {
        //render scene to FBO A
        renderScene();

        //render FBO A to FBO B, using horizontal blur
        horizontalBlur();

        //render FBO B to scene, using vertical blur
        verticalBlur();

//        region = new TextureRegion(t.getColorBufferTexture());
        batch.setShader(blurShader);
//        batch.draw(tex, 0,0);
        batch.flush();
        batch.end();
    }

}
