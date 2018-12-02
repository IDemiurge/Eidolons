package eidolons.libgdx.shaders.blur;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;

public class BlurSample extends Game {
    private static final float WORLD_TO_SCREEN = 1.0f / 100.0f;

    private static final float SCENE_WIDTH = 12.80f;
    private static final float SCENE_HEIGHT = 7.20f;

    private static final int VIRTUAL_WIDTH = 1280;
    private static final int VIRTUAL_HEIGHT = 720;
    private static final java.lang.String FULL_BLUR = "";//"blur/rock.png";

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture background;
    private Texture foreground;
    private Texture mountains;
    private Texture rock;
    private Texture dinosaur;
    private Texture caveman;
    private ShaderProgram shader;
    private FrameBuffer fboA;
    private FrameBuffer fboB;
    private FadeImageContainer fluctuate;

    public static void main(String[] args) {
        new LwjglApplication(new BlurSample());
    }
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);

        batch = new SpriteBatch();

        background = (TextureCache.getOrCreate("blur/background.png"));
        foreground = (TextureCache.getOrCreate("blur/foreground.png"));
        mountains = (TextureCache.getOrCreate("blur/mountains.png"));

        rock = (TextureCache.getOrCreate(FULL_BLUR));

        caveman = (TextureCache.getOrCreate("blur/caveman.png"));
        dinosaur = (TextureCache.getOrCreate("blur/dinosaur.png"));
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(FileManager.readFile(PathFinder.getResPath()+
         "img/blur/blur.vert"), FileManager.readFile(PathFinder.getResPath()+"img/blur/blur.frag"));
        fboA =  FrameBuffer.createFrameBuffer(Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
        fboB =   FrameBuffer.createFrameBuffer(Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);

        camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);

        if (!shader.isCompiled()) {
            Gdx.app.error("Shader", shader.getLog());
            Gdx.app.exit();
        }

        shader.begin();
        shader.setUniformf("resolution", VIRTUAL_WIDTH);
        shader.end();

        fluctuate = new FadeImageContainer();
        fluctuate.setAlphaTemplate(ALPHA_TEMPLATE.HIGHLIGHT);
    }

    public void dispose() {
        batch.dispose();
        background.dispose();
        foreground.dispose();
        mountains.dispose();
        caveman.dispose();
        dinosaur.dispose();
        background.dispose();
        shader.dispose();
        fboA.dispose();
        fboB.dispose();
    }

    public void render() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.begin();

        // Draw background as-is
        batch.setShader(null);
        drawTexture(background,  0.0f, 0.0f);
        batch.flush();

        // Draw blurred mountains
        fboA.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setShader(null);
        drawTexture(mountains, 0.0f, 0.0f);
        batch.flush();
        fboA.end();
        applyBlur(3.0f);

        // Draw foreground and characters without blur effect
        batch.setShader(null);
        drawTexture(foreground, 0.0f, 0.0f);
        drawTexture(caveman, 1.0f, 1.5f);
        drawTexture(dinosaur, 6.0f, 2.45f);
        batch.flush();

        // Draw blurred rock
        fboA.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setShader(null);
        drawTexture(rock, 0.0f, 0.0f);
        batch.flush();
        fboA.end();

        fluctuate.act(Gdx.graphics.getDeltaTime());

        applyBlur(fluctuate.getColor().a*10);

        batch.end();
    }

    private void applyBlur(float blur) {
        // Horizontal blur from FBO A to FBO B
        fboB.begin();
        batch.setShader(shader);
        shader.setUniformf("dir", 1.0f, 0.0f);
        shader.setUniformf("radius", blur);
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        drawTexture(fboA.getColorBufferTexture(),  0.0f, 0.0f);
        batch.flush();
        fboB.end();

        // Vertical blur from FBO B to the screen
        shader.setUniformf("dir", 0.0f, 1.0f);
        shader.setUniformf("radius", blur);
        drawTexture(fboB.getColorBufferTexture(), 0.0f, 0.0f);
        batch.flush();
    }

    private void drawTexture(Texture texture, float x, float y) {
        int width = texture.getWidth();
        int height = texture.getHeight();

        batch.draw(texture,
         x, y,
         0.0f, 0.0f,
         width, height,
         WORLD_TO_SCREEN, WORLD_TO_SCREEN,
         0.0f,
         0, 0,
         width, height,
         false, false);
    }
}
