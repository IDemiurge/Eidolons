package libgdx.gui.dungeon.panels.headquarters;

import libgdx.gui.dungeon.panels.headquarters.datasource.HqDataMaster;
import libgdx.gui.dungeon.panels.headquarters.datasource.hero.HqHeroDataSource;

/**
 * Created by JustMe on 4/17/2018.
 */
public interface HqActor {

    default void modelChanged() {
        try {
            HqDataMaster.modelChanged(getUserObject().getEntity());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    HqHeroDataSource getUserObject();
}
