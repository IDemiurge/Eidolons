package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Manipulator;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Veil;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.*;
import eidolons.game.core.Eidolons;
import eidolons.game.module.cinematic.flight.FlightData;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.generator.model.AbstractCoordinates;
import eidolons.system.text.DescriptionTooltips;
import main.elements.conditions.Condition;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.log;

public abstract class Puzzle {

    protected PuzzleEnvironment environment;
    protected PuzzleHandler handler;
    protected PuzzleQuest quest;
    protected PuzzleData data;
    protected PuzzleStats stats;

    protected List<PuzzleTrigger> triggers = new ArrayList<>();

    protected List<PuzzleSetup> setups;
    protected List<PuzzleRules> rules;
    protected List<PuzzleResolution> resolutions;


    protected boolean active;
    protected boolean solved;
    protected boolean failed;
    protected Coordinates coordinates;

    protected Veil veil;
    protected Veil exitVeil;
    protected LevelBlock block;
    protected String title;

    public Puzzle() {
    }

    protected void init() {
        stats = new PuzzleStats(this);
        title = getDefaultTitle();
        handler = createHandler();
    }

    protected abstract PuzzleHandler createHandler();

    public void setResolutions(PuzzleResolution... resolutions) {
        this.resolutions = new ArrayList<>(Arrays.asList(resolutions));
    }

    public PuzzleMaster getMaster() {
        return Eidolons.getGame().getDungeonMaster().getPuzzleMaster();
    }

    public void setRules(PuzzleRules... rules) {
        this.rules = new ArrayList<>(Arrays.asList(rules));
    }

    public void setup(PuzzleSetup... setups) {
        this.setups = new ArrayList<>(Arrays.asList(setups));
    }

    public int getWidth() {
        int width = data.getIntValue(PuzzleData.PUZZLE_VALUE.WIDTH);
        if (width==0) {
            return getDefaultWidth();
        }
        return width;
    }


    public int getHeight() {
        int height = data.getIntValue(PuzzleData.PUZZLE_VALUE.HEIGHT);
        if (height==0) {
            return getDefaultHeight();
        }
        return height;
    }
    //TODO

    protected int getDefaultHeight() {
        return 0;
    }
    protected int getDefaultWidth() {
        return 0;
    }
    public void createTrigger(PuzzleTrigger.PUZZLE_TRIGGER type, Event.EVENT_TYPE event, Condition checks, Runnable action) {
        log(1, ">> Puzzle createTrigger :" + event + " " + checks);
        triggers.add(new PuzzleTrigger(this, type, event, checks, action));
    }


    public void manipulatorActs(Manipulator manipulator) {
        for (PuzzleRules rule : rules) {
            rule.manipulatorActs(manipulator);
        }
    }

    public PuzzleQuest getQuest() {
        return quest;
    }

    public PuzzleData getData() {
        return data;
    }

    public PuzzleStats getStats() {
        return stats;
    }

    public List<PuzzleTrigger> getTriggers() {
        return triggers;
    }

    public List<PuzzleRules> getRules() {
        return rules;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isSolved() {
        return solved;
    }

    public boolean isFailed() {
        return failed;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isPale() {
        // return data.getBooleanValue(PuzzleData.PUZZLE_VALUE.PALE);
        return false;
    }

    public void setEnterVeil(Veil veil) {
        this.veil = veil;
    }

    public void setExitVeil(Veil exitVeil) {
        this.exitVeil = exitVeil;
    }

    public void setQuest(PuzzleQuest quest) {
        this.quest = quest;
    }

    public void setData(PuzzleData data) {
        this.data = data;
        init();
    }


    public void setStats(PuzzleStats stats) {
        this.stats = stats;
    }

    public String getOverrideBackground() {
        return null;
    }

    public FlightData getFlightData() {
        return null;
    }

    public LevelBlock getBlock() {
        return block;
    }

    public Coordinates getExitCoordinates() {
        String value = data.getValue(PuzzleData.PUZZLE_VALUE.EXIT);
        if (value.isEmpty()) {
            return null;
        }
        return getAbsoluteCoordinate(new Coordinates(value));
    }

    public void setBlock(LevelBlock block) {
        this.block = block;
    }

    public Coordinates getEntranceCoordinates() {
        return getAbsoluteCoordinate(new Coordinates(data.getValue(PuzzleData.PUZZLE_VALUE.ENTRANCE)));
    }

    protected int getWaitTimeBeforeEndMsg(boolean failed) {
        return 0;
    }

    protected String getFailCinematicScriptKey() {
        return null;
    }

    protected String getWinCinematicScriptKey() {
        return null;
    }


    protected String getFailText() {
        String key = data.getValue(PuzzleData.PUZZLE_VALUE.TIP_FAIL);
        if (StringMaster.isEmpty(key)) {
            key = getTitle() + "_failed";
        }
        return DescriptionTooltips.getTipMap().get(
                key.toLowerCase());
    }

    protected String getCompletionText() {
        return getTitle() + " has been completed! \n" +
                quest.getVictoryText() + "\n" +
                stats.getVictoryText();
    }


    public Coordinates getAbsoluteCoordinate(Coordinates c) {
        AbstractCoordinates coord = new AbstractCoordinates(true, c.x + getCoordinates().x, c.y + getCoordinates().y);
        if (coord.isInvalid()) {
            return c;
        }
        return coord;
    }

    public Coordinates getAbsoluteCoordinate(int i, int j) {
        return new AbstractCoordinates(true, i, j).getOffset(getCoordinates());
    }

    public float getDifficultyCoef() {
        return data.getFloatValue(PuzzleData.PUZZLE_VALUE.DIFFICULTY_COEF) / 100;
    }

    public void decrementCounter() {
        if (quest == null) {
            failed();
        } else if (!quest.decrementCounter())
            failed();

        //could fire an event of course
    }

    public int getCountersMax() {
        return data.getIntValue(PuzzleData.PUZZLE_VALUE.COUNTERS_MAX);
    }

    public String getTitle() {
        return title;
    }

    protected String getDefaultTitle() {
        return "Mystery";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestText() {
        return "Solve" + getTitle();
    }

    public int getSoulforceBase() {
        return 100;
    }

    public Coordinates getCenterCoordinates() {
        return Coordinates.get(getCoordinates().x + getWidth() / 2,
                getCoordinates().y + getHeight() / 2);
    }


    public Condition createSolutionCondition() {
        return null;
    }

    public boolean isMinimizeUI() {
        return false;
    }

    public void finished() {
        handler.afterEndTip();
    }

    public void complete() {
        handler.win();
    }

    public void failed() {
        handler.failed();
    }

    public void ended() {
        handler.ended();
    }

    public void activate() {
        handler.activate();
    }

    public PuzzleHandler getHandler() {
        return handler;
    }

    public int getTipDelay() {
        return 0;
    }

    public String getEnterCinematicScriptKey() {
        return null;
    }
}
