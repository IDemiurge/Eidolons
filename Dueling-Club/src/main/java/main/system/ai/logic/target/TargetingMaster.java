package main.system.ai.logic.target;

import main.ability.Ability;
import main.ability.effects.Effect;
import main.ability.effects.oneshot.common.SpecialTargetingEffect;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.Targeting;
import main.entity.obj.ActiveObj;
import main.entity.obj.top.DC_ActiveObj;
import main.system.auxiliary.ClassMaster;

import java.util.List;

public class TargetingMaster {
    public static Targeting findTargeting(ActiveObj active) {
        return findTargeting(active, null);
    }

    public static Targeting getZoneEffect(DC_ActiveObj active) {
        List<Effect> zoneEffects = EffectMaster.getEffectsOfClass(active,
                SpecialTargetingEffect.class);
        if (!zoneEffects.isEmpty()) {
            SpecialTargetingEffect zoneEffect = (SpecialTargetingEffect) zoneEffects
                    .get(0);
            zoneEffect.initTargeting();
            return zoneEffect.getTargeting();
        }
        return active.getTargeting();
    }

    public static Targeting findTargeting(ActiveObj active,
                                          Class<SelectiveTargeting> CLASS) {
        Targeting t = active.getTargeting();
        if (checkTargeting(CLASS, t))
            return t;

        t = findTargetingInAbils(active, CLASS);
        if (t != null)
            return t;

        for (ActiveObj a : active.getActives()) {
            if (active instanceof DC_ActiveObj)// 2 layers maximum, i hope
                t = findTargeting(a, CLASS);
            if (t != null)
                return t;
            else {
                for (ActiveObj a2 : a.getActives()) {
                    t = findTargetingInAbils(a2, CLASS);
                    if (t != null)
                        return t;
                }
            }
        }
        return null;
    }

    private static boolean checkTargeting(Class<SelectiveTargeting> CLASS,
                                          Targeting t) {
        if (CLASS == null && t != null)
            return true;
        return ClassMaster.isInstanceOf(t, CLASS);
    }

    public static Targeting findTargetingInAbils(ActiveObj active,
                                                 Class<SelectiveTargeting> CLASS) {
        if (active.getAbilities() != null)
            for (Ability abil : active.getAbilities())
                if (abil.getTargeting() != null)
                    // if (abil.getTargeting().getClass().equals(CLASS))
                    if (checkTargeting(CLASS, abil.getTargeting()))
                        return abil.getTargeting();
        return null;
    }

}
