package main.ability.conditions.special;

import main.ability.conditions.DC_Condition;
import main.entity.Ref;
import main.entity.item.DC_HeroItemObj;
import main.entity.obj.unit.Unit;
import main.system.auxiliary.SearchMaster;

import java.util.List;

/**
 * Created by JustMe on 11/16/2017.
 */
public class RestCondition extends DC_Condition {
    private int minDistance = 5;
    private String FOOD_ITEM = "Food";

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
            if (new SearchMaster<DC_HeroItemObj>().find(FOOD_ITEM, sub.getInventory(), true) != null)
                hasFood = true;
        }
        if (true)
            return true;
        return hasFood;
    }
}
