package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.ai.advanced.engagement.PlayerStatus;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleQuest;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleStats;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.netherflame.main.event.TipMessageMaster;
import eidolons.game.netherflame.main.event.TipMessageSource;
import eidolons.game.netherflame.main.pale.PaleAspect;
import eidolons.libgdx.anims.fullscreen.FullscreenAnimDataSource;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims;
import eidolons.system.audio.MusicMaster;
import main.content.enums.GenericEnums;
import main.content.enums.rules.VisionEnums;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.threading.WaitMaster;

import static main.system.GuiEventType.*;
import static main.system.auxiliary.log.LogMaster.log;

public abstract class PuzzleHandler<T extends Puzzle> extends PuzzleElement<T> {


    protected PuzzleSetup<T, ?> setup;

    public PuzzleHandler(T puzzle) {
        super(puzzle);
        setup = createSetup();
    }

    protected abstract PuzzleSetup<T, ?> createSetup();

    public void afterTipAction() {
        GuiEventManager.trigger(CAMERA_PAN_TO_COORDINATE, puzzle.getCenterCoordinates());
    }

    public void glimpse() {
        GuiEventManager.trigger(SHOW_FULLSCREEN_ANIM,
                new FullscreenAnimDataSource(FullscreenAnims.FULLSCREEN_ANIM.GATE_FLASH,
                        1, FACING_DIRECTION.NONE, GenericEnums.BLENDING.SCREEN));
    }

    protected void updateQuest() {
        GuiEventManager.trigger(QUEST_UPDATE, puzzle.quest);
    }

    protected abstract void playerActionDone(DC_ActiveObj action);

    public void activate() {
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

    public void finished() {
        log(LOG_CHANNEL.ANIM_DEBUG, "Puzzle finished: " + this);
        //        resolutions.forEach(r -> r.finished());
        //        rules.forEach(r -> r.finished());
        glimpse();
        puzzle.active = false;
        getMaster().deactivated(puzzle);
    }

    public void complete() {
        log(LOG_CHANNEL.ANIM_DEBUG, "Puzzle completed: " + this);

        cinematicWin();
        WaitMaster.WAIT(puzzle.getWaitTimeBeforeEndMsg(false));
        if (!isTestMode()) {
            puzzle.quest.complete();
            puzzle.stats.complete();
            puzzle.solved = true;
            String text = puzzle.getCompletionText();
            TipMessageSource src = new TipMessageSource(
                    text, "", "Onward!", false, () -> {
                Eidolons.onThisOrNonGdxThread(() -> finished());
            }
            );
            TipMessageMaster.tip(src);
        } else {
            failed();
        }
        ended();
    }

    protected boolean isTestMode() {
        return false;
    }

    public void failed() {
        if (puzzle.isFailed())
            return;
        log(LOG_CHANNEL.ANIM_DEBUG, "Puzzle failed: " + this);
        MusicMaster.playMoment(MusicMaster.MUSIC_MOMENT.FALL);

        EUtils.showInfoText(true, "Mystery fades: " + puzzle.getTitle());
        puzzle.stats.failed();
        puzzle.quest.failed();

        puzzle.failed = true;

        String text = puzzle.getFailText();

        cinematicFail();
        WaitMaster.WAIT(puzzle.getWaitTimeBeforeEndMsg(true));
        TipMessageSource src = new TipMessageSource(
                text, "", "Continue", false, () -> {
            Eidolons.onThisOrNonGdxThread(() -> finished());
        }
        );
        TipMessageMaster.tip(src);
        ended();
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
        Eidolons.onNonGdxThread(() -> {
           cinematicStart();
            //just zoom out? need to center cam on exits..
        beforeTip(); //TODO pause/disable things ! enter cinematic mode or something
        if (puzzle.getTipDelay() > 0) {
            WaitMaster.WAIT(puzzle.getTipDelay());
        }
        if (isFirstAttempt() && !puzzle.getData().getValue(PuzzleData.PUZZLE_VALUE.TIP).isEmpty()) {
            TipMessageMaster.tip(puzzle.getData().getValue(PuzzleData.PUZZLE_VALUE.TIP),
                    () -> afterTipAction());
        } else {
            afterTipAction();
        }
        puzzle.activate();
        });
    }

    protected boolean isFirstAttempt() {
        return !puzzle.isFailed() || PuzzleMaster.TEST_MODE;
    }

    protected void beforeTip() {

    }
}
