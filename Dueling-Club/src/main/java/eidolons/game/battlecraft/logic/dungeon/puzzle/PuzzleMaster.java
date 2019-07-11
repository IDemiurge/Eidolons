package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.game.battlecraft.logic.dungeon.puzzle.art.ArtPuzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Manipulator;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ArrayMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PuzzleMaster {
    DungeonMaster master;
    List<Puzzle> puzzles = new ArrayList<>();

    public PuzzleMaster(DungeonMaster master) {
        this.master = master;
    }

    public void initPuzzles(Dungeon dungeon, DungeonLevel dungeonLevel) {
        for (String coord : dungeon.getCustomDataMap().keySet()) {
            String s = dungeon.getCustomDataMap().get(coord);
            for (String substring : ContainerUtils.openContainer(s)) {
                if (substring.split("::")[0].trim().equalsIgnoreCase("puzzle")) {
                    Coordinates c = Coordinates.get(coord);
                    LevelBlock block = dungeonLevel.getBlockForCoordinate(c);
                    Puzzle puzzle =//PuzzleConstructor.
                            createPuzzle(dungeon.getCustomDataMap(), block, substring.split("::")[1], c);
                    puzzles.add(puzzle);

                }
            }
        }
    }

    private void initManipulator(Puzzle puzzle,
                                 Coordinates c, String data) {

        GuiEventManager.trigger(GuiEventType.INIT_MANIPULATOR, new Manipulator(puzzle,
                Manipulator.Manipulator_template.rotating_cross,
                c, data));
    }

    private Puzzle createPuzzle(Map<String, String> customDataMap, LevelBlock block,
                                String s, Coordinates coordinates) {
        /**
         * type(args...)
         */

        String name = VariableManager.removeVarPart(s);
        String args = VariableManager.getVars(s);

        String setupData = "";
        for (Coordinates c : block.getCoordinatesList()) {
            String data = customDataMap.get(c.toString());
            if (data != null) {
                setupData += VariableManager.getStringWithVariable(c.toString(), data) + ";";


            }

        }

        switch (new EnumMaster<puzzle_type>().retrieveEnumConst(puzzle_type.class, name)) {
            case art:
                return setupPuzzle(new ArtPuzzle("1"), args, setupData, coordinates);
            case maze:
                break;
        }

        return null;
    }

    private Puzzle setupPuzzle(Puzzle puzzle, String args, String setupData, Coordinates coordinates) {
//        puzzle.initArgs(args);
        puzzle.setCoordinates(coordinates);
        puzzle.setup(new PuzzleSetup(puzzle, ""));
        puzzle.setRules(new PuzzleRules(puzzle));
        puzzle.setResolutions(new PuzzleResolution(puzzle));

        for (String data : ContainerUtils.openContainer(setupData)) {
            if (data.contains("manip(")) {
                initManipulator(puzzle, Coordinates.get(VariableManager.removeVarPart(data)),
                        VariableManager.getVarPart(data));
            }
        }
        /**
         * we could skip this for now and do direct init!
         *
         * maybe just bind via shortcuts, to be generalized later
         *
         */
        return puzzle;
    }


    public enum puzzle_type {
        art,
        maze,

    }

    public enum puzzle_condition_template {

        pale_align,

        light_fit,

        item_placed,

        corpse,


    }

    public enum PUZZLE_ACTION_BASE {
        MOVE,
        MOVE_AFTER,


    }

    public enum PUZZLE_ACTION_MUTATOR {
        FACING,

    }

    public enum PUZZLE_SOLVE_MUTATOR {
        ATTACK,
        MESSAGE,

    }

    public enum PUZZLE_TIMED_MUTATOR {
        FADING_LIGHT,
        FALLING_CEILING,
        RISING_WATER,

    }

    public enum PUZZLE_RULE_MUTATOR {
        TELEPORTERS,

    }

    public enum PUZZLE_SOLUTION {
        ALL_SAME,
        SHAPE,
        PATH,

        FIND_SECRET,
        DISCOVER_PATTERN,

    }
    /**
     * cells
     * geometry
     *
     * levers and pits
     *
     * our own specials
     * walls
     *
     * LIGHT
     *
     * overlaying placement
     * > Align real-world and pale aspect
     *
     * Creating puzzles:
     *
     * Script/
     * Triggers for completion conditions
     *
     *
     * Parse from text
     *
     * templates
     *
     * Win conditions
     *
     * Effects
     * unseal door, remove obstacle, additional scripts (spawn enemies, ?
     * show memory
     * start dialogue
     * * the usual !
     *
     * Tips (when is it enacted? how is it highlighted ? )
     * > Tips when doing related action
     * > Tips when 'failing'
     *
     *
     *
     *
     *
     *
     *
     *
     */
}
