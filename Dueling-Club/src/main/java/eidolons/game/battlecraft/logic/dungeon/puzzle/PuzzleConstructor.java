package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.ability.conditions.AreaCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Veil;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums.PUZZLE_PUNISHMENT;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums.PUZZLE_RESOLUTION;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTrigger;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.system.ConditionsUtils;
import main.content.enums.GenericEnums;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.Condition;
import main.elements.conditions.NotCondition;
import main.elements.conditions.standard.LambdaCondition;
import main.elements.conditions.standard.PositionCondition;
import main.elements.triggers.Trigger;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.entity.ConditionMaster;

import static main.system.auxiliary.StringMaster.ALT_XML_SEPARATOR;

public abstract class PuzzleConstructor<T extends Puzzle> {

    protected T puzzle;
    protected PuzzleData puzzleData;
    protected String[] mutatorArgs;

    public PuzzleConstructor(String... mutatorArgs) {
        this.mutatorArgs = mutatorArgs;
    }


    public T create(String data, String blockData, Coordinates coordinates, LevelBlock block) {
        puzzle = createPuzzle();
        puzzle.setCoordinates(coordinates);
        puzzle.setBlock(block);

        puzzleData = createData(data);
        puzzle.setData(puzzleData);
        PuzzleResolution resolution = createResolutions(puzzleData);
        //        resolution.addPunishment();
        puzzle.setResolutions(resolution);
        //        puzzle.setup(setup);
        PuzzleRules rules = createRules(puzzleData);
        puzzle.setRules(rules);

        initEnterTrigger();
        initExitTrigger();
        preloadAssets();
        {
            Veil veil;
            Coordinates c = puzzle.getEntranceCoordinates();

            if (!isAreaEnter()) {
                puzzle.setEnterVeil(new Veil(puzzle, c, false, true));
            }
            if (isPointExit())
                if (!puzzleData.getValue(PuzzleData.PUZZLE_VALUE.EXIT).isEmpty()) {
                    c = puzzle.getExitCoordinates();
                    if (c != null) {
                        puzzle.setExitVeil(new Veil(puzzle, c, false, false));
                    }
                }
        }

        return puzzle;
    }

    protected void preloadAssets() {
        if (puzzle.getOverrideBackground() != null) {
            SpriteAnimationFactory.preload(puzzle.getOverrideBackground());
        }
    }


    protected PuzzleRules createRules(PuzzleData puzzleData) {
        return new PuzzleRules(puzzle);
    }

    protected PuzzleResolution createResolutions(PuzzleData puzzleData) {
        PuzzleResolution resolution = createResolution();
        resolution.setSolution(getSolution());
        for (String substring : ContainerUtils.openContainer(puzzleData.getValue(PuzzleData.PUZZLE_VALUE.PUNISHMENT),
                ALT_XML_SEPARATOR)) {
            String arg = VariableManager.getVars(substring);
            PUZZLE_PUNISHMENT p = new EnumMaster<PUZZLE_PUNISHMENT>().
                    retrieveEnumConst(PUZZLE_PUNISHMENT.class, VariableManager.removeVarPart(substring));
            resolution.addPunishment(p, arg);
        }

        for (String substring : ContainerUtils.openContainer(puzzleData.getValue(PuzzleData.PUZZLE_VALUE.RESOLUTION),
                ALT_XML_SEPARATOR)) {
            String arg = VariableManager.getVars(substring);
            PUZZLE_RESOLUTION p = new EnumMaster<PUZZLE_RESOLUTION>().
                    retrieveEnumConst(PUZZLE_RESOLUTION.class, VariableManager.removeVarPart(substring));
            resolution.addResolutions(p, arg);
        }
        return resolution;
    }

    protected abstract PuzzleEnums.PUZZLE_SOLUTION getSolution();

    protected PuzzleResolution createResolution() {
        return new PuzzleResolution(puzzle);
    }

