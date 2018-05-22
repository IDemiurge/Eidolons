package eidolons.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.bf.SuperActor.BLENDING;
import eidolons.system.utils.GdxUtil;

/**
 * Created by JustMe on 5/21/2018.
 */
public class GdxMixer extends GdxUtil{

    private final ShaderProgram shaderProgram;
    private final BLENDING blending;
    private final Actor[] actors;
    private   Float[] alpha;
    private final String pathRoot;
    private float m_fboScaler = 1.5f;
    private boolean m_fboEnabled = true;
    private FrameBuffer fbo = null;
    private TextureRegion region = null;
    private SpriteBatch batch;


    @Override
    protected void execute() {

    }

    private void renderToFbo() {
        batch.setShader(shaderProgram);
        int i=0;
        for (Actor sub : actors) {
            sub.draw(batch, alpha[i++]);
            batch.setBlendFunction(blending.blendSrcFunc, blending.blendDstFunc);
        }
    }
    public GdxMixer(ShaderProgram shaderProgram, BLENDING blending, String pathRoot, Actor... actors) {
        this.shaderProgram = shaderProgram;
        this.blending = blending;
        this.pathRoot = pathRoot;
        this.actors = actors;
    }

    public void mix() {

        FileHandle handle = new FileHandle(getPath());
        GdxImageMaster.writeImage(handle, region.getTexture());
    }

    private String getPath() {
        return pathRoot;
    }

    public void render(SpriteBatch spriteBatch) {
        if (spriteBatch == null)
            spriteBatch = new SpriteBatch();
        this.batch =spriteBatch;

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        if (m_fboEnabled)      // enable or disable the supersampling
        {
            if (fbo == null) {
                // m_fboScaler increase or decrease the antialiasing quality
                fbo = FrameBuffer.createFrameBuffer(
                 Format.RGB565, (int) (width * m_fboScaler),
                 (int) (height * m_fboScaler), false);
                region = new TextureRegion(fbo.getColorBufferTexture());
                region.flip(false, true);
            }

            fbo.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        }

        // this is the main render function
        renderToFbo();

        if (fbo != null) {
            fbo.end();

            spriteBatch.begin();
            spriteBatch.draw(region, 0, 0, width, height);
            spriteBatch.end();
        }
    }

}
