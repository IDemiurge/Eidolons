package main.client.cc.gui.tabs;

import main.client.cc.gui.MainViewPanel;
import main.client.cc.gui.MainViewPanel.HERO_VIEWS;
import main.entity.obj.unit.DC_HeroObj;
import main.game.DC_Game;
import main.swing.generic.components.G_Panel;

public abstract class HeroPanelTab extends G_Panel {

    protected DC_HeroObj hero;
    protected HERO_VIEWS linkedView;
    protected MainViewPanel mvp;
    protected DC_Game game;
    protected String title;
    private boolean dirty;

    public HeroPanelTab(String title, MainViewPanel mvp, DC_HeroObj hero) {
        this.title = title;
        this.hero = hero;
        this.mvp = mvp;
        this.game = hero.getGame();
        initView();
        // refresh();
    }

    public void activate() {
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public abstract void initView();

    public HERO_VIEWS getLinkedView() {
        return linkedView;
    }

    public void setLinkedView(HERO_VIEWS linkedView) {
        this.linkedView = linkedView;
    }

    public void setHero(DC_HeroObj hero) {
        this.hero = hero;
    }

    public boolean isReinitDataRequired() {
        return false;
    }

}
