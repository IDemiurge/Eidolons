package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.ai.advanced.engagement.PlayerStatus;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleQuest;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleStats;
import eidolons.game.core.EUtils;
import eidolons.game.netherflame.main.pale.PaleAspect;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicMaster;
import eidolons.system.text.tips.TextEvent;
import eidolons.system.text.tips.TipMessageMaster;
import eidolons.system.text.tips.Tips;
import main.content.enums.rules.VisionEnums;
import main.elements.triggers.Trigger;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.threading.WaitMaster;

import java.util.LinkedList;
import java.util.List;

import static main.system.GuiEventType.*;
import static main.system.auxiliary.log.LogMaster.log;

public abstract class PuzzleHandler<T extends Puzzle> extends PuzzleElement<T> {


    protected PuzzleSetup<T, ?> setup;
    protected List<Trigger> customTriggers = new LinkedList<>();


    public PuzzleHandler(T puzzle) {
        super(puzzle);
        setup = createSetup();
    }

    protected abstract PuzzleSetup<T, ?> createSetup();

    public void afterTipAction() {
        GuiEventManager.trigger(CAMERA_PAN_TO_COORDINATE, puzzle.getCenterCoordinates());
    }

    public void glimpse() {
    }

    protected void updateQuest() {
        GuiEventManager.trigger(QUEST_UPDATE, puzzle.quest);
    }

    protected void playerActionDone(DC_ActiveObj action) {

    }

    public void started() {
        puzzle.active = true;
        puzzle.failed = false;
        puzzle.triggers.clear();
        if (puzzle.setups != null)
            for (PuzzleSetup setup : puzzle.setups) {
                setup.started();
            }
        if (setup != null)
            setup.started();

        puzzle.resolutions.forEach(r -> r.started());
        puzzle.rules.forEach(r -> r.started());
        getMaster().activated(puzzle);
        GuiEventManager.trigger(PLAYER_STATUS_CHANGED,
                new PlayerStatus(VisionEnums.PLAYER_STATUS.PUZZLE,
                        puzzle.stats.getIntValue(PuzzleStats.PUZZLE_STAT.TIMES_FAILED)));
        puzzle.quest = initQuest(puzzle.data);
        puzzle.stats.started();
        log(LOG_CHANNEL.ANIM_DEBUG, "Puzzle Activated: " + this);
        glimpse();

        if (puzzle.isMinimizeUI()) {
            GuiEventManager.trigger(MINIMIZE_UI_ON);
        }
        if (puzzle.getOverrideBackground() != null) {
            GuiEventManager.trigger(UPDATE_DUNGEON_BACKGROUND, puzzle.getOverrideBackground());
        }
        if (puzzle.getFlightData() != null) {
            GuiEventManager.trigger(FLIGHT_START, puzzle.getFlightData());
        }

    }

    public void afterEndTip() {
        log(LOG_CHANNEL.ANIM_DEBUG, "Puzzle finished: " + this);
        //        resolutions.forEach(r -> r.finished());
        //        rules.forEach(r -> r.finished());
        glimpse();
        puzzle.active = false;
        getMaster().deactivated(puzzle);
    }

    public void win() {
        log(LOG_CHANNEL.ANIM_DEBUG, "Puzzle completed: " + this);

        cinematicWin();
        WaitMaster.WAIT(puzzle.getWaitTimeBeforeEndMsg(false));
        if (!isTestMode()) {
            puzzle.quest.complete();
            puzzle.stats.complete();
            puzzle.solved = true;

            TextEvent tip = getWinTip();
            if (tip == null) {
                TipMessageMaster.tip(puzzle.getWinText(), () -> afterEndTip());
            } else
                TipMessageMaster.tip(tip, () -> afterEndTip());

        } else {
            failed();
        }
        ended();
    }

    protected boolean isTestMode() {
        return false;
    }

