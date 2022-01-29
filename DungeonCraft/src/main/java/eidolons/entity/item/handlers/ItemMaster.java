package eidolons.entity.item.handlers;

import eidolons.content.consts.VisualEnums.ITEM_FILTERS;
import eidolons.entity.item.HeroItem;
import eidolons.entity.item.QuickItem;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_MATERIAL_GROUP;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.values.properties.G_PROPS;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.launch.Flags;

/**
 * Created by JustMe on 10/31/2017.
 */
public class ItemMaster {
    public static final java.lang.String FOOD = "Food";
    public static final java.lang.String TORCH = "Torch";

    public static boolean isItemsDisabled() {
        return Flags.isJar();
    }
    public static boolean isBreakable(HeroItem item) {
        if (item instanceof QuickItem) {
            QuickItem q = (QuickItem) item;
            if (q.isCoating()) {
                return true;
            }
            if (q.isConcoction()) {
                return true;
            }
            return q.isPotion();
        }
        // material - glass?

        return false;
    }

    static boolean isRing(ObjType type) {
        return type.checkProperty(G_PROPS.JEWELRY_TYPE, ItemEnums.JEWELRY_TYPE.RING + "");
    }

    static boolean checkSpecialType(ObjType type) {

        return type.checkBool(GenericEnums.STD_BOOLS.SPECIAL_ITEM);
    }

    public static boolean checkMaterial(ObjType type, MATERIAL material) {
        return checkMaterial(type, material.getGroup());
    }

    public static boolean checkMaterial(ObjType type, ITEM_MATERIAL_GROUP group) {
        return StringMaster.compare(group.toString(),
         type.getProperty(G_PROPS.ITEM_MATERIAL_GROUP), true);
    }

    public static boolean checkFilter(HeroItem item, ITEM_FILTERS filter) {
        switch (filter) {
            case ALL:
                return true;
            case WEAPONS:
                return item.getOBJ_TYPE_ENUM() == DC_TYPE.WEAPONS;
            case ARMOR:
                return item.getOBJ_TYPE_ENUM() == DC_TYPE.ARMOR;
            case USABLE:
                return item.getOBJ_TYPE_ENUM() == DC_TYPE.ITEMS;
            case JEWELRY:
                return item.getOBJ_TYPE_ENUM() == DC_TYPE.JEWELRY;

            case QUEST:
                break;
        }
        return false;
    }

    public static boolean isGlovedFistOn() {
        return true;
    }
}
