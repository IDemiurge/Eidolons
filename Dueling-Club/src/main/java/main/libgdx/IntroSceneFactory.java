package main.libgdx;

import main.libgdx.stage.ChainedStage;

import java.util.Arrays;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class IntroSceneFactory {
    private static final String javaLogoPath = "big/java_logo.png";
    private static final String libgdxLogoPath = "big/libgdx_logo.png";
    private static final String backIntroPath = "big/ravenwood.jpg";
    private static final String portratePath = "mini/unit/Darkness/dungeon/drone.jpg";
    private static final String notSkipableMessage = "This message you cant skip, 3 sec.";
    private static final String skipableMessage = "This message you may skip, press any key blablabla...";

    public static ChainedStage getIntroStage() {
        DialogScenario javaScenario = new DialogScenario(500, false, getOrCreateR(javaLogoPath), null, null);
        DialogScenario libgdxScenario = new DialogScenario(500, false, getOrCreateR(libgdxLogoPath), null, null);
        DialogScenario introScenario = new DialogScenario(500, false, getOrCreateR(backIntroPath), notSkipableMessage, getOrCreateR(portratePath));
        DialogScenario introScenario2 = new DialogScenario(500, true, getOrCreateR(backIntroPath), skipableMessage, null);

        return new ChainedStage(Arrays.asList(javaScenario, libgdxScenario, introScenario, introScenario2));
    }
}
