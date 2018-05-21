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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.particles.Ambience;
import eidolons.libgdx.anims.particles.ParticleManager;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.launch.ScenarioLauncher;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.screens.ScreenData;
import eidolons.system.text.TipMaster;
import main.content.CONTENT_CONSTS2.EMITTER_PRESET;
import main.system.graphics.FontMaster.FONT;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class LoadingStage extends Stage {
    protected ScreenData data;
    private boolean fogOn = false;
    private boolean engineInit = true;
    private boolean fullscreen = true;
    private boolean loaderWheel;
    private Image fullscreenImage;
    private List<Ambience> fogList = new ArrayList<>();
    private Image loadingImage;
    private SuperContainer logoImage;
    private Label missionName;
    private Label tip;
    private float counter = 0;

    public LoadingStage(ScreenData data) {
        this.data = data;
        if (data.equals("Loading...")) {
            engineInit = true;
            loaderWheel = true;
//            fogOn = false;
        }

        tip = new Label(getBottonText(), StyleHolder.getHqLabelStyle( 20 ));

        tip.addListener(TipMaster.getListener(tip));
        //TODO click to show next tip
        tip.setPosition(GdxMaster.centerWidth(tip), 0);

        if (!fullscreen) {
            final TextureRegion logoTexture =
             getOrCreateR("UI/logo.png");
            logoImage = new SuperContainer(new Image(logoTexture));
            logoImage.setPosition(0, GdxMaster.getHeight() - loadingImage.getHeight());
           addActor(logoImage);
        } else {
            final TextureRegion fullscreenTexture =
             getOrCreateR(
              (engineInit) ? "UI/logo fullscreen.png"
               : "UI/moe loading screen.png");
            fullscreenImage = new Image(fullscreenTexture);
            addActor(fullscreenImage);
        }

        addActor(tip);
        if (fogOn)
           addFog();

        if (ScenarioLauncher.running) {
            missionName = new Label(data.getName()
             , StyleHolder.getSizedLabelStyle(FONT.AVQ, 24));
            missionName.setPosition(GdxMaster.centerWidth(missionName),
             GdxMaster.top(missionName));
            addActor(missionName);
//            if (fogOn) {
//                addActor(ParticleManager.addFogOn(
//                 new Vector2(missionName.getX() + 300, missionName.getY() - 300)
//                 , fogList));
//                addActor(ParticleManager.addFogOn(
//                 new Vector2(missionName.getX(), missionName.getY() - 300)
//                 , fogList));
//                addActor(ParticleManager.addFogOn(new Vector2(missionName.getX(), missionName.getY()), fogList));
//            }
        }
        if (loaderWheel) {
            final TextureRegion loadingTexture = getOrCreateR("UI/loading-wheel-trans_256х256.png");
            loadingImage = new Image(loadingTexture);
            loadingImage.setOrigin(Align.center);
            loadingImage.setPosition(
             GdxMaster.getWidth() / 2 - loadingImage.getWidth() / 2,
             GdxMaster.getHeight() / 2 - loadingImage.getHeight() / 2);
            //loadingTexture.getTexture();
            addActor(loadingImage);
        }
    }

    private void addFog() {
        int width = GdxMaster.getWidth();
        int height = GdxMaster.getHeight();
        for (int h = 0; h <=height; h+=300) {
            for (int w = 0; w <=width; w+=300) {
//                if (RandomWizard.chance(70)) {
//                    continue;
//                }
                Vector2 v= new Vector2(w,h);
                Ambience fog = ParticleManager.addFogOn(v, EMITTER_PRESET.MIST_WHITE);
                fogList.add(fog);
                addActor(fog);
            }
        }

    }

    public static String getBottonText() {
//        return "Tip: " +
//         TipMaster.getTip()
//         + "(click to show next tip)";
       return "Eidolons v" + CoreEngine.VERSION+ " [beta] by Alexander @EiDemiurge     " +
        "     ***        Got feedback? Contact me: justmeakk@gmail.com";
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
        tip.setVisible(!(Eidolons.getScreen() instanceof DungeonScreen));

        if (loadingImage != null) {
            counter += delta;
            if (counter >= 0.05) {
                loadingImage.setRotation(loadingImage.getRotation() + 30);
                counter = 0;
            }
        }
        if (logoImage != null)
            logoImage.act(delta);
    }

    public void done() {
        if (fogList != null)
            for (Ambience fog : fogList)
                fog.getEffect().dispose();
    }

    @Override
    public void draw() {
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
