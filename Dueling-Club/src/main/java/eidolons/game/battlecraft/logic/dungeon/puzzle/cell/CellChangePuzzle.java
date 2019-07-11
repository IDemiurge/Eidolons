package eidolons.game.battlecraft.logic.dungeon.puzzle.cell;

import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.objects.Door;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;

import java.util.List;

public class CellChangePuzzle {

    /**
     *  init data for puzzle
     *
     * point of entry - in code ?
     * perhaps,
     *
     * coordinates for key things:
     * - door to open
     * - manipulators
     * - bounds
     *
     * how to make it easier to debug?
     *
     * use more ()->
     *
     *
     * how to sync with SCRIPTS?
     *
     * >> Add mutator via subclasses only? Or in a more data-driven way for random later on?
     *
     */

    DC_Game game;
    LevelBlock block;

    Coordinates target;
    List<Coordinates> manipulators;

    String argument;

    Door doorEnter;
    Door doorExit;
    boolean sealBehind;

    //aha - so these mutators will be defined in 'scripts' ! with args.




}
