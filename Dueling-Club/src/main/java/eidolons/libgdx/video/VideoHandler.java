package eidolons.libgdx.video;

import main.system.GuiEventManager;
import main.system.GuiEventType;

public class VideoHandler {

    public VideoHandler() {
        GuiEventManager.bind(GuiEventType.PLAY_VIDEO , p-> {
//            screen.setVideo(video);

        });
    }
}
