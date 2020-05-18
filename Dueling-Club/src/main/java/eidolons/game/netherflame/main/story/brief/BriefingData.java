package eidolons.game.netherflame.main.story.brief;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.Scene;

public class BriefingData implements Scene {

//    String loadVideo;
//    float videoDur;
//    boolean loopVideo;
//    float[] imgDur;
//    boolean playSlides;

    public String background;
    public String[] images;
    public String[] msgs;
    public  boolean autoPlay;
    public String backgroundSprite;

    public BriefingData(String backgroundSprite,String background,   String[] images, String[] msgs, boolean autoPlay ) {
        this.background = background;
        this.images = images;
        this.msgs = msgs;
        this.autoPlay = autoPlay;
        this.backgroundSprite = backgroundSprite;
    }

    public BriefingData(String background, String[] images, String[] msgs, boolean autoPlay) {
        this.background = background;
        this.images = images;
        this.msgs = msgs;
        this.autoPlay = autoPlay;
    }

    public BriefingData(String background, String  images, String  msgs, boolean autoPlay) {
        this.background = background;
        this.images = new String[]{
                images
        };
        this.msgs = new String[]{
                msgs
        };
        this.autoPlay = autoPlay;
    }
    public BriefingData(String[] images, String[] msgs, boolean autoPlay) {
        this.images = images;
        this.msgs = msgs;
        this.autoPlay = autoPlay;
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
