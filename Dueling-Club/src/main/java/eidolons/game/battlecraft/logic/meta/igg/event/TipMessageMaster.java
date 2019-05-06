package eidolons.game.battlecraft.logic.meta.igg.event;

import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import java.util.Arrays;
import java.util.stream.Collectors;

import static eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster.TIP_MESSAGE.*;

public class TipMessageMaster {
    public static void welcome() {
        tip(WELCOME
//                , WELCOME_2, WELCOME_3
        );
    }

    public static void death() {
        tip(DEATH);
        WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE);
    }
    private static void tip(TIP_MESSAGE... tips) {
        Runnable chain = createChain(tips);
          TipMessageSource  source = getSource(tips[0]);
        source.setRunnable(chain);
        if (source.isOptional()){
            if (OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.MESSAGES_OFF))
                return;
        }
        GuiEventManager.trigger(GuiEventType. TIP_MESSAGE, source);
    }

    private static Runnable createChain(TIP_MESSAGE[] tips) {
        if (tips.length<=1)
            return ()->{};
        TIP_MESSAGE[] tipsChopped =
                Arrays.stream(tips).skip(1).collect(Collectors.toList()).toArray(new TIP_MESSAGE[tips.length - 1]);
        return ()-> tip(tipsChopped);
    }

    private static TipMessageSource getSource(TIP_MESSAGE tip) {
     return    new TipMessageSource(tip.message,tip.img, "Continue", tip.optional ,null);
    }

    public static void test() {
        tip(TEST);
    }
    public static void testChained() {
        welcome();
    }

    public enum TIP_MESSAGE {

        //ui
TEST("This shell is waning. Time to put off the mask. The pain will be legendary..."),
        // combat
        DEATH("Dead!"),
        UNCONSCIOUS(
//                "I am waning. Yet it is not over, there is life still in this shell of mine. All I need is time... Come, come to me, my Shadow!"),
                "This shell is waning. Time to put off the mask. The pain will be legendary... I will have to be quick, kill every foe before my consciousness fades. \n [2] rounds to finish the fight"),
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
                "If you feel you no longer need aid, be sure to "  ),
        WELCOME_2("This Demo consists of " +
                "In this first location, you are relatively safe, good time to practice the controls!"),
        WELCOME_3("Ha!"),

        TUTORIAL_(""),

        //dev

;
        public boolean optional=true;
        public String img;
        public String message;
//to txt of course
        TIP_MESSAGE(String message) {
            this.message = message;
        }
    }
}
