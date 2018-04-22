package eidolons.libgdx.gui.panels.headquarters.tabs.inv;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.libgdx.gui.panels.headquarters.HqSlotActor;
import eidolons.libgdx.texture.Images;

/**
 * Created by JustMe on 4/18/2018.
 */
public class ItemActor extends HqSlotActor<DC_HeroItemObj>{
    public ItemActor(DC_HeroItemObj model) {
        super(model);
    }

    @Override
    protected String getOverlay(DC_HeroItemObj model) {
        return null;
    }

    @Override
    protected String getEmptyImage() {
        return Images.EMPTY_ITEM;
    }
}
