package main.libgdx.screens;

import main.libgdx.stage.HeadQuarterStage;

public class HeadquarterScreen extends ScreenWithVideoLoader {
    private HeadQuarterStage headQuarterStage;

    @Override
    protected void afterLoad() {

    }

    @Override
    protected void preLoad() {
        super.preLoad();
        headQuarterStage = new HeadQuarterStage();
//        libraryPanel  = new LibraryPanel();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (canShowScreen()) {
            headQuarterStage.act(delta);
            headQuarterStage.draw();
        }
    }
}
