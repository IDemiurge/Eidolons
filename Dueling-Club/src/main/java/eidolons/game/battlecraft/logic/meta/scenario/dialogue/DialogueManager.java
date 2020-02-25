package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.intro.IntroFactory;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechExecutor;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueContainer;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.Scene;
import eidolons.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import main.content.CONTENT_CONSTS;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
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
    private static   boolean SKIP_INTRO = false;
    private static   boolean PARSE_ON_INIT = CoreEngine.isIDE();
    private static boolean running;
    private static Runnable afterDialogue;

    protected   SpeechExecutor speechExecutor;
    protected DialogueFactory dialogueFactory;
    protected IntroFactory introFactory;
    protected DialogueActorMaster dialogueActorMaster;
    private DialogueContainer container;

    public DialogueManager(MetaGameMaster master) {
        super(master);
//        SKIP_INTRO = OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.TESTER_VERSION);
        PARSE_ON_INIT = !OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.TESTER_VERSION);

        if (PARSE_ON_INIT)
            DialogueLineFormatter.fullUpdate();

        dialogueFactory = createDialogueFactory();
        introFactory = createIntroFactory();
        dialogueActorMaster = new DialogueActorMaster(master);
        speechExecutor = new SpeechExecutor(master, this);


        GuiEventManager.bind(INIT_DIALOG, obj -> {
            if (CoreEngine.isIDE())
                if (!EidolonsGame.BRIDGE)
                    if (CoreEngine.isLiteLaunch()) {
                        if (!CoreEngine.isDialogueTest()) {
                            return;
                        }
                    }
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
                new DialogueHandler(this, dialogue, getGame(), list.subList(0,1)));
    }

    public void introDialogue() {
        if ( EidolonsGame.PUZZLES|| SKIP_INTRO || !EidolonsGame.BRIDGE){
            return ;
        }

//        GuiEventManager.trigger(GuiEventType.BLACKOUT_OUT, 8);

        String dialogue = EidolonsGame.DUEL_TEST ? DUEL : INTRO;
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
        GameDialogue dialogue = null;//new LinearDialogue();
        dialogue =  getMaster().getDialogueFactory().getDialogue("Bearhug");
        List<Scene> list = SceneFactory.getScenesLinear(dialogue);
        GuiEventManager.trigger(DIALOG_SHOW,
         new DialogueHandler(this, dialogue, getGame(), list));
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

    public void setContainer(DialogueContainer container) {
        this.container = container;
    }

    public DialogueContainer getContainer() {
        return container;
    }
}
