package eidolons.ability.effects.oneshot.explore;

import eidolons.ability.effects.DC_Effect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.ability.effects.Effects;
import main.entity.Ref;

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
        if (allies.isEmpty())
            getGame().getMetaMaster().getPartyManager().getParty().addMember(getGame().getMetaMaster().getPartyManager().getParty().getLeader());
// mystery fix
        for (Unit sub : new LinkedList<>(allies)) {
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
            allies.remove(sub);
            continue;
            //remove
        }
        if (allies.isEmpty()) {
            FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.REQUIREMENT, "You need food supplies to camp!", ref.getSourceObj());
            return false;
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
