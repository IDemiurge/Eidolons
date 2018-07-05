package eidolons.libgdx.gui.panels.headquarters.creation;

import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;

/**
 * Created by JustMe on 7/4/2018.
 */
public abstract class HcElement extends HqElement {

    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }

}
