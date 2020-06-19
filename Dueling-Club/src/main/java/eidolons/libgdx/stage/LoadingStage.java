
package eidolons.libgdx.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.game.EidolonsGame;
import eidolons.game.netherflame.main.IntroLauncher;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.particles.ambi.Ambience;
import eidolons.libgdx.particles.ambi.AmbienceDataSource.VFX_TEMPLATE;
import eidolons.libgdx.particles.ambi.ParticleManager;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import eidolons.system.text.TipMaster;
import main.content.enums.GenericEnums;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.TimeMaster;
import main.system.datatypes.WeightMap;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class LoadingStage extends Stage {
    protected ScreenData data;
    private final boolean fogOn = false;//!CoreEngine.isLiteLaunch();
    private boolean engineInit = true;
    private final Image fullscreenImage;
    private final List<Ambience> fogList = new ArrayList<>();

    Group vfxLayer;
    private Label missionName;
    private final Label underText;
    private final float counter = 0;

    public LoadingStage(ScreenData data) {
        this.data = data;
        if (data.equals("Loading...")) {
            engineInit = true;
        }
        underText = new Label(getBottomText(), StyleHolder.getHqLabelStyle(17));

        underText.addListener(TipMaster.getListener(underText));
        //TODO click to show next tip
        underText.setPosition(GdxMaster.centerWidth(underText), 0);

        final TextureRegion fullscreenTexture =
                getOrCreateR(getBackgroundImagePath());
        fullscreenImage = new Image(fullscreenTexture);

        float x = new Float(GdxMaster.getWidth())/1920;
        float y =  new Float(GdxMaster.getHeight())/1080;
        float s = MathUtils.lerp(1, Math.max(x, y), 0.5f);
        fullscreenImage.setScale(s);
        addActor(fullscreenImage);
        if (!Flags.isCombatGame()){
            fullscreenImage.setVisible(false);
        }
//        fullscreenImage.setPosition(GdxMaster.centerWidth(fullscreenImage),
//         GdxMaster.centerHeight(fullscreenImage));


        addActor(underText);
        if (fogOn)
            addFog();

        //Tester Check loading screen is important..
        // if (ScenarioLauncher.running) {
        //     missionName = new Label(data.getName()
        //             , StyleHolder.getSizedLabelStyle(FONT.AVQ, 24));
        //     missionName.setPosition(GdxMaster.centerWidth(missionName),
        //             GdxMaster.getTopY(missionName));
        //     addActor(missionName);
        // }
    }

    protected String getBackgroundImagePath() {
        return                (engineInit) ? "main/art/MAIN_MENU.jpg"
                        : "ui/main/moe loading screen.png";
    }

    public static String getBottomText() {
//        return "Tip: " +
//         TipMaster.getTip()
//         + "(click to show next tip)";

        if (EidolonsGame.TESTER_VERSION) {
            return "Eidolons: Netherflame v" + CoreEngine.VERSION + " [" +
                    CoreEngine.VERSION_NAME +
                    "] by Alexander Kamen" +
                    "    ***  Found bugs? Contact me at @EiDemiurge & EidolonsGame@gmail.com";
        }
        return "Eidolons: Netherflame - " +
                CoreEngine.VERSION_NAME + "[" +
                "v" + CoreEngine.VERSION + "]";
    }

    public Label getUnderText() {
        return underText;
    }

    private void addFog() {
        int width = GdxMaster.getWidth();
        int height = 400;
        for (int h = 0; h <= height; h += RandomWizard.getRandomInt(200) + 50) {
            for (int w = 0; w <= width; w += RandomWizard.getRandomInt(200) + 50) {
                if (RandomWizard.chance(30)) {
                    continue;
                }

                //local time?!
                VFX_TEMPLATE template =
                        new EnumMaster<VFX_TEMPLATE>().retrieveEnumConst(VFX_TEMPLATE.class,
                                new WeightMap<>().
//                        chain(AMBIENCE_TEMPLATE.COLD, 10).
        chain(VFX_TEMPLATE.CAVE, 10).
                                        chain(VFX_TEMPLATE.DEEP_MIST, 10).
                                        chain(VFX_TEMPLATE.CRYPT, 10).
                                        getRandomByWeight().toString());

                boolean night = TimeMaster.getTime() % (3600 * 24 * 1000) > 3600 * 12 * 1000;

                GenericEnums.VFX[] vfxes = night ? template.nightly : template.daily;

                int n = vfxes.length;
                GenericEnums.VFX vfx = vfxes[RandomWizard.getRandomInt(n)];

                Vector2 v = new Vector2(w, h);
                Ambience fog = ParticleManager.addFogOn(v, vfx);
                fogList.add(fog);
                addActor(fog);
            }
        }

    }

    @Override
    public Actor hit(float stageX, float stageY, boolean touchable) {
        return super.hit(stageX, stageY, touchable);
    }

    @Override
    public void setViewport(Viewport viewport) {
        super.setViewport(viewport);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        for (Ambience sub : fogList) {
            sub.act(delta);
        }
        if (IntroLauncher.INTRO_RUNNING) {
            underText.setVisible(false);
        } else
            underText.setVisible(!(ScreenMaster.getScreen() instanceof DungeonScreen));

    }

    public void done() {
        if (fogList != null)
            for (Ambience fog : fogList)
                fog.getEffect().dispose();
    }

    @Override
    public void draw() {
        // ???
        final Matrix4 combined = getCamera().combined.cpy();
        getCamera().update();

        final Group root = getRoot();

        if (!root.isVisible()) return;

        combined.setToOrtho2D(0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());

        Batch batch = this.getBatch();
        batch.setProjectionMatrix(combined);
        batch.begin();
        root.draw(batch, 1);
        batch.end();
    }
}
