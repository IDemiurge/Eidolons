package eidolons.game.netherflame.boss.logic.rules;

import main.game.logic.event.Event;

public interface BossRules {

    void handleEvent(Event e);

    void newTurn();


    void respawned();

    void newHeroChosen();

    void death();


}
