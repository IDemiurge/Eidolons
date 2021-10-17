package eidolons.entity.active.spaces;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.advanced.engagement.EngageEvent;
import main.system.datatypes.DequeImpl;
import org.apache.commons.lang3.text.StrBuilder;

import java.util.ArrayList;
import java.util.List;

public class QuickItemFP {

    public List<FeatSpaceData> split(DequeImpl<DC_QuickItemObj> items, Unit unit) {
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

    private FeatSpaceData createQIFP(DC_ArmorObj armor, Integer bonus, DequeImpl<DC_QuickItemObj> items) {
        Integer n = armor.getIntParam(PARAMS.QUICK_SLOTS);
        n = Math.min(6, n + bonus);
        StrBuilder sbuilder = new StrBuilder();
        for (int i = 0; i < n; i++) {
            DC_QuickItemObj item = items.pop();
            sbuilder.append(item.getName());
        }

        FeatSpaceData data = new FeatSpaceData(sbuilder.toString());
        return data;
    }
}
