package main.ability.effects;

import main.ability.effects.oneshot.MicroEffect;
import main.client.cc.logic.spells.DivinationMaster;
import main.entity.obj.DC_HeroObj;

public class DivinationEffect extends MicroEffect {

    @Override
    public boolean applyThis() {
        DC_HeroObj hero = (DC_HeroObj) ref.getSourceObj();
        try {
            DivinationMaster.divine(hero);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
