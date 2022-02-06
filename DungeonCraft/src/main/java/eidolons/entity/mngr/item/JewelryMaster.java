package eidolons.entity.mngr.item;

/**
 * Created by JustMe on 4/8/2018.
 */

import main.content.enums.entity.ItemEnums;
import main.content.values.properties.G_PROPS;
import main.entity.type.ObjType;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;

public class JewelryMaster {

    public static final int AMULET_INDEX = 2;
    public static final int LIST_SIZE = 5;

    public static List<ObjType> getSortedJewelryData(List<ObjType> data) {

        List<ObjType> sortedData = new ArrayList<>();
        ListMaster.fillWithNullElements(sortedData, LIST_SIZE);
        int rings = 0;
        boolean left = true;
        for (ObjType type : data) {
            if (type.checkProperty(G_PROPS.JEWELRY_TYPE, "" + ItemEnums.JEWELRY_TYPE.AMULET)) {
                sortedData.set(AMULET_INDEX, type);
            } else {
                int index = rings;
                if (!left) {
                    index++;
                } else {
                    rings++;
                }
                if (index >= AMULET_INDEX) {
                    index++;
                }
                if (index >= sortedData.size()) {
                    return sortedData;
                }
                left = !left;
                sortedData.set(index, type);
            }
        }

        return sortedData;
    }

}
