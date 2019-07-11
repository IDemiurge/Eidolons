package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Manipulator;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.*;
import main.elements.conditions.Condition;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.auxiliary.data.ArrayMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Puzzle {

    PuzzleEnvironment environment;
    PuzzleQuest quest;
    PuzzleData data;
    PuzzleStats stats;

    List<PuzzleTrigger> triggers;

    List<PuzzleSetup> setups;
    List<PuzzleRules> rules;
    List<PuzzleResolution> resolutions;

    boolean active;
    boolean solved;
    boolean failed;
    private Coordinates coordinates;

    public Puzzle() {
    }

    public Puzzle(PuzzleEnvironment environment, PuzzleQuest quest, PuzzleData data,
                  List<PuzzleSetup> setups, List<PuzzleRules> rules, List<PuzzleResolution> resolutions) {
        this.environment = environment;
        this.quest = quest;
        this.data = data;
        this.setups = setups;
        this.rules = rules;
        this.resolutions = resolutions;
    }

    public void setResolutions(PuzzleResolution... resolutions) {
        this.resolutions = new ArrayList<>(Arrays.asList(resolutions));
    }

    public void setRules(PuzzleRules... rules) {
        this.rules = new ArrayList<>(Arrays.asList(rules));
    }

    public void setup(PuzzleSetup... setups) {
        for (PuzzleSetup setup : setups) {
            setup.init();

        }

    }

    public int getWidth() {
        return 7;
    }
    public int getHeight() {
        return 5;
    }
    //TODO

    protected void createTrigger(Event.EVENT_TYPE event, Condition checks, Runnable action) {
        triggers.add(new PuzzleTrigger(this, event, checks, action));
    }

    public void manipulatorActs(Manipulator manipulator) {
        for (PuzzleRules rule : rules) {
            rule.manipulatorActs(manipulator);
        }
    }

    public PuzzleEnvironment getEnvironment() {
        return environment;
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

    public List<PuzzleSetup> getSetups() {
        return setups;
    }

    public List<PuzzleRules> getRules() {
        return rules;
    }

    public List<PuzzleResolution> getResolutions() {
        return resolutions;
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
}
