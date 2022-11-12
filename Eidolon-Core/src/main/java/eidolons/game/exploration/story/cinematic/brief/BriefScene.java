package eidolons.game.exploration.story.cinematic.brief;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.Scene;

public class BriefScene implements Scene {
    BriefingData data;
    private boolean done;

    public BriefScene(BriefingData data) {
        this.data = data;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public BriefingData getData() {
        return data;
    }
}
