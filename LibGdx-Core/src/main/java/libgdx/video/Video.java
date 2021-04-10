package libgdx.video;

import com.badlogic.gdx.video.VideoPlayer;

public class Video {
    VideoPlayer player;

    boolean looping;


    public Video(VideoPlayer player) {
        this.player = player;
//        player.setOnCompletionListener(this);
//        player.setOnVideoSizeListener(this);
        player.render();
    }

    public void render(){

    }
}