    public void failed() {
        // if (puzzle.isFailed())
        //     return;
        log(LOG_CHANNEL.ANIM_DEBUG, "Puzzle failed: " + this);
        MusicMaster.playMoment(MusicEnums.MUSIC_MOMENT.FALL);

        EUtils.showInfoText(true, "Mystery fades: " + puzzle.getTitle());
        try {
            puzzle.stats.failed();
            puzzle.quest.failed();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        puzzle.failed = true;

        cinematicFail();

        WaitMaster.WAIT(puzzle.getWaitTimeBeforeEndMsg(true));

        //TODO
        // String text = puzzle.getFailText();
        // TipMessageSource src = new TipMessageSource(
        //         text, "", "Continue", false, () -> {
        //     Eidolons.onThisOrNonGdxThread(() -> afterEndTip());
        // }
        // );

        TextEvent failTip = getFailTip();
        if (failTip == null) {
            TipMessageMaster.tip(puzzle.getFailText(), () -> afterEndTip());
        } else
            TipMessageMaster.tip(failTip, () -> afterEndTip());
        ended();
    }

    protected TextEvent getFailTip() {
        String value = getData().getValue(PuzzleData.PUZZLE_VALUE.TIP_FAIL);
        TextEvent tip = Tips.getTipConst(value);
        if (tip != null) {
            return tip;
        }
        return Tips.getPuzzleTipConst(false, puzzle.getType(), getTipSuffix());
    }

    protected TextEvent getWinTip() {
        String value = getData().getValue(PuzzleData.PUZZLE_VALUE.TIP_WIN);
        TextEvent tip = Tips.getTipConst(value);
        if (tip != null) {
            return tip;
        }
        return Tips.getPuzzleTipConst(true, puzzle.getType(), getTipSuffix());
    }

    protected TextEvent getIntroTip() {
        String value = getData().getValue(PuzzleData.PUZZLE_VALUE.TIP_INTRO);
        TextEvent tip = Tips.getTipConst(value);
        if (tip != null) {
            return tip;
        }
        return Tips.getPuzzleTipConst(null, puzzle.getType(), getTipSuffix());
    }

    protected String getTipSuffix() {
        return "";
    }


    public void handleEvent(Event event) {

    }

    public void customAction() {

    }

    public void ended() {
        if (puzzle.isMinimizeUI())
            GuiEventManager.trigger(MINIMIZE_UI_OFF);

        if (puzzle.getOverrideBackground() != null) {
            GuiEventManager.trigger(RESET_DUNGEON_BACKGROUND);
        }
        if (puzzle.getFlightData() != null) {
            GuiEventManager.trigger(FLIGHT_END);
        }
        customTriggers.clear();
        setup.ended();
    }


    protected PuzzleQuest initQuest(PuzzleData data) {
        return new PuzzleQuest(puzzle, data);
    }

    protected void cinematicFail() {
    }

    protected void cinematicWin() {
    }

    protected void cinematicStart() {
    }

    protected void entered() {
        if (puzzle.isPale()) {
            PaleAspect.enterPale();
        }
        // Eidolons.onNonGdxThread(() -> {
        cinematicStart();
        //just zoom out? need to center cam on exits..
        beforeTip(); //TODO pause/disable things ! enter cinematic mode or something
        if (puzzle.getTipDelay() > 0) {
            WaitMaster.WAIT(puzzle.getTipDelay());
        }
        // isFirstAttempt() && !puzzle.getData().getValue(PuzzleData.PUZZLE_VALUE.TIP_INTRO).isEmpty()
        if (true) {
            TextEvent tip = getIntroTip();
            if (tip == null) {
                TipMessageMaster.tip(puzzle.getIntroText(), () -> afterTipAction());
            } else
                TipMessageMaster.tip(tip, () -> afterTipAction());

        } else {
            afterTipAction();
        }
        puzzle.activate();
        // });
    }

    protected boolean isFirstAttempt() {
        return !puzzle.isFailed() || PuzzleMaster.TEST_MODE;
    }

    protected void beforeTip() {

    }

    public List<Trigger> getCustomTriggers() {
        return customTriggers;
    }
}
