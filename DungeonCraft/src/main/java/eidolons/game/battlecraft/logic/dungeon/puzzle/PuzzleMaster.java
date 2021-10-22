package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.dungeon.puzzle.art.ArtPuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.encounter.EncPuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.VoidMazeConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTrigger;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.exploration.dungeons.struct.LevelBlock;
import eidolons.game.exploration.dungeons.struct.LevelStruct;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.Map;

import static eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums.puzzle_type;

public class PuzzleMaster {
    public static final boolean TEST_MODE = true;
    DungeonMaster master;
    DequeImpl<Puzzle> puzzles = new DequeImpl<>();
    DequeImpl<Puzzle> activePuzzles = new DequeImpl<>();

    public PuzzleMaster(DungeonMaster master) {
        this.master = master;
    }

    public boolean isUiMinimized() {
        if (getCurrent() == null) {
            return false;
        }
        return getCurrent().isMinimizeUI();
    }

    public Puzzle getCurrent() {
        if (activePuzzles.isEmpty()) {
            return null;
        }
        return activePuzzles.get(0);
    }

    public void playerActionDone(DC_ActiveObj action) {
        for (Puzzle activePuzzle : activePuzzles) {
            activePuzzle.getHandler().playerActionDone(action);
        }
    }

    public void processEvent(Event event) {
        for (Puzzle activePuzzle : activePuzzles) {
            for (PuzzleTrigger trigger : activePuzzle.getTriggers()) {
                trigger.check(event);
            }
            activePuzzle.getHandler().handleEvent(event);
        }
    }

    public void init(Map<Coordinates, CellScriptData> textDataMap) {
        for (Coordinates c : textDataMap.keySet()) {
            String s = textDataMap.get(c).getValue(CellScriptData.CELL_SCRIPT_VALUE.puzzles);
            if (!StringMaster.isEmpty(s))
                for (String substring : ContainerUtils.openContainer(s)) {
                    try {
                        LevelStruct struct = master.getStructMaster().getLowestStruct(c);
                        LevelBlock block = null;
                        if (struct instanceof LevelBlock) {
                            block = ((LevelBlock) struct);
                        }
                        Puzzle puzzle =//PuzzleConstructor.
                                createPuzzle(textDataMap, block, substring, c);
                        puzzles.add(puzzle);

                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }

                }
        }
    }

    public void activated(Puzzle puzzle) {
        activePuzzles.add(puzzle);

    }

    public void deactivated(Puzzle puzzle) {
        activePuzzles.remove(puzzle);
        if (puzzle.isMinimizeUI())
            GuiEventManager.trigger(GuiEventType.PUZZLE_FINISHED, puzzle);
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING_RESET);
    }


    private Puzzle createPuzzle(Map<Coordinates, CellScriptData> customDataMap, LevelBlock block,
                                String dataString, Coordinates coordinates) {
        /**
         * type(args...) or DataUnit
         */

        String name = VariableManager.removeVarPart(dataString);
        String args = VariableManager.getVars(dataString);
        if (dataString.contains(">>")) {
            name = new PuzzleData(dataString).getValue(PuzzleData.PUZZLE_VALUE.TYPE);
            args = dataString;
        }

        Map<Coordinates, CellScriptData> setupData =new HashMap<>();
        if (block != null)
        for (Coordinates c : block.getCoordinatesSet()) {
            CellScriptData data = customDataMap.get(c);
            if (data != null) {
                setupData.put(c, data);
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
            case voidmaze:
                return new VoidMazeConstructor(args);
            case encounter:
                return new EncPuzzleConstructor(args);
        }
        return null;
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
