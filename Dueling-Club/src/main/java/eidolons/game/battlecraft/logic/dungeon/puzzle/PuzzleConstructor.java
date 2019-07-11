package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.game.bf.Coordinates;

public class PuzzleConstructor {

    /**
     * data format
     *
     * coordinates
     * mutators
     *
     * main
     *
     * use ability-like?
     *
     * type
     *
     * supposedly I'd do this by hand
     *
     * on cells with data in LE?
     *
     * Sounds good actually, so first we gotta assemble the shit
     *
     */
    public Puzzle createPuzzle(LevelBlock block, Dungeon dungeon){

        for (String s : dungeon.getCustomDataMap().keySet()) {

            if (block.getCoordinatesList().contains(new Coordinates(s))){



            }

        }



        Puzzle puzzle = new Puzzle();



        return puzzle;
    }
}
