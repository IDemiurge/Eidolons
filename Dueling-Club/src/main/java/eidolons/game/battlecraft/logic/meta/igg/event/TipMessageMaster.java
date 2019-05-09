package eidolons.game.battlecraft.logic.meta.igg.event;

import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import java.util.Arrays;
import java.util.stream.Collectors;

import static eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster.TIP_MESSAGE.*;
import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE;
import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE_DEATH;

public class TipMessageMaster {
    public static void welcome() {
        tip(WELCOME
//                , WELCOME_2, WELCOME_3
        );
    }

    public static void death() {
        tip(DEATH);
        WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE_DEATH);
    }

    private static void tip(TIP_MESSAGE... tips) {
        Runnable chain = createChain(tips);
        TipMessageSource source = getSource(tips[0]);
        source.setRunnable(chain);
        if (source.isOptional()) {
            if (OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.MESSAGES_OFF)) {
                chain.run();
                return;
            }
        }
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, source);
    }

    private static Runnable createChain(TIP_MESSAGE[] tips) {
        if (tips.length <= 1)
            return () -> {
            };
        TIP_MESSAGE[] tipsChopped =
                Arrays.stream(tips).skip(1).collect(Collectors.toList()).toArray(new TIP_MESSAGE[tips.length - 1]);
        return () -> tip(tipsChopped);
    }

    private static TipMessageSource getSource(TIP_MESSAGE tip) {
        return new TipMessageSource(tip.message, tip.img, "Continue", tip.optional, null, tip.messageChannel);
    }

    public static void test() {
    }

    public static void testChained() {
        welcome();
    }

    public enum TIP_MESSAGE {

        //ui
        // combat
        UNCONSCIOUS(// "I am waning. Yet it is not over, there is life still in this shell of mine. All I need is time... Come, come to me, my Shadow!"),
                "This shell is waning. Time to put off the mask. The pain will be legendary... I will have to be quick, kill every foe before my consciousness fades. \n [2] rounds to finish the fight"),
        SHADE_RESTORE("Their pain will be enough to bring my pale avatar back. These ashes shall arise..."),
        DEATH_SHADE_FINAL(false, "This torment is all but in vain, and cruel is the fate. " +
                "Better to sleep now and harken to the song of the stars. Father, I am coming home..."),

        DEATH_SHADE_TIME(false, "I do not fear death. But have I overestimated myself? Still, there is an escape yet, another chance. " +
                "I better make it count."),
        DEATH_SHADE(false, "I do not fear death. But have I overestimated myself? Still, there is an escape yet, another chance. " +
                "I better make it count."),
        DEATH(false, "My avatar crumbles. The sweet cold of death engulfs me, tempts me. Yet I am not done yet. " +
                "So from its ashes I will rise...\n"),
        DEATH_FINAL(false, "And now it comes for real. My hand is empty - is this the final darkness?"),
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
                "If you feel you no longer need aid, be sure to "),
        WELCOME_2("This Demo consists of " +
                "In this first location, you are relatively safe, good time to practice the controls!"),
        WELCOME_3("Ha!"),

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

        ;
        public boolean optional = true;
        public String img;
        public String message;
        public WaitMaster.WAIT_OPERATIONS messageChannel = MESSAGE_RESPONSE;

        //to txt of course
        TIP_MESSAGE(boolean optional, String message) {
            this.optional = optional;
            this.message = message;
        }

        TIP_MESSAGE(String message) {
            this.message = message;
        }

    }

    static {
        DEATH.messageChannel = MESSAGE_RESPONSE_DEATH;
        DEATH_SHADE.messageChannel = MESSAGE_RESPONSE_DEATH;
        DEATH_SHADE_TIME.messageChannel = MESSAGE_RESPONSE_DEATH;
    }
}