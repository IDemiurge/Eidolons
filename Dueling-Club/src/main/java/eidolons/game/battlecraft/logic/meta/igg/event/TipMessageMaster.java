package eidolons.game.battlecraft.logic.meta.igg.event;

import com.bitfire.utils.ItemsManager;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Demo;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Images;
import eidolons.game.battlecraft.logic.meta.tutorial.TutorialManager;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.texture.Images;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import eidolons.system.text.DescriptionTooltips;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster.TIP_MESSAGE.*;
import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE;
import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE_DEATH;

public class TipMessageMaster {
    private static List<Event.EVENT_TYPE> eventsMessaged=    new ArrayList<>() ;
    public static final TIP_MESSAGE[] tutorialTips = {
//            ALERT,


    };
    public static void welcome() {
        tip(WELCOME
        );
    }


    public static void death() {
        tip(DEATH);
        WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE_DEATH);
    }

    public static void tip(String[] args) {
        List<TIP_MESSAGE> list = new ArrayList<>();
        for (String arg : args) {
            TIP_MESSAGE tip = new EnumMaster<TIP_MESSAGE>().
                    retrieveEnumConst(TIP_MESSAGE.class, arg);
            if (tip == null) {
                continue;
            }
            list.add(tip);
        }
        tip(list.toArray(new TIP_MESSAGE[list.size()]));
    }

    public static void tip(  TIP_MESSAGE... tips) {
        tip(false, tips);
    }
    public static void tip(boolean manual, TIP_MESSAGE... tips) {
        if (tips[0].done) {
            return;
        }
        Runnable chain = createChain(tips);
        TipMessageSource source = getSource(tips[0]);
        source.setRunnable(chain);
        if (source.isOptional() && !manual  ) {
            if (OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.MESSAGES_OFF)) {
                chain.run();
                return;
            }
        }
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, source);
    }

    public static void tip(TipMessageSource source) {
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, source);
    }

    private static Runnable createChain(TIP_MESSAGE[] tips) {
        if (tips.length <= 1)
            return () -> {
                if (tips[0].once){
                    tips[0].done = true;
                }
            tips[0].run();
            };
        TIP_MESSAGE[] tipsChopped =
                Arrays.stream(tips).skip(1).collect(Collectors.toList()).toArray(new TIP_MESSAGE[tips.length - 1]);
        return () -> tip(tipsChopped);
    }

    private static TipMessageSource getSource(TIP_MESSAGE tip) {
        return new TipMessageSource(tip.message, tip.img, "Continue", tip.optional, null, tip.messageChannel);
    }

    public static void onEvent(Event.EVENT_TYPE type) {
        if (eventsMessaged.contains(type))
            return;
        checkEventMessaged(type);

        TIP_MESSAGE tip = getTip(type);
        tip(tip);
    }

    private static void checkEventMessaged(Event.EVENT_TYPE type) {
        eventsMessaged.add(type);
    }

    private static TIP_MESSAGE getTip(Event.EVENT_TYPE type) {
//         new EnumMaster<ENUM>().retrieveEnumConst(ENUM.class, string )
        if (type instanceof Event.STANDARD_EVENT_TYPE) {
            switch (((Event.STANDARD_EVENT_TYPE) type)) {
                case HERO_LEVEL_UP:
                    return HERO_LEVEL_UP;
            }
        }

        return null;
    }


