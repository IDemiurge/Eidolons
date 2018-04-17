package eidolons.libgdx.gui.panels.headquarters;

import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;

/**
 * Created by JustMe on 4/16/2018.
 */
public abstract class HqElement extends TablePanel {

    protected HqHeroDataSource dataSource;

    @Override
    public void updateAct(float delta) {
        dataSource = getUserObject();
        update(delta);
    }

    @Override
    public float getPrefWidth() {
        return getWidth();
    }
    @Override
    public float getPrefHeight() {
        return getHeight();
    }

    protected abstract void update(float delta);

    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }
}
