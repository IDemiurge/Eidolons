package eidolons.libgdx.gui.panels.headquarters;

import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;

/**
 * Created by JustMe on 4/16/2018.
 */
public abstract class HqElement extends TablePanel implements HqActor{

    protected HqHeroDataSource dataSource;

    @Override
    public void updateAct(float delta) {
        dataSource = getUserObject();
        update(delta);
    }

    protected abstract void update(float delta);

    @Override
    public float getPrefWidth() {
        return getWidth();
    }
    @Override
    public float getPrefHeight() {
        return getHeight();
    }

    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }
}
