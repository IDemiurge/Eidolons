package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.intro.IntroFactory;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.DialogueContainerAdapter;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechExecutor;

import eidolons.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicMaster;
import main.content.CONTENT_CONSTS;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.sound.AudioEnums;
import main.system.threading.WaitMaster;

import java.util.List;

import static main.system.GuiEventType.DIALOG_SHOW;
import static main.system.GuiEventType.INIT_DIALOG;

/**
 * Created by JustMe on 5/14/2017.
 */
public class DialogueManager extends MetaGameHandler<ScenarioMeta> {
    public static   boolean TEST = false;
    private static final String DUEL = "Duel";
    private static final String INTRO = "Intro";
    private static final boolean SKIP_INTRO = CoreEngine.TEST_LAUNCH;
    private static boolean running;
    private static Runnable afterDialogue;

    protected   SpeechExecutor speechExecutor;
    protected DialogueFactory dialogueFactory;
    protected IntroFactory introFactory;
    protected DialogueActorMaster dialogueActorMaster;
    private DialogueContainerAdapter container;

    public DialogueManager(MetaGameMaster master) {
        super(master);
//        SKIP_INTRO = OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.TESTER_VERSION);
        boolean PARSE_ON_INIT = Flags.isDialogueTest() || Flags.isFullVersion();

        if (PARSE_ON_INIT)
            DialogueLineFormatter.fullUpdate();

        dialogueFactory = createDialogueFactory();
        introFactory = createIntroFactory();
        dialogueActorMaster = new DialogueActorMaster(master);
        speechExecutor = new SpeechExecutor(master, this);

        GuiEventManager.bind(INIT_DIALOG, obj -> {
            Object key = obj.get();
            startDialogue(key.toString());
        });
    }


    public void startDialogue (String key) {
        GameDialogue dialogue =  getDialogueFactory().getDialogue(
                key);
        List<Scene> list = SceneFactory.getScenesLinear(dialogue);

        switch (key.toLowerCase()) {
            case "awakening":
                MusicMaster.playMoment(MusicEnums.MUSIC_MOMENT.SELENE);
                break;
            case "sentries":
                DC_SoundMaster.playEffectSound(AudioEnums.SOUNDS.ALERT, CONTENT_CONSTS.SOUNDSET.ironman);
                break;
            case "meet_harvester":
                MusicMaster.playMoment(MusicEnums.MUSIC_MOMENT.HARVEST);
                break;
        }

        GuiEventManager.trigger(DIALOG_SHOW,
                new DialogueHandler(this, dialogue, getGame(), list.subList(0,1)));
    }

    public void introDialogue() {
        String dialogue =null ;// EidolonsGame.DUEL_TEST ? DUEL : INTRO;
        //TODO core Review
        if (dialogue == null) {
            return;
        }
        GuiEventManager.trigger(GuiEventType.BLACKOUT_OUT, 1);
        GuiEventManager.trigger(GuiEventType.BLACKOUT_IN, 1);
        GuiEventManager.trigger(GuiEventType.BLACKOUT_AND_BACK, 8);
        WaitMaster.doAfterWait(5000, () -> GuiEventManager.trigger(INIT_DIALOG,
                dialogue));
    }
    public void startDialogue(GameDialogue dialogue) {
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
        GameDialogue dialogue;//new LinearDialogue();
        dialogue =  getMaster().getDialogueFactory().getDialogue("Bearhug");
        List<Scene> list = SceneFactory.getScenesLinear(dialogue);
        GuiEventManager.trigger(DIALOG_SHOW,
         new DialogueHandler(this, dialogue, getGame(), list));
    }
    public static boolean isBlotch() {
        return true;
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

    public void setContainer(DialogueContainerAdapter container) {
        this.container = container;
    }

    public DialogueContainerAdapter getContainer() {
        return container;
    }
}
