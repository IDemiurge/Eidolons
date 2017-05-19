package main.ability.effects.oneshot;

import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.game.bf.Coordinates;

public class ConsumeCorpseEffect extends MicroEffect implements OneshotEffect {

    @Override
    public boolean applyThis() {

        Coordinates c = ref.getTargetObj().getCoordinates();
        ref.getGame().getGraveyardManager().destroyTopCorpse(c);
        return true;
    }

}
