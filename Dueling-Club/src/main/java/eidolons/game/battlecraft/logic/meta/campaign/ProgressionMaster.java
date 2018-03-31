package eidolons.game.battlecraft.logic.meta.campaign;

import eidolons.entity.obj.unit.Unit;

/**
 * Created by JustMe on 7/23/2017.
 */
public class ProgressionMaster {
    //auto-levelups, limitations, taking advice

    public void determineProgression(Unit hero) {
        //pre-made branches?

        if (checkAdvice(hero)) {
            if (takeAdviceForProgression(hero)) return;
        }

    }

    private boolean takeAdviceForProgression(Unit hero) {
        return false;
    }

    private boolean checkAdvice(Unit hero) {
        return false;
    }

    public enum FREE_HERO {
        ZAK,
        SVEN,
        TURGOND,
        SYMNAE,
        KAELRIN,
        GWYN,
        TOLSEN,
        JEZAL,
        HARLEN,
        JAIENNE,

    }
}
