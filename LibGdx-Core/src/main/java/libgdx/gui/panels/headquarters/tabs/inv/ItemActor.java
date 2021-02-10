package libgdx.gui.panels.headquarters.tabs.inv;

import eidolons.entity.item.DC_HeroItemObj;
import libgdx.gui.panels.headquarters.HqSlotActor;
import libgdx.texture.Images;

/**
 * Created by JustMe on 4/18/2018.
 */
public class ItemActor extends HqSlotActor<DC_HeroItemObj>{
    public ItemActor(DC_HeroItemObj model) {
        super(model);
//        int size = UiMaster.getIconSize();
//        String path = getWeaponIconPath(entity);
//        if (entity != null) {
//            if (!C_OBJ_TYPE.ITEMS.equals(entity.getOBJ_TYPE_ENUM())) {
//                size = 128;
//            }
//        }
    }

    @Override
    protected String getOverlay(DC_HeroItemObj model) {
        return null;
    }

    @Override
    protected String getEmptyImage() {
        return Images.EMPTY_LIST_ITEM;
    }

}
