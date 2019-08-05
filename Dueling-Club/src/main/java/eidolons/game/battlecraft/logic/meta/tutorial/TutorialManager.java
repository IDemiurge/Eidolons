package eidolons.game.battlecraft.logic.meta.tutorial;

import eidolons.game.battlecraft.logic.meta.igg.IGG_Demo;
import eidolons.game.battlecraft.logic.meta.igg.event.TIP;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster;

import java.util.ArrayList;
import java.util.List;

public class TutorialManager {
    public static final List<String> messages=    new ArrayList<>() ;
    public static String NEXT_HERO= IGG_Demo.HERO_KESERIM_SLEEPLESS;

    public static void init(){
        for (TIP tutorialTip : TipMessageMaster.tutorialTips) {
            messages.add(tutorialTip.message);
        }
//        Rune messages?
    }

    public static String nextHero() {
        return NEXT_HERO;
    }
}
