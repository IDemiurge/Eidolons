package main.libgdx.video;

/**
 * Created by JustMe on 10/21/2017.
 */

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import main.client.dc.Launcher;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;

import java.io.File;
import java.io.FileNotFoundException;

public class TestVideoMaster extends ApplicationAdapter implements InputProcessor {
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 target = new Vector3();
    public OrthographicCamera cam;
    public CameraInputController inputController;
    public ModelInstance instance;
    public Environment environment;
    public VideoPlayer videoPlayer;
    public Mesh mesh;

    public static void main(String[] args) {
        TestVideoMaster a = new TestVideoMaster();
        new LwjglApplication(a, getConf());
    }


    protected static LwjglApplicationConfiguration getConf() {
//        Eidolons. getApplication().getGraphics().setFullscreenMode();
        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
        conf.title = "Eidolons: Battlecraft v" + Launcher.VERSION;
//        if (Gdx.graphics.isGL30Available())
        conf.useGL30 = true;
        OptionsMaster.init();

        conf.fullscreen = //false;
         OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.FULLSCREEN);

        conf.foregroundFPS = 60;
        conf.backgroundFPS = -1;
        conf.width = 1600;
        conf.height = 900;
        return conf;
    }

    @Override
    public void create() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 0);
        cam.lookAt(0, 0, 0);
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        MeshBuilder meshBuilder = new MeshBuilder();
        meshBuilder.begin(Usage.Position | Usage.TextureCoordinates, GL20.GL_POINTS);
        // @formatter:off
        try {
            meshBuilder.rect((short) 0, (short) 0, (short) Gdx.graphics.getWidth(), (short) Gdx.graphics.getHeight());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }      // @formatter:on
        mesh = meshBuilder.end();
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        videoPlayer.resize(1200, 600);
        try {
            videoPlayer.play(new FileHandle(new File
             ("C:\\Eidolons\\art materials\\video\\moneda.ogg")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Gdx.input.setInputProcessor(new InputMultiplexer(this, inputController = new CameraInputController(cam)));
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);
    }

    @Override
    public void render() {
        inputController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

//        final float delta = Gdx.graphics.getDeltaTime();
//        tmpV1.set(cam.direction).crs(cam.up).y = 0f;
//        cam.rotateAround(target, tmpV1.nor(), delta * 20);
//        cam.rotateAround(target, Vector3.Y, delta * -30);
//        cam.update();

        if (!videoPlayer.render()) { // As soon as the video is finished, we start the file again using the same player.
            try {
                videoPlayer.play(new FileHandle(new File
                 ("C:\\Eidolons\\art materials\\video\\moneda.ogg")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose() {
    }

    public boolean needsGL20() {
        return true;
    }

    public void resume() {
    }

    public void resize(int width, int height) {
    }

    public void pause() {
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
