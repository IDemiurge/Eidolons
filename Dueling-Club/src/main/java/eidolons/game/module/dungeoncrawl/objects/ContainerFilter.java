package eidolons.game.module.dungeoncrawl.objects;

import main.content.DC_TYPE;
import main.content.enums.entity.DungeonObjEnums.CONTAINER_CONTENTS;
import main.content.enums.entity.ItemEnums.ITEM_RARITY;
import main.content.enums.entity.ItemEnums.WEAPON_TYPE;
import main.content.values.properties.G_PROPS;
import main.entity.type.ObjType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/3/2018.
 */
public class ContainerFilter {
    public static List<ObjType> filter(List<ObjType> list,
                                       CONTAINER_CONTENTS c, ITEM_RARITY rarity) {
        list = new ArrayList<>(list);
        list.removeIf(type -> isFilteredOut(type, c, rarity));

        return list;
    }

    private static boolean isFilteredOut(ObjType type, CONTAINER_CONTENTS c,
                                         ITEM_RARITY rarity) {
        DC_TYPE TYPE = (DC_TYPE) type.getOBJ_TYPE_ENUM();
        switch (TYPE) {
            case WEAPONS:
                if (type.getProperty(G_PROPS.WEAPON_TYPE).equalsIgnoreCase(WEAPON_TYPE.NATURAL.toString())) {
                    return true;
                }
                if (type.getProperty(G_PROPS.WEAPON_TYPE).equalsIgnoreCase(
                 WEAPON_TYPE.MAGICAL.toString())) {
                    return true;
                }
                if (type.getName().contains("Pistol")) return true;
                if (type.getName().contains("Bullet")) return true;
                break;
            case ITEMS:
//                if (type.getProperty(G_PROPS.ITEM_GROUP).equalsIgnoreCase(
//                 ITEM_GROUP.CONCOCTIONS.toString())) {
//                    return true;
//                }
//                if (type.getProperty(G_PROPS.ITEM_GROUP).equalsIgnoreCase(
//                 ITEM_GROUP.COATING.toString())) {
//                    return true;
//                }
                break;
        }


        return false;
    }
}
