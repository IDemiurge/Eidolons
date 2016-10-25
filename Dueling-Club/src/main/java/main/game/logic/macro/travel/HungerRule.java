package main.game.logic.macro.travel;

import main.content.parameters.MACRO_PARAMS;
import main.entity.obj.DC_HeroObj;
import main.game.logic.macro.MacroManager;
import main.game.logic.macro.rules.TurnRule;

public class HungerRule extends TurnRule {
    private static final float HEALTH_REDUCTION_FACTOR = 0.1f;

    public void newTurn() {
        for (MacroParty p : MacroManager.getGame().getParties()) {
            // TODO check turn is 'normal' - not a chase/flight, nor a battle or
            // march
            consumeProvisions(p);
        }
    }

    private void consumeProvisions(MacroParty p) {
        p.modifyParameter(MACRO_PARAMS.C_PROVISIONS,
                -p.getIntParam(MACRO_PARAMS.CONSUMPTION));
        if (p.getIntParam(MACRO_PARAMS.C_PROVISIONS) < 0) {
            int hungerAmount = p.getIntParam(MACRO_PARAMS.C_PROVISIONS);
            // choose who gets the last provisions or to split equally
            p.setParam(MACRO_PARAMS.C_PROVISIONS, 0);
            // reduce health, motivation, maybe other params too... check alive!
            // :)
            // hunger parameter? starvation buff that increases init/atk/sp but
            // reduces foc/def/ess
            // increase speed, reduce
            // stealth/detection
            for (DC_HeroObj hero : p.getMembers()) {
                hero.modifyParameter(MACRO_PARAMS.HUNGER, hungerAmount);
                applyHunger(hero);
            }
        } else {
            for (DC_HeroObj hero : p.getMembers()) {
                int hungerSated = hero.getIntParam(MACRO_PARAMS.HUNGER);
                if (hungerSated > p.getIntParam(MACRO_PARAMS.C_PROVISIONS))
                    hungerSated = p.getIntParam(MACRO_PARAMS.C_PROVISIONS);

                hero.modifyParameter(MACRO_PARAMS.HUNGER, -hungerSated);
                p.modifyParameter(MACRO_PARAMS.C_PROVISIONS, -hungerSated);

            }
        }

    }

    private void applyHunger(DC_HeroObj hero) {
        int hunger = hero.getIntParam(MACRO_PARAMS.HUNGER);

        // continuous vs oneshot ... each turn

        hero.modifyParameter(MACRO_PARAMS.HEALTH,
                -(Math.round(hunger * HEALTH_REDUCTION_FACTOR)));

        // 'buff rule' - hungry, starved
        // HEALTH
        // ONENESS
        // LOYALTY

    }

}
