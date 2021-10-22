
package libgdx.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.content.consts.VisualEnums;
import eidolons.game.EidolonsGame;
import eidolons.netherflame.main.IntroLauncher;
import eidolons.system.libgdx.datasource.ScreenData;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.particles.ambi.Ambience;
import libgdx.particles.ambi.ParticleManager;
import libgdx.screens.ScreenMaster;
import libgdx.screens.dungeon.DungeonScreen;
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

public class LoadingStage extends Stage {
    protected ScreenData data;
    private FadeImageContainer fullscreenImage;
    private final List<Ambience> fogList = new ArrayList<>();

    private Label underText;

    public LoadingStage(ScreenData data, String loadScreenPath) {
        this.data = data;
        if (isBgImageOn()) {
            underText = new Label(getBottomText(), StyleHolder.getHqLabelStyle(17));

            underText.addListener(TipMaster.getListener(underText));
            //TODO click to show next tip
            underText.setPosition(GdxMaster.centerWidth(underText), 52);
            fullscreenImage = new FadeImageContainer(loadScreenPath);
            fullscreenImage.setNoAtlas(true);
            float x = (float) GdxMaster.getWidth() / 1920;
            float y = (float) GdxMaster.getHeight() / 1080;
            float s = MathUtils.lerp(1, Math.max(x, y), 0.5f);
            fullscreenImage.setScale(s);
            addActor(fullscreenImage);
            addActor(underText);
            //!CoreEngine.isLiteLaunch();
            boolean fogOn = false;
            if (fogOn)
                addFog();
        }
    }

    protected boolean isBgImageOn() {
        return Flags.isCombatGame();
    }

    public FadeImageContainer getFullscreenImage() {
        return fullscreenImage;
    }

    public static String getBottomText() {
        if (EidolonsGame.TESTER_VERSION) {
            return "Eidolons: Netherflame v" + CoreEngine.VERSION + " [" +
                    CoreEngine.VERSION_NAME +
                    "] by Alexander Kamen" +
                    "    ***  Got feedback? Just ping me on Discord :)";
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
                VisualEnums.VFX_TEMPLATE template =
                        new EnumMaster<VisualEnums.VFX_TEMPLATE>().retrieveEnumConst(VisualEnums.VFX_TEMPLATE.class,
                                new WeightMap<>().
                                        //                        chain(AMBIENCE_TEMPLATE.COLD, 10).
                                                chain(VisualEnums.VFX_TEMPLATE.CAVE, 10).
                                        chain(VisualEnums.VFX_TEMPLATE.DEEP_MIST, 10).
                                        chain(VisualEnums.VFX_TEMPLATE.CRYPT, 10).
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
        if (underText != null) {
            if (IntroLauncher.INTRO_RUNNING) {
                underText.setVisible(false);
            } else
        underText.setVisible(!(ScreenMaster.getScreen() instanceof DungeonScreen));
        }

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
