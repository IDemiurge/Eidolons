
package eidolons.libgdx.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Launcher;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.particles.ambi.Ambience;
import eidolons.libgdx.particles.VFX;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import eidolons.libgdx.particles.ambi.AmbienceDataSource.AMBIENCE_TEMPLATE;
import eidolons.libgdx.particles.ambi.ParticleManager;
import eidolons.libgdx.launch.ScenarioLauncher;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.screens.ScreenData;
import eidolons.system.text.TipMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.ArrayMaster;
import main.system.datatypes.WeightMap;
import main.system.graphics.FontMaster.FONT;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class LoadingStage extends Stage {
    protected ScreenData data;
    private boolean fogOn = !CoreEngine.isLiteLaunch();
    private boolean engineInit = true;
    private Image fullscreenImage;
    private List<Ambience> fogList = new ArrayList<>();

    Group vfxLayer;
    private Label missionName;
    private Label underText;
    private float counter = 0;

    public LoadingStage(ScreenData data) {
        this.data = data;
        if (data.equals("Loading...")) {
            engineInit = true;
        }
        underText = new Label(getBottonText(), StyleHolder.getHqLabelStyle(17));

        underText.addListener(TipMaster.getListener(underText));
        //TODO click to show next tip
        underText.setPosition(GdxMaster.centerWidth(underText), 0);

        final TextureRegion fullscreenTexture =
         getOrCreateR(
          (engineInit) ? "ui/main/logo fullscreen.png"
           : "ui/main/moe loading screen.png");
        fullscreenImage = new Image(fullscreenTexture);
        addActor(fullscreenImage);


        addActor(underText);
        if (fogOn)
            addFog();

        if (ScenarioLauncher.running) {
            missionName = new Label(data.getName()
             , StyleHolder.getSizedLabelStyle(FONT.AVQ, 24));
            missionName.setPosition(GdxMaster.centerWidth(missionName),
             GdxMaster.top(missionName));
            addActor(missionName);
        }
    }

    public static String getBottonText() {
//        return "Tip: " +
//         TipMaster.getTip()
//         + "(click to show next tip)";
        return "Eidolons: Netherflame v" + CoreEngine.VERSION + " [" +
                CoreEngine.VERSION_NAME +
                "] by Alexander Kamen" +
         "    ***  Found bugs? Contact me at @EiDemiurge & EidolonsGame@gmail.com";
    }

    public Label getUnderText() {
        return underText;
    }

    private void addFog() {
        int width = GdxMaster.getWidth();
        int height = 400;
        for (int h = 0; h <= height; h +=RandomWizard.getRandomInt(200)+50) {
            for (int w = 0; w <= width; w += RandomWizard.getRandomInt(200)+50) {
                if (RandomWizard.chance(30)) {
                    continue;
                }

                //local time?!
                AMBIENCE_TEMPLATE template =
                         new EnumMaster<AMBIENCE_TEMPLATE>().retrieveEnumConst(AMBIENCE_TEMPLATE.class,
                        new WeightMap<>().
//                        chain(AMBIENCE_TEMPLATE.COLD, 10).
                        chain(AMBIENCE_TEMPLATE.CAVE, 10).
                        chain(AMBIENCE_TEMPLATE.DEEP_MIST, 10).
                        chain(AMBIENCE_TEMPLATE.CRYPT , 10).
                        getRandomByWeight().toString());

                boolean night = TimeMaster.getTime()% (3600*24*1000) > 3600*12*1000;

                VFX[] vfxes = night ? template.nightly : template.daily;

                int n = vfxes.length;
                VFX vfx = vfxes[RandomWizard.getRandomInt(n)];

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
        if (IGG_Launcher.INTRO_RUNNING) {
            underText.setVisible(false);
        } else
            underText.setVisible(!(Eidolons.getScreen() instanceof DungeonScreen));

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