// to .txt already?

    public enum TIP_MESSAGE {

        // PLOT
        Stone_Warden(false, IGG_Images.BRIEF_ART.STONE_WARDEN.getPath(), ""),

        warden2(false, IGG_Images.BRIEF_ART.STONE_WARDEN.getPath(), ""),
        warden3(false, IGG_Images.BRIEF_ART.STONE_WARDEN.getPath(), ""),
        crypt_maid(false, IGG_Images.BRIEF_ART.STONE_WARDEN.getPath(), ""),
        crypt_angel(false, IGG_Images.BRIEF_ART.STONE_WARDEN.getPath(), ""),

        rune_priest(true, ""),
        //self-chain constructor?i

        // TIPS
        Strategy(true, "", ""),

        //EVENTS

        First_shadow(false, "", ""),
        First_death(false, "", ""),
        HERO_LEVEL_UP(false, "", ""),

        //HERO
        TRAP_GORR(false, IGG_Images.HERO_ART.GORR_128.getPath(),
                ""){
            public void run() {
                TutorialManager.NEXT_HERO= IGG_Demo.HERO_GORR;
            }
        },
        TRAP_GWYNN(false, IGG_Images.HERO_ART.GWYN_128.getPath(),
                ""){
            public void run() {
                TutorialManager.NEXT_HERO= IGG_Demo.HERO_DARK_ELF;
            }
        },
        TRAP_GRIMBART(false, IGG_Images.HERO_ART.GRIMBART_128.getPath(),
                ""){
            public void run() {
                TutorialManager.NEXT_HERO= IGG_Demo.HERO_GRIMBART;
            }
        },
        //TUTORIAL
        TUTORIAL_START(false, IGG_Images.BRIEF_ART.APHOLON_SMALL.getPath(),
                "The Transformation has taken a heavy toll on me. Without Apholon and its thousand trapped souls, it is my own soul that must be bled to keep Eidolons from disintegrating. \n" +
                        "Fortunately, this place is filled with fragments of the Esoterica - albeit writ in crude runes of the dwarves. Still, each one will give me back a bit of my former Mastery."),




        TUTORIAL_PATH(false, "", "Good choice! Let's start with the basics then. Disable the Basic Tutorial no ignore these"){
            public void run() {
                Eidolons.TUTORIAL_PATH=true;
            }
        },
        TUTORIAL_PATH_DONE(false, "", "Well done! If you wish to revisit the lessons learned, you can do so via Menu->Info->Journal - " +
                "it will bring up your memory journal in form of a dialogue."){
            public void run() {
                Eidolons.TUTORIAL_PATH=false;
            }
        },



        Gateway_Glyphs(true,         Images.GATEWAY_GLYPH,""),

        //ui
        // combat
        IMMORTAL("Soulforce is effectively infinite, but the negative value you end up with will show you how much better you should do next time to win fairly. "),

        UNCONSCIOUS(

                // "My avatar is failing. But it is not over yet, there is life still in this shell!
                // All I need is time... !"),
                "This shell is waning. Time to put off the mask. The pain will be legendary... " +
                        "I will have to be quick, kill every foe before my avatar crumbles. "),
        SHADE_RESTORE("Their pain will be enough to restore my avatar. Let these ashes rise anew..."),
        DEATH_SHADE_FINAL(false, "This torment is all but in vain, and cruel is the fate. " +
                "Better to sleep now and harken to the song of the stars. Father, I am coming home..."),

        DEATH_SHADE_TIME(false,
                "Late now is the vengeance. " +
//                "Out of time. ")
                "This avatar is lost for now, but my grip is still strong. This quest will continue."),
        DEATH_SHADE(false, "I do not fear death. But have I overestimated myself? Still, there is an escape yet, another chance. " +
                "I better make it count."),

        NEXT_LEVEL(false, Images.VICTORY, ""),

        VICTORY(false, Images.VICTORY, ""),

        DEATH(false, Images.DEFEAT, "My avatar crumbles. The sweet cold of death engulfs me, tempts me. Yet I am not done yet. " +
                "From its ashes I can rise again...\n"),
        DEATH_FINAL(false, "And now comes the darkness. My hand is empty - is this the final hour?"),
        //TODO multiple random ones?!

//        COMBAT_WONT_END,
//        COMBAT_WONT_END,
//        COMBAT_WONT_END,

        //explore
//        VISIBILITY,


        //demo
        WELCOME("Greetings! Alexander here, " + //my own pic?!
                "very glad to have you on board! " +
                "I will be guiding your journey through the stormy seas of this Demo. I am sorry to inflict this form of tutorial on you, but with only a few weeks to make the demo, I had little choice! " +
                "If you feel you no longer need aid, be sure to disable these with the checkbox. " +
                "See the Manual via main menu, "),
        WELCOME_2("This Demo consists of " +
                "In this first location, you can choose a path that is relatively safe and has some optional tips to read and space" +
                " to practice your sword. "),
        WELCOME_3("Good luck!"),

        TUTORIAL_COMBAT(""),
        /*
        There are two values relevant to this for each unit - Noise and Perception.
Some actions may create more Noise

perception simply multiplies the remaining value, and the result is the hearing quality.
On the range of 0 t o100,
from “didn’t hear” to “heard where and what” with some things in between.
         */
/*
TODO DISPLAY TOOLTIP TITLE?
EXPLORATION


gwynn
keep to the shadows, rush forwards and strike from behind
use the debilitating sorcery to weaken your foes in open battle
 dual wielding attacks require Focus, but can be devastating indeed

 when things turn ill, you can attempt to escape into shadows again by using Stealth Mode in combination with Shadow Cloak.



gorr
let the enemy have the first swing, and return it with fury
when
use Fire Breath to 'breathe out' and reduce Rage, or just for good AoE damage
be careful not to let the Rage Counters reach 100 - or Gorr will lose control over himself!
another trick is to set yourself on fire and gain Rage each time the burning hurts you


raina
position
use holy magic to ensure Raina is at her finest when she meets the foe
consider using Defend


ranged
required reloading with ammo, which you carry in your Quick Slots alongside potions

spells


potions

quests

overlaying
some of the objects you see overlaid on the walls can be interacted with.
Pick up mushrooms to eat (or better not), examine the runes carved into the stone,

walls and secrets

if you examine closer
some walls will have "Marked" prefix -
those lead to secret passages, hidden treasures or into deadly traps.


 */
        //dev

        LIBRARY(false, "","" );
        public boolean optional = true;
        public boolean once ;
        public boolean done ;
        public String img;
        public String message;
        public WaitMaster.WAIT_OPERATIONS messageChannel = MESSAGE_RESPONSE;

        TIP_MESSAGE( String img) {
            this.img = img;
            this.once = true;
        }

        //to txt of course
        TIP_MESSAGE(boolean optional, String message) {
            this.optional = optional;
            this.message = message;


        }

        TIP_MESSAGE(boolean optional, String img, String message) {
            this.optional = optional;
            this.img = img;
            this.message = message;
        }

        public void run() {
        }
    }

    static {
        DEATH.messageChannel = MESSAGE_RESPONSE_DEATH;
        DEATH_SHADE.messageChannel = MESSAGE_RESPONSE_DEATH;
        DEATH_SHADE_TIME.messageChannel = MESSAGE_RESPONSE_DEATH;
    }
}