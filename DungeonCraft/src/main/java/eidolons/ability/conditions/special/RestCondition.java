package eidolons.ability.conditions.special;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import main.entity.Ref;
import main.system.auxiliary.SearchMaster;

import java.util.List;

/**
 * Created by JustMe on 11/16/2017.
 */
public class RestCondition extends DC_Condition {
    private final int minDistance = 5;

    @Override
    public boolean check(Ref ref) {
        //check whole party!
        //check anyone has food
        List<Unit> allies = getGame().getMetaMaster().getPartyManager().getParty().getMembers();
        boolean hasFood = false;
        for (Unit sub : allies) {
            int distance = getGame().getAiManager().getAnalyzer().getClosestEnemyDistance(sub);
//            if (distance < minDistance)
//                return false;
            String FOOD_ITEM = "Food";
            if (new SearchMaster<DC_HeroItemObj>().find(FOOD_ITEM, sub.getInventory(), true) != null)
                hasFood = true;
        }
        if (true)
            return true;
        return hasFood;
    }
}
