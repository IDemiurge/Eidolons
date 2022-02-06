package eidolons.netherflame.feature.reflectItem.data;

import eidolons.content.PARAMS;
import main.content.enums.entity.SkillEnums;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.entity.Entity;
import main.system.auxiliary.data.MapMaster;
import org.apache.commons.collections4.map.UnmodifiableSortedMap;

import java.util.Map;
import java.util.SortedMap;

import static main.content.enums.entity.SkillEnums.MASTERY.*;

public class WeaponMasteryData {
    private static final MASTERY[] MASTERY_TYPES = {
            BLUNT_MASTERY,
            AXE_MASTERY,
            BLADE_MASTERY,
            POLEARM_MASTERY,
            MARKSMANSHIP_MASTERY,
            UNARMED_MASTERY,
    };

    Map<SkillEnums.MASTERY, Integer> rankMap;

    int dual;
    int twohanded;

    public WeaponMasteryData(Entity source) {
        for (MASTERY masteryType : MASTERY_TYPES) {
            rankMap.put(masteryType, source.getIntParam(masteryType.getParam()));
        }
        MapMaster.sortByValue(rankMap);
        dual = source.getIntParam(PARAMS.DUAL_WIELDING_MASTERY);
        twohanded = source.getIntParam(PARAMS.TWO_HANDED_MASTERY);
    }

    public MASTERY getGreatest(int offset) {
        if (offset>rankMap.size())
            throw new RuntimeException("Offset greater than map size!");
        return (MASTERY) rankMap.keySet().toArray()[offset];
    }
}
