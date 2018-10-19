package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.texture.Images;
import main.content.enums.entity.ItemEnums.ITEM_GROUP;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;

/**
 * Created by JustMe on 10/18/2018.
 */
public class GoldMaster {
    public static final PARAMETER GOLD_VALUE = PARAMS.GOLD_COST;

    public static boolean checkGoldPack(Entity item, Unit hero) {
        if (isGoldPack(item)) {
            return false;
        }
        hero.modifyParameter(PARAMS.GOLD, item.getIntParam(GOLD_VALUE));
        return true;
    }

    public static boolean isGoldPack(Entity item) {
        return item.checkProperty(G_PROPS.ITEM_GROUP, ITEM_GROUP.GOLD_PACK.name());
    }

    public static boolean isGoldPacksOn() {
        return true;
    }

    public static String getImageVariant(Entity item) {
        Integer value = item.getIntParam(GOLD_VALUE);
        if (value>200){
            return Images.GOLD_PACK_LARGE;
        }
        if (value>50){
            return Images.GOLD_PACK_AVERAGE;
        }
        return Images.GOLD_PACK_SMALL;
    }
}
