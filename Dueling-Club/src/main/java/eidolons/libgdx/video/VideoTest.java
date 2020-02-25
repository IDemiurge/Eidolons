package eidolons.libgdx.video;

import java.io.FileNotFoundException;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

public class VideoTest extends Game implements ApplicationListener, InputProcessor {
    public static final String PATH = "C:\\soft\\steamworks_sdk_144\\sdk\\tools\\ContentBuilder\\content\\windows\\resources\\video\\main_menu_slow_original_size.ogv";
    public PerspectiveCamera cam;
    public CameraInputController inputController;
    public ModelInstance instance;
    public Environment environment;

    public VideoPlayer videoPlayer;
    public Mesh mesh;

    Stage stage;
public static void main(String[] dsf){
    new LwjglApplication(new VideoTest());
}
    @Override
    public void create () {
        stage = new Stage(new ScreenViewport());
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        LabelStyle lstyle =  new LabelStyle(new BitmapFont(), Color.WHITE);

        Label l = new Label("Stage is here!", lstyle);
        Table t = new Table();
        t.add(l).expand().fill();
        t.setFillParent(true);

        stage.addActor(t);

        videoPlayer = VideoPlayerCreator.createVideoPlayer();//cam, mesh, GL20.GL_TRIANGLES);
        videoPlayer.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);
    }
    private boolean initialized = false;

    @Override
    public void render () {
        if(!initialized) {
            try {
                FileHandle fh = Gdx.files.absolute(
                        "C:\\soft\\steamworks_sdk_144\\sdk\\tools\\ContentBuilder\\content\\windows\\resources\\video\\main_menu_slow_original_size.ogv");
                Gdx.app.log("TEST", "Loading file : " + fh.file().getAbsolutePath());
                videoPlayer.play(fh);
            } catch (FileNotFoundException e) {
                Gdx.app.log("TEST", "Err: " + e);
            }
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        if (!videoPlayer.render()) { // As soon as the video is finished, we start the file again using the same player.
            try {
                videoPlayer.play(Gdx.files.internal(PATH));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        //stage.getBatch().begin();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        //stage.getBatch().end();
    }

    @Override
    public void dispose () {
    }

    public boolean needsGL20 () {
        return true;
    }

    public void resume () {
    }

    public void resize (int width, int height) {
        if(stage.getWidth() != width || stage.getHeight() != height)
            stage.getViewport().update(width, height, true);

        if(videoPlayer != null)
            videoPlayer.resize(width, height);
    }

    public void pause () {
    }

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }
    }
