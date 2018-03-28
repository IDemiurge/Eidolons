package main.client.cc.logic.items;

import main.content.enums.GenericEnums;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_MATERIAL_GROUP;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.values.properties.G_PROPS;
import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 10/31/2017.
 */
public class ItemMaster {
    public static boolean isBreakable(DC_HeroItemObj item) {
        if (item instanceof DC_QuickItemObj) {
            DC_QuickItemObj q = (DC_QuickItemObj) item;
            if (q.isCoating()) {
                return true;
            }
            if (q.isConcoction()) {
                return true;
            }
            if (q.isPotion()) {
                return true;
            }
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
}
