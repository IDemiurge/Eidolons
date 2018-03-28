package main.libgdx.video;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import main.data.filesys.PathFinder;
import main.libgdx.GdxMaster;
import main.system.auxiliary.data.FileManager;

import java.io.FileNotFoundException;

/**
 * Created by JustMe on 10/21/2017.
 */
public class VideoMaster {

    private VideoPlayer player;
    private CameraInputController inputController;
    private Music audio;
    private boolean videoAvailable = true;
    private boolean available = true;

    public VideoPlayer getPlayer() {
        return player;
    }

    public void stop() {
        player.stop();
    }

    public VideoPlayer play(String path, int w, int h) {
        if (!videoAvailable) return null;
        OrthographicCamera cam = new OrthographicCamera(GdxMaster.getWidth(), GdxMaster.getHeight());
        cam.position.set(10f, 10f, 0);
        cam.lookAt(0, 0, 0);
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        MeshBuilder meshBuilder = new MeshBuilder();
        meshBuilder.begin(Usage.Position | Usage.TextureCoordinates, GL20.GL_POINTS);
        // @formatter:off
        try {
            meshBuilder.rect((short) 0, (short) 0, (short) GdxMaster.getWidth(), (short) GdxMaster.getHeight());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        player = VideoPlayerCreator.createVideoPlayer();
        player.resize(w, h);
        FileHandle file = new FileHandle(FileManager.getFile(path));
        try {
            player.play(file);
        } catch (FileNotFoundException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } catch (UnsatisfiedLinkError e) {
            // doesnt work on mac
            // TODO it probably should work, but release we are using doesnt include binaries for oses other then windows
            e.printStackTrace();
            // use dummy player so nothing explodes if it expects non null object with some luck
            player = new DummyVideoPlayer();
            videoAvailable = false;
        }
//            Gdx.input.setInputProcessor(new InputMultiplexer(
//             inputController = new CameraInputController(cam)));
//            Gdx.gl.glEnable(GL20.GL_CULL_FACE);
//            Gdx.gl.glCullFace(GL20.GL_BACK);

        return player;
    }

    public void playTestVideo() {
        play(getTestPath(), GdxMaster.getWidth(), GdxMaster.getHeight());
    }

    public String getTestPath() {
        return PathFinder.getVideoPath() + "Main_Menu_slow_original_size.ogv"; //moneda.ogg
    }

    public CameraInputController getInputController() {
        return inputController;
    }

    public boolean isAvailable() {
        return available;
    }

    private static class DummyVideoPlayer implements VideoPlayer {

        @Override
        public boolean play(FileHandle file) throws FileNotFoundException {
            return false;
        }

        @Override
        public boolean render() {
            return false;
        }

        @Override
        public boolean isBuffered() {
            return false;
        }

        @Override
        public void resize(int width, int height) {

        }

        @Override
        public void pause() {

        }

        @Override
        public void resume() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void setOnVideoSizeListener(VideoSizeListener listener) {

        }

        @Override
        public void setOnCompletionListener(CompletionListener listener) {

        }

        @Override
        public int getVideoWidth() {
            return 0;
        }

        @Override
        public int getVideoHeight() {
            return 0;
        }

        @Override
        public boolean isPlaying() {
            return false;
        }

        @Override
        public void dispose() {

        }
    }
}
