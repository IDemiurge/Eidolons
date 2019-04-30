package eidolons.game.battlecraft.logic.meta.igg.event;

import eidolons.game.battlecraft.logic.meta.igg.IGG_Images;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.ArrayMaster;
import main.system.threading.WaitMaster;

import java.util.Arrays;
import java.util.stream.Collectors;

import static eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster.MESSAGE_TIPS.*;

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
    private static void tip(MESSAGE_TIPS... tips) {
        Runnable chain = createChain(tips);
          TipMessageSource  source = getSource(tips[0]);
        source.setRunnable(chain);
        if (source.isOptional()){
            if (OptionsMaster.getEngineOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.MESSAGES_OFF))
                return;
        }
        GuiEventManager.trigger(GuiEventType. TIP_MESSAGE, source);
    }

    private static Runnable createChain(MESSAGE_TIPS[] tips) {
        if (tips.length<=1)
            return ()->{};
        MESSAGE_TIPS[] tipsChopped =
                Arrays.stream(tips).skip(1).collect(Collectors.toList()).toArray(new MESSAGE_TIPS[tips.length - 1]);
        return ()-> tip(tipsChopped);
    }

    private static TipMessageSource getSource(MESSAGE_TIPS tip) {
     return    new TipMessageSource(tip.message,tip.img, "Continue", tip.optional ,null);
    }

    public static void test() {
        tip(TEST);
    }
    public static void testChained() {
        welcome();
    }

    enum MESSAGE_TIPS{

        //ui
TEST("This shell is waning. Time to put off the mask. The pain will be legendary..."),
        // combat
        DEATH("Dead!"),
        UNCONSCIOUS(
//                "I am waning. Yet it is not over, there is life still in this shell of mine. All I need is time... Come, come to me, my Shadow!"),
                "This shell is waning. Time to put off the mask. The pain will be legendary..."),
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
        String img;
        String message;
//to txt of course
        MESSAGE_TIPS(String message) {
            this.message = message;
        }
    }
}
