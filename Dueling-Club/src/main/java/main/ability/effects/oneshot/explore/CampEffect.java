package main.ability.effects.oneshot.explore;

import main.ability.effects.DC_Effect;
import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.content.PARAMS;
import main.entity.Ref;
import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.unit.Unit;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 11/28/2017.
 */
public class CampEffect extends DC_Effect {
    @Override
    public boolean applyThis() {

        /*
        restore fully, dispel all timed buffs, apply bonus
         */
        List<Unit> allies = getGame().getMetaMaster().getPartyManager().getParty().getMembers();


        for (Unit sub :    new LinkedList<>( allies)) {
            DC_HeroItemObj item = sub.getItemFromInventory("Food");
            if (item != null) {
                sub.removeFromInventory(item);
                continue;
            }
            DC_QuickItemObj qitem = sub.getQuickItem("Food");
            if (item != null) {
                sub.removeQuickItem(qitem);
                continue;
            }
//            allies.remove(sub);
            continue;
            //remove
        }

       Effects restorationEffects = new Effects();
        restorationEffects.add(new ModifyValueEffect(PARAMS.C_ENDURANCE, MOD.SET_TO_PERCENTAGE,
         "125", true));
        restorationEffects.add(new ModifyValueEffect(PARAMS.C_FOCUS, MOD.SET_TO_PERCENTAGE,
         "70", true));
        restorationEffects.add(new ModifyValueEffect(PARAMS.C_ESSENCE, MOD.SET_TO_PERCENTAGE,
         "100", true));
        restorationEffects.add(new ModifyValueEffect(PARAMS.C_STAMINA, MOD.SET_TO_PERCENTAGE,
         "125", true));
        restorationEffects.add(new ModifyValueEffect(PARAMS.C_TOUGHNESS, MOD.SET_TO_PERCENTAGE,
         "100", true));
        for (Unit sub : allies) {
            Ref REF = sub.getRef().getTargetingRef(sub);
            restorationEffects.apply(REF);
        }
        float time = 20;
       getGame().getDungeonMaster().
         getExplorationMaster().getTimeMaster().playerRests(time);
//       getGame().getDungeonMaster().
//         getExplorationMaster().getTimeMaster().timePassed(time);

//            applyRested();
            for (Unit sub : allies) {
            }

        return true;
    }
}
