package main.libgdx.video;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import main.system.auxiliary.data.FileManager;

import java.io.FileNotFoundException;

/**
 * Created by JustMe on 10/21/2017.
 */
public class VideoMaster {

    private VideoPlayer player;
    private CameraInputController inputController;

    public VideoPlayer getPlayer() {
        return player;
    }

    public void stop() {
        player.stop();
    }
        public VideoPlayer play(String path, int w, int h) {

        OrthographicCamera cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
        }

         player = VideoPlayerCreator.createVideoPlayer();
        player.resize(w, h);
        FileHandle file = new FileHandle(FileManager.getFile(path));
        try {
            player.play(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//            Gdx.input.setInputProcessor(new InputMultiplexer(
//             inputController = new CameraInputController(cam)));
//            Gdx.gl.glEnable(GL20.GL_CULL_FACE);
//            Gdx.gl.glCullFace(GL20.GL_BACK);

        return player;
    }

    public void playTestVideo() {
        play(getTestPath(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public String getTestPath() {
        return   "C:\\Eidolons\\art materials\\video\\Main_Menu.ogv"; //moneda.ogg
    }

    public CameraInputController getInputController() {
        return inputController;
    }
}
