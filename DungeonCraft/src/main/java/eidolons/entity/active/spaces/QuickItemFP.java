package eidolons.entity.active.spaces;

import eidolons.content.PARAMS;
import eidolons.entity.item.ArmorItem;
import eidolons.entity.item.QuickItem;
import eidolons.entity.unit.Unit;
import main.system.datatypes.DequeImpl;
import org.apache.commons.lang3.text.StrBuilder;

import java.util.ArrayList;
import java.util.List;

public class QuickItemFP {

    public List<FeatSpaceData> split(DequeImpl<QuickItem> items, Unit unit) {
        List<FeatSpaceData> spaces =     new ArrayList<>();
        Integer bonus = unit.getIntParam(PARAMS.QUICK_SLOT_BONUS);
        //c_ is just useless now!
        FeatSpaceData space = createQIFP(unit.getArmor(), bonus, items);
        spaces.add(space);
        if (!items.isEmpty() && unit.getInnerArmor() != null) {
            space = createQIFP(unit.getInnerArmor(), bonus, items);
            spaces.add(space);
        }
        //TODO cloak?
        return spaces;
    }

    private FeatSpaceData createQIFP(ArmorItem armor, Integer bonus, DequeImpl<QuickItem> items) {
        Integer n = armor.getIntParam(PARAMS.QUICK_SLOTS);
        n = Math.min(6, n + bonus);
        StrBuilder sbuilder = new StrBuilder();
        for (int i = 0; i < n; i++) {
            QuickItem item = items.pop();
            sbuilder.append(item.getName());
        }

        FeatSpaceData data = new FeatSpaceData(sbuilder.toString());
        return data;
    }
}
