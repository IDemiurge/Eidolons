package eidolons.system.text.tips;

import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import eidolons.system.text.DescriptionTooltips;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.system.text.tips.TIP.*;
import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE_DEATH;

public class TipMessageMaster {
    private static final List<Event.EVENT_TYPE> eventsMessaged = new ArrayList<>();
    public static final TextEvent[] tutorialTips = {
            //            ALERT,


    };

    public static void welcome() {
        //        TODO
        //        tip(welcome_1, welcome_2);
    }


    public static void death() {
        tip(DEATH);
        WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE_DEATH);
    }

    public static void tip(String str, Runnable runnable) {
        TextEvent tipConst = Tips.getTipConst(str);
        if (tipConst != null) {
            tip(tipConst, runnable);
        } else {
            tip(new CustomTip(str, runnable));
        }
    }

    public static void tip(TextEvent tip, Runnable runnable) {
        tip(new TipMessageSource(tip.getMessage(), tip.getImg(), tip.getConfirmText(), false, runnable));
    }

    public static void tip(String s) {
        tip(ContainerUtils.openContainer(s).stream().map(str -> Tips.getTipConst(str)).collect(Collectors.toList())
                .toArray(new TextEvent[0]));
    }
    // public static void tip(String data) {
    //     tip(data, null);
    // }

    public static void tip(String... args) {
        List<TextEvent> list = new ArrayList<>();
        for (String arg : args) {
            TextEvent tip = new EnumMaster<TextEvent>().
                    retrieveEnumConst(TIP.class, arg);
            if (tip == null) {
                String text = DescriptionTooltips.getTipMap().get(arg.trim());
                if (StringMaster.isEmpty(text)) {
                    text = arg.trim(); //TODO better way?
                }
                tip = new CustomTip(text );
            }
            list.add(tip);
        }
        if (!list.isEmpty()) {
            tip(list.toArray(new TextEvent[0]));
        }
    }


    public static void tip(TextEvent... tips) {
        tip(false, tips);
    }

    public static void tip(boolean manual, TextEvent... tips) {
        if (tips[0].isDone()) {
            return;
        }
        Runnable chain = createChain(tips);
        TipMessageSource source = getSource(tips[0]);
        source.setRunnable(chain);
        if (source.isOptional() && !manual) {
            if (OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.MESSAGES_OFF)) {
                chain.run();
                return;
            }
        }
        if (StringMaster.isEmpty(source.getImage())
                && source.getBtnRun()[0] == null) {
            GuiEventManager.trigger(GuiEventType.SHOW_TEXT_CENTERED, source.getMessage());
        } else {
            GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, source);
        }
        main.system.auxiliary.log.LogMaster.log(1, "Showing TIP: " + tips[0].toString());
    }

    public static void tip(TipMessageSource source) {
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, source);
    }

    private static Runnable createChain(TextEvent[] tips) {
        if (tips.length <= 1)
            return () -> {
                if (!Flags.isIDE())
                    if (tips[0].isOnce()) {
                        tips[0].setDone(true);
                    }
                tips[0].run();
            };
        TextEvent[] tipsChopped =
                Arrays.stream(tips).skip(1).collect(Collectors.toList()).toArray(new TextEvent[tips.length - 1]);
        return () -> tip(tipsChopped);
    }

    private static TipMessageSource getSource(TextEvent tip) {
        String message = tip.getMessage();
        if (tip.getMessage().isEmpty()) {
            message = DescriptionTooltips.getTipMap().get(tip.toString().toLowerCase());
        }
        return new TipMessageSource(message, tip.getImg(), tip.getConfirmText(), tip.isOptional(), null, tip.getMessageChannel());
    }

    public static void onEvent(Event.EVENT_TYPE type) {
        if (eventsMessaged.contains(type))
            return;
        checkEventMessaged(type);

        TextEvent tip = getTip(type);
        tip(tip);
    }

    private static void checkEventMessaged(Event.EVENT_TYPE type) {
        eventsMessaged.add(type);
    }

    private static TextEvent getTip(Event.EVENT_TYPE type) {
        //         new EnumMaster<ENUM>().retrieveEnumConst(ENUM.class, string )
        if (type instanceof Event.STANDARD_EVENT_TYPE) {
            switch (((Event.STANDARD_EVENT_TYPE) type)) {
                case HERO_LEVEL_UP:
                    return HERO_LEVEL_UP;
            }
        }

        return null;
    }


    static {
        DEATH.messageChannel = MESSAGE_RESPONSE_DEATH;
        DEATH_SHADE.messageChannel = MESSAGE_RESPONSE_DEATH;
        DEATH_SHADE_TIME.messageChannel = MESSAGE_RESPONSE_DEATH;
    }
}