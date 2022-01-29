package libgdx.gui.dungeon.panels.headquarters.tabs.inv;

import eidolons.entity.item.HeroItem;
import libgdx.gui.dungeon.panels.headquarters.HqSlotActor;
import eidolons.content.consts.Images;

/**
 * Created by JustMe on 4/18/2018.
 */
public class ItemActor extends HqSlotActor<HeroItem>{
    public ItemActor(HeroItem model) {
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
    protected String getOverlay(HeroItem model) {
        return null;
    }

    @Override
    protected String getEmptyImage() {
        return Images.EMPTY_LIST_ITEM;
    }

}
