package eidolons.libgdx.gui.panels.headquarters.tabs.inv;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;

/**
 * Created by JustMe on 4/18/2018.
 */
public class StashPanel extends ValueTable<DC_HeroItemObj, ItemActor>{

    public StashPanel(int wrap, int size) {
        super(wrap, size);
    }

    @Override
    protected ItemActor createElement(DC_HeroItemObj datum) {
        return null;
    }

    @Override
    protected ItemActor[] initActorArray() {
        return new ItemActor[0];
    }

    @Override
    protected DC_HeroItemObj[] initDataArray() {
        return new DC_HeroItemObj[0];
    }
}
