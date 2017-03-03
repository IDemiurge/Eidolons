package main.ability.effects;

import main.ability.Abilities;
import main.content.enums.entity.SpellEnums;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.tools.target.EffectFinder;
import main.game.core.game.DC_Game;
import main.rules.magic.ResistanceRule;

public class DC_EffectManager implements EffectManager {

    private DC_Game game;

    public DC_EffectManager(DC_Game game) {
        this.game = game;
    }

    @Override
    public void setEffectRefs(Abilities abilities, Ref ref) {
        Effects effects = EffectFinder.getEffectsFromAbilities(abilities);
        for (Effect e : effects) {
            e.setRef(ref);
        }
    }

    @Override
    public void setEffectRefs(Abilities abilities) {
        setEffectRefs(abilities, abilities.getRef());
    }

    @Override
    public boolean checkNotResisted(Effect effect) {
        if (effect.isIrresistible()) {
            return true;
        }
        if (!checkEffectType(effect)) {
            return true;
        }

        Ref ref = effect.getRef();
        try {
            if (!checkTargeting(effect)) {
                return true;
            }
            if (effect instanceof ReducedEffect) {
                DC_ActiveObj spell = (DC_ActiveObj) ref.getActive();
                if (spell == null) {
                    return true;
                }
                if (spell.getResistanceType() != SpellEnums.RESISTANCE_TYPE.IRRESISTIBLE) {
                    if (spell.getResistanceType() != SpellEnums.RESISTANCE_TYPE.CHANCE_TO_BLOCK) {
                        int mod = ResistanceRule.getResistanceMod(ref);
                        ((ReducedEffect) effect).setResistanceMod(mod);
                        return true;
                    }
                }

            }
            if (checkResistanceAlreadyChecked(effect)) {
                return true;
            }
            return ResistanceRule.checkNotResisted(ref);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    private boolean checkResistanceAlreadyChecked(Effect effect) {
        try {
            DC_ActiveObj spell = (DC_ActiveObj) effect.getRef().getActive();
            boolean resistanceChecked = spell.isResistanceChecked();
            spell.setResistanceChecked(true);
            return resistanceChecked;
        } catch (Exception ignored) {

        }
        return false;
    }

    private boolean checkTargeting(Effect effect) {
        Obj targetObj = effect.getRef().getTargetObj();
        if (!(targetObj instanceof Unit)) {
            return false;
        }
        return targetObj.getOwner() != effect.getRef().getSourceObj()
                .getOwner();
    }

    private boolean checkEffectType(Effect effect) {

        return (effect instanceof ResistibleEffect);
    }

}
