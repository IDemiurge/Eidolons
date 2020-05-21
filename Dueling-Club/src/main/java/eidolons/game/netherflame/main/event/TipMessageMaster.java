package eidolons.game.netherflame.main.event;

import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import eidolons.system.text.DescriptionTooltips;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.game.netherflame.main.event.TIP.*;
import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE_DEATH;

public class TipMessageMaster {
    private static List<Event.EVENT_TYPE> eventsMessaged=    new ArrayList<>() ;
    public static final TIP[] tutorialTips = {
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

    public static void tip(String s, Runnable after) {
         for(String substring: ContainerUtils.openContainer( s)){
             TIP tip  =new EnumMaster<TIP>().retrieveEnumConst(TIP.class, substring);
             if (tip == null) {
                 String text = DescriptionTooltips.getTipMap().get(s.toLowerCase());
                 if (text != null) {
                     tip(new TipMessageSource(text, "", "Continue", false, after));
                 }
                 return;
             }
             if (after != null) {
                 tip(new TipMessageSource(tip.message, tip.img, "Continue", false, after));
             } else
                tip(tip);
         }
    }

    public static void tip(String[] args) {
        List<TIP> list = new ArrayList<>();
        for (String arg : args) {
            TIP tip = new EnumMaster<TIP>().
                    retrieveEnumConst(TIP.class, arg);
            if (tip == null) {
                String text = DescriptionTooltips.getTipMap().get(arg.trim());
                if (StringMaster.isEmpty(text)) {
                    text = arg.trim(); //TODO better way?
                }
                tip(new TipMessageSource(text, "", "Continue", true, ()->{}));
                continue;
            }
            list.add(tip);
        }
        if (!list.isEmpty()) {
            tip(list.toArray(new TIP[0]));
        }
    }

    public static void tip(  TIP... tips) {
        tip(false, tips);
    }
    public static void tip(boolean manual, TIP... tips) {
        for (TIP tip : tips) {

        }
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
        if (StringMaster.isEmpty(source.getImage())
        && source.getBtnRun()[0]==null ) {
            GuiEventManager.trigger(GuiEventType.SHOW_TEXT_CENTERED, source.getMessage());
        } else {
            GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, source);
        }
    }

    public static void tip(TipMessageSource source) {
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, source);
    }

    private static Runnable createChain(TIP[] tips) {
        if (tips.length <= 1)
            return () -> {
                if (!CoreEngine.isIDE())
                if (tips[0].once){
                    tips[0].done = true;
                }
            tips[0].run();
            };
        TIP[] tipsChopped =
                Arrays.stream(tips).skip(1).collect(Collectors.toList()).toArray(new TIP[tips.length - 1]);
        return () -> tip(tipsChopped);
    }

    private static TipMessageSource getSource(TIP tip) {
        String message = tip.message;
        if (tip.message.isEmpty()) {
            message =DescriptionTooltips.getTipMap().get(tip.name().toLowerCase());
        }
        return new TipMessageSource( message, tip.img, "Continue", tip.optional, null, tip.messageChannel);
    }

    public static void onEvent(Event.EVENT_TYPE type) {
        if (eventsMessaged.contains(type))
            return;
        checkEventMessaged(type);

        TIP tip = getTip(type);
        tip(tip);
    }

    private static void checkEventMessaged(Event.EVENT_TYPE type) {
        eventsMessaged.add(type);
    }

    private static TIP getTip(Event.EVENT_TYPE type) {
//         new EnumMaster<ENUM>().retrieveEnumConst(ENUM.class, string )
        if (type instanceof Event.STANDARD_EVENT_TYPE) {
            switch (((Event.STANDARD_EVENT_TYPE) type)) {
                case HERO_LEVEL_UP:
                        return HERO_LEVEL_UP;
            }
        }

        return null;
    }

    public static void tip(String data) {
        tip(data, null);
    }


    static {
        DEATH.messageChannel = MESSAGE_RESPONSE_DEATH;
        DEATH_SHADE.messageChannel = MESSAGE_RESPONSE_DEATH;
        DEATH_SHADE_TIME.messageChannel = MESSAGE_RESPONSE_DEATH;
    }
}