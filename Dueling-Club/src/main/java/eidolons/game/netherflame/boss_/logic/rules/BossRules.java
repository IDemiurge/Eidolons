package eidolons.game.netherflame.boss_.logic.rules;

import main.game.bf.Coordinates;
import main.game.logic.event.Event;

public interface BossRules {

    void handleEvent(Event e);

    void checkRoundEnds(Event e);

    boolean isMeleeCell(Coordinates c);

    void checkScriptRules();

    void newTurn();

    void roundStarts();

    void respawned();

    void newHeroChosen();

    void heroSacrificed();

    void death();

    void bossDeath();

    void allyDeath();

    void doubleDeath();

    /*
    melee rules

    death rules

    round rules

    create triggers?

    special rules:
    - spawn minions
    -
     */
}
