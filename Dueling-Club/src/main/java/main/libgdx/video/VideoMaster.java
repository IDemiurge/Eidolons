package main.libgdx.video;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import main.system.auxiliary.data.FileManager;

import java.io.FileNotFoundException;

/**
 * Created by JustMe on 10/21/2017.
 */
public class VideoMaster {
    VideoPlayer player =VideoPlayerCreator.createVideoPlayer();
    public void play(String path){
        FileHandle file= new FileHandle(FileManager.getFile(path));
        try {
            player.play(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