    protected PuzzleData createData(String text) {
        PuzzleData data =null ;
        if (!text.contains(">>")) {
            data = new PuzzleData();
            PuzzleData.PUZZLE_VALUE[] values = getRelevantValues();
            int i = 0;
            for (String substring : ContainerUtils.openContainer(text, ",")) {
                data.setValue(values[i++], substring);
            }
            int coef = getDifficultyCoef(Eidolons.getGame().getMissionMaster().getOptionManager().getDifficulty());
            data.setValue(PuzzleData.PUZZLE_VALUE.DIFFICULTY_COEF, coef);
            int reward = puzzle.getSoulforceBase() * coef / 100;
            data.setValue(PuzzleData.PUZZLE_VALUE.SOULFORCE_REWARD, reward);
        } else {
            data = new PuzzleData(text);
        }
        return data;
    }

    protected int getDifficultyCoef(GenericEnums.DIFFICULTY difficulty) {
        switch (difficulty) {
            case NEOPHYTE:
                return 50;
            case NOVICE:
                return 80;
            case DISCIPLE:
                return 100;
            case ADEPT:
                return 130;
            case CHAMPION:
                return 150;
            case AVATAR:
                return 200;
        }
        return 100;
    }

    protected PuzzleData.PUZZLE_VALUE[] getRelevantValues() {
        return new PuzzleData.PUZZLE_VALUE[]{
                PuzzleData.PUZZLE_VALUE.WIDTH,
                PuzzleData.PUZZLE_VALUE.HEIGHT,
                PuzzleData.PUZZLE_VALUE.PUNISHMENT,
                PuzzleData.PUZZLE_VALUE.RESOLUTION,
                PuzzleData.PUZZLE_VALUE.TIP,
        };
    }

    protected abstract T createPuzzle();

    private PuzzleHandler getHandler() {
        return puzzle.getHandler();
    }


    protected void initExitTrigger() {
        createTriggerGlobal(PuzzleTrigger.PUZZLE_TRIGGER.EXIT,
                ConditionsUtils.join(new LambdaCondition(ref -> puzzle.active),
                        ConditionsUtils.fromTemplate(ConditionMaster.CONDITION_TEMPLATES.MAIN_HERO),
                        getPuzzleExitConditions()),
                () -> exited(),
                Event.STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING
        );
    }

    public void createTriggerGlobal(PuzzleTrigger.PUZZLE_TRIGGER type, Condition checks, Runnable action, Event.EVENT_TYPE event) {
        Trigger trigger = new PuzzleTrigger(puzzle, type, event, checks, action);
        Eidolons.getGame().getManager().addTrigger(trigger);
    }
    private void exited() {
        //TODO isApplyPunishment()
        if (isPointExit())
            if (Eidolons.getPlayerCoordinates().equals(puzzle.getExitCoordinates())) {
                return;
            }
        puzzle.failed();
    }

    protected void initEnterTrigger() {
         createTriggerGlobal(PuzzleTrigger.PUZZLE_TRIGGER.ENTER,
                ConditionsUtils.join(new LambdaCondition(ref -> !puzzle.active && (!puzzle.solved || isReplayable())),
                        ConditionsUtils.fromTemplate(ConditionMaster.CONDITION_TEMPLATES.MAIN_HERO),
                        getPuzzleEnterConditions()),
                () -> puzzle.getHandler().entered(), Event.STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING
        );

    }

    protected boolean isReplayable() {
        return false;
    }

    protected Condition getPuzzleExitConditions() {
        if (isAreaExit()) {
            return ConditionsUtils.or(new PositionCondition(
                            puzzle.getExitCoordinates()),
                    new NotCondition(new AreaCondition(puzzle.getCoordinates(), puzzle.getWidth(), puzzle.getHeight())));
        }
        return new PositionCondition(()->
                puzzle.getExitCoordinates());
    }


    protected Condition getPuzzleEnterConditions() {
        if (isAreaEnter()) {
            AreaCondition areaCondition = new AreaCondition(puzzle.getCoordinates(), puzzle.getWidth(), puzzle.getHeight());
            if (puzzle.getExitCoordinates() == null) {
                return areaCondition;
            }
            PositionCondition condition = new PositionCondition(
                    puzzle.getExitCoordinates());
            return ConditionsUtils.or(condition,
                    areaCondition);
        }
        return new PositionCondition(
                puzzle.getEntranceCoordinates());
    }

    protected boolean isPointExit() {
        return false;
    }

    protected boolean isAreaEnter() {
        return false;
    }

    protected boolean isAreaExit() {
        return false;
    }

}
