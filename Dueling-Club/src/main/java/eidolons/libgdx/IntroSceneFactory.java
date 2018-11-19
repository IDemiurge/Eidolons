package eidolons.libgdx;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogView;

import java.util.Arrays;
import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class IntroSceneFactory {
    private static final String javaLogoPath = "big/java_logo.png";
    private static final String libgdxLogoPath = "big/libgdx_logo.png";
    private static final String backIntroPath = "big/ravenwood.jpg";
    private static final String portratePath = "mini/unit/Darkness/dungeon/drone.jpg";
    private static final String notSkipableMessage = "This message you cant skip, 3 sec.";
    private static final String skipableMessage = "This message you may skip, press any key blablabla...";
    private static final String demoIntroBackPath = "big/new3/Queen of Lust.jpg";
    private static final String demoIntroMes = "A long time ago, in a galaxy far, far away...";
    private static final String loremIpsum = "[#FF0000FF]Lorem ipsum[] dolor sit amet, [#00FF00FF]consectetur adipiscing elit[]. [#0000FFFF]Vestibulum faucibus[], augue sit amet porttitor rutrum, nulla eros finibus mauris, nec sagittis mauris nulla et urna. Sed ac orci nec urna ornare aliquam a sit amet neque. Nulla condimentum iaculis dolor, et porttitor dui sollicitudin vel. Fusce convallis fringilla dolor eu mollis. Nam porta augue nec ullamcorper ultricies. Morbi bibendum libero efficitur metus accumsan viverra at ut metus. Duis congue pulvinar ligula, sed maximus tellus lacinia eu.";

    public static List<DialogView> getIntroStage() {
        DialogView javaScenario = new DialogView(4500, false, getOrCreateR(javaLogoPath), null, null);
        DialogView libgdxScenario = new DialogView(1500, false, getOrCreateR(libgdxLogoPath), null, null);
        DialogView introScenario = new DialogView(1500, false, getOrCreateR(backIntroPath), notSkipableMessage, getOrCreateR(portratePath));
        DialogView introScenario2 = new DialogView(1500, true, getOrCreateR(backIntroPath), skipableMessage, null);

        return Arrays.asList(javaScenario, libgdxScenario, introScenario, introScenario2);
    }

    public static List<DialogView> getDemoIntro() {
        DialogView scenario = new DialogView(2000, false, getOrCreateR(demoIntroBackPath), demoIntroMes, null);
        DialogView scenario1 = new DialogView(2000, false, getOrCreateR(demoIntroBackPath), loremIpsum, null);

        return Arrays.asList(scenario, scenario1);
    }
}
