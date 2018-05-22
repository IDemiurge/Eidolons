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
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.particles.Ambience;
import eidolons.libgdx.anims.particles.ParticleManager;
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
    private Image fullscreenImage;
    private List<Ambience> fogList = new ArrayList<>();
    private Label missionName;
    private Label underText;
    private float counter = 0;

    public LoadingStage(ScreenData data) {
        this.data = data;
        if (data.equals("Loading...")) {
            engineInit = true;
        }
        underText = new Label(getBottonText(), StyleHolder.getHqLabelStyle(20));

        underText.addListener(TipMaster.getListener(underText));
        //TODO click to show next tip
        underText.setPosition(GdxMaster.centerWidth(underText), 0);

        final TextureRegion fullscreenTexture =
         getOrCreateR(
          (engineInit) ? "UI/logo fullscreen.png"
           : "UI/moe loading screen.png");
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
        return "Eidolons v" + CoreEngine.VERSION + " [beta] by Alexander @EiDemiurge     " +
         "     ***        Got feedback? Contact me: justmeakk@gmail.com";
    }

    public Label getUnderText() {
        return underText;
    }

    private void addFog() {
        int width = GdxMaster.getWidth();
        int height = GdxMaster.getHeight();
        for (int h = 0; h <= height; h += 300) {
            for (int w = 0; w <= width; w += 300) {
//                if (RandomWizard.chance(70)) {
//                    continue;
//                }
                Vector2 v = new Vector2(w, h);
                Ambience fog = ParticleManager.addFogOn(v, EMITTER_PRESET.MIST_WHITE);
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
