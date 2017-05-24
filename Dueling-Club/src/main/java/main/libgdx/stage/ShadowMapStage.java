package main.libgdx.stage;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 5/24/2017.
 */
public class ShadowMapStage extends Stage {

    private static final String TEXTURE_PATH ="UI\\bf\\shadowMap.png" ;
    ShadowMapDataSource shadowMapDataSource;
    TextureRegion shadowMapTexture;


    public ShadowMapStage(ShadowMapDataSource shadowMapDataSource) {
        this.shadowMapDataSource = shadowMapDataSource;
        shadowMapTexture = TextureCache.getOrCreateR(TEXTURE_PATH);
        addActor(new Image(shadowMapTexture));

//        GuiEventManager.bind(GuiEventType.UPDATE_SHADOWMAP);
        // break into regions?
        //separate shadow for each cell?
    }

    @Override
    public void draw() {
        //camera?
        super.draw();
    }

    public class ShadowMapDataSource{
        DC_Game game;

        public ShadowMapDataSource(DC_Game game) {
            this.game = game;
        }

        public float getGamma(Coordinates c){
            return game.getVisionMaster().getGammaMaster().getGammaForCell(c.x, c.y);
        }
    }
}
