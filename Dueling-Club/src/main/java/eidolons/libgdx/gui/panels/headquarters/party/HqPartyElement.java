package eidolons.libgdx.gui.panels.headquarters.party;

import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;

import java.util.List;

/**
 * Created by JustMe on 4/16/2018.
 */
public abstract class HqPartyElement extends TablePanel{
    protected List<HqHeroDataSource> dataSource;

    @Override
    public void updateAct(float delta) {
        dataSource = getUserObject();
        update(delta);
    }

    protected abstract void update(float delta);

    @Override
    public List<HqHeroDataSource> getUserObject() {
        return (List<HqHeroDataSource>) super.getUserObject();
    }
}
