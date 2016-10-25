package main.ability.effects.oneshot.special;

import main.ability.effects.oneshot.MicroEffect;
import main.game.battlefield.Coordinates;

public class ConsumeCorpseEffect extends MicroEffect {

    @Override
    public boolean applyThis() {

        Coordinates c = ref.getTargetObj().getCoordinates();
        ref.getGame().getGraveyardManager().destroyTopCorpse(c);
        return true;
    }

}
