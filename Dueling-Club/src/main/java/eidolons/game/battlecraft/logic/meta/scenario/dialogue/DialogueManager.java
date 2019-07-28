package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.content.PROPS;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.intro.IntroFactory;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechExecutor;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.Scene;
import eidolons.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import main.content.CONTENT_CONSTS;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;

import static main.system.GuiEventType.DIALOG_SHOW;
import static main.system.GuiEventType.INIT_DIALOG;

/**
 * Created by JustMe on 5/14/2017.
 */
public class DialogueManager extends MetaGameHandler<ScenarioMeta> {
    private static boolean running;
    private static Runnable afterDialogue;

    protected   SpeechExecutor speechExecutor;
    protected DialogueFactory dialogueFactory;
    protected IntroFactory introFactory;
    protected DialogueActorMaster dialogueActorMaster;

    public DialogueManager(MetaGameMaster master) {
        super(master);
        dialogueFactory = createDialogueFactory();
        introFactory = createIntroFactory();
        dialogueActorMaster = new DialogueActorMaster(master);
        speechExecutor = new SpeechExecutor(master);

        GuiEventManager.bind(INIT_DIALOG, obj -> {
            if (CoreEngine.isIDE())
            if (!EidolonsGame.BRIDGE)
            if (CoreEngine.isLiteLaunch())
            {
                if (!CoreEngine.isDialogueTest()){
                    return;
                }
            }
            Object key = obj.get();
            GameDialogue dialogue =  getDialogueFactory().getDialogue(
                    key.toString());
            List<Scene> list = SceneFactory.getScenesLinear(dialogue);

            switch (key.toString().toLowerCase()) {
                case "awakening":
                    MusicMaster.playMoment(MusicMaster.MUSIC_MOMENT.SELENE);
                    break;
                case "sentries":
                    DC_SoundMaster.playEffectSound(SoundMaster.SOUNDS.ALERT, CONTENT_CONSTS.SOUNDSET.ironman);
                    break;
                case "meet_harvester":
                    MusicMaster.playMoment(MusicMaster.MUSIC_MOMENT.HARVEST);
                    break;
            }

            GuiEventManager.trigger(DIALOG_SHOW,
                    new DialogueHandler(dialogue, getGame(), list.subList(0,1)));
        });
    }

    public static void afterDialogue(Runnable o) {
        afterDialogue = o;
    }

    public static void dialogueDone( ) {
        if (afterDialogue != null) {
            afterDialogue.run();
            afterDialogue = null;
        }
    }

    public boolean isPreloadDialogues() {
        return true;
    }

    public DialogueActorMaster getDialogueActorMaster() {
        return dialogueActorMaster;
    }

    @Override
    public IntroFactory getIntroFactory() {
        return introFactory;
    }

    @Override
    public DialogueFactory getDialogueFactory() {
        return dialogueFactory;
    }

    public SpeechExecutor getSpeechExecutor() {
        return speechExecutor;
    }

    public  static void createTutorialJournal() {
        DialogueLineFormatter.createTutorialJournal();
    }
        public  static void tutorialJournal() {

        GuiEventManager.trigger(INIT_DIALOG, "Tutorial Journal");


    }
        public   void test() {
        GameDialogue dialogue = null;//new LinearDialogue();
        dialogue =  getMaster().getDialogueFactory().getDialogue("Bearhug");
        List<Scene> list = SceneFactory.getScenesLinear(dialogue);
        GuiEventManager.trigger(DIALOG_SHOW,
         new DialogueHandler(dialogue, getGame(), list));
    }
    public void startScenarioIntroDialogues() {
        String data = getMetaGame().getScenario().getProperty(PROPS.
         SCENARIO_INTRO_DIALOGUES);
        startDialogues(data);
    }

    public void startMissionIntroDialogues() {
//        Mission mission = getMetaGame().getScenario().getMissionIndex();
//        String data = mission.getProperty(PROPS.MISSION_INTRO_DIALOGUES);
//        startDialogues(data);

    }

    public void startDialogues(String data) {
        for (String substring : ContainerUtils.open(data)) {
//            GameDialogue dialogue=  getDialogueFactory().getDialogue(substring, (ScenarioMetaMaster) master);
//            startDialogue(dialogue);
        }
    }

    public void startDialogue(GameDialogue dialogue) {
        //if in game

        getGame().getGameLoop().setPaused(true);
        try {
            new DialogueWizard(dialogue).start();
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        } finally {
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_LOOP_PAUSE_DONE, true);
        }
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        DialogueManager.running = running;
    }

    protected IntroFactory createIntroFactory() {
        return new IntroFactory();
    }

    protected DialogueFactory createDialogueFactory() {
        return new DialogueFactory();
    }
}
