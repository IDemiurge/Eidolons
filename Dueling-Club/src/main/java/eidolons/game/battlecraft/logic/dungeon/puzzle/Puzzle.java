package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Manipulator;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Veil;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.*;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.generator.model.AbstractCoordinates;
import eidolons.game.netherflame.igg.event.TipMessageMaster;
import eidolons.game.netherflame.igg.event.TipMessageSource;
import eidolons.game.netherflame.igg.pale.PaleAspect;
import eidolons.libgdx.anims.fullscreen.FullscreenAnimDataSource;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims;
import eidolons.system.audio.MusicMaster;
import eidolons.system.text.DescriptionTooltips;
import main.content.enums.GenericEnums;
import main.elements.conditions.Condition;
import main.elements.triggers.Trigger;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LOG_CHANNEL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.log;

public class Puzzle {

    PuzzleEnvironment environment;
    PuzzleQuest quest;
    PuzzleData data;
    PuzzleStats stats;

    List<PuzzleTrigger> triggers = new ArrayList<>();

    List<PuzzleSetup> setups;
    List<PuzzleRules> rules;
    List<PuzzleResolution> resolutions;

    boolean active;
    boolean solved;
    boolean failed;
    private Coordinates coordinates;

    private Veil veil;
    private Veil exitVeil;
    private LevelBlock block;
    private String title;

    public Puzzle() {
        stats = new PuzzleStats(this);
        title = getDefaultTitle();
    }


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
        return data.getIntValue(PuzzleData.PUZZLE_VALUE.WIDTH);
    }

    public int getHeight() {
        return data.getIntValue(PuzzleData.PUZZLE_VALUE.HEIGHT);
    }
    //TODO

    public void createTrigger(PuzzleTrigger.PUZZLE_TRIGGER type, Event.EVENT_TYPE event, Condition checks, Runnable action) {
        log(1, ">> Puzzle createTrigger :" + event + " " + checks);
        triggers.add(new PuzzleTrigger(this, type, event, checks, action));
    }

    public void createTriggerGlobal(PuzzleTrigger.PUZZLE_TRIGGER type, Condition checks, Runnable action, Event.EVENT_TYPE event) {
        Trigger trigger = new PuzzleTrigger(this, type, event, checks, action);
        Eidolons.getGame().getManager().addTrigger(trigger);
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

    public boolean isPale() {
        return data.getBooleanValue(PuzzleData.PUZZLE_VALUE.PALE);
    }

    public void setEnterVeil(Veil veil) {
        this.veil = veil;
    }

    public Veil getVeil() {
        return veil;
    }

    public void setExitVeil(Veil exitVeil) {
        this.exitVeil = exitVeil;
    }

    public Veil getExitVeil() {
        return exitVeil;
    }

    public void setEnvironment(PuzzleEnvironment environment) {
        this.environment = environment;
    }

    public void setQuest(PuzzleQuest quest) {
        this.quest = quest;
    }

    public void setData(PuzzleData data) {
        this.data = data;
    }

    public void setStats(PuzzleStats stats) {
        this.stats = stats;
    }

    public void activate() {
        active = true;
        failed = false;
        triggers.clear();
        for (PuzzleSetup setup : setups) {
            setup.started();
        }
        resolutions.forEach(r -> r.started());
        rules.forEach(r -> r.started());
        getMaster().activated(this);
        quest = initQuest(data);
        stats.started();
        log(LOG_CHANNEL.ANIM_DEBUG, "Puzzle Activated: " + this);
        glimpse();
    }

    protected PuzzleQuest initQuest(PuzzleData data) {
        return new PuzzleQuest(this, data);
    }

    public LevelBlock getBlock() {
        return block;
    }

    public Coordinates getExitCoordinates() {
        return new Coordinates(data.getValue(PuzzleData.PUZZLE_VALUE.EXIT)).negativeY().getOffset(getCoordinates());
    }

    public void setBlock(LevelBlock block) {
        this.block = block;
    }

    public Coordinates getEntranceCoordinates() {
        return new Coordinates(data.getValue(PuzzleData.PUZZLE_VALUE.ENTRANCE)).negativeY().getOffset(getCoordinates());
    }

    public void failed() {
        if (isFailed())
            return;
        log(LOG_CHANNEL.ANIM_DEBUG, "Puzzle failed: " + this);
        MusicMaster.playMoment(MusicMaster.MUSIC_MOMENT.FALL);

        EUtils.showInfoText(true, "Mystery fades: " + getTitle());
        stats.failed();
        quest.failed();

        failed = true;

        String text = getFailText();
        TipMessageSource src = new TipMessageSource(
                text, "", "Continue", false, () -> {
            Eidolons.onThisOrNonGdxThread(() -> finished());
        }
        );
        TipMessageMaster.tip(src);
    }

    public void exited() {

    }

    public void complete() {
        log(LOG_CHANNEL.ANIM_DEBUG, "Puzzle completed: " + this);
        quest.complete();
        stats.complete();
        //TODO  stats.complete();
        GuiEventManager.trigger(GuiEventType.PUZZLE_COMPLETED, this);
        solved = true;
        String text = getCompletionText();
        TipMessageSource src = new TipMessageSource(
                text, "", "Onward!", false, () -> {
            Eidolons.onThisOrNonGdxThread(() -> finished());
        }
        );
        TipMessageMaster.tip(src);
    }

    private String getFailText() {
        String key = data.getValue(PuzzleData.PUZZLE_VALUE.TIP_FAIL);
        if (StringMaster.isEmpty(key)) {
            key = getTitle() + "_failed";
        }
        return DescriptionTooltips.getTipMap().get(
                key.toLowerCase());
    }
    private String getCompletionText() {
        return getTitle() + " has been completed! \n" +
                quest.getVictoryText() + "\n" +
                stats.getVictoryText();
    }

    public void finished() {
        log(LOG_CHANNEL.ANIM_DEBUG, "Puzzle finished: " + this);
//        resolutions.forEach(r -> r.finished());
//        rules.forEach(r -> r.finished());
        glimpse();

        if (isPale()) {
            PaleAspect.exitPale();
        }
        active = false;
        getMaster().deactivated(this);
    }

    public void glimpse() {
        GuiEventManager.trigger(GuiEventType.SHOW_FULLSCREEN_ANIM,
                new FullscreenAnimDataSource(FullscreenAnims.FULLSCREEN_ANIM.GATE_FLASH,
                        1, FACING_DIRECTION.NONE, GenericEnums.BLENDING.SCREEN));
    }

    public Coordinates getAbsoluteCoordinate(Coordinates wall) {
        return wall.negativeY().getOffset(getCoordinates());
    }

    public Coordinates getAbsoluteCoordinate(int i, int j) {
        return new AbstractCoordinates(true, i, j).negativeY().getOffset(getCoordinates());
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
        return Coordinates.get(getCoordinates().x + getWidth() / 2, getCoordinates().y - getHeight() / 2);
    }

    public Condition createSolutionCondition() {
        return null;
    }
}
