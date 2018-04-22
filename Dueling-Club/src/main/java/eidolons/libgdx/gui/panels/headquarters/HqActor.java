package eidolons.libgdx.gui.panels.headquarters;

import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;

/**
 * Created by JustMe on 4/17/2018.
 */
public interface HqActor {

    default void modelChanged() {
        HqDataMaster.modelChanged(getUserObject().getEntity());
    }

    HqHeroDataSource getUserObject();
}
