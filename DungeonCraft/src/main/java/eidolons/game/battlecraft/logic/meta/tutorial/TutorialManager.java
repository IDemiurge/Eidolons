package eidolons.game.battlecraft.logic.meta.tutorial;

import eidolons.system.text.tips.TextEvent;
import eidolons.system.text.tips.TipMessageMaster;

import java.util.ArrayList;
import java.util.List;

public class TutorialManager {
    public static final List<String> messages=    new ArrayList<>() ;
    public static String NEXT_HERO= "Anphis val Keserim";

    public static void init(){
        for (TextEvent tutorialTip : TipMessageMaster.tutorialTips) {
            messages.add(tutorialTip.getMessage());
        }
//        Rune messages?
    }

    public static String nextHero() {
        return NEXT_HERO;
    }
}
