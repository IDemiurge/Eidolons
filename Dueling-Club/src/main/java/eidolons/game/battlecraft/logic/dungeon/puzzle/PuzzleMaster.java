package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.game.battlecraft.logic.dungeon.puzzle.art.ArtPuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTrigger;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;

import java.util.Map;

public class PuzzleMaster {
    DungeonMaster master;
    DequeImpl<Puzzle> puzzles = new DequeImpl<>();
    DequeImpl<Puzzle> activePuzzles = new DequeImpl<>();

    public PuzzleMaster(DungeonMaster master) {
        this.master = master;
    }


    public void processEvent(Event event) {
        for (Puzzle activePuzzle : activePuzzles) {
            for (PuzzleTrigger trigger : activePuzzle.getTriggers()) {
                trigger.check(event);
            }
        }
    }

    public void init(Map<Coordinates, CellScriptData> textDataMap) {
        for (Coordinates c : textDataMap.keySet()) {
            String s = textDataMap.get(c).getValue(CellScriptData.CELL_SCRIPT_VALUE.puzzles);
            if (!StringMaster.isEmpty(s))
            for (String substring : ContainerUtils.openContainer(s)) {
                    try {
                        LevelStruct struct = master.getStructMaster().getLowestStruct(c);
                        if (struct instanceof LevelBlock) {
                            LevelBlock block = ((LevelBlock) struct);
                        Puzzle puzzle =//PuzzleConstructor.
                                createPuzzle(textDataMap, block, substring , c);
                        puzzles.add(puzzle);
                        }
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }

            }
        }
    }

    public void activated(Puzzle puzzle) {
        activePuzzles.add(puzzle);
        GuiEventManager.trigger(GuiEventType.PUZZLE_STARTED, puzzle);

    }

    public void deactivated(Puzzle puzzle) {
        activePuzzles.remove(puzzle);
        GuiEventManager.trigger(GuiEventType.PUZZLE_FINISHED, puzzle);
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING_RESET);
    }


    private Puzzle createPuzzle(Map<Coordinates, CellScriptData> customDataMap, LevelBlock block,
                                String dataString, Coordinates coordinates) {
        /**
         * type(args...)
         */

        String name = VariableManager.removeVarPart(dataString);
        String args = VariableManager.getVars(dataString);

        String setupData = "";
        for (Coordinates c : block.getCoordinatesSet()) {
            CellScriptData data = customDataMap.get(c );
            if (data != null) {
                setupData += VariableManager.getStringWithVariable(c.toString(),
                        data.getValue(CellScriptData.CELL_SCRIPT_VALUE.script)) + ";";
                //TODO
            }

        }
        PuzzleConstructor constructor = getPuzzleConstructor(name);
        return constructor.create(args, setupData, coordinates, block);
    }

    private PuzzleConstructor getPuzzleConstructor(String name) {
        String[] args = name.split("-");
        name = args[0];
        if (args.length > 1)
            args = args[1].split("_");
        else
            args = new String[0];
        switch (new EnumMaster<puzzle_type>().retrieveEnumConst(puzzle_type.class, name)) {
            case art:
                return new ArtPuzzleConstructor(args);
            case maze:
                return new MazePuzzleConstructor(args);
        }
        return null;
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
        MOVE_AFTER, ACTION, FACING,


    }

    public enum PUZZLE_ACTION {
        ROTATE_MOSAIC_CELL_CLOCKWISE,
        ROTATE_MOSAIC_CELL_ANTICLOCKWISE,

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
        GET_TO_EXIT,
        MOSAIC,
        SHAPE,
        PATH,

        SLOTS,

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
