package eidolons.libgdx.gui.panels.headquarters.weave;

import eidolons.libgdx.screens.GameScreen;

/**
 * Created by JustMe on 6/4/2018.
 */
public class WeaveScreen extends GameScreen {

    WeaveSpace space;
    WeaveUi ui;


    @Override
    protected void preLoad() {
        space = new WeaveSpace();
//        ui = new WeaveUi();
    }

    @Override
    protected void afterLoad() {

    }
    @Override
    protected void renderMain(float delta) {
        super.renderMain(delta);
        space.act(delta);
        ui.act(delta);
        space.draw();
        ui.draw();
    }
    @Override
    public void reset() {
        super.reset();
    }


    @Override
    protected boolean isLoadingWithVideo() {
        return super.isLoadingWithVideo();
    }
}
