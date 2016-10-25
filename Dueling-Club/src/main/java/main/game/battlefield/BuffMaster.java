package main.game.battlefield;

import main.ability.effects.AddBuffEffect;
import main.ability.effects.Effect;
import main.content.CONTENT_CONSTS.BUFF_TYPE;
import main.content.CONTENT_CONSTS.STD_BOOLS;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.obj.specific.BuffObj;
import main.entity.obj.top.DC_ActiveObj;
import main.entity.type.ObjType;
import main.system.ai.logic.target.EffectMaster;

import java.util.LinkedList;
import java.util.List;

public class BuffMaster {
    public static void applyBuff(String name, Effect effect, Obj target) {
        applyBuff(name, effect, target, null);
    }

    public static void applyBuff(Effect effect, Obj target, Integer duration) {
        applyBuff(null, effect, target, duration);

    }

    public static void applyBuff(Effect effect, Obj target) {
        applyBuff(null, effect, target, null);
    }

    public static void applyBuff(String buffName, Effect effect, Obj target,
                                 Integer duration) {
        AddBuffEffect addBuffEffect = new AddBuffEffect(buffName, effect);
        if (duration != null)
            addBuffEffect.setDuration(duration);
        addBuffEffect.apply(Ref.getSelfTargetingRefCopy(target));

    }

    public static boolean checkBuffDispelable(BuffObj buff) {
        if (buff.getBuffType() == BUFF_TYPE.SPELL)
            if (!buff.isPermanent())
                if (!buff.checkBool(STD_BOOLS.NON_DISPELABLE))
                    return true;

        return false;
    }

    public static List<ObjType> getBuffsFromSpell(DC_ActiveObj spell) {
        List<ObjType> buffTypes = new LinkedList<>();
        for (Effect e : EffectMaster.getEffectsOfClass(spell.getAbilities(),
                AddBuffEffect.class)) {
            ObjType buffType = ((AddBuffEffect) e).getBuffTypeLazily();

            if (buffType != null)
                buffTypes.add(buffType);
        }

        return buffTypes;
    }

}
