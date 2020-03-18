package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.ability.conditions.AreaCondition;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleResolution.PUZZLE_PUNISHMENT;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleResolution.PUZZLE_RESOLUTION;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Veil;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTrigger;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster;
import eidolons.game.battlecraft.logic.meta.igg.pale.PaleAspect;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.system.ConditionsUtils;
import main.content.enums.GenericEnums;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.Condition;
import main.elements.conditions.NotCondition;
import main.elements.conditions.standard.LambdaCondition;
import main.elements.conditions.standard.PositionCondition;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.entity.ConditionMaster;

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
        initSetup();

        initEnterTrigger();
        initExitTrigger();
        boolean pale = puzzleData.getBooleanValue(PuzzleData.PUZZLE_VALUE.PALE);
        if (EidolonsGame.BRIDGE) {
            pale = true;
        }
        {
            Veil veil;
            Coordinates c = new Coordinates(puzzleData.getValue(PuzzleData.PUZZLE_VALUE.ENTRANCE)).negativeY()
                    .getOffset(puzzle.getCoordinates());

            if (!isAreaEnter()) {
                puzzle.setEnterVeil(veil = new Veil(puzzle, c, pale, true));
                GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, veil);
            }

            if (isPointExit())
                if (!puzzleData.getValue(PuzzleData.PUZZLE_VALUE.EXIT).isEmpty()) {
                    c = new Coordinates(puzzleData.getValue(PuzzleData.PUZZLE_VALUE.EXIT)).negativeY()
                            .getOffset(puzzle.getCoordinates());

                    puzzle.setExitVeil(veil = new Veil(puzzle, c, pale, false));
                    GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, veil);
                }
        }

        return puzzle;
    }



    protected PuzzleRules createRules(PuzzleData puzzleData) {
        return new PuzzleRules(puzzle);
    }

    protected PuzzleResolution createResolutions(PuzzleData puzzleData) {
        PuzzleResolution resolution = createResolution();
        resolution.setSolution(getSolution());
        for (String substring : ContainerUtils.openContainer(puzzleData.getValue(PuzzleData.PUZZLE_VALUE.PUNISHMENT),
                "|")) {
            String arg = VariableManager.getVars(substring);
            PUZZLE_PUNISHMENT p = new EnumMaster<PUZZLE_PUNISHMENT>().
                    retrieveEnumConst(PUZZLE_PUNISHMENT.class, VariableManager.removeVarPart(substring));
            resolution.addPunishment(p, arg);
        }

        for (String substring : ContainerUtils.openContainer(puzzleData.getValue(PuzzleData.PUZZLE_VALUE.RESOLUTION),
                "|")) {
            String arg = VariableManager.getVars(substring);
            PUZZLE_RESOLUTION p = new EnumMaster<PUZZLE_RESOLUTION>().
                    retrieveEnumConst(PUZZLE_RESOLUTION.class, VariableManager.removeVarPart(substring));
            resolution.addResolutions(p, arg);
        }
        return resolution;
    }

    protected abstract PuzzleMaster.PUZZLE_SOLUTION getSolution();

    protected PuzzleResolution createResolution() {
        return new PuzzleResolution(puzzle);
    }

    protected void initSetup() {
        PuzzleSetup setup = new PuzzleSetup(puzzle, puzzleData);
        puzzle.setup(setup);

    }

    protected PuzzleData createData(String text) {
        PuzzleData data = new PuzzleData();

        PuzzleData.PUZZLE_VALUE[] values = getRelevantValues();
        int i = 0;
        for (String substring : ContainerUtils.openContainer(text, ",")) {
            data.setValue(values[i++], substring);
        }
        int coef = getDifficultyCoef(Eidolons.getGame().getBattleMaster().getOptionManager().getDifficulty());
//        if () TODO disable by option
//            coef=100;
        data.setValue(PuzzleData.PUZZLE_VALUE.DIFFICULTY_COEF, coef);

        int reward = puzzle.getSoulforceBase() * coef / 100;
        data.setValue(PuzzleData.PUZZLE_VALUE.SOULFORCE_REWARD, reward);
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
                PuzzleData.PUZZLE_VALUE.PALE,
        };
    }

    protected abstract T createPuzzle();

    protected void entered() {
        if (puzzle.isPale()) {
            PaleAspect.enterPale();
        }
        if (!puzzle.isFailed() && !puzzle.getData().getValue(PuzzleData.PUZZLE_VALUE.TIP).isEmpty()) {
            TipMessageMaster.tip(puzzle.getData().getValue(PuzzleData.PUZZLE_VALUE.TIP),
                    () -> afterTipAction());
        } else {
            afterTipAction();
        }
        puzzle.activate();
    }

    protected void afterTipAction() {
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_COORDINATE, puzzle.getCenterCoordinates());
    }

    protected void initExitTrigger() {
        puzzle.createTriggerGlobal(PuzzleTrigger.PUZZLE_TRIGGER.EXIT,
                ConditionsUtils.join(new LambdaCondition(ref -> puzzle.active),
                        ConditionsUtils.fromTemplate(ConditionMaster.CONDITION_TEMPLATES.MAINHERO),
                        getPuzzleExitConditions()),
                () -> exited(),
                Event.STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING
        );
    }

    private void exited() {
        //TODO isApplyPunishment()
        if (isPointExit())
            if (Eidolons.getMainHero().getCoordinates().equals(puzzle.getExitCoordinates())){
                return;
            }
        puzzle.failed();
    }

    protected void initEnterTrigger() {
        puzzle.createTriggerGlobal(PuzzleTrigger.PUZZLE_TRIGGER.ENTER,
                ConditionsUtils.join(new LambdaCondition(ref -> !puzzle.active  && (!puzzle.solved || isReplayable())),
                        ConditionsUtils.fromTemplate(ConditionMaster.CONDITION_TEMPLATES.MAINHERO),
                        getPuzzleEnterConditions()),
                () -> entered(), Event.STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING
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
        return new PositionCondition(
                puzzle.getExitCoordinates());
    }


    protected Condition getPuzzleEnterConditions() {
        if (isAreaEnter()) {
            return ConditionsUtils.or(new PositionCondition(
                            puzzle.getExitCoordinates()),
                    new AreaCondition(puzzle.getCoordinates(), puzzle.getWidth(), puzzle.getHeight()));
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