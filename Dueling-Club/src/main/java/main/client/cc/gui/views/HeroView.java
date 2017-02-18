package main.client.cc.gui.views;

import main.client.cc.gui.misc.PoolComp;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import main.swing.generic.components.G_Panel;

public abstract class HeroView extends G_Panel {
    protected static final String POOL = "Points to spend";

    // +description text
    protected Unit hero;
    protected PoolComp poolComp;
    protected boolean editable = false;
    private boolean dirty = false;

    // size

    public HeroView(Unit hero) {
        this.hero = hero;
        initPoolComp();
    }

    protected void updatePoolComp() {
        poolComp.update();
    }

    @Override
    public boolean isAutoSizingOn() {
        return false;
    }

    protected void initPoolComp() {
        poolComp = new PoolComp(hero, getPoolParam(), POOL, isPoolC());
        updatePoolComp();
    }

    private boolean isPoolC() {
        return false;
    }

    protected int getPoolWidth() {
        return 3;
    }

    public void refresh() {
        poolComp.setText(hero.getParam(getPoolParam()));
    }

    public abstract void init();

    public abstract void activate();

    public abstract PARAMS getPoolParam();

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Unit getHero() {
        return hero;
    }

    public void setHero(Unit hero) {
        this.hero = hero;
    }

    public HC_TabPanel getTabbedPanel() {
        return null;
    }

}
