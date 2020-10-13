package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import eidolons.ability.conditions.puzzle.VoidCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleResolution;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleRules;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.libgdx.assets.Assets;
import eidolons.libgdx.particles.ParticlesSprite;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.OrConditions;
import main.elements.conditions.standard.PositionCondition;
import main.game.bf.Coordinates;
import main.system.launch.Flags;

import java.util.Map;

public class VoidMazeConstructor extends MazePuzzleConstructor {

    public VoidMazeConstructor(String... args) {
        super(args);
    }

    @Override
    protected PuzzleResolution createResolutions(PuzzleData puzzleData) {
        return super.createResolutions(puzzleData);
    }

    protected void preloadAssets() {
        super.preloadAssets();
        if (!Flags.isLiteLaunch()) {
            Assets.loadSprite(ParticlesSprite.PARTICLES_SPRITE.SNOW.path);
            // Assets.loadSprite(FullscreenAnims.FULLSCREEN_ANIM.GATE_FLASH.getSpritePath());
        }
    }

    @Override
    public MazePuzzle create(String data, Map<Coordinates, CellScriptData> blockData,
                             Coordinates coordinates, LevelBlock block) {
        return super.create(data, blockData, coordinates, block);
    }

    @Override
    protected boolean isAreaExit() {
        return false;
    }

    @Override
    protected PuzzleResolution createResolution() {
        return new PuzzleResolution(puzzle) {
            @Override
            protected Condition getFailConditions() {
                OrConditions conditions = new OrConditions();
                for (Coordinates falseExit : VoidMazeConstructor.this.
                        getPuzzle().getFalseExits()) {
                    conditions.add(new PositionCondition(puzzle.getAbsoluteCoordinate(falseExit)));
                }
                conditions.add(new VoidCondition());

                NotCondition notCondition = new NotCondition(new PositionCondition(puzzle.getEntranceCoordinates()));

                return new Conditions(notCondition, conditions);
            }

            @Override
            protected Condition getSolveConditions() {
                return super.getSolveConditions();
            }
        };
    }

    @Override
    protected PuzzleRules createRules(PuzzleData puzzleData) {
        return super.createRules(puzzleData);
    }

    private VoidMaze getPuzzle() {
        return (VoidMaze) puzzle;
    }

    @Override
    protected MazePuzzle createPuzzle() {
        return new VoidMaze();
    }

}
